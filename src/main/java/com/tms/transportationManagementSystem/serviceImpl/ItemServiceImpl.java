package com.tms.transportationManagementSystem.serviceImpl;

import com.tms.transportationManagementSystem.dto.RequestDTO.ItemRequestDTO;
import com.tms.transportationManagementSystem.dto.ResponseDTO.ItemResponseDTO;
import com.tms.transportationManagementSystem.entity.Item;
import com.tms.transportationManagementSystem.repository.ItemRepository;
import com.tms.transportationManagementSystem.service.ItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;

    @Override
    public ItemResponseDTO createItem(ItemRequestDTO dto) {

        Item item = Item.builder()
                .itemNumber(dto.getItemNumber())
                .description(dto.getDescription())
                .description2(dto.getDescription2())
                .lineType(dto.getLineType())
                .stockingType(dto.getStockingType())
                .thirdItemNumber(dto.getThirdItemNumber())
                .shortItemNo(dto.getShortItemNo())
                .build();

        return mapToResponse(itemRepository.save(item));
    }

    @Override
    public List<ItemResponseDTO> getAllItems() {

        return itemRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public ItemResponseDTO getItemById(Long id) {

        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Item not found"));

        return mapToResponse(item);
    }

    @Override
    public ItemResponseDTO updateItem(Long id, ItemRequestDTO dto) {

        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Item not found"));

        item.setItemNumber(dto.getItemNumber());
        item.setDescription(dto.getDescription());
        item.setDescription2(dto.getDescription2());
        item.setLineType(dto.getLineType());
        item.setStockingType(dto.getStockingType());
        item.setThirdItemNumber(dto.getThirdItemNumber());
        item.setShortItemNo(dto.getShortItemNo());

        return mapToResponse(itemRepository.save(item));
    }

    @Override
    public void deleteItem(Long id) {
        itemRepository.deleteById(id);
    }

    private ItemResponseDTO mapToResponse(Item item) {

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