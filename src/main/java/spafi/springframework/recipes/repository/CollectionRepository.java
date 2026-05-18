package spafi.springframework.recipes.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import spafi.springframework.recipes.model.RecipeCollection;


public interface CollectionRepository extends JpaRepository<RecipeCollection, Long> {
}
