# Till The End Godot Prototype Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Build the Phase 1 playable Godot prototype for Till The End as a tile-grid district defense game.

**Architecture:** Preserve the existing Java project untouched and add a new Godot project under `godot/`. Keep game rules in small GDScript classes that can be tested headlessly, then connect them to a simple playable scene with menu, build interaction, raid turns, and win/loss state.

**Tech Stack:** Godot 4, GDScript, built-in Godot UI nodes, custom headless test runner, Git.

---

## Scope

This plan implements Phase 1 only: a working vertical slice with one city map, buildable towers, a fixed road route, basic gold economy, raid start, enemy movement, tower attacks, raid victory, and city core loss. Seasons, morale, district effects, dragon boss behavior, save/load, audio, and final pixel art are intentionally outside this first implementation plan and remain in the approved design spec for later plans.

## File Structure

- Create `godot/project.godot`: Godot project configuration.
- Create `godot/scenes/main_menu.tscn`: main menu scene.
- Create `godot/scenes/city_defense.tscn`: playable prototype scene.
- Create `godot/scripts/ui/main_menu.gd`: menu button behavior.
- Create `godot/scripts/ui/city_defense_screen.gd`: prototype rendering, input, HUD, and button wiring.
- Create `godot/scripts/game/game_state.gd`: saveable gameplay state and build/raid setup rules.
- Create `godot/scripts/game/turn_manager.gd`: deterministic raid turn advancement.
- Create `godot/scripts/systems/combat_resolver.gd`: tower target selection and damage application.
- Create `godot/scripts/entities/enemy_unit.gd`: enemy state.
- Create `godot/scripts/entities/defense_tower.gd`: tower state.
- Create `godot/tests/test_runner.gd`: no-dependency headless test runner.
- Create `godot/tests/test_game_state.gd`: state initialization tests.
- Create `godot/tests/test_building.gd`: tower build economy tests.
- Create `godot/tests/test_raid_turns.gd`: raid turn resolution tests.
- Create `godot/tests/test_scene_loads.gd`: scene load smoke tests.
- Create `godot/README.md`: run and test instructions.

## Commands

Run tests from the repository root:

```powershell
godot --headless --path godot -s res://tests/test_runner.gd
```

Run the game from the repository root:

```powershell
godot --path godot
```

If `godot` is not on `PATH`, run the same commands with the installed Godot executable path.

---

### Task 1: Create Godot Project and Test Harness

**Files:**
- Create: `godot/project.godot`
- Create: `godot/tests/test_runner.gd`

- [ ] **Step 1: Verify Godot CLI is available**

Run:

```powershell
godot --version
```

Expected: prints a Godot 4 version string. If the command is missing, install Godot 4 or add the executable to `PATH` before continuing.

- [ ] **Step 2: Create the Godot project configuration**

Create `godot/project.godot`:

```ini
; Engine configuration file.
config_version=5

[application]
config/name="Till The End: District Defense Prototype"
run/main_scene="res://scenes/main_menu.tscn"

[display]
window/size/viewport_width=1280
window/size/viewport_height=720
window/stretch/mode="canvas_items"
window/stretch/aspect="keep"

[rendering]
textures/canvas_textures/default_texture_filter=0
```

- [ ] **Step 3: Create the headless test runner**

Create `godot/tests/test_runner.gd`:

```gdscript
extends SceneTree

var failures := 0
var assertions := 0

func _init() -> void:
    run_all_tests()
    if failures == 0:
        print("ALL TESTS PASSED: %d assertions" % assertions)
        quit(0)
    else:
        push_error("TESTS FAILED: %d failures across %d assertions" % [failures, assertions])
        quit(1)

func run_all_tests() -> void:
    var dir := DirAccess.open("res://tests")
    if dir == null:
        fail("Cannot open res://tests")
        return

    var files := dir.get_files()
    files.sort()

    for file_name in files:
        if not file_name.begins_with("test_"):
            continue
        if not file_name.ends_with(".gd"):
            continue
        if file_name == "test_runner.gd":
            continue

        var script := load("res://tests/%s" % file_name)
        if script == null:
            fail("Could not load %s" % file_name)
            continue

        print("Running %s" % file_name)
        var test_instance := script.new()
        test_instance.run(self)

func assert_true(value: bool, message: String) -> void:
    assertions += 1
    if not value:
        fail(message)

func assert_false(value: bool, message: String) -> void:
    assertions += 1
    if value:
        fail(message)

func assert_eq(actual: Variant, expected: Variant, message: String) -> void:
    assertions += 1
    if actual != expected:
        fail("%s | expected=%s actual=%s" % [message, str(expected), str(actual)])

func fail(message: String) -> void:
    failures += 1
    push_error(message)
```

- [ ] **Step 4: Run the empty test suite**

Run:

```powershell
godot --headless --path godot -s res://tests/test_runner.gd
```

Expected: `ALL TESTS PASSED: 0 assertions`.

- [ ] **Step 5: Commit**

```powershell
git add godot/project.godot godot/tests/test_runner.gd
git commit -m "chore: scaffold godot prototype"
```

---

### Task 2: Add Core Game State

**Files:**
- Create: `godot/tests/test_game_state.gd`
- Create: `godot/scripts/game/game_state.gd`

- [ ] **Step 1: Write the failing game state test**

Create `godot/tests/test_game_state.gd`:

```gdscript
extends RefCounted

const GameState = preload("res://scripts/game/game_state.gd")

func run(t) -> void:
    var state := GameState.new_game()

    t.assert_eq(state.gold, 200, "new game starts with original-game-inspired gold")
    t.assert_eq(state.core_hp, 30, "city core starts alive")
    t.assert_eq(state.phase, GameState.Phase.PLANNING, "new game starts in planning phase")
    t.assert_eq(state.road_path[0], Vector2i(0, 4), "road starts at west spawn")
    t.assert_eq(state.city_core_position, Vector2i(15, 4), "core is on east edge")
    t.assert_true(state.is_road(Vector2i(3, 4)), "road tile is recognized")
    t.assert_false(state.is_buildable(Vector2i(3, 4)), "road tile cannot receive towers")
    t.assert_true(state.is_buildable(Vector2i(3, 3)), "clear adjacent tile can receive towers")
```

- [ ] **Step 2: Run test to verify it fails**

Run:

```powershell
godot --headless --path godot -s res://tests/test_runner.gd
```

Expected: FAIL because `res://scripts/game/game_state.gd` does not exist.

- [ ] **Step 3: Implement minimal game state**

Create `godot/scripts/game/game_state.gd`:

```gdscript
class_name GameState
extends RefCounted

enum Phase {
    PLANNING,
    RAID,
    VICTORY,
    GAME_OVER
}

const MAP_WIDTH := 16
const MAP_HEIGHT := 10
const TOWER_COST := 100
const RAID_REWARD := 75

var phase: Phase = Phase.PLANNING
var gold := 200
var core_hp := 30
var command_points := 0
var command_points_per_turn := 2
var raid_turn := 0
var year := 1
var season_index := 0
var city_core_position := Vector2i(15, 4)
var road_path: Array[Vector2i] = []
var towers: Array = []
var enemies: Array = []
var next_tower_id := 1
var next_enemy_id := 1

static func new_game() -> GameState:
    return GameState.new()

func _init() -> void:
    reset()

func reset() -> void:
    phase = Phase.PLANNING
    gold = 200
    core_hp = 30
    command_points = 0
    command_points_per_turn = 2
    raid_turn = 0
    year = 1
    season_index = 0
    city_core_position = Vector2i(15, 4)
    road_path = [
        Vector2i(0, 4),
        Vector2i(1, 4),
        Vector2i(2, 4),
        Vector2i(3, 4),
        Vector2i(4, 4),
        Vector2i(5, 4),
        Vector2i(6, 4),
        Vector2i(7, 4),
        Vector2i(8, 4),
        Vector2i(9, 4),
        Vector2i(10, 4),
        Vector2i(11, 4),
        Vector2i(12, 4),
        Vector2i(13, 4),
        Vector2i(14, 4),
        city_core_position
    ]
    towers = []
    enemies = []
    next_tower_id = 1
    next_enemy_id = 1

func is_in_bounds(grid_position: Vector2i) -> bool:
    return (
        grid_position.x >= 0
        and grid_position.y >= 0
        and grid_position.x < MAP_WIDTH
        and grid_position.y < MAP_HEIGHT
    )

func is_road(grid_position: Vector2i) -> bool:
    return road_path.has(grid_position)

func is_core(grid_position: Vector2i) -> bool:
    return grid_position == city_core_position

func is_buildable(grid_position: Vector2i) -> bool:
    if not is_in_bounds(grid_position):
        return false
    if is_road(grid_position):
        return false
    if is_core(grid_position):
        return false
    return true
```

- [ ] **Step 4: Run test to verify it passes**

Run:

```powershell
godot --headless --path godot -s res://tests/test_runner.gd
```

Expected: PASS with 8 assertions.

- [ ] **Step 5: Commit**

```powershell
git add godot/scripts/game/game_state.gd godot/tests/test_game_state.gd
git commit -m "feat: add prototype game state"
```

---

### Task 3: Add Tower Building and Economy

**Files:**
- Create: `godot/tests/test_building.gd`
- Create: `godot/scripts/entities/defense_tower.gd`
- Modify: `godot/scripts/game/game_state.gd`

- [ ] **Step 1: Write the failing building test**

Create `godot/tests/test_building.gd`:

```gdscript
extends RefCounted

const GameState = preload("res://scripts/game/game_state.gd")

func run(t) -> void:
    var state := GameState.new_game()
    var build_position := Vector2i(3, 3)

    t.assert_true(state.build_tower(build_position), "can build tower on clear tile")
    t.assert_eq(state.gold, 100, "building tower spends gold")
    t.assert_eq(state.towers.size(), 1, "tower list receives built tower")
    t.assert_eq(state.towers[0].grid_position, build_position, "tower stores grid position")
    t.assert_false(state.build_tower(build_position), "cannot build two towers on one tile")
    t.assert_false(state.build_tower(Vector2i(3, 4)), "cannot build tower on road")

    state.gold = 99
    t.assert_false(state.build_tower(Vector2i(4, 3)), "cannot build tower without enough gold")
```

- [ ] **Step 2: Run test to verify it fails**

Run:

```powershell
godot --headless --path godot -s res://tests/test_runner.gd
```

Expected: FAIL because `GameState.build_tower` is not defined.

- [ ] **Step 3: Add tower entity**

Create `godot/scripts/entities/defense_tower.gd`:

```gdscript
class_name DefenseTower
extends RefCounted

var id := 0
var grid_position := Vector2i.ZERO
var damage := 5
var range_tiles := 3
var cooldown_turns := 0
var cooldown_remaining := 0

func _init(tower_id: int = 0, position: Vector2i = Vector2i.ZERO) -> void:
    id = tower_id
    grid_position = position

func can_fire() -> bool:
    return cooldown_remaining <= 0

func mark_fired() -> void:
    cooldown_remaining = cooldown_turns

func tick_cooldown() -> void:
    if cooldown_remaining > 0:
        cooldown_remaining -= 1
```

- [ ] **Step 4: Modify game state with build rules**

Replace `godot/scripts/game/game_state.gd` with:

```gdscript
class_name GameState
extends RefCounted

const DefenseTower = preload("res://scripts/entities/defense_tower.gd")

enum Phase {
    PLANNING,
    RAID,
    VICTORY,
    GAME_OVER
}

const MAP_WIDTH := 16
const MAP_HEIGHT := 10
const TOWER_COST := 100
const RAID_REWARD := 75

var phase: Phase = Phase.PLANNING
var gold := 200
var core_hp := 30
var command_points := 0
var command_points_per_turn := 2
var raid_turn := 0
var year := 1
var season_index := 0
var city_core_position := Vector2i(15, 4)
var road_path: Array[Vector2i] = []
var towers: Array[DefenseTower] = []
var enemies: Array = []
var next_tower_id := 1
var next_enemy_id := 1

static func new_game() -> GameState:
    return GameState.new()

func _init() -> void:
    reset()

func reset() -> void:
    phase = Phase.PLANNING
    gold = 200
    core_hp = 30
    command_points = 0
    command_points_per_turn = 2
    raid_turn = 0
    year = 1
    season_index = 0
    city_core_position = Vector2i(15, 4)
    road_path = [
        Vector2i(0, 4),
        Vector2i(1, 4),
        Vector2i(2, 4),
        Vector2i(3, 4),
        Vector2i(4, 4),
        Vector2i(5, 4),
        Vector2i(6, 4),
        Vector2i(7, 4),
        Vector2i(8, 4),
        Vector2i(9, 4),
        Vector2i(10, 4),
        Vector2i(11, 4),
        Vector2i(12, 4),
        Vector2i(13, 4),
        Vector2i(14, 4),
        city_core_position
    ]
    towers = []
    enemies = []
    next_tower_id = 1
    next_enemy_id = 1

func is_in_bounds(grid_position: Vector2i) -> bool:
    return (
        grid_position.x >= 0
        and grid_position.y >= 0
        and grid_position.x < MAP_WIDTH
        and grid_position.y < MAP_HEIGHT
    )

func is_road(grid_position: Vector2i) -> bool:
    return road_path.has(grid_position)

func is_core(grid_position: Vector2i) -> bool:
    return grid_position == city_core_position

func is_buildable(grid_position: Vector2i) -> bool:
    if not is_in_bounds(grid_position):
        return false
    if is_road(grid_position):
        return false
    if is_core(grid_position):
        return false
    return get_tower_at(grid_position) == null

func get_tower_at(grid_position: Vector2i) -> DefenseTower:
    for tower in towers:
        if tower.grid_position == grid_position:
            return tower
    return null

func can_build_tower(grid_position: Vector2i) -> bool:
    return phase == Phase.PLANNING and gold >= TOWER_COST and is_buildable(grid_position)

func build_tower(grid_position: Vector2i) -> bool:
    if not can_build_tower(grid_position):
        return false

    var tower := DefenseTower.new(next_tower_id, grid_position)
    next_tower_id += 1
    towers.append(tower)
    gold -= TOWER_COST
    return true
```

- [ ] **Step 5: Run tests to verify they pass**

Run:

```powershell
godot --headless --path godot -s res://tests/test_runner.gd
```

Expected: PASS with 15 assertions.

- [ ] **Step 6: Commit**

```powershell
git add godot/scripts/game/game_state.gd godot/scripts/entities/defense_tower.gd godot/tests/test_building.gd
git commit -m "feat: add tower building economy"
```

---

### Task 4: Add Raid Enemies, Combat, and Turn Resolution

**Files:**
- Create: `godot/tests/test_raid_turns.gd`
- Create: `godot/scripts/entities/enemy_unit.gd`
- Create: `godot/scripts/systems/combat_resolver.gd`
- Create: `godot/scripts/game/turn_manager.gd`
- Modify: `godot/scripts/game/game_state.gd`

- [ ] **Step 1: Write the failing raid test**

Create `godot/tests/test_raid_turns.gd`:

```gdscript
extends RefCounted

const GameState = preload("res://scripts/game/game_state.gd")
const TurnManager = preload("res://scripts/game/turn_manager.gd")

func run(t) -> void:
    var state := GameState.new_game()
    t.assert_true(state.build_tower(Vector2i(5, 3)), "tower is built near the road")
    t.assert_true(state.start_raid(), "raid starts from planning phase")
    t.assert_eq(state.phase, GameState.Phase.RAID, "phase switches to raid")
    t.assert_eq(state.enemies.size(), 3, "first wave spawns three enemies")

    var manager := TurnManager.new(state)
    var first_position := state.enemies[0].grid_position
    var result := manager.advance_raid_turn()

    t.assert_true(result.enemy_moves.size() > 0, "turn records enemy movement")
    t.assert_false(state.enemies[0].grid_position == first_position, "enemy advances along road")

    for index in range(12):
        if state.phase != GameState.Phase.RAID:
            break
        manager.advance_raid_turn()

    t.assert_eq(state.phase, GameState.Phase.PLANNING, "raid returns to planning after enemies are defeated")
    t.assert_eq(state.gold, 175, "raid reward is paid after tower cost")
```

- [ ] **Step 2: Run test to verify it fails**

Run:

```powershell
godot --headless --path godot -s res://tests/test_runner.gd
```

Expected: FAIL because `turn_manager.gd`, `enemy_unit.gd`, and `start_raid` are not implemented.

- [ ] **Step 3: Add enemy entity**

Create `godot/scripts/entities/enemy_unit.gd`:

```gdscript
class_name EnemyUnit
extends RefCounted

var id := 0
var grid_position := Vector2i.ZERO
var path_index := 0
var hp := 10
var max_hp := 10
var core_damage := 3

func _init(
    enemy_id: int = 0,
    position: Vector2i = Vector2i.ZERO,
    start_path_index: int = 0,
    health: int = 10,
    damage_to_core: int = 3
) -> void:
    id = enemy_id
    grid_position = position
    path_index = start_path_index
    hp = health
    max_hp = health
    core_damage = damage_to_core

func is_alive() -> bool:
    return hp > 0

func take_damage(amount: int) -> void:
    hp = maxi(0, hp - amount)
```

- [ ] **Step 4: Add combat resolver**

Create `godot/scripts/systems/combat_resolver.gd`:

```gdscript
class_name CombatResolver
extends RefCounted

static func manhattan_distance(a: Vector2i, b: Vector2i) -> int:
    return absi(a.x - b.x) + absi(a.y - b.y)

static func pick_target_for_tower(tower: DefenseTower, enemies: Array[EnemyUnit]) -> EnemyUnit:
    var best_target: EnemyUnit = null
    var best_progress := -1

    for enemy in enemies:
        if not enemy.is_alive():
            continue
        var distance := manhattan_distance(tower.grid_position, enemy.grid_position)
        if distance > tower.range_tiles:
            continue
        if enemy.path_index > best_progress:
            best_progress = enemy.path_index
            best_target = enemy

    return best_target

static func resolve_tower_attack(tower: DefenseTower, target: EnemyUnit) -> Dictionary:
    target.take_damage(tower.damage)
    tower.mark_fired()
    return {
        "tower": tower.id,
        "target": target.id,
        "damage": tower.damage,
        "target_hp": target.hp
    }
```

- [ ] **Step 5: Add turn manager**

Create `godot/scripts/game/turn_manager.gd`:

```gdscript
class_name TurnManager
extends RefCounted

const GameState = preload("res://scripts/game/game_state.gd")
const CombatResolver = preload("res://scripts/systems/combat_resolver.gd")

var state: GameState

func _init(game_state: GameState) -> void:
    state = game_state

func advance_raid_turn() -> Dictionary:
    var result := {
        "enemy_moves": [],
        "tower_attacks": [],
        "core_damage": 0,
        "outcome": ""
    }

    if state.phase != GameState.Phase.RAID:
        result.outcome = "not_in_raid"
        return result

    _move_enemies(result)
    _fire_towers(result)
    _remove_defeated_enemies()

    state.raid_turn += 1
    state.command_points = state.command_points_per_turn

    if state.core_hp <= 0:
        state.core_hp = 0
        state.phase = GameState.Phase.GAME_OVER
        result.outcome = "lost"
    elif state.enemies.is_empty():
        state.phase = GameState.Phase.PLANNING
        state.gold += GameState.RAID_REWARD
        state.command_points = 0
        result.outcome = "raid_won"
    else:
        result.outcome = "continue"

    return result

func _move_enemies(result: Dictionary) -> void:
    for enemy in state.enemies:
        if not enemy.is_alive():
            continue

        var old_position := enemy.grid_position
        enemy.path_index = mini(enemy.path_index + 1, state.road_path.size() - 1)
        enemy.grid_position = state.road_path[enemy.path_index]
        result.enemy_moves.append({
            "enemy": enemy.id,
            "from": old_position,
            "to": enemy.grid_position
        })

        if enemy.grid_position == state.city_core_position:
            state.core_hp -= enemy.core_damage
            result.core_damage += enemy.core_damage
            enemy.take_damage(enemy.hp)

func _fire_towers(result: Dictionary) -> void:
    for tower in state.towers:
        tower.tick_cooldown()
        if not tower.can_fire():
            continue

        var target := CombatResolver.pick_target_for_tower(tower, state.enemies)
        if target != null:
            result.tower_attacks.append(CombatResolver.resolve_tower_attack(tower, target))

func _remove_defeated_enemies() -> void:
    var survivors: Array[EnemyUnit] = []
    for enemy in state.enemies:
        if enemy.is_alive():
            survivors.append(enemy)
    state.enemies = survivors
```

- [ ] **Step 6: Modify game state with raid setup**

Replace `godot/scripts/game/game_state.gd` with:

```gdscript
class_name GameState
extends RefCounted

const DefenseTower = preload("res://scripts/entities/defense_tower.gd")
const EnemyUnit = preload("res://scripts/entities/enemy_unit.gd")

enum Phase {
    PLANNING,
    RAID,
    VICTORY,
    GAME_OVER
}

const MAP_WIDTH := 16
const MAP_HEIGHT := 10
const TOWER_COST := 100
const RAID_REWARD := 75

var phase: Phase = Phase.PLANNING
var gold := 200
var core_hp := 30
var command_points := 0
var command_points_per_turn := 2
var raid_turn := 0
var year := 1
var season_index := 0
var city_core_position := Vector2i(15, 4)
var road_path: Array[Vector2i] = []
var towers: Array[DefenseTower] = []
var enemies: Array[EnemyUnit] = []
var next_tower_id := 1
var next_enemy_id := 1

static func new_game() -> GameState:
    return GameState.new()

func _init() -> void:
    reset()

func reset() -> void:
    phase = Phase.PLANNING
    gold = 200
    core_hp = 30
    command_points = 0
    command_points_per_turn = 2
    raid_turn = 0
    year = 1
    season_index = 0
    city_core_position = Vector2i(15, 4)
    road_path = [
        Vector2i(0, 4),
        Vector2i(1, 4),
        Vector2i(2, 4),
        Vector2i(3, 4),
        Vector2i(4, 4),
        Vector2i(5, 4),
        Vector2i(6, 4),
        Vector2i(7, 4),
        Vector2i(8, 4),
        Vector2i(9, 4),
        Vector2i(10, 4),
        Vector2i(11, 4),
        Vector2i(12, 4),
        Vector2i(13, 4),
        Vector2i(14, 4),
        city_core_position
    ]
    towers = []
    enemies = []
    next_tower_id = 1
    next_enemy_id = 1

func is_in_bounds(grid_position: Vector2i) -> bool:
    return (
        grid_position.x >= 0
        and grid_position.y >= 0
        and grid_position.x < MAP_WIDTH
        and grid_position.y < MAP_HEIGHT
    )

func is_road(grid_position: Vector2i) -> bool:
    return road_path.has(grid_position)

func is_core(grid_position: Vector2i) -> bool:
    return grid_position == city_core_position

func is_buildable(grid_position: Vector2i) -> bool:
    if not is_in_bounds(grid_position):
        return false
    if is_road(grid_position):
        return false
    if is_core(grid_position):
        return false
    return get_tower_at(grid_position) == null

func get_tower_at(grid_position: Vector2i) -> DefenseTower:
    for tower in towers:
        if tower.grid_position == grid_position:
            return tower
    return null

func can_build_tower(grid_position: Vector2i) -> bool:
    return phase == Phase.PLANNING and gold >= TOWER_COST and is_buildable(grid_position)

func build_tower(grid_position: Vector2i) -> bool:
    if not can_build_tower(grid_position):
        return false

    var tower := DefenseTower.new(next_tower_id, grid_position)
    next_tower_id += 1
    towers.append(tower)
    gold -= TOWER_COST
    return true

func start_raid() -> bool:
    if phase != Phase.PLANNING:
        return false
    if core_hp <= 0:
        return false

    phase = Phase.RAID
    raid_turn = 0
    command_points = command_points_per_turn
    enemies = []

    for index in range(3):
        var enemy := EnemyUnit.new(next_enemy_id, road_path[0], 0, 10, 3)
        next_enemy_id += 1
        enemies.append(enemy)

    return true
```

- [ ] **Step 7: Run tests to verify they pass**

Run:

```powershell
godot --headless --path godot -s res://tests/test_runner.gd
```

Expected: PASS with 24 assertions.

- [ ] **Step 8: Commit**

```powershell
git add godot/scripts/game/game_state.gd godot/scripts/game/turn_manager.gd godot/scripts/systems/combat_resolver.gd godot/scripts/entities/enemy_unit.gd godot/tests/test_raid_turns.gd
git commit -m "feat: add deterministic raid turns"
```

---

### Task 5: Add Main Menu and Playable City Screen

**Files:**
- Create: `godot/tests/test_scene_loads.gd`
- Create: `godot/scenes/main_menu.tscn`
- Create: `godot/scenes/city_defense.tscn`
- Create: `godot/scripts/ui/main_menu.gd`
- Create: `godot/scripts/ui/city_defense_screen.gd`

- [ ] **Step 1: Write the failing scene load test**

Create `godot/tests/test_scene_loads.gd`:

```gdscript
extends RefCounted

func run(t) -> void:
    var menu_scene := load("res://scenes/main_menu.tscn")
    var city_scene := load("res://scenes/city_defense.tscn")

    t.assert_true(menu_scene != null, "main menu scene loads")
    t.assert_true(city_scene != null, "city defense scene loads")

    var city_instance := city_scene.instantiate()
    t.assert_true(city_instance != null, "city defense scene instantiates")
    city_instance.queue_free()
```

- [ ] **Step 2: Run test to verify it fails**

Run:

```powershell
godot --headless --path godot -s res://tests/test_runner.gd
```

Expected: FAIL because the scenes do not exist.

- [ ] **Step 3: Add main menu scene**

Create `godot/scenes/main_menu.tscn`:

```ini
[gd_scene load_steps=2 format=3]

[ext_resource type="Script" path="res://scripts/ui/main_menu.gd" id="1"]

[node name="MainMenu" type="Control"]
layout_mode = 3
anchors_preset = 15
anchor_right = 1.0
anchor_bottom = 1.0
grow_horizontal = 2
grow_vertical = 2
script = ExtResource("1")
```

- [ ] **Step 4: Add main menu script**

Create `godot/scripts/ui/main_menu.gd`:

```gdscript
extends Control

func _ready() -> void:
    var root := CenterContainer.new()
    root.set_anchors_preset(Control.PRESET_FULL_RECT)
    add_child(root)

    var panel := PanelContainer.new()
    panel.custom_minimum_size = Vector2(420, 260)
    root.add_child(panel)

    var box := VBoxContainer.new()
    box.add_theme_constant_override("separation", 16)
    panel.add_child(box)

    var title := Label.new()
    title.text = "Till The End"
    title.horizontal_alignment = HORIZONTAL_ALIGNMENT_CENTER
    title.add_theme_font_size_override("font_size", 36)
    box.add_child(title)

    var subtitle := Label.new()
    subtitle.text = "District Defense Prototype"
    subtitle.horizontal_alignment = HORIZONTAL_ALIGNMENT_CENTER
    subtitle.add_theme_font_size_override("font_size", 18)
    box.add_child(subtitle)

    var start_button := Button.new()
    start_button.text = "New Defense"
    start_button.pressed.connect(_on_start_pressed)
    box.add_child(start_button)

    var quit_button := Button.new()
    quit_button.text = "Quit"
    quit_button.pressed.connect(_on_quit_pressed)
    box.add_child(quit_button)

func _on_start_pressed() -> void:
    get_tree().change_scene_to_file("res://scenes/city_defense.tscn")

func _on_quit_pressed() -> void:
    get_tree().quit()
```

- [ ] **Step 5: Add city defense scene**

Create `godot/scenes/city_defense.tscn`:

```ini
[gd_scene load_steps=2 format=3]

[ext_resource type="Script" path="res://scripts/ui/city_defense_screen.gd" id="1"]

[node name="CityDefense" type="Node2D"]
script = ExtResource("1")
```

- [ ] **Step 6: Add playable city defense script**

Create `godot/scripts/ui/city_defense_screen.gd`:

```gdscript
extends Node2D

const GameState = preload("res://scripts/game/game_state.gd")
const TurnManager = preload("res://scripts/game/turn_manager.gd")

const TILE_SIZE := 40
const MAP_ORIGIN := Vector2(48, 120)

var state: GameState
var turn_manager: TurnManager
var status_label: Label
var log_label: Label
var start_button: Button
var next_turn_button: Button
var reset_button: Button
var last_log := "Build towers on open tiles, then start the raid."

func _ready() -> void:
    state = GameState.new_game()
    turn_manager = TurnManager.new(state)
    _build_hud()
    _update_hud()
    queue_redraw()

func _unhandled_input(event: InputEvent) -> void:
    if not (event is InputEventMouseButton):
        return
    if not event.pressed or event.button_index != MOUSE_BUTTON_LEFT:
        return

    var grid_position := _screen_to_grid(event.position)
    if state.phase == GameState.Phase.PLANNING:
        if state.build_tower(grid_position):
            last_log = "Built tower at %s. Gold remaining: %d." % [str(grid_position), state.gold]
        elif state.is_road(grid_position):
            last_log = "Road tiles must remain open for the prototype route."
        elif not state.is_in_bounds(grid_position):
            last_log = "Select a tile inside the city grid."
        else:
            last_log = "Cannot build there."

    _update_hud()
    queue_redraw()

func _draw() -> void:
    _draw_map()
    _draw_towers()
    _draw_enemies()

func _draw_map() -> void:
    for x in range(GameState.MAP_WIDTH):
        for y in range(GameState.MAP_HEIGHT):
            var grid_position := Vector2i(x, y)
            var rect := Rect2(
                MAP_ORIGIN + Vector2(x * TILE_SIZE, y * TILE_SIZE),
                Vector2(TILE_SIZE - 2, TILE_SIZE - 2)
            )
            var fill := Color(0.20, 0.33, 0.24)
            if state.is_road(grid_position):
                fill = Color(0.42, 0.33, 0.23)
            if state.is_core(grid_position):
                fill = Color(0.20, 0.30, 0.62)

            draw_rect(rect, fill, true)
            draw_rect(rect, Color(0.06, 0.08, 0.07), false, 1.0)

func _draw_towers() -> void:
    for tower in state.towers:
        var center := _grid_to_screen(tower.grid_position) + Vector2(TILE_SIZE / 2, TILE_SIZE / 2)
        var base := Rect2(center - Vector2(12, 12), Vector2(24, 24))
        draw_rect(base, Color(0.25, 0.55, 0.95), true)
        draw_rect(base, Color(0.88, 0.95, 1.0), false, 2.0)

func _draw_enemies() -> void:
    for enemy in state.enemies:
        var center := _grid_to_screen(enemy.grid_position) + Vector2(TILE_SIZE / 2, TILE_SIZE / 2)
        draw_circle(center, 12.0, Color(0.78, 0.18, 0.14))
        draw_arc(center, 15.0, 0.0, TAU, 24, Color(1.0, 0.76, 0.32), 2.0)

func _build_hud() -> void:
    var layer := CanvasLayer.new()
    add_child(layer)

    var panel := PanelContainer.new()
    panel.position = Vector2(760, 32)
    panel.custom_minimum_size = Vector2(460, 420)
    layer.add_child(panel)

    var box := VBoxContainer.new()
    box.add_theme_constant_override("separation", 10)
    panel.add_child(box)

    var title := Label.new()
    title.text = "Kuala Lumpur Defense"
    title.add_theme_font_size_override("font_size", 24)
    box.add_child(title)

    status_label = Label.new()
    status_label.autowrap_mode = TextServer.AUTOWRAP_WORD_SMART
    box.add_child(status_label)

    var button_row := HBoxContainer.new()
    button_row.add_theme_constant_override("separation", 8)
    box.add_child(button_row)

    start_button = Button.new()
    start_button.text = "Start Raid"
    start_button.pressed.connect(_on_start_raid_pressed)
    button_row.add_child(start_button)

    next_turn_button = Button.new()
    next_turn_button.text = "Next Turn"
    next_turn_button.pressed.connect(_on_next_turn_pressed)
    button_row.add_child(next_turn_button)

    reset_button = Button.new()
    reset_button.text = "Reset"
    reset_button.pressed.connect(_on_reset_pressed)
    button_row.add_child(reset_button)

    log_label = Label.new()
    log_label.autowrap_mode = TextServer.AUTOWRAP_WORD_SMART
    box.add_child(log_label)

func _on_start_raid_pressed() -> void:
    if state.start_raid():
        last_log = "Raid started. Advance turns to resolve enemy movement and tower fire."
    else:
        last_log = "Cannot start raid from the current phase."
    _update_hud()
    queue_redraw()

func _on_next_turn_pressed() -> void:
    var result := turn_manager.advance_raid_turn()
    if result.outcome == "raid_won":
        last_log = "Raid won. Reward gained. Build again before the next attack."
    elif result.outcome == "lost":
        last_log = "The city core has fallen."
    elif result.outcome == "continue":
        last_log = "Turn %d: %d moves, %d tower attacks, %d core damage." % [
            state.raid_turn,
            result.enemy_moves.size(),
            result.tower_attacks.size(),
            result.core_damage
        ]
    else:
        last_log = "Start a raid before advancing turns."

    _update_hud()
    queue_redraw()

func _on_reset_pressed() -> void:
    state = GameState.new_game()
    turn_manager = TurnManager.new(state)
    last_log = "Prototype reset."
    _update_hud()
    queue_redraw()

func _update_hud() -> void:
    var phase_name := "Planning"
    if state.phase == GameState.Phase.RAID:
        phase_name = "Raid"
    elif state.phase == GameState.Phase.GAME_OVER:
        phase_name = "Game Over"
    elif state.phase == GameState.Phase.VICTORY:
        phase_name = "Victory"

    status_label.text = "Phase: %s\nGold: %d\nCore HP: %d\nTowers: %d\nEnemies: %d\nCommand Points: %d\nRaid Turn: %d" % [
        phase_name,
        state.gold,
        state.core_hp,
        state.towers.size(),
        state.enemies.size(),
        state.command_points,
        state.raid_turn
    ]
    log_label.text = last_log
    start_button.disabled = state.phase != GameState.Phase.PLANNING
    next_turn_button.disabled = state.phase != GameState.Phase.RAID

func _screen_to_grid(screen_position: Vector2) -> Vector2i:
    var local := screen_position - MAP_ORIGIN
    return Vector2i(floori(local.x / TILE_SIZE), floori(local.y / TILE_SIZE))

func _grid_to_screen(grid_position: Vector2i) -> Vector2:
    return MAP_ORIGIN + Vector2(grid_position.x * TILE_SIZE, grid_position.y * TILE_SIZE)
```

- [ ] **Step 7: Run tests to verify they pass**

Run:

```powershell
godot --headless --path godot -s res://tests/test_runner.gd
```

Expected: PASS with 27 assertions.

- [ ] **Step 8: Launch the prototype manually**

Run:

```powershell
godot --path godot
```

Expected: main menu opens. Click `New Defense`, build one or two towers beside the road, click `Start Raid`, click `Next Turn` until either the raid is won or the core is damaged.

- [ ] **Step 9: Commit**

```powershell
git add godot/scenes/main_menu.tscn godot/scenes/city_defense.tscn godot/scripts/ui/main_menu.gd godot/scripts/ui/city_defense_screen.gd godot/tests/test_scene_loads.gd
git commit -m "feat: add playable city defense screen"
```

---

### Task 6: Add Prototype Run Documentation

**Files:**
- Create: `godot/README.md`

- [ ] **Step 1: Create run instructions**

Create `godot/README.md`:

```markdown
# Till The End Godot Prototype

This folder contains the Phase 1 Godot prototype for the upgraded Till The End district defense game.

## Requirements

- Godot 4.x
- `godot` executable available on `PATH`

## Run

From the repository root:

```powershell
godot --path godot
```

## Test

From the repository root:

```powershell
godot --headless --path godot -s res://tests/test_runner.gd
```

## Current Play Loop

1. Open the game.
2. Click `New Defense`.
3. Click clear green tiles near the road to build towers.
4. Click `Start Raid`.
5. Click `Next Turn` to advance enemy movement and tower fire.
6. Win the raid by defeating all enemies before the city core falls.
```

- [ ] **Step 2: Run tests one final time**

Run:

```powershell
godot --headless --path godot -s res://tests/test_runner.gd
```

Expected: PASS with 27 assertions.

- [ ] **Step 3: Check git status**

Run:

```powershell
git status --short
```

Expected: only `godot/README.md` is uncommitted.

- [ ] **Step 4: Commit**

```powershell
git add godot/README.md
git commit -m "docs: add godot prototype instructions"
```

---

## Completion Criteria

- `godot --headless --path godot -s res://tests/test_runner.gd` passes.
- `godot --path godot` opens the main menu.
- The city defense screen allows tower placement during planning.
- Starting a raid spawns three enemies.
- Advancing turns moves enemies, fires towers, removes defeated enemies, and pays a reward after raid victory.
- The old Java project remains untouched.
- The final worktree contains only intentional Godot prototype files and this plan.

## Self-Review Notes

- Spec coverage for Phase 1 is complete: main menu, tile-grid city, buildable tower tiles, one route, gold economy, start raid button, turn-based enemy movement, tower attacks, raid win, and core loss are all covered.
- The plan intentionally excludes seasons, morale, district bonuses, dragon boss, save/load, audio, and final art because those belong to later phases in the approved design.
- Type names are consistent across tasks: `GameState`, `DefenseTower`, `EnemyUnit`, `CombatResolver`, and `TurnManager`.
