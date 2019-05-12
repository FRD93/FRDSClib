FRDConvolutionPlugIn {

	// System variables
	var presetPath, presets, conv, name_r, inCh_r, outCh_r, inGain_r, outGain_r, convName_r, addAction_r, actionNode_r, convNameList;
	var fftsize = 2048, irbuffer, bufsize, irspectrum;
	var path;
	// GUI variables
	var window, inCh_n, outCh_n, inGain_s, outGain_s, convFiles_p, hasGUI;


	// new method
	*new { | inCh=20, outCh=0, inGain=0, outGain=0, convName="conv1.wav", addAction='addToTail', actionNode=1 |
		^super.new.init(inCh, outCh, inGain, outGain, convName, addAction, actionNode)
	}

	init { | inCh, outCh, inGain, outGain, convName, addAction, actionNode |

		// INTERNAL VARIABLES
		path =  "".resolveRelative;
		convNameList = PathName(path ++ "IR/").files.collect({|file| file.fileName});
		inCh_r = inCh;
		outCh_r = outCh;
		inGain_r = inGain;
		outGain_r = outGain;
		convName_r = convName;
		addAction_r = addAction;
		actionNode_r = actionNode;
		this.loadConvFile(convName_r);
		hasGUI = false;

	}



	loadConvFile { | convFileName |
		Routine{
			try({conv.free});
			irbuffer= Buffer.read(Server.local, path ++ "IR/" ++ convFileName);
			0.1.wait;
			bufsize= PartConv.calcBufSize(fftsize, irbuffer);
			irspectrum= Buffer.alloc(Server.local, bufsize, 1);
			irspectrum.preparePartConv(irbuffer, fftsize);
			irbuffer.free; // don't need time domain data anymore, just needed spectral version
			SynthDef( \FRDConvolution, { | inCh=20, outCh=0, inGain=0, outGain=0 |
				var input, chain, mag_mean=0, mag_std_dev=0, mag_thresh=1.0, mags_result, fft_size=2048;
				input = In.ar( inCh, 2 ) * inGain.dbamp * 0.0125;
				input = [ PartConv.ar( input[0], fftsize, irspectrum.bufnum ), PartConv.ar( input[1], fftsize, irspectrum.bufnum ) ];
				input = CompanderD.ar( input, 0.6, 1, 0.1 );
				input = Limiter.ar(input);
				Out.ar( outCh, input * outGain.dbamp );
			}).add;
			0.1.wait;
			conv = Synth(\FRDConvolution, [ \inCh, inCh_r, \outCh, outCh_r, \inGain, inGain_r, \outGain, outGain_r ], actionNode_r, addAction_r);
		}.play( AppClock );
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
		conv.set(\inCh, inCh_r);
		if(hasGUI, {inCh_n.value_(inCh_r)});
		^inCh_r
	}

	// outCh (output channel)
	outCh {
		^outCh_r
	}
	outCh_ { | outCh |
		outCh_r = outCh;
		conv.set(\outCh, outCh_r);
		if(hasGUI, {outCh_n.value_(outCh_r)});
		^outCh_r
	}


	// inGain (input gain)
	inGain {
		^inGain_r
	}
	inGain_ { | inGain |
		inGain_r = inGain;
		conv.set(\inGain, inGain_r);
		if(hasGUI, {inGain_s.value_(inGain_r.dbamp)});
		^inGain_r
	}

	// outGain (output gain)
	outGain {
		^outGain_r
	}
	outGain_ { | outGain |
		outGain_r = outGain;
		conv.set(\outGain, outGain_r);
		if(hasGUI, {outGain_s.value_(outGain_r.dbamp)});
		^outGain_r
	}




	// get convolution synth
	synth {
		^conv
	}


	/*
	* GUI
	*/
	showGUI {
		hasGUI = true;
		// GUI WIDGETS
		inCh_n = NumberBox().action_({ | num | this.inCh_(num.value.asInteger)}).value_(inCh_r);
		outCh_n = NumberBox().action_({ | num | this.outCh_(num.value.asInteger)}).value_(outCh_r);
		convFiles_p = PopUpMenu().items_(convNameList).action_({|item| this.loadConvFile(convNameList.at(item.value))});
		inGain_s = Slider().action_({|val| this.inGain_(val.value.ampdb)}).value_(inGain_r.dbamp);
		outGain_s = Slider().action_({|val| this.outGain_(val.value.ampdb)}).value_(outGain_r.dbamp);



		window = Window("FRDConvolutionPlugIn", Rect(width: 20, height: 20)).onClose_({hasGUI = false});
		window.layout_(
			HLayout(
				VLayout(
					// inCh
					StaticText().string_("inCh"),
					inCh_n,
					// outCh
					StaticText().string_("outCh"),
					outCh_n,
					// Conv File
					StaticText().string_("Conv File"),
					convFiles_p
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
		SynthDef(\FRDReverb, {| inCh, outCh, wet=0.5, feed=0.35, width=0.5, lowshelff=2646, lowshelfg=0, hishelff=2646, hishelfg=0 |
			var in, out;
			in = In.ar(inCh, 2);
			out = FRDReverb.ar(fl: in[0], fr: in[1], feed: feed, width: width, lowshelff: lowshelff, lowshelfg: lowshelfg, hishelff: hishelff, hishelfg: hishelfg, wet: wet);
			Out.ar(outCh, out);
		}).writeDefFile;

	}

}


