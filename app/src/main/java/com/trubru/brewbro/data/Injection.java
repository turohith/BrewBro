package com.trubru.brewbro.data;

import android.content.Context;
import android.support.annotation.NonNull;

import com.trubru.brewbro.data.local.BrewBroDatabase;
import com.trubru.brewbro.util.AppExecutors;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by Kolin on 1/29/2018.
 */

public class Injection {

    public static BrewBroRepository provideRecipeRepository(@NonNull Context context){
        checkNotNull(context);
        BrewBroDatabase database = BrewBroDatabase.getInstance(context);
        return BrewBroRepository.getInstance(FakeDataSource.getInstance(),
                BrewBroDataSource.getInstance(new AppExecutors(),
                        database.recipeDao()));
    }
}
