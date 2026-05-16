package spafi.springframework.recipes.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import spafi.springframework.recipes.model.Favorite;

public interface FavoriteRepository extends JpaRepository<Favorite, Long> {
}
