package com.nagp.security.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.nagp.security.models.Role;

@Repository
public interface RoleRepo extends JpaRepository<Role, Long>{
	Role findByName(String name);

}
