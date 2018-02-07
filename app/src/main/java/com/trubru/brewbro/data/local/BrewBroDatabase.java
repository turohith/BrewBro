package com.trubru.brewbro.data.local;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

import com.trubru.brewbro.data.Recipe;

@Database(entities = {Recipe.class}, version = 1, exportSchema = false)
public abstract class BrewBroDatabase extends RoomDatabase {

    private static BrewBroDatabase INSTANCE;

    //Place All DAOs here
    public abstract RecipeDao recipeDao();

    private static final Object sLock = new Object();

    public static BrewBroDatabase getInstance(Context context) {
        synchronized (sLock) {
            if (INSTANCE == null) {
                INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                        BrewBroDatabase.class, "BrewBroDatabase.db")
                        .build();
                }
                return INSTANCE;
        }
    }
}
