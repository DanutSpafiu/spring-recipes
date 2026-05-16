package spafi.springframework.recipes.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import spafi.springframework.recipes.model.RecipeCollection;

import java.util.Collection;

public interface CollectionRepository extends JpaRepository<RecipeCollection, Long> {
}
