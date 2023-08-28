package com.training.jwt.service;


import com.training.jwt.dao.RoleDao;
import com.training.jwt.dao.UserDao;
import com.training.jwt.model.Roles;
import com.training.jwt.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
public class UserService {

    @Autowired
    private UserDao userDao;

    @Autowired
    private RoleDao roleDao;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public void initRoleAndUser() {

        Roles adminRole = new Roles();
        adminRole.setRoleName("Admin");
        roleDao.save(adminRole);

        Roles userRole = new Roles();
        userRole.setRoleName("User");
        roleDao.save(userRole);

        User adminUser = new User();
        adminUser.setUserName("admin123");
        adminUser.setUserPassword(getEncodedPassword("admin@pass"));

        Set<Roles> adminRoles = new HashSet<>();
        adminRoles.add(adminRole);
        adminUser.setRoles(adminRoles);
        userDao.save(adminUser);

    }
    public User registerNewUser(User user) {
        Roles role = roleDao.findById("User").get();
        Set<Roles> userRoles = new HashSet<>();
        userRoles.add(role);
        user.setRoles(userRoles);
        user.setUserPassword(getEncodedPassword(user.getUserPassword()));

        return userDao.save(user);
    }

    public String getEncodedPassword(String password) {
        return passwordEncoder.encode(password);
    }
}
