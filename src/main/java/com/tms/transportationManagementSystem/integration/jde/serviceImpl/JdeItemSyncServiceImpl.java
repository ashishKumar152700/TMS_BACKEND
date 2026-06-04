package com.tms.transportationManagementSystem.integration.jde.serviceImpl;

import com.tms.transportationManagementSystem.entity.ItemMaster;
import com.tms.transportationManagementSystem.integration.jde.client.JdeItemClient;
import com.tms.transportationManagementSystem.integration.jde.dto.response.JdeItemMasterApiResponse;
import com.tms.transportationManagementSystem.integration.jde.dto.response.JdeItemResponseDTO;
import com.tms.transportationManagementSystem.integration.jde.service.JdeItemSyncService;
import com.tms.transportationManagementSystem.repository.ItemMasterRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class JdeItemSyncServiceImpl implements JdeItemSyncService {

    private final JdeItemClient jdeItemClient;
    private final ItemMasterRepository itemMasterRepository;

//    @Override
//    public void syncItems() {
//
//        String response = jdeItemClient.getItems();
//
//        System.out.println("=================================");
//        System.out.println("item master"+ response);
//        System.out.println("=================================");
//    }

    @Override
    public void syncItems() {

        JdeItemMasterApiResponse response =
                jdeItemClient.getItems();

        List<JdeItemResponseDTO> items =
                response.getItemList();

        for (JdeItemResponseDTO dto : items) {

//            if (dto.getItemNumber() == null ||
//                    dto.getItemNumber().trim().isEmpty()) {
//                continue;
//            }

            ItemMaster item =
                    itemMasterRepository
                            .findByItemNumber(dto.getItemNumber())
                            .orElse(new ItemMaster());

            item.setItemNumber(dto.getItemNumber().trim());
            item.setDescription(dto.getDescription());
            item.setDescription2(dto.getDescription2());
            item.setLineType(dto.getLineType());
            item.setStockingType(dto.getStockingType());
            item.setThirdItemNumber(dto.getThirdItemNumber());
            item.setShortItemNo(dto.getShortItemNo());

            itemMasterRepository.save(item);
        }

        log.info("Item Master Sync Completed. Total Records = {}",
                items.size());
    }
}