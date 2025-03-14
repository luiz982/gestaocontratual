package com.getinfo.gestaocontratual.repository;

import com.getinfo.gestaocontratual.entities.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Role findByName(String name);
}

