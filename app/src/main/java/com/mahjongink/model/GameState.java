package com.mahjongink.model;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents the complete state of a game session.
 * Used for saving and restoring games.
 */
public class GameState {
    private final String layoutId;
    private final List<TileState> tileStates;
    private final int selectedTileId;
    private final long elapsedTimeMs;
    private final long startTimeMs;

    public GameState(String layoutId, List<TileState> tileStates, int selectedTileId,
                     long elapsedTimeMs, long startTimeMs) {
        this.layoutId = layoutId;
        this.tileStates = new ArrayList<>(tileStates);
        this.selectedTileId = selectedTileId;
        this.elapsedTimeMs = elapsedTimeMs;
        this.startTimeMs = startTimeMs;
    }

    public String getLayoutId() {
        return layoutId;
    }

    public List<TileState> getTileStates() {
        return new ArrayList<>(tileStates);
    }

    public int getSelectedTileId() {
        return selectedTileId;
    }

    public long getElapsedTimeMs() {
        return elapsedTimeMs;
    }

    public long getStartTimeMs() {
        return startTimeMs;
    }

    /**
     * Represents the state of a single tile for persistence.
     */
    public static class TileState {
        private final int id;
        private final String type;
        private final int x;
        private final int y;
        private final int z;
        private final boolean removed;

        public TileState(int id, String type, int x, int y, int z, boolean removed) {
            this.id = id;
            this.type = type;
            this.x = x;
            this.y = y;
            this.z = z;
            this.removed = removed;
        }

        public int getId() {
            return id;
        }

        public String getType() {
            return type;
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

        public boolean isRemoved() {
            return removed;
        }
    }

    @NonNull
    @Override
    public String toString() {
        return "GameState{layout=" + layoutId + ", tiles=" + tileStates.size() + "}";
    }
}
