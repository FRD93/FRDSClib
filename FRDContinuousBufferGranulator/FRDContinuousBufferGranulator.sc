FRDContinuousBufferGranulator {

	// System variables
	var presetPath, presets, input, name_r, buf_r, outCh_r, routine_r, addAction_r, actionNode_r, maxDel_r, minGDur_r, maxGDur_r, maxRate_r, maxPan_r, grainsPerSecond_r, amp_r;
	var path;
	// GUI variables
	var window, inCh_n, outCh_n, patterns_b, inGain_s, hasGUI;


	// new method
	*new { | buf, outCh=0, maxDel=0, minGDur=0.25, maxGDur=0.5, maxRate=0, maxPan=0.63, grainsPerSecond=60, amp=1, addAction='addToHead', actionNode=1 |
		^super.new.init(buf, outCh, maxDel, minGDur, maxGDur, maxRate, maxPan, grainsPerSecond, amp, addAction, actionNode)
	}

	init { | buf, outCh, maxDel, minGDur, maxGDur, maxRate, maxPan, grainsPerSecond, amp, addAction, actionNode |
		buf_r = buf;
		outCh_r = outCh;
		maxDel_r = maxDel;
		minGDur_r = minGDur;
		maxGDur_r = maxGDur;
		maxRate_r = maxRate;
		maxPan_r = maxPan;
		grainsPerSecond_r = grainsPerSecond;
		amp_r = amp;
		addAction_r = addAction;
		actionNode_r = actionNode;

		routine_r = Routine{
			var delay;
			inf.do({ | id |
				delay = rrand(0.0, maxDel_r);
				Synth(\GrainBufMonoAR, [
					\buf, buf_r,
					\delay, delay,
					\pos, rrand(0, buf_r.numFrames),
					\dur, rrand(minGDur_r, maxGDur_r).round(0.01),
					\atk, rrand(0.2, 0.8).roundUp(0.1),
					\rate, rrand(1-maxRate_r, 1+maxRate_r).round(0.25),
					\amp, exprand(0.75, 1) * amp_r,
					\pan, rrand(-1*maxPan_r, maxPan_r),
					\out, outCh_r
				], actionNode_r, addAction_r);
				//Synth.tail(s, \GrainBusMonoAR, [\bus, 20, \dur, 1, \atk, 0.5, \rate, 1, \amp, 1, \pan, 0, \out, 0]);
				grainsPerSecond_r.reciprocal.wait;
			});
		};
		routine_r.play;
	}


	// Get a Dictionary for integration in FRDMixerMatrixPlugIn
	asMixerMatrixProcess {
		^Dictionary.new.put(\outCh, outCh_r).put(\inChannels, 0).put(\outChannels, 2)
	}


	/*
	* GETTERS AND SETTERS
	* FOR PARAMETERS
	*/

	// inCh (input channel)
	buf {
		^buf_r
	}
	buf_ { | buf |
		buf_r = buf;
		^buf_r
	}

	// outCh (output channel)
	outCh {
		^outCh_r
	}
	outCh_ { | outCh |
		outCh_r = outCh;
		if(hasGUI, {outCh_n.value_(outCh_r)});
		^outCh_r
	}

	maxDel {
		^maxDel_r
	}
	maxDel_ { | maxDel |
		maxDel_r = maxDel;
		^maxDel_r
	}

	minGDur {
		^minGDur_r
	}
	minGDur_ { | minGDur |
		minGDur_r = minGDur;
		^minGDur_r
	}

	maxGDur {
		^maxGDur_r
	}
	maxGDur_ { | maxGDur |
		maxGDur_r = maxGDur;
		^maxGDur_r
	}

	maxRate {
		^maxRate_r
	}
	maxRate_ { | maxRate |
		maxRate_r = maxRate;
		^maxRate_r
	}

	maxPan {
		^maxPan_r
	}
	maxPan_ { | maxPan |
		maxPan_r = maxPan;
		^maxPan_r
	}

	grainsPerSecond {
		^grainsPerSecond_r
	}
	grainsPerSecond_ { | grainsPerSecond |
		grainsPerSecond_r = grainsPerSecond;
		^grainsPerSecond_r
	}

	amp {
		^amp_r
	}
	amp_ { | amp |
		amp_r = amp;
		^amp_r
	}


	start {
		routine_r.reset.play;
	}

	stop {
		routine_r.stop;
	}

	/*
	* STORE THE SYNTH DEFINITION TO FILE
	*/
	writeSynthDef {
		SynthDef(\GrainBufMonoAR, { | buf, delay, pos, dur, atk, rate, amp, pan, out |
			var snd, env;
			EnvGen.ar(Env.new([0, 0], [dur + delay]), doneAction: 2);
			snd = PlayBuf.ar(1, buf, rate, 1, pos);
			snd = PitchShift.ar(snd, 0.01, rate, 0, 0);
			env = EnvGen.ar(Env.new([0, 1, 0], [dur * atk, dur * (1.0 - atk)], [-2, 2]), levelScale: amp, doneAction: 0);
			Out.ar(out, Pan2.ar(DelayL.ar(snd * env, 1, delay), pan));
		}).writeDefFile.add;
	}

}


