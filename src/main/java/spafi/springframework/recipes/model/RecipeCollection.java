package spafi.springframework.recipes.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(
        name = "recipe_collections",
        uniqueConstraints = @UniqueConstraint(columnNames = {"owner_id", "name"})
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RecipeCollection {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "collection_recipes",
            joinColumns = @JoinColumn(name = "collection_id", nullable = false),
            inverseJoinColumns = @JoinColumn(name = "recipe_id", nullable = false),
            uniqueConstraints = @UniqueConstraint(columnNames = {"collection_id", "recipe_id"})
    )
    private Set<Recipe> recipes = new HashSet<>();

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt = Instant.now();
}
