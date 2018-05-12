package com.example.baking.ui;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

import com.example.baking.R;
import com.example.baking.data.RecipesViewModel;
import com.example.baking.data.database.entity.Ingredient;
import com.example.baking.data.database.entity.Step;
import com.example.baking.ui.adapters.RecipeStepClickListener;
import com.example.baking.ui.fragments.RecipeDetailFragment;

import java.util.ArrayList;
import java.util.List;

public class RecipeDetailActivity extends AppCompatActivity implements RecipeStepClickListener {
    private int mStepCount;
    private String mRecipeName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);

        String recipeName = getIntent().getStringExtra("name");
        if (recipeName != null) {
            mRecipeName = recipeName;
            getSupportActionBar().setTitle(recipeName);
        }

        final RecipeDetailFragment recipeDetailFragment = new RecipeDetailFragment();
        final FragmentManager fragmentManager = getSupportFragmentManager();
        final int id = getIntent().getIntExtra("id", 1);

        final RecipesViewModel viewModel = ViewModelProviders.of(this).get(RecipesViewModel.class);
        viewModel.getSteps(id).observe(this, new Observer<List<Step>>() {
            @Override
            public void onChanged(@Nullable final List<Step> steps) {
                mStepCount = steps.size();
                viewModel.getIngredients(id).observe(RecipeDetailActivity.this, new Observer<List<Ingredient>>() {
                    @Override
                    public void onChanged(@Nullable List<Ingredient> ingredients) {
                        Bundle bundle = new Bundle();
                        bundle.putParcelableArrayList("steps", (ArrayList<Step>) steps);
                        bundle.putParcelableArrayList("ingredients", (ArrayList<Ingredient>) ingredients);
                        recipeDetailFragment.setArguments(bundle);
                        fragmentManager.beginTransaction().replace(R.id.recipe_steps_container, recipeDetailFragment).commit();
                    }
                });
            }
        });

    }
    @Override
    public void onRecipeStepClick(int databaseGeneratedStepId) {
        Intent intent = new Intent(RecipeDetailActivity.this, RecipeStepDetailsActivity.class);
        intent.putExtra("id", databaseGeneratedStepId);
        intent.putExtra("name", mRecipeName);
        intent.putExtra("stepCount", mStepCount);
        startActivity(intent);
    }
}