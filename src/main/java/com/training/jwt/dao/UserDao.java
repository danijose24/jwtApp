package com.training.jwt.dao;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.training.jwt.model.User;

@Repository
public interface UserDao extends CrudRepository<User, Integer> {
	
	User findByUserName(String userName);

}