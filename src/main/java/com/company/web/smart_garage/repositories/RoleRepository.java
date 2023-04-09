package com.company.web.smart_garage.repositories;

import com.company.web.smart_garage.models.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    // Optional<Role> findByName(String name);
    Role findByName(String username);

}
