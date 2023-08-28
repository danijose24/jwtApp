package com.training.jwt.dao;

import com.training.jwt.model.Roles;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleDao extends CrudRepository<Roles, String> {

}
