// Linear map
Map {

	*new { | val=0.5, from_low=0, from_hi=1, to_low=1, to_hi=4 |
		^super.new.init(val, from_low, from_hi, to_low, to_hi)
	}

	init { | val, from_low, from_hi, to_low, to_hi |
		^(((val - from_low) / (from_hi - from_low)) * (to_hi - to_low) + to_low)
	}
}


// Exponential map
MapE {

	*new { | val=0.5, from_low=0, from_hi=1, to_low=1, to_hi=4 |
		^super.new.init(val, from_low, from_hi, to_low, to_hi)
	}

	init { | val, from_low, from_hi, to_low, to_hi |
		^(((val - from_low) / (from_hi - from_low)).pow(2) * (to_hi - to_low) + to_low)
	}
}


// Logarithmic map
MapL {

	*new { | val=0.5, from_low=0, from_hi=1, to_low=1, to_hi=4 |
		^super.new.init(val, from_low, from_hi, to_low, to_hi)
	}

	init { | val, from_low, from_hi, to_low, to_hi |
		^(((val - from_low) / (from_hi - from_low)).sqrt * (to_hi - to_low) + to_low)
	}
}