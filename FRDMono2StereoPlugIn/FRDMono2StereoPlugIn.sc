FRDMono2StereoPlugIn {

	// System variables
	var presetPath, presets, input, name_r, inCh_r, outCh_r, inGain_r, numChOut_r, addAction_r, actionNode_r;
	var path;
	// GUI variables
	var window, inCh_n, outCh_n, inGain_s, hasGUI;


	// new method
	*new { | inCh=20, outCh=0, addAction='addToHead', actionNode=1 |
		^super.new.init(inCh, outCh, addAction, actionNode)
	}

	init { | inCh, outCh, addAction, actionNode |

		// INTERNAL VARIABLES
		path =  "".resolveRelative;
		inCh_r = inCh;
		outCh_r = outCh;
		addAction_r = addAction;
		actionNode_r = actionNode;
		input = Synth(\FRDMono2Stereo, [ \inCh, inCh_r, \outCh, outCh_r ], actionNode_r, addAction_r);
		hasGUI = false;
	}


	// Get a Dictionary for integration in FRDMixerMatrixPlugIn
	asMixerMatrixProcess {
		^Dictionary.new.put(\inCh, inCh_r).put(\outCh, outCh_r).put(\inChannels, 1).put(\outChannels, 2)
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
				)
			)
		);
		window.front;
	}

	/*
	* STORE THE SYNTH DEFINITION TO FILE
	*/
	writeSynthDef {
		SynthDef( \FRDMono2Stereo, { | inCh=20, outCh=0 |
			Out.ar( outCh, In.ar(inCh, 1) ! 2 );
		} ).writeDefFile.add;
	}

}


