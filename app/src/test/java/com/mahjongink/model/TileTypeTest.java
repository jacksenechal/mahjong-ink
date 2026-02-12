package com.mahjongink.model;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Unit tests for the TileType enum.
 */
public class TileTypeTest {

    @Test
    public void testCanMatch_sameType() {
        assertTrue(TileType.canMatch(TileType.CHARACTER_1, TileType.CHARACTER_1));
        assertTrue(TileType.canMatch(TileType.WIND_EAST, TileType.WIND_EAST));
        assertTrue(TileType.canMatch(TileType.DRAGON_RED, TileType.DRAGON_RED));
    }

    @Test
    public void testCanMatch_differentTypes() {
        assertFalse(TileType.canMatch(TileType.CHARACTER_1, TileType.CHARACTER_2));
        assertFalse(TileType.canMatch(TileType.WIND_EAST, TileType.WIND_WEST));
        assertFalse(TileType.canMatch(TileType.CHARACTER_1, TileType.WIND_EAST));
    }

    @Test
    public void testCanMatch_flowers() {
        // Any flower can match any other flower
        assertTrue(TileType.canMatch(TileType.FLOWER_PLUM, TileType.FLOWER_ORCHID));
        assertTrue(TileType.canMatch(TileType.FLOWER_ORCHID, TileType.FLOWER_CHRYSANTHEMUM));
        assertTrue(TileType.canMatch(TileType.FLOWER_CHRYSANTHEMUM, TileType.FLOWER_BAMBOO));
        assertTrue(TileType.canMatch(TileType.FLOWER_PLUM, TileType.FLOWER_PLUM));
    }

    @Test
    public void testCanMatch_seasons() {
        // Any season can match any other season
        assertTrue(TileType.canMatch(TileType.SEASON_SPRING, TileType.SEASON_SUMMER));
        assertTrue(TileType.canMatch(TileType.SEASON_SUMMER, TileType.SEASON_AUTUMN));
        assertTrue(TileType.canMatch(TileType.SEASON_AUTUMN, TileType.SEASON_WINTER));
        assertTrue(TileType.canMatch(TileType.SEASON_SPRING, TileType.SEASON_SPRING));
    }

    @Test
    public void testCanMatch_flowerWithNonFlower() {
        // Flowers cannot match non-flowers
        assertFalse(TileType.canMatch(TileType.FLOWER_PLUM, TileType.CHARACTER_1));
        assertFalse(TileType.canMatch(TileType.FLOWER_PLUM, TileType.SEASON_SPRING));
        assertFalse(TileType.canMatch(TileType.FLOWER_PLUM, TileType.WIND_EAST));
    }

    @Test
    public void testGetSuit() {
        assertEquals(TileType.Suit.CHARACTER, TileType.CHARACTER_1.getSuit());
        assertEquals(TileType.Suit.CHARACTER, TileType.CHARACTER_9.getSuit());
        assertEquals(TileType.Suit.BAMBOO, TileType.BAMBOO_5.getSuit());
        assertEquals(TileType.Suit.CIRCLE, TileType.CIRCLE_3.getSuit());
        assertEquals(TileType.Suit.WIND, TileType.WIND_NORTH.getSuit());
        assertEquals(TileType.Suit.DRAGON, TileType.DRAGON_RED.getSuit());
        assertEquals(TileType.Suit.FLOWER, TileType.FLOWER_PLUM.getSuit());
        assertEquals(TileType.Suit.SEASON, TileType.SEASON_SPRING.getSuit());
    }

    @Test
    public void testAllTypesHaveSuit() {
        for (TileType type : TileType.values()) {
            assertNotNull(type.getSuit());
            assertNotEquals(TileType.Suit.UNKNOWN, type.getSuit());
        }
    }
}
