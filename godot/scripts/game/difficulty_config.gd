class_name DifficultyConfig
extends Resource

@export var id: String = ""
@export var display_name: String = ""
@export var base_tax: int = 0
@export var wave_size: int = 0
@export var enemy_health: int = 0
@export var enemy_core_damage: int = 0
@export var threat_per_season: int = 0
@export var events_per_season: int = 0

static func for_id(id: String = "normal") -> Resource:
	if id == "hard":
		return load("res://resources/difficulties/hard.tres")
	return load("res://resources/difficulties/normal.tres")
