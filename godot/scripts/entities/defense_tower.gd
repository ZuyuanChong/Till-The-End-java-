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
