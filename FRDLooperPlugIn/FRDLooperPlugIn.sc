FRDLooperPlugIn {

	// System variables
	var presetPath, presets, looper, name_r, inCh_r, outCh_r, inGain_r, outGain_r, feedback_r, time_r, addAction_r, actionNode_r;
	var path;
	// GUI variables
	var window, inCh_n, outCh_n, inGain_s, outGain_s, feedback_k, time_n, hasGUI;


	// new method
	*new { | inCh=20, outCh=0, inGain=0, outGain=0, feedback=0.5, time=0.5, addAction='addToTail', actionNode=1 |
		^super.new.init(inCh, outCh, inGain, outGain, feedback, time, addAction, actionNode)
	}

	init { | inCh, outCh, inGain, outGain, feedback, time, addAction, actionNode |

		// INTERNAL VARIABLES
		path =  "".resolveRelative;
		inCh_r = inCh;
		outCh_r = outCh;
		inGain_r = inGain;
		outGain_r = outGain;
		feedback_r = feedback;
		time_r = time;
		addAction_r = addAction;
		actionNode_r = actionNode;
		looper = Synth(\FRDLooper, [ \inCh, inCh_r, \outCh, outCh_r, \inGain, inGain_r, \outGain, outGain_r, \feedback, feedback_r, \time, time_r ], actionNode_r, addAction_r);
		hasGUI = false;

	}







	// Get a Dictionary for integration in FRDMixerMatrixPlugIn
	asMixerMatrixProcess {
		^Dictionary.new.put(\inCh, inCh_r).put(\outCh, outCh_r).put(\inChannels, 2).put(\outChannels, 2)
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
		looper.set(\inCh, inCh_r);
		if(hasGUI, {inCh_n.value_(inCh_r)});
		^inCh_r
	}

	// outCh (output channel)
	outCh {
		^outCh_r
	}
	outCh_ { | outCh |
		outCh_r = outCh;
		looper.set(\outCh, outCh_r);
		if(hasGUI, {outCh_n.value_(outCh_r)});
		^outCh_r
	}


	// inGain (input gain)
	inGain {
		^inGain_r
	}
	inGain_ { | inGain |
		inGain_r = inGain;
		looper.set(\inGain, inGain_r);
		if(hasGUI, {inGain_s.value_(inGain_r.dbamp)});
		^inGain_r
	}

	// outGain (output gain)
	outGain {
		^outGain_r
	}
	outGain_ { | outGain |
		outGain_r = outGain;
		looper.set(\outGain, outGain_r);
		if(hasGUI, {outGain_s.value_(outGain_r.dbamp)});
		^outGain_r
	}


	// time
	time {
		^time_r
	}
	time_ { | time |
		time_r = time;
		looper.set(\time, time_r);
		if(hasGUI, {time_n.value_(time_r)});
		^time_r
	}


	// feedback
	feedback {
		^feedback_r
	}
	feedback_ { | feedback |
		feedback_r = feedback;
		looper.set(\feedback, feedback_r);
		if(hasGUI, {feedback_k.value_(feedback_r)});
		^feedback_r
	}


	// get looper synth
	synth {
		^looper
	}


	/*
	* GUI
	*/
	showGUI {
		hasGUI = true;
		// GUI WIDGETS
		inCh_n = NumberBox().action_({ | num | this.inCh_(num.value.asInteger)}).value_(inCh_r);
		outCh_n = NumberBox().action_({ | num | this.outCh_(num.value.asInteger)}).value_(outCh_r);
		time_n = NumberBox().action_({ | num | this.time_(num.value)}).value_(time_r);
		feedback_k = Knob().action_({ | num | this.feedback_(num.value)}).value_(feedback_r);
		inGain_s = Slider().action_({|val| this.inGain_(val.value.ampdb)}).value_(inGain_r.dbamp);
		outGain_s = Slider().action_({|val| this.outGain_(val.value.ampdb)}).value_(outGain_r.dbamp);



		window = Window("FRDLooperPlugIn", Rect(width: 20, height: 20)).onClose_({hasGUI = false});
		window.layout_(
			HLayout(
				VLayout(
					// inCh
					StaticText().string_("inCh"),
					inCh_n,
					// outCh
					StaticText().string_("outCh"),
					outCh_n,
					// time
					StaticText().string_("time"),
					time_n,
					// outCh
					StaticText().string_("feedback"),
					feedback_k,
				),
				// inGain
				VLayout(
					StaticText().string_("inGain"),
					inGain_s
				),
				// outGain
				VLayout(
					StaticText().string_("outGain"),
					outGain_s
				),
			)
		);
		window.front;
	}

	/*
	* STORE THE SYNTH DEFINITION TO FILE
	*/
	writeSynthDef {
		SynthDef(\FRDLooper, { | inCh=2, outCh=0, feedback=0.5, time=0.5, inGain=0, outGain=0 |
			var input, delay, back;
			feedback = feedback.clip(0.0, 1.0);
			back = LocalIn.ar(2);
			input = In.ar(inCh, 2) * inGain.dbamp;
			delay = DelayC.ar((input * (1.0 - feedback)) + (back * feedback), 8, time - ControlDur.ir); // MAX 8 sec.
			LocalOut.ar([delay[1], delay[0]]);
			Out.ar(outCh, delay * outGain.dbamp);
	} ).writeDefFile.add;

	}

}


