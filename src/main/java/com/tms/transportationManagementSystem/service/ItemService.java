package com.tms.transportationManagementSystem.service;

import com.tms.transportationManagementSystem.dto.RequestDTO.ItemRequestDTO;
import com.tms.transportationManagementSystem.dto.ResponseDTO.ItemResponseDTO;

import java.util.List;

public interface ItemService {



    List<ItemResponseDTO> getAllItems();

    ItemResponseDTO getItemById(Long id);



}