# Till The End Godot Prototype

This folder contains the Phase 1 Godot prototype for the upgraded Till The End district defense game.

## Requirements

- Godot 4.x
- `godot` executable available on `PATH`

If you downloaded Godot locally, the ignored `.tools/godot-4.6.2/` path is a convenient place to keep the executable when `godot` is not on `PATH`.

## Run

From the repository root:

```powershell
godot --path godot
```

If `godot` is not on `PATH` and you have downloaded Godot into `.tools/godot-4.6.2/`, use:

```powershell
& '.\.tools\godot-4.6.2\Godot_v4.6.2-stable_win64_console.exe' --path godot
```

## Test

From the repository root:

```powershell
godot --headless --path godot -s res://tests/test_runner.gd
```

If `godot` is not on `PATH` and you have downloaded Godot into `.tools/godot-4.6.2/`, use:

```powershell
& '.\.tools\godot-4.6.2\Godot_v4.6.2-stable_win64_console.exe' --headless --path godot -s res://tests/test_runner.gd
```

## Current Play Loop

1. Open the game.
2. Click `New Defense`.
3. Click clear green tiles near the road to build towers.
4. Click `Start Raid`.
5. Click `Next Turn` to advance enemy movement and tower fire.
6. Win the raid and earn the reward by defeating all enemies before they breach the city core.
7. If enemies breach the core but it survives, the game returns to planning without paying a raid reward.
