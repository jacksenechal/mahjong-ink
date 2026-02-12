package com.mahjongink.model;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Unit tests for the Board class.
 */
public class BoardTest {

    private Board board;
    private List<Tile> tiles;

    @Before
    public void setUp() {
        tiles = new ArrayList<>();
        // Create a simple 2x2 grid at z=0
        tiles.add(new Tile(0, TileType.CHARACTER_1, new Position(0, 0, 0)));
        tiles.add(new Tile(1, TileType.CHARACTER_1, new Position(1, 0, 0)));
        tiles.add(new Tile(2, TileType.CHARACTER_2, new Position(0, 1, 0)));
        tiles.add(new Tile(3, TileType.CHARACTER_2, new Position(1, 1, 0)));

        board = new Board("test", tiles);
    }

    @Test
    public void testGetTiles_returnsAllTiles() {
        List<Tile> result = board.getTiles();
        assertEquals(4, result.size());
    }

    @Test
    public void testGetTileAt_existingPosition() {
        Tile tile = board.getTileAt(new Position(0, 0, 0));
        assertNotNull(tile);
        assertEquals(0, tile.getId());
    }

    @Test
    public void testGetTileAt_nonExistingPosition() {
        Tile tile = board.getTileAt(new Position(99, 99, 99));
        assertNull(tile);
    }

    @Test
    public void testIsTileFree_edgeTile() {
        // Tile at (0,0) should be free (left side open)
        Tile tile = tiles.get(0);
        assertTrue(board.isTileFree(tile));
    }

    @Test
    public void testIsTileFree_blockedTile() {
        // Add a tile on top
        Tile topTile = new Tile(4, TileType.CHARACTER_3, new Position(0, 0, 1));
        List<Tile> newTiles = new ArrayList<>(tiles);
        newTiles.add(topTile);
        Board newBoard = new Board("test2", newTiles);

        // Bottom tile should not be free
        assertFalse(newBoard.isTileFree(tiles.get(0)));
        // Top tile should be free
        assertTrue(newBoard.isTileFree(topTile));
    }

    @Test
    public void testRemovePair_matchingTiles() {
        Tile tile1 = tiles.get(0);
        Tile tile2 = tiles.get(1);

        boolean result = board.removePair(tile1, tile2);

        assertTrue(result);
        assertTrue(tile1.isRemoved());
        assertTrue(tile2.isRemoved());
    }

    @Test
    public void testRemovePair_nonMatchingTiles() {
        Tile tile1 = tiles.get(0); // CHARACTER_1
        Tile tile2 = tiles.get(2); // CHARACTER_2

        boolean result = board.removePair(tile1, tile2);

        assertFalse(result);
        assertFalse(tile1.isRemoved());
        assertFalse(tile2.isRemoved());
    }

    @Test
    public void testRemovePair_sameTile() {
        Tile tile = tiles.get(0);
        boolean result = board.removePair(tile, tile);
        assertFalse(result);
    }

    @Test
    public void testIsGameWon_allTilesRemoved() {
        for (Tile tile : tiles) {
            tile.setRemoved(true);
        }
        assertTrue(board.isGameWon());
    }

    @Test
    public void testIsGameWon_someTilesRemaining() {
        tiles.get(0).setRemoved(true);
        assertFalse(board.isGameWon());
    }

    @Test
    public void testGetRemainingTileCount() {
        assertEquals(4, board.getRemainingTileCount());
        tiles.get(0).setRemoved(true);
        assertEquals(3, board.getRemainingTileCount());
    }

    @Test
    public void testGetFreeTiles() {
        List<Tile> freeTiles = board.getFreeTiles();
        // In a 2x2 grid, corner tiles should be free
        assertTrue(freeTiles.size() > 0);
    }

    @Test
    public void testFlowersMatchAnyFlower() {
        List<Tile> flowerTiles = new ArrayList<>();
        flowerTiles.add(new Tile(0, TileType.FLOWER_PLUM, new Position(0, 0, 0)));
        flowerTiles.add(new Tile(1, TileType.FLOWER_ORCHID, new Position(1, 0, 0)));

        Board flowerBoard = new Board("flowers", flowerTiles);

        assertTrue(flowerBoard.removePair(flowerTiles.get(0), flowerTiles.get(1)));
    }

    @Test
    public void testSeasonsMatchAnySeason() {
        List<Tile> seasonTiles = new ArrayList<>();
        seasonTiles.add(new Tile(0, TileType.SEASON_SPRING, new Position(0, 0, 0)));
        seasonTiles.add(new Tile(1, TileType.SEASON_SUMMER, new Position(1, 0, 0)));

        Board seasonBoard = new Board("seasons", seasonTiles);

        assertTrue(seasonBoard.removePair(seasonTiles.get(0), seasonTiles.get(1)));
    }

    @Test
    public void testSetSelectedTile() {
        Tile tile = tiles.get(0);
        board.setSelectedTile(tile);

        assertEquals(tile, board.getSelectedTile());
        assertTrue(tile.isSelected());
    }

    @Test
    public void testSetSelectedTile_nullDeselects() {
        Tile tile = tiles.get(0);
        board.setSelectedTile(tile);
        board.setSelectedTile(null);

        assertNull(board.getSelectedTile());
        assertFalse(tile.isSelected());
    }
}
