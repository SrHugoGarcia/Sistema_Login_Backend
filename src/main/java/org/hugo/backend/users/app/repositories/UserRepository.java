package org.hugo.backend.users.app.repositories;
import org.hugo.backend.users.app.models.entities.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends CrudRepository<User,Long> {
    @Query("Select u from User u where u.email=?1")
    Optional<User>  getUserByEmail(String email);

    // Método para filtrar y paginar usuarios usando una especificación y Pageable.
    List<User> findAll(Specification<User> spec, Pageable pageable);
    List<User> findAll(Pageable pageable);

}
