package com.raf.foodOrder.controller;

import com.raf.foodOrder.model.Permission;
import com.raf.foodOrder.service.PermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/permissions")
@CrossOrigin
public class PermissionController {

    private PermissionService permissionService;

    @Autowired
    public PermissionController(PermissionService permissionService) {
        this.permissionService = permissionService;
    }

    @GetMapping
    public ResponseEntity<?> getAllPermissions() {

        Optional<List<Permission>> permissions = permissionService.findAll();
        if (permissions.isPresent()) {
            return ResponseEntity.ok(permissions);
        } else {
            return ResponseEntity.notFound().build();

        }
    }
}
