# CLAUDE.md — eDEX-UI Android Launcher with Embedded Termux

## Build & Development Commands
- **Build Android App**: `./gradlew assembleDebug`
- **Run Tests**: `./gradlew test`
- **Lint Check**: `./gradlew lint`

## Project Overview
An Android launcher built from scratch featuring:
1. **Pure Native eDEX-UI (Jetpack Compose)**: Sci-fi HUD, real-time system monitoring gauges, terminal emulator views, and app drawer.
2. **Embedded Termux CLI Engine**: Self-contained Termux bootstrap rootfs and PTY process execution without requiring external Termux installation.
