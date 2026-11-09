# PingPlayer

[![build](https://github.com/HoneyBerries/PingPlayer/actions/workflows/build.yml/badge.svg)](https://github.com/HoneyBerries/PingPlayer/actions/workflows/build.yml)
[![License: GPL v3](https://img.shields.io/badge/License-GPLv3-blue.svg)](LICENSE)

A lightweight Paper plugin for Minecraft **26.3** that lets players and admins check latency and IP addresses, and shows each player's ping in the tab list.

Available on [Modrinth](https://modrinth.com/plugin/ping-players).

## Features

- **Ping command** – check your own ping or another player's with `/ping [player]`
- **IP command** – view an online player's IP address with `/ip <player>`
- **Tab list ping** – ping is shown next to each name, color-coded by quality (toggleable)
- **Configurable thresholds** – tune what counts as excellent, good, medium or bad
- **Permission based** – every feature has its own permission node
- **Folia supported**

## Compatibility

| Requirement | Version |
|-------------|---------|
| Minecraft | 26.3 |
| Server software | Paper and forks (Folia, Purpur) |
| Java | 25 |

## Commands

| Command | Aliases | Permission | Description |
|---------|---------|------------|-------------|
| `/ping [player]` | | `pingplayer.ping` | Check your own or another player's ping |
| `/ping help` | | `pingplayer.ping` | Show ping command help |
| `/ip <player>` | `/getip`, `/ipaddress` | `pingplayer.ip` | View a player's IP address |
| `/ip help` | | `pingplayer.ip` | Show IP command help |
| `/pingplayer reload` | | `pingplayer.settings` | Reload the configuration |
| `/pingplayer help` | | `pingplayer.settings` | Show admin command help |

## Permissions

| Permission | Default | Description |
|------------|---------|-------------|
| `pingplayer.ping` | everyone | Use `/ping` |
| `pingplayer.ip` | op | Use `/ip` |
| `pingplayer.settings` | op | Use `/pingplayer` admin commands |
| `pingplayer.viewping` | everyone | See ping values in the tab list |

## Configuration

`plugins/PingPlayer/config.yml` is generated on first start. See [`config.yml`](src/main/resources/config.yml) for the defaults and every option; apply changes with `/pingplayer reload`.

Ping colors in the tab list and command output:

| Color | Quality |
|-------|---------|
| Green | Excellent |
| Yellow | Good |
| Gold | Medium |
| Red | Bad |
| Dark red | Terrible |

## Installation

1. Download the latest jar from [Modrinth](https://modrinth.com/plugin/ping-players) or [GitHub Releases](https://github.com/HoneyBerries/PingPlayer/releases).
2. Put it in your server's `plugins` folder.
3. Start the server.
4. Edit `plugins/PingPlayer/config.yml` if you want, then run `/pingplayer reload`.

## Building from source

Requires JDK 25 and Maven.

```sh
git clone https://github.com/HoneyBerries/PingPlayer.git
cd PingPlayer
mvn clean package
```

The jar is written to `target/pingplayer-<version>.jar`.

## Releases

Pushing to `main` runs the [publish workflow](.github/workflows/publish.yml). If the version in `pom.xml` has not been released yet, it builds the plugin, publishes it to Modrinth and creates a GitHub release. Bump the version in `pom.xml` and `plugin.yml` to cut a new release.

## Contributing

Issues and pull requests are welcome.

## License

PingPlayer is licensed under the [GNU General Public License v3.0](LICENSE).
