package com.mahjongink.model;

import androidx.annotation.NonNull;

/**
 * Represents a 3D position on the board.
 * x: horizontal position (left to right)
 * y: vertical position (top to bottom)
 * z: layer/depth (0 = bottom, higher = on top)
 */
public class Position {
    private final int x;
    private final int y;
    private final int z;

    public Position(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getZ() {
        return z;
    }

    /**
     * Returns the screen X coordinate based on tile dimensions.
     */
    public float getScreenX(float tileWidth, float tileSpacing) {
        return x * (tileWidth + tileSpacing);
    }

    /**
     * Returns the screen Y coordinate based on tile dimensions.
     * Higher Z layers are offset slightly to create depth effect.
     */
    public float getScreenY(float tileHeight, float tileSpacing, float layerOffset) {
        return y * (tileHeight * 0.75f + tileSpacing) - z * layerOffset;
    }

    @NonNull
    @Override
    public String toString() {
        return "(" + x + "," + y + "," + z + ")";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Position position = (Position) o;
        return x == position.x && y == position.y && z == position.z;
    }

    @Override
    public int hashCode() {
        int result = x;
        result = 31 * result + y;
        result = 31 * result + z;
        return result;
    }
}
