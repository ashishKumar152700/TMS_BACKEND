package com.tms.transportationManagementSystem.controller;

import com.tms.transportationManagementSystem.dto.RequestDTO.CreateRoleRequest;
import com.tms.transportationManagementSystem.dto.ResponseDTO.RoleResponse;
import com.tms.transportationManagementSystem.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/roles")
@RequiredArgsConstructor
public class RoleController {

    private final RoleService roleService;

    @PostMapping
    public RoleResponse createRole(
            @RequestBody CreateRoleRequest request
    ) {

        return roleService.createRole(request);
    }

    @GetMapping
    public List<RoleResponse> getAllRoles() {

        return roleService.getAllRoles();
    }

    @GetMapping("/{id}")
    public RoleResponse getRoleById(
            @PathVariable Long id
    ) {

        return roleService.getRoleById(id);
    }

    @PutMapping("/{id}")
    public RoleResponse updateRole(
            @PathVariable Long id,
            @RequestBody CreateRoleRequest request
    ) {

        return roleService.updateRole(id, request);
    }

    @DeleteMapping("/{id}")
    public String deleteRole(
            @PathVariable Long id
    ) {

        roleService.deleteRole(id);

        return "Role Deleted Successfully";
    }
}