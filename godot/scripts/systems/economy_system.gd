class_name EconomySystem
extends RefCounted

static func tax_for(morale: int, difficulty: Resource) -> int:
	if morale >= 70:
		return difficulty.base_tax + 20
	if morale <= 30:
		return difficulty.base_tax - 20
	return difficulty.base_tax
