package com.trubru.brewbro.data.local;


import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;

import com.trubru.brewbro.data.BrewBroDataSource;
import com.trubru.brewbro.data.Recipe;
import com.trubru.brewbro.util.AppExecutors;

import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Concrete implementation of a data source as a db.
 */
public class BrewBroLocalDataSource implements BrewBroDataSource {

    private static volatile BrewBroLocalDataSource INSTANCE;

    private RecipeDao mRecipeDao;

    private AppExecutors mAppExecutors;

    //Prevent direct instantiation
    private BrewBroLocalDataSource(@NonNull AppExecutors appExecutors,
                                   @NonNull RecipeDao recipeDao) {
        mAppExecutors = appExecutors;
        mRecipeDao = recipeDao;
    }

    public static BrewBroLocalDataSource getInstance(@NonNull AppExecutors appExecutors,
                                                     @NonNull RecipeDao recipeDao) {
        if (INSTANCE == null) {
            synchronized (BrewBroLocalDataSource.class) {
                if (INSTANCE ==null) {
                    INSTANCE = new BrewBroLocalDataSource(appExecutors,recipeDao);
                }
            }
        }
        return INSTANCE;
    }

    /**
     * Note: {@Link LoadRecipesCallback#onDataNotAvailable()} is fired if the database doesn't exist
     * or if the table is empty
     */
    @Override
    public void getRecipes(@NonNull final LoadRecipesCallback callback) {
        Runnable runnable = () -> {
            final List<Recipe> recipes = mRecipeDao.getRecipes();
            mAppExecutors.mainThread().execute(() -> {
                if (recipes.isEmpty()) {
                    callback.onDataNotAvailable();
                } else {
                    callback.onRecipesLoaded(recipes);
                }
            });
        };
        mAppExecutors.diskIO().execute(runnable);
    }


    @Override
    public void getRecipe(@NonNull final String recipeId, @NonNull final GetRecipeCallback callback) {
        Runnable runnable = () -> {
            final Recipe recipe = mRecipeDao.getRecipeById(recipeId);

            mAppExecutors.mainThread().execute(() -> {
                if (recipe != null) {
                    callback.onRecipeLoaded(recipe);
                } else {
                    callback.onDataNotAvailable();
                }
            });
        };
        mAppExecutors.mainThread().execute(runnable);
    }

    @Override
    public void saveRecipe(@NonNull final Recipe recipe) {
        checkNotNull(recipe);
        Runnable saveRunnable = () -> { mRecipeDao.insertRecipe(recipe); };
        mAppExecutors.diskIO().execute(saveRunnable);
    }


    @Override
    public void completeRecipe(@NonNull final Recipe recipe) {
        Runnable completeRunnable = () -> {
            mRecipeDao.updateCompleted(recipe.getId(), true);
        };
        mAppExecutors.diskIO().execute(completeRunnable);
    }

    @Override
    public void completeRecipe(@NonNull String taskId) {
        // Not required for the local data source because the {@link BrewBroRepository} handles
        // converting from a {@code recipeId} to a {@link recipe} using its cached data.
    }

    @Override
    public void activateRecipe(@NonNull final Recipe recipe) {
        Runnable activateRunnable = () -> {
            mRecipeDao.updateCompleted(recipe.getId(), true);
        };
        mAppExecutors.diskIO().execute(activateRunnable);
    }

    @Override
    public void activateRecipe(@NonNull String recipeId) {
        // Not required for the local data source because the {@link BrewBroRepository} handles
        // converting from a {@code recipeId} to a {@link recipe} using its cached data.
    }

    @Override
    public void clearCompletedRecipes() {
        Runnable clearRecipeRunnable = () -> {mRecipeDao.deleteCompletedRecipes();};
        mAppExecutors.diskIO().execute(clearRecipeRunnable);
    }

    @Override
    public void refreshRecipes() {
        // Not required because the {@link RecipesRepository} handles the logic of refreshing the
        // recipes from all the available data sources.
    }

    @Override
    public void deleteRecipe(@NonNull String recipeId) {
        Runnable deleteRunnable = () -> {mRecipeDao.deleteRecipeById(recipeId);};
    }

    @Override
    public void deleteAllRecipes() {
        Runnable deleteRunnable = () -> {mRecipeDao.deleteRecipes();};
        mAppExecutors.diskIO().execute(deleteRunnable);
    }

    @VisibleForTesting
    static void clearInstance() {
        INSTANCE = null;
    }

}
