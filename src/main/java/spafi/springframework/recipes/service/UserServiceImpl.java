package spafi.springframework.recipes.service;

import org.springframework.stereotype.Service;
import spafi.springframework.recipes.model.Favorite;
import spafi.springframework.recipes.model.Recipe;
import spafi.springframework.recipes.model.User;
import spafi.springframework.recipes.repositories.FavoriteRepository;
import spafi.springframework.recipes.repositories.RecipeRepository;
import spafi.springframework.recipes.repositories.UserRepository;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RecipeRepository recipeRepository;
    private final FavoriteRepository favoriteRepository;

    public UserServiceImpl(UserRepository userRepository,
                           RecipeRepository recipeRepository,
                           FavoriteRepository favoriteRepository) {
        this.userRepository = userRepository;
        this.recipeRepository = recipeRepository;
        this.favoriteRepository = favoriteRepository;
    }

    @Override
    public void addFavoriteRecipe(Long userId, Long recipeId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found: " + userId));

        Recipe recipe = recipeRepository.findById(recipeId)
                .orElseThrow(() -> new RuntimeException("Recipe not found: " + recipeId));

        Favorite favorite = new Favorite();
        favorite.setUser(user);
        favorite.setRecipe(recipe);

        favoriteRepository.save(favorite);
    }
}