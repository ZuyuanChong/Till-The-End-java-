class_name CombatResolver
extends RefCounted

const DefenseTower = preload("res://scripts/entities/defense_tower.gd")
const EnemyUnit = preload("res://scripts/entities/enemy_unit.gd")

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
