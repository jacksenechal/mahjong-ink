package com.mahjongink.model;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Represents the current state of the game board.
 * Contains all tiles and manages their positions and states.
 */
public class Board {
    private final String layoutId;
    private final List<Tile> tiles;
    private final Map<Position, Tile> positionMap;
    private Tile selectedTile;

    public Board(String layoutId, List<Tile> tiles) {
        this.layoutId = layoutId;
        this.tiles = new ArrayList<>(tiles);
        this.positionMap = new HashMap<>();
        this.selectedTile = null;

        // Build position lookup map
        for (Tile tile : tiles) {
            positionMap.put(tile.getPosition(), tile);
        }
    }

    public String getLayoutId() {
        return layoutId;
    }

    public List<Tile> getTiles() {
        return Collections.unmodifiableList(tiles);
    }

    public Tile getTileAt(Position position) {
        return positionMap.get(position);
    }

    public Tile getSelectedTile() {
        return selectedTile;
    }

    public void setSelectedTile(Tile tile) {
        // Clear previous selection
        if (selectedTile != null) {
            selectedTile.setSelected(false);
        }

        selectedTile = tile;

        if (selectedTile != null) {
            selectedTile.setSelected(true);
        }
    }

    /**
     * Checks if a tile is currently free (can be selected and removed).
     * A tile is free if:
     * 1. It has no tiles on top of it (higher Z)
     * 2. At least one side (left or right) is open
     */
    public boolean isTileFree(Tile tile) {
        if (tile == null || tile.isRemoved()) return false;

        Position pos = tile.getPosition();

        // Check if any tile is on top of this one
        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                Position above = new Position(pos.getX() + dx, pos.getY() + dy, pos.getZ() + 1);
                Tile aboveTile = positionMap.get(above);
                if (aboveTile != null && !aboveTile.isRemoved()) {
                    return false;
                }
            }
        }

        // Check if left side is blocked
        boolean leftBlocked = false;
        boolean rightBlocked = false;

        for (int dy = -1; dy <= 1; dy++) {
            Position left = new Position(pos.getX() - 1, pos.getY() + dy, pos.getZ());
            Tile leftTile = positionMap.get(left);
            if (leftTile != null && !leftTile.isRemoved()) {
                leftBlocked = true;
                break;
            }
        }

        for (int dy = -1; dy <= 1; dy++) {
            Position right = new Position(pos.getX() + 1, pos.getY() + dy, pos.getZ());
            Tile rightTile = positionMap.get(right);
            if (rightTile != null && !rightTile.isRemoved()) {
                rightBlocked = true;
                break;
            }
        }

        // Tile is free if at least one side is open
        return !leftBlocked || !rightBlocked;
    }

    /**
     * Gets all tiles that are currently free to be selected.
     */
    public List<Tile> getFreeTiles() {
        List<Tile> freeTiles = new ArrayList<>();
        for (Tile tile : tiles) {
            if (!tile.isRemoved() && isTileFree(tile)) {
                freeTiles.add(tile);
            }
        }
        return freeTiles;
    }

    /**
     * Attempts to remove a pair of matching tiles.
     * Returns true if the pair was successfully removed.
     */
    public boolean removePair(Tile tile1, Tile tile2) {
        if (tile1 == null || tile2 == null) return false;
        if (tile1.getId() == tile2.getId()) return false; // Same tile
        if (!tile1.canMatchWith(tile2)) return false;
        if (!isTileFree(tile1) || !isTileFree(tile2)) return false;

        tile1.setRemoved(true);
        tile2.setRemoved(true);

        // Clear selection if one of the removed tiles was selected
        if (selectedTile != null &&
                (selectedTile.getId() == tile1.getId() || selectedTile.getId() == tile2.getId())) {
            selectedTile = null;
        }

        return true;
    }

    /**
     * Checks if the game is won (all tiles removed).
     */
    public boolean isGameWon() {
        for (Tile tile : tiles) {
            if (!tile.isRemoved()) return false;
        }
        return true;
    }

    /**
     * Checks if the game is stuck (no more valid moves).
     */
    public boolean isGameStuck() {
        List<Tile> freeTiles = getFreeTiles();

        // Group free tiles by type
        Map<TileType, List<Tile>> typeGroups = new HashMap<>();
        for (Tile tile : freeTiles) {
            TileType type = tile.getType();
            if (!typeGroups.containsKey(type)) {
                typeGroups.put(type, new ArrayList<>());
            }
            typeGroups.get(type).add(tile);
        }

        // Check for any valid pairs
        Set<TileType> checkedTypes = new HashSet<>();
        for (Tile tile : freeTiles) {
            TileType type = tile.getType();
            if (checkedTypes.contains(type)) continue;
            checkedTypes.add(type);

            List<Tile> sameType = typeGroups.get(type);
            if (sameType != null && sameType.size() >= 2) return false;

            // Check flowers and seasons
            if (TileType.canMatch(type, TileType.FLOWER_PLUM) &&
                    (typeGroups.containsKey(TileType.FLOWER_PLUM) ||
                            typeGroups.containsKey(TileType.FLOWER_ORCHID) ||
                            typeGroups.containsKey(TileType.FLOWER_CHRYSANTHEMUM) ||
                            typeGroups.containsKey(TileType.FLOWER_BAMBOO))) {
                int flowerCount = 0;
                if (typeGroups.containsKey(TileType.FLOWER_PLUM)) flowerCount += typeGroups.get(TileType.FLOWER_PLUM).size();
                if (typeGroups.containsKey(TileType.FLOWER_ORCHID)) flowerCount += typeGroups.get(TileType.FLOWER_ORCHID).size();
                if (typeGroups.containsKey(TileType.FLOWER_CHRYSANTHEMUM)) flowerCount += typeGroups.get(TileType.FLOWER_CHRYSANTHEMUM).size();
                if (typeGroups.containsKey(TileType.FLOWER_BAMBOO)) flowerCount += typeGroups.get(TileType.FLOWER_BAMBOO).size();
                if (flowerCount >= 2) return false;
            }

            if (TileType.canMatch(type, TileType.SEASON_SPRING) &&
                    (typeGroups.containsKey(TileType.SEASON_SPRING) ||
                            typeGroups.containsKey(TileType.SEASON_SUMMER) ||
                            typeGroups.containsKey(TileType.SEASON_AUTUMN) ||
                            typeGroups.containsKey(TileType.SEASON_WINTER))) {
                int seasonCount = 0;
                if (typeGroups.containsKey(TileType.SEASON_SPRING)) seasonCount += typeGroups.get(TileType.SEASON_SPRING).size();
                if (typeGroups.containsKey(TileType.SEASON_SUMMER)) seasonCount += typeGroups.get(TileType.SEASON_SUMMER).size();
                if (typeGroups.containsKey(TileType.SEASON_AUTUMN)) seasonCount += typeGroups.get(TileType.SEASON_AUTUMN).size();
                if (typeGroups.containsKey(TileType.SEASON_WINTER)) seasonCount += typeGroups.get(TileType.SEASON_WINTER).size();
                if (seasonCount >= 2) return false;
            }
        }

        return true;
    }

    /**
     * Gets the number of remaining tiles.
     */
    public int getRemainingTileCount() {
        int count = 0;
        for (Tile tile : tiles) {
            if (!tile.isRemoved()) count++;
        }
        return count;
    }

    @NonNull
    @Override
    public String toString() {
        return "Board{" + layoutId + ", " + getRemainingTileCount() + " tiles remaining}";
    }
}
