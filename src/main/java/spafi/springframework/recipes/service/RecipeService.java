package spafi.springframework.recipes.service;

import spafi.springframework.recipes.model.Recipe;

import java.util.List;

public interface RecipeService {

    List<Recipe> getAllRecipes();
    Recipe getRecipeById(Long id);
    Recipe addNewRecipe(Recipe recipe);
    Recipe addNewRecipeByUser(Long userId, Recipe recipe);
}