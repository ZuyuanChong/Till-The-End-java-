extends RefCounted

const GameState = preload("res://scripts/game/game_state.gd")

func run(t) -> void:
	var state := GameState.new_game()

	t.assert_eq(state.gold, 200, "new game starts with original-game-inspired gold")
	t.assert_eq(state.core_hp, 30, "city core starts alive")
	t.assert_eq(state.phase, GameState.Phase.PLANNING, "new game starts in planning phase")
	t.assert_eq(state.road_path[0], Vector2i(0, 4), "road starts at west spawn")
	t.assert_eq(state.city_core_position, Vector2i(15, 4), "core is on east edge")
	t.assert_true(state.is_road(Vector2i(3, 4)), "road tile is recognized")
	t.assert_false(state.is_buildable(Vector2i(3, 4)), "road tile cannot receive towers")
	t.assert_true(state.is_buildable(Vector2i(3, 3)), "clear adjacent tile can receive towers")
