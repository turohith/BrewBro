package com.trubru.brewbro.data;


import android.support.annotation.NonNull;

import java.util.List;

public interface BrewBroDataSource {

    interface LoadRecipesCallback {
        void onRecipesLoaded(List<Recipe> recipes);
        void onDataNotAvailable();
    }

    interface GetRecipeCallback {
        void onRecipeLoaded(Recipe recipe);
        void onDataNotAvailable();
    }

    void getRecipes(@NonNull LoadRecipesCallback callback);

    void getRecipe(@NonNull String recipeId, @NonNull GetRecipeCallback callback);

    void saveRecipe(@NonNull Recipe recipe);

    void deleteRecipe(@NonNull String recipeId);

    void completeRecipe(@NonNull Recipe recipe);

    void completeRecipe(@NonNull String taskId);

    void activateRecipe(@NonNull Recipe recipe);

    void activateRecipe(@NonNull String recipeId);

    void clearCompletedRecipes();

    void refreshRecipes();

    void deleteAllRecipes();

}
