package spafi.springframework.recipes.controller;


import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import spafi.springframework.recipes.model.Recipe;
import spafi.springframework.recipes.service.RecipeService;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/api/recipes")
public class RecipeController {

    private final RecipeService recipeService;

    @GetMapping
    public List<Recipe> getAllRecipes() {
        return recipeService.getAllRecipes();
    }

    @GetMapping("/{id}")
    public Recipe getRecipeById(@PathVariable Long id) {
        return recipeService.getRecipeById(id);
    }
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Recipe addNewRecipe(@RequestBody Recipe recipe) {
        return recipeService.addNewRecipe(recipe);
    }

    @PostMapping("/users/{userId}")
    @ResponseStatus(HttpStatus.CREATED)
    public Recipe addNewRecipeByUser(@PathVariable Long userId, @RequestBody Recipe recipe) {
        return recipeService.addNewRecipeByUser(userId, recipe);
    }


}