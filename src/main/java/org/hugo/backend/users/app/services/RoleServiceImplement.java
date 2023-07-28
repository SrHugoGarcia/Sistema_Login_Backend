package org.hugo.backend.users.app.services;

import org.hugo.backend.users.app.exceptions.role.RoleNotFoundException;
import org.hugo.backend.users.app.models.entities.Role;
import org.hugo.backend.users.app.repositories.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class RoleServiceImplement implements RoleService{
    @Autowired
    private RoleRepository roleRepository;
    private static final String USER_NOT_FOUND_MSG = " no existe en la base de datos";
    private static final String ID_ERROR_MSG = "Error: el ID: ";
    @Override
    @Transactional
    public Role createOne(Role role) {
        return roleRepository.save(role);
    }

    @Override
    @Transactional
    public Role updateOne(Role role, Long id) {
        Role roleExist = findById(id);
        roleExist.setRole(role.getRole());
        return roleRepository.save(roleExist);
    }

    @Override
    @Transactional
    public void deleteOne(Long id) {
        findById(id);
        roleRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Role> findAll() {
        return (List<Role>) roleRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Role findById(Long id) {
        Role role = roleRepository.findById(id).orElse(null);
        if(role == null){
            throw new RoleNotFoundException(ID_ERROR_MSG
                    .concat(id.toString())
                    .concat(USER_NOT_FOUND_MSG));
        }
        return role;
    }
}
