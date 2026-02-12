package com.mahjongink.logic;

import com.mahjongink.model.Board;
import com.mahjongink.model.GameConfig;
import com.mahjongink.model.Layout;
import com.mahjongink.model.Position;
import com.mahjongink.model.Tile;
import com.mahjongink.model.TileType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

/**
 * Generates playable boards from layouts.
 * Handles tile placement with different difficulty strategies.
 */
public class BoardGenerator {

    private final Random random;

    public BoardGenerator() {
        this.random = new Random();
    }

    public BoardGenerator(long seed) {
        this.random = new Random(seed);
    }

    /**
     * Generates a board from a layout with the specified difficulty.
     */
    public Board generateBoard(Layout layout, GameConfig.Difficulty difficulty) {
        List<Position> positions = new ArrayList<>(layout.getPositions());

        // Ensure we have an even number of tiles
        if (positions.size() % 2 != 0) {
            positions.remove(positions.size() - 1);
        }

        // Generate tile distribution based on difficulty
        List<TileType> tileTypes = generateTileDistribution(positions.size(), difficulty);

        // Create tiles
        List<Tile> tiles = new ArrayList<>();
        for (int i = 0; i < positions.size(); i++) {
            Tile tile = new Tile(i, tileTypes.get(i), positions.get(i));
            tiles.add(tile);
        }

        return new Board(layout.getId(), tiles);
    }

    /**
     * Generates a board that is guaranteed to be solvable.
     * This is used for EASY and MEDIUM difficulties.
     */
    public Board generateSolvableBoard(Layout layout, GameConfig.Difficulty difficulty) {
        int maxAttempts = 100;

        for (int attempt = 0; attempt < maxAttempts; attempt++) {
            Board board = generateBoard(layout, difficulty);
            if (isBoardSolvable(board)) {
                return board;
            }
        }

        // Fallback: return the last generated board
        return generateBoard(layout, difficulty);
    }

    /**
     * Generates a tile distribution for the given number of tiles.
     */
    private List<TileType> generateTileDistribution(int tileCount, GameConfig.Difficulty difficulty) {
        List<TileType> distribution = new ArrayList<>();

        // Create pairs of tiles
        int pairCount = tileCount / 2;

        // Get available tile types with their frequencies
        List<TileType> availableTypes = getAvailableTileTypes();

        // Shuffle available types
        Collections.shuffle(availableTypes, random);

        // Create pairs
        int typeIndex = 0;
        int pairsOfThisType = 0;
        final int maxPairsPerType = 4; // Standard mahjong has 4 of each tile

        for (int i = 0; i < pairCount; i++) {
            TileType type = availableTypes.get(typeIndex);
            distribution.add(type);
            distribution.add(type);

            pairsOfThisType++;
            if (pairsOfThisType >= maxPairsPerType) {
                typeIndex = (typeIndex + 1) % availableTypes.size();
                pairsOfThisType = 0;
            }
        }

        // Shuffle the distribution
        Collections.shuffle(distribution, random);

        return distribution;
    }

    /**
     * Returns all available tile types for the game.
     */
    private List<TileType> getAvailableTileTypes() {
        List<TileType> types = new ArrayList<>();

        // Characters 1-9
        for (int i = 1; i <= 9; i++) {
            types.add(TileType.valueOf("CHARACTER_" + i));
        }

        // Bamboos 1-9
        for (int i = 1; i <= 9; i++) {
            types.add(TileType.valueOf("BAMBOO_" + i));
        }

        // Circles 1-9
        for (int i = 1; i <= 9; i++) {
            types.add(TileType.valueOf("CIRCLE_" + i));
        }

        // Winds
        types.add(TileType.WIND_NORTH);
        types.add(TileType.WIND_EAST);
        types.add(TileType.WIND_SOUTH);
        types.add(TileType.WIND_WEST);

        // Dragons
        types.add(TileType.DRAGON_RED);
        types.add(TileType.DRAGON_GREEN);
        types.add(TileType.DRAGON_WHITE);

        // Flowers
        types.add(TileType.FLOWER_PLUM);
        types.add(TileType.FLOWER_ORCHID);
        types.add(TileType.FLOWER_CHRYSANTHEMUM);
        types.add(TileType.FLOWER_BAMBOO);

        // Seasons
        types.add(TileType.SEASON_SPRING);
        types.add(TileType.SEASON_SUMMER);
        types.add(TileType.SEASON_AUTUMN);
        types.add(TileType.SEASON_WINTER);

        return types;
    }

    /**
     * Checks if a board is potentially solvable using a simplified simulation.
     * This is a heuristic - not perfect but good enough for difficulty control.
     */
    private boolean isBoardSolvable(Board board) {
        // Create a copy of the board for simulation
        List<Tile> tiles = board.getTiles();
        Set<Integer> removed = new HashSet<>();

        int movesAvailable = 0;
        int totalMoves = 0;

        // Simulate up to 100 moves
        for (int step = 0; step < 100; step++) {
            List<Tile> freeTiles = getFreeTiles(tiles, removed);

            if (freeTiles.isEmpty()) {
                break;
            }

            // Find matching pairs among free tiles
            Map<TileType, List<Tile>> typeGroups = new HashMap<>();
            for (Tile tile : freeTiles) {
                if (!typeGroups.containsKey(tile.getType())) {
                    typeGroups.put(tile.getType(), new ArrayList<>());
                }
                typeGroups.get(tile.getType()).add(tile);
            }

            // Count available pairs
            boolean foundPair = false;
            for (List<Tile> group : typeGroups.values()) {
                if (group.size() >= 2) {
                    // Remove a pair
                    removed.add(group.get(0).getId());
                    removed.add(group.get(1).getId());
                    movesAvailable++;
                    foundPair = true;
                    break;
                }
            }

            // Check for flower/season pairs
            if (!foundPair) {
                List<Tile> flowers = new ArrayList<>();
                List<Tile> seasons = new ArrayList<>();

                for (Tile tile : freeTiles) {
                    if (isFlower(tile.getType())) flowers.add(tile);
                    if (isSeason(tile.getType())) seasons.add(tile);
                }

                if (flowers.size() >= 2) {
                    removed.add(flowers.get(0).getId());
                    removed.add(flowers.get(1).getId());
                    movesAvailable++;
                    foundPair = true;
                } else if (seasons.size() >= 2) {
                    removed.add(seasons.get(0).getId());
                    removed.add(seasons.get(1).getId());
                    movesAvailable++;
                    foundPair = true;
                }
            }

            if (!foundPair) {
                break;
            }

            totalMoves++;
        }

        // Board is solvable if we can remove most tiles
        float removalRate = (float) removed.size() / tiles.size();
        return removalRate >= 0.8f; // At least 80% removable
    }

    private List<Tile> getFreeTiles(List<Tile> tiles, Set<Integer> removed) {
        List<Tile> freeTiles = new ArrayList<>();
        Map<Position, Tile> positionMap = new HashMap<>();

        for (Tile tile : tiles) {
            if (!removed.contains(tile.getId())) {
                positionMap.put(tile.getPosition(), tile);
            }
        }

        for (Tile tile : tiles) {
            if (removed.contains(tile.getId())) continue;

            Position pos = tile.getPosition();

            // Check if blocked from above
            boolean blockedAbove = false;
            for (int dx = -1; dx <= 1 && !blockedAbove; dx++) {
                for (int dy = -1; dy <= 1 && !blockedAbove; dy++) {
                    Position above = new Position(pos.getX() + dx, pos.getY() + dy, pos.getZ() + 1);
                    Tile aboveTile = positionMap.get(above);
                    if (aboveTile != null && !removed.contains(aboveTile.getId())) {
                        blockedAbove = true;
                    }
                }
            }

            if (blockedAbove) continue;

            // Check if left/right blocked
            boolean leftBlocked = false;
            boolean rightBlocked = false;

            for (int dy = -1; dy <= 1; dy++) {
                Position left = new Position(pos.getX() - 1, pos.getY() + dy, pos.getZ());
                Tile leftTile = positionMap.get(left);
                if (leftTile != null && !removed.contains(leftTile.getId())) {
                    leftBlocked = true;
                    break;
                }
            }

            for (int dy = -1; dy <= 1; dy++) {
                Position right = new Position(pos.getX() + 1, pos.getY() + dy, pos.getZ());
                Tile rightTile = positionMap.get(right);
                if (rightTile != null && !removed.contains(rightTile.getId())) {
                    rightBlocked = true;
                    break;
                }
            }

            if (!leftBlocked || !rightBlocked) {
                freeTiles.add(tile);
            }
        }

        return freeTiles;
    }

    private boolean isFlower(TileType type) {
        return type == TileType.FLOWER_PLUM || type == TileType.FLOWER_ORCHID
                || type == TileType.FLOWER_CHRYSANTHEMUM || type == TileType.FLOWER_BAMBOO;
    }

    private boolean isSeason(TileType type) {
        return type == TileType.SEASON_SPRING || type == TileType.SEASON_SUMMER
                || type == TileType.SEASON_AUTUMN || type == TileType.SEASON_WINTER;
    }
}
