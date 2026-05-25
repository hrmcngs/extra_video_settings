# Extra Video Settings

Restores vanilla video settings that **Embeddium / Sodium** hides, adds **save/load profiles** for key bindings & video settings, and lets you **mask player names** in chat, tab list, and nameplates.

Works on **Forge 1.20.1**, **NeoForge 1.20.2**, and **Fabric 1.20.1**.

***

## Restored Settings Options

<div><div><table><thead><tr><th style="text-align:center">Setting Name</th><th>Description</th></tr></thead><tbody><tr><td style="text-align:center">FOV Effects</td><td style="text-align:center">Adjusts how much the field of view (FOV) changes when gaining speed. Setting it to 0% removes the screen distortion effect.</td></tr><tr><td style="text-align:center">Distortion Effects</td><td style="text-align:center">Controls the intensity of screen distortion effects (such as Nether portals).</td></tr><tr><td style="text-align:center">Darkness Pulsing</td><td style="text-align:center">Adjusts the pulsing strength of the Darkness effect (e.g., in the Deep Dark).</td></tr><tr><td style="text-align:center">Damage Tilt</td><td style="text-align:center">Controls how much the screen tilts when taking damage.</td></tr><tr><td style="text-align:center">Glint Speed</td><td style="text-align:center">Changes the animation speed of enchanted item glint.</td></tr><tr><td style="text-align:center">Glint Strength</td><td style="text-align:center">Adjusts the brightness/intensity of the enchanted item glint.</td></tr><tr><td style="text-align:center">Entity Shadows</td><td style="text-align:center">Toggles the visibility of shadows under entities (mobs and players).</td></tr></tbody></table></div></div>

***

## Button Location

## <span style="font-size:14px"><strong>Options Menu</strong> → Click the <strong>"Vanilla Options"</strong> button</span>

## <span style="font-size:14px"><strong>Embeddium Video Settings Screen</strong> → Click the <strong>"Vanilla Options"</strong> button in the bottom-right corner</span>

***

## Settings Profiles (Save & Load)

Save and load your **key bindings** and **video / options** as named profiles. Covers vanilla *and* modded settings, so you can roundtrip an entire setup across worlds, machines, or installs.

### GUI

Open the **Options** screen and click the **"Profiles..."** button (placed directly below the option grid). Enter a name, then save / load / delete either keys or video. If the layout overflows on a small window, scroll with the mouse wheel — bounded so you can't scroll past the edges.

### Commands (with Tab completion)

| Command | Action |
|---|---|
| `/evs save keys <name>` | Save all key bindings |
| `/evs load keys <name>` | Load key bindings from profile |
| `/evs save video <name>` | Save all video / options |
| `/evs load video <name>` | Load video / options from profile |
| `/evs list keys` &nbsp;/&nbsp; `/evs list video` | List saved profiles |
| `/evs delete keys <name>` &nbsp;/&nbsp; `/evs delete video <name>` | Delete a profile |

Profiles are plain text files in `config/extra_video_settings/profiles/`. **Edit them by hand**, drop them into other installs, or share with friends.

***

## Player Name Masking

Hide player names for screenshots, streaming, or just keeping screenshots out of search. Masks names everywhere they appear: **chat**, **death messages**, **command output**, **tab list**, and **nameplates above players**.

### Modes

- **OFF** — show the real name (default)
- **BLACKOUT** — replace the name with █ blocks of the same length
- **OBFUSCATED** — keep the original text but apply the vanilla §k garbled-font effect

You can set **different modes for yourself vs. other players** — for example, OBFUSCATED for everyone else but BLACKOUT for your own name.

### Setup

Edit `config/extra_video_settings/name_mask.txt`:

```
others=OBFUSCATED
self=BLACKOUT
```

Restart the game (or rejoin) and your name + everyone else's will be masked across all UI surfaces.

***

## Requirements

- **Forge 1.20.1** — works standalone; Embeddium 0.1.0+ is optional (the vanilla-settings restoration only activates when Embeddium is present)
- **NeoForge 1.20.2** — works standalone, no optimisation mod required
- **Fabric 1.20.1** — requires **Fabric API**; Sodium / Iris are optional

***

## Notes

- All three loader builds share a common codebase; behaviour is identical across Forge / NeoForge / Fabric.
- Profiles are forward-compatible — a profile saved on one loader loads fine on another (same vanilla key/option IDs).
- Name masking is **client-side only** — other players still see your real name; this only affects what *you* see on *your* screen.
