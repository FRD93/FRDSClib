FRDMIDIBassSynth {
	var source, channel, outCh, noteOn, noteOff, cc_f, imod_cc_val, imod_val, lpf_cc_val, lpf_val, atk_cc_val, atk_val, notes;

	*new { | midisource=0, midichan=0, atk_cc=32, imod_cc=33, lpf_cc=34, out=0  |
		^super.new.init(midisource, midichan, atk_cc, imod_cc, lpf_cc, out)
	}

	init { | midisource=0, midichan=0, atk_cc=32, imod_cc=33, lpf_cc=34, out=0 |
		source = midisource;
		channel = midichan;
		outCh= out;
		atk_cc_val = atk_cc;
		lpf_cc_val = lpf_cc;
		imod_cc_val = imod_cc;
		atk_val = 0.05;
		lpf_val = 2000;
		imod_val = 10;
		notes = Array.newClear(128);

		noteOn = { | src, chan, num, vel |

			if((src == MIDIClient.sources[source].uid) && (chan == channel), {
				("src:" + src + "chan:" + chan + "num:" + num + "vel:" + vel).postln;
				notes[num] = Synth(\Bass_02, [\freq, num.midicps, \amp, vel / 127.0, \atk, atk_val, \lpf, lpf_val, \out, outCh]);
			});
		};

		noteOff = { | src, chan, num, vel |
			//("src:" + src + "chan:" + chan + "num:" + num + "vel:" + vel).postln;
			if((src == MIDIClient.sources[source].uid) && (chan == channel), {
				notes[num].release(0.01);
				notes[num] = nil;
			});
		};

		cc_f = { | src, chan, num, val |

			if((src == MIDIClient.sources[source].uid) && (chan == channel), {
				[src, chan, num, val].postln;
				// atk
				if(atk_cc_val == num, {
					atk_val = val * 0.2 / 127;
					notes.do({ | synth | synth.set(\atk, atk_val) });
				});
				// lpf
				if(lpf_cc_val == num, {
					lpf_val = (val * 8000 / 127) + 20;
					notes.do({ | synth | synth.set(\lpf, lpf_val) });
				});
				// imod
				if(imod_cc_val == num, {
					imod_val = (val * 99 / 127) + 1;
					notes.do({ | synth | synth.set(\imod, imod_val) });
				});
			});
		};

		MIDIIn.addFuncTo(\noteOn, noteOn);
		MIDIIn.addFuncTo(\noteOff, noteOff);
		MIDIIn.addFuncTo(\control, cc_f);

	}

	writeSynthDef {
		// VECCHIO
		SynthDef(\Bass_02, { | freq=40, lpf=2000, amp=0.5, atk=0.01, gate=1, pan=0, out=0 |
			var osc, env, mod;
			env = EnvGen.ar(Env.adsr(atk, 0.1, 0.9, 0.01, 1), gate, AmpCompA.ir(freq), doneAction: 2);
			osc = LFSaw.ar(freq + [SinOsc.ar(Rand(0.01, 0.1), Rand(0.0, 2pi), Rand(0.01, 1)), -1 * Rand(-0.2309, 0), Rand(0, 0.327) * SinOsc.ar(Rand(0.01, 0.1), Rand(0.0, 2pi), Rand(0.01, 1))], 1.5pi, env * amp / 3).sum;
			osc = BLowPass.ar(osc, lpf * amp + 30);
			Out.ar(out, Pan2.ar(osc, pan));
		}).writeDefFile.add;

		SynthDef(\Bass_02, { | freq=60, lpf=15000, amp=0.5, atk=0.01, imod=10, gate=1, pan=0, out=0 |
			var osc, env, fenv, mod;
			fenv = EnvGen.ar(Env.new([100, 0], [atk * 0.01], 2));
			mod = SinOsc.ar(freq, 0, fenv * freq);
			env = EnvGen.ar(Env.perc(atk, 0.5));
			env = env * EnvGen.ar(Env.adsr(atk, 0.1, 0.9, 0.01, 1), gate, AmpCompA.ir(freq), doneAction: 2);
			osc = LFSaw.ar(freq + SinOsc.ar(freq, 0, freq * imod, freq) + mod, 0, env);
			//osc = LFSaw.ar(freq + mod, 1.5pi, env * amp / 3);
			osc = BLowPass.ar(osc, lpf * amp + 30);
			Out.ar(out, Pan2.ar(osc, pan));
		}).writeDefFile.add;
	}
}