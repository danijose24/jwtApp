
package com.training.jwt.dao;

import com.training.jwt.model.Permission;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PermissionDao extends CrudRepository<Permission, String> {

}
