FRDMIDIDrums {
	var source, channel, outCh, noteOn, noteOff, cc_f, has_tom;

	var kick_note=29, kick_timbre=30, tom1_loss=31, tom2_loss=32;
	var snare_note=49, snare_timbre=110, snare_dur=15;
	var tom1_synth, tom1_exc_chan=90, tom1_exc2_chan=91, tom2_synth, tom2_exc_chan=92, tom2_exc2_chan=93;

	*new { | midisource=0, midichan=0, enable_tom=true, out=0  |
		^super.new.init(midisource, midichan, enable_tom, out)
	}

	init { | midisource=0, midichan=0, enable_tom=true, out=0 |
		source = midisource;
		channel = midichan;
		has_tom = enable_tom;
		outCh= out;

		if(has_tom, {
			tom1_synth = Synth(\Tom, [\tension, 0.012, \loss, 0.9993, \amp, 0.2, \in, tom1_exc_chan, \ext_trig, tom1_exc2_chan, \pan, -0.3, \out, outCh]);
			tom2_synth = Synth(\Tom, [\tension, 0.015, \loss, 0.9994, \amp, 0.2, \in, tom2_exc_chan, \ext_trig, tom2_exc2_chan, \pan, 0.3, \out, outCh]);
		});

		noteOn = { | src, chan, num, vel |
			//("src:" + src + "chan:" + chan + "num:" + num + "vel:" + vel).postln;
			if((src == MIDIClient.sources[source].uid) && (chan == channel), {

				/*
				* * NoteOn Mapping * *
				34 -> Snare Side
				35 -> Kick Double
		V		36 -> Kick Center
				37 -> Snare Sidestick
		V		38 -> Snare Center
				39 -> Snare Rimclick
				40 -> Snare Rimshot
				41 -> Floor Tom 2 Center
				42 -> Hi-Hat Tip Closed
				43 -> Floor Tom 1 Center
				44 -> Hi-Hat Pedal
		V		45 -> Clap
				46 -> Hi-Hat Tip CC Control
		V		47 -> Rack Tom 2 Center
		V		48 -> Rack Tom 1 Center
				49 -> Splash Choke
				50 -> Splash Edge
				51 -> Ride Bow Tip
				52 -> Ride Bow Shank
				53 -> Ride Bell
				54 -> Crash Left Choke
				55 -> Crash Left Edge
				56 -> Crash Right Choke
				57 -> Crash Right Edge
				58 -> Ride Choke
				59 -> Ride Edge
				60 -> Hi-Hat Shank Closed
				61 -> Hi-Hat Shank Closed Tight
				62 -> Hi-Hat Shank Looser
				63 -> Hi-Hat Shank Open 1
				64 -> Hi-Hat Shank Open 2
				65 -> Hi-Hat Shank Open 3
				66 -> Hi-Hat Tip Closed Tight
		V		67 -> Hi-Hat Closed 1 Pan Left
		V		68 -> Hi-Hat Closed 2 Pan Right
		V		69 -> Hi-Hat Closed 3 Pan Center
				70 -> Hi-Hat Tip Open 2
				71 -> Hi-Hat Tip Open 3
				72 -> Hi-Hat Pedal
				73 -> Hi-Hat Footsplash


				*/
				if(num == 36, {Synth(\Kick, [\midiInit, kick_timbre, \midiEnd, kick_note, \amp, vel / 127, \outBus, outCh])});
				if(num == 38, {Synth(\Snare, [\midiInit, snare_timbre, \midiEnd, snare_note, \amp, vel / 127, \dur, snare_dur / 127, \outBus, outCh])});
				if(num == 45, {Synth(\Clap, [\shift, 1, \body, 1, \amp, vel / 127, \outBus, outCh])});
				if(has_tom, {
					if(num == 47, {Synth.before(tom1_synth, \TomExcitation, [\out, tom1_exc_chan, \amp, vel / 127, \outBus, outCh]); Synth.before(tom1_synth, \TomTrigger, [\out, tom1_exc2_chan]); });
					if(num == 48, {Synth.before(tom2_synth, \TomExcitation, [\out, tom2_exc_chan, \amp, vel / 127, \outBus, outCh]); Synth.before(tom2_synth, \TomTrigger, [\out, tom2_exc2_chan]);});
				});
				if(num == 67, {Synth(\Hat, [\hpf, 1000, \shift, 1, \amp, vel / 127, \pan, -0.63, \outBus, outCh])});
				if(num == 68, {Synth(\Hat, [\hpf, 900, \shift, 0.75, \amp, vel / 127, \pan, 0.63, \outBus, outCh])});
				if(num == 69, {Synth(\Hat, [\hpf, 1100, \shift, 1.25, \amp, vel / 127, \pan, 0, \outBus, outCh])});

			});
		};

		noteOff = { | src, chan, num, vel |

		};

		cc_f = { | src, chan, num, val |

			if((src == MIDIClient.sources[source].uid) && (chan == channel), {
				/*
				* * CC Mapping * *
				29 -> Kick Note
				30 -> Kick Timbre
				31 -> Tom 1 Loss
				32 -> Tom 2 Loss

				*/
				if(num == 29, {kick_note = val});
				if(num == 30, {kick_timbre = val});
				if(num == 31, {tom1_loss = 0.999 + ((val / 127) * 0.0009); tom1_synth.set(\loss, tom1_loss);});
				if(num == 32, {tom2_loss = 0.999 + ((val / 127) * 0.0009); tom2_synth.set(\loss, tom2_loss);});

			});
		};

		MIDIIn.addFuncTo(\noteOn, noteOn);
		MIDIIn.addFuncTo(\noteOff, noteOff);
		MIDIIn.addFuncTo(\control, cc_f);

	}

	writeSynthDef {
		SynthDef(\Kick, { | amp=0.3, dur=0.29, midiInit=110, midiEnd=29, outBus=0 |
			var env0, env1, env1m, out;

			env0 =  EnvGen.ar(Env.new([0.5, 1, 0.5, 0], [0.005, 0.06, dur-0.03], [-4, -2, -4]), doneAction:2);
			env1 = EnvGen.ar(Env.new([midiInit, 59, midiEnd], [0.005, dur], [-4, -5]));
			env1m = env1.midicps;

			out = LFPulse.ar(env1m, 0, 0.5, 1, -0.5);
			out = out + WhiteNoise.ar(1);
			out = LPF.ar(out, env1m*1.5, env0);
			out = out + SinOsc.ar(env1m, 0.5, env0);

			out = out * 1.2;
			out = out.clip2(1) * amp;

			Out.ar(outBus, out.dup);
		}).writeDefFile.add;

		SynthDef(\Snare, { | amp=0.8, dur=0.13, midiInit=110, midiEnd=49, outBus=0 |
			var env0, env1, env2, env1m, oscs, noise, out;

			env0 = EnvGen.ar(Env.new([0.5, 1, 0.5, 0], [0.005, 0.03, dur-0.03], [-4, -2, -4]));
			env1 = EnvGen.ar(Env.new([midiInit, 60, midiEnd], [0.005, dur-0.03], [-4, -5]));
			env1m = env1.midicps;
			env2 = EnvGen.ar(Env.new([1, 0.4, 0], [0.05, dur], [-2, -2]), doneAction:2);

			oscs = LFPulse.ar(env1m, 0, 0.5, 1, -0.5) + LFPulse.ar(env1m * 1.6, 0, 0.5, 0.5, -0.25);
			oscs = LPF.ar(oscs, env1m*1.2, env0);
			oscs = oscs + SinOsc.ar(env1m, 0.8, env0);

			noise = WhiteNoise.ar(0.2);
			noise = HPF.ar(noise, 200, 2);
			noise = BPF.ar(noise, 6900, 0.6, 3) + noise;
			noise = noise * env2;

			out = oscs + noise;
			out = out.clip2(1) * amp;

			Out.ar(outBus, out.dup);
		}).writeDefFile.add;

		SynthDef(\Clap, { | amp = 0.5, shift=1, body=1, outBus=0 |
			var env1, env2, out, noise1, noise2;
			var hpf1=600, lpf1=2000, hpf2=1000, lpf2=1200;
			body = (3.0 - (body.clip(0, 1) * 3)).clip(0.01, 3);
			hpf1 = hpf1 * shift * body;
			lpf1 = lpf1 * shift;
			hpf2 = hpf2 * shift * body;
			lpf2 = lpf2 * shift;

			env1 = EnvGen.ar(Env.new([0, 1, 0, 1, 0, 1, 0, 1, 0], [0.001, 0.013, 0, 0.01, 0, 0.01, 0, 0.03], [0, -3, 0, -3, 0, -3, 0, -4]));
			env2 = EnvGen.ar(Env.new([0, 1, 0], [0.02, 0.3], [0, -4]), doneAction:2);

			noise1 = WhiteNoise.ar(env1);
			noise1 = HPF.ar(noise1, hpf1);
			noise1 = BPF.ar(noise1, lpf1, 3);

			noise2 = WhiteNoise.ar(env2);
			noise2 = HPF.ar(noise2, hpf2);
			noise2 = BPF.ar(noise2, lpf2, 0.7, 0.7);

			out = noise1 + noise2;
			out = out * 2;
			out = out.softclip * amp;

			Out.ar(outBus, out.dup);
		}).writeDefFile.add;

		SynthDef(\Hat, { | amp=0.3, hpf=1000, shift=1, pan=0, outBus=0 |
			var env1, env2, out, oscs1, noise, n, n2;
			var bpf=6000, lpf=3000, hhpf=1000;
			bpf = bpf * shift;
			lpf = lpf * shift;
			hhpf = hhpf * shift;

			n = 5;
			thisThread.randSeed = 4;

			env1 = EnvGen.ar(Env.new([0, 1.0, 0], [0.001, 0.2], [0, -12]));
			env2 = EnvGen.ar(Env.new([0, 1.0, 0.05, 0], [0.002, 0.05, 0.03], [0, -4, -4]), doneAction:2);

			oscs1 = Mix.fill(n, {|i|
				SinOsc.ar(
					( i.linlin(0, n-1, 42, 74) + rand2(4.0) ).midicps,
					SinOsc.ar( (i.linlin(0, n-1, 78, 80) + rand2(4.0) ).midicps, 0.0, 12),
					1/n
				)
			});

			oscs1 = BHiPass.ar(oscs1, hpf, 2, env1);
			n2 = 8;
			noise = WhiteNoise.ar;
			noise = Mix.fill(n2, {|i|
				var freq;
				freq = (i.linlin(0, n-1, 40, 50) + rand2(4.0) ).midicps.reciprocal;
				CombN.ar(noise, 0.04, freq, 0.1)
			}) * (1/n) + noise;
			noise = BPF.ar(noise, bpf, 0.9, 0.5, noise);
			noise = BLowShelf.ar(noise, lpf, 0.5, -6);
			noise = BHiPass.ar(noise, hhpf, 1.5, env2);

			out = noise + oscs1;
			out = out.softclip;
			out = out * amp;

			Out.ar(outBus, Pan2.ar(out, pan));
		}).writeDefFile.add;

		SynthDef(\TomExcitation, { | amp=0.5, dur=0.1, out=20 |
			var excitation = EnvGen.kr(Env.perc(0.00, 1), Impulse.kr(0), timeScale: 0.1, doneAction: 0 ) * (PinkNoise.ar(0.4));
			var ex2env = EnvGen.ar(Env.perc(0.001, 1, curve: -8), Impulse.kr(0), timeScale: 0.1, doneAction: 0 );
			excitation =  excitation + SinOsc.ar(40 + SinOsc.ar(40, 0, 40 * ex2env * 100), 1.5pi, ex2env * 0.25);
			DetectSilence.ar(excitation, doneAction: 2);
			Out.ar(out, excitation * amp);
		}).writeDefFile.add;

		SynthDef(\TomTrigger, { | out=20 |
			Out.ar(out, Impulse.ar(0));
		}).writeDefFile.add;

		SynthDef(\Tom, { | amp=0.5, atk=0.01, tension=0.02, loss=0.9999, pan=0, in=20, ext_trig=21, out=0 |
			var excitation = In.ar(in, 1);
			var sig = MembraneCircle.ar(excitation, tension, loss) * amp;

			sig = CompanderD.ar(sig, thresh: 0.25, slopeBelow: 1, slopeAbove: 0.01, clampTime: 0.025, relaxTime: 0.0001) * 3;
			sig = sig + HPF.ar(sig, 3000);
			sig = sig + HPF.ar(sig, 4000);
			sig = sig + HPF.ar(sig, 5000);
			sig = sig.tanh;
			sig = Pan2.ar(sig, pan);
			sig = GVerb.ar(sig, roomsize: 10, revtime: 0.5, damping: 0, inputbw: 0);

			//sig = sig + BBandPass.ar(sig, 4000);
			//DetectSilence.ar(sig, doneAction: 2);
			Out.ar(out, sig);
		}).writeDefFile.add;
	}
}