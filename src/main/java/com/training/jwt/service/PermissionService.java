package com.training.jwt.service;

import com.training.jwt.dao.PermissionDao;
import com.training.jwt.model.Permission;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class PermissionService {

    @Autowired
    private PermissionDao permissionDao;

    public Permission createNewPermission(Permission permission) {
        return permissionDao.save(permission);
    }
    
    public Set<Permission> getPermission(Set<Permission> permissionList) {

        List<Permission> existingPermissions = (List<Permission>) permissionDao.findAll();
        Set<Permission> permissions = new HashSet<>();
        Map<String, Permission> permissionMap = new HashMap<>();
        for (Permission permission : existingPermissions) {
            permissionMap.put(permission.getPermissionName(), permission);
        }

        for (Permission permission : permissionList) {
            permissions.add(permissionMap.get(permission.getPermissionName()));
        }
    return permissions;
    }
}
