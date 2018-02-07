package com.trubru.brewbro.recipelist;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.PopupMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.trubru.brewbro.R;
import com.trubru.brewbro.ScrollChildSwipeRefreshLayout;
import com.trubru.brewbro.SnackbarMessage;
import com.trubru.brewbro.data.Recipe;
import com.trubru.brewbro.databinding.RecipeListFragmentBinding;
import com.trubru.brewbro.util.SnackbarUtils;

import java.util.ArrayList;

public class RecipeListFragment extends Fragment {

    private RecipeListViewModel mRecipeListViewModel;

    private RecipeListFragmentBinding mRecipeListFragmentBinding;

    private RecipeListAdapter mRecipeListAdapter;

    public RecipeListFragment(){} //fragments require empty constructor

    public static RecipeListFragment newInstance() { return new RecipeListFragment(); }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {

        mRecipeListFragmentBinding = RecipeListFragmentBinding.inflate(inflater, container,false);

        mRecipeListViewModel = RecipeListActivity.obtainViewModel(getActivity());

        mRecipeListFragmentBinding.setViewmodel(mRecipeListViewModel);

        setHasOptionsMenu(true);

        return mRecipeListFragmentBinding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setupSnackbar();

        setupFab();

        setupListAdapter();

        setupRefreshLayout();
    }

    private void setupRefreshLayout() {
        ListView listView = mRecipeListFragmentBinding.recipeList;
        final ScrollChildSwipeRefreshLayout swipeRefreshLayout = mRecipeListFragmentBinding.refreshLayout;
        swipeRefreshLayout.setColorSchemeColors(
                ContextCompat.getColor(getActivity(), R.color.colorPrimary),
                ContextCompat.getColor(getActivity(), R.color.colorAccent),
                ContextCompat.getColor(getActivity(), R.color.colorPrimaryDark)
        );

        swipeRefreshLayout.setScrollUpChild(listView);
    }

    /**
     * TODO Add and pop up methods for filtering or sorting of recipes
     */

    @Override
    public void onResume() {
        super.onResume();
        mRecipeListViewModel.start();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.recipe_list_actions_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.recipe_add_navigation_menu_item:
                mRecipeListViewModel.addNewRecipe();
                break;
            //TODO add other cases for menu option times

        }
        return true;
    }

    private void setupListAdapter() {
        ListView listView = mRecipeListFragmentBinding.recipeList;

        mRecipeListAdapter = new RecipeListAdapter(
                new ArrayList<Recipe>(0),
                mRecipeListViewModel
        );
        listView.setAdapter(mRecipeListAdapter);
    }

    private void setupFab() {
        FloatingActionButton fab = getActivity().findViewById(R.id.fab_add_recipe);

        fab.setImageResource(R.drawable.ic_add);
        fab.setOnClickListener((v) -> { mRecipeListViewModel.addNewRecipe(); } );
    }

    private void setupSnackbar() {
        mRecipeListViewModel.getSnackbarMessage().observe(this, new SnackbarMessage.SnackbarObserver() {
            @Override
            public void onNewMessage(@StringRes int snackbarMessageResourceId) {
                SnackbarUtils.showSnackbar(getView(), getString(snackbarMessageResourceId));
            }
        });
    }



}
