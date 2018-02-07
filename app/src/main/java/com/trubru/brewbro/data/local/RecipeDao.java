package com.trubru.brewbro.data.local;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.trubru.brewbro.data.Recipe;

import java.util.List;

/**
 * Data Access Object for Recipes table
 */
@Dao
public interface RecipeDao {

    /**
     * Select all recipes from the recipes table.
     * @return all recipes.
     */
    @Query("SELECT * FROM Recipes")
    List<Recipe> getRecipes();

    /**
     * Select a recipe by id.
     *
     * @param recipeId the recipe id.
     * @return the recipe with recipeId.
     */
    @Query("SELECT * FROM Recipes WHERE entryid = :recipeId")
    Recipe getRecipeById(String recipeId);

    /**
     * Insert a recipe in the database. If the recipe already exists, replace it.
     * @param recipe the recipe to be inserted.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertRecipe(Recipe recipe);

    /**
     * Update a recipe.
     * @param recipe recipe to be updated
     * @return the number of recipes updated. This should always be 1.
     */
    @Update
    int updateRecipe(Recipe recipe);

    /**
     * Update the complete status of a recipe
     * @param recipeId    id of the recipe
     * @param completed status to be updated
     */
    @Query("UPDATE recipes SET completed = :completed WHERE entryid = :recipeId")
    void updateCompleted(String recipeId, boolean completed);

    /**
     * Delete a recipe by id.
     * @return the number of recipes deleted. This should always be 1.
     */
    @Query("DELETE FROM Recipes WHERE entryid = :recipeId")
    int deleteRecipeById(String recipeId);

    /**
     * Delete all recipes.
     */
    @Query("DELETE FROM Recipes")
    void deleteRecipes();

    /**
     * Delete all completed recipes from the table.
     * @return the number of recipes deleted.
     */
    @Query("DELETE FROM Recipes WHERE completed = 1")
    int deleteCompletedRecipes();
}
