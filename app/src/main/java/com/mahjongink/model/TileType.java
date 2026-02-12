package com.mahjongink.model;

/**
 * Represents the type of a Mahjong tile.
 * Mahjong solitaire uses 144 tiles total:
 * - 36 Characters (1-9, 4 of each)
 * - 36 Bamboos (1-9, 4 of each)
 * - 36 Circles (1-9, 4 of each)
 * - 16 Winds (N, E, S, W, 4 of each)
 * - 12 Dragons (Red, Green, White, 4 of each)
 * - 4 Flowers (4 unique)
 * - 4 Seasons (4 unique)
 */
public enum TileType {
    // Character suit (Man) - 1-9
    CHARACTER_1, CHARACTER_2, CHARACTER_3, CHARACTER_4, CHARACTER_5,
    CHARACTER_6, CHARACTER_7, CHARACTER_8, CHARACTER_9,

    // Bamboo suit (Sou) - 1-9
    BAMBOO_1, BAMBOO_2, BAMBOO_3, BAMBOO_4, BAMBOO_5,
    BAMBOO_6, BAMBOO_7, BAMBOO_8, BAMBOO_9,

    // Circle suit (Pin) - 1-9
    CIRCLE_1, CIRCLE_2, CIRCLE_3, CIRCLE_4, CIRCLE_5,
    CIRCLE_6, CIRCLE_7, CIRCLE_8, CIRCLE_9,

    // Winds - 4 directions
    WIND_NORTH, WIND_EAST, WIND_SOUTH, WIND_WEST,

    // Dragons - 3 colors
    DRAGON_RED, DRAGON_GREEN, DRAGON_WHITE,

    // Flowers - 4 unique (can match any flower)
    FLOWER_PLUM, FLOWER_ORCHID, FLOWER_CHRYSANTHEMUM, FLOWER_BAMBOO,

    // Seasons - 4 unique (can match any season)
    SEASON_SPRING, SEASON_SUMMER, SEASON_AUTUMN, SEASON_WINTER;

    /**
     * Checks if two tiles can be matched.
     * Regular tiles must be identical. Flowers match any flower, seasons match any season.
     */
    public static boolean canMatch(TileType a, TileType b) {
        if (a == b) return true;

        // Flowers match any other flower
        if (isFlower(a) && isFlower(b)) return true;

        // Seasons match any other season
        if (isSeason(a) && isSeason(b)) return true;

        return false;
    }

    private static boolean isFlower(TileType type) {
        return type == FLOWER_PLUM || type == FLOWER_ORCHID
                || type == FLOWER_CHRYSANTHEMUM || type == FLOWER_BAMBOO;
    }

    private static boolean isSeason(TileType type) {
        return type == SEASON_SPRING || type == SEASON_SUMMER
                || type == SEASON_AUTUMN || type == SEASON_WINTER;
    }

    /**
     * Returns the suit category for display purposes.
     */
    public Suit getSuit() {
        String name = name();
        if (name.startsWith("CHARACTER")) return Suit.CHARACTER;
        if (name.startsWith("BAMBOO")) return Suit.BAMBOO;
        if (name.startsWith("CIRCLE")) return Suit.CIRCLE;
        if (name.startsWith("WIND")) return Suit.WIND;
        if (name.startsWith("DRAGON")) return Suit.DRAGON;
        if (name.startsWith("FLOWER")) return Suit.FLOWER;
        if (name.startsWith("SEASON")) return Suit.SEASON;
        return Suit.UNKNOWN;
    }

    public enum Suit {
        CHARACTER, BAMBOO, CIRCLE, WIND, DRAGON, FLOWER, SEASON, UNKNOWN
    }
}
