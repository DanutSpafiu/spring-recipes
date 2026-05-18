package spafi.springframework.recipes;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;
import spafi.springframework.recipes.model.Recipe;
import spafi.springframework.recipes.model.User;
import spafi.springframework.recipes.repository.RecipeRepository;
import spafi.springframework.recipes.repository.UserRepository;

import jakarta.annotation.PostConstruct;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class RecipesApplicationTests {

    @Autowired
    private WebApplicationContext context;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private RecipeRepository recipeRepository;

    @Autowired
    private UserRepository userRepository;

    private MockMvc mockMvc;

    @PostConstruct
    void setUpMockMvc() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
    }

    private User seedUser(String username, String email) {
        User u = new User();
        u.setUsername(username);
        u.setEmail(email);
        return userRepository.save(u);
    }

    private Recipe seedRecipe(String title, User owner) {
        Recipe r = new Recipe();
        r.setTitle(title);
        r.setIngredients("flour, water");
        r.setInstructions("mix and bake");
        r.setOwner(owner);
        return recipeRepository.save(r);
    }

    // -----------------------------------------------------------------------
    // GET /api/recipes
    // -----------------------------------------------------------------------
    @Nested
    @DisplayName("GET /api/recipes")
    class GetAllRecipes {

        @Test
        @DisplayName("TC-R1: empty DB -> empty list")
        void emptyList() throws Exception {
            // GIVEN: the recipes table is empty (handled by @Transactional rollback)
            // WHEN
            mockMvc.perform(get("/api/recipes"))
                    // THEN
                    .andExpect(status().isOk())
                    .andExpect(content().json("[]"));
        }

        @Test
        @DisplayName("TC-R2: multiple recipes -> all returned")
        void multipleRecipes() throws Exception {
            // GIVEN
            User owner = seedUser("alice", "alice@x.com");
            seedRecipe("Pizza", owner);
            seedRecipe("Pasta", owner);
            seedRecipe("Pie",   owner);

            // WHEN + THEN
            mockMvc.perform(get("/api/recipes"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(3))
                    .andExpect(jsonPath("$[0].title").exists())
                    .andExpect(jsonPath("$[0].ingredients").exists())
                    .andExpect(jsonPath("$[0].instructions").exists());
        }
    }

    // -----------------------------------------------------------------------
    // GET /api/recipes/{id}
    // -----------------------------------------------------------------------
    @Nested
    @DisplayName("GET /api/recipes/{id}")
    class GetRecipeById {

        @Test
        @DisplayName("TC-R3: existing recipe -> 200 with payload")
        void existingRecipe() throws Exception {
            // GIVEN
            User owner = seedUser("bob", "bob@x.com");
            Recipe saved = seedRecipe("Pizza", owner);

            // WHEN + THEN
            mockMvc.perform(get("/api/recipes/{id}", saved.getId()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(saved.getId()))
                    .andExpect(jsonPath("$.title").value("Pizza"));
        }

        @Test
        @DisplayName("TC-R4: missing recipe -> service throws NotFoundException")
        void missingRecipe() {
            // GIVEN: no recipe with id = 999

            // WHEN + THEN: controller doesn't translate the exception, so MockMvc
            // surfaces it as a wrapped exception (current behavior of the codebase)
            assertThrows(Exception.class,
                    () -> mockMvc.perform(get("/api/recipes/{id}", 999L)));
        }

        @Test
        @DisplayName("TC-R5: non-numeric id -> 400 Bad Request")
        void badPathVariable() throws Exception {
            // GIVEN: DB state irrelevant
            // WHEN + THEN: Spring fails to bind "abc" to Long
            mockMvc.perform(get("/api/recipes/abc"))
                    .andExpect(status().isBadRequest());
        }
    }

    // -----------------------------------------------------------------------
    // POST /api/recipes
    // -----------------------------------------------------------------------
    @Nested
    @DisplayName("POST /api/recipes")
    class CreateRecipe {

        @Test
        @DisplayName("TC-R6: valid recipe with existing owner -> 201 Created")
        void validRecipe() throws Exception {
            // GIVEN
            User owner = seedUser("carol", "carol@x.com");
            Map<String, Object> body = Map.of(
                    "title", "Soup",
                    "ingredients", "water, salt",
                    "instructions", "boil",
                    "owner", Map.of("id", owner.getId())
            );

            // WHEN + THEN
            mockMvc.perform(post("/api/recipes")
                            .contentType("application/json")
                            .content(objectMapper.writeValueAsString(body)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.id").exists())
                    .andExpect(jsonPath("$.title").value("Soup"))
                    .andExpect(jsonPath("$.createdAt").exists());

            assertEquals(1, recipeRepository.count());
        }

        @Test
        @DisplayName("TC-R7: missing required field (ingredients) -> persistence fails")
        void missingRequiredField() {
            // GIVEN: body has no `ingredients`
            Map<String, Object> body = Map.of(
                    "title", "Soup",
                    "instructions", "boil"
            );

            // WHEN + THEN: JPA rejects the insert because of NOT NULL constraint
            assertThrows(Exception.class, () ->
                    mockMvc.perform(post("/api/recipes")
                            .contentType("application/json")
                            .content(objectMapper.writeValueAsString(body))));
        }

        @Test
        @DisplayName("TC-R8: no owner supplied -> persistence fails (owner_id NOT NULL)")
        void noOwner() {
            // GIVEN
            Map<String, Object> body = Map.of(
                    "title", "Soup",
                    "ingredients", "water",
                    "instructions", "boil"
            );

            // WHEN + THEN
            assertThrows(Exception.class, () ->
                    mockMvc.perform(post("/api/recipes")
                            .contentType("application/json")
                            .content(objectMapper.writeValueAsString(body))));
        }
    }

    // -----------------------------------------------------------------------
    // POST /api/recipes/users/{userId}
    // -----------------------------------------------------------------------
    @Nested
    @DisplayName("POST /api/recipes/users/{userId}")
    class CreateRecipeForUser {

        @Test
        @DisplayName("TC-R9: existing user -> recipe saved with that owner")
        void existingUser() throws Exception {
            // GIVEN
            User owner = seedUser("dave", "dave@x.com");
            Map<String, Object> body = Map.of(
                    "title", "Salad",
                    "ingredients", "lettuce",
                    "instructions", "mix"
            );

            // WHEN + THEN
            mockMvc.perform(post("/api/recipes/users/{userId}", owner.getId())
                            .contentType("application/json")
                            .content(objectMapper.writeValueAsString(body)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.id").exists())
                    .andExpect(jsonPath("$.title").value("Salad"))
                    .andExpect(jsonPath("$.owner.id").value(owner.getId()));

            assertEquals(1, recipeRepository.count());
        }

        @Test
        @DisplayName("TC-R10: unknown user -> service throws, no recipe inserted")
        void unknownUser() {
            // GIVEN: no user with id = 999
            Map<String, Object> body = Map.of(
                    "title", "Salad",
                    "ingredients", "lettuce",
                    "instructions", "mix"
            );

            // WHEN + THEN
            assertThrows(Exception.class, () ->
                    mockMvc.perform(post("/api/recipes/users/{userId}", 999L)
                            .contentType("application/json")
                            .content(objectMapper.writeValueAsString(body))));

            assertEquals(0, recipeRepository.count());
        }
    }

    // -----------------------------------------------------------------------
    // GET /api/users
    // -----------------------------------------------------------------------
    @Nested
    @DisplayName("GET /api/users")
    class GetAllUsers {

        @Test
        @DisplayName("TC-U1: empty DB -> empty list")
        void emptyList() throws Exception {
            mockMvc.perform(get("/api/users"))
                    .andExpect(status().isOk())
                    .andExpect(content().json("[]"));
        }

        @Test
        @DisplayName("TC-U2: multiple users -> all returned")
        void multipleUsers() throws Exception {
            // GIVEN
            seedUser("eve",  "eve@x.com");
            seedUser("finn", "finn@x.com");

            // WHEN + THEN
            mockMvc.perform(get("/api/users"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(2));
        }
    }

    // -----------------------------------------------------------------------
    // GET /api/users/{id}
    // -----------------------------------------------------------------------
    @Nested
    @DisplayName("GET /api/users/{id}")
    class GetUserById {

        @Test
        @DisplayName("TC-U3: existing user -> 200 with payload")
        void existingUser() throws Exception {
            // GIVEN
            User saved = seedUser("gina", "gina@x.com");

            // WHEN + THEN
            mockMvc.perform(get("/api/users/{id}", saved.getId()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(saved.getId()))
                    .andExpect(jsonPath("$.username").value("gina"));
        }

        @Test
        @DisplayName("TC-U4: missing user -> service throws RuntimeException")
        void missingUser() {
            // GIVEN: no user with id = 42

            // WHEN + THEN
            Throwable root = assertThrows(Exception.class,
                    () -> mockMvc.perform(get("/api/users/{id}", 42L)));

            // Walk to the root cause and confirm it mentions the missing id
            while (root.getCause() != null) root = root.getCause();
            assertNotNull(root.getMessage());
            assertTrue(root.getMessage().contains("42"));
        }
    }

    // -----------------------------------------------------------------------
    // POST /api/users
    // -----------------------------------------------------------------------
    @Nested
    @DisplayName("POST /api/users")
    class CreateUser {

        @Test
        @DisplayName("TC-U5: valid new user -> 201 with generated id")
        void validUser() throws Exception {
            // GIVEN
            Map<String, Object> body = Map.of(
                    "username", "alice",
                    "email",    "alice@x.com"
            );

            // WHEN + THEN
            mockMvc.perform(post("/api/users")
                            .contentType("application/json")
                            .content(objectMapper.writeValueAsString(body)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.id").exists())
                    .andExpect(jsonPath("$.username").value("alice"))
                    .andExpect(jsonPath("$.createdAt").exists());

            assertEquals(1, userRepository.count());
        }

        @Test
        @DisplayName("TC-U6: duplicate username -> unique constraint violated")
        void duplicateUsername() {
            // GIVEN
            seedUser("alice", "alice@x.com");
            Map<String, Object> body = Map.of(
                    "username", "alice",
                    "email",    "different@x.com"
            );

            // WHEN + THEN
            assertThrows(Exception.class, () ->
                    mockMvc.perform(post("/api/users")
                            .contentType("application/json")
                            .content(objectMapper.writeValueAsString(body))));
        }

        @Test
        @DisplayName("TC-U7: duplicate email -> unique constraint violated")
        void duplicateEmail() {
            // GIVEN
            seedUser("alice", "alice@x.com");
            Map<String, Object> body = Map.of(
                    "username", "different",
                    "email",    "alice@x.com"
            );

            // WHEN + THEN
            assertThrows(Exception.class, () ->
                    mockMvc.perform(post("/api/users")
                            .contentType("application/json")
                            .content(objectMapper.writeValueAsString(body))));
        }

        @Test
        @DisplayName("TC-U8: username longer than 30 chars -> persistence fails")
        void usernameTooLong() {
            // GIVEN: username with 31 characters
            String longUsername = "a".repeat(31);
            Map<String, Object> body = Map.of(
                    "username", longUsername,
                    "email",    "long@x.com"
            );

            // WHEN + THEN
            assertThrows(Exception.class, () ->
                    mockMvc.perform(post("/api/users")
                            .contentType("application/json")
                            .content(objectMapper.writeValueAsString(body))));
        }
    }
}