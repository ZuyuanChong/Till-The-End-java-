extends SceneTree

var failures := 0
var assertions := 0

func _init() -> void:
	run_all_tests()
	if failures == 0:
		print("ALL TESTS PASSED: %d assertions" % assertions)
		quit(0)
	else:
		push_error("TESTS FAILED: %d failures across %d assertions" % [failures, assertions])
		quit(1)

func run_all_tests() -> void:
	var dir := DirAccess.open("res://tests")
	if dir == null:
		fail("Cannot open res://tests")
		return

	var files := dir.get_files()
	files.sort()

	for file_name in files:
		if not file_name.begins_with("test_"):
			continue
		if not file_name.ends_with(".gd"):
			continue
		if file_name == "test_runner.gd":
			continue

		var script := load("res://tests/%s" % file_name)
		if script == null:
			fail("Could not load %s" % file_name)
			continue

		print("Running %s" % file_name)
		var test_instance: Variant = script.new()
		test_instance.run(self)

func assert_true(value: bool, message: String) -> void:
	assertions += 1
	if not value:
		fail(message)

func assert_false(value: bool, message: String) -> void:
	assertions += 1
	if value:
		fail(message)

func assert_eq(actual: Variant, expected: Variant, message: String) -> void:
	assertions += 1
	if actual != expected:
		fail("%s | expected=%s actual=%s" % [message, str(expected), str(actual)])

func fail(message: String) -> void:
	failures += 1
	push_error(message)
