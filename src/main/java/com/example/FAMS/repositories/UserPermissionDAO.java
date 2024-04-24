package com.example.FAMS.repositories;

import com.example.FAMS.enums.Role;
import com.example.FAMS.models.UserPermission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserPermissionDAO extends JpaRepository<UserPermission, Integer> {

    Optional<UserPermission> findUserPermissionByRole(Role role);

}
