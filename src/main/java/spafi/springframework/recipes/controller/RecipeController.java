package spafi.springframework.recipes.controller;


import lombok.AllArgsConstructor;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import spafi.springframework.recipes.model.Recipe;
import spafi.springframework.recipes.service.RecipeServiceImpl;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/api/recipes")
public class RecipeController {

    private final RecipeServiceImpl recipeService;

    @GetMapping
    public List<Recipe> getAll() {
        return recipeService.getAll();
    }

    @GetMapping("/{id}")
    public Recipe getRecipeById(@PathVariable Long id) throws ChangeSetPersister.NotFoundException {
        return recipeService.getById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Recipe addNewRecipe(@RequestBody Recipe recipe) {
        return recipeService.addNew(recipe);
    }

    @PostMapping("/users/{userId}")
    @ResponseStatus(HttpStatus.CREATED)
    public Recipe addNewRecipeByUser(@PathVariable Long userId, @RequestBody Recipe recipe) {
        return recipeService.addNewByUser(userId, recipe);
    }

}
