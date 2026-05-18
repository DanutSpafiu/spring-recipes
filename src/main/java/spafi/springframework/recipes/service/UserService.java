package spafi.springframework.recipes.service;

import spafi.springframework.recipes.model.User;

import java.util.List;

public interface UserService {
    List<User> getAllUsers();
    User getUserById(Long id);
    User addNewUser(User user);
    void addFavoriteRecipe(Long userId, Long recipeId);
    void rateRecipe(Long userId, Long recipeId, int rating);
}
