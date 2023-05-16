{ | gran |
	Routine{
		var size = 1024;
		var env = Env.new([0.25, 1], [1], [4]).asSignal(size).asArray;
		var n_steps = 4;
		var rmenv = Env.new({rrand(5, 500)} ! n_steps, {1/n_steps} ! n_steps, [4]).asSignal(size).asArray;
		var delta = size.reciprocal * gran.patternDuration;
		var amp = gran.amp, rate = gran.rate;
		("Pattern delta:" + delta).postln;

		env.do({ | eamp, id |
			Synth(\GrainBufMonoRM, [\buf, gran.buf, \delay, 0, \pos, rrand(0, gran.buf.numFrames / 2), \dur, 0.15, \atk, 0.5, \rate, rate + rrand(-0.005, 0.005), \amp, amp * eamp, \pan, rrand(-0.03, 0.63), \out, gran.outCh, \carFreq, rmenv[id]], gran.actionNode, gran.addAction);

			(delta + (0.005 * id / size)).wait;
		});
		"Pattern finito".postln;

	}
}
