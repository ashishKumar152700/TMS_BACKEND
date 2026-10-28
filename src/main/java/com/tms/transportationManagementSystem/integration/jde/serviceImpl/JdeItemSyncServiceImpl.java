package com.tms.transportationManagementSystem.integration.jde.serviceImpl;

import com.tms.transportationManagementSystem.entity.ItemMaster;
import com.tms.transportationManagementSystem.integration.jde.client.JdeItemClient;
import com.tms.transportationManagementSystem.integration.jde.dto.response.JdeItemMasterApiResponse;
import com.tms.transportationManagementSystem.integration.jde.dto.response.JdeItemResponseDTO;
import com.tms.transportationManagementSystem.integration.jde.service.JdeItemSyncService;
import com.tms.transportationManagementSystem.repository.ItemMasterRepository;
import com.tms.transportationManagementSystem.serviceImpl.SalesOrderLookup;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class JdeItemSyncServiceImpl implements JdeItemSyncService {

    private final JdeItemClient jdeItemClient;
    private final ItemMasterRepository itemMasterRepository;

    @Override
    public void syncItems() {

        JdeItemMasterApiResponse response =
                jdeItemClient.getItems();

        if (response == null || response.getItemList() == null) {
            log.info("No item data received from JDE");
            return;
        }

        List<JdeItemResponseDTO> items =
                response.getItemList();

        // Existing item numbers ek hi query mein (pehle har item ke liye findByItemNumber)
        Set<String> existingItemNumbers =
                new HashSet<>(itemMasterRepository.findAllItemNumbers());

        List<ItemMaster> newItems = new ArrayList<>();

        for (JdeItemResponseDTO dto : items) {

            if (dto.getItemNumber() == null ||
                    dto.getItemNumber().trim().isEmpty()) {
                continue;
            }

            String itemNumber = dto.getItemNumber().trim();

            // Sirf naya item insert hoga. DB mein pehle se hai to skip -
            // TMS mein kiye gaye changes overwrite nahi honge.
            // add() false = DB mein hai ya isi response mein pehle aa chuka hai.
            if (!existingItemNumbers.add(itemNumber)) {
                continue;
            }

            ItemMaster item = new ItemMaster();

            item.setItemNumber(itemNumber);
            item.setDescription(dto.getDescription());
            item.setDescription2(dto.getDescription2());
            item.setLineType(dto.getLineType());
            item.setStockingType(dto.getStockingType());
            item.setThirdItemNumber(dto.getThirdItemNumber());
            item.setShortItemNo(dto.getShortItemNo());

            newItems.add(item);
        }

        // Har chunk ek transaction mein save (pehle har row ka alag transaction)
        SalesOrderLookup.inChunks(newItems, itemMasterRepository::saveAll);

        log.info("Item Master Sync Completed. Total Records = {} | New Inserted = {}",
                items.size(), newItems.size());
    }
}
