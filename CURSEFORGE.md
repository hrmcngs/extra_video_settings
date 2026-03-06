# Extra Video Settings

Restores vanilla video settings that Embeddium removes, and adds the ability to save/load key bindings & video settings as profiles.

## Features

### Restored Video Settings (with Embeddium)

When Embeddium is installed, the following vanilla settings are added back to the General tab:

- **FOV Effects** - Controls FOV change with movement speed
- **Distortion Effects** - Nether portal / nausea screen distortion
- **Darkness Pulsing** - Deep Dark darkness effect intensity
- **Damage Tilt** - Camera tilt when taking damage
- **Glint Speed** - Enchantment glint animation speed
- **Glint Strength** - Enchantment glint intensity
- **Entity Shadows** - Toggle entity ground shadows

### Settings Profiles (Save & Load)

Save and load your key bindings and video settings to text files. Includes vanilla AND modded settings.

**GUI:** Open the Settings screen and click the "Profiles..." button next to "Done".

**Commands (with Tab completion):**

- `/evs save keys <name>` - Save all key bindings
- `/evs load keys <name>` - Load key bindings from profile
- `/evs save video <name>` - Save all video/options settings
- `/evs load video <name>` - Load video/options settings from profile
- `/evs list keys` / `/evs list video` - List saved profiles
- `/evs delete keys <name>` / `/evs delete video <name>` - Delete a profile

Profiles are saved as human-readable text files in `config/extra_video_settings/profiles/`. You can edit them manually or share them with others.

## Requirements

- Minecraft Forge 1.20.1
- Embeddium 0.1.0+ (optional - video settings restoration requires it, profiles work without it)
