# PingPlayer

PingPlayer is a lightweight Minecraft plugin that allows players and server administrators to check ping (latency) information and IP addresses of online players.

## Features

- **Ping Command**: Check your own ping or ping of other players with `/ping [player]`
- **IP Command**: View the IP address of online players with `/ip <player>`
- **Tablist Integration**: Automatically displays player ping in the tab list with color coding
- **Configurable Thresholds**: Customize ping quality thresholds to match your server's needs

## Commands

| Command | Permission | Description |
|---------|------------|-------------|
| `/ping` | `pingplayer.ping` | Check your own ping |
| `/ping <player>` | `pingplayer.ping` | Check another player's ping |
| `/ping help` | `pingplayer.ping` | Display help information for the ping command |
| `/ip <player>` | `pingplayer.ip` | View a player's IP address |
| `/ip help` | `pingplayer.ip` | Display help information for the IP command |
| `/pingplayer reload` | `pingplayer.settings` | Reload the plugin configuration |
| `/pingplayer help` | `pingplayer.settings` | Display admin command help |

## Permissions

| Permission | Description |
|------------|-------------|
| `pingplayer.ping` | Allows using the `/ping` command |
| `pingplayer.ip` | Allows using the `/ip` command |
| `pingplayer.settings` | Allows using the `/pingplayer` administrative commands |
| `pingplayer.viewping` | Allows viewing the ping of another player on the tab list |

## Configuration

The plugin creates a `config.yml` file with customizable ping thresholds:

```yaml
ping-thresholds:
  excellent: 50   # 0-50ms = Excellent (Green)
  good: 100       # 51-100ms = Good (Yellow)
  medium: 200     # 101-200ms = Ok (Gold)
  bad: 300        # 201-300ms = Bad (Red)
                  # 301+ms = Terrible (Dark Red)
```

## Installation

1. Download the latest version of PingPlayer from [GitHub Releases](https://github.com/yourusername/PingPlayer/releases)
2. Place the JAR file in your server's `plugins` folder
3. Restart your server or use a plugin manager to load the plugin
4. Modify the configuration in `plugins/PingPlayer/config.yml` if needed
5. Use `/pingplayer reload` to apply configuration changes

## Tablist Display

The plugin automatically updates the tab list to show each player's ping with a color indicator:
- Green: Excellent ping
- Yellow: Good ping
- Red: Medium ping
- Dark Red: Bad ping

## Building from Source

1. Clone the repository: `git clone https://github.com/yourusername/PingPlayer.git`
2. Navigate to the project directory: `cd PingPlayer`
3. Build with Maven: `mvn clean package`
4. Find the compiled JAR in the `target` directory

## Contributing

Contributions are welcome! Feel free to open issues or submit pull requests on GitHub.

## License
This project is licensed under the MIT License
