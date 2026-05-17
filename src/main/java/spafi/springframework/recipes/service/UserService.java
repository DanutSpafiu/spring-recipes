package spafi.springframework.recipes.service;

public interface UserService {
    void addFavoriteRecipe(Long userId, Long recipeId);
    void rateRecipe(Long userId, Long recipeId, int rating);
}
