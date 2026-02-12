package com.mahjongink.logic;

import com.mahjongink.model.Board;
import com.mahjongink.model.GameConfig;
import com.mahjongink.model.Layout;
import com.mahjongink.model.Tile;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Unit tests for the GameManager class.
 */
public class GameManagerTest {

    private GameManager gameManager;
    private TestGameListener listener;

    private static class TestGameListener implements GameManager.GameListener {
        boolean gameStarted = false;
        boolean gameWon = false;
        boolean gameLost = false;
        Board lastBoard;

        @Override
        public void onGameStarted(Board board) {
            gameStarted = true;
            lastBoard = board;
        }

        @Override
        public void onGameWon(Board board, long timeMs) {
            gameWon = true;
        }

        @Override
        public void onGameLost(Board board) {
            gameLost = true;
        }

        @Override
        public void onTileSelected(Tile tile) {}

        @Override
        public void onTilesRemoved(Tile tile1, Tile tile2) {}

        @Override
        public void onLayoutChanged(Layout layout) {}

        void reset() {
            gameStarted = false;
            gameWon = false;
            gameLost = false;
            lastBoard = null;
        }
    }

    @Before
    public void setUp() {
        gameManager = new GameManager();
        listener = new TestGameListener();
        gameManager.setListener(listener);
    }

    @Test
    public void testStartNewGame_createsNewBoard() {
        gameManager.startNewGame();

        assertTrue(listener.gameStarted);
        assertNotNull(listener.lastBoard);
        assertNotNull(gameManager.getCurrentBoard());
        assertNotNull(gameManager.getCurrentLayout());
    }

    @Test
    public void testStartNewGame_incrementsGamesPlayed() {
        int initialGames = gameManager.getGamesPlayed();
        gameManager.startNewGame();
        assertEquals(initialGames + 1, gameManager.getGamesPlayed());
    }

    @Test
    public void testSetAndGetConfig() {
        GameConfig config = new GameConfig();
        config.setDifficulty(GameConfig.Difficulty.HARD);
        config.setLayoutMode(GameConfig.LayoutMode.PROGRESSIVE);

        gameManager.setConfig(config);

        assertEquals(GameConfig.Difficulty.HARD, gameManager.getConfig().getDifficulty());
        assertEquals(GameConfig.LayoutMode.PROGRESSIVE, gameManager.getConfig().getLayoutMode());
    }

    @Test
    public void testOnTileSelect_withNullTile_returnsFalse() {
        gameManager.startNewGame();
        boolean result = gameManager.onTileSelected(null);
        assertFalse(result);
    }

    @Test
    public void testOnTileSelect_withRemovedTile_returnsFalse() {
        gameManager.startNewGame();
        Board board = gameManager.getCurrentBoard();

        // Get a tile and mark it as removed
        Tile tile = board.getTiles().get(0);
        tile.setRemoved(true);

        boolean result = gameManager.onTileSelected(tile);
        assertFalse(result);
    }

    @Test
    public void testGetHint_returnsNullOrValidPair() {
        gameManager.startNewGame();
        Tile[] hint = gameManager.getHint();

        // Hint may be null if no moves available, or a valid pair
        if (hint != null) {
            assertNotNull(hint[0]);
            assertNotNull(hint[1]);
            assertTrue(hint[0].canMatchWith(hint[1]));
        }
    }

    @Test
    public void testStartNewGame_withSpecificLayout() {
        gameManager.startNewGame("turtle");

        assertEquals("turtle", gameManager.getCurrentLayout().getId());
        assertEquals(GameConfig.LayoutMode.FIXED, gameManager.getConfig().getLayoutMode());
    }

    @Test
    public void testProgressiveMode_advancesIndex() {
        GameConfig config = new GameConfig();
        config.setLayoutMode(GameConfig.LayoutMode.PROGRESSIVE);
        config.setProgressiveIndex(0);
        gameManager.setConfig(config);

        gameManager.startNewGame();

        // Simulate winning
        Board board = gameManager.getCurrentBoard();
        for (Tile tile : board.getTiles()) {
            tile.setRemoved(true);
        }

        // Trigger win check by selecting a tile
        gameManager.onTileSelected(null);

        assertTrue(listener.gameWon);
        assertEquals(1, gameManager.getConfig().getProgressiveIndex());
    }

    @Test
    public void testSaveGameState_returnsValidState() {
        gameManager.startNewGame();

        var state = gameManager.saveGameState();

        assertNotNull(state);
        assertNotNull(state.getLayoutId());
        assertFalse(state.getTileStates().isEmpty());
    }
}
