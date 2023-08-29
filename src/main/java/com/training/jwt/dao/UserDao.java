package com.training.jwt.dao;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.training.jwt.model.User;

@Repository
public interface UserDao extends CrudRepository<User, Integer> {
	
	@Query("SELECT u FROM User u JOIN FETCH u.role WHERE u.userName = :username")
	User findByUserName(@Param("username") String username);


}
