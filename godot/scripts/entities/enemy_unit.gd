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
