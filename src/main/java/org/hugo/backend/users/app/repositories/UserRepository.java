package org.hugo.backend.users.app.repositories;
import org.hugo.backend.users.app.models.entities.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface UserRepository extends CrudRepository<User,Long> {
    @Query("Select u from User u where u.email=?1")
    Optional<User>  getUserByEmail(String email);

}
