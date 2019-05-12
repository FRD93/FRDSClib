FRDRingModulatorPlugIn {

	// System variables
	var presetPath, presets, ring, name_r, inCh_r, outCh_r, inGain_r, outGain_r, feedback_r, time_r, addAction_r, actionNode_r, freq_r, wet_r;
	var path;
	// GUI variables
	var window, inCh_n, outCh_n, freq_n, wet_s, hasGUI;


	// new method
	*new { | inCh=20, outCh=0, freq=155.6, wet=0, addAction='addToTail', actionNode=1 |
		^super.new.init(inCh, outCh, freq, wet, addAction, actionNode)
	}

	init { | inCh, outCh, freq, wet, addAction, actionNode |

		// INTERNAL VARIABLES
		path =  "".resolveRelative;
		inCh_r = inCh;
		outCh_r = outCh;
		freq_r = freq;
		wet_r = wet;
		addAction_r = addAction;
		actionNode_r = actionNode;
		ring = Synth(\FRDRingModulator, [ \inCh, inCh_r, \outCh, outCh_r, \freq, freq_r, \wet, wet_r ], actionNode_r, addAction_r);
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
		ring.set(\inCh, inCh_r);
		if(hasGUI, {inCh_n.value_(inCh_r)});
		^inCh_r
	}

	// outCh (output channel)
	outCh {
		^outCh_r
	}
	outCh_ { | outCh |
		outCh_r = outCh;
		ring.set(\outCh, outCh_r);
		if(hasGUI, {outCh_n.value_(outCh_r)});
		^outCh_r
	}


	// freq
	freq {
		^freq_r
	}
	freq_ { | freq |
		freq_r = freq;
		ring.set(\freq, freq_r);
		if(hasGUI, {freq_n.value_(freq_r)});
		^freq_r
	}


	// wet
	wet {
		^wet_r
	}
	wet_ { | wet |
		wet_r = wet;
		ring.set(\wet, wet_r);
		if(hasGUI, {wet_s.value_(wet_r)});
		^wet_r
	}



	// get ring synth
	synth {
		^ring
	}


	/*
	* GUI
	*/
	showGUI {
		hasGUI = true;
		// GUI WIDGETS
		inCh_n = NumberBox().action_({ | num | this.inCh_(num.value.asInteger)}).value_(inCh_r);
		outCh_n = NumberBox().action_({ | num | this.outCh_(num.value.asInteger)}).value_(outCh_r);
		freq_n = NumberBox().action_({ | num | this.freq_(num.value)}).value_(freq_r);
		wet_s = Slider().action_({ | num | this.wet_(num.value)}).value_(wet_r);

		window = Window("FRDRingModulatorPlugIn", Rect(width: 20, height: 20)).onClose_({hasGUI = false});
		window.layout_(
			HLayout(
				VLayout(
					// inCh
					StaticText().string_("inCh"),
					inCh_n,
					// outCh
					StaticText().string_("outCh"),
					outCh_n,
					// freq
					StaticText().string_("freq"),
					freq_n,

				),
				// inertia
				VLayout(
					StaticText().string_("wet"),
					wet_s,
				)
			)
		);
		window.front;
	}

	/*
	* STORE THE SYNTH DEFINITION TO FILE
	*/
	writeSynthDef {
		SynthDef( \FRDRingModulator, { | inCh=20, freq=440, wet=0.5, outCh=0 |
			var rm, sig, freqs, amp;
			freqs = freq * [ 1, 2, 3, 4, 5 ];
			sig = In.ar( inCh, 2 );
			rm = SinOsc.ar( freqs, 0, 0.5, 0.5 );
			rm = rm * SinOsc.ar( ( { Rand( 0.100, 0.50 ) } ! freqs.size ), { Rand( 0.0, 2pi ) } ! freqs.size, 1 );
			//rm = SelectX.ar( Lag2.ar( TRand.ar( 0, 4, Impulse.ar( 1 ) ), 0.5 ), rm );
			sig = SelectX.ar( wet, [ sig, ( sig * rm.sum ) ] );
			Out.ar( outCh, sig );
		} ).writeDefFile.add;

	}

}


