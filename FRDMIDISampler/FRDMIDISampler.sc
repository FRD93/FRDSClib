FRDMIDISampler {
	var source, channel, outCh, noteOn, noteOff, cc_f, players, buffers, atk, rel;

	*new { | midisource=0, midichan=0, folder="", attack=1, release=1, out=0  |
		^super.new.init(midisource, midichan, folder, attack, release, out)
	}

	init { | midisource=0, midichan=0, folder="", attack=1, release=1, out=0 |
		source = midisource;
		channel = midichan;
		atk = attack;
		rel = release;
		outCh= out;
		players = Array.newClear(128);
		buffers = PathName(folder).files.collect({ | file | Buffer.read(Server.local, file.asAbsolutePath) });

		noteOn = { | src, chan, num, vel |
			if((src == MIDIClient.sources[source].uid) && (chan == channel), {
				if(players[num].class != Synth, {
					[num, buffers[num]].postln;

					players[num] = Synth(\Sampler, [\buf, buffers[num], \amp, vel / 127, \rate, 1, \gate, 1, \attack, atk, \release, rel, \out, outCh]);
				});
			});
		};

		noteOff = { | src, chan, num, vel |
			//("src:" + src + "chan:" + chan + "num:" + num + "vel:" + vel).postln;
			if((src == MIDIClient.sources[source].uid) && (chan == channel), {
				if(players[num].class == Synth, {
					players[num].release(rel);
					players[num] = nil;
				});
			});
		};

		cc_f = { | src, chan, num, val |

			if((src == MIDIClient.sources[source].uid) && (chan == channel), {
				[src, chan, num, val].postln;
				// insert cc controls here

			});
		};

		MIDIIn.addFuncTo(\noteOn, noteOn);
		MIDIIn.addFuncTo(\noteOff, noteOff);
		MIDIIn.addFuncTo(\control, cc_f);

	}

	writeSynthDef {
		SynthDef(\Sampler, { | buf, amp=0.5, rate=1, pan=0, gate=1, attack=0.01, release=0.01, out=0 |
			var sig, env;
			env = EnvGen.ar(Env.adsr(attack, 0, 1, release, 1), gate);
			sig  = PlayBuf.ar(1, buf, rate, 1) * amp;
			sig = sig * env;
			DetectSilence.ar(sig);
			Out.ar(out, Pan2.ar(sig, pan));
		}).writeDefFile.add;
	}
}