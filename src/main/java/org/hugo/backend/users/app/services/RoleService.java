package org.hugo.backend.users.app.services;

import org.hugo.backend.users.app.models.entities.Role;

import java.util.List;

public interface RoleService {
    Role createOne(Role role);
    Role updateOne(Role role,Long id);
    void deleteOne(Long id);
    List<Role> findAll();
    Role findById(Long id);
}
