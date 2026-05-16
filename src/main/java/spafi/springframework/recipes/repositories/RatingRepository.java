package spafi.springframework.recipes.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import spafi.springframework.recipes.model.Rating;

public interface RatingRepository extends JpaRepository<Rating, Long> {
}
