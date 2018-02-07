package com.trubru.brewbro.recipelist;

import android.databinding.BindingAdapter;
import android.widget.ListView;

import com.trubru.brewbro.data.Recipe;

import java.util.List;

public class RecipeListBindings {

    @SuppressWarnings("unchecked")
    @BindingAdapter("app:items")
    public static void setItems(ListView listView, List<Recipe> items) {
        RecipeListAdapter adapter = (RecipeListAdapter) listView.getAdapter();
        if (adapter != null){
            adapter.replaceData(items);
        }

    }

}
