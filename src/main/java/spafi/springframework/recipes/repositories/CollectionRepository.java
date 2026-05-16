package spafi.springframework.recipes.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;

public interface CollectionRepository extends JpaRepository<Collection, Long> {
}
