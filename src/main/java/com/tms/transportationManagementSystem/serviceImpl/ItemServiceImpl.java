package com.tms.transportationManagementSystem.serviceImpl;

import com.tms.transportationManagementSystem.dto.ResponseDTO.ItemResponseDTO;
import com.tms.transportationManagementSystem.entity.ItemMaster;
import com.tms.transportationManagementSystem.repository.ItemMasterRepository;
import com.tms.transportationManagementSystem.service.ItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemMasterRepository itemRepository;

    @Override
    public List<ItemResponseDTO> getAllItems() {

        return itemRepository.findAll()
                .stream()
                .map(this::mapToDTO)
                .toList();
    }

    @Override
    public ItemResponseDTO getItemById(Long id) {

        ItemMaster item = itemRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Item not found"));

        return mapToDTO(item);
    }

    private ItemResponseDTO mapToDTO(ItemMaster item) {

        return ItemResponseDTO.builder()
                .id(item.getId())
                .itemNumber(item.getItemNumber())
                .description(item.getDescription())
                .description2(item.getDescription2())
                .lineType(item.getLineType())
                .stockingType(item.getStockingType())
                .thirdItemNumber(item.getThirdItemNumber())
                .shortItemNo(item.getShortItemNo())
                .build();
    }

}