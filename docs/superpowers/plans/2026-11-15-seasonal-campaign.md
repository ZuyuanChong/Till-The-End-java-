# Seasonal Campaign Implementation Plan

> **For agentic workers** Use subagent-driven development for the independent balance resources and systems. Integrate the campaign and interface in this session. Track steps using the checkboxes below.

**Goal** Implement the approved Phase 2 seasonal city-defense campaign.

**Architecture** Keep GameState as the owner of campaign state. Pure systems and Godot Resources supply difficulty, tax, morale, and event rules. TurnManager resolves raids and asks GameState to advance the campaign exactly once after survival.

**Tech Stack** Godot 4.6.2, GDScript, built-in Resources, the existing headless test runner.

**Spec** `docs/superpowers/specs/2026-05-18-till-the-end-district-defense-design.md`, Phase 2.

## Global Constraints

- Strict Manhattan tower targeting.
- Initial Normal state remains 200 gold, 30 core HP, and 3 enemies at 5 HP.
- Seasonal progression happens only after completed raids with a surviving core.
- Combat, planning transactions, and UI use the same GameState.
- Temporary seasonal effects expire at the next season transition.
- All balance values below are provisional implementation choices.

## Balance And Interfaces

`DifficultyConfig` extends Resource. Export `id`, `display_name`, `base_tax`, `wave_size`, `enemy_health`, `enemy_core_damage`, `threat_per_season`, and `events_per_season`. Static `for_id(id: String = "normal") -> Resource` loads the matching resource, falling back to Normal for unknown IDs. Normal values are 80, 3, 5, 3, 10, 1. Hard values are 50, 4, 7, 4, 15, 2.

`EconomySystem.tax_for(morale: int, difficulty: Resource) -> int` returns base tax plus 20 at morale >= 70, minus 20 at morale <= 30, otherwise base tax.

`MoraleSystem.after_raid(morale: int, leaks: int) -> int` adds 5 after a clean raid or subtracts 5 per leak, clamped to 0..100. `condition_for(morale: int) -> String` returns Panic at <=30, Fatigue at <=50, Steady at <=70, otherwise Hope.

`SeasonEvent` extends Resource. Export `id`, `display_name`, `gold_delta`, `morale_delta`, `wall_damage`, and `tower_damage_delta`.

`SeasonSystem.events_for(season_index: int, rng: RandomNumberGenerator, count: int) -> Array[Resource]` selects distinct events for the requested season. Use two event resources per season. Spring has Festival (+20 gold, +8 morale) and Visitors (+40 gold). Summer has Drought (-6 morale, 2 wall damage) and Heatwave (-8 morale). Autumn has Harvest (+50 gold, +4 morale) and Rain (-1 tower damage for the season). Winter has Hunger (-10 morale) and Blizzard (2 wall damage). Count is bounded to the available events. Index wraps over four seasons.

`GameState.new_game(difficulty_id: String = "normal", seed_value: int = 1)` supports reproducible event selection. Add `difficulty`, `morale = 60`, `dragon_threat = 0`, `last_tax = 0`, `season_damage_modifier = 0`, `active_events`, `max_core_hp = 30`, and a seeded RNG. `advance_season()` increments Spring/Summer/Autumn/Winter and year, applies raid morale, one tax payment, events, and threat capped at 100. The first planning phase starts at Year 1 Spring with its original treasury, and no initial event.

Clean raid reward remains 75. Breached raids earn zero reward but still advance the season and collect tax. Dead cities cannot start raids or perform purchases. Year scaling adds 1 enemy and 2 enemy HP per year after Year 1, capped at 12 enemies. Core damage remains difficulty-driven.

Planning purchases are atomic. `upgrade_tower(position)` costs 75, adds 2 damage, and caps towers at level 3. `fortify_wall()` costs 100, adds 15 maximum and current core HP, and caps at wall level 3. `repair_wall()` costs 50 and repairs up to 10 HP, rejecting full-health walls. `rally_citizens()` costs 50, adds 10 morale up to 100, rejecting full morale. These wall actions upgrade the existing city-core defensive health. Spatial wall entities belong to Phase 3.

## Review Focus

- Repeated turn/start/button actions must not collect tax twice.
- Lost raids and lethal seasonal damage must produce a consistent game-over state.
- Hard selection must survive menu scene transitions and reset.
- Failed purchases must preserve gold, levels, health, and morale.
- A temporary Rain modifier must not permanently change a tower's base damage.

## Task 1 - Balance Resources And Pure Systems

Create `godot/scripts/game/difficulty_config.gd`, `godot/scripts/systems/{economy_system,morale_system,season_system,season_event}.gd`, resources under `godot/resources/{difficulties,events}/`, and `godot/tests/test_campaign_systems.gd`.

- [x] Add tests for Normal/Hard values, fallback, morale boundaries, tax at 30/31/69/70, event selection and no duplicate events.
- [x] Run tests and observe failure for missing systems.
- [x] Implement the interfaces and balance values above.
- [x] Run the complete headless suite successfully.

Representative assertions follow.

```gdscript
t.assert_eq(EconomySystem.tax_for(60, normal), 80, "normal tax")
t.assert_eq(EconomySystem.tax_for(70, hard), 70, "hope boosts hard tax")
t.assert_eq(MoraleSystem.after_raid(2, 3), 0, "panic clamps")
t.assert_eq(MoraleSystem.after_raid(98, 0), 100, "hope clamps")
```

## Task 2 - Campaign Integration And Planning Purchases

Modify `game_state.gd`, `turn_manager.gd`, `defense_tower.gd`, and `combat_resolver.gd`. Add `test_campaign.gd` and update raid tests for seasonal tax.

- [x] Add failing tests for four-season rollover, exactly-once transitions, death, deterministic events, Hard scaling, purchases, and modifier expiration.
- [x] Preserve raid outcomes and add season/tax/event information to completed turn results.
- [x] Resolve tower damage with a separate temporary modifier, minimum 1 damage.
- [x] Run the full suite and resolve all failures.

## Task 3 - Playable Campaign Interface

Modify `main_menu.gd` and `city_defense_screen.gd`. Add focused `campaign_hud.gd` if HUD construction makes the screen unwieldy. Extend scene smoke tests.

- [x] Add menu Normal/Hard selection and carry it through scene transition and Reset.
- [x] Add visible year, season, difficulty, morale, tax, threat, and event results.
- [x] Add build/select modes, tower inspection and upgrade, wall repair/fortification, and morale rally controls. Disable purchases with insufficient funds, invalid phase, or capped values.
- [x] Add health bars, selected tower range, and simple seasonal map colors to communicate the current game state.
- [x] Exercise purchase controls and a complete raid using scene tests and perform a rendered smoke check.

## Verification

From the implementation worktree, run the pinned local executable.

```powershell
& '.\.tools\godot-4.6.2\Godot_v4.6.2-stable_win64_console.exe' --headless --path godot -s res://tests/test_runner.gd
```

Expected exit code 0 and `ALL TESTS PASSED` with the actual assertion count.

Run the application through its main menu, verify difficulty selection, build and upgrade, play a full raid, and verify the next season's HUD. Update `godot/README.md` with the implemented controls and Phase 2 scope. Review the final diff and commit the implementation on `codex/till-end-seasonal-campaign`.
