package spafi.springframework.recipes.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import spafi.springframework.recipes.model.User;

public interface UserRepository extends JpaRepository<User, Long> {
}
