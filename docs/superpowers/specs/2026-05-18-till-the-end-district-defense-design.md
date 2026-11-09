# Till The End District Defense Design

Date: 2026-05-18

## Goal

Rebuild **Till The End** from a Java ASCII tower-defense game into a polished 2D pixel-art district defense strategy game. The new game should preserve the original identity: the player is the mayor of Kuala Lumpur defending the city from dragon invasion through seasonal planning, tax income, defensive upgrades, citizen morale, and escalating raids.

The implementation target is **Godot 4 with GDScript** because it is better suited than plain Java, Swing, JavaFX, or LibGDX for fast iteration on tile maps, pixel art, UI, animation, audio, and pathfinding. Java and LibGDX remain acceptable fallback options if the project later requires staying in the Java ecosystem.

## Existing Game Analysis

The current project is a NetBeans-style Java application under `Till The End/src/TillTheEnd`. The main entry point is `TillTheEnd.java`, which starts or loads a game, selects Normal or Hard difficulty, then delegates to `Normal.gameMethod(...)` or `Hard.gameMethod(...)`.

The current loop is:

1. Dragon raid begins.
2. Dragon attacks the wall for up to 10 exchanges.
3. Tower counterattacks after each dragon action.
4. If wall health reaches 0, the player loses.
5. If dragon health reaches 0, the player wins.
6. If neither happens, the dragon retreats and grows stronger.
7. Player receives tax income.
8. The season advances and random seasonal events modify stats.
9. Player spends gold on tower, wall, or citizen upgrades.
10. The game saves and the next season begins.

The current code provides useful game design material, but it mixes concerns that should be separated in the rebuild:

- `Dragon`, `Tower`, `Wall`, `Citizen`, `Season`, and `Tax` contain useful rules, but also print ASCII art, read input, play audio, and roll random combat directly.
- `Normal` and `Hard` duplicate the same loop, so difficulty should become data-driven configuration.
- Save/load is a line-based encrypted file format. The rebuild should use structured save data.
- Console rendering and blocking sound playback should be replaced by Godot rendering, UI, and audio systems.

Reusable ideas:

- Kuala Lumpur mayor fantasy.
- Dragon invasion as the central threat.
- Season and year cycle.
- Tax/gold economy.
- Wall and tower upgrades.
- Citizen emotion/social pressure system.
- Normal and Hard difficulty.
- Dramatic sound and combat feedback.
- Save/load support.

Refactor or replace:

- Replace ASCII rendering with tile and sprite rendering.
- Replace `Scanner` menus with mouse/keyboard UI.
- Replace copied Normal/Hard controllers with difficulty resources.
- Replace `SameBehaviour` with explicit concepts such as attackers, damageable objects, upgradeable buildings, and raid actors.
- Convert random combat into a deterministic turn result system that the UI can display clearly.

## Upgraded Game Design

The upgraded game is a **pixel-art district defense strategy game**. The player manages Kuala Lumpur as a city map rather than a single abstract wall.

The main gameplay screen is a top-down orthographic tile map containing:

- **City Core**: main objective. If it falls, the game ends.
- **Districts**: residential, market, civic, workshop, shrine, and gate districts.
- **Roads**: enemy pathfinding routes and strategic choke points.
- **Walls and Gates**: block, redirect, or delay enemies.
- **Towers**: attack enemies in range.
- **Season Layer**: visual tint, weather effects, and event overlays for spring, summer, autumn, and winter.

The game loop has five phases:

1. **Season Planning Phase**: collect tax, resolve seasonal event, build and upgrade towers, walls, districts, and morale policies.
2. **Raid Preview Phase**: show spawn points, likely paths, enemy types, dragon warning level, and expected rewards.
3. **Raid Phase**: enemies move along paths in turn steps. Towers fire automatically.
4. **Command Turns**: the mayor spends limited command points on emergency actions.
5. **Aftermath Phase**: damaged districts reduce income or morale, surviving districts generate bonuses, and dragon threat escalates.

The game remains tower-defense and resource-strategy focused. The player does not directly control a hero unit. Strategic decisions come from where to build, which districts to protect, which paths to manipulate, and when to spend command points during raids.

## Visual Design

Use 16x16 or 32x32 pixel-art tiles. Top-down orthographic presentation is preferred over isometric because it keeps tile targeting, path previews, and defense placement clear.

Visual elements:

- Roads, grass, rivers, city plazas, gates, walls, and district tiles.
- Prosperous, damaged, panicked, burning, and fortified district states.
- Towers with firing animations, recoil, muzzle flashes, and cooldown indicators.
- Walls with cracked damage states.
- Enemies with readable silhouettes and movement animations.
- Dragon boss as a large multi-tile flying sprite with shadow, wing flaps, fire breath, and retreat animation.
- HUD showing gold, season, year, morale, dragon threat, city core health, and command points.
- Floating damage numbers, hit sparks, fire effects, weather effects, and short combat log entries.

Original MP3 files can be reused as placeholders or reference material, but final audio should use non-blocking Godot audio playback.

## Architecture

Use **Godot 4** as the primary implementation target.

Recommended project shape:

```text
res://
  scenes/
    main_menu.tscn
    city_defense.tscn
    upgrade_panel.tscn
    raid_result_panel.tscn
  scripts/
    game/
      game_state.gd
      turn_manager.gd
      difficulty_config.gd
      save_service.gd
    systems/
      season_system.gd
      economy_system.gd
      morale_system.gd
      combat_resolver.gd
      pathfinding_service.gd
      command_point_system.gd
    map/
      city_map.gd
      district.gd
      tile_rules.gd
    entities/
      enemy.gd
      dragon_boss.gd
      tower.gd
      wall.gd
      projectile.gd
    ui/
      hud.gd
      build_menu.gd
      command_bar.gd
  resources/
    enemies/
    towers/
    seasons/
    events/
    difficulties/
  assets/
    sprites/
    tiles/
    audio/
```

Core architectural rules:

- `game_state.gd` owns saveable campaign state: year, season, gold, morale, districts, towers, walls, enemies, dragon threat, difficulty, and city core health.
- Systems modify state through explicit methods.
- UI reads state and emits commands such as `build_tower`, `repair_wall`, `start_raid`, or `use_command`.
- `turn_manager.gd` advances raids one inspectable turn at a time.
- Enemy, tower, event, season, and difficulty values should be stored as Godot `Resource` files so balance can change without rewriting core systems.
- Tile movement should use Godot `AStarGrid2D`.
- UI should use Godot `Control` nodes.
- Save/load should use JSON or a structured Godot resource format.

Original Java mapping:

- `Season.java` becomes `season_system.gd` plus season event resources.
- `Tax.java` becomes `economy_system.gd`.
- `Citizen.java` becomes `morale_system.gd`.
- `Tower.java`, `Wall.java`, and `Dragon.java` become entity scenes plus resource-driven stats.
- `Normal.java` and `Hard.java` become difficulty resources.
- `TillTheEnd.java` becomes the main menu and game bootstrap flow.

## Gameplay Systems

### Economy and Districts

Districts replace the old purely abstract city stats:

- **Market**: increases tax income.
- **Residential**: affects morale and panic.
- **Civic**: generates command points.
- **Workshop**: reduces build or upgrade costs.
- **Shrine**: improves morale recovery or seasonal resilience.
- **Gate**: controls enemy entry points and wall defense.

Damaged districts reduce income, morale, or command generation until repaired.

### Morale

The original citizen stats should be redesigned into readable city conditions:

- Panic
- Fatigue
- Courage
- Discipline
- Anger
- Hope

These preserve the feel of emotional, nervous, lazy, berserk, diligent, and fearless without requiring players to manage six raw counters exactly as the ASCII version did.

Morale affects tax reliability, command point generation, tower performance, repair speed, and event outcomes.

### Seasons and Events

Spring, summer, autumn, and winter remain central. Seasonal events can:

- Add or remove gold.
- Damage districts or walls.
- Change tower accuracy.
- Change enemy speed.
- Increase or reduce morale.
- Raise dragon threat.
- Create temporary map hazards such as flood, drought, fire, or snow.

Hard difficulty can apply two events per season, stronger dragon threat growth, lower tax income, or harsher district damage.

### Enemies

Regular raids use minion waves. The dragon acts as the seasonal boss.

Enemy roles:

- **Scouts**: fast, low health, pressure weak paths.
- **Brutes**: slow, high health, damage walls.
- **Fire Imps**: ignite districts and reduce tax output.
- **Siege Beasts**: prioritize gates and walls.
- **Dragon**: flying boss that can ignore some path rules, breathe fire, damage districts, and retreat if not defeated in time.

### Towers and Defenses

Tower roles:

- **Ballista**: high single-target damage.
- **Watchtower**: long range and reliable accuracy.
- **Cannon**: slow reload with splash damage.
- **Frost Shrine**: slows enemies.
- **Signal Tower**: boosts nearby towers or command generation.

Defense roles:

- **Wall**: blocks or redirects enemies.
- **Gate**: durable controlled entry point.
- **Barricade**: cheaper temporary obstacle.
- **Trap**: one-use damage or slow effect.

### Mayor Command Points

Most power comes from planning, but each raid grants limited command points for clutch decisions:

- **Focus Fire**: selected towers prioritize one target.
- **Emergency Repair**: restore a wall or district during a raid.
- **Militia Block**: place a temporary blocker on a road tile.
- **Evacuate District**: prevent casualties or panic, but lose income this season.
- **Rally Citizens**: reduce panic and increase command generation.
- **Overcharge Tower**: boost one tower for a turn, then apply cooldown.

Each raid turn should present an actual tradeoff between immediate survival and future stability.

## Win and Loss Conditions

Loss condition:

- The city core reaches 0 health.

Raid success condition:

- All regular enemies are defeated or the wave timer ends without the city core falling.

Dragon raid outcomes:

- If the dragon reaches 0 health, the campaign is won.
- If the dragon survives its raid duration, it retreats and returns stronger.
- If the dragon destroys the city core, the campaign is lost.

Campaign progression:

- Years and seasons advance after each raid.
- Dragon threat rises with time, failed defenses, and certain events.
- Higher years introduce more enemy types, spawn points, and harsher seasonal effects.

## Implementation Phases

### Phase 1: Godot Prototype

Create a playable vertical slice:

- Main menu.
- One tile-map city.
- Buildable tower and wall tiles.
- One road/pathfinding route.
- Basic gold economy.
- Start raid button.
- Minion wave moves turn by turn toward the city core.
- One tower attacks enemies.
- Win raid if all enemies die.
- Lose campaign if city core health reaches 0.

### Phase 2: Original Systems Translation

Add the original identity:

- Year and season cycle.
- Tax collection.
- Normal and Hard difficulty data.
- Seasonal events inspired by `Season.java`.
- Morale inspired by `Citizen.java`.
- Tower and wall upgrades inspired by `Tower.java` and `Wall.java`.
- Dragon threat meter.

### Phase 3: District Defense

Make the city map strategic:

- District tiles with income, morale, and command effects.
- Multiple roads and spawn points.
- Walls influence enemy routes.
- Enemy AI chooses paths with `AStarGrid2D`.
- District damage affects future income and morale.

### Phase 4: Hybrid Raid Commands

Add command point actions:

- Focus fire.
- Emergency repair.
- Militia block.
- Evacuate district.
- Rally citizens.
- Tower overcharge.
- Raid turn UI and combat log.

### Phase 5: Dragon Boss

Add the main identity:

- Dragon appears every few seasons or at max threat.
- Multi-tile flying boss behavior.
- Fire breath damages walls and districts.
- Dragon retreats if not killed after a fixed number of raid turns.
- Campaign win when dragon health reaches 0.

### Phase 6: Content and Polish

Add production polish:

- Pixel-art sprite pass.
- Animations and visual effects.
- Audio pass.
- Menus and save/load.
- Tutorial first year.
- More towers, enemies, events, maps, and difficulty scaling.
- Balance pass.

## Example Code Sketches

Example turn manager:

```gdscript
class_name TurnManager
extends Node

signal raid_turn_finished(result)

var state: GameState
var pathfinding: PathfindingService
var combat: CombatResolver

func advance_raid_turn() -> void:
    var result := {
        "enemy_moves": [],
        "tower_attacks": [],
        "damage_events": []
    }

    for enemy in state.enemies:
        if enemy.is_alive():
            var move := pathfinding.next_step_for(enemy, state.city_core_position)
            enemy.grid_position = move
            result.enemy_moves.append({"enemy": enemy.id, "to": move})

            if move == state.city_core_position:
                state.core_hp -= enemy.core_damage
                result.damage_events.append({"target": "core", "amount": enemy.core_damage})

    for tower in state.towers:
        if tower.can_fire():
            var target := combat.pick_target_for_tower(tower, state.enemies)
            if target:
                var attack := combat.resolve_tower_attack(tower, target)
                result.tower_attacks.append(attack)

    state.command_points = state.command_points_per_turn
    state.raid_turn += 1
    raid_turn_finished.emit(result)
```

Example tower data resource:

```gdscript
class_name TowerData
extends Resource

@export var id: String
@export var display_name: String
@export var cost: int = 100
@export var damage: int = 5
@export var range_tiles: int = 3
@export var accuracy: float = 0.8
@export var crit_chance: float = 0.2
@export var cooldown_turns: int = 1
@export var sprite: Texture2D
```

Example seasonal event resource:

```gdscript
class_name SeasonEvent
extends Resource

@export var id: String
@export var display_name: String
@export var season: String
@export var gold_delta: int = 0
@export var morale_delta: int = 0
@export var tower_accuracy_delta: float = 0.0
@export var wall_damage: int = 0
@export var dragon_threat_delta: int = 1
```

## Testing and Verification

Test the rebuild in layers:

- Unit test pure logic where practical: economy, season selection, damage calculation, command costs, and save data migration.
- Use deterministic random seeds for combat and event tests.
- Verify pathfinding when walls are added or removed.
- Playtest the Phase 1 prototype for a complete loop: build, start raid, enemies move, tower fires, raid ends, reward or loss applies.
- For each content phase, verify that the game can save and reload from the planning phase.
- Balance with visible debug overlays for enemy path, tower range, district status, and dragon threat.

## Open Decisions For Implementation Planning

These decisions should be made during implementation planning, not in this design spec:

- Exact Godot project location inside the repository.
- Whether to preserve the old Java project untouched or move it under an `legacy-java/` folder.
- Initial tile size, likely 16x16 or 32x32.
- First map dimensions.
- First prototype art source: placeholder programmer art, free assets, or custom pixel-art pass.
- Exact save format: JSON file or Godot resource serialization.

