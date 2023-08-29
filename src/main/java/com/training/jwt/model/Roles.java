package com.training.jwt.model;

import lombok.Data;

import javax.persistence.*;


@Entity
@Table(name="roles")
@Data
public class Roles {
    @Id
    private String roleName;
    private String roleDescription;
}
