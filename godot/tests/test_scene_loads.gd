extends RefCounted

func run(t) -> void:
	var menu_scene := load("res://scenes/main_menu.tscn")
	var city_scene := load("res://scenes/city_defense.tscn")

	t.assert_true(menu_scene != null, "main menu scene loads")
	t.assert_true(city_scene != null, "city defense scene loads")
	if city_scene == null:
		return

	var city_instance: Node = city_scene.instantiate()
	t.assert_true(city_instance != null, "city defense scene instantiates")
	city_instance.queue_free()
