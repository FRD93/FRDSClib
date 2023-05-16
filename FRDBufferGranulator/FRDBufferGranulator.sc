FRDBufferGranulator {

	// System variables
	var presetPath, presets, input, name_r, buf_r, inCh_r, outCh_r, routines_r, patterns_r, addAction_r, actionNode_r, patternDuration_r, amp_r, rate_r;
	var path;
	// GUI variables
	var window, inCh_n, outCh_n, patterns_b, inGain_s, hasGUI;


	// new method
	*new { | buf, outCh=0, addAction='addToHead', actionNode=1 |
		^super.new.init(buf, outCh, addAction, actionNode)
	}

	init { | buf, outCh, addAction, actionNode |
		patternDuration_r = 1;
		amp_r = 1;
		rate_r = 1;
		// INTERNAL VARIABLES
		path =  "".resolveRelative ++ "Patterns/";
		patterns_r = PathName(path).files.collect({|name| name.fileNameWithoutExtension});
		patterns_r.postln;
		routines_r = Dictionary();
		patterns_r.do({ | pattern |
			(path++pattern++".scd").postln;
			routines_r.put( pattern.asString, File.open(path++pattern++".scd", "r").readAllString.interpret.value(this));
			routines_r.at(pattern.asString).postln;
		});
		buf_r = buf;
		outCh_r = outCh;
		addAction_r = addAction;
		actionNode_r = actionNode;
		hasGUI = false;
	}


	// Get a Dictionary for integration in FRDMixerMatrixPlugIn
	asMixerMatrixProcess {
		^Dictionary.new.put(\outCh, outCh_r).put(\inChannels, 0).put(\outChannels, 2)
	}


	spawn { | amp=0.5, dur=0.1, atk=0.1, curve=1, pan=0 |
		Synth(\FRDBusGrain, [\inCh, inCh_r, \outCh, outCh_r, \amp, amp, \dur, dur, \atk, atk, \curve, curve, \pan, pan], actionNode_r, addAction_r);
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
		input.set(\outCh, outCh_r);
		if(hasGUI, {outCh_n.value_(outCh_r)});
		^outCh_r
	}


	patternDuration {
		^patternDuration_r
	}
	patternDuration_ { | patternDuration |
		patternDuration_r = patternDuration;
		^patternDuration_r
	}

	amp {
		^amp_r
	}
	amp_ { | amp |
		amp_r = amp;
		^amp
	}

	rate {
		^rate_r
	}
	rate_ { | rate |
		rate_r = rate;
		^rate_r
	}

	addAction {
		^addAction_r
	}

	actionNode {
		^actionNode_r
	}



	playPattern { | pattern |
		routines_r[pattern].reset.play;
	}





	// get input synth
	synth {
		^input
	}


	/*
	* GUI
	*/
	showGUI {
		hasGUI = true;
		// GUI WIDGETS
		inCh_n = NumberBox().action_({ | num | this.inCh_(num.value.asInteger)}).value_(inCh_r);
		outCh_n = NumberBox().action_({ | num | this.outCh_(num.value.asInteger)}).value_(outCh_r);
		patterns_b = patterns_r.collect({ | pattern |
			Button().states_([[pattern, Color.black, Color.red], [pattern, Color.black, Color.green]]).action_({|val|
				if(val.value == 1, {
					routines_r.at(pattern.asString).reset.play(AppClock);
				}, {
					routines_r.at(pattern.asString).stop();
				});
			})
		});
		window = Window("FRDBusGranulatorPlugIn", Rect(width: 20, height: 20)).onClose_({hasGUI = false});
		window.layout_(
			HLayout(
				VLayout(
					// inCh
					StaticText().string_("inCh"),
					inCh_n,
					// outCh
					StaticText().string_("outCh"),
					outCh_n,
					// Pattern
				),
				VLayout(*patterns_b)
			)
		);
		window.front;
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

		SynthDef(\GrainBufMonoRM, { | buf, delay, pos, dur, atk, rate, amp, pan, out, carFreq=50 |
			var snd, env, car;
			car = SinOsc.ar(carFreq, 1.5pi, 1, 0.0);
			EnvGen.ar(Env.new([0, 0], [dur + delay]), doneAction: 2);
			snd = PlayBuf.ar(1, buf, rate, 1, pos);
			snd = PitchShift.ar(snd, 0.01, rate, 0, 0);
			env = EnvGen.ar(Env.new([0, 1, 0], [dur * atk, dur * (1.0 - atk)], [-2, 2]), levelScale: amp, doneAction: 0);
			Out.ar(out, Pan2.ar(DelayL.ar(snd * env * car, 1, delay), pan));
		}).writeDefFile.add;
	}

}


