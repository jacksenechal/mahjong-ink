package com.mahjongink.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.mahjongink.model.Board;
import com.mahjongink.model.Position;
import com.mahjongink.model.Tile;
import com.mahjongink.model.TileType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Custom view for rendering the Mahjong game board.
 * Optimized for e-ink displays with high contrast and no animations.
 */
public class GameBoardView extends View {

    // E-ink optimized colors - high contrast
    private static final int COLOR_BACKGROUND = Color.WHITE;
    private static final int COLOR_TILE_FACE = Color.WHITE;
    private static final int COLOR_TILE_BORDER = Color.BLACK;
    private static final int COLOR_TILE_SELECTED = Color.BLACK;
    private static final int COLOR_TILE_TEXT = Color.BLACK;
    private static final int COLOR_TILE_HIGHLIGHT = Color.BLACK;
    private static final int COLOR_HINT = Color.GRAY;

    // Tile dimensions (in pixels)
    private float tileWidth = 60f;
    private float tileHeight = 80f;
    private float tileDepth = 8f; // Visual depth for stacked tiles
    private float tileSpacing = 2f;

    private Board board;
    private Tile hintTile1;
    private Tile hintTile2;

    private final Paint paint;
    private final RectF rect;

    private OnTileClickListener tileClickListener;

    // Cache for tile positions on screen
    private final Map<Integer, RectF> tileBoundsCache;

    public interface OnTileClickListener {
        void onTileClick(Tile tile);
    }

    public GameBoardView(Context context) {
        super(context);
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        rect = new RectF();
        tileBoundsCache = new HashMap<>();
    }

    public GameBoardView(Context context, AttributeSet attrs) {
        super(context, attrs);
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        rect = new RectF();
        tileBoundsCache = new HashMap<>();
    }

    public void setBoard(Board board) {
        this.board = board;
        this.hintTile1 = null;
        this.hintTile2 = null;
        tileBoundsCache.clear();
        calculateTileDimensions();
        invalidate();
    }

    public void setOnTileClickListener(OnTileClickListener listener) {
        this.tileClickListener = listener;
    }

    public void showHint(Tile tile1, Tile tile2) {
        this.hintTile1 = tile1;
        this.hintTile2 = tile2;
        invalidate();
    }

    public void clearHint() {
        this.hintTile1 = null;
        this.hintTile2 = null;
        invalidate();
    }

    /**
     * Calculates tile dimensions based on view size and board layout.
     */
    private void calculateTileDimensions() {
        if (board == null || getWidth() == 0 || getHeight() == 0) return;

        List<Tile> tiles = board.getTiles();
        if (tiles.isEmpty()) return;

        // Find bounds of the board
        int minX = Integer.MAX_VALUE, maxX = Integer.MIN_VALUE;
        int minY = Integer.MAX_VALUE, maxY = Integer.MIN_VALUE;
        int maxZ = 0;

        for (Tile tile : tiles) {
            Position pos = tile.getPosition();
            minX = Math.min(minX, pos.getX());
            maxX = Math.max(maxX, pos.getX());
            minY = Math.min(minY, pos.getY());
            maxY = Math.max(maxY, pos.getY());
            maxZ = Math.max(maxZ, pos.getZ());
        }

        // Calculate tile size to fit within view
        float availableWidth = getWidth() - getPaddingLeft() - getPaddingRight();
        float availableHeight = getHeight() - getPaddingTop() - getPaddingBottom();

        int tileCountX = maxX - minX + 1;
        int tileCountY = maxY - minY + 1;

        // Account for layer offset
        float layerOffsetX = maxZ * tileDepth * 0.5f;
        float layerOffsetY = maxZ * tileDepth * 0.5f;

        float maxTileWidth = (availableWidth - layerOffsetX) / tileCountX - tileSpacing;
        float maxTileHeight = (availableHeight - layerOffsetY) / tileCountY - tileSpacing * 0.75f;

        // Maintain aspect ratio
        tileWidth = Math.min(maxTileWidth, maxTileHeight * 0.75f);
        tileHeight = tileWidth / 0.75f;

        // Recalculate tile bounds
        tileBoundsCache.clear();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        calculateTileDimensions();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // Clear background
        canvas.drawColor(COLOR_BACKGROUND);

        if (board == null) return;

        // Draw tiles in Z-order (bottom to top)
        List<Tile> tiles = board.getTiles();
        int maxZ = 0;
        for (Tile tile : tiles) {
            maxZ = Math.max(maxZ, tile.getPosition().getZ());
        }

        for (int z = 0; z <= maxZ; z++) {
            for (Tile tile : tiles) {
                if (tile.getPosition().getZ() == z && !tile.isRemoved()) {
                    drawTile(canvas, tile);
                }
            }
        }
    }

    private void drawTile(Canvas canvas, Tile tile) {
        RectF bounds = getTileBounds(tile);
        if (bounds == null) return;

        boolean isSelected = tile.isSelected();
        boolean isHint = (tile == hintTile1 || tile == hintTile2);

        // Draw shadow/depth for stacked tiles
        if (tile.getPosition().getZ() > 0) {
            paint.setColor(Color.GRAY);
            paint.setStyle(Paint.Style.FILL);
            rect.set(bounds.left + tileDepth, bounds.top + tileDepth,
                    bounds.right + tileDepth, bounds.bottom + tileDepth);
            canvas.drawRect(rect, paint);
        }

        // Draw tile face
        if (isSelected) {
            paint.setColor(COLOR_TILE_SELECTED);
        } else {
            paint.setColor(COLOR_TILE_FACE);
        }
        paint.setStyle(Paint.Style.FILL);
        canvas.drawRect(bounds, paint);

        // Draw border
        paint.setColor(COLOR_TILE_BORDER);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(isSelected ? 3f : 2f);
        canvas.drawRect(bounds, paint);

        // Draw hint indicator
        if (isHint) {
            paint.setColor(COLOR_HINT);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(4f);
            float inset = 4f;
            rect.set(bounds.left + inset, bounds.top + inset,
                    bounds.right - inset, bounds.bottom - inset);
            canvas.drawRect(rect, paint);
        }

        // Draw tile content
        drawTileContent(canvas, tile, bounds);
    }

    private void drawTileContent(Canvas canvas, Tile tile, RectF bounds) {
        TileType type = tile.getType();
        paint.setColor(isSelected(tile) ? Color.WHITE : COLOR_TILE_TEXT);
        paint.setStyle(Paint.Style.FILL);

        float centerX = bounds.centerX();
        float centerY = bounds.centerY();
        float contentSize = Math.min(bounds.width(), bounds.height()) * 0.6f;

        // Draw based on tile type
        String symbol = getTileSymbol(type);
        paint.setTextSize(contentSize * 0.8f);
        paint.setTextAlign(Paint.Align.CENTER);

        float textY = centerY + contentSize * 0.3f;
        canvas.drawText(symbol, centerX, textY, paint);

        // Draw small suit indicator for suited tiles
        if (type.getSuit() == TileType.Suit.CHARACTER ||
                type.getSuit() == TileType.Suit.BAMBOO ||
                type.getSuit() == TileType.Suit.CIRCLE) {
            paint.setTextSize(contentSize * 0.3f);
            String suit = getSuitSymbol(type.getSuit());
            canvas.drawText(suit, centerX, bounds.bottom - contentSize * 0.2f, paint);
        }
    }

    private boolean isSelected(Tile tile) {
        return tile.isSelected();
    }

    private String getTileSymbol(TileType type) {
        String name = type.name();

        // Characters
        if (name.startsWith("CHARACTER_")) {
            int num = Integer.parseInt(name.substring(10));
            return String.valueOf(num);
        }

        // Bamboos
        if (name.startsWith("BAMBOO_")) {
            int num = Integer.parseInt(name.substring(7));
            if (num == 1) return "🌸"; // Flower for 1 of bamboo
            return String.valueOf(num);
        }

        // Circles
        if (name.startsWith("CIRCLE_")) {
            int num = Integer.parseInt(name.substring(7));
            return String.valueOf(num);
        }

        // Winds
        if (name.equals("WIND_NORTH")) return "N";
        if (name.equals("WIND_EAST")) return "E";
        if (name.equals("WIND_SOUTH")) return "S";
        if (name.equals("WIND_WEST")) return "W";

        // Dragons
        if (name.equals("DRAGON_RED")) return "R";
        if (name.equals("DRAGON_GREEN")) return "G";
        if (name.equals("DRAGON_WHITE")) return "B";

        // Flowers
        if (name.equals("FLOWER_PLUM")) return "P";
        if (name.equals("FLOWER_ORCHID")) return "O";
        if (name.equals("FLOWER_CHRYSANTHEMUM")) return "C";
        if (name.equals("FLOWER_BAMBOO")) return "F";

        // Seasons
        if (name.equals("SEASON_SPRING")) return "1";
        if (name.equals("SEASON_SUMMER")) return "2";
        if (name.equals("SEASON_AUTUMN")) return "3";
        if (name.equals("SEASON_WINTER")) return "4";

        return "?";
    }

    private String getSuitSymbol(TileType.Suit suit) {
        switch (suit) {
            case CHARACTER: return "万";
            case BAMBOO: return "条";
            case CIRCLE: return "圈";
            default: return "";
        }
    }

    private RectF getTileBounds(Tile tile) {
        int tileId = tile.getId();
        if (tileBoundsCache.containsKey(tileId)) {
            return tileBoundsCache.get(tileId);
        }

        Position pos = tile.getPosition();

        // Calculate screen position
        float x = getPaddingLeft() + pos.getX() * (tileWidth + tileSpacing);
        float y = getPaddingTop() + pos.getY() * (tileHeight * 0.75f + tileSpacing);

        // Offset by Z layer
        x -= pos.getZ() * tileDepth * 0.5f;
        y -= pos.getZ() * tileDepth * 0.5f;

        RectF bounds = new RectF(x, y, x + tileWidth, y + tileHeight);
        tileBoundsCache.put(tileId, bounds);
        return bounds;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() != MotionEvent.ACTION_DOWN) {
            return super.onTouchEvent(event);
        }

        if (board == null) return false;

        float x = event.getX();
        float y = event.getY();

        // Find clicked tile (check from top to bottom)
        List<Tile> tiles = board.getTiles();
        Tile clickedTile = null;
        int maxZ = -1;

        for (Tile tile : tiles) {
            if (tile.isRemoved()) continue;

            RectF bounds = getTileBounds(tile);
            if (bounds.contains(x, y) && tile.getPosition().getZ() > maxZ) {
                clickedTile = tile;
                maxZ = tile.getPosition().getZ();
            }
        }

        if (clickedTile != null && tileClickListener != null) {
            tileClickListener.onTileClick(clickedTile);
            return true;
        }

        return super.onTouchEvent(event);
    }
}
