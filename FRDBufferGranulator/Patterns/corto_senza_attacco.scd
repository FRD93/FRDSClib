{ | gran |
	Routine{
		var size = 128;
		var nenv = 4;
		var env = Env.new({rrand(0.0, 1.0).round(0.1)} ! nenv, ({rrand(0.0, 1.0)} ! nenv).normalizeSum, {[-4, 4].choose} ! nenv).asSignal(size).asArray;
		var n_steps = 4;
		//var rmenv = Env.new({exprand(50, 500)} ! n_steps, {1/n_steps} ! n_steps, [4]).asSignal(size).asArray;
		var rmenv = Env.new(rrand(50, 500) ! 2, [1], [4]).asSignal(size).asArray;
		var delta = size.reciprocal * gran.patternDuration;
		var amp = gran.amp, rate = gran.rate;
		"Start pattern".postln;
		("PatternDuration:" + gran.patternDuration + " Delta:" + delta).postln;
		env.do({ | eamp, id |
			Synth(\GrainBufMonoAR, [\buf, gran.buf, \delay, 0, \pos, rrand(0, gran.buf.numFrames / 2), \dur, 0.15, \atk, 0.5, \rate, rate + rrand(-0.005, 0.005), \amp, amp * eamp, \pan, rrand(-0.03, 0.63), \out, gran.outCh, \carFreq, rmenv[id]], gran.actionNode, gran.addAction);
			(delta).wait;
		});
		"End pattern".postln;
	}
}