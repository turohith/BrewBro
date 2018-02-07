package com.trubru.brewbro.data;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.common.base.Objects;
import com.google.common.base.Strings;

import java.util.UUID;

/**
 * Model Class for Recipes
 * This is what a recipe is, it's name, ingredients, day it was created, etc...
 */
@Entity(tableName = "recipes")
public final class Recipe {


    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "entryid")
    private final String rId;

    @Nullable
    @ColumnInfo(name = "name")
    private final String rName;

    @Nullable
    @ColumnInfo(name = "description")
    private final String rDescription;

    @ColumnInfo(name = "completed")
    private final boolean rCompleted;


    /**
     * Use this constructor to create a new active Recipe.
     *
     * @param name       name of the recipe
     * @param description description of the recipe
     */
    @Ignore
    public Recipe(@Nullable String name, @Nullable String description) {
        this(name, description, UUID.randomUUID().toString(), false);
    }


    /**
     * Use this constructor to create an active Recipe if the Recipe already has an id (copy of another
     * Recipe).
     *
     * @param name       name of the recipe
     * @param description description of the recipe
     * @param id          id of the recipe
     */
    @Ignore
    public Recipe(@Nullable String name, @Nullable String description, @NonNull String id) {
        this(name, description, id, false);
    }

    /**
     * Use this constructor to create a new rCompleted Recipe.
     *
     * @param name       name of the recipe
     * @param description description of the recipe
     * @param completed   true if the recipe is rCompleted, false if it's active
     */
    @Ignore
    public Recipe(@Nullable String name, @Nullable String description, boolean completed) {
        this(name, description, UUID.randomUUID().toString(), completed);
    }

    /**
     * Use this constructor to specify a rCompleted Task if the Task already has an id (copy of
     * another Task).
     *
     * @param name       name of the recipe
     * @param description description of the recipe
     * @param completed   true if the recipe is rCompleted, false if it's active
     * @param id          id of the recipe
     */
    public Recipe(@Nullable String name, @Nullable String description,
                @NonNull String id, boolean completed) {
        rId = id;
        rName = name;
        rDescription = description;
        rCompleted = completed;
    }

    @NonNull
    public String getrId() {
        return rId;
    }

    @Nullable
    public String getrName() {
        return rName;
    }

    @Nullable
    public String getrDescription() {
        return rDescription;
    }

    public boolean isrCompleted() {
        return rCompleted;
    }

    /**
     * PLACE ALL GETTERS AND SETTERS HERe
     */



    public boolean isCompleted() {
        return rCompleted;
    }

    public boolean isIncomplete() { return !rCompleted; }

    @NonNull
    public String getId() {
        return rId;
    }

    @Nullable
    public String getName() {
        return rName;
    }

    @Nullable
    public String getDescription() {
        return rDescription;
    }

    /**
     * TODO Write this:
     * Use the isIncomplete to check if crucial elements of a Recipe are nonexistent
    public boolean isIncomplete() {
        return Strings.isNullOrEmpty(rName) &&
                Strings.isNullOrEmpty(rDescription);
    }
     */

    //Use the isEmpty method to see if a recipe doesn't exist

    public boolean isEmpty() {
        return Strings.isNullOrEmpty(rName) &&
                Strings.isNullOrEmpty(rDescription);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Recipe recipe = (Recipe) o;
        return Objects.equal(rId, recipe.rId) &&
                Objects.equal(rName, recipe.rName) &&
                Objects.equal(rDescription, recipe.rDescription);
    }

    @Nullable
    public String getNameForList() {
        if (!Strings.isNullOrEmpty(rName)) {
            return rName;
        } else {
            return rDescription;
        }
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(rId, rName, rDescription);
    }


}
