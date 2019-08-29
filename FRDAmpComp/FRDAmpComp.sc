FRDAmpComp {
	*new { | freq=440 |
		^super.new.init(freq)
	}
	init { | freq |
		^(10.0 / freq).pow(0.33)
	}
}