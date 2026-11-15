extends Node2D

const GameState = preload("res://scripts/game/game_state.gd")
const TurnManager = preload("res://scripts/game/turn_manager.gd")
const CampaignHud = preload("res://scripts/ui/campaign_hud.gd")
const CombatResolver = preload("res://scripts/systems/combat_resolver.gd")

const TILE_SIZE := 40
const MAP_ORIGIN := Vector2(48, 120)
const SEASON_COLORS := [Color("355a43"), Color("53653c"), Color("5b5941"), Color("617476")]

var state: GameState
var turn_manager: TurnManager
var hud: CanvasLayer
var selected_position := Vector2i(-1, -1)
var build_mode := true
var last_log := "Year 1 - the city prepares."

func _ready() -> void:
	var difficulty_id := str(get_tree().root.get_meta("difficulty_id", "normal"))
	state = GameState.new_game(difficulty_id, randi())
	turn_manager = TurnManager.new(state)
	hud = CampaignHud.new()
	add_child(hud)
	hud.action_requested.connect(_on_action)
	hud.build_mode_changed.connect(func(enabled: bool): build_mode = enabled)
	_update_hud()

func _unhandled_input(event: InputEvent) -> void:
	if not event is InputEventMouseButton or not event.pressed or event.button_index != MOUSE_BUTTON_LEFT:
		return
	var local: Vector2 = event.position - MAP_ORIGIN
	var position := Vector2i(floori(local.x / TILE_SIZE), floori(local.y / TILE_SIZE))
	if not state.is_in_bounds(position):
		return
	selected_position = position
	if build_mode and state.get_tower_at(position) == null and state.phase == GameState.Phase.PLANNING:
		if state.build_tower(position):
			last_log = "Tower commissioned - 100 gold."
		else:
			last_log = "Tower placement unavailable."
	_update_hud()

func _draw() -> void:
	if state == null:
		return
	var selected_tower = state.get_tower_at(selected_position)
	for x in range(GameState.MAP_WIDTH):
		for y in range(GameState.MAP_HEIGHT):
			var cell := Vector2i(x, y)
			var rect := Rect2(_tile_position(cell), Vector2(TILE_SIZE - 2, TILE_SIZE - 2))
			var fill: Color = SEASON_COLORS[state.season_index]
			if state.is_road(cell):
				fill = Color("8a8170")
			if selected_tower != null and CombatResolver.manhattan_distance(cell, selected_position) <= selected_tower.range_tiles:
				fill = fill.lightened(0.15)
			draw_rect(rect, fill)
			if state.is_core(cell):
				_draw_keep(rect.position)
			if cell == selected_position:
				draw_rect(rect, Color("faf2c0"), false, 2)
	for tower in state.towers:
		var origin := _tile_position(tower.grid_position)
		draw_rect(Rect2(origin + Vector2(8, 12), Vector2(24, 24)), Color("7ba3b6"))
		draw_rect(Rect2(origin + Vector2(6, 6), Vector2(28, 10)), Color("ced9dc"))
		draw_rect(Rect2(origin + Vector2(17, 17), Vector2(6, 12)), Color("334954"))
		for level in range(tower.level):
			draw_rect(Rect2(origin + Vector2(8 + level * 9, 4), Vector2(5, 5)), Color("f2d06e"))
	for enemy in state.enemies:
		var center := _tile_position(enemy.grid_position) + Vector2(20 + (enemy.id % 3 - 1) * 9, 20)
		draw_rect(Rect2(center - Vector2(6, 9), Vector2(12, 18)), Color("cc6360"))
		draw_rect(Rect2(center + Vector2(-3, -6), Vector2(2, 2)), Color.WHITE)
		draw_rect(Rect2(center + Vector2(2, -6), Vector2(2, 2)), Color.WHITE)
		draw_rect(Rect2(center + Vector2(-7, -13), Vector2(14, 2)), Color("24272a"))
		draw_rect(Rect2(center + Vector2(-7, -13), Vector2(14.0 * enemy.hp / enemy.max_hp, 2)), Color("efbd7b"))

func _draw_keep(origin: Vector2) -> void:
	draw_rect(Rect2(origin + Vector2(3, 8), Vector2(34, 29)), Color("c1c8c7"))
	draw_rect(Rect2(origin + Vector2(2, 3), Vector2(10, 13)), Color("dce0df"))
	draw_rect(Rect2(origin + Vector2(28, 3), Vector2(10, 13)), Color("dce0df"))
	draw_rect(Rect2(origin + Vector2(16, 22), Vector2(9, 15)), Color("304f61"))

func _tile_position(cell: Vector2i) -> Vector2:
	return MAP_ORIGIN + Vector2(cell) * TILE_SIZE

func _on_action(action: String) -> void:
	match action:
		"start": _on_start_raid_pressed()
		"turn": _on_next_turn_pressed()
		"reset": _on_reset_pressed()
		"upgrade":
			if state.upgrade_tower(selected_position):
				last_log = "Tower upgraded - 75 gold."
		"repair":
			if state.repair_wall():
				last_log = "City wall repaired - 50 gold."
		"fortify":
			if state.fortify_wall():
				last_log = "City wall fortified - 100 gold."
		"rally":
			if state.rally_citizens():
				last_log = "Citizens rallied - 50 gold."
	_update_hud()

func _on_start_raid_pressed() -> void:
	if state.start_raid():
		last_log = "%d enemies approach the city." % state.enemies.size()
	_update_hud()

func _on_next_turn_pressed() -> void:
	var result := turn_manager.advance_raid_turn()
	match result.outcome:
		"raid_won": last_log = "Raid repelled. Reward +75 gold."
		"raid_survived": last_log = "City breached. The defenses held."
		"lost": last_log = "Kuala Lumpur has fallen."
		"continue": last_log = "Turn %d - %d shots, %d wall damage." % [state.raid_turn, result.tower_attacks.size(), result.core_damage]
	if result.has("season"):
		last_log += "\n%s, Year %d. Tax +%d gold." % [state.season_name(), state.year, state.last_tax]
	_update_hud()

func _on_reset_pressed() -> void:
	state = GameState.new_game(str(state.difficulty.id), randi())
	turn_manager = TurnManager.new(state)
	selected_position = Vector2i(-1, -1)
	last_log = "Year 1 - the city prepares."
	_update_hud()

func _update_hud() -> void:
	hud.refresh(state, selected_position, last_log)
	queue_redraw()
