/*
 * Copyright 2016, The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.trubru.brewbro.recipelist;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.content.Context;
import android.databinding.ObservableArrayList;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.databinding.ObservableList;
import android.graphics.drawable.Drawable;

import com.trubru.brewbro.R;
import com.trubru.brewbro.SingleLiveEvent;
import com.trubru.brewbro.SnackbarMessage;
import com.trubru.brewbro.addeditrecipe.AddEditRecipeActivity;
import com.trubru.brewbro.data.BrewBroDataSource;
import com.trubru.brewbro.data.BrewBroRepository;
import com.trubru.brewbro.data.Recipe;
import com.trubru.brewbro.recipedetails.RecipeDetailsActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kolin on 2/3/2018.
 */

public class RecipeListViewModel extends AndroidViewModel {
    //These observable fields will update Views automatically
    public final ObservableList<Recipe> items = new ObservableArrayList<>();
    public final ObservableBoolean dataLoading = new ObservableBoolean(false);
    public final ObservableField<String> currentFilteringLabel = new ObservableField<>();
    public final ObservableField<String> noRecipesLabel = new ObservableField<>();
    public final ObservableField<Drawable> noRecipesIconRes = new ObservableField<>();
    public final ObservableBoolean empty = new ObservableBoolean(false);
    public final ObservableBoolean recipesAddViewVisible = new ObservableBoolean();

    private RecipesFilterType mCurrentFiltering = RecipesFilterType.ALL_RECIPES;

    private final SnackbarMessage mSnackbarText = new SnackbarMessage();

    private final BrewBroRepository mBrewBroRepository;

    private final ObservableBoolean mIsDataLoadingError = new ObservableBoolean(false);

    private final SingleLiveEvent<String> mOpenRecipeEvent = new SingleLiveEvent<>();

    private final Context mContext; //To avoid leaks this must be an Application context

    private final SingleLiveEvent mNewRecipeEvent = new SingleLiveEvent();

    public RecipeListViewModel(Application context,BrewBroRepository repository) {
        super(context);
        mContext = context.getApplicationContext();
        mBrewBroRepository = repository;

        setFiltering(RecipesFilterType.ALL_RECIPES);
    }

    public void start(){ loadRecipes(false); }

    public void loadRecipes(boolean forceUpdate) { loadRecipes(forceUpdate,true);}

    public void setFiltering(RecipesFilterType requestType) {
        mCurrentFiltering = requestType;

        // Depending on the filter type, set the filtering label, icon drawables, etc.
        switch (requestType) {
            case ALL_RECIPES:
                currentFilteringLabel.set(mContext.getString(R.string.label_all));
                noRecipesLabel.set(mContext.getResources().getString(R.string.no_recipes_all));
                recipesAddViewVisible.set(true);
                break;
            case COMPLETED_RECIPES:
                currentFilteringLabel.set(mContext.getString(R.string.label_all));
                noRecipesLabel.set(mContext.getResources().getString(R.string.no_recipes_all));
                recipesAddViewVisible.set(true);
                break;
        }
    }

    SingleLiveEvent<String> getOpenRecipeEvent() { return mOpenRecipeEvent; }

    SnackbarMessage getSnackbarMessage() {
        return mSnackbarText;
    }

    SingleLiveEvent<Void> getNewRecipeEvent() { return mNewRecipeEvent; }

    private void showSnackbarMessage(Integer message) {
        mSnackbarText.setValue(message);
    }

    /**
     * Called by the Data Binding library and the FAB's click listener.
     */
    public void addNewRecipe() {mNewRecipeEvent.call(); }

    void handleActivityResult(int requestCode, int resultCode) {
        if (AddEditRecipeActivity.REQUEST_CODE == requestCode) {
            switch (resultCode) {
                case RecipeDetailsActivity.EDIT_RESULT_OK:
                    mSnackbarText.setValue(R.string.recipe_saved_text);
                    break;
                case AddEditRecipeActivity.ADD_EDIT_RESULT_OK:
                    mSnackbarText.setValue(R.string.recipe_added_text);
                    break;
                case RecipeDetailsActivity.DELETE_RESULT_OK:
                    mSnackbarText.setValue(R.string.recipe_deleted_text);
                    break;
            }
        }
    }

    /**
     * @param forceUpdate   Pass in true to refresh the data in the {@link BrewBroDataSource}
     * @param showLoadingUI Pass in true to display a loading icon in the UI
     */
    private void loadRecipes(boolean forceUpdate, final boolean showLoadingUI) {
        if (showLoadingUI) {
            dataLoading.set(true);
        }
        if (forceUpdate) {
            mBrewBroRepository.refreshRecipes();
        }

        mBrewBroRepository.getRecipes(new BrewBroDataSource.LoadRecipesCallback() {
            @Override
            public void onRecipesLoaded(List<Recipe> recipes) {
                List<Recipe> recipesToShow = new ArrayList<>();

                //TODO Add any filtering in the following for
                for (Recipe recipe : recipes) {
                    switch (mCurrentFiltering) {
                        case ALL_RECIPES:
                            recipesToShow.add(recipe);
                            break;
                        case COMPLETED_RECIPES:
                            if (recipe.isCompleted()) {
                                recipesToShow.add(recipe);
                            }
                            break;
                        default:
                            recipesToShow.add(recipe);
                            break;
                    }
                }
                if (showLoadingUI) {
                    dataLoading.set(false);
                }
                mIsDataLoadingError.set(false);

                items.clear();
                items.addAll(recipesToShow);
                empty.set(items.isEmpty());
            }

            @Override
            public void onDataNotAvailable() {
                mIsDataLoadingError.set(true);
            }
        });
    }
}
