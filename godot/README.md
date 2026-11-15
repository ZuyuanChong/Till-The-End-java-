# Till The End Godot Prototype

This folder contains the Phase 2 seasonal campaign for Till The End district defense.

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
2. Select Normal or Hard, then click `New Defense`.
3. In Build mode, click open tiles to place a tower for 100 gold. Click a tower to select it and see its Manhattan range.
4. Upgrade selected towers for 75 gold, up to level 3. Inspect mode selects tiles without building.
5. Repair the city wall for 50 gold, fortify it for 100 gold, or rally citizens for 50 gold.
6. Click `Start Raid`, then `Next Turn` to resolve movement and tower fire.
7. A clean defense earns 75 gold. Surviving a breach earns no raid reward. Both outcomes advance the season and collect tax once.
8. Read the new seasonal events and plan the next defense. Winter rolls over to Spring in the next year.

## Campaign Rules

- Normal tax starts at 80 gold. Hard tax starts at 50 gold. Morale at 70 or higher adds 20, and morale at 30 or lower subtracts 20.
- Hard starts with four enemies at 7 HP and two events each season. Normal starts with three enemies at 5 HP and one event.
- Each later year adds one enemy and 2 enemy HP. Wave size caps at 12.
- Rain reduces tower damage for its season. Seasonal modifiers expire at the next transition.
- Wall fortification increases the city core's defensive health. Spatial walls and rerouting are part of the next development phase.
- Dragon threat increases over the campaign and caps at 100. Boss encounters, save/load, and final art remain in later phases.
- Reset retains difficulty and starts a fresh campaign with a new event seed.
