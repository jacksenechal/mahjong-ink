package com.mahjongink.logic;

import com.mahjongink.model.Layout;
import com.mahjongink.model.Position;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Catalog of all available game layouts.
 * Layouts are ordered by approximate difficulty (easiest to hardest).
 */
public class LayoutCatalog {

    private static final List<Layout> LAYOUTS = new ArrayList<>();

    static {
        initializeLayouts();
    }

    private static void initializeLayouts() {
        // Easy layouts (simple, few layers)
        LAYOUTS.add(createLayout("pyramid", "Pyramid", "Simple triangular layout", 2,
                createPyramidLayout()));
        LAYOUTS.add(createLayout("diamond", "Diamond", "Diamond-shaped layout", 2,
                createDiamondLayout()));
        LAYOUTS.add(createLayout("cross", "Cross", "Simple cross pattern", 2,
                createCrossLayout()));
        LAYOUTS.add(createLayout("small_square", "Small Square", "Compact square layout", 3,
                createSmallSquareLayout()));

        // Medium layouts (classic turtle variations)
        LAYOUTS.add(createLayout("turtle", "Turtle", "Classic mahjong solitaire layout", 5,
                createTurtleLayout()));
        LAYOUTS.add(createLayout("spider", "Spider", "Spider-shaped layout", 5,
                createSpiderLayout()));
        LAYOUTS.add(createLayout("flower", "Flower", "Flower pattern layout", 4,
                createFlowerLayout()));
        LAYOUTS.add(createLayout("fortress", "Fortress", "Fortress wall layout", 5,
                createFortressLayout()));

        // Harder layouts (complex shapes, more layers)
        LAYOUTS.add(createLayout("dragon", "Dragon", "Complex dragon pattern", 7,
                createDragonLayout()));
        LAYOUTS.add(createLayout("temple", "Temple", "Temple gate layout", 6,
                createTempleLayout()));
        LAYOUTS.add(createLayout("well", "The Well", "Deep layered layout", 8,
                createWellLayout()));
        LAYOUTS.add(createLayout("cat", "Cat", "Cat-shaped layout", 6,
                createCatLayout()));

        // Expert layouts (very complex)
        LAYOUTS.add(createLayout("scorpion", "Scorpion", "Complex scorpion pattern", 9,
                createScorpionLayout()));
        LAYOUTS.add(createLayout("cobra", "Cobra", "Cobra snake layout", 9,
                createCobraLayout()));
        LAYOUTS.add(createLayout("ox", "Ox", "Ox-shaped complex layout", 8,
                createOxLayout()));
        LAYOUTS.add(createLayout("ram", "Ram", "Ram horn layout", 8,
                createRamLayout()));
    }

    public static List<Layout> getAllLayouts() {
        return Collections.unmodifiableList(LAYOUTS);
    }

    public static Layout getLayoutById(String id) {
        for (Layout layout : LAYOUTS) {
            if (layout.getId().equals(id)) {
                return layout;
            }
        }
        return LAYOUTS.get(0); // Default to first layout
    }

    public static Layout getLayoutByIndex(int index) {
        if (index < 0 || index >= LAYOUTS.size()) {
            return LAYOUTS.get(0);
        }
        return LAYOUTS.get(index);
    }

    public static int getLayoutCount() {
        return LAYOUTS.size();
    }

    public static int getIndexById(String id) {
        for (int i = 0; i < LAYOUTS.size(); i++) {
            if (LAYOUTS.get(i).getId().equals(id)) {
                return i;
            }
        }
        return 0;
    }

    // Layout creation helpers

    private static Layout createLayout(String id, String name, String description,
                                       int difficulty, List<Position> positions) {
        return new Layout(id, name, description, difficulty, positions);
    }

    /**
     * Simple pyramid layout - 36 tiles
     */
    private static List<Position> createPyramidLayout() {
        List<Position> positions = new ArrayList<>();
        // Base layer
        for (int y = 0; y < 5; y++) {
            for (int x = y; x < 9 - y; x++) {
                positions.add(new Position(x + 4, y + 3, 0));
            }
        }
        // Second layer
        for (int y = 0; y < 3; y++) {
            for (int x = y; x < 5 - y; x++) {
                positions.add(new Position(x + 6, y + 4, 1));
            }
        }
        // Top
        positions.add(new Position(8, 5, 2));
        return positions;
    }

    /**
     * Diamond layout - 40 tiles
     */
    private static List<Position> createDiamondLayout() {
        List<Position> positions = new ArrayList<>();
        int[] widths = {2, 4, 6, 8, 6, 4, 2};
        for (int y = 0; y < widths.length; y++) {
            int startX = (10 - widths[y]) / 2;
            for (int x = 0; x < widths[y]; x++) {
                positions.add(new Position(startX + x + 4, y + 2, 0));
            }
        }
        return positions;
    }

    /**
     * Cross layout - 36 tiles
     */
    private static List<Position> createCrossLayout() {
        List<Position> positions = new ArrayList<>();
        // Vertical bar
        for (int y = 0; y < 9; y++) {
            for (int x = 0; x < 3; x++) {
                positions.add(new Position(x + 8, y + 1, 0));
            }
        }
        // Horizontal bar
        for (int x = 0; x < 9; x++) {
            for (int y = 0; y < 3; y++) {
                if (x < 3 || x >= 6) { // Skip center (already filled)
                    positions.add(new Position(x + 5, y + 4, 0));
                }
            }
        }
        return positions;
    }

    /**
     * Small square layout - 48 tiles
     */
    private static List<Position> createSmallSquareLayout() {
        List<Position> positions = new ArrayList<>();
        for (int z = 0; z < 3; z++) {
            for (int y = 0; y < 4; y++) {
                for (int x = 0; x < 4; x++) {
                    positions.add(new Position(x + 7 - z, y + 3 - z, z));
                }
            }
        }
        return positions;
    }

    /**
     * Classic Turtle layout - 144 tiles
     */
    private static List<Position> createTurtleLayout() {
        List<Position> positions = new ArrayList<>();

        // Layer 0 - Base (largest)
        // Center block
        for (int y = 2; y < 10; y++) {
            for (int x = 4; x < 12; x++) {
                positions.add(new Position(x, y, 0));
            }
        }
        // Left wing
        for (int y = 3; y < 9; y++) {
            positions.add(new Position(2, y, 0));
            positions.add(new Position(3, y, 0));
        }
        // Right wing
        for (int y = 3; y < 9; y++) {
            positions.add(new Position(12, y, 0));
            positions.add(new Position(13, y, 0));
        }
        // Head
        for (int x = 6; x < 10; x++) {
            positions.add(new Position(x, 1, 0));
        }
        // Tail
        for (int x = 6; x < 10; x++) {
            positions.add(new Position(x, 10, 0));
        }

        // Layer 1
        for (int y = 3; y < 9; y++) {
            for (int x = 5; x < 11; x++) {
                positions.add(new Position(x, y, 1));
            }
        }

        // Layer 2
        for (int y = 4; y < 8; y++) {
            for (int x = 6; x < 10; x++) {
                positions.add(new Position(x, y, 2));
            }
        }

        // Layer 3
        for (int y = 5; y < 7; y++) {
            for (int x = 7; x < 9; x++) {
                positions.add(new Position(x, y, 3));
            }
        }

        // Layer 4 - Top
        positions.add(new Position(7, 5, 4));
        positions.add(new Position(8, 5, 4));
        positions.add(new Position(7, 6, 4));
        positions.add(new Position(8, 6, 4));

        return positions;
    }

    /**
     * Spider layout - 104 tiles
     */
    private static List<Position> createSpiderLayout() {
        List<Position> positions = new ArrayList<>();

        // Body (center)
        for (int y = 4; y < 8; y++) {
            for (int x = 6; x < 10; x++) {
                positions.add(new Position(x, y, 0));
            }
        }

        // Legs (8 legs)
        int[][] legStarts = {{4, 3}, {11, 3}, {4, 8}, {11, 8}, {3, 2}, {12, 2}, {3, 9}, {12, 9}};
        for (int[] start : legStarts) {
            positions.add(new Position(start[0], start[1], 0));
            positions.add(new Position(start[0] + (start[0] < 7 ? -1 : 1), start[1] + (start[1] < 6 ? -1 : 1), 0));
        }

        // Upper body layers
        for (int z = 1; z < 4; z++) {
            for (int y = 5 - z; y < 7 + z; y++) {
                for (int x = 7 - z; x < 9 + z; x++) {
                    positions.add(new Position(x, y, z));
                }
            }
        }

        return positions;
    }

    /**
     * Flower layout - 96 tiles
     */
    private static List<Position> createFlowerLayout() {
        List<Position> positions = new ArrayList<>();

        // Center
        for (int y = 5; y < 7; y++) {
            for (int x = 7; x < 9; x++) {
                positions.add(new Position(x, y, 0));
            }
        }

        // Petals (4 petals)
        int[][] petalCenters = {{7, 3}, {7, 8}, {4, 5}, {11, 5}};
        for (int[] center : petalCenters) {
            for (int dy = -1; dy <= 1; dy++) {
                for (int dx = -1; dx <= 1; dx++) {
                    positions.add(new Position(center[0] + dx, center[1] + dy, 0));
                }
            }
        }

        // Layers
        for (int z = 1; z < 3; z++) {
            for (int y = 5 - z; y < 7 + z; y++) {
                for (int x = 7 - z; x < 9 + z; x++) {
                    positions.add(new Position(x, y, z));
                }
            }
        }

        return positions;
    }

    /**
     * Fortress layout - 128 tiles
     */
    private static List<Position> createFortressLayout() {
        List<Position> positions = new ArrayList<>();

        // Outer walls
        for (int x = 3; x < 13; x++) {
            positions.add(new Position(x, 2, 0));
            positions.add(new Position(x, 9, 0));
        }
        for (int y = 2; y <= 9; y++) {
            positions.add(new Position(3, y, 0));
            positions.add(new Position(12, y, 0));
        }

        // Inner structure
        for (int y = 4; y < 8; y++) {
            for (int x = 5; x < 11; x++) {
                positions.add(new Position(x, y, 0));
            }
        }

        // Towers (corners with height)
        int[][] towers = {{3, 2}, {12, 2}, {3, 9}, {12, 9}};
        for (int[] tower : towers) {
            positions.add(new Position(tower[0], tower[1], 1));
            positions.add(new Position(tower[0], tower[1], 2));
        }

        // Center keep
        for (int z = 1; z < 4; z++) {
            for (int y = 5 - z / 2; y < 7 + z / 2; y++) {
                for (int x = 7 - z / 2; x < 9 + z / 2; x++) {
                    positions.add(new Position(x, y, z));
                }
            }
        }

        return positions;
    }

    /**
     * Dragon layout - 156 tiles
     */
    private static List<Position> createDragonLayout() {
        List<Position> positions = new ArrayList<>();

        // Head
        for (int y = 1; y < 4; y++) {
            for (int x = 6; x < 10; x++) {
                positions.add(new Position(x, y, 0));
            }
        }

        // Body (serpentine)
        int[] bodyY = {4, 4, 5, 5, 6, 6, 7, 7, 8, 8, 9, 9};
        int[] bodyX = {5, 10, 4, 11, 4, 11, 5, 10, 6, 9, 7, 8};
        for (int i = 0; i < bodyY.length; i++) {
            positions.add(new Position(bodyX[i], bodyY[i], 0));
            positions.add(new Position(bodyX[i], bodyY[i] + 1, 0));
        }

        // Tail
        for (int x = 6; x < 10; x++) {
            positions.add(new Position(x, 11, 0));
        }

        // Layers on body
        for (int z = 1; z < 3; z++) {
            for (int y = 5; y < 9; y++) {
                for (int x = 6; x < 10; x++) {
                    positions.add(new Position(x, y, z));
                }
            }
        }

        return positions;
    }

    /**
     * Temple layout - 132 tiles
     */
    private static List<Position> createTempleLayout() {
        List<Position> positions = new ArrayList<>();

        // Base/platform
        for (int y = 8; y < 11; y++) {
            for (int x = 3; x < 13; x++) {
                positions.add(new Position(x, y, 0));
            }
        }

        // Pillars
        for (int y = 3; y < 8; y++) {
            positions.add(new Position(4, y, 0));
            positions.add(new Position(11, y, 0));
        }

        // Roof
        for (int x = 2; x < 14; x++) {
            positions.add(new Position(x, 1, 0));
            positions.add(new Position(x, 2, 0));
        }

        // Roof layers
        for (int z = 1; z < 4; z++) {
            for (int x = 3 + z; x < 13 - z; x++) {
                positions.add(new Position(x, 2, z));
            }
        }

        return positions;
    }

    /**
     * Well layout - 140 tiles (deep)
     */
    private static List<Position> createWellLayout() {
        List<Position> positions = new ArrayList<>();

        // Concentric rings going down
        for (int z = 0; z < 5; z++) {
            int size = 5 - z;
            int offsetX = 7 + z / 2;
            int offsetY = 5 + z / 2;

            for (int y = 0; y < size; y++) {
                for (int x = 0; x < size; x++) {
                    // Only outer ring of each layer
                    if (x == 0 || x == size - 1 || y == 0 || y == size - 1 || z == 4) {
                        positions.add(new Position(offsetX + x, offsetY + y, z));
                    }
                }
            }
        }

        return positions;
    }

    /**
     * Cat layout - 120 tiles
     */
    private static List<Position> createCatLayout() {
        List<Position> positions = new ArrayList<>();

        // Head
        for (int y = 1; y < 5; y++) {
            for (int x = 6; x < 10; x++) {
                positions.add(new Position(x, y, 0));
            }
        }
        // Ears
        positions.add(new Position(5, 1, 0));
        positions.add(new Position(10, 1, 0));

        // Body
        for (int y = 5; y < 10; y++) {
            for (int x = 5; x < 11; x++) {
                positions.add(new Position(x, y, 0));
            }
        }

        // Tail
        positions.add(new Position(11, 7, 0));
        positions.add(new Position(12, 6, 0));
        positions.add(new Position(12, 5, 0));

        // Layers on body
        for (int z = 1; z < 3; z++) {
            for (int y = 6; y < 9; y++) {
                for (int x = 6; x < 10; x++) {
                    positions.add(new Position(x, y, z));
                }
            }
        }

        return positions;
    }

    /**
     * Scorpion layout - 168 tiles (complex)
     */
    private static List<Position> createScorpionLayout() {
        List<Position> positions = new ArrayList<>();

        // Body (elongated)
        for (int y = 4; y < 8; y++) {
            for (int x = 5; x < 11; x++) {
                positions.add(new Position(x, y, 0));
            }
        }

        // Pincers (left and right)
        for (int i = 0; i < 4; i++) {
            positions.add(new Position(3 - i, 3 + i, 0));
            positions.add(new Position(12 + i, 3 + i, 0));
        }

        // Tail (curved up)
        int[] tailX = {8, 9, 9, 10, 10, 10};
        int[] tailY = {2, 2, 1, 1, 0, 0};
        for (int i = 0; i < tailX.length; i++) {
            positions.add(new Position(tailX[i], tailY[i], 0));
        }

        // Legs
        for (int i = 0; i < 4; i++) {
            positions.add(new Position(4, 5 + i, 0));
            positions.add(new Position(11, 5 + i, 0));
        }

        // Body layers
        for (int z = 1; z < 4; z++) {
            for (int y = 5; y < 7; y++) {
                for (int x = 6; x < 10; x++) {
                    positions.add(new Position(x, y, z));
                }
            }
        }

        return positions;
    }

    /**
     * Cobra layout - 152 tiles
     */
    private static List<Position> createCobraLayout() {
        List<Position> positions = new ArrayList<>();

        // Head (wide)
        for (int y = 1; y < 4; y++) {
            for (int x = 5; x < 11; x++) {
                positions.add(new Position(x, y, 0));
            }
        }

        // Body (S-curve)
        int[] bodyX = {6, 7, 8, 8, 7, 6, 5, 5, 6, 7, 8, 9};
        int[] bodyY = {4, 4, 5, 6, 7, 7, 8, 9, 10, 10, 9, 8};
        for (int i = 0; i < bodyX.length; i++) {
            positions.add(new Position(bodyX[i], bodyY[i], 0));
            positions.add(new Position(bodyX[i] + 1, bodyY[i], 0));
        }

        // Hood (raised sides)
        positions.add(new Position(4, 2, 1));
        positions.add(new Position(11, 2, 1));

        return positions;
    }

    /**
     * Ox layout - 144 tiles
     */
    private static List<Position> createOxLayout() {
        List<Position> positions = new ArrayList<>();

        // Head
        for (int y = 2; y < 5; y++) {
            for (int x = 6; x < 10; x++) {
                positions.add(new Position(x, y, 0));
            }
        }
        // Horns
        positions.add(new Position(4, 1, 0));
        positions.add(new Position(5, 2, 0));
        positions.add(new Position(11, 2, 0));
        positions.add(new Position(12, 1, 0));

        // Body (large)
        for (int y = 5; y < 10; y++) {
            for (int x = 4; x < 12; x++) {
                positions.add(new Position(x, y, 0));
            }
        }

        // Legs
        for (int y = 10; y < 12; y++) {
            positions.add(new Position(5, y, 0));
            positions.add(new Position(6, y, 0));
            positions.add(new Position(9, y, 0));
            positions.add(new Position(10, y, 0));
        }

        // Body layers
        for (int z = 1; z < 3; z++) {
            for (int y = 6; y < 9; y++) {
                for (int x = 5; x < 11; x++) {
                    positions.add(new Position(x, y, z));
                }
            }
        }

        return positions;
    }

    /**
     * Ram layout - 136 tiles
     */
    private static List<Position> createRamLayout() {
        List<Position> positions = new ArrayList<>();

        // Head
        for (int y = 3; y < 6; y++) {
            for (int x = 6; x < 10; x++) {
                positions.add(new Position(x, y, 0));
            }
        }

        // Large curved horns
        int[][] hornLeft = {{5, 2}, {4, 1}, {3, 1}, {2, 2}, {2, 3}};
        int[][] hornRight = {{10, 2}, {11, 1}, {12, 1}, {13, 2}, {13, 3}};
        for (int[] pos : hornLeft) {
            positions.add(new Position(pos[0], pos[1], 0));
        }
        for (int[] pos : hornRight) {
            positions.add(new Position(pos[0], pos[1], 0));
        }

        // Body
        for (int y = 6; y < 10; y++) {
            for (int x = 5; x < 11; x++) {
                positions.add(new Position(x, y, 0));
            }
        }

        // Body layers
        for (int z = 1; z < 3; z++) {
            for (int y = 7; y < 9; y++) {
                for (int x = 6; x < 10; x++) {
                    positions.add(new Position(x, y, z));
                }
            }
        }

        return positions;
    }
}
