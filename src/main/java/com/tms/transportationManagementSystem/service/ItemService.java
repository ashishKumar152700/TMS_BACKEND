package com.tms.transportationManagementSystem.service;

import com.tms.transportationManagementSystem.dto.RequestDTO.ItemRequestDTO;
import com.tms.transportationManagementSystem.dto.ResponseDTO.ItemResponseDTO;

import java.util.List;

public interface ItemService {

    ItemResponseDTO createItem(ItemRequestDTO requestDTO);

    List<ItemResponseDTO> getAllItems();

    ItemResponseDTO getItemById(Long id);

    ItemResponseDTO updateItem(Long id, ItemRequestDTO requestDTO);

    void deleteItem(Long id);
}