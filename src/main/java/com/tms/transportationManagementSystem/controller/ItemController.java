package com.tms.transportationManagementSystem.controller;

import com.tms.transportationManagementSystem.dto.RequestDTO.ItemRequestDTO;
import com.tms.transportationManagementSystem.dto.ResponseDTO.ItemResponseDTO;
import com.tms.transportationManagementSystem.service.ItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/items")
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;

    @PostMapping
    public ItemResponseDTO createItem(
            @RequestBody ItemRequestDTO requestDTO) {

        return itemService.createItem(requestDTO);
    }

    @GetMapping
    public List<ItemResponseDTO> getAllItems() {
        return itemService.getAllItems();
    }

    @GetMapping("/{id}")
    public ItemResponseDTO getItemById(
            @PathVariable Long id) {

        return itemService.getItemById(id);
    }

    @PutMapping("/{id}")
    public ItemResponseDTO updateItem(
            @PathVariable Long id,
            @RequestBody ItemRequestDTO requestDTO) {

        return itemService.updateItem(id, requestDTO);
    }

    @DeleteMapping("/{id}")
    public void deleteItem(@PathVariable Long id) {
        itemService.deleteItem(id);
    }
}