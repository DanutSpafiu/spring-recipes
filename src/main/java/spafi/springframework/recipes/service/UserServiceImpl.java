package spafi.springframework.recipes.service;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import spafi.springframework.recipes.model.Favorite;
import spafi.springframework.recipes.model.Rating;
import spafi.springframework.recipes.model.Recipe;
import spafi.springframework.recipes.model.User;
import spafi.springframework.recipes.repositories.FavoriteRepository;
import spafi.springframework.recipes.repositories.RatingRepository;
import spafi.springframework.recipes.repositories.RecipeRepository;
import spafi.springframework.recipes.repositories.UserRepository;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RecipeRepository recipeRepository;
    private final FavoriteRepository favoriteRepository;
    private final RatingRepository ratingRepository;
    public UserServiceImpl(UserRepository userRepository,
                           RecipeRepository recipeRepository,
                           FavoriteRepository favoriteRepository,
                           RatingRepository ratingRepository) {
        this.userRepository = userRepository;
        this.recipeRepository = recipeRepository;
        this.favoriteRepository = favoriteRepository;
        this.ratingRepository = ratingRepository;

    }

    @Transactional
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

    @Transactional
    @Override
    public void rateRecipe(Long userId, Long recipeId, int rate) {
        if(rate < 1 || rate > 5) {
            throw new IllegalArgumentException("Invalid rating: " + rate);
        }
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found: " + userId));
        Recipe recipe = recipeRepository.findById(recipeId)
                .orElseThrow(() -> new RuntimeException("Recipe not found: " + recipeId));

        Rating rating = ratingRepository.findByUserAndRecipe(user, recipe)
                .orElseGet( () -> {
                    Rating r = new Rating();
                    r.setUser(user);
                    r.setRecipe(recipe);
                    return r;
                });
        rating.setScore((short) rate);
        ratingRepository.save(rating);
    }
}