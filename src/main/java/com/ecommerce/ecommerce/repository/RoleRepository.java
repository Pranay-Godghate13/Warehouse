package com.ecommerce.ecommerce.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ecommerce.ecommerce.pojo.AppRole;
import com.ecommerce.ecommerce.pojo.Role;

public interface RoleRepository extends JpaRepository<Role,Long> {
    
    Optional<Role> findByRoleName(AppRole appRole);
}
