package com.training.jwt.model;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.persistence.FetchType;
import javax.persistence.CascadeType;
import javax.persistence.JoinTable;
import javax.persistence.JoinColumn;


import java.util.Set;

@Data
@Entity
@Table(name = "user")
public class User {

	@Id
	private String userName;
	private String userFirstName;
	private String userLastName;
	private String userPassword;

	@ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@JoinTable(name = "USER_ROLE",
			joinColumns = {
					@JoinColumn(name = "USER_ID")
			},
			inverseJoinColumns = {
					@JoinColumn(name = "ROLE_ID")
			}
	)
	private Set<Roles> roles;
}