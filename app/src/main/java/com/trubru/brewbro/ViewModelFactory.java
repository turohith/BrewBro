package com.trubru.brewbro;

import android.annotation.SuppressLint;
import android.app.Application;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;

import com.trubru.brewbro.data.BrewBroRepository;
import com.trubru.brewbro.data.Injection;
import com.trubru.brewbro.recipelist.RecipeListViewModel;

/**
 * Created by Kolin on 2/3/2018.
 */

public class ViewModelFactory extends ViewModelProvider.NewInstanceFactory {
    @SuppressLint("StaticFieldLeak")
    private static volatile ViewModelFactory INSTANCE;

    private final Application mApplication;

    private final BrewBroRepository mBrewBroRepository;

    public static ViewModelFactory getInstance(Application application) {
        if (INSTANCE == null) {
            synchronized (ViewModelFactory.class) {
                if (INSTANCE == null){
                    INSTANCE = new ViewModelFactory(application,
                            Injection.provideRecipeRepository(application.getApplicationContext()));
                }
            }
        }
        return INSTANCE;
    }

    @VisibleForTesting
    public static void destroyInstance() { INSTANCE = null; }

    private ViewModelFactory(Application application, BrewBroRepository brewBroRepository) {
        mApplication =application;
        mBrewBroRepository = brewBroRepository;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(RecipeListViewModel.class)) {
            return (T) new RecipeListViewModel(mApplication, mBrewBroRepository);
        }
        throw new IllegalArgumentException("Unknown ViewModel Class: " + modelClass.getName());
    }
}
