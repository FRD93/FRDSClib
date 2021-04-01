FRDLiveInputPlugIn {

	// System variables
	var presetPath, presets, input, name_r, inCh_r, outCh_r, inGain_r, numChOut_r, addAction_r, actionNode_r;
	var path;
	// GUI variables
	var window, inCh_n, outCh_n, inGain_s, hasGUI;


	// new method
	*new { | inCh=20, outCh=0, inGain=0, numChOut=2, addAction='addToHead', actionNode=1 |
		^super.new.init(inCh, outCh, inGain, numChOut, addAction, actionNode)
	}

	init { | inCh, outCh, inGain, numChOut, addAction, actionNode |

		// INTERNAL VARIABLES
		path =  "".resolveRelative;
		inCh_r = inCh;
		outCh_r = outCh;
		inGain_r = inGain;
		numChOut_r = numChOut.clip(1, 2);
		addAction_r = addAction;
		actionNode_r = actionNode;
		if(numChOut_r==1, {input = Synth(\FRDLiveInput1, [ \inCh, inCh_r, \outCh, outCh_r, \inGain, inGain_r ], actionNode_r, addAction_r);});
		if(numChOut_r==2, {input = Synth(\FRDLiveInput2, [ \inCh, inCh_r, \outCh, outCh_r, \inGain, inGain_r ], actionNode_r, addAction_r);});

		hasGUI = false;
	}


	// Get a Dictionary for integration in FRDMixerMatrixPlugIn
	asMixerMatrixProcess {
		^Dictionary.new.put(\inCh, inCh_r).put(\outCh, outCh_r).put(\inChannels, numChOut_r).put(\outChannels, numChOut_r)
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


	// inGain
	inGain {
		^inGain_r
	}
	inGain_ { | inGain |
		inGain_r = inGain;
		input.set(\inGain, inGain_r);
		if(hasGUI, {inGain_s.value_(inGain_r.dbamp)});
		^inGain_r
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
		inGain_s = Slider().action_({ | num | this.inGain_(num.value.ampdb)}).value_(inGain_r.dbamp);

		window = Window("FRDLiveInputPlugIn", Rect(width: 20, height: 20)).onClose_({hasGUI = false});
		window.layout_(
			HLayout(
				VLayout(
					// inCh
					StaticText().string_("inCh"),
					inCh_n,
					// outCh
					StaticText().string_("outCh"),
					outCh_n,
				),
				// inertia
				VLayout(
					StaticText().string_("Gain"),
					inGain_s,
				)
			)
		);
		window.front;
	}

	/*
	* STORE THE SYNTH DEFINITION TO FILE
	*/
	writeSynthDef {
		SynthDef( \FRDLiveInput1, { | inCh=20, inGain=0, outCh=0 |
			var input = SoundIn.ar(inCh, inGain.dbamp);
			Out.ar( outCh, input );
		} ).writeDefFile.add;

		SynthDef( \FRDLiveInput2, { | inCh=20, inGain=0, outCh=0 |
			var input = SoundIn.ar(inCh, inGain.dbamp);
			Out.ar( outCh, [input, 0] );
		} ).writeDefFile.add;

	}

}


