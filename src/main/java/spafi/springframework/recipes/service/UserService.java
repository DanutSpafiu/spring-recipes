package spafi.springframework.recipes.service;

public interface UserService {
    void addFavoriteRecipe(Long userId, Long recipeId);
}
