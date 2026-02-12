package com.mahjongink.logic;

import com.mahjongink.model.Board;
import com.mahjongink.model.GameConfig;
import com.mahjongink.model.GameState;
import com.mahjongink.model.Layout;
import com.mahjongink.model.Tile;

import java.util.ArrayList;
import java.util.List;

/**
 * Manages the overall game session including layout progression,
 * game state, and user preferences.
 */
public class GameManager {

    private final BoardGenerator boardGenerator;
    private GameConfig config;
    private Board currentBoard;
    private Layout currentLayout;
    private long gameStartTime;
    private int gamesWon;
    private int gamesPlayed;
    private GameListener listener;

    public interface GameListener {
        void onGameStarted(Board board);
        void onGameWon(Board board, long timeMs);
        void onGameLost(Board board);
        void onTileSelected(Tile tile);
        void onTilesRemoved(Tile tile1, Tile tile2);
        void onLayoutChanged(Layout layout);
    }

    public GameManager() {
        this.boardGenerator = new BoardGenerator();
        this.config = new GameConfig();
        this.gamesWon = 0;
        this.gamesPlayed = 0;
    }

    public void setListener(GameListener listener) {
        this.listener = listener;
    }

    public GameConfig getConfig() {
        return config;
    }

    public void setConfig(GameConfig config) {
        this.config = config;
    }

    public Board getCurrentBoard() {
        return currentBoard;
    }

    public Layout getCurrentLayout() {
        return currentLayout;
    }

    public int getGamesWon() {
        return gamesWon;
    }

    public int getGamesPlayed() {
        return gamesPlayed;
    }

    /**
     * Starts a new game with the current configuration.
     */
    public void startNewGame() {
        // Select layout based on mode
        currentLayout = selectLayout();

        // Generate board based on difficulty
        if (config.getDifficulty() == GameConfig.Difficulty.HARD) {
            // Hard mode: completely random, may not be solvable
            currentBoard = boardGenerator.generateBoard(currentLayout, config.getDifficulty());
        } else {
            // Easy/Medium: try to generate solvable boards
            currentBoard = boardGenerator.generateSolvableBoard(currentLayout, config.getDifficulty());
        }

        gameStartTime = System.currentTimeMillis();
        gamesPlayed++;

        if (listener != null) {
            listener.onLayoutChanged(currentLayout);
            listener.onGameStarted(currentBoard);
        }
    }

    /**
     * Starts a new game with a specific layout.
     */
    public void startNewGame(String layoutId) {
        config.setLayoutMode(GameConfig.LayoutMode.FIXED);
        config.setFixedLayoutId(layoutId);
        startNewGame();
    }

    /**
     * Handles tile selection. Returns true if a pair was removed.
     */
    public boolean onTileSelected(Tile tile) {
        if (currentBoard == null) {
            return false;
        }

        if (tile == null) {
            checkGameState();
            return false;
        }

        if (tile.isRemoved()) {
            return false;
        }

        if (!currentBoard.isTileFree(tile)) {
            return false;
        }

        Tile selected = currentBoard.getSelectedTile();

        if (selected == null) {
            // Select this tile
            currentBoard.setSelectedTile(tile);
            if (listener != null) {
                listener.onTileSelected(tile);
            }
            return false;
        }

        if (selected.getId() == tile.getId()) {
            // Deselect
            currentBoard.setSelectedTile(null);
            if (listener != null) {
                listener.onTileSelected(null);
            }
            return false;
        }

        // Try to match
        if (currentBoard.removePair(selected, tile)) {
            currentBoard.setSelectedTile(null);
            if (listener != null) {
                listener.onTilesRemoved(selected, tile);
            }

            // Check game state
            checkGameState();
            return true;
        } else {
            // Select the new tile instead
            currentBoard.setSelectedTile(tile);
            if (listener != null) {
                listener.onTileSelected(tile);
            }
            return false;
        }
    }

    /**
     * Checks if the game has been won or lost.
     */
    private void checkGameState() {
        if (currentBoard.isGameWon()) {
            gamesWon++;
            long gameTime = System.currentTimeMillis() - gameStartTime;

            // Advance progressive mode
            if (config.getLayoutMode() == GameConfig.LayoutMode.PROGRESSIVE) {
                config.advanceProgressive();
            }

            if (listener != null) {
                listener.onGameWon(currentBoard, gameTime);
            }
        } else if (currentBoard.isGameStuck()) {
            if (listener != null) {
                listener.onGameLost(currentBoard);
            }
        }
    }

    /**
     * Selects the next layout based on the current configuration.
     */
    private Layout selectLayout() {
        switch (config.getLayoutMode()) {
            case FIXED:
                if (config.getFixedLayoutId() != null) {
                    return LayoutCatalog.getLayoutById(config.getFixedLayoutId());
                }
                // Fall through to random if no fixed layout set

            case RANDOM:
                int randomIndex = (int) (Math.random() * LayoutCatalog.getLayoutCount());
                return LayoutCatalog.getLayoutByIndex(randomIndex);

            case PROGRESSIVE:
                int index = config.getProgressiveIndex() % LayoutCatalog.getLayoutCount();
                return LayoutCatalog.getLayoutByIndex(index);

            default:
                return LayoutCatalog.getLayoutByIndex(0);
        }
    }

    /**
     * Gets a hint - returns a pair of tiles that can be matched.
     */
    public Tile[] getHint() {
        if (currentBoard == null) return null;

        List<Tile> freeTiles = currentBoard.getFreeTiles();

        // Look for matching pairs
        for (int i = 0; i < freeTiles.size(); i++) {
            for (int j = i + 1; j < freeTiles.size(); j++) {
                Tile t1 = freeTiles.get(i);
                Tile t2 = freeTiles.get(j);
                if (t1.canMatchWith(t2)) {
                    return new Tile[]{t1, t2};
                }
            }
        }

        return null;
    }

    /**
     * Saves the current game state.
     */
    public GameState saveGameState() {
        if (currentBoard == null) return null;

        List<GameState.TileState> tileStates = new ArrayList<>();
        for (Tile tile : currentBoard.getTiles()) {
            tileStates.add(new GameState.TileState(
                    tile.getId(),
                    tile.getType().name(),
                    tile.getPosition().getX(),
                    tile.getPosition().getY(),
                    tile.getPosition().getZ(),
                    tile.isRemoved()
            ));
        }

        int selectedId = currentBoard.getSelectedTile() != null
                ? currentBoard.getSelectedTile().getId() : -1;

        long elapsedTime = System.currentTimeMillis() - gameStartTime;

        return new GameState(
                currentLayout.getId(),
                tileStates,
                selectedId,
                elapsedTime,
                gameStartTime
        );
    }

    /**
     * Restores a game from saved state.
     */
    public void restoreGameState(GameState state) {
        // TODO: Implement state restoration
    }
}
