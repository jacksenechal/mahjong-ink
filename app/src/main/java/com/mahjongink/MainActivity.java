package com.mahjongink;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;
import com.mahjongink.logic.GameManager;
import com.mahjongink.logic.LayoutCatalog;
import com.mahjongink.model.Board;
import com.mahjongink.model.GameConfig;
import com.mahjongink.model.Layout;
import com.mahjongink.model.Tile;
import com.mahjongink.view.GameBoardView;

/**
 * Main activity for the Mahjong Ink game.
 * Displays the game board and handles user interactions.
 */
public class MainActivity extends AppCompatActivity implements GameManager.GameListener {

    private DrawerLayout drawerLayout;
    private GameBoardView gameBoardView;
    private TextView statusText;
    private TextView layoutNameText;
    private TextView tilesRemainingText;

    private GameManager gameManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Set up toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Initialize views
        drawerLayout = findViewById(R.id.drawer_layout);
        gameBoardView = findViewById(R.id.game_board_view);
        statusText = findViewById(R.id.status_text);
        layoutNameText = findViewById(R.id.layout_name_text);
        tilesRemainingText = findViewById(R.id.tiles_remaining_text);

        // Set up navigation drawer
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this::onNavigationItemSelected);

        // Initialize game manager
        gameManager = new GameManager();
        gameManager.setListener(this);

        // Set up game board click listener
        gameBoardView.setOnTileClickListener(tile -> gameManager.onTileSelected(tile));

        // Start first game
        gameManager.startNewGame();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_new_game) {
            showNewGameConfirmation();
            return true;
        } else if (id == R.id.action_hint) {
            showHint();
            return true;
        } else if (id == R.id.action_menu) {
            drawerLayout.openDrawer(GravityCompat.START);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_new_game) {
            drawerLayout.closeDrawer(GravityCompat.START);
            showNewGameConfirmation();
            return true;
        } else if (id == R.id.nav_select_layout) {
            drawerLayout.closeDrawer(GravityCompat.START);
            openLayoutSelection();
            return true;
        } else if (id == R.id.nav_difficulty_easy) {
            setDifficulty(GameConfig.Difficulty.EASY);
            return true;
        } else if (id == R.id.nav_difficulty_medium) {
            setDifficulty(GameConfig.Difficulty.MEDIUM);
            return true;
        } else if (id == R.id.nav_difficulty_hard) {
            setDifficulty(GameConfig.Difficulty.HARD);
            return true;
        } else if (id == R.id.nav_mode_fixed) {
            setLayoutMode(GameConfig.LayoutMode.FIXED);
            return true;
        } else if (id == R.id.nav_mode_random) {
            setLayoutMode(GameConfig.LayoutMode.RANDOM);
            return true;
        } else if (id == R.id.nav_mode_progressive) {
            setLayoutMode(GameConfig.LayoutMode.PROGRESSIVE);
            return true;
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private void setDifficulty(GameConfig.Difficulty difficulty) {
        gameManager.getConfig().setDifficulty(difficulty);
        updateStatusText();
        drawerLayout.closeDrawer(GravityCompat.START);

        String diffName = difficulty.name().toLowerCase();
        Toast.makeText(this, "Difficulty: " + diffName, Toast.LENGTH_SHORT).show();
    }

    private void setLayoutMode(GameConfig.LayoutMode mode) {
        gameManager.getConfig().setLayoutMode(mode);
        updateStatusText();
        drawerLayout.closeDrawer(GravityCompat.START);

        String modeName = mode.name().toLowerCase();
        Toast.makeText(this, "Layout mode: " + modeName, Toast.LENGTH_SHORT).show();
    }

    private void showNewGameConfirmation() {
        new AlertDialog.Builder(this)
                .setTitle("New Game")
                .setMessage("Start a new game?")
                .setPositiveButton("Yes", (dialog, which) -> gameManager.startNewGame())
                .setNegativeButton("No", null)
                .show();
    }

    private void showHint() {
        Tile[] hint = gameManager.getHint();
        if (hint != null) {
            gameBoardView.showHint(hint[0], hint[1]);
            Toast.makeText(this, "Hint shown", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "No moves available", Toast.LENGTH_SHORT).show();
        }
    }

    private void openLayoutSelection() {
        Intent intent = new Intent(this, LayoutSelectionActivity.class);
        startActivity(intent);
    }

    private void updateStatusText() {
        GameConfig config = gameManager.getConfig();
        String status = "Diff: " + config.getDifficulty().name().substring(0, 1) +
                " | Mode: " + config.getLayoutMode().name().substring(0, 1);
        statusText.setText(status);
    }

    // GameListener implementation

    @Override
    public void onGameStarted(Board board) {
        gameBoardView.setBoard(board);
        gameBoardView.clearHint();
        updateStatusText();
        updateGameInfo();
    }

    @Override
    public void onGameWon(Board board, long timeMs) {
        updateGameInfo();
        String timeStr = formatTime(timeMs);
        new AlertDialog.Builder(this)
                .setTitle("Congratulations!")
                .setMessage("You won!\nTime: " + timeStr)
                .setPositiveButton("Next Game", (dialog, which) -> gameManager.startNewGame())
                .setCancelable(false)
                .show();
    }

    @Override
    public void onGameLost(Board board) {
        updateGameInfo();

        GameConfig.LayoutMode mode = gameManager.getConfig().getLayoutMode();
        if (mode == GameConfig.LayoutMode.RANDOM) {
            new AlertDialog.Builder(this)
                    .setTitle("Game Over")
                    .setMessage("No more moves available.")
                    .setPositiveButton("Retry", (dialog, which) -> {
                        // Retry same layout
                        gameManager.startNewGame();
                    })
                    .setNegativeButton("New Layout", (dialog, which) -> gameManager.startNewGame())
                    .setCancelable(false)
                    .show();
        } else {
            new AlertDialog.Builder(this)
                    .setTitle("Game Over")
                    .setMessage("No more moves available.")
                    .setPositiveButton("Try Again", (dialog, which) -> gameManager.startNewGame())
                    .setCancelable(false)
                    .show();
        }
    }

    @Override
    public void onTileSelected(Tile tile) {
        gameBoardView.invalidate();
    }

    @Override
    public void onTilesRemoved(Tile tile1, Tile tile2) {
        gameBoardView.invalidate();
        updateGameInfo();
    }

    @Override
    public void onLayoutChanged(Layout layout) {
        layoutNameText.setText(layout.getName());
    }

    private void updateGameInfo() {
        Board board = gameManager.getCurrentBoard();
        if (board != null) {
            tilesRemainingText.setText("Tiles: " + board.getRemainingTileCount());
        }
    }

    private String formatTime(long timeMs) {
        long seconds = timeMs / 1000;
        long minutes = seconds / 60;
        seconds = seconds % 60;
        return String.format("%d:%02d", minutes, seconds);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Check if a new layout was selected
        String selectedLayoutId = getSharedPreferences("mahjong_ink", MODE_PRIVATE)
                .getString("selected_layout_id", null);
        if (selectedLayoutId != null) {
            getSharedPreferences("mahjong_ink", MODE_PRIVATE)
                    .edit()
                    .remove("selected_layout_id")
                    .apply();
            gameManager.startNewGame(selectedLayoutId);
        }
    }
}
