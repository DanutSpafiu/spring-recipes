package spafi.springframework.recipes.service;

import org.springframework.stereotype.Service;
import spafi.springframework.recipes.model.Recipe;
import spafi.springframework.recipes.model.User;
import spafi.springframework.recipes.repositories.RecipeRepository;
import spafi.springframework.recipes.repositories.UserRepository;

import java.util.List;

@Service
public class RecipeServiceImpl implements RecipeService {

    private final RecipeRepository recipeRepository;
    private final UserRepository userRepository;

    public RecipeServiceImpl(RecipeRepository recipeRepository, UserRepository userRepository) {
        this.recipeRepository = recipeRepository;
        this.userRepository = userRepository;
    }

    @Override
    public List<Recipe> getAllRecipes() {
        return recipeRepository.findAll();
    }

    @Override
    public Recipe getRecipeById(Long id) {
        return recipeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Recipe not found: " + id));
    }

    @Override
    public Recipe addNewRecipe(Recipe recipe) {
        return recipeRepository.save(recipe);
    }

    @Override
    public Recipe addNewRecipeByUser(Long userId, Recipe recipe) {
        User owner = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found: " + userId));
        recipe.setOwner(owner);
        return recipeRepository.save(recipe);
    }
}