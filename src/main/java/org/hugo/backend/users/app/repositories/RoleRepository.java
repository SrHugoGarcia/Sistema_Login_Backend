package org.hugo.backend.users.app.repositories;

import org.hugo.backend.users.app.models.entities.Role;
import org.hugo.backend.users.app.models.entities.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;


public interface RoleRepository extends CrudRepository<Role,Long> {
    @Query("SELECT r FROM Role r WHERE LOWER(r.role) LIKE %:keyword%")
    Role findRoleByKeyword(@Param("keyword") String keyword);

}
