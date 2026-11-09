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
var enemies_leaked_this_raid := 0
var year := 1
var season_index := 0
var city_core_position := Vector2i(15, 4)
var road_path: Array[Vector2i] = []
var towers: Array[DefenseTower] = []
var enemies: Array[EnemyUnit] = []
var next_tower_id := 1
var next_enemy_id := 1

static func new_game() -> GameState:
	return new()

func _init() -> void:
	reset()

func reset() -> void:
	phase = Phase.PLANNING
	gold = 200
	core_hp = 30
	command_points = 0
	command_points_per_turn = 2
	raid_turn = 0
	enemies_leaked_this_raid = 0
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
	enemies_leaked_this_raid = 0
	command_points = command_points_per_turn
	enemies = []

	for index in range(3):
		var enemy := EnemyUnit.new(next_enemy_id, road_path[0], 0, 5, 3)
		next_enemy_id += 1
		enemies.append(enemy)

	return true
