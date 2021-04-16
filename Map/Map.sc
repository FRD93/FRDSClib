// Linear map
Map {

	*new { | val=0.5, from_low=0, from_hi=1, to_low=1, to_hi=4, power=1 |
		^super.new.init(val, from_low, from_hi, to_low, to_hi, power)
	}

	init { | val, from_low, from_hi, to_low, to_hi, power |
		^(((val - from_low) / (from_hi - from_low)).pow(power) * (to_hi - to_low) + to_low)
	}
}


/* Old stuff here,  keeping for backward compatibility of my projects */

// Exponential map
MapE {

	*new { | val=0.5, from_low=0, from_hi=1, to_low=1, to_hi=4 |
		^super.new.init(val, from_low, from_hi, to_low, to_hi)
	}

	init { | val, from_low, from_hi, to_low, to_hi |
		^(((val - from_low) / (from_hi - from_low)).pow(2) * (to_hi - to_low) + to_low)
	}
}


// Squared map
MapL {

	*new { | val=0.5, from_low=0, from_hi=1, to_low=1, to_hi=4 |
		^super.new.init(val, from_low, from_hi, to_low, to_hi)
	}

	init { | val, from_low, from_hi, to_low, to_hi |
		^(((val - from_low) / (from_hi - from_low)).sqrt * (to_hi - to_low) + to_low)
	}
}