---
name: intellij-version-upgrade
description: Update this repository to support a new IntelliJ IDEA or platform release. Use this whenever the user asks to add support for a newer IDE version, bump the IntelliJ target, update plugin compatibility metadata, or prepare the repo for a new IntelliJ release. Also use it when the work likely involves `platformVersion`, `pluginSinceBuild`, `CHANGELOG.md`, release steps, or IntelliJ Platform Gradle plugin upgrades.
---

# IntelliJ Version Upgrade

Use this skill to update this repository for a new IntelliJ IDEA release with minimal churn and strong validation.

This repo has an established pattern for compatibility bumps: start with metadata changes, verify the build, and only expand into build-tooling or CI changes when the new IDE version actually requires them.

## Repository-specific expectations

- Main compatibility knobs live in `gradle.properties`:
  - `pluginVersion`
  - `pluginSinceBuild`
  - `platformVersion`
- The Gradle build already reads those values in `build.gradle.kts`, so prefer changing properties rather than rewriting build logic.
- Release notes belong in `CHANGELOG.md`, usually under `[Unreleased]` as `Compatibility with IntelliJ <version>`.
- Release flow reference is in `RELEASE.md`.
- Common validation commands are:
  - `./gradlew build`
  - `./gradlew check`
  - `./gradlew verifyPlugin`
  - optional: `./gradlew runIde`

## Workflow

### 1. Inspect the current state

Read these files first:

- `gradle.properties`
- `gradle/libs.versions.toml`
- `build.gradle.kts`
- `CHANGELOG.md`
- `RELEASE.md`
- `qodana.yml`
- `.github/workflows/build.yml`
- `.github/workflows/release.yml`
- `.github/workflows/run-ui-tests.yml` if present
- `CLAUDE.md` if present

Then inspect repo history for prior compatibility updates. Search recent commits and branches for terms like:

- `Compatibility with IntelliJ`
- `Compatibility adjustments`
- `platformType`
- `sinceBuild`

The goal is to recover the repo’s house style before making changes.

### 2. Confirm the exact IntelliJ build baseline

Do not guess `pluginSinceBuild`.

Look up the correct baseline build family for the requested IntelliJ release from JetBrains sources, then set `pluginSinceBuild` to that exact baseline. For example, IntelliJ IDEA 2026.1 maps to baseline `261`.

If you use web search, cite the source in your response.

### 3. Make the minimal compatibility update first

Start with the smallest likely change set:

- Update `gradle.properties`
  - bump `platformVersion`
  - bump `pluginSinceBuild`
  - bump `pluginVersion` to the next release version if this change is intended to ship
- Update `CHANGELOG.md`
  - add `Compatibility with IntelliJ <version>` under `[Unreleased]`

Keep `build.gradle.kts` unchanged unless verification proves the IntelliJ Platform Gradle plugin DSL or build wiring needs adjustment.

## Dependency and tooling updates

When a new IntelliJ release is involved, inspect open dependency-update PRs and current pinned versions, but keep the rule simple:

- Prefer updating dependencies that are already clearly part of the IntelliJ/tooling stack for this repo.
- Do not churn unrelated dependencies just because a new IDE version is being added.

### IntelliJ Platform Gradle plugin rule

Whenever you update `org.jetbrains.intellij.platform` in `gradle/libs.versions.toml`, you must check the release notes before finalizing the change.

Specifically:

1. Review release notes for the exact upgrade range.
2. Look only for actionable migration items:
   - minimum supported Gradle version changes
   - removed DSL aliases or deprecated APIs
   - required build script changes
   - sandbox path changes
   - plugin verification DSL changes
3. Apply required repo changes if they affect this repository.
4. If there are no required repo changes, say that explicitly in the final summary.

For this repo, note that an upgrade from `org.jetbrains.intellij.platform` 2.10.5 to 2.13.1 did **not** require additional repo changes beyond the version bump, but that fact is specific to this repo state and must be rechecked for future upgrades.

### Qodana and CI

Only update `qodana.yml`, workflow actions, Gradle wrapper, or Java/tool versions when one of these is true:

- the current build warns that the tool is outdated
- the new IntelliJ version breaks verification/building with current tooling
- there is an already-open dependency-update path that clearly belongs with the IDE upgrade

Good candidates in this repo:

- `gradle/libs.versions.toml`
- `qodana.yml`
- `.github/workflows/*.yml`
- `.java-version`
- `gradle/wrapper/gradle-wrapper.properties`

## Validation

After editing, run:

1. `./gradlew build`
2. `./gradlew verifyPlugin`
3. `./gradlew check` if needed for extra confidence or if other files changed
4. optionally `./gradlew runIde` for a sandbox smoke test

Treat `build` and `verifyPlugin` as the core gate.

If verification succeeds with deprecation or override-only API warnings, do not expand scope unless the user asked for cleanup. Report the warnings succinctly and leave them alone.

## Pull request and release behavior

If the user asks for a PR:

1. Check whether unrelated working-tree files exist.
2. Ask whether to include unrelated files if scope is ambiguous.
3. Create a focused branch.
4. Commit only the intended files.
5. Create a PR with:
   - a short title like `Add IntelliJ IDEA <version> support`
   - summary bullets for metadata/tooling updates
   - a test plan listing `./gradlew build` and `./gradlew verifyPlugin`
   - a note on the IntelliJ Platform Gradle plugin release-notes review if that plugin was bumped

### Full release workflow for this repository

When the user wants the whole change shipped, follow the release sequence from `RELEASE.md`:

1. Make sure `CHANGELOG.md` has the compatibility note under `[Unreleased]`.
2. Make sure `gradle.properties` has the new `pluginVersion`.
3. Open the PR and get it green.
4. Merge the PR into the repository's release branch/mainline used by the workflow.
5. Wait for GitHub Actions to create the draft GitHub Release.
6. Publish the GitHub Release (convert it from draft to released).
7. Wait for the automation to open the changelog PR created from the release flow.
8. Merge the changelog PR.

Important:

- Treat merge, release publication, and changelog-PR merge as separate high-impact actions. Confirm before doing them unless the user explicitly asked for unattended end-to-end execution.
- If branch naming differs (`main` vs `master`), follow the repository's current release workflow rather than hard-coding a branch name.
- After the PR is green, the release work is not finished until the draft release has been published and the changelog PR has been merged.

If the user asks for unattended execution, follow this workflow end-to-end unless a risky scope decision requires confirmation.

## Output checklist

When you finish, summarize:

- new `platformVersion`
- new `pluginSinceBuild`
- new `pluginVersion` if bumped
- any dependency/tooling changes made
- whether `build` passed
- whether `verifyPlugin` passed
- whether IntelliJ Platform Gradle plugin release notes required any repo changes
