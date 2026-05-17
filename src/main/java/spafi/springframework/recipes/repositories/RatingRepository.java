package spafi.springframework.recipes.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import spafi.springframework.recipes.model.Rating;
import spafi.springframework.recipes.model.Recipe;
import spafi.springframework.recipes.model.User;

import java.util.Optional;

public interface RatingRepository extends JpaRepository<Rating, Long> {
    Optional<Rating> findByUserAndRecipe(User user, Recipe recipe);
}
