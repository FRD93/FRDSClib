{ | gran |
	Routine{
		var size = 1024;
		var env = Env.new([0, 1, 0], [0.5, 0.5], [2, -2]).asSignal(size).asArray;
		var delta = size.reciprocal * gran.patternDuration;
		var amp = gran.amp, rate = gran.rate;
		("Pattern delta:" + delta).postln;

		env.do({ | eamp |
			Synth(\GrainBufMonoAR, [\buf, gran.buf, \delay, 0, \pos, rrand(0, gran.buf.numFrames / 2), \dur, rrand(0.25, 0.5), \atk, rrand(0.4, 0.6), \rate, rate + rrand(-0.005, 0.005), \amp, amp * eamp, \pan, rrand(-0.2, 0.2), \out, gran.outCh], gran.actionNode, gran.addAction);

			(delta + 0.001.rand).wait;
		});
		"Pattern finito".postln;

	}
}
