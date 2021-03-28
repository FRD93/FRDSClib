PitchShiftSTK {
	// SOURCE: https://reading.supply/@ben/implementing-a-pitch-shifter-in-supercollider-Z0fcAX
	classvar delayLengthSamp = 5024;
	*ar { arg in, shift;
		var delayLength = delayLengthSamp / Server.default.sampleRate;
		var halfLength = delayLength / 2;
		var sweep = Sweep.ar(
			Impulse.kr(0),
			1.0 - shift
		);
		var env = EnvGen.ar(
			Env.triangle(delayLength),
			Impulse.ar(delayLength.reciprocal)
		);
		var lines = DelayC.ar(
			in,
			maxdelaytime: delayLength,
			delaytime: Wrap.ar(
				[sweep, sweep + halfLength],
				0, delayLength
			),
			mul: [env, 1.0 - env]
		);
                ^lines.sum;
        }
}