package com.trubru.brewbro.recipedetails;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;

import static com.trubru.brewbro.addeditrecipe.AddEditRecipeActivity.ADD_EDIT_RESULT_OK;

/**
 * Created by Kolin on 2/4/2018.
 */

public class RecipeDetailsActivity extends AppCompatActivity implements RecipeDetailsNavigator {
    public static final String EXTRA_RECIPE_ID = "RECIPE_ID";

    public static final int DELETE_RESULT_OK = RESULT_FIRST_USER + 2;

    public static final int EDIT_RESULT_OK = RESULT_FIRST_USER + 3;

    private RecipeDetailsViewModel mRecipeDetailsViewModel;





    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_EDIT_RECIPE) {
            // If the task was edited successfully, go back to the list.
            if (resultCode == ADD_EDIT_RESULT_OK) {
                // If the result comes from the add/edit screen, it's an edit.
                setResult(EDIT_RESULT_OK);
                finish();
            }
        }
    }


}
