FRDChimes {
	var data, routine;

	*new { | base_note=34, sequence_len=1, amp=0.5, out=0, note_pattern, delta_pattern, dur_pattern, clock, quant |
		^super.new.init(base_note, sequence_len, amp, out, note_pattern, delta_pattern, dur_pattern, clock, quant);
	}

	init { | base_note=34, sequence_len=1, amp=0.5, out=0, note_pattern, delta_pattern, dur_pattern, clock, quant |
		data = Dictionary.new;
		data.put("base_note", base_note);
		data.put("sequence_len", sequence_len);
		data.put("amp", amp);
		data.put("out", out);
		data.put("note_pattern", note_pattern);
		data.put("delta_pattern", delta_pattern);
		data.put("dur_pattern", dur_pattern);
		data.put("clock", clock);
		data.put("quant", quant);
	}

	play {
		routine = Routine{
			data["sequence_len"].do({ | id |
				Synth.head(Server.local, \FRDChime, [\freq, (data["note_pattern"].wrapAt(id) + data["base_note"]).midicps, \dur, data["dur_pattern"].wrapAt(id) * data["delta_pattern"].wrapAt(id), \pan, rrand(-0.63, 0.63), \amp, data["amp"], \out, data["out"]]);
				data["delta_pattern"].wrapAt(id).wait;
			});
		}.play(data["clock"], data["quant"]);
	}

	playRandom {
		routine = Routine{
			data["sequence_len"].do({ | seq_id |
				data["note_pattern"].do({ | note, id |
					var delta_choice = data["delta_pattern"].size.rand;
					Synth.head(Server.local, \FRDChime, [\freq, (data["note_pattern"].choose + data["base_note"]).midicps, \dur, data["dur_pattern"][delta_choice] * data["delta_pattern"][delta_choice], \pan, rrand(-0.63, 0.63), \amp, data["amp"], \out, data["out"]]);
					data["delta_pattern"][delta_choice].wait;
				});
			});
		}.play(data["clock"], data["quant"]);
	}

	isPlaying {
		^routine.isPlaying
	}

	stop { | name |
		routine.stop;
	}

	baseNote_ { | base_note |
		data.put("base_note", base_note);
	}

	sequenceLen_ { | sequence_len |
		data.put("sequence_len", sequence_len);
	}

	notePattern_ { | note_pattern |
		data.put("note_pattern", note_pattern);
	}

	deltaPattern_ { | delta_pattern |
		data.put("delta_pattern", delta_pattern);
	}

	durPattern_ { | dur_pattern |
		data.put("dur_pattern", dur_pattern);
	}

	amp_ { | amp |
		data.put("amp", amp);
	}

	out_ { | out |
		data.put("out", out);
	}

	clock_ { | clock |
		data.put("clock", clock);
	}

	quant_ { | quant |
		data.put("quant", quant);
	}

	writeSynthDef {
		SynthDef(\FRDChime, { | freq=88, imod=1, amp=0.5, dur=0.5, pan=0, out=0 |
			var sig, signal, aenv, fmod;
			var freqs = [1400, 2300, 5267, 8543, 9832, 15222, 16387];
			var amps = [1, 0.8, 0.6, 0.4, 0.3, 0.4, 0.5];
			aenv = EnvGen.ar(Env.perc(0.001, dur - 0.001), levelScale: amp * 24, doneAction: 2);
			fmod = SinOsc.ar(freq, freq, freq * imod) * aenv;
			sig = LFTri.ar(freq);
			sig = (sig * 0.01) + BBandPass.ar(sig, freqs, 0.1, amps).sum * 6 / amps.sum;

			sig = MoogFF.ar(sig / 2, 12000);
			sig = sig * aenv;
			sig = Pan2.ar(sig, pan);
			Out.ar(out, sig);
		}).writeDefFile.add;
	}

}