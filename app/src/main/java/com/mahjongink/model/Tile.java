package com.mahjongink.model;

import androidx.annotation.NonNull;

/**
 * Represents a single Mahjong tile on the board.
 * Each tile has a type and a position in 3D space (x, y, z).
 */
public class Tile {
    private final int id;
    private final TileType type;
    private Position position;
    private boolean selected;
    private boolean removed;

    public Tile(int id, TileType type, Position position) {
        this.id = id;
        this.type = type;
        this.position = position;
        this.selected = false;
        this.removed = false;
    }

    public int getId() {
        return id;
    }

    public TileType getType() {
        return type;
    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public boolean isRemoved() {
        return removed;
    }

    public void setRemoved(boolean removed) {
        this.removed = removed;
    }

    /**
     * Checks if this tile can be matched with another tile.
     */
    public boolean canMatchWith(Tile other) {
        if (other == null || other.removed || this.removed) return false;
        return TileType.canMatch(this.type, other.type);
    }

    @NonNull
    @Override
    public String toString() {
        return "Tile{" + type + " pos=" + position + "}";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Tile tile = (Tile) o;
        return id == tile.id;
    }

    @Override
    public int hashCode() {
        return id;
    }
}
