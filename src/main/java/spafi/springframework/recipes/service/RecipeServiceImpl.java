package spafi.springframework.recipes.service;

import org.springframework.stereotype.Service;
import spafi.springframework.recipes.model.Recipe;
import spafi.springframework.recipes.repositories.RecipeRepository;

import java.util.List;

@Service
public class RecipeServiceImpl implements RecipeService {

    private final RecipeRepository recipeRepository;

    public RecipeServiceImpl(RecipeRepository recipeRepository) {
        this.recipeRepository = recipeRepository;
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
}