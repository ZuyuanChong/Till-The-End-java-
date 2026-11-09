extends RefCounted

const GameState = preload("res://scripts/game/game_state.gd")

func run(t) -> void:
	var state := GameState.new_game()
	var build_position := Vector2i(3, 3)

	t.assert_true(state.build_tower(build_position), "can build tower on clear tile")
	t.assert_eq(state.gold, 100, "building tower spends gold")
	t.assert_eq(state.towers.size(), 1, "tower list receives built tower")
	t.assert_eq(state.towers[0].grid_position, build_position, "tower stores grid position")
	t.assert_false(state.build_tower(build_position), "cannot build two towers on one tile")
	t.assert_false(state.build_tower(Vector2i(3, 4)), "cannot build tower on road")

	state.gold = 99
	t.assert_false(state.build_tower(Vector2i(4, 3)), "cannot build tower without enough gold")
