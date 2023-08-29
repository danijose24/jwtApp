package com.training.jwt.model;

import lombok.Data;

import javax.persistence.*;
import java.util.Set;

@Data
@Entity
public class Permission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Set the generation strategy
    private Long id;
    private String permissionName;

}