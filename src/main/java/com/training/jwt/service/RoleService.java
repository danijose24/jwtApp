package com.training.jwt.service;

import com.training.jwt.dao.RoleDao;
import com.training.jwt.model.Roles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RoleService {

    @Autowired
    private RoleDao roleDao;

    public Roles createNewRole(Roles role) {
        return roleDao.save(role);
    }
}
