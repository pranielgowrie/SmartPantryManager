package com.example.smartpantrymanager.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartpantrymanager.R;
import com.example.smartpantrymanager.model.PantryItem;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

// Displays pantry items and forwards row actions to the Activity.
public class PantryAdapter
        extends RecyclerView.Adapter<PantryAdapter.ItemViewHolder> {

    // Defines the actions available for each pantry item.
    public interface ItemListener {

        void onEdit(PantryItem item);

        void onDelete(PantryItem item);
    }

    // Items currently displayed by the RecyclerView.
    private final List<PantryItem> items;

    // Sends row actions back to the Activity.
    private final ItemListener listener;

    public PantryAdapter(ItemListener listener) {
        items = new ArrayList<>();
        this.listener = listener;
    }

    // Replaces the displayed items with current database records.
    public void setItems(List<PantryItem> newItems) {
        items.clear();

        if (newItems != null) {
            items.addAll(newItems);
        }

        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType) {

        // Creates one pantry row from the XML layout.
        View view = LayoutInflater
                .from(parent.getContext())
                .inflate(
                        R.layout.item_pantry,
                        parent,
                        false
                );

        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(
            @NonNull ItemViewHolder holder,
            int position) {

        // Displays the item at the current list position.
        PantryItem item = items.get(position);
        holder.bind(item, listener);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    // Holds the controls used by one pantry row.
    public static final class ItemViewHolder
            extends RecyclerView.ViewHolder {

        private final TextView nameText;
        private final TextView quantityText;
        private final TextView expiryText;
        private final Button editButton;
        private final Button deleteButton;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);

            // Connects Java controls to the row layout.
            nameText = itemView.findViewById(
                    R.id.txtItemName
            );

            quantityText = itemView.findViewById(
                    R.id.txtItemQuantity
            );

            expiryText = itemView.findViewById(
                    R.id.txtItemExpiry
            );

            editButton = itemView.findViewById(
                    R.id.btnEdit
            );

            deleteButton = itemView.findViewById(
                    R.id.btnDelete
            );
        }

        // Displays one pantry item and connects its buttons.
        private void bind(
                final PantryItem item,
                final ItemListener listener) {

            nameText.setText(item.getName());
            quantityText.setText(formatQuantity(item));
            displayExpiry(item);

            // Sends the selected item to the edit action.
            editButton.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (listener != null) {
                                listener.onEdit(item);
                            }
                        }
                    }
            );

            // Sends the selected item to the delete action.
            deleteButton.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (listener != null) {
                                listener.onDelete(item);
                            }
                        }
                    }
            );
        }

        // Shows the expiry date only when one is available.
        private void displayExpiry(PantryItem item) {
            if (!item.hasExpiry()) {
                expiryText.setText("");
                expiryText.setVisibility(View.GONE);
                return;
            }

            String expiryValue =
                    itemView.getContext().getString(
                            R.string.expiry_value,
                            item.getExpiry()
                    );

            expiryText.setText(expiryValue);
            expiryText.setVisibility(View.VISIBLE);
        }

        // Formats whole and decimal quantities cleanly.
        private String formatQuantity(PantryItem item) {
            double quantity = item.getQuantity();

            boolean wholeNumber =
                    Double.compare(
                            quantity,
                            Math.rint(quantity)
                    ) == 0;

            if (wholeNumber) {
                return String.format(
                        Locale.getDefault(),
                        "%.0f %s",
                        quantity,
                        item.getUnit()
                );
            }

            return String.format(
                    Locale.getDefault(),
                    "%.2f %s",
                    quantity,
                    item.getUnit()
            );
        }
    }
}