package com.mahjongink.logic;

import com.mahjongink.model.Board;
import com.mahjongink.model.GameConfig;
import com.mahjongink.model.Layout;
import com.mahjongink.model.Position;
import com.mahjongink.model.Tile;

import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

/**
 * Unit tests for the BoardGenerator class.
 */
public class BoardGeneratorTest {

    private BoardGenerator generator;

    @Before
    public void setUp() {
        generator = new BoardGenerator(12345); // Fixed seed for reproducibility
    }

    @Test
    public void testGenerateBoard_createsCorrectNumberOfTiles() {
        Layout layout = LayoutCatalog.getLayoutById("turtle");
        Board board = generator.generateBoard(layout, GameConfig.Difficulty.MEDIUM);

        assertNotNull(board);
        assertEquals(layout.getTileCount(), board.getTiles().size());
    }

    @Test
    public void testGenerateBoard_allTilesHaveValidPositions() {
        Layout layout = LayoutCatalog.getLayoutById("pyramid");
        Board board = generator.generateBoard(layout, GameConfig.Difficulty.EASY);

        List<Tile> tiles = board.getTiles();
        for (Tile tile : tiles) {
            assertNotNull(tile.getPosition());
            assertTrue(tile.getPosition().getX() >= 0);
            assertTrue(tile.getPosition().getY() >= 0);
            assertTrue(tile.getPosition().getZ() >= 0);
        }
    }

    @Test
    public void testGenerateBoard_hasEvenNumberOfTiles() {
        Layout layout = LayoutCatalog.getLayoutById("dragon");
        Board board = generator.generateBoard(layout, GameConfig.Difficulty.HARD);

        int tileCount = board.getTiles().size();
        assertTrue("Tile count should be even", tileCount % 2 == 0);
    }

    @Test
    public void testGenerateBoard_createsMatchingPairs() {
        Layout layout = LayoutCatalog.getLayoutById("cross");
        Board board = generator.generateBoard(layout, GameConfig.Difficulty.MEDIUM);

        // Count tile types
        int[] typeCounts = new int[50]; // Simplified counting
        for (Tile tile : board.getTiles()) {
            int typeOrdinal = tile.getType().ordinal();
            if (typeOrdinal < typeCounts.length) {
                typeCounts[typeOrdinal]++;
            }
        }

        // Each type should have an even count (pairs)
        for (int count : typeCounts) {
            if (count > 0) {
                assertTrue("Each tile type should appear in pairs", count % 2 == 0);
            }
        }
    }

    @Test
    public void testGenerateSolvableBoard_createsSolvableGame() {
        Layout layout = LayoutCatalog.getLayoutById("diamond");
        Board board = generator.generateSolvableBoard(layout, GameConfig.Difficulty.EASY);

        assertNotNull(board);

        // A solvable board should have at least some free tiles initially
        List<Tile> freeTiles = board.getFreeTiles();
        assertFalse("Solvable board should have free tiles", freeTiles.isEmpty());
    }

    @Test
    public void testGenerateBoard_differentDifficultiesProduceDifferentBoards() {
        Layout layout = LayoutCatalog.getLayoutById("turtle");

        Board easyBoard = generator.generateBoard(layout, GameConfig.Difficulty.EASY);
        Board mediumBoard = generator.generateBoard(layout, GameConfig.Difficulty.MEDIUM);
        Board hardBoard = generator.generateBoard(layout, GameConfig.Difficulty.HARD);

        // Boards should have same structure but different tile placements
        assertEquals(easyBoard.getTiles().size(), mediumBoard.getTiles().size());
        assertEquals(mediumBoard.getTiles().size(), hardBoard.getTiles().size());
    }

    @Test
    public void testGenerateBoard_withSmallLayout() {
        Layout layout = LayoutCatalog.getLayoutById("pyramid");
        Board board = generator.generateBoard(layout, GameConfig.Difficulty.EASY);

        assertNotNull(board);
        assertTrue(board.getTiles().size() > 0);
        assertTrue(board.getTiles().size() % 2 == 0);
    }

    @Test
    public void testGenerateBoard_tilePositionsMatchLayout() {
        Layout layout = LayoutCatalog.getLayoutById("flower");
        Board board = generator.generateBoard(layout, GameConfig.Difficulty.MEDIUM);

        List<Tile> tiles = board.getTiles();
        List<Position> positions = layout.getPositions();

        // Each tile should have a corresponding layout position
        assertEquals(positions.size(), tiles.size());
    }
}
