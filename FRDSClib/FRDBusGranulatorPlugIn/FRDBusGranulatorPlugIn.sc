FRDBusGranulatorPlugIn {

	// System variables
	var presetPath, presets, input, name_r, inCh_r, outCh_r, routines_r, patterns_r, addAction_r, actionNode_r;
	var path;
	// GUI variables
	var window, inCh_n, outCh_n, patterns_b, inGain_s, hasGUI;


	// new method
	*new { | inCh=20, outCh=0, addAction='addToHead', actionNode=1 |
		^super.new.init(inCh, outCh, addAction, actionNode)
	}

	init { | inCh, outCh, addAction, actionNode |

		// INTERNAL VARIABLES
		path =  "".resolveRelative ++ "Patterns/";
		patterns_r = PathName(path).files.collect({|name| name.fileNameWithoutExtension});
		patterns_r.postln;
		routines_r = Dictionary();
		patterns_r.do({ | pattern |
			(path++pattern++".scd").postln;
			routines_r.put( pattern.asSymbol, File.open(path++pattern++".scd", "r").readAllString.interpret.value(this));
			routines_r.at(pattern.asSymbol).postln;
		});
		inCh_r = inCh;
		outCh_r = outCh;
		addAction_r = addAction;
		actionNode_r = actionNode;
		input = Synth(\FRDBusGrain, [ \inCh, inCh_r, \outCh, outCh_r ], actionNode_r, addAction_r);
		hasGUI = false;
	}


	// Get a Dictionary for integration in FRDMixerMatrixPlugIn
	asMixerMatrixProcess {
		^Dictionary.new.put(\inCh, inCh_r).put(\outCh, outCh_r).put(\inChannels, 1).put(\outChannels, 2)
	}


	spawn { | amp=0.5, dur=0.1, atk=0.1, curve=1, pan=0 |
		Synth(\FRDBusGrain, [\inCh, inCh_r, \outCh, outCh_r, \amp, amp, \dur, dur, \atk, atk, \curve, curve, \pan, pan], actionNode_r, addAction_r);
	}



	/*
	* GETTERS AND SETTERS
	* FOR PARAMETERS
	*/

	// inCh (input channel)
	inCh {
		^inCh_r
	}
	inCh_ { | inCh |
		inCh_r = inCh;
		input.set(\inCh, inCh_r);
		if(hasGUI, {inCh_n.value_(inCh_r)});
		^inCh_r
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
					routines_r.at(pattern.asSymbol).reset.play(AppClock);
				}, {
					routines_r.at(pattern.asSymbol).stop();
				});
			})
		});
		window = Window("FRDMono2StereoPlugIn", Rect(width: 20, height: 20)).onClose_({hasGUI = false});
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
		SynthDef( \FRDBusGrain, { | inCh=20, outCh=0, amp=0.5, dur=0.1, atk=0.01, curve=1, pan=0 |
			var input, env, grain;
			atk = atk.clip(0.0, 1.0);
			input = InFeedback.ar(inCh, 1);
			env = EnvGen.ar(Env.new([0, 1, 0], [dur*atk, dur*(1.0-atk)], [curve.neg, curve]), levelScale: amp, doneAction: 0);
			grain = Pan2.ar(input * env, pan);
			Out.ar( outCh, grain );
		} ).writeDefFile.add;
	}

}


