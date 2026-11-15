extends RefCounted

const GameState = preload("res://scripts/game/game_state.gd")

func run(t) -> void:
	var menu: Node = load("res://scenes/main_menu.tscn").instantiate()
	t.root.add_child(menu)
	var selector: OptionButton = menu.get("difficulty_selector")
	t.assert_true(selector != null, "menu exposes difficulty selector")
	if selector != null:
		t.assert_eq(selector.item_count, 2, "menu offers Normal and Hard")
	menu.free()
	t.root.set_meta("difficulty_id", "hard")
	var city: Node = load("res://scenes/city_defense.tscn").instantiate()
	t.root.add_child(city)
	var state = city.get("state")
	t.assert_eq(str(state.difficulty.id), "hard", "city receives selected difficulty")
	city.call("_on_reset_pressed")
	state = city.get("state")
	t.assert_eq(str(state.difficulty.id), "hard", "reset retains selected difficulty")
	var click := InputEventMouseButton.new()
	click.button_index = MOUSE_BUTTON_LEFT
	click.pressed = true
	click.position = Vector2(48 + 5 * 40 + 20, 120 + 3 * 40 + 20)
	city.call("_unhandled_input", click)
	t.assert_eq(state.towers.size(), 1, "map input places a tower")
	var hud = city.get("hud")
	t.assert_true(hud != null, "campaign HUD is present")
	if hud != null:
		hud.upgrade_button.pressed.emit()
		t.assert_eq(state.towers[0].level, 2, "HUD upgrade signal reaches game rules")
		t.assert_eq(state.gold, 25, "HUD upgrade charges correct price")
		t.assert_true(hud.upgrade_button.disabled, "HUD disables unaffordable upgrade")
		state.gold = 300
		city.call("_update_hud")
		hud.fortify_button.pressed.emit()
		t.assert_eq(state.wall_level, 2, "HUD fortifies the city wall")
		t.assert_true(hud.status_label.text.contains("Spring"), "HUD shows season")
		t.assert_true(hud.status_label.text.contains("Hard"), "HUD shows difficulty")
	city.call("_on_start_raid_pressed")
	if hud != null:
		t.assert_true(hud.fortify_button.disabled, "raid disables planning purchases")
	city.free()
	t.root.remove_meta("difficulty_id")
