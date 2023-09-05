package com.training.jwt.service;

import com.training.jwt.dao.UserDao;
import com.training.jwt.model.Permission;
import com.training.jwt.model.Role;
import com.training.jwt.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class JwtUserDetailsService implements UserDetailsService {
	
	@Autowired
	private UserDao userDao;

	@Override
	public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {
		User user = userDao.findByUserName(userName);
		List<GrantedAuthority> authorities = new ArrayList<>();

		if (user == null) {
			throw new UsernameNotFoundException("User not found with username: " + userName);
		}
		for (Role role : user.getRole()) {
			authorities.add(new SimpleGrantedAuthority(role.getRoleName()));
			for (Permission permission : role.getPermissions()) {
				authorities.add(new SimpleGrantedAuthority(permission.getPermissionName()));
			}
		}

		return new org.springframework.security.core.userdetails.User(user.getUserName(), user.getUserPassword(),
				authorities);
	}

}
