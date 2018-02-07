package com.trubru.brewbro.recipelist;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.trubru.brewbro.R;
import com.trubru.brewbro.ViewModelFactory;
import com.trubru.brewbro.addeditrecipe.AddEditRecipeActivity;
import com.trubru.brewbro.recipedetails.RecipeDetailsActivity;
import com.trubru.brewbro.util.ActivityUtils;

/**
 * Created by Kolin on 2/3/2018.
 */

public class RecipeListActivity extends AppCompatActivity implements RecipeItemNavigator, RecipesNavigator{
    private DrawerLayout mDrawerLayout;
    private RecipeListViewModel mRecipeListViewModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recipe_list_activity);

        setupToolbar();
        setupNavigationDrawer();
        setupViewFragment();

        mRecipeListViewModel = obtainViewModel(this);

        mRecipeListViewModel.getOpenRecipeEvent().observe(this, (recipeId) -> {
            if (recipeId != null) {
                openRecipeDetails(recipeId);
            }
        });

        mRecipeListViewModel.getNewRecipeEvent().observe(this, new Observer<Void>() {
            @Override
            public void onChanged(@Nullable Void _) {
                addNewRecipe();
            }
        });
    }

    public static RecipeListViewModel obtainViewModel(FragmentActivity activity) {
        ViewModelFactory factory = ViewModelFactory.getInstance(activity.getApplication());

        RecipeListViewModel viewModel =
                ViewModelProviders.of(activity,factory).get(RecipeListViewModel.class);

        return viewModel;
    }

    private void setupViewFragment() {
        RecipeListFragment recipeListFragment = (RecipeListFragment)
                getSupportFragmentManager().findFragmentById(R.id.contentFrame);
        if (recipeListFragment == null) {
            recipeListFragment = RecipeListFragment.newInstance();
            ActivityUtils.replaceFragmentInActivity(
                    getSupportFragmentManager(),recipeListFragment,R.id.contentFrame);
        }
    }

    private void setupToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        ab.setHomeAsUpIndicator(R.drawable.ic_menu);
        ab.setDisplayHomeAsUpEnabled(true);
    }

    private void setupNavigationDrawer() {
        mDrawerLayout = findViewById(R.id.drawer_layout);
        mDrawerLayout.setStatusBarBackground(R.color.colorPrimaryDark);
        NavigationView navigationView = findViewById(R.id.nav_view);
        if (navigationView != null) {
            setupDrawerContent(navigationView);
        }
    }

    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                (menuItem) -> {
                        switch (menuItem.getItemId()) {
                            case R.id.nav_drawer_recipes:
                                // Do nothing, we're already on that screen
                                break;
                            case R.id.nav_drawer_home:
                                break;
                            default:
                                break;
                        }
                        // Close the navigation drawer when an item is selected.
                        menuItem.setChecked(true);
                        mDrawerLayout.closeDrawers();
                        return true;
                });
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // Open the navigation drawer when the home icon is selected from the toolbar.
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        mRecipeListViewModel.handleActivityResult(requestCode, resultCode);
    }

    @Override
    public void addNewRecipe() {
        Intent intent = new Intent(this, AddEditRecipeActivity.class);
        startActivityForResult(intent,AddEditRecipeActivity.REQUEST_CODE);
    }

    @Override
    public void openRecipeDetails(String recipeId) {
        Intent intent = new Intent(this, RecipeDetailsActivity.class);
        intent.putExtra(RecipeDetailsActivity.EXTRA_RECIPE_ID, recipeId);
        startActivityForResult(intent, AddEditRecipeActivity.REQUEST_CODE);

    }





































}
