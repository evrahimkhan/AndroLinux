# eDEX-UI Inspired Android Launcher with Embedded Termux: Pure Native Architecture Plan

## Context
The user wants to build a custom Android launcher from scratch.
- **UI/UX Design**: Futuristic sci-fi eDEX-UI style built using **Pure Native Android UI (Jetpack Compose)** for high performance, smooth animations, and robust Android home screen integration.
- **Core Terminal Engine**: Fully embedded Termux CLI environment inside the launcher app (no external Termux installation required). Official Termux support (bootstrap rootfs, package management via `pkg`/`apt`, and native PTY execution).
- **Reference & Analysis**: Use `rea` (Ghidra / binary analysis) to extract features and reference source code from `edex-ui` and `termux-app`.
- **Development Tools**: ECC plugins for planning, design, implementation, build, and verification. Superdesign for UI/UX design, Superpowers for coding.

---

## Architecture & Technical Approach

### 1. Pure Native eDEX-UI in Jetpack Compose
- **Sci-Fi HUD & Dashboard**: Compose custom Canvas rendering for futuristic circular gauges, system stats (CPU, RAM, Battery, Network), and glowing terminal borders.
- **Terminal Emulator View**: Integrate `termux-shared` and `terminal-view` libraries directly into Compose via `AndroidView` wrapping `TermuxTerminalView`.
- **App Drawer & Home Screen**: Standard Android `Launcher` implementation (`android.intent.action.MAIN`, `android.intent.category.HOME`), grid-based app drawer with instant search and category filtering.

### 2. Embedded Termux Engine without External APK
- **Bootstrap Management**: Automatically extract the official Termux bootstrap zip (`bootstrap-arm64.zip`) into app-internal storage (`/data/data/com.android.edexlauncher/files/usr`) upon first launch.
- **Terminal Session & PTY**: Use Termux's native process execution and JNI `libtermux-exec` / `forkpty` to run shells (`bash`/`zsh`) securely inside the app sandbox.

---

## Step-by-Step Implementation Plan

1. **Project Scaffolding**: Create Android Gradle project with Jetpack Compose and Kotlin DSL.
2. **Termux Core Embedding**: Bundle Termux bootstrap assets, implement native bootstrap extractor and PTY terminal session manager.
3. **Pure Native eDEX-UI Design**: Build Jetpack Compose components mirroring eDEX-UI themes (neon accents, system telemetry rings, tabs, virtual keyboard).
4. **Launcher Home Integration**: Set up intent filters for Home Launcher, app drawer, and widget support.
5. **Verification & Testing**: Use ECC build and review plugins to ensure stability and performance.
