# Backend Optimization – Changes Log

**Date:** 2026-10-27
**Branch:** `radhe_c_laude`
**Goal:** Code optimize karna **bina koi functionality / API response change kiye** (frontend already integrated hai).

> Saare API URLs, request bodies, response JSON fields, error messages aur business rules **same** hain.
> Main kaam: DB queries kam karna (N+1 problem), duplicate code/queries hatana, debug `System.out` ko logger mein badalna.

---

## 0. Pehle wala feature change (isi branch par, optimization se pehle)

`GET /api/vehicle-inspections/guard/pending` (Guard · Truck Verification screen) ab sirf wahi records deta hai jahan:
- Admin ne vehicle inspection **APPROVED** kiya ho (`reviewStatus = APPROVED`)
- Transporter ne appointment letter **ACCEPT** kiya ho (`status = ACCEPTED` aur `transporterAcceptance = true`)
- Guard verification abhi **PENDING / ON_HOLD** ho

Ye ek requested functional change tha. Iske alawa neeche ke saare changes sirf optimization hain.

---

## 1. Global setting – `application.properties`

```properties
spring.jpa.properties.hibernate.default_batch_fetch_size=100
```

- Lazy relations (order lines, trip → vehicle/shipment, bid → transporter, route → stops, wagairah) ab **ek-ek query ki jagah batch (IN query)** mein load hote hain.
- Ye pure project ke har list API par N+1 queries kam karta hai. Koi code ya response change nahi hota.

---

## 2. Naya helper – `serviceImpl/SalesOrderLookup.java`

Kai services mein same pattern tha:
`shipmentOrderRepository.findByShipmentId(...)` → har order ke liye `orderRepository.findById(...)` → har order ke liye lines query.

Ab ek shared `@Component` hai:

| Method | Kaam |
|---|---|
| `ordersByShipmentId(shipmentIds)` | Saare shipments ke orders **2 queries** mein (shipment_orders order preserve hota hai; missing order skip hota hai, pehle jaisa) |
| `linesByOrderId(orderIds)` | Saare orders ki lines **1 query** mein |
| `toShipmentOrderDTO(order)` | `ShipmentOrderResponseDTO` banane ka code jo 4 jagah copy-paste tha |
| `inChunks(...)` | IN query ko 1000-1000 ke chunks mein todta hai (PostgreSQL parameter limit se bachne ke liye) |

---

## 3. Repository changes (sirf naye methods add hue, koi purana method hata/badla nahi)

| Repository | Naya method |
|---|---|
| `JdeSalesOrderLineRepository` | `findBySalesOrder_IdIn(Collection<Long>)` |
| `ShipmentOrderRepository` | `findByShipmentIdIn(...)`, `findByOrderIdIn(...)` |
| `JdeSalesOrderRepository` | `findByCustomerNumberIn(...)`, `findAllOrderNumbers()` (JPQL) |
| `GatePassRepository` | `findByVehicleInspectionIdIn(...)` |
| `TransporterAppointmentLetterRepository` | `findByTrip_IdIn(...)` |
| `TransporterShipmentBidTrackerRepository` | `findByTransporterId(Long)` |
| `VehicleInspectionRepository` | `findByGuardVerificationStatusInAndReviewStatus(...)` (guard/pending feature ke liye) |

---

## 4. Service-wise changes

### 4.1 `JdeSalesOrderServiceImpl` (sabse bada gain)
APIs: group by customer, group by customer & route, group by route, available routes, all sales orders.

| Pehle | Ab |
|---|---|
| Har order ke liye `mappingRepository.findByOrderId` | Saari mappings 1 query mein, map mein |
| Har order ke liye `findBestRoute` → 2 queries (destination + stop) | Candidate routes **city-wise cache** (same city dobara query nahi) |
| Har order ke liye `lineRepository.findBySalesOrder` | Saari lines 1 batch query |
| `groupOrdersByCustomerAndRoute`: NEW → ROUTE_ASSIGNED ke liye har order ka alag `save` | Ek `saveAll` |
| `getAvailableRoutes` sirf route IDs ke liye poora `groupOrdersByRoute()` (saare DTOs + lines) banata tha | Wahi route-resolution logic bina DTO/lines banaye chalta hai |
| Route-stops map, unmapped DTO, route-group DTO ka code 2-2 baar copy | Private helpers (`buildRouteStopsMap`, `toUnmappedDTO`, `toRouteGroupDTO`) |

Route select karne ka rule **same** hai: manual mapping ho to wahi, warna jis candidate route par sabse zyada orders hain (tie par pehle jaisa hi route).
Unused `JdeSalesOrderLineRepository` field hata diya.

### 4.2 `ShipmentServiceImpl`
- `getAllShipments`, `getOpenBids`: orders ab `SalesOrderLookup` se batch mein aate hain (pehle har order ke liye `findById`).
- `getOpenBids`: "already bid placed" check pehle har shipment ke liye `existsBy...` query tha. Ab transporter ke saare tracker rows 1 query mein, `Set` mein.
- `createShipment`:
  - "Order already assigned" check pehle har order ke liye query tha, ab 1 query (`findByOrderIdIn`). Validation ka order aur error messages same hain.
  - Vehicle validation **do baar** ho raha tha (same `findById` 2 baar). Ab ek baar `existsById`. Error message same hai: `"Vehicle not found : <id>"`. Vehicle pehle bhi shipment pe set nahi hota tha, ab bhi nahi hota.
  - `routeId`, `selectedVehicleType`, `selectedVehicleCount` 2-2 baar set ho rahe the. Duplicate hata diya.
  - Har order ke liye `save` + `flush` ki jagah `saveAll` + ek `flush` (same transaction, same result).
- `System.out.println` → `log.debug(...)` (`@Slf4j`).
- Unused `UserRepository` field aur unused import hataye.
- Read-only APIs (`getOpenBids`, `getAllShipments`) par `@Transactional(readOnly = true)`.

### 4.3 `ShipmentBidServiceImpl`
- `getMyAppliedBids`, `getAllShipmentBidsForAdmin`: orders batch mein (`SalesOrderLookup`).
- Duplicate `ShipmentOrderResponseDTO` builder code hata kar shared helper use kiya.
- ~65 lines ka commented-out purana `getAllShipmentBidsForAdmin` hata diya (dead code).

### 4.4 `VehicleInspectionServiceImpl`
- `mapToResponse` har inspection ke liye gate pass + appointment letter ki **alag-alag queries** karta tha. Ab list APIs (`getAllInspections`, `getPendingReviews`, `getAdminInspections`, `getGuardPendingInspections`) mein ye sab **ek saath batch** mein load hota hai (`InspectionLookups` record).
- Admin APIs: sales orders `SalesOrderLookup` se batch mein (pehle har order ke liye `findById`).
- `mapToAdminResponse` mein appointment letter **do baar** fetch ho raha tha. Ab ek baar.
- `getGuardPendingInspections`: appointment letter pehle filter aur mapping dono mein alag-alag fetch hota tha. Ab ek baar.
- Single-record APIs (`getInspectionById`, `create`, `review`, `verifyVehicleAtGate`) ka behaviour bilkul same hai.
- Unused local variable (`currentStatus`), duplicate import aur unused repository fields hataye.

### 4.5 `TransporterAppointmentLetterServiceImpl`
- `createAppointmentLetter` mein `findSalesOrdersByShipmentId` same shipment ke liye **do baar** call ho raha tha. Ab ek baar, aur result dono jagah use hota hai.
- Duplicate / unused imports saaf kiye.

### 4.6 `LoadingConfirmationServiceImpl`
- Duplicate repository fields hataye (`JdeSalesOrderLineRepository` 2 baar aur `JdeSalesOrderRepository` 2 baar inject ho rahe the).
- `getItemsByTripId`: orders + lines batch mein (pehle har order ke liye 2 queries).
- `replaceItems` (create/update draft): har item ke liye `findById` ki jagah saari lines ek `findAllById` mein. Validation ka order aur saare error messages same hain.

### 4.7 `EligibleLoadingTripServiceImpl`
- Pehle `vehicleInspectionRepository.findAll()` se **saari inspections, unki PDF/image files (LOB) ke saath**, memory mein load hoti thi, phir Java mein `ACCEPTED` filter hota tha.
- Ab `findByGuardVerificationStatus(ACCEPTED)`, yaani filter DB mein. Result same hai, memory aur DB load kaafi kam.

### 4.8 `GatePassServiceImpl`
- `getAllGatePasses`: har gate pass ke liye shipment aur trip ka alag `findById`. Ab dono `findAllById` se batch mein.
- `gateOut` ka behaviour same hai.

### 4.9 `CustomerServiceImpl`
- `getAllCustomersWithOrders`: har customer ke liye orders query. Ab saare customers ke orders 1 batch query (`findByCustomerNumberIn`).
- Order → DTO mapping ka bada block do APIs mein copy tha. Ab ek private method `toOrderDTO` hai (same fields, same trim logic).

### 4.10 JDE Sales Order Sync (`JdeSalesOrderSyncServiceImpl`)
- Pehle JDE se aaye har order ke liye `findByOrderNumber` query. Ab saare existing order numbers **1 query** se `Set` mein aate hain.
- Same sync run mein duplicate order number aaye to wo bhi skip hota hai (pehle jaisa).
- Lines: har line ka `save` ki jagah `saveAll`.

### 4.11 `JdeSalesOrderClient`
- Poora JDE response `System.out` pe print hota tha (bada aur slow). Ab `log.debug`.

---

## 5. Dhyan dene wali baatein (edge cases)

- **Debug logs:** `ShipmentServiceImpl` aur `JdeSalesOrderClient` ke console prints ab `DEBUG` level par hain. Dekhne ke liye `application.properties` mein ye line daalein:
  ```properties
  logging.level.com.tms.transportationManagementSystem=DEBUG
  ```
- **Corrupt data case:** agar kisi `shipment_orders` row ka `order_id` NULL ho, ya kisi gate pass ka `trip_id` / `shipment_id` NULL ho, to pehle API 500 error deti thi (`findById(null)`). Ab wo row skip ho jaati hai. Normal data par koi fark nahi.
- **List order:** lists ka order pehle jaisa hi DB natural order hai. Kisi query mein naya `ORDER BY` add nahi kiya.

---

## 6. Jo jaan-boojh kar NAHI badla (risk ki wajah se), aage ke liye suggestions

1. **`VehicleInspection` ke 8 `@Lob byte[]` file columns:** har inspection load par saari files bhi memory mein aati hain. Isko theek karne ke liye files alag table/entity mein le jaani padengi, ya Hibernate bytecode enhancement (lazy basic) lagana padega. Ye schema/structure change hai, isliye abhi nahi kiya.
2. **Hardcoded JDE credentials** (`JdeSalesOrderClient` mein username/password/URL): inhe `application.properties` / environment variables mein le jaana chahiye.
3. **Customer / Item / Transporter JDE sync:** inme bhi per-record lookup hai, lekin ye upsert (update-or-insert) logic hai jiske edge cases nazuk hain, isliye nahi chheda.
4. **`TripServiceImpl`** field injection (`@Autowired`) use karta hai. Constructor injection behtar hai, par sirf style ka mamla hai.
5. **`JdeTransporterSyncService`** saare exceptions chupchap nigal leta hai (`catch` khaali hai). Kam se kam `log.error` hona chahiye.

---

## 7. Verification

- `mvnw clean compile`: **success**.
- Saare naye Spring Data derived query method names Spring Data ke `PartTree` parser se validate kiye. Saare valid nikle. Negative control (galat property name) par exception aaya, yaani check sach mein kaam kar raha tha. Ye temporary check file baad mein delete kar di.
- App ko yahan **start karke test nahi kiya**, kyunki startup pe JDE schedulers turant chal jaate hain (JDE call + DB write), aur port 8081 pe app pehle se chal rahi thi.

**Aapko karna hai:** server restart karke frontend ke main screens ek baar chala kar dekh lein:
Sales orders / route grouping, Shipments, Open bids, Bid management, Vehicle inspection (admin + guard), Appointment letters, Loading confirmation, Gate passes, Customers.

---

## 8. Revert kaise karein

Saare changes git mein uncommitted hain:

```bash
git diff --stat                   # kya-kya badla
git checkout -- <file>            # kisi ek file ko wapas purane jaisa
git checkout -- . && git clean -f src/main/java/com/tms/transportationManagementSystem/serviceImpl/SalesOrderLookup.java   # sab kuch wapas
```

> Note: `git checkout -- .` pehle wala guard/pending feature change bhi revert kar dega.
