package com.training.jwt.model;

import lombok.Data;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;


@Entity
@Table(name="role")
@Data
public class Role {
    @Id
    private String roleName;
    private String roleDescription;

    @ManyToMany(cascade = CascadeType.PERSIST)
    @JoinTable(
            name = "role_permission",
            joinColumns = @JoinColumn(name = "role_id"),
            inverseJoinColumns = @JoinColumn(name = "permission_id")
    )
    private Set<Permission> permissions = new HashSet<>();

}
