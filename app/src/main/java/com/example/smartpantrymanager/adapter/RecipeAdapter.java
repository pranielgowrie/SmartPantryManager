package com.example.smartpantrymanager.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartpantrymanager.R;
import com.example.smartpantrymanager.model.Recipe;

import java.util.ArrayList;
import java.util.List;

// Displays recipes and sends selections to the Activity.
public class RecipeAdapter
        extends RecyclerView.Adapter<RecipeAdapter.RecipeHolder> {

    // Handles recipe selections.
    public interface RecipeListener {

        void onRecipeSelected(Recipe recipe);
    }

    // Recipes currently displayed in the list.
    private final List<Recipe> recipes;

    // Sends the selected recipe back to the Activity.
    private final RecipeListener listener;

    public RecipeAdapter(RecipeListener listener) {
        recipes = new ArrayList<>();
        this.listener = listener;
    }

    // Replaces the current list with new recipe results.
    public void setRecipes(List<Recipe> newRecipes) {
        recipes.clear();

        if (newRecipes != null) {
            recipes.addAll(newRecipes);
        }

        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecipeHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType) {

        // Creates one recipe row from the XML layout.
        View view = LayoutInflater.from(
                parent.getContext()
        ).inflate(
                R.layout.item_recipe,
                parent,
                false
        );

        return new RecipeHolder(view);
    }

    @Override
    public void onBindViewHolder(
            @NonNull RecipeHolder holder,
            int position) {

        Recipe recipe = recipes.get(position);
        holder.bind(recipe, listener);
    }

    @Override
    public int getItemCount() {
        return recipes.size();
    }

    // Holds the controls for one recipe row.
    static class RecipeHolder
            extends RecyclerView.ViewHolder {

        private final TextView nameText;
        private final TextView countText;

        RecipeHolder(@NonNull View itemView) {
            super(itemView);

            // Connects controls to the recipe row layout.
            nameText = itemView.findViewById(
                    R.id.txtRecipeName
            );

            countText = itemView.findViewById(
                    R.id.txtIngredientCount
            );
        }

        // Displays one recipe and connects its selection action.
        void bind(
                final Recipe recipe,
                final RecipeListener listener) {

            nameText.setText(recipe.getName());

            String countValue =
                    recipe.getIngredientCount()
                            + " ingredients";

            countText.setText(countValue);

            // Sends the selected recipe to the Activity.
            itemView.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (listener != null) {
                                listener.onRecipeSelected(
                                        recipe
                                );
                            }
                        }
                    }
            );
        }
    }
}