/*
Drone1
A drone-like synthesizer.
Copyright ©2018, Francesco Roberto Dani
*/

FRDMIDIDrone1 {
	// System variables
	var freq_d, lowpass_d, amp_d, gate_d, octA_d, oct_d, rmA_d, flangerA_d, vel_d, max_d, explode_d, lagTime_d, outCh_d;
	var drone;
	// GUI variables
	var window, freq_k, lowpass_k, amp_k, octA_k, oct_k, rmA_k, flangerA_k, vel_k, max_k, explode_b;
	// MIDI variables
	var noteOn, noteOff, cc_f, channel, source, notes;


	*new { | midisource=0, midichan=0, freq=55, attack=1, release=1, lowpass=1200, amp=0.0, gate=0, octA=0.3, oct=1, rmA=0.6, flangerA=0.5, vel=0.4, max=0.1, explode=0, lagTime=1, outCh=0 |
		^super.new.init(midisource, midichan, freq, attack, release, lowpass, amp, gate, octA, oct, rmA, flangerA, vel, max, explode, lagTime, outCh)
	}



	init { | midisource, midichan, freq, attack=1, release=1, lowpass, amp, gate, octA, oct, rmA, flangerA, vel, max, explode, lagTime, outCh |
		freq_d = freq;
		lowpass_d = lowpass;
		amp_d = amp;
		gate_d = gate;
		octA_d = octA;
		rmA_d = rmA;
		flangerA_d = flangerA;
		vel_d = vel;
		max_d = max;
		explode_d = explode;
		lagTime_d = lagTime;
		outCh_d = outCh;
		source = midisource;
		channel = midichan;
		notes = Array.newClear(128);

		noteOn = { | src, chan, num, vel |
			("src:" + src + "chan:" + chan + "num:" + num + "vel:" + vel).postln;
			if((src == MIDIClient.sources[source].uid) && (chan == channel), {

				notes[num] = Synth.head(1, \FRDDrone1, [\freq, num.midicps, \attack, attack, \release, release, \lowpass, lowpass_d, \amp, vel / 127.0, \gate, 1, \octA, octA_d, \oct, oct_d, \rmA, rmA_d, \flangerA, flangerA_d, \vel, vel_d, \max, max_d, \explode, explode_d, \lagTime, lagTime_d, \outCh, outCh_d]);
			});
		};

		noteOff = { | src, chan, num, vel |
			notes[num].release(release);
			notes[num] = nil;
		};

		cc_f = { | src, chan, num, val |
			/*lowpass_d, amp_d, gate_d, octA_d, oct_d, rmA_d, flangerA_d, vel_d, max_d, explode_d, lagTime_d, o
			* * CC Mapping * *
			23 -> freq
			24 -> lowpass
			25 -> oct
			26 -> octAmp
			27 -> flangerVel
			28 -> flangerMax
			29 -> flangerAmp
			30 -> rmAmp
			*/
			if((src == MIDIClient.sources[source].uid) && (chan == channel), {
				if(num == 23, { this.freq_(val.midicps) });
				if(num == 24, { this.lowpass_(val.midicps) });
				if(num == 25, { this.oct_(val.midicps) });
				if(num == 26, { this.octA_(val / 127) });
				if(num == 27, { this.vel_(val / 12.7) });
				if(num == 28, { this.max_(val / 127) });
				if(num == 29, { this.flangerA_(val / 127) });
				if(num == 30, { this.rmA_(val / 127) });
			});
		};

		MIDIIn.addFuncTo(\noteOn, noteOn);
		MIDIIn.addFuncTo(\noteOff, noteOff);
		MIDIIn.addFuncTo(\control, cc_f);



	}


	/*
	* GET AND SET METHODS
	*/
	freq {
		^freq_d
	}
	freq_ { | freq |
		freq_d = freq;
		notes.do({ | drone | drone.set(\freq, freq_d); });
		^freq_d
	}



	lowpass {
		^lowpass_d
	}
	lowpass_ { | lowpass |
		lowpass_d = lowpass;
		notes.do({ | drone | drone.set(\lowpass, lowpass_d); });
		^lowpass_d
	}



	amp {
		^amp_d
	}
	amp_ { | amp |
		amp_d = amp;
		notes.do({ | drone | drone.set(\amp, amp_d); });
		^amp_d
	}



	gate {
		^gate_d
	}
	gate_ { | gate |
		gate_d = gate;
		notes.do({ | drone | drone.set(\gate, gate_d); });
		^gate_d
	}



	octA {
		^octA_d
	}
	octA_ { | octA |
		octA_d = octA;
		notes.do({ | drone |  drone.set(\octA, octA_d); });
		^octA_d
	}



	oct {
		^oct_d
	}
	oct_ { | oct |
		oct_d = oct;
		notes.do({ | drone | drone.set(\oct, oct_d); });
		^oct_d
	}



	rmA {
		^rmA_d
	}
	rmA_ { | rmA |
		rmA_d = rmA;
		notes.do({ | drone | drone.set(\rmA, rmA_d); });
		^rmA_d
	}




	flangerA {
		^flangerA_d
	}
	flangerA_ { | flangerA |
		flangerA_d = flangerA;
		notes.do({ | drone | drone.set(\flangerA, flangerA_d); });
		^flangerA_d
	}



	vel {
		^vel_d
	}
	vel_ { | vel |
		vel_d = vel;
		notes.do({ | drone | drone.set(\vel, vel_d); });
		^vel_d
	}



	max {
		^max_d
	}
	max_ { | max |
		max_d = max;
		notes.do({ | drone | drone.set(\max, max_d); });
		^max_d
	}



	lagTime {
		^lagTime_d
	}
	lagTime_ { | lagTime |
		lagTime_d = lagTime;
		notes.do({ | drone | drone.set(\lagTime, lagTime_d); });
		^lagTime_d
	}



	outCh {
		^outCh_d
	}
	outCh_ { | outCh |
		outCh_d = outCh;
		notes.do({ | drone | drone.set(\outCh, outCh_d); });
		^outCh_d
	}



	/*
	* UTILITY FUNCTIONS
	*/
	explode {
		Routine{
			drone.set(\explode, 1);
			0.01.wait;
			drone.set(\explode, 0);
		}.play;
	}

	// Get a Dictionary for integration in FRDMixerMatrixPlugIn
	asMixerMatrixProcess {
		^Dictionary.new.put(\inCh, nil).put(\outCh, outCh_d).put(\inChannels, 0).put(\outChannels, 1)
	}

	// get synth
	synth {
		^drone
	}

	showGUI {
		window = Window.new("FRDDrone1PlugIn", Rect(width: 20, height: 20)).front;
		freq_k = Knob().action_({|val| this.freq_(val.value.pow(2) * 12000 + 33)});
		lowpass_k = Knob().action_({|val| this.lowpass_(val.value * 12000)});
		amp_k = Knob().action_({|val| this.amp_(val.value)});
		oct_k = Knob().action_({|val| this.oct_(val.value * 4 + 0.5)});
		octA_k = Knob().action_({|val| this.octA_(val.value)});
		rmA_k = Knob().action_({|val| this.rmA_(val.value)});
		flangerA_k = Knob().action_({|val| this.flangerA_(val.value)});
		vel_k = Knob().action_({|val| this.vel_(val.value * 2)});
		max_k = Knob().action_({|val| this.max_(val.value)});
		explode_b = Button().action_({this.explode}).states_([["Explode"]]);
		window.layout_(
			HLayout(
				VLayout(StaticText().string_("Amp"), amp_k),
				VLayout(StaticText().string_("Freq"), freq_k),
				VLayout(StaticText().string_("LPF"), lowpass_k),
				VLayout(StaticText().string_("Oct"), oct_k),
				VLayout(StaticText().string_("Oct Amp"), octA_k),
				VLayout(StaticText().string_("Ring"), rmA_k),
				VLayout(StaticText().string_("Flanger"), flangerA_k),
				VLayout(StaticText().string_("Fvel"), vel_k),
				VLayout(StaticText().string_("Fmax"), max_k),
				explode_b
			)
		)
	}





	writeDefFile {
		SynthDef(\FRDDrone1, { | freq=55, attack=1, release=1, lowpass=1200, amp=0.5, gate=0, octA=0, oct=1, rmA=0, flangerA=0, vel=0.4, max=0.01, convA=0, explode=0, lagTime=1, outCh=0 |
			var env = EnvGen.ar(Env.adsr(attack, 0, 1, release), gate, doneAction: 0);
			var sig = FRDDrone1.ar(freq, lowpass, amp, gate, octA, oct, rmA, 0, vel, max, explode, lagTime);
			sig = sig * env ! 2;
			DetectSilence.ar(sig, doneAction: 2);
			Out.ar(outCh, sig);
		}).writeDefFile.add;
	}



}




