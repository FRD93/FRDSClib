FRDPitchTrackRingModulatorPlugIn {

	// System variables
	var presetPath, presets, ring, name_r, inCh_r, outCh_r, inGain_r, outGain_r, feedback_r, time_r, addAction_r, actionNode_r, found_pitches, noteList, right_pitch, min_time_diff_r, inertia_r, last_time_found, enabled_r, oscfunc;
	var path;
	// GUI variables
	var window, inCh_n, outCh_n, min_time_diff_k, inertia_k, enabled_b, hasGUI;


	// new method
	*new { | inCh=20, outCh=0, enabled=false, min_time_diff=1, inertia=2, addAction='addToTail', actionNode=1 |
		^super.new.init(inCh, outCh, enabled, min_time_diff, inertia, addAction, actionNode)
	}

	init { | inCh, outCh, enabled, min_time_diff, inertia, addAction, actionNode |

		// INTERNAL VARIABLES
		path =  "".resolveRelative;
		inCh_r = inCh;
		outCh_r = outCh;
		enabled_r = enabled;
		min_time_diff_r = min_time_diff;
		inertia_r = inertia;
		addAction_r = addAction;
		actionNode_r = actionNode;
		noteList = ( 24..120 ).midicps;
		found_pitches = ( 0..12 );
		right_pitch = 0;
		last_time_found = Main.elapsedTime.ceil;
		ring = Synth(\FRDPitchTrackRingModulator, [ \inCh, inCh_r, \outCh, outCh_r, \inertia, inertia_r ], actionNode_r, addAction_r);
		hasGUI = false;
		this.startOSCconnection();

	}



	startOSCconnection {
		oscfunc = OSCFunc( {	 | msg, time, addr, recvPort |
			//msg.postln;
			found_pitches = found_pitches.rotate( -1 );
			found_pitches[ found_pitches.size - 1 ] = msg[3].nearestInList( noteList );
			//found_pitches.round( 1 ).postln;
			if( ( ( found_pitches.sum / found_pitches.size ).trunc == found_pitches.last.trunc ) && ( right_pitch != found_pitches.last ) && ( right_pitch != ( found_pitches.last / 2 ) && ( ( Main.elapsedTime.ceil - last_time_found ) > min_time_diff_r ) ), {
				( "Found: " + msg[3].nearestInList( noteList ) ).postln;
				[ Main.elapsedTime.ceil, last_time_found ].postln;
				right_pitch = found_pitches.last;
				last_time_found = Main.elapsedTime.ceil;
				if( enabled_r == true, {
					Routine{
						ring.set( \freq, msg[3].nearestInList( noteList ) );
						0.01.wait;
						Synth.before(ring, \SendTrig, [ \out, 99 ]);
					}.play( AppClock );
				} );
			} );

		}, '/pitch' );
		oscfunc.add;
	}

	stopOSCconnection{
		oscfunc.remove;
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


	// min time difference between intervals
	min_time_diff {
		^min_time_diff_r
	}
	min_time_diff_ { | min_time_diff |
		min_time_diff_r = min_time_diff;
		if(hasGUI, {min_time_diff_k.value_(min_time_diff_r / 4)});
		^min_time_diff_r
	}


	// inertia factor
	inertia {
		^inertia_r
	}
	inertia_ { | inertia |
		inertia_r = inertia;
		ring.set(\inertia, inertia_r);
		if(hasGUI, {inertia_k.value_(inertia_r / 4)});
		^inertia_r
	}


	// enable / disable ring modulator
	enabled {
		^enabled_r
	}
	enabled_ { | enabled |
		enabled_r = enabled;
		"inside enabled_:".postln;
		enabled_r.postln;
		if(hasGUI, {enabled_b.value_(enabled_r.asInteger)});
		^enabled_r
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
		min_time_diff_k = Knob().action_({ | num | this.min_time_diff_(num.value * 4)}).value_(min_time_diff_r / 4);
		inertia_k = Knob().action_({ | num | this.inertia_(num.value * 4)}).value_(inertia_r / 4);
		enabled_b = Button().states_([["Disabled"], ["Enabled"]]).action_({|val| this.enabled_(val.value.asBoolean); val.value.asBoolean.postln;}).value_(enabled_r.asInteger);


		window = Window("FRDPitchTrackRingModulatorPlugIn", Rect(width: 20, height: 20)).onClose_({hasGUI = false});
		window.layout_(
			HLayout(
				VLayout(
					// inCh
					StaticText().string_("inCh"),
					inCh_n,
					// outCh
					StaticText().string_("outCh"),
					outCh_n,
					// enabled
					enabled_b,

				),
				// inertia
				VLayout(
					StaticText().string_("inertia"),
					inertia_k,
				),
				// min_time_diff
				VLayout(
					StaticText().string_("min distance"),
					min_time_diff_k
				),
			)
		);
		window.front;
	}

	/*
	* STORE THE SYNTH DEFINITION TO FILE
	*/
	writeSynthDef {
		SynthDef( \FRDPitchTrackRingModulator, { | inCh=20, freq=440, inertia=1, amp=1, trigBus=99, outCh=0 |
			var rm, sig, freqs, pitch, hasPitch, env, freq2, hasFreq;

			trigBus = In.ar( trigBus );

			env = EnvGen.ar( Env.perc( inertia, inertia, 1, [ 4, 4 ] ), trigBus ) * amp;

			sig = In.ar( inCh, 2 );
			//sig.poll;
			//Out.ar( 60, sig );

			# pitch, hasPitch = Pitch.kr( in:sig.sum, initFreq:440, minFreq:80, maxFreq:7500, execFreq:256, maxBinsPerOctave:16, median:32, ampThreshold:0.01, peakThreshold:0.5, downSample:1, clar:1 );

			SendReply.kr( Impulse.kr( 100 ), '/pitch', [ pitch, hasPitch ], 1905 );

			freqs = Lag.ar( K2A.ar( freq ), 0.01 ) * [ 0.75, 1, 1.5, 2, 3 ];

			rm = SinOsc.ar( freqs, 0, 0.5, 0.5 );

			rm = rm * SinOsc.ar( ( { Rand( 0.100, 0.50 ) } ! freqs.size ), { Rand( 0.0, 2pi ) } ! freqs.size, 1 );

			sig = SelectX.ar( env, [ sig, ( sig * rm.sum ) ] );

			Out.ar( outCh, sig );

		} ).writeDefFile.add;

		SynthDef( \SendTrig, { | out=0 |
			Out.ar( out, Impulse.ar( 0 ) );
		} ).writeDefFile.add;

	}

}


