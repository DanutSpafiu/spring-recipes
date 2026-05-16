package spafi.springframework.recipes.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import spafi.springframework.recipes.model.User;

public interface UserRepository extends JpaRepository<User, Long> {
}
