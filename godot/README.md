# Till The End Godot Prototype

This folder contains the Phase 1 Godot prototype for the upgraded Till The End district defense game.

## Requirements

- Godot 4.x
- `godot` executable available on `PATH`

This worktree also includes a local Godot executable at `.tools/godot-4.6.2/` if `godot` is not on `PATH`.

## Run

From the repository root:

```powershell
godot --path godot
```

If `godot` is not on `PATH`, use the local executable:

```powershell
& '.\.tools\godot-4.6.2\Godot_v4.6.2-stable_win64_console.exe' --path godot
```

## Test

From the repository root:

```powershell
godot --headless --path godot -s res://tests/test_runner.gd
```

If `godot` is not on `PATH`, use the local executable:

```powershell
& '.\.tools\godot-4.6.2\Godot_v4.6.2-stable_win64_console.exe' --headless --path godot -s res://tests/test_runner.gd
```

## Current Play Loop

1. Open the game.
2. Click `New Defense`.
3. Click clear green tiles near the road to build towers.
4. Click `Start Raid`.
5. Click `Next Turn` to advance enemy movement and tower fire.
6. Win the raid by defeating all enemies before the city core falls.
