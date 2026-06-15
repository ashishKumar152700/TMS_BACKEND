package com.tms.transportationManagementSystem.controller;

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

    @GetMapping
    public List<ItemResponseDTO> getAllItems() {
        return itemService.getAllItems();
    }

    @GetMapping("/{id}")
    public ItemResponseDTO getItemById(
            @PathVariable Long id) {

        return itemService.getItemById(id);
    }
}