package com.mahjongink.model;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Represents a board layout configuration.
 * Contains the template positions where tiles should be placed.
 */
public class Layout {
    private final String id;
    private final String name;
    private final String description;
    private final int difficulty; // 1-10 scale
    private final List<Position> positions;

    public Layout(String id, String name, String description, int difficulty, List<Position> positions) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.difficulty = difficulty;
        this.positions = Collections.unmodifiableList(new ArrayList<>(positions));
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public int getDifficulty() {
        return difficulty;
    }

    public List<Position> getPositions() {
        return positions;
    }

    public int getTileCount() {
        return positions.size();
    }

    /**
     * Validates that this layout has a valid number of tiles (must be even for pairing).
     */
    public boolean isValid() {
        return positions.size() % 2 == 0 && positions.size() > 0;
    }

    @NonNull
    @Override
    public String toString() {
        return "Layout{" + name + " (" + positions.size() + " tiles)}";
    }
}
