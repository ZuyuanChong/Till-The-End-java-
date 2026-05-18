extends Control

func _ready() -> void:
	var root := CenterContainer.new()
	root.set_anchors_preset(Control.PRESET_FULL_RECT)
	add_child(root)

	var panel := PanelContainer.new()
	panel.custom_minimum_size = Vector2(420, 260)
	root.add_child(panel)

	var box := VBoxContainer.new()
	box.add_theme_constant_override("separation", 16)
	panel.add_child(box)

	var title := Label.new()
	title.text = "Till The End"
	title.horizontal_alignment = HORIZONTAL_ALIGNMENT_CENTER
	title.add_theme_font_size_override("font_size", 36)
	box.add_child(title)

	var subtitle := Label.new()
	subtitle.text = "District Defense Prototype"
	subtitle.horizontal_alignment = HORIZONTAL_ALIGNMENT_CENTER
	subtitle.add_theme_font_size_override("font_size", 18)
	box.add_child(subtitle)

	var start_button := Button.new()
	start_button.text = "New Defense"
	start_button.pressed.connect(_on_start_pressed)
	box.add_child(start_button)

	var quit_button := Button.new()
	quit_button.text = "Quit"
	quit_button.pressed.connect(_on_quit_pressed)
	box.add_child(quit_button)

func _on_start_pressed() -> void:
	get_tree().change_scene_to_file("res://scenes/city_defense.tscn")

func _on_quit_pressed() -> void:
	get_tree().quit()
