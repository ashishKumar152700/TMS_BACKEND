package com.tms.transportationManagementSystem.controller;

import com.tms.transportationManagementSystem.entity.VehicleInspection;
import com.tms.transportationManagementSystem.repository.VehicleInspectionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/vehicle-inspections")
@RequiredArgsConstructor
public class VehicleInspectionFileController {

    private final VehicleInspectionRepository vehicleInspectionRepository;


    @GetMapping("/{inspectionId}/files/{fileType}")
    public ResponseEntity<byte[]> getFile(
            @PathVariable Long inspectionId,
            @PathVariable String fileType
    ) {

        VehicleInspection inspection =
                vehicleInspectionRepository.findById(inspectionId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Vehicle inspection not found"
                                )
                        );


        byte[] file;
        String fileName;
        String contentType;


        switch (fileType.toLowerCase()) {

            case "rc":
                file = inspection.getRcFile();
                fileName = inspection.getRcFileName();
                contentType = inspection.getRcContentType();
                break;


            case "insurance":
                file = inspection.getInsuranceFile();
                fileName = inspection.getInsuranceFileName();
                contentType = inspection.getInsuranceContentType();
                break;


            case "fitness":
                file = inspection.getFitnessCertificateFile();
                fileName = inspection.getFitnessCertificateFileName();
                contentType = inspection.getFitnessCertificateContentType();
                break;


            case "puc":
                file = inspection.getPucFile();
                fileName = inspection.getPucFileName();
                contentType = inspection.getPucContentType();
                break;


            case "permit":
                file = inspection.getPermitFile();
                fileName = inspection.getPermitFileName();
                contentType = inspection.getPermitContentType();
                break;


            case "tax":
                file = inspection.getTaxDocumentFile();
                fileName = inspection.getTaxDocumentFileName();
                contentType = inspection.getTaxDocumentContentType();
                break;


            case "driving-license":
                file = inspection.getDrivingLicenseFile();
                fileName = inspection.getDrivingLicenseFileName();
                contentType = inspection.getDrivingLicenseContentType();
                break;


            case "driver-id":
                file = inspection.getDriverIdGatePassFile();
                fileName = inspection.getDriverIdGatePassFileName();
                contentType = inspection.getDriverIdGatePassContentType();
                break;


            default:
                throw new RuntimeException(
                        "Invalid file type: " + fileType
                );
        }


        if (file == null) {
            throw new RuntimeException(
                    "File not found"
            );
        }


        MediaType mediaType;

        try {

            mediaType = MediaType.parseMediaType(
                    contentType
            );

        } catch (Exception e) {

            mediaType =
                    MediaType.APPLICATION_OCTET_STREAM;
        }


        return ResponseEntity.ok()
                .contentType(mediaType)
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "inline; filename=\"" + fileName + "\""
                )
                .body(file);
    }
}