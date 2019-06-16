FRDAntiLarsenPlugIn {

	var inCh_r, outCh_r, addAction_r, actionNode_r, synth;
	var hasGUI, window, inCh_n, outCh_n;

	// new method
	*new { | inCh=20, outCh=0, addAction='addToHead', actionNode=1 |
		^super.new.init(inCh, outCh, addAction, actionNode)
	}

	init { | inCh, outCh, addAction, actionNode |
		inCh_r = inCh;
		outCh_r = outCh;
		addAction_r = addAction;
		actionNode_r = actionNode;
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
		synth.set(\inCh, inCh_r);
		if(hasGUI, {inCh_n.value_(inCh_r)});
		^inCh_r
	}

	// outCh (output channel)
	outCh {
		^outCh_r
	}
	outCh_ { | outCh |
		outCh_r = outCh;
		synth.set(\outCh, outCh_r);
		if(hasGUI, {outCh_n.value_(outCh_r)});
		^outCh_r
	}





	// get input synth
	synth {
		^synth
	}


	/*
	* GUI
	*/
	showGUI {
		hasGUI = true;
		// GUI WIDGETS
		inCh_n = NumberBox().action_({ | num | this.inCh_(num.value.asInteger)}).value_(inCh_r);
		outCh_n = NumberBox().action_({ | num | this.outCh_(num.value.asInteger)}).value_(outCh_r);
		window = Window("FRDAntiLarsenPlugIn", Rect(width: 20, height: 20)).onClose_({hasGUI = false});
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
		SynthDef( \FRDAntiLarsen, { | inCh=20, outCh=0, multiplier=10, coeff=0.8 |
			var input, chain, outliers, size=128;
			var mean, max;
			input = InFeedback.ar(inCh, 1);
			chain = FFT(LocalBuf(size), input, wintype: 1);
			outliers = PV_Copy(chain, LocalBuf(size));
			chain = chain.pvcalc(size, { | mags, phases |
				mean = mags.sum / mags.size;
				mean = 10;
				mags.do({| mag |
					//mean = mean + mag.abs;
				});

				[mags, phases]
			});
			Poll.kr(Impulse.kr(10), mean, \sum);
			outliers = PV_MagAbove(outliers, multiplier * mean);
			chain = chain.pvcalc2(outliers, size, { | magnitudes1, phases1, magnitudes2, phases2 |
				magnitudes1 = magnitudes1 - (magnitudes2 * coeff);
				[magnitudes1, phases1]
			});
			chain = IFFT(chain);
			Out.ar( outCh, chain );
		} ).writeDefFile.add;
	}

}


