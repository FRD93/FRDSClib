/*
Library of classes for mathematical operations.
Copyright ©2019, Francesco Roberto Dani - f.r.d@hotmail.it
License: GPLv3
*/


// Gaussian
// Fill an array of size N with a gaussian function controlling normalized center and variance. Result is normalizeSum
Gaussian {
	*new { | size=128, center=0.5, variance=0.1 |
		^super.new.init(size, center, variance)
	}

	init { | size, center, variance |
		var a, b, c;
		a = 1 / (variance * 2pi.sqrt);
		b = (center.clip(0.0, 1.0) * 20) - 10;
		c = variance.clip(0.0, 1.0).pow(4) * 80;
		^(0..size).normalize(-10, 10).collect({ |num| num.gaussCurve(1, b, c) }).normalizeSum
	}
}

