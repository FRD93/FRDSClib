/*
Drone1
A drone-like synthesizer.
Copyright ©2018, Francesco Roberto Dani
*/

FRDDrone1PlugIn {
	// System variables
	var freq_d, lowpass_d, amp_d, gate_d, octA_d, oct_d, rmA_d, flangerA_d, vel_d, max_d, explode_d, lagTime_d, outCh_d;
	var drone;
	// GUI variables
	var window, freq_k, lowpass_k, amp_k, octA_k, oct_k, rmA_k, flangerA_k, vel_k, max_k, explode_b;



	*new { | freq=55, lowpass=1200, amp=0.0, gate=0, octA=0.3, oct=1, rmA=0.6, flangerA=0.5, vel=0.4, max=0.1, explode=0, lagTime=1, outCh=0 |
		^super.new.init(freq, lowpass, amp, gate, octA, oct, rmA, flangerA, vel, max, explode, lagTime, outCh)
	}



	init { | freq, lowpass, amp, gate, octA, oct, rmA, flangerA, vel, max, explode, lagTime, outCh |
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

		drone = Synth.head(1, \FRDDrone1, [\freq, freq_d, \lowpass, lowpass_d, \amp, amp_d, \gate, gate_d, \octA, octA_d, \oct, oct_d, \rmA, rmA_d, \flangerA, flangerA_d, \vel, vel_d, \max, max_d, \explode, explode_d, \lagTime, lagTime_d, \outCh, outCh_d]);

	}


	/*
	* GET AND SET METHODS
	*/
	freq {
		^freq_d
	}
	freq_ { | freq |
		freq_d = freq;
		drone.set(\freq, freq_d);
		^freq_d
	}



	lowpass {
		^lowpass_d
	}
	lowpass_ { | lowpass |
		lowpass_d = lowpass;
		drone.set(\lowpass, lowpass_d);
		^lowpass_d
	}



	amp {
		^amp_d
	}
	amp_ { | amp |
		amp_d = amp;
		drone.set(\amp, amp_d);
		^amp_d
	}



	gate {
		^gate_d
	}
	gate_ { | gate |
		gate_d = gate;
		drone.set(\gate, gate_d);
		^gate_d
	}



	octA {
		^octA_d
	}
	octA_ { | octA |
		octA_d = octA;
		drone.set(\octA, octA_d);
		^octA_d
	}



	oct {
		^oct_d
	}
	oct_ { | oct |
		oct_d = oct;
		drone.set(\oct, oct_d);
		^oct_d
	}



	rmA {
		^rmA_d
	}
	rmA_ { | rmA |
		rmA_d = rmA;
		drone.set(\rmA, rmA_d);
		^rmA_d
	}




	flangerA {
		^flangerA_d
	}
	flangerA_ { | flangerA |
		flangerA_d = flangerA;
		drone.set(\flangerA, flangerA_d);
		^flangerA_d
	}



	vel {
		^vel_d
	}
	vel_ { | vel |
		vel_d = vel;
		drone.set(\vel, vel_d);
		^vel_d
	}



	max {
		^max_d
	}
	max_ { | max |
		max_d = max;
		drone.set(\max, max_d);
		^max_d
	}



	lagTime {
		^lagTime_d
	}
	lagTime_ { | lagTime |
		lagTime_d = lagTime;
		drone.set(\lagTime, lagTime_d);
		^lagTime_d
	}



	outCh {
		^outCh_d
	}
	outCh_ { | outCh |
		outCh_d = outCh;
		drone.set(\outCh, outCh_d);
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
		SynthDef(\FRDDrone1, { | freq=55, lowpass=1200, amp=0.5, gate=0, octA=0, oct=1, rmA=0, flangerA=0, vel=0.4, max=0.01, convA=0, explode=0, lagTime=1, outCh=0 |
			var sig = FRDDrone1.ar(freq, lowpass, amp, gate, octA, oct, rmA, flangerA, vel, max, explode, lagTime);
			Out.ar(outCh, sig);
		}).writeDefFile.add;
	}



}




