# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

CrazyCat (围住神经猫) is an Android game where the player places obstacles on a hexagonal grid to trap a cat. Built with Java, Android SDK (minSdk 16, targetSdk 30), using legacy `android.support.v7` libraries (not AndroidX).

## Build & Development Commands

```bash
# Build debug APK (CI command)
./gradlew assembleDebug --stacktrace

# Build release APK
./gradlew assembleRelease

# Clean build
./gradlew clean

# Install debug APK to connected device/emulator
./gradlew installDebug
```

CI runs on GitHub Actions (`.github/workflows/android.yml`): `assembleDebug` on ubuntu-latest with JDK 11.

## Architecture

Single-module Gradle project (`:app`). All source under package `ndnu.tdy.CreazyCat` with three sub-packages:

- **Activity** — Three screens: `MainActivity` (splash) → `ChooseActivity` (mode select) → `GameActivity` (gameplay). Activities are thin; they set up music services and delegate rendering to `GameView`.

- **View** — `GameView.java` is the core (~520 lines). Extends `SurfaceView`, handles hexagonal grid rendering, cat sprite animation (16 frames at 65ms), cat AI movement (BFS distance evaluation across 6 hex directions), touch input for placing obstacles, and win/loss detection. `Point.java` is a data class for grid cells with status enum (`STATUS_OFF`/`STATUS_ON`/`STATUS_IN`).

- **Music** — Three nearly identical `Service` classes (`MusicServer`, `MusicServer2`, `MusicServer3`) each playing a different MP3 in a `:remote` process via `MediaPlayer`.

## Game Modes

| Mode | Flag | Grid | Obstacles | Special |
|------|------|------|-----------|---------|
| Simple | 1 | 12×12 | grid/5 | — |
| Normal | 2 | 9×9 | grid/6 | — |
| Hard | 3 | 8×8 | grid/7 | — |
| Timed | 4 | 8×8 | grid/4 | 10-second countdown |

## Key Technical Details

- **Hex grid**: Odd-row offset coordinate system. Neighbor calculations are inline in `GameView`.
- **Rendering**: Manual `Canvas` drawing via `SurfaceHolder` with `Timer`/`TimerTask` at ~15 FPS.
- **No architecture pattern**: All game logic, rendering, and state live in `GameView`. No MVP/MVVM.
- **No third-party libraries** beyond Android support libraries.
- **Package note**: The package is spelled `CreazyCat` (typo), not `CrazyCat`.
