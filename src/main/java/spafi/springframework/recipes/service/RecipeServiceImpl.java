package spafi.springframework.recipes.service;

import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.stereotype.Service;
import spafi.springframework.recipes.model.Recipe;
import spafi.springframework.recipes.model.User;
import spafi.springframework.recipes.repository.RecipeRepository;
import spafi.springframework.recipes.repository.UserRepository;

import java.util.List;

@Service
public class RecipeServiceImpl {

    private final RecipeRepository recipeRepository;
    private final UserRepository userRepository;

    public RecipeServiceImpl(RecipeRepository recipeRepository, UserRepository userRepository) {
        this.recipeRepository = recipeRepository;
        this.userRepository = userRepository;
    }

    public List<Recipe> getAll() {
        return recipeRepository.findAll();
    }

    public Recipe getById(Long id) throws ChangeSetPersister.NotFoundException {
        return recipeRepository.findById(id)
                .orElseThrow(() -> new ChangeSetPersister.NotFoundException());
    }

    public Recipe addNew(Recipe recipe) {
        return recipeRepository.save(recipe);
    }

    public Recipe addNewByUser(Long userId, Recipe recipe) {
        User owner = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found: " + userId));
        recipe.setOwner(owner);
        return recipeRepository.save(recipe);
    }
}
