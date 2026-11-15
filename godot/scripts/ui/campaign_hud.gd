extends CanvasLayer

const GameState = preload("res://scripts/game/game_state.gd")
const MoraleSystem = preload("res://scripts/systems/morale_system.gd")

signal action_requested(action: String)
signal build_mode_changed(enabled: bool)

var status_label: Label
var event_label: Label
var log_label: Label
var selection_label: Label
var health_bar: ProgressBar
var threat_bar: ProgressBar
var start_button: Button
var next_turn_button: Button
var upgrade_button: Button
var repair_button: Button
var fortify_button: Button
var rally_button: Button

func _ready() -> void:
	var panel := PanelContainer.new()
	panel.position = Vector2(748, 24)
	panel.size = Vector2(500, 672)
	var style := StyleBoxFlat.new()
	style.bg_color = Color("20272a")
	style.content_margin_left = 20
	style.content_margin_right = 20
	style.content_margin_top = 16
	style.content_margin_bottom = 16
	panel.add_theme_stylebox_override("panel", style)
	add_child(panel)
	var box := VBoxContainer.new()
	box.add_theme_constant_override("separation", 8)
	panel.add_child(box)
	var title := Label.new()
	title.text = "Kuala Lumpur"
	title.add_theme_font_size_override("font_size", 26)
	box.add_child(title)
	status_label = _label(box)
	health_bar = _bar(box, Color("65bfa0"))
	threat_bar = _bar(box, Color("e47870"))
	event_label = _label(box)
	event_label.custom_minimum_size.y = 42
	var modes := HBoxContainer.new()
	box.add_child(modes)
	var group := ButtonGroup.new()
	for mode in ["Build", "Inspect"]:
		var button := Button.new()
		button.text = mode
		button.toggle_mode = true
		button.button_group = group
		button.button_pressed = mode == "Build"
		button.size_flags_horizontal = Control.SIZE_EXPAND_FILL
		button.pressed.connect(func(): build_mode_changed.emit(button.text == "Build"))
		modes.add_child(button)
	selection_label = _label(box)
	selection_label.custom_minimum_size.y = 42
	upgrade_button = _button(box, "Upgrade Tower  75g", "Add 2 damage, up to level 3", "upgrade")
	var defenses := HBoxContainer.new()
	box.add_child(defenses)
	repair_button = _button(defenses, "Repair Wall  50g", "Restore up to 10 wall health", "repair")
	fortify_button = _button(defenses, "Fortify Wall  100g", "Add 15 wall capacity and health, up to level 3", "fortify")
	rally_button = _button(box, "Rally Citizens  50g", "Raise morale by 10", "rally")
	var raid_buttons := HBoxContainer.new()
	box.add_child(raid_buttons)
	start_button = _button(raid_buttons, "Start Raid", "Commit the city's defenses", "start")
	next_turn_button = _button(raid_buttons, "Next Turn", "Resolve enemy movement and tower attacks", "turn")
	_button(raid_buttons, "Reset", "Start a fresh campaign with the same difficulty", "reset")
	log_label = _label(box)
	log_label.size_flags_vertical = Control.SIZE_EXPAND_FILL

func refresh(state, selected_position: Vector2i, message: String) -> void:
	var phase_name: String = "Planning"
	if state.phase == GameState.Phase.RAID:
		phase_name = "Raid - turn %d" % state.raid_turn
	elif state.phase == GameState.Phase.GAME_OVER:
		phase_name = "City fallen"
	status_label.text = "Year %d - %s | %s\n%s | Gold %d | Last tax %d\nWall %d/%d | Morale %d - %s\nDragon threat %d%% | Enemies %d" % [
		state.year, state.season_name(), state.difficulty.display_name,
		phase_name, state.gold, state.last_tax, state.core_hp, state.max_core_hp,
		state.morale, MoraleSystem.condition_for(state.morale), state.dragon_threat, state.enemies.size()]
	health_bar.max_value = state.max_core_hp
	health_bar.value = state.core_hp
	health_bar.tooltip_text = "City wall %d / %d" % [state.core_hp, state.max_core_hp]
	threat_bar.value = state.dragon_threat
	threat_bar.tooltip_text = "Dragon threat %d%%" % state.dragon_threat
	var event_names: PackedStringArray = []
	for event in state.active_events:
		event_names.append(event.display_name)
	event_label.text = "Season events - " + (", ".join(event_names) if not event_names.is_empty() else "Calm")
	if state.season_damage_modifier != 0:
		event_label.text += "\nTower damage %+d this season" % state.season_damage_modifier
	var tower = state.get_tower_at(selected_position)
	selection_label.text = "Tower 100g | Wall level %d" % state.wall_level
	if tower != null:
		selection_label.text = "Tower [%d, %d] | Level %d\nDamage %d | Range %d" % [
			selected_position.x, selected_position.y, tower.level,
			maxi(1, tower.damage + state.season_damage_modifier), tower.range_tiles]
	upgrade_button.disabled = not state.can_upgrade_tower(selected_position)
	repair_button.disabled = not state.can_repair_wall()
	fortify_button.disabled = not state.can_fortify_wall()
	rally_button.disabled = not state.can_rally_citizens()
	start_button.disabled = state.phase != GameState.Phase.PLANNING
	next_turn_button.disabled = state.phase != GameState.Phase.RAID
	log_label.text = message

func _label(parent: Node) -> Label:
	var label := Label.new()
	label.autowrap_mode = TextServer.AUTOWRAP_WORD_SMART
	label.add_theme_font_size_override("font_size", 16)
	parent.add_child(label)
	return label

func _bar(parent: Node, color: Color) -> ProgressBar:
	var bar := ProgressBar.new()
	bar.custom_minimum_size.y = 10
	bar.show_percentage = false
	var fill := StyleBoxFlat.new()
	fill.bg_color = color
	bar.add_theme_stylebox_override("fill", fill)
	parent.add_child(bar)
	return bar

func _button(parent: Node, text: String, tooltip: String, action: String) -> Button:
	var button := Button.new()
	button.text = text
	button.tooltip_text = tooltip
	button.custom_minimum_size.y = 36
	button.size_flags_horizontal = Control.SIZE_EXPAND_FILL
	button.pressed.connect(func(): action_requested.emit(action))
	parent.add_child(button)
	return button
