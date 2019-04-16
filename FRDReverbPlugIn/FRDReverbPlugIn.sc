FRDReverbPlugIn {

	// System variables
	var presetPath, presets, reverb, name_r, inCh_r, outCh_r, wet_r, feed_r, clearness_r, centerfreq_r, higain_r, width_r, lowgain_r, addAction_r, actionNode_r;
	// GUI variables
	var window, inCh_n, outCh_n, wet_k, feed_k, clearness_k, centerfreq_k, width_k, hasGUI;


	// new method
	*new { | inCh=20, outCh=0, wet=0.0, feed=0.35, clearness=0, centerfreq=2656, width=0.5, addAction='addToTail', actionNode=1 |
		^super.new.init(inCh, outCh, wet, feed, clearness, centerfreq, width, addAction, actionNode)
	}

	init { | inCh, outCh, wet, feed, clearness, centerfreq, width, addAction, actionNode |

		// INTERNAL VARIABLES
		inCh_r = inCh;
		outCh_r = outCh;
		wet_r = wet;
		feed_r = feed;
		clearness_r = clearness;
		centerfreq_r = centerfreq;
		width_r = width;
		addAction_r = addAction;
		actionNode_r = actionNode;
		this.computeGains();
		reverb = Synth(\FRDReverb, [\inCh, inCh_r, \outCh, outCh_r, \wet, wet_r, \feed, feed_r, \lowshelff, centerfreq_r, \hishelff, centerfreq_r, \lowshelfg, higain_r, \hishelf_g, lowgain_r], actionNode_r, addAction_r);
		hasGUI = false;

	}





	/*
	* UTILITY FUNCTIONS
	*/

	// computeGains (compute hishelfg_r and lowshelfg_r from clearness_r)
	computeGains {
		// range from -60dB to +10dB
		if(clearness_r > 0, {
			higain_r = clearness_r * 10.0;
			lowgain_r = clearness_r.neg * 60.0;
		});
		if(clearness_r < 0, {
			higain_r = clearness_r * 60.0;
			lowgain_r = clearness_r.neg * 10.0;
		});
		if(clearness_r == 0, {
			higain_r = 0;
			lowgain_r = 0;
		});
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
		reverb.set(\inCh, inCh_r);
		if(hasGUI, {inCh_n.value_(inCh_r)});
		^inCh_r
	}

	// outCh (output channel)
	outCh {
		^outCh_r
	}
	outCh_ { | outCh |
		outCh_r = outCh;
		reverb.set(\outCh, outCh_r);
		if(hasGUI, {outCh_n.value_(outCh_r)});
		^outCh_r
	}

	// wet (dry/wet ratio)
	wet {
		^wet_r
	}
	wet_ { | wet |
		wet_r = wet;
		reverb.set(\wet, wet_r);
		if(hasGUI, {wet_k.value_(wet_r)});
		^wet_r
	}

	// width
	width {
		^width_r
	}
	width_ { | width |
		width_r = width;
		reverb.set(\width, width_r);
		if(hasGUI, {width_k.value_(width_r)});
		^width_r
	}

	// feed
	feed {
		^feed_r
	}
	feed_ { | feed |
		feed_r = feed;
		reverb.set(\feed, feed_r);
		if(hasGUI, {feed_k.value_(feed_r)});
		^feed_r
	}

	// clearness
	clearness {
		^clearness_r
	}
	clearness_ { | clearness |
		clearness_r = clearness.clip(-2.0, 2.0);
		this.computeGains();
		reverb.set(\lowshelfg, higain_r);
		reverb.set(\hishelfg, lowgain_r);
		if(hasGUI, {clearness_k.value_((clearness_r + 2) / 4)});
		^clearness_r
	}

	// centerfreq
	centerfreq {
		^centerfreq_r
	}
	centerfreq_ { | centerfreq |
		centerfreq_r = centerfreq.clip(0, 20000);
		reverb.set(\lowshelff, centerfreq_r);
		reverb.set(\hishelff, centerfreq_r);
		if(hasGUI, {centerfreq_k.value_(centerfreq_r / 20000)});
		^centerfreq_r
	}

	// reverb synth
	reverb {
		^reverb
	}


	/*
	* GUI
	*/
	showGUI {
		hasGUI = true;
		// GUI WIDGETS
		inCh_n = NumberBox().action_({ | num | this.inCh_(num.value.asInteger)}).value_(inCh_r);
		outCh_n = NumberBox().action_({ | num | this.outCh_(num.value.asInteger)}).value_(outCh_r);
		wet_k = Knob().action_({ | num | this.wet_(num.value)}).value_(wet_r);
		width_k = Knob().action_({ | num | this.width_(num.value)}).value_(width_r);
		feed_k = Knob().action_({ | num | this.feed_(num.value)}).value_(feed_r);
		clearness_k = Knob().action_({ | num | this.clearness_(num.value * 4 - 2)}).value_(clearness_r);
		centerfreq_k = Knob().action_({ | num | this.centerfreq_(num.value * 19980 + 20)}).value_(centerfreq_r);

		window = Window("FRDReverbPlugIn", Rect(width: 20, height: 20)).onClose_({hasGUI = false});
		window.layout_(
			HLayout(
				// inCh
				VLayout(
					StaticText().string_("inCh"),
					inCh_n
				),
				// outCh
				VLayout(
					StaticText().string_("outCh"),
					outCh_n
				),
				// wet
				VLayout(
					StaticText().string_("Wet").align_(\center),
					wet_k
				),
				// width
				VLayout(
					StaticText().string_("Stereo"),
					width_k
				),
				// feed
				VLayout(
					StaticText().string_("Feedback"),
					feed_k
				),
				// clearness
				VLayout(
					StaticText().string_("Clearness"),
					clearness_k
				),
				// centerfreq
				VLayout(
					StaticText().string_("CenterFreq"),
					centerfreq_k
				),
			)
		);
		window.front;
	}

	/*
	* STORE THE SYNTH DEFINITION TO FILE
	*/
	writeSynthDef {
		SynthDef(\FRDReverb, {| inCh, outCh, wet=0.5, feed=0.35, width=0.5, lowshelff=2646, lowshelfg=0, hishelff=2646, hishelfg=0 |
			var in, out;
			in = In.ar(inCh, 2);
			out = FRDReverb.ar(fl: in[0], fr: in[1], feed: feed, width: width, lowshelff: lowshelff, lowshelfg: lowshelfg, hishelff: hishelff, hishelfg: hishelfg, wet: wet);
			Out.ar(outCh, out);
		}).writeDefFile;

	}

}


