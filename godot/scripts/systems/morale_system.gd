class_name MoraleSystem
extends RefCounted

static func after_raid(morale: int, leaks: int) -> int:
	return clampi(morale + 5 if leaks == 0 else morale - 5 * leaks, 0, 100)

static func condition_for(morale: int) -> String:
	if morale <= 30:
		return "Panic"
	if morale <= 50:
		return "Fatigue"
	if morale <= 70:
		return "Steady"
	return "Hope"
