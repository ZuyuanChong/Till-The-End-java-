extends RefCounted

const GameState = preload("res://scripts/game/game_state.gd")
const TurnManager = preload("res://scripts/game/turn_manager.gd")
const CombatResolver = preload("res://scripts/systems/combat_resolver.gd")
const EnemyUnit = preload("res://scripts/entities/enemy_unit.gd")

func run(t) -> void:
	_test_progression(t)
	_test_purchases(t)
	_test_difficulty_and_damage(t)
	_test_loss(t)

func _test_progression(t) -> void:
	var state = GameState.new_game("normal", 7)
	t.assert_eq(state.season_name(), "Spring", "campaign opens in Spring")
	t.assert_eq(state.gold, 200, "initial season keeps starting treasury")
	t.assert_eq(state.last_tax, 0, "initial planning has no duplicate tax")
	var result := _finish_clean_raid(state)
	t.assert_eq(state.season_name(), "Summer", "surviving advances one season")
	t.assert_eq(state.last_tax, 80, "normal raid earns one seasonal tax")
	t.assert_eq(state.gold, 355, "reward and tax have separate payments")
	t.assert_eq(state.dragon_threat, 10, "season raises dragon threat")
	t.assert_eq(state.active_events.size(), 1, "Normal selects one event")
	t.assert_eq(result.season.tax, 80, "turn result exposes season summary")
	var treasury: int = state.gold
	var ignored := TurnManager.new(state).advance_raid_turn()
	t.assert_eq(ignored.outcome, "not_in_raid", "completed raid cannot repeat")
	t.assert_eq(state.advance_season(), {}, "direct repeated transition is rejected")
	t.assert_eq(state.gold, treasury, "repeated actions cannot award extra tax")
	for index in range(3):
		_finish_clean_raid(state)
	t.assert_eq(state.year, 2, "four raids advance the year")
	t.assert_eq(state.season_index, 0, "Winter rolls over to Spring")
	state.dragon_threat = 99
	_finish_clean_raid(state)
	t.assert_eq(state.dragon_threat, 100, "dragon threat is capped")
	var same_seed = GameState.new_game("normal", 7)
	_finish_clean_raid(same_seed)
	t.assert_eq(same_seed.active_events[0].id, result.season.events[0], "seeded event replay matches")

func _test_purchases(t) -> void:
	var state = GameState.new_game()
	t.assert_true(state.build_tower(Vector2i(5, 3)), "build tower for upgrade")
	t.assert_true(state.upgrade_tower(Vector2i(5, 3)), "tower upgrades during planning")
	t.assert_eq(state.towers[0].level, 2, "tower level increases")
	t.assert_eq(state.towers[0].damage, 7, "tower upgrade adds damage")
	t.assert_eq(state.gold, 25, "tower upgrade spends 75 gold")
	t.assert_false(state.upgrade_tower(Vector2i(5, 3)), "insufficient upgrade funds rejected")
	t.assert_eq(state.gold, 25, "failed upgrade preserves gold")
	state.gold = 1000
	t.assert_true(state.upgrade_tower(Vector2i(5, 3)), "tower reaches level three")
	t.assert_false(state.upgrade_tower(Vector2i(5, 3)), "tower cap rejects more upgrades")
	var treasury: int = state.gold
	t.assert_false(state.upgrade_tower(Vector2i(3, 4)), "empty tile upgrade rejected")
	t.assert_eq(state.gold, treasury, "invalid tower upgrade preserves treasury")
	t.assert_true(state.fortify_wall(), "wall can be fortified")
	t.assert_eq(state.max_core_hp, 45, "fortification adds capacity")
	t.assert_eq(state.core_hp, 45, "fortification adds current health")
	t.assert_false(state.repair_wall(), "full wall rejects repair")
	state.core_hp = 39
	t.assert_true(state.repair_wall(), "damaged wall can be repaired")
	t.assert_eq(state.core_hp, 45, "repairs clamp at max health")
	state.morale = 95
	t.assert_true(state.rally_citizens(), "rally raises morale")
	t.assert_eq(state.morale, 100, "rally morale clamps")
	t.assert_false(state.rally_citizens(), "full morale rejects rally")
	state.start_raid()
	t.assert_false(state.fortify_wall(), "raid rejects wall purchases")
	t.assert_false(state.rally_citizens(), "raid rejects planning rally")
	t.assert_false(state.upgrade_tower(Vector2i(5, 3)), "raid rejects tower upgrades")
	var raid_gold: int = state.gold
	t.assert_false(state.repair_wall(), "raid rejects repairs")
	t.assert_eq(state.gold, raid_gold, "failed raid purchases preserve treasury")

func _test_difficulty_and_damage(t) -> void:
	var hard = GameState.new_game("hard", 3)
	hard.start_raid()
	t.assert_eq(hard.enemies.size(), 4, "Hard spawns four enemies")
	t.assert_eq(hard.enemies[0].hp, 7, "Hard enemies have seven health")
	for enemy in hard.enemies:
		enemy.take_damage(enemy.hp)
	TurnManager.new(hard).advance_raid_turn()
	t.assert_eq(hard.active_events.size(), 2, "Hard selects two distinct events")
	t.assert_eq(hard.last_tax, 50, "Hard tax is lower")
	t.assert_eq(hard.dragon_threat, 15, "Hard threat rises faster")
	_finish_clean_raid(hard)
	t.assert_eq(hard.season_damage_modifier, -1, "Autumn Rain applies a temporary damage penalty")
	_finish_clean_raid(hard)
	t.assert_eq(hard.season_damage_modifier, 0, "Winter expires Autumn Rain")
	hard.year = 2
	hard.start_raid()
	t.assert_eq(hard.enemies.size(), 5, "later years grow waves")
	t.assert_eq(hard.enemies[0].hp, 9, "later years strengthen enemies")
	var state = GameState.new_game()
	state.build_tower(Vector2i(5, 3))
	var tower = state.towers[0]
	var enemy = EnemyUnit.new(20, Vector2i(5, 4), 5, 20, 3)
	var attack := CombatResolver.resolve_tower_attack(tower, enemy, -1)
	t.assert_eq(attack.damage, 4, "seasonal damage applies to combat")
	t.assert_eq(tower.damage, 5, "seasonal damage preserves tower base")
	var minimum_attack := CombatResolver.resolve_tower_attack(tower, enemy, -100)
	t.assert_eq(minimum_attack.damage, 1, "seasonal penalties keep damage positive")
	var outside_range: Array[EnemyUnit] = [EnemyUnit.new(21, Vector2i(2, 4), 2)]
	t.assert_eq(CombatResolver.pick_target_for_tower(tower, outside_range), null, "adjacent lane cannot bypass Manhattan range")
	state.season_damage_modifier = -1
	_finish_clean_raid(state)
	t.assert_eq(state.season_damage_modifier, 0, "previous seasonal modifier expires")

func _test_loss(t) -> void:
	var state = GameState.new_game()
	state.core_hp = 1
	state.start_raid()
	var manager = TurnManager.new(state)
	for index in range(20):
		if state.phase != GameState.Phase.RAID:
			break
		manager.advance_raid_turn()
	t.assert_eq(state.phase, GameState.Phase.GAME_OVER, "core death ends campaign")
	t.assert_eq(state.season_index, 0, "lost raid cannot advance season")
	t.assert_eq(state.gold, 200, "lost raid cannot collect tax")
	t.assert_false(state.fortify_wall(), "dead city cannot fortify")
	t.assert_false(state.start_raid(), "dead city cannot restart raid")
	var seasonal_loss = GameState.new_game("hard", 1)
	seasonal_loss.core_hp = 2
	var result := _finish_clean_raid(seasonal_loss)
	t.assert_eq(seasonal_loss.phase, GameState.Phase.GAME_OVER, "lethal Drought ends campaign")
	t.assert_eq(result.outcome, "lost", "lethal seasonal event updates raid outcome")
	t.assert_eq(seasonal_loss.core_hp, 0, "seasonal damage clamps at zero")
	t.assert_eq(seasonal_loss.advance_season(), {}, "dead city cannot collect another season")

func _finish_clean_raid(state) -> Dictionary:
	state.start_raid()
	for enemy in state.enemies:
		enemy.take_damage(enemy.hp)
	return TurnManager.new(state).advance_raid_turn()
