package spafi.springframework.recipes.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import spafi.springframework.recipes.model.Recipe;

public interface RecipeRepository extends JpaRepository<Recipe, Long> {
}
