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

    /**
     * Crea un nuevo rol en la base de datos.
     *
     * @param role El objeto Role que se va a crear.
     * @return Retorna el objeto Role creado y guardado en la base de datos.
     */
    @Override
    @Transactional
    public Role createOne(Role role) {
        return roleRepository.save(role);
    }

    /**
     * Actualiza un rol existente en la base de datos.
     *
     * @param role El objeto Role con los nuevos datos a actualizar.
     * @param id El ID del rol que se va a actualizar.
     * @return Retorna el objeto Role actualizado y guardado en la base de datos.
     * @throws RoleNotFoundException Si el rol con el ID especificado no existe en la base de datos.
     */
    @Override
    @Transactional
    public Role updateOne(Role role, Long id) {
        Role roleExist = findById(id);
        roleExist.setRole(role.getRole());
        return roleRepository.save(roleExist);
    }
    /**
     * Elimina un rol existente de la base de datos.
     *
     * @param id El ID del rol que se va a eliminar.
     * @throws RoleNotFoundException Si el rol con el ID especificado no existe en la base de datos.
     */

    @Override
    @Transactional
    public void deleteOne(Long id) {
        findById(id);
        roleRepository.deleteById(id);
    }

    /**
     * Obtiene una lista de todos los roles en la base de datos.
     *
     * @return Retorna una lista de objetos Role que representa todos los roles en la base de datos.
     */
    @Override
    @Transactional(readOnly = true)
    public List<Role> findAll() {
        return (List<Role>) roleRepository.findAll();
    }

    /**
     * Busca un rol por su ID en la base de datos.
     *
     * @param id El ID del rol que se va a buscar.
     * @return Retorna el objeto Role correspondiente al ID especificado.
     * @throws RoleNotFoundException Si el rol con el ID especificado no existe en la base de datos.
     */
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
