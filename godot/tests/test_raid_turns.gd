extends RefCounted

const GameState = preload("res://scripts/game/game_state.gd")
const TurnManager = preload("res://scripts/game/turn_manager.gd")

func run(t) -> void:
	var state := GameState.new_game()
	t.assert_true(state.build_tower(Vector2i(5, 3)), "tower is built near the road")
	t.assert_true(state.start_raid(), "raid starts from planning phase")
	t.assert_eq(state.phase, GameState.Phase.RAID, "phase switches to raid")
	t.assert_eq(state.enemies.size(), 3, "first wave spawns three enemies")
	t.assert_eq(state.enemies[0].max_hp, 5, "first wave enemies use prototype raid health")

	var manager := TurnManager.new(state)
	var first_position := state.enemies[0].grid_position
	var result := manager.advance_raid_turn()

	t.assert_true(result.enemy_moves.size() > 0, "turn records enemy movement")
	t.assert_false(state.enemies[0].grid_position == first_position, "enemy advances along road")

	for index in range(12):
		if state.phase != GameState.Phase.RAID:
			break
		result = manager.advance_raid_turn()

	t.assert_eq(state.phase, GameState.Phase.PLANNING, "raid returns to planning after enemies are defeated")
	t.assert_eq(state.gold, 175, "raid reward is paid after tower cost")
	t.assert_eq(result.outcome, "raid_won", "defeated raid reports win outcome")

	var breach_state := GameState.new_game()
	t.assert_true(breach_state.start_raid(), "no-tower raid starts")
	var breach_manager := TurnManager.new(breach_state)
	var breach_result := {}
	for index in range(20):
		if breach_state.phase != GameState.Phase.RAID:
			break
		breach_result = breach_manager.advance_raid_turn()

	t.assert_eq(breach_state.phase, GameState.Phase.PLANNING, "breached raid returns to planning if core survives")
	t.assert_true(breach_state.core_hp > 0, "breached raid leaves surviving core above zero hp")
	t.assert_true(breach_state.core_hp < 30, "breached raid damages the core")
	t.assert_eq(breach_state.gold, 200, "breached raid does not pay reward")
	t.assert_eq(breach_result.outcome, "raid_survived", "breached raid reports survived outcome")
