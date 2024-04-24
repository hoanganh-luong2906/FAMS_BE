package com.example.FAMS.models;

import com.example.FAMS.enums.Permission;
import com.example.FAMS.enums.Role;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "user_permission")
public class UserPermission {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int permissionId;

    @Column(name = "role", nullable = false)
    @Enumerated(EnumType.STRING)
    private Role role;

    @Column(name = "syllabus", nullable = false)
    @Enumerated(EnumType.STRING)
    private List<Permission> syllabus;

    @Column(name = "training_program", nullable = false)
    @Enumerated(EnumType.STRING)
    private List<Permission> trainingProgram;

    @Column(name = "user_class", nullable = false)
    @Enumerated(EnumType.STRING)
    private List<Permission> userClass;

    @Column(name = "learning_material")
    @Enumerated(EnumType.STRING)
    private List<Permission> learningMaterial;

    @Column(name = "user_management", nullable = false)
    @Enumerated(EnumType.STRING)
    private List<Permission> userManagement;

    @OneToMany(mappedBy = "role")
    @JsonManagedReference
    private Set<User> users = new HashSet<>();

    public List<SimpleGrantedAuthority> getAuthorities() {
        List<SimpleGrantedAuthority> authList = new ArrayList<>();
        syllabus.forEach(permission -> {
            authList.add(new SimpleGrantedAuthority(permission.getPermission()));
        });
        trainingProgram.forEach(permission -> {
            authList.add(new SimpleGrantedAuthority(permission.getPermission()));
        });
        userClass.forEach(permission -> {
            authList.add(new SimpleGrantedAuthority(permission.getPermission()));
        });
        learningMaterial.forEach(permission -> {
            authList.add(new SimpleGrantedAuthority(permission.getPermission()));
        });
        userManagement.forEach(permission -> {
            authList.add(new SimpleGrantedAuthority(permission.getPermission()));
        });
        authList.add(new SimpleGrantedAuthority("ROLE_" + role.name()));
        return authList;
    }
}
