class_name TurnManager
extends RefCounted

const GameState = preload("res://scripts/game/game_state.gd")
const CombatResolver = preload("res://scripts/systems/combat_resolver.gd")
const EnemyUnit = preload("res://scripts/entities/enemy_unit.gd")

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
