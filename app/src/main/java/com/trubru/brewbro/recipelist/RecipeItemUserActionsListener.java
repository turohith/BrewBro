package com.trubru.brewbro.recipelist;

import android.view.View;

import com.trubru.brewbro.R;
import com.trubru.brewbro.data.Recipe;

/**
 * Created by Kolin on 2/3/2018.
 */

public interface RecipeItemUserActionsListener {
    void onRecipeClicked(Recipe recipe);
}
