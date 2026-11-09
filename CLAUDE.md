# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project

PingPlayer is a Paper/Folia/Purpur plugin (Minecraft 26.3, Java 25) that provides `/ping`, `/ip` and `/pingplayer` commands and shows color-coded ping in the tab list. Single Maven module, package `me.honeyberries.pingPlayer`. There are no tests and no linter.

## Commands

- Build: `mvn clean package` → `target/pingplayer-<version>.jar` (maven-shade runs; `target/original-*.jar` is the unshaded one).
- There is no run target; test by dropping the jar into a Paper server's `plugins/` folder.

## Architecture

- `PingPlayer` (main class) wires everything in `onEnable`: loads `PingSettings`, registers the three command executors, and starts a repeating `TabUpdateTask` every tick via `Scheduler`. `PingPlayer.getInstance()` is used as a global accessor (`getPlugin(...)`), and many classes hold it in a field.
- `Scheduler` / `Task` wrap Bukkit and Folia schedulers behind one API so the plugin runs on both. Always schedule through `Scheduler`, not `Bukkit.getScheduler()` directly. Note that the "async" helpers on Folia currently use the global region scheduler.
- `PingSettings` is a singleton that owns `config.yml`. `load()` reads values with fallbacks, sorts the four ping thresholds, then writes the normalized values back to disk (`syncConfiguration`), so user edits and comments in the config get rewritten on every load/reload. Thresholds are a sorted 4-element list (excellent, good, medium, bad) shared by tab coloring and command output.
- `TabUpdateTask` sets `playerListName` for every online player with `pingplayer.viewping`, only when `show-ping-on-tab` is true.

## Config and permissions

Adding a command or permission means editing `src/main/resources/plugin.yml` as well as the Java code. `plugin.yml` is Maven-filtered (`filtering` is on in `pom.xml`), and its `version` and `api-version` are set by hand. Permissions are `pingplayer.ping`, `.ip`, `.settings` and `.viewping`. See README.md for the full command and permission tables.

## Releases

Pushing to `main` triggers `.github/workflows/publish.yml`. It reads the version from `pom.xml`, skips if a GitHub release `v<version>` already exists, and otherwise publishes to Modrinth (project `4mxNaSgs`, slug `ping-players`) with `Kir-Antipov/mc-publish@v3` and then creates the GitHub release. Bump the version in **both** `pom.xml` and `plugin.yml` to release. The Modrinth step needs a `MODRINTH_TOKEN` repo secret; without it the action only warns, and the GitHub release is still created (so Modrinth gets silently skipped). Licensed GPL-3.0.
