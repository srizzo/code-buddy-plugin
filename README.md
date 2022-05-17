# CodeBuddy Plugin for IntelliJ

![Build](https://github.com/srizzo/code-buddy-plugin/workflows/Build/badge.svg)
[![Version](https://img.shields.io/jetbrains/plugin/v/io.github.srizzo.codebuddy.code-buddy-plugin.svg)](https://plugins.jetbrains.com/plugin/io.github.srizzo.codebuddy.code-buddy-plugin)
[![Downloads](https://img.shields.io/jetbrains/plugin/d/io.github.srizzo.codebuddy.code-buddy-plugin.svg)](https://plugins.jetbrains.com/plugin/io.github.srizzo.codebuddy.code-buddy-plugin)

<!-- Plugin description -->
Enhanced text editing capabilities, inspired by TextMate.

Install from https://plugins.jetbrains.com/plugin/16252-codebuddy.

### Headless Find &amp; Replace (no Editor Search Controls)

- Use Selection for Find - &#x2318;E<sup>1</sup>
- Use Selection for Replace - &#x21E7;&#x2318;E<sup>1</sup>
- Find All - &#x21E7;&#x2318;F<sup>1</sup>
- Replace - &#x2325;&#x2318;G<sup>1</sup>
- Replace All in Selection - &#x2303;&#x21E7;&#x2318;G<sup>1</sup>

### Text Selection

- Select Paragraph - &#x2303;&#x2325;P<sup>1</sup>
- Select Enclosing Typing Pairs - &#x21E7;&#x2318;B<sup>1</sup>

### Text Manipulation

- Twiddle - &#x2303;T - Twiddle/swap/cycle between selected words<sup>1</sup>

### CodeBuddy Keymap

- TextMate inspired shortcuts

### Enhanced/Quick Column Selection Mode

Activate on Settings | Editor | CodeBuddy

- When text is selected, press &#x2325; to toggle between Selection &rarr; Block Selection
- Crosshair cursor when Column Selection Mode is active
- Exit Column Selection Mode by pressing arrow up, down, &#x2303;, &#x21E7;, or &#x2318;.
- Press arrow up or down to return to single cursor

<footer>
    <small><sup>1</sup> When using the CodeBuddy Keymap</small>
</footer>
<!-- Plugin description end -->

## Installation

- From the marketplace:

  https://plugins.jetbrains.com/plugin/16252-codebuddy

- Using IDE built-in plugin system:

  <kbd>Settings/Preferences</kbd> > <kbd>Plugins</kbd> > <kbd>Marketplace</kbd> > <kbd>Search for "CodeBuddy"</kbd> >
  <kbd>Install Plugin</kbd>

- Manually:

  Download the [latest release](https://github.com/srizzo/code-buddy-plugin/releases/latest) and install it manually using
  <kbd>Settings/Preferences</kbd> > <kbd>Plugins</kbd> > <kbd>⚙️</kbd> > <kbd>Install plugin from disk...</kbd>


---
Plugin based on the [IntelliJ Platform Plugin Template][template].

[template]: https://github.com/JetBrains/intellij-platform-plugin-template
