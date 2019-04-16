FRDDrone1 {
	*ar {|freq=55, lowpass=1200, amp=0.5, gate=0, octA=0, oct=1, rmA=0, flangerA=0, vel=0.4, max=0.01,  explode=0/*, shosh=0*/, lagTime=1|
		var sig, env, detune, out;
		var in, ffreq, hasFreq, octaver, rm, flanger, feedback, delay, conv;
		var lopToZero, lopToMax;

		// INVILUPPI DI CONTROLLO
		freq = Lag.ar(K2A.ar(freq), lagTime);
		lowpass = Lag.ar(K2A.ar(lowpass), lagTime);
		lopToZero = EnvGen.ar(Env.new([0,1,1,0],[0.005,3.85,0]), explode, lowpass, 0);
		//lopToMax = EnvGen.ar(Env.new([0,18000,18000,0],[0,1,0.5]), shosh, 1, 0);
		lowpass = lowpass - lopToZero;// + lopToMax;

		// SIG
		env = EnvGen.ar(Env.adsr(5,sustainLevel:0.8,releaseTime:5),gate,doneAction:0);
		detune = [1, 0.9991, 1.0003, 0.998, 1.006];
		freq = freq * detune;
		sig = Saw.ar(freq, amp);
		sig = sig.pow(sig.cubed).cubed;
		sig = BLowPass4.ar(sig, SinOsc.ar(0.05 * detune.pow(8), 0, lowpass/4, lowpass*3/4), 0.5);
		sig = sig + DelayC.ar(sig, 0.1, SinOsc.ar(0.05 * detune.pow(16), 0, 0.05, 0.05));
		sig = BHiPass4.ar(sig, lowpass / 2);
		out = sig.sum / detune.size;
		out = out + FreeVerb.ar(out, 1, 3) / 2;
		out = LeakDC.ar(out) * amp;
		out = Limiter.ar(out, amp);

		// EFXs
		in = out;
		feedback = LocalIn.ar(1);
		octaver = PitchShift.ar(in, 0.1, oct, 0.1, 0) * octA;
		# ffreq, hasFreq = Pitch.kr(octaver, 440, 60, 4000, 220, 16, 1);
		in = in + octaver;
		rm = in * SinOsc.ar(ffreq/3, 0, rmA);
		in = in + rm;
		flanger = (in + feedback + DelayC.ar(in + feedback, 1, SinOsc.ar(vel, 0, (max/2), (max/2)))) / 2;
		in = in + (flanger*flangerA);
		out = in;
		flanger = LeakDC.ar(flanger);
		LocalOut.ar(flanger);

		^out



	}
}


