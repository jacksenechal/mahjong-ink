# Mahjong Ink

A Mahjong Solitaire puzzle app optimized for Android e-ink tablets. Simple, high-contrast tiles with no animations or transitions - just fast, easy on the eyes gameplay designed for low refresh rate displays.

## Features

- **E-ink Optimized**: High contrast black and white design, no animations, no transitions
- **16 Built-in Layouts**: From easy (Pyramid, Diamond) to expert (Scorpion, Cobra)
- **Three Difficulty Levels**:
  - Easy: 70% solvable boards, more pairs available
  - Medium: 85% solvable boards, balanced challenge
  - Hard: Completely random tile placement, may be unsolvable
- **Layout Modes**:
  - Fixed: Play the same layout repeatedly
  - Random: Random layout after each win, retry option on loss
  - Progressive: Progress through layouts from easiest to hardest
- **Hint System**: Shows available moves when stuck
- **No Sound**: Silent operation perfect for quiet environments

## Screenshots

*Screenshots will be added here*

## Download

Download the latest APK from the [Releases](../../releases) page or from the build artifacts in GitHub Actions.

## Building

### Prerequisites

- Android Studio Hedgehog (2023.1.1) or later
- JDK 17 or later
- Android SDK 34

### Build Instructions

1. Clone the repository:
   ```bash
   git clone https://github.com/yourusername/mahjong-ink.git
   cd mahjong-ink
   ```

2. Build the debug APK:
   ```bash
   ./gradlew assembleDebug
   ```

3. Build the release APK:
   ```bash
   ./gradlew assembleRelease
   ```

4. Run tests:
   ```bash
   ./gradlew test
   ```

## Architecture

The app follows a clean, layered architecture:

```
com.mahjongink/
├── model/          # Data models (Tile, Board, Layout, etc.)
├── logic/          # Game logic (BoardGenerator, GameManager, LayoutCatalog)
├── view/           # Custom views (GameBoardView)
├── MainActivity.java
└── LayoutSelectionActivity.java
```

### Key Components

- **TileType**: Enum representing all 42 tile types (Characters, Bamboos, Circles, Winds, Dragons, Flowers, Seasons)
- **Board**: Manages tile positions, free tile detection, and game state
- **BoardGenerator**: Creates playable boards with configurable difficulty
- **GameManager**: Manages game sessions, layout progression, and user preferences
- **LayoutCatalog**: Contains 16 predefined layouts ordered by difficulty
- **GameBoardView**: Custom view for rendering the game board with e-ink optimization

## Layouts

| Layout | Tiles | Difficulty | Description |
|--------|-------|------------|-------------|
| Pyramid | 36 | 2/10 | Simple triangular layout |
| Diamond | 40 | 2/10 | Diamond-shaped layout |
| Cross | 36 | 2/10 | Simple cross pattern |
| Small Square | 48 | 3/10 | Compact square layout |
| Turtle | 144 | 5/10 | Classic mahjong solitaire |
| Spider | 104 | 5/10 | Spider-shaped layout |
| Flower | 96 | 4/10 | Flower pattern |
| Fortress | 128 | 5/10 | Fortress wall layout |
| Dragon | 156 | 7/10 | Complex dragon pattern |
| Temple | 132 | 6/10 | Temple gate layout |
| The Well | 140 | 8/10 | Deep layered layout |
| Cat | 120 | 6/10 | Cat-shaped layout |
| Scorpion | 168 | 9/10 | Complex scorpion pattern |
| Cobra | 152 | 9/10 | Cobra snake layout |
| Ox | 144 | 8/10 | Ox-shaped layout |
| Ram | 136 | 8/10 | Ram horn layout |

## E-ink Optimization

This app is specifically designed for e-ink displays:

- **High Contrast**: Pure black (#000000) on pure white (#FFFFFF)
- **No Animations**: All animations disabled to prevent ghosting
- **No Transitions**: Instant screen updates
- **Simple Graphics**: Minimal use of gradients or complex shapes
- **Hardware Acceleration Disabled**: Prevents rendering artifacts on some e-ink devices

## Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

### Adding New Layouts

To add a new layout:

1. Add the layout creation method to `LayoutCatalog.java`
2. Register it in `initializeLayouts()`
3. Update the README with the layout information

### Code Style

- Follow Java conventions
- Add Javadoc comments for public methods
- Write unit tests for new functionality
- Keep methods small and focused

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## Acknowledgments

- Tile designs inspired by traditional Mahjong sets
- Layout patterns based on classic Mahjong Solitaire games
- Built with open-source libraries from the Android community

## Support

For bug reports and feature requests, please use the [GitHub Issues](../../issues) page.
