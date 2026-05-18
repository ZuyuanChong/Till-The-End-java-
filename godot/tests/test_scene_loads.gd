extends RefCounted

const GameState = preload("res://scripts/game/game_state.gd")

func run(t) -> void:
	var menu_scene := load("res://scenes/main_menu.tscn")
	var city_scene := load("res://scenes/city_defense.tscn")

	t.assert_true(menu_scene != null, "main menu scene loads")
	t.assert_true(city_scene != null, "city defense scene loads")
	if menu_scene == null or city_scene == null:
		return

	var menu_instance: Node = menu_scene.instantiate()
	var city_instance: Node = city_scene.instantiate()
	t.assert_true(menu_instance != null, "main menu scene instantiates")
	t.assert_true(city_instance != null, "city defense scene instantiates")

	t.root.add_child(menu_instance)
	t.root.add_child(city_instance)

	t.assert_true(_has_button_with_text(menu_instance, "New Defense"), "main menu has New Defense button")
	t.assert_true(_has_button_with_text(menu_instance, "Quit"), "main menu has Quit button")
	t.assert_true(_has_label_with_text(menu_instance, "Till The End"), "main menu has title label")

	t.assert_true(_has_button_with_text(city_instance, "Start Raid"), "city defense has Start Raid button")
	t.assert_true(_has_button_with_text(city_instance, "Next Turn"), "city defense has Next Turn button")
	t.assert_true(_has_button_with_text(city_instance, "Reset"), "city defense has Reset button")
	t.assert_true(city_instance.get("state") != null, "city defense exposes state after ready")
	t.assert_true(city_instance.get("turn_manager") != null, "city defense exposes turn manager after ready")

	city_instance.call("_on_start_raid_pressed")
	var state: GameState = city_instance.get("state")
	t.assert_eq(state.phase, GameState.Phase.RAID, "start raid handler switches to raid phase")

	city_instance.call("_on_next_turn_pressed")
	t.assert_true(
		state.raid_turn == 1
		or state.phase == GameState.Phase.PLANNING
		or state.phase == GameState.Phase.GAME_OVER,
		"next turn handler advances or resolves raid"
	)

	menu_instance.queue_free()
	city_instance.queue_free()

func _has_button_with_text(root: Node, text: String) -> bool:
	return _has_control_with_text(root, Button, text)

func _has_label_with_text(root: Node, text: String) -> bool:
	return _has_control_with_text(root, Label, text)

func _has_control_with_text(root: Node, control_type: Variant, text: String) -> bool:
	if is_instance_of(root, control_type) and root.get("text") == text:
		return true

	for child in root.get_children():
		if _has_control_with_text(child, control_type, text):
			return true

	return false
