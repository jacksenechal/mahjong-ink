package com.mahjongink.model;

import androidx.annotation.NonNull;

/**
 * Configuration for a game session.
 * Contains settings like difficulty, layout mode, and selected layout.
 */
public class GameConfig {

    public enum Difficulty {
        EASY(0.7f),    // 70% solvable boards, more pairs available
        MEDIUM(0.85f), // 85% solvable boards, balanced
        HARD(1.0f);    // 100% random, may be unsolvable

        private final float solvableThreshold;

        Difficulty(float solvableThreshold) {
            this.solvableThreshold = solvableThreshold;
        }

        public float getSolvableThreshold() {
            return solvableThreshold;
        }
    }

    public enum LayoutMode {
        FIXED,      // Play same layout repeatedly
        RANDOM,     // Random layout each game
        PROGRESSIVE // Progress through layouts in order
    }

    private Difficulty difficulty;
    private LayoutMode layoutMode;
    private String fixedLayoutId; // Used when layoutMode is FIXED
    private int progressiveIndex; // Current index for progressive mode

    public GameConfig() {
        this.difficulty = Difficulty.MEDIUM;
        this.layoutMode = LayoutMode.RANDOM;
        this.fixedLayoutId = null;
        this.progressiveIndex = 0;
    }

    public Difficulty getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(Difficulty difficulty) {
        this.difficulty = difficulty;
    }

    public LayoutMode getLayoutMode() {
        return layoutMode;
    }

    public void setLayoutMode(LayoutMode layoutMode) {
        this.layoutMode = layoutMode;
    }

    public String getFixedLayoutId() {
        return fixedLayoutId;
    }

    public void setFixedLayoutId(String fixedLayoutId) {
        this.fixedLayoutId = fixedLayoutId;
    }

    public int getProgressiveIndex() {
        return progressiveIndex;
    }

    public void setProgressiveIndex(int progressiveIndex) {
        this.progressiveIndex = progressiveIndex;
    }

    public void advanceProgressive() {
        this.progressiveIndex++;
    }

    @NonNull
    @Override
    public String toString() {
        return "GameConfig{difficulty=" + difficulty + ", mode=" + layoutMode + "}";
    }
}
