package com.tms.transportationManagementSystem.dto.ResponseDTO;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class TransporterResponseDTO {

    private Long id;
    private String companyName;
    private String gstNumber;
    private String panNumber;
    private String contactPerson;
    private String mobile;
    private String email;
    private String address;
    private String city;
    private String state;
    private String pincode;
    private String status;
    private Long addressNumber;


    private List<VehicleResponseDTO> vehicles;
}