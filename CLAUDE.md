# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Build, test, and run commands

- `./gradlew build` — full plugin build.
- `./gradlew check` — main local verification path; runs tests plus configured checks. Matches `.run/Run Tests.run.xml`.
- `./gradlew verifyPlugin` — JetBrains plugin verification against recommended IDEs. Matches `.run/Run Verifications.run.xml`.
- `./gradlew test` — run the test suite.
- `./gradlew test --tests 'io.github.srizzo.codebuddy.twiddle.TwiddleActionTest'` — run one test class.
- `./gradlew test --tests 'io.github.srizzo.codebuddy.twiddle.TwiddleActionTest.testTwiddleAction'` — run one test method.
- `./gradlew runIde` — launch a sandbox IDE with the plugin installed. Matches `.run/Run Plugin.run.xml`.
- `./gradlew runIdeForUiTests` — launch IDE configured for UI tests / robot server on port 8082.

## Architecture overview

This is an IntelliJ Platform plugin called `CodeBuddy`, targeting IntelliJ IDEA 2025.3+ (`gradle.properties`). The plugin is registered through `src/main/resources/META-INF/plugin.xml`, which wires together actions, application services, a bundled macOS keymap, the settings page, and a post-startup activity.

### Main feature areas

- `findreplace/`: headless find/replace actions that operate on the current editor selection and update IntelliJ `FindModel`s instead of opening the usual search UI.
- `select/`: editor selection helpers such as paragraph selection and enclosing typing-pair selection.
- `twiddle/`: multi-caret text rotation/swapping behavior.
- `columnmode/`: temporary column-selection mode behavior, including modifier-key driven entry/exit, single-caret restoration, and cursor updates.
- `settings/`: persisted application-level plugin settings shown under Editor > CodeBuddy.
- `util/`: shared editor/action helpers used across features.

### How column mode works

Column mode is the most cross-cutting part of the plugin.

- `ColumnModeStartupActivity` installs a global `IdeEventQueue` dispatcher after project startup.
- That dispatcher watches raw `KeyEvent`s and conditionally triggers IntelliJ actions by ID through `RunActionUtil`.
- Behavior is gated by booleans in `CodeBuddySettingsState`, which is an application-level `PersistentStateComponent` stored in `CodeBuddySettingsPlugin.xml`.
- `BlockSelectionUtil` contains the core selection/block-selection conversion logic and platform-specific modifier detection (`Alt` on macOS, `Ctrl` elsewhere).
- `CrosshairCursorOnColumnModeUpdater` and related actions provide the visual/editor-state side of the feature.

When editing column mode behavior, check both the event interception path and the action implementations; the feature is split between startup wiring, settings flags, and editor mutation utilities.

### Actions and IDs

Most user-facing behavior is exposed as IntelliJ actions declared in `plugin.xml`. Tests and internal code often invoke actions by ID or by action class constant rather than by directly calling helper methods, so keep action IDs stable unless you also update tests and registrations.

### Testing approach

Tests are mostly IntelliJ fixture tests based on `BasePlatformTestCase` via `src/test/java/io/github/srizzo/codebuddy/test/BaseTestCase.java`. Test data for editor-behavior tests lives under `src/test/testData/`. Some newer tests are simpler instantiation/assertion tests that do not use fixtures.

When changing editor actions, check whether the existing test style is fixture-based with before/after files or a lighter unit-style test, and extend the matching style instead of introducing a third pattern.

## Project-specific notes

- The Gradle build extracts the plugin description from the `<!-- Plugin description --> ... <!-- Plugin description end -->` block in `README.md`; keep that block valid when editing plugin metadata.
- Plugin compatibility is driven by `pluginSinceBuild=252` and `platformVersion=2025.3` in `gradle.properties`.
- The repository uses the IntelliJ Platform Gradle Plugin 2.x, plus Changelog, Qodana, and Kover plugins (`gradle/libs.versions.toml`).
