package com.trubru.brewbro.recipelist;

/**
 * Created by Kolin on 2/3/2018.
 * This interface defines navigation actions that can be called from an item in the list
 * of recipes.
 * TODO add shortcut option for going directly
 */

public interface RecipeItemNavigator {
    void openRecipeDetails(String recipeId);
}
