package com.mahjongink;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mahjongink.logic.LayoutCatalog;
import com.mahjongink.model.Layout;

import java.util.List;

/**
 * Activity for selecting game layouts.
 * Displays layouts in a grid with thumbnails and descriptions.
 */
public class LayoutSelectionActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private LayoutAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_layout_selection);

        // Enable back button
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Select Layout");
        }

        recyclerView = findViewById(R.id.layout_recycler_view);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 3));

        List<Layout> layouts = LayoutCatalog.getAllLayouts();
        adapter = new LayoutAdapter(layouts, this::onLayoutSelected);
        recyclerView.setAdapter(adapter);
    }

    private void onLayoutSelected(Layout layout) {
        // Save selected layout
        SharedPreferences prefs = getSharedPreferences("mahjong_ink", MODE_PRIVATE);
        prefs.edit()
                .putString("selected_layout_id", layout.getId())
                .putString("selected_layout_mode", "fixed")
                .apply();

        finish();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    /**
     * RecyclerView adapter for layout grid.
     */
    private static class LayoutAdapter extends RecyclerView.Adapter<LayoutViewHolder> {

        private final List<Layout> layouts;
        private final OnLayoutClickListener listener;

        interface OnLayoutClickListener {
            void onLayoutClick(Layout layout);
        }

        LayoutAdapter(List<Layout> layouts, OnLayoutClickListener listener) {
            this.layouts = layouts;
            this.listener = listener;
        }

        @NonNull
        @Override
        public LayoutViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_layout, parent, false);
            return new LayoutViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull LayoutViewHolder holder, int position) {
            Layout layout = layouts.get(position);
            holder.bind(layout, listener);
        }

        @Override
        public int getItemCount() {
            return layouts.size();
        }
    }

    /**
     * ViewHolder for layout grid items.
     */
    private static class LayoutViewHolder extends RecyclerView.ViewHolder {

        private final View thumbnailView;
        private final TextView nameText;
        private final TextView infoText;

        LayoutViewHolder(@NonNull View itemView) {
            super(itemView);
            thumbnailView = itemView.findViewById(R.id.layout_thumbnail);
            nameText = itemView.findViewById(R.id.layout_name);
            infoText = itemView.findViewById(R.id.layout_info);
        }

        void bind(Layout layout, LayoutAdapter.OnLayoutClickListener listener) {
            nameText.setText(layout.getName());
            infoText.setText(layout.getTileCount() + " tiles | Diff: " + layout.getDifficulty() + "/10");

            // Draw simple thumbnail representation
            thumbnailView.post(() -> drawThumbnail(layout));

            itemView.setOnClickListener(v -> listener.onLayoutClick(layout));
        }

        private void drawThumbnail(Layout layout) {
            // Simple visual representation - just set background based on difficulty
            int difficulty = layout.getDifficulty();
            int shade = 255 - (difficulty * 20);
            thumbnailView.setBackgroundColor(android.graphics.Color.rgb(shade, shade, shade));
        }
    }
}
