package com.epam.esm.dao;

import com.epam.esm.model.Role;

import java.util.Optional;

public interface RoleDao extends Dao<Role> {
    Optional<Role> findByName(String name);
}
