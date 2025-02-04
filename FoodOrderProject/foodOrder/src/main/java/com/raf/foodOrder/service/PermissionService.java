package com.raf.foodOrder.service;

import com.raf.foodOrder.model.Permission;
import com.raf.foodOrder.repository.PermissionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PermissionService {

    private PermissionRepository permissionRepository;

    @Autowired
    public PermissionService(PermissionRepository permissionRepository) {
        this.permissionRepository = permissionRepository;
    }

    public Optional<List<Permission>> findAll(){

        Optional<List<Permission>> permissions = Optional.of(permissionRepository.findAll());
        return permissions;
    }
}
