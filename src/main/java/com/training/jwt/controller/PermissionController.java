package com.training.jwt.controller;

import com.training.jwt.model.Permission;
import com.training.jwt.service.PermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PermissionController {

    @Autowired
    private PermissionService permissionService;
    @PostMapping({"/createNewPermission"})
    public Permission createNewPermission(@RequestBody Permission permission) {
        return permissionService.createNewPermission(permission);
    }
}



