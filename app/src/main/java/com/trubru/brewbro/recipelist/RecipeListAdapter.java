package com.trubru.brewbro.recipelist;

import android.databinding.DataBindingUtil;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.trubru.brewbro.data.Recipe;
import com.trubru.brewbro.databinding.RecipeListItemBinding;

import java.util.List;

public class RecipeListAdapter extends BaseAdapter {

    private final RecipeListViewModel mRecipeListViewModel;

    private List<Recipe> mRecipes;

    public RecipeListAdapter(List<Recipe> recipes,
                             RecipeListViewModel recipeListViewModel) {
        mRecipeListViewModel = recipeListViewModel;
        setList(recipes);
    }

    public void replaceData(List<Recipe> recipes) { setList(recipes); }

    @Override
    public int getCount() {
        return mRecipes != null ? mRecipes.size() : 0;
    }

    @Override
    public Object getItem(int position) {
        return mRecipes.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, final View view, final ViewGroup viewGroup) {
        RecipeListItemBinding binding;
        if (view == null) {
            //Inflate
            LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());

            //create the binding
            binding = RecipeListItemBinding.inflate(inflater, viewGroup, false);
        } else {
            //recycle view
            binding = DataBindingUtil.getBinding(view);
        }

        RecipeItemUserActionsListener userActionsListener = new RecipeItemUserActionsListener() {
            @Override
            public void onRecipeClicked(Recipe recipe) {
                mRecipeListViewModel.getOpenRecipeEvent().setValue(recipe.getId());
            }
        };

        binding.setRecipe(mRecipes.get(position));

        binding.setListener(userActionsListener);

        binding.executePendingBindings();
        return binding.getRoot();
    }


    public void setList(List<Recipe> recipes) {
        mRecipes = recipes;
        notifyDataSetChanged();
    }

}
