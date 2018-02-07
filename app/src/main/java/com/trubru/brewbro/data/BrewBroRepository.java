package com.trubru.brewbro.data;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Concrete database. That is, this takes all Data Sources and gives the rest of the app the ability
 * to use it.
 * TODO: Implement this class using Livedata (once saved it will be part of DB and no longer 'live'
 */
public class BrewBroRepository implements BrewBroDataSource {

    private volatile static BrewBroRepository INSTANCE = null;
    //TODO: Change this to match the remote data source
    private final BrewBroDataSource mBrewBroRemoteDataSource;
    private final BrewBroDataSource mBrewBroLocalDataSource;

    Map<String, Recipe> mCachedRecipes;

    private boolean mCacheIsFucked = false; //forces an update on next try

    //Prevent direct instantiation
    private BrewBroRepository(@NonNull BrewBroDataSource brewBroRemoteDataSource,
                              @NonNull BrewBroDataSource brewBroLocalDataSource) {
        mBrewBroRemoteDataSource = checkNotNull(brewBroRemoteDataSource);
        mBrewBroLocalDataSource = checkNotNull(brewBroLocalDataSource);
    }

    /**
     * Returns the single instance of this class, creating it if necessary.
     */
    public static BrewBroRepository getInstance(BrewBroDataSource brewBroRemoteDataSource,
                                                BrewBroDataSource brewBroLocalDatabase) {
        if (INSTANCE == null) {
            synchronized (BrewBroRepository.class) {
                if (INSTANCE == null) {
                    INSTANCE = new BrewBroRepository(brewBroRemoteDataSource, brewBroLocalDatabase);
                }
            }
        }
        return INSTANCE;
    }

    public static void destroyInstance() {INSTANCE = null;} //used to create a new instance next time it's called

    /**
     * Gets the database from either local or remote, whichever is available sooner
     */
    @Override
    public void getRecipes(@NonNull LoadRecipesCallback callback) {
        checkNotNull(callback);

        //respond immediately with cache if available and cache is not fucked
        if(mCachedRecipes != null && !mCacheIsFucked) {
            callback.onRecipesLoaded(new ArrayList<>(mCachedRecipes.values()));
            return;
        }

        //EspressoIdlingResource.increment(); // App is busy until further notice

        if (mCacheIsFucked) {
            //if cache is fucked, renew from remote
            getRecipesFromRemoteDataSource(callback);
        } else {
            mBrewBroLocalDataSource.getRecipes(new LoadRecipesCallback() {
                @Override
                public void onRecipesLoaded(List<Recipe> recipes) {
                    refreshCache(recipes);

                    //EspressoIdlingResource.decrement();  --sets app as idle
                    callback.onRecipesLoaded(new ArrayList<>(mCachedRecipes.values()));
                }

                @Override
                public void onDataNotAvailable() {
                    getRecipesFromRemoteDataSource(callback);
                }
            });
        }
    }

    @Nullable
    private Recipe getRecipeWithID(@NonNull String id) {
        checkNotNull(id);
        if (mCachedRecipes == null || mCachedRecipes.isEmpty()) {
            return null;
        } else {
            return mCachedRecipes.get(id);
        }
    }

    private void refreshCache(List<Recipe> recipes) {
        if (mCachedRecipes == null) {
            mCachedRecipes = new LinkedHashMap<>();
        }
        mCachedRecipes.clear();
        for (Recipe recipe : recipes) {
            mCachedRecipes.put(recipe.getId(),recipe);
        }
        mCacheIsFucked = false;
    }

    private void getRecipesFromRemoteDataSource(@NonNull final LoadRecipesCallback callback) {
        mBrewBroRemoteDataSource.getRecipes(new LoadRecipesCallback() {
            @Override
            public void onRecipesLoaded(List<Recipe> recipes) {
                refreshCache(recipes);
                refreshLocalDataSource(recipes);

                //EspressoIdlingResource.decrement(); //sets app as idling
                callback.onRecipesLoaded(new ArrayList<>(mCachedRecipes.values()));
            }

            @Override
            public void onDataNotAvailable() {
                //EspressoIdlingResource.decrement(); //sets app as idling
                callback.onDataNotAvailable();
            }
        });
    }

    private void refreshLocalDataSource(List<Recipe> recipes) {
        mBrewBroLocalDataSource.deleteAllRecipes();
        for (Recipe recipe : recipes) {
            mBrewBroLocalDataSource.saveRecipe(recipe);
        }
    }

    @Override
    public void getRecipe(@NonNull String recipeId, @NonNull GetRecipeCallback callback) {
        checkNotNull(recipeId);
        checkNotNull(callback);

        Recipe cachedRecipe = getRecipeWithID(recipeId);

        //returns the current cached recipe if it exists
        if (cachedRecipe != null) {
            callback.onRecipeLoaded(cachedRecipe);
            return;
        }

        //EspressoIdlingResource.increment(); // App is busy until further notice

        //Otherwise, checks the Local Data Source
        mBrewBroLocalDataSource.getRecipe(recipeId, new GetRecipeCallback() {
            @Override
            public void onRecipeLoaded(Recipe recipe) {
                if (mCachedRecipes == null) {
                    mCachedRecipes = new LinkedHashMap<>();
                }
                mCachedRecipes.put(recipe.getId(), recipe);

                //EspressoIdlingResource.decrement(); // Set app as idle.

                callback.onRecipeLoaded(recipe);
            }

            @Override
            public void onDataNotAvailable() {
                //If data not available, update local database with remote
                mBrewBroRemoteDataSource.getRecipe(recipeId, new GetRecipeCallback() {
                    @Override
                    public void onRecipeLoaded(Recipe recipe) {
                        //if the data doesn't exist, return
                        if (recipe == null) {
                            onDataNotAvailable();
                            return;
                        }

                        if (mCachedRecipes == null) {
                            mCachedRecipes = new LinkedHashMap<>();
                        }
                        mCachedRecipes.put(recipe.getId(),recipe);
                        //EspressoIdlingResource.decrement(); // Set app as idle.

                        callback.onRecipeLoaded(recipe);
                    }

                    @Override
                    public void onDataNotAvailable() {
                        //EspressoIdlingResource.decrement(); // Set app as idle.
                        callback.onDataNotAvailable();
                    }
                });            }
        });
    }

    @Override
    public void saveRecipe(@NonNull Recipe recipe) {
        checkNotNull(recipe);
        mBrewBroRemoteDataSource.saveRecipe(recipe);
        mBrewBroLocalDataSource.saveRecipe(recipe);

        if (mCachedRecipes == null) {
            mCachedRecipes = new LinkedHashMap<>();
        }
        mCachedRecipes.put(recipe.getId(),recipe);
    }

    @Override
    public void deleteRecipe(@NonNull String recipeId) {
        mBrewBroRemoteDataSource.deleteRecipe(recipeId);
        mBrewBroLocalDataSource.deleteRecipe(recipeId);

        mCachedRecipes.remove(recipeId);
    }

    @Override
    public void completeRecipe(@NonNull Recipe recipe) {
        checkNotNull(recipe);
        mBrewBroRemoteDataSource.completeRecipe(recipe);
        mBrewBroLocalDataSource.completeRecipe(recipe);

        Recipe completedRecipe = new Recipe(recipe.getName(),recipe.getDescription(),recipe.getId());

        // Do in memory cache update to keep the app UI up to date
        if (mCachedRecipes == null) {
            mCachedRecipes = new LinkedHashMap<>();
        }
        mCachedRecipes.put(recipe.getId(),completedRecipe);
    }

    @Override
    public void completeRecipe(@NonNull String recipeId) {
        checkNotNull(recipeId);
        completeRecipe(getRecipeWithID(recipeId));
    }

    @Override
    public void activateRecipe(@NonNull Recipe recipe) {
        checkNotNull(recipe);
        mBrewBroRemoteDataSource.activateRecipe(recipe);
        mBrewBroLocalDataSource.activateRecipe(recipe);

        Recipe activeRecipe = new Recipe(recipe.getName(),recipe.getDescription(),recipe.getId());

        // Do in memory cache update to keep the app UI up to date
        if (mCachedRecipes == null) {
            mCachedRecipes = new LinkedHashMap<>();
        }
        mCachedRecipes.put(recipe.getId(),activeRecipe);
    }

    @Override
    public void activateRecipe(@NonNull String recipeId) {
        checkNotNull(recipeId);
        activateRecipe(getRecipeWithID(recipeId));
    }

    @Override
    public void clearCompletedRecipes() {
        mBrewBroRemoteDataSource.clearCompletedRecipes();
        mBrewBroLocalDataSource.clearCompletedRecipes();

        // Do in memory cache update to keep the app UI up to date
        if (mCachedRecipes == null) {
            mCachedRecipes = new LinkedHashMap<>();
        }
        Iterator<Map.Entry<String, Recipe>> iterator = mCachedRecipes.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, Recipe> entry = iterator.next();
            if(entry.getValue().isCompleted()) {
                iterator.remove();
            }
        }

    }

    @Override
    public void refreshRecipes() {
        mCacheIsFucked = true;
    }

    @Override
    public void deleteAllRecipes() {
        mBrewBroLocalDataSource.deleteAllRecipes();
        mBrewBroRemoteDataSource.deleteAllRecipes();

        if (mCachedRecipes == null) {
            mCachedRecipes = new LinkedHashMap<>();
        }
        mCachedRecipes.clear();
    }


}
