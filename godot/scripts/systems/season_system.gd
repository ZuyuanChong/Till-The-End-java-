class_name SeasonSystem
extends RefCounted

const EVENT_POOLS := [
	[preload("res://resources/events/festival.tres"), preload("res://resources/events/visitors.tres")],
	[preload("res://resources/events/drought.tres"), preload("res://resources/events/heatwave.tres")],
	[preload("res://resources/events/harvest.tres"), preload("res://resources/events/rain.tres")],
	[preload("res://resources/events/hunger.tres"), preload("res://resources/events/blizzard.tres")],
]

static func events_for(season_index: int, rng: RandomNumberGenerator, count: int) -> Array[Resource]:
	var available: Array = EVENT_POOLS[posmod(season_index, EVENT_POOLS.size())].duplicate()
	var selected: Array[Resource] = []
	for _i in range(clampi(count, 0, available.size())):
		selected.append(available.pop_at(rng.randi_range(0, available.size() - 1)))
	return selected
