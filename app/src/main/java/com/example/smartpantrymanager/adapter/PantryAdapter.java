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

// Displays pantry items and handles row actions.
public class PantryAdapter
        extends RecyclerView.Adapter<PantryAdapter.ItemViewHolder> {

    // Pantry row actions
    public interface ItemListener {

        void onEdit(PantryItem item);

        void onDelete(PantryItem item);
    }

    // Displayed pantry items
    private final List<PantryItem> items = new ArrayList<>();

    // Row action listener
    private final ItemListener listener;

    public PantryAdapter(ItemListener listener) {
        this.listener = listener;
    }

    // Updates the displayed items.
    public void setItems(List<PantryItem> newItems) {
        int previousSize = items.size();

        items.clear();

        if (previousSize > 0) {
            notifyItemRangeRemoved(0, previousSize);
        }

        if (newItems != null && !newItems.isEmpty()) {
            items.addAll(newItems);
            notifyItemRangeInserted(0, items.size());
        }
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType) {

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

        holder.bind(
                items.get(position),
                listener
        );
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    // Holds one pantry row.
    public static final class ItemViewHolder
            extends RecyclerView.ViewHolder {

        // Row controls
        private final TextView nameText;
        private final TextView quantityText;
        private final TextView expiryText;
        private final Button editButton;
        private final Button deleteButton;

        public ItemViewHolder(
                @NonNull View itemView) {

            super(itemView);

            nameText =
                    itemView.findViewById(
                            R.id.txtItemName
                    );

            quantityText =
                    itemView.findViewById(
                            R.id.txtItemQuantity
                    );

            expiryText =
                    itemView.findViewById(
                            R.id.txtItemExpiry
                    );

            editButton =
                    itemView.findViewById(
                            R.id.btnEdit
                    );

            deleteButton =
                    itemView.findViewById(
                            R.id.btnDelete
                    );
        }

        // Displays one pantry item.
        private void bind(
                PantryItem item,
                ItemListener listener) {

            nameText.setText(item.getName());

            quantityText.setText(
                    formatQuantity(item)
            );

            displayExpiry(item);

            editButton.setOnClickListener(view -> {
                if (listener != null) {
                    listener.onEdit(item);
                }
            });

            deleteButton.setOnClickListener(view -> {
                if (listener != null) {
                    listener.onDelete(item);
                }
            });
        }

        // Displays the optional expiry date.
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

        // Formats the quantity and unit.
        private String formatQuantity(PantryItem item) {
            double quantity = item.getQuantity();

            boolean wholeNumber =
                    Double.compare(
                            quantity,
                            Math.rint(quantity)
                    ) == 0;

            String format = wholeNumber
                    ? "%.0f %s"
                    : "%.2f %s";

            return String.format(
                    Locale.getDefault(),
                    format,
                    quantity,
                    item.getUnit()
            );
        }
    }
}