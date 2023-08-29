package com.training.jwt.service;

import com.training.jwt.dao.RoleDao;
import com.training.jwt.model.Permission;
import com.training.jwt.model.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
public class RoleService {

    @Autowired
    private RoleDao roleDao;

    @Autowired
    private PermissionService permissionService;

    public Role createNewRole(Role role) {
        if(role.getPermissions()!=null)
        {
            role.setPermissions(permissionService.getPermission(role.getPermissions()));
        }
        return roleDao.save(role);
    }
}
