class_name GameState
extends RefCounted

const DefenseTower = preload("res://scripts/entities/defense_tower.gd")
const EnemyUnit = preload("res://scripts/entities/enemy_unit.gd")
const DifficultyConfig = preload("res://scripts/game/difficulty_config.gd")
const EconomySystem = preload("res://scripts/systems/economy_system.gd")
const MoraleSystem = preload("res://scripts/systems/morale_system.gd")
const SeasonSystem = preload("res://scripts/systems/season_system.gd")

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
const UPGRADE_COST := 75
const FORTIFY_COST := 100
const REPAIR_COST := 50
const RALLY_COST := 50
const SEASONS := ["Spring", "Summer", "Autumn", "Winter"]

var phase: Phase = Phase.PLANNING
var gold := 200
var core_hp := 30
var max_core_hp := 30
var wall_level := 1
var difficulty: Resource
var morale := 60
var dragon_threat := 0
var last_tax := 0
var season_damage_modifier := 0
var active_events: Array[Resource] = []
var rng := RandomNumberGenerator.new()
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

static func new_game(difficulty_id: String = "normal", seed_value: int = 1) -> GameState:
	return new(difficulty_id, seed_value)

func _init(difficulty_id: String = "normal", seed_value: int = 1) -> void:
	reset(difficulty_id, seed_value)

func reset(difficulty_id: String = "normal", seed_value: int = 1) -> void:
	phase = Phase.PLANNING
	gold = 200
	core_hp = 30
	max_core_hp = 30
	wall_level = 1
	difficulty = DifficultyConfig.for_id(difficulty_id)
	morale = 60
	dragon_threat = 0
	last_tax = 0
	season_damage_modifier = 0
	active_events = []
	rng.seed = seed_value
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
	return can_spend(TOWER_COST) and is_buildable(grid_position)

func build_tower(grid_position: Vector2i) -> bool:
	if not can_build_tower(grid_position):
		return false

	var tower := DefenseTower.new(next_tower_id, grid_position)
	next_tower_id += 1
	towers.append(tower)
	gold -= TOWER_COST
	return true

func can_spend(cost: int) -> bool:
	return phase == Phase.PLANNING and core_hp > 0 and gold >= cost

func can_upgrade_tower(position: Vector2i) -> bool:
	var tower := get_tower_at(position)
	return can_spend(UPGRADE_COST) and tower != null and tower.level < 3

func upgrade_tower(position: Vector2i) -> bool:
	if not can_upgrade_tower(position):
		return false
	var tower := get_tower_at(position)
	tower.level += 1
	tower.damage += 2
	gold -= UPGRADE_COST
	return true

func can_fortify_wall() -> bool:
	return can_spend(FORTIFY_COST) and wall_level < 3

func fortify_wall() -> bool:
	if not can_fortify_wall():
		return false
	wall_level += 1
	max_core_hp += 15
	core_hp += 15
	gold -= FORTIFY_COST
	return true

func can_repair_wall() -> bool:
	return can_spend(REPAIR_COST) and core_hp < max_core_hp

func repair_wall() -> bool:
	if not can_repair_wall():
		return false
	core_hp = mini(max_core_hp, core_hp + 10)
	gold -= REPAIR_COST
	return true

func can_rally_citizens() -> bool:
	return can_spend(RALLY_COST) and morale < 100

func rally_citizens() -> bool:
	if not can_rally_citizens():
		return false
	morale = mini(100, morale + 10)
	gold -= RALLY_COST
	return true

func season_name() -> String:
	return SEASONS[season_index]

func advance_season() -> Dictionary:
	# A resolved raid consumes its transition by returning to planning here.
	if phase != Phase.RAID or not enemies.is_empty() or core_hp <= 0:
		return {}
	season_index = (season_index + 1) % SEASONS.size()
	if season_index == 0:
		year += 1
	morale = MoraleSystem.after_raid(morale, enemies_leaked_this_raid)
	last_tax = EconomySystem.tax_for(morale, difficulty)
	gold += last_tax
	season_damage_modifier = 0
	active_events = SeasonSystem.events_for(season_index, rng, difficulty.events_per_season)
	var event_ids: Array[String] = []
	for event in active_events:
		gold = maxi(0, gold + event.gold_delta)
		morale = clampi(morale + event.morale_delta, 0, 100)
		core_hp = maxi(0, core_hp - event.wall_damage)
		season_damage_modifier += event.tower_damage_delta
		event_ids.append(str(event.id))
	dragon_threat = mini(100, dragon_threat + difficulty.threat_per_season + 5 * enemies_leaked_this_raid)
	command_points = 0
	phase = Phase.PLANNING if core_hp > 0 else Phase.GAME_OVER
	return {"tax": last_tax, "events": event_ids, "season": season_name(), "year": year}

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
	var year_bonus := maxi(0, year - 1)
	for tower in towers:
		tower.cooldown_remaining = 0

	for index in range(mini(12, difficulty.wave_size + year_bonus)):
		var enemy := EnemyUnit.new(next_enemy_id, road_path[0], 0,
			difficulty.enemy_health + 2 * year_bonus, difficulty.enemy_core_damage)
		next_enemy_id += 1
		enemies.append(enemy)

	return true
