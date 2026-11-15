extends RefCounted

const DifficultyConfig = preload("res://scripts/game/difficulty_config.gd")
const EconomySystem = preload("res://scripts/systems/economy_system.gd")
const MoraleSystem = preload("res://scripts/systems/morale_system.gd")
const SeasonEvent = preload("res://scripts/systems/season_event.gd")
const SeasonSystem = preload("res://scripts/systems/season_system.gd")

func run(t) -> void:
	var initial_assertions: int = t.assertions
	var initial_failures: int = t.failures
	_test_difficulty_and_tax(t, DifficultyConfig, EconomySystem)
	_test_morale(t, MoraleSystem)
	_test_events(t, SeasonSystem, SeasonEvent)
	print("Campaign systems: %d assertions, %d failures" % [
		t.assertions - initial_assertions, t.failures - initial_failures])

func _test_difficulty_and_tax(t, config, economy) -> void:
	var normal: Resource = config.for_id()
	var hard: Resource = config.for_id("hard")
	var fields := ["id", "display_name", "base_tax", "wave_size", "enemy_health",
		"enemy_core_damage", "threat_per_season", "events_per_season"]
	var normal_values := ["normal", "Normal", 80, 3, 5, 3, 10, 1]
	var hard_values := ["hard", "Hard", 50, 4, 7, 4, 15, 2]
	var fallback: Resource = config.for_id("unknown")
	for i in range(fields.size()):
		t.assert_eq(normal.get(fields[i]), normal_values[i], "Normal %s" % fields[i])
		t.assert_eq(hard.get(fields[i]), hard_values[i], "Hard %s" % fields[i])
		t.assert_eq(fallback.get(fields[i]), normal_values[i], "fallback %s" % fields[i])
	t.assert_eq(config.for_id("normal").id, "normal", "explicit Normal selection")
	for row in [[0, 60, 30], [30, 60, 30], [31, 80, 50], [60, 80, 50],
		[69, 80, 50], [70, 100, 70], [100, 100, 70]]:
		t.assert_eq(economy.tax_for(row[0], normal), row[1], "Normal tax at %d" % row[0])
		t.assert_eq(economy.tax_for(row[0], hard), row[2], "Hard tax at %d" % row[0])
	var custom: Resource = config.new()
	custom.base_tax = 123
	t.assert_eq(economy.tax_for(60, custom), 123, "tax reads resource balance")
	t.assert_eq(normal.base_tax, 80, "tax does not mutate difficulty")

func _test_morale(t, morale) -> void:
	for row in [[60, 0, 65], [60, 1, 55], [60, 3, 45], [2, 3, 0],
		[98, 0, 100], [0, 0, 5], [100, 1, 95]]:
		t.assert_eq(morale.after_raid(row[0], row[1]), row[2], "raid morale %s" % str(row))
	for row in [[0, "Panic"], [30, "Panic"], [31, "Fatigue"], [50, "Fatigue"],
		[51, "Steady"], [70, "Steady"], [71, "Hope"], [100, "Hope"]]:
		t.assert_eq(morale.condition_for(row[0]), row[1], "condition at %d" % row[0])

func _test_events(t, seasons, event_script) -> void:
	var expected := [
		{"festival": ["Festival", 20, 8, 0, 0], "visitors": ["Visitors", 40, 0, 0, 0]},
		{"drought": ["Drought", 0, -6, 2, 0], "heatwave": ["Heatwave", 0, -8, 0, 0]},
		{"harvest": ["Harvest", 50, 4, 0, 0], "rain": ["Rain", 0, 0, 0, -1]},
		{"hunger": ["Hunger", 0, -10, 0, 0], "blizzard": ["Blizzard", 0, 0, 2, 0]},
	]
	var fields := ["display_name", "gold_delta", "morale_delta", "wall_damage", "tower_damage_delta"]
	var rng := RandomNumberGenerator.new()
	rng.seed = 42
	for index in range(4):
		var events: Array[Resource] = seasons.events_for(index, rng, 99)
		t.assert_eq(events.size(), 2, "count bounded to two events for season %d" % index)
		var ids := []
		for event in events:
			t.assert_eq(event.get_script(), event_script, "event uses SeasonEvent Resource")
			t.assert_false(ids.has(event.id), "events are distinct")
			ids.append(event.id)
			t.assert_true(expected[index].has(event.id), "event belongs to requested season")
			if not expected[index].has(event.id):
				continue
			for i in range(fields.size()):
				t.assert_eq(event.get(fields[i]), expected[index][event.id][i], "%s %s" % [event.id, fields[i]])
		for count in [-1, 0, 1, 2]:
			t.assert_eq(seasons.events_for(index, rng, count).size(), maxi(count, 0), "bounded event count %d" % count)
	for row in [[4, ["festival", "visitors"]], [9, ["drought", "heatwave"]],
		[-1, ["blizzard", "hunger"]]]:
		var ids := []
		for event in seasons.events_for(row[0], rng, 2):
			ids.append(event.id)
		ids.sort()
		t.assert_eq(ids, row[1], "season index wraps: %d" % row[0])
	var first := RandomNumberGenerator.new()
	var second := RandomNumberGenerator.new()
	first.seed = 12345
	second.seed = 12345
	var seen := {}
	for i in range(32):
		var a: Array[Resource] = seasons.events_for(0, first, 1)
		var b: Array[Resource] = seasons.events_for(0, second, 1)
		t.assert_eq(a[0].id, b[0].id, "same seed reproduces selection %d" % i)
		seen[a[0].id] = true
	t.assert_eq(seen.size(), 2, "RNG can select either seasonal event")
