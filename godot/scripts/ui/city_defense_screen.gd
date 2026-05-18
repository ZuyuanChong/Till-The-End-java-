extends Node2D

const GameState = preload("res://scripts/game/game_state.gd")
const TurnManager = preload("res://scripts/game/turn_manager.gd")

const TILE_SIZE := 40
const MAP_ORIGIN := Vector2(48, 120)

var state: GameState
var turn_manager: TurnManager
var status_label: Label
var log_label: Label
var start_button: Button
var next_turn_button: Button
var reset_button: Button
var last_log := "Build towers on open tiles, then start the raid."

func _ready() -> void:
	state = GameState.new_game()
	turn_manager = TurnManager.new(state)
	_build_hud()
	_update_hud()
	queue_redraw()

func _unhandled_input(event: InputEvent) -> void:
	if not (event is InputEventMouseButton):
		return
	if not event.pressed or event.button_index != MOUSE_BUTTON_LEFT:
		return

	var grid_position := _screen_to_grid(event.position)
	if state.phase == GameState.Phase.PLANNING:
		if state.build_tower(grid_position):
			last_log = "Built tower at %s. Gold remaining: %d." % [str(grid_position), state.gold]
		elif state.is_road(grid_position):
			last_log = "Road tiles must remain open for the prototype route."
		elif not state.is_in_bounds(grid_position):
			last_log = "Select a tile inside the city grid."
		else:
			last_log = "Cannot build there."

	_update_hud()
	queue_redraw()

func _draw() -> void:
	_draw_map()
	_draw_towers()
	_draw_enemies()

func _draw_map() -> void:
	for x in range(GameState.MAP_WIDTH):
		for y in range(GameState.MAP_HEIGHT):
			var grid_position := Vector2i(x, y)
			var rect := Rect2(
				MAP_ORIGIN + Vector2(x * TILE_SIZE, y * TILE_SIZE),
				Vector2(TILE_SIZE - 2, TILE_SIZE - 2)
			)
			var fill := Color(0.20, 0.33, 0.24)
			if state.is_road(grid_position):
				fill = Color(0.42, 0.33, 0.23)
			if state.is_core(grid_position):
				fill = Color(0.20, 0.30, 0.62)

			draw_rect(rect, fill, true)
			draw_rect(rect, Color(0.06, 0.08, 0.07), false, 1.0)

func _draw_towers() -> void:
	for tower in state.towers:
		var center := _grid_to_screen(tower.grid_position) + Vector2(TILE_SIZE / 2, TILE_SIZE / 2)
		var base := Rect2(center - Vector2(12, 12), Vector2(24, 24))
		draw_rect(base, Color(0.25, 0.55, 0.95), true)
		draw_rect(base, Color(0.88, 0.95, 1.0), false, 2.0)

func _draw_enemies() -> void:
	for enemy in state.enemies:
		var center := _grid_to_screen(enemy.grid_position) + Vector2(TILE_SIZE / 2, TILE_SIZE / 2)
		draw_circle(center, 12.0, Color(0.78, 0.18, 0.14))
		draw_arc(center, 15.0, 0.0, TAU, 24, Color(1.0, 0.76, 0.32), 2.0)

func _build_hud() -> void:
	var layer := CanvasLayer.new()
	add_child(layer)

	var panel := PanelContainer.new()
	panel.position = Vector2(760, 32)
	panel.custom_minimum_size = Vector2(460, 420)
	layer.add_child(panel)

	var box := VBoxContainer.new()
	box.add_theme_constant_override("separation", 10)
	panel.add_child(box)

	var title := Label.new()
	title.text = "Kuala Lumpur Defense"
	title.add_theme_font_size_override("font_size", 24)
	box.add_child(title)

	status_label = Label.new()
	status_label.autowrap_mode = TextServer.AUTOWRAP_WORD_SMART
	box.add_child(status_label)

	var button_row := HBoxContainer.new()
	button_row.add_theme_constant_override("separation", 8)
	box.add_child(button_row)

	start_button = Button.new()
	start_button.text = "Start Raid"
	start_button.pressed.connect(_on_start_raid_pressed)
	button_row.add_child(start_button)

	next_turn_button = Button.new()
	next_turn_button.text = "Next Turn"
	next_turn_button.pressed.connect(_on_next_turn_pressed)
	button_row.add_child(next_turn_button)

	reset_button = Button.new()
	reset_button.text = "Reset"
	reset_button.pressed.connect(_on_reset_pressed)
	button_row.add_child(reset_button)

	log_label = Label.new()
	log_label.autowrap_mode = TextServer.AUTOWRAP_WORD_SMART
	box.add_child(log_label)

func _on_start_raid_pressed() -> void:
	if state.start_raid():
		last_log = "Raid started. Advance turns to resolve enemy movement and tower fire."
	else:
		last_log = "Cannot start raid from the current phase."
	_update_hud()
	queue_redraw()

func _on_next_turn_pressed() -> void:
	var result := turn_manager.advance_raid_turn()
	if result.outcome == "raid_won":
		last_log = "Raid won. Reward gained. Build again before the next attack."
	elif result.outcome == "lost":
		last_log = "The city core has fallen."
	elif result.outcome == "continue":
		last_log = "Turn %d: %d moves, %d tower attacks, %d core damage." % [
			state.raid_turn,
			result.enemy_moves.size(),
			result.tower_attacks.size(),
			result.core_damage
		]
	else:
		last_log = "Start a raid before advancing turns."

	_update_hud()
	queue_redraw()

func _on_reset_pressed() -> void:
	state = GameState.new_game()
	turn_manager = TurnManager.new(state)
	last_log = "Prototype reset."
	_update_hud()
	queue_redraw()

func _update_hud() -> void:
	var phase_name := "Planning"
	if state.phase == GameState.Phase.RAID:
		phase_name = "Raid"
	elif state.phase == GameState.Phase.GAME_OVER:
		phase_name = "Game Over"
	elif state.phase == GameState.Phase.VICTORY:
		phase_name = "Victory"

	status_label.text = "Phase: %s\nGold: %d\nCore HP: %d\nTowers: %d\nEnemies: %d\nCommand Points: %d\nRaid Turn: %d" % [
		phase_name,
		state.gold,
		state.core_hp,
		state.towers.size(),
		state.enemies.size(),
		state.command_points,
		state.raid_turn
	]
	log_label.text = last_log
	start_button.disabled = state.phase != GameState.Phase.PLANNING
	next_turn_button.disabled = state.phase != GameState.Phase.RAID

func _screen_to_grid(screen_position: Vector2) -> Vector2i:
	var local := screen_position - MAP_ORIGIN
	return Vector2i(floori(local.x / TILE_SIZE), floori(local.y / TILE_SIZE))

func _grid_to_screen(grid_position: Vector2i) -> Vector2:
	return MAP_ORIGIN + Vector2(grid_position.x * TILE_SIZE, grid_position.y * TILE_SIZE)
