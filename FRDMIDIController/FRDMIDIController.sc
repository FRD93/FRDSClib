FRDMIDIController {
	var <params, dev, srcID, noteOns, noteOffs;

	*new { | device=nil |
		^super.new.init(device)
	}

	init { | device |
		dev = device;
		params = Dictionary.new;
		params.put("Notes", Dictionary.new);
		params.put("NoteOn", 0);
		params.put("NoteOff", 0);
		128.do({ | id | params.at("Notes").put(id.asString, 0); });
		if(dev == nil, {
			srcID = nil;
		}, {
			srcID = nil;
			MIDIClient.destinations.do({ | client, index | if(client.name == dev, { srcID = index  }); });
			if(srcID == nil, { ("Wrong MIDI device name:" + dev).postln; });
		});
		if(dev == "Arturia MiniLab mkII", {
			"MIDI Controller set: Arturia MiniLab mkII".postln;
			params.put("CC Numbers", Dictionary.new
				.put("Knob 1", 112)
				.put("Knob 1 button", 113)
				.put("Knob 2", 74)
				.put("Knob 3", 71)
				.put("Knob 4", 76)
				.put("Knob 5", 77)
				.put("Knob 6", 93)
				.put("Knob 7", 73)
				.put("Knob 8", 75)
				.put("Knob 9", 114)
				.put("Knob 9 button", 115)
				.put("Knob 10", 18)
				.put("Knob 11", 19)
				.put("Knob 12", 16)
				.put("Knob 13", 17)
				.put("Knob 14", 91)
				.put("Knob 15", 79)
				.put("Knob 16", 72)
				.put("Pad 1", 22)
				.put("Pad 2", 23)
				.put("Pad 3", 24)
				.put("Pad 4", 25)
				.put("Pad 5", 26)
				.put("Pad 6", 27)
				.put("Pad 7", 28)
				.put("Pad 8", 29)
			)
			.put("CC Funcs", Dictionary.new
				.put("Knob 1", 0)
				.put("Knob 1 button", 0)
				.put("Knob 2", 0)
				.put("Knob 3", 0)
				.put("Knob 4", 0)
				.put("Knob 5", 0)
				.put("Knob 6", 0)
				.put("Knob 7", 0)
				.put("Knob 8", 0)
				.put("Knob 9", 0)
				.put("Knob 9 button", 0)
				.put("Knob 10", 0)
				.put("Knob 11", 0)
				.put("Knob 12", 0)
				.put("Knob 13", 0)
				.put("Knob 14", 0)
				.put("Knob 15", 0)
				.put("Knob 16", 0)
				.put("Pad 1", 0)
				.put("Pad 2", 0)
				.put("Pad 3", 0)
				.put("Pad 4", 0)
				.put("Pad 5", 0)
				.put("Pad 6", 0)
				.put("Pad 7", 0)
				.put("Pad 8", 0)
			);
		});
	}

	postCC {
		MIDIFunc.cc({arg ...args; args.postln});
	}

	addFuncToCC { | cc, func |
		if(params.at("CC Funcs").at(cc).class == MIDIFunc, { params.at("CC Funcs").at(cc).free; });
		params.at("CC Funcs").put(cc, MIDIFunc.cc(func, params.at("CC Numbers").at(cc)));
	}

	removeFuncToCC { | cc |
		params.at("CC Funcs").at(cc).free;
		params.at("CC Funcs").put(cc, nil);
	}

	addSynthToNotes { | synth_name, synth_default_args, chan=0 |
		if(params.at("NoteOn").class == MIDIFunc, { params.at("NoteOn").free; });
		"Adding NoteOn synth".postln;
		params.put("NoteOn", MIDIFunc.noteOn({ | velocity, note, chan, null |
			velocity.postln;
			params.at("Notes").put(note.asString, Synth(synth_name, [\midi_note, note, \velocity, velocity] ++ synth_default_args));
		}));
		if(params.at("NoteOff").class == MIDIFunc, { params.at("NoteOff").free; });
		params.put("NoteOff", MIDIFunc.noteOff({ | velocity, note, chan, null |
			params.at("Notes").at(note.asString).set(\gate, 0);
			params.at("Notes").put(note.asString, 0);
		}));
	}

	mapCCToSynthArg { | cc, param, map_func |
		var func = { | val |
			val = map_func.value(val);
			params.at("Notes").keysValuesDo({ | key, synth |
				if(synth.class == Synth, { synth.set(param, val); });
			});
		};
		this.addFuncToCC(cc, func);
	}


	postCCNumbers {
		if(dev == "Arturia MiniLab mkII", {
			"Arturia MiniLab mkII controls numbers:
* * KNOBS * *

Knob 1: 112 (strange behavior: values in range [62, 66])
Knob 1 button: 113
Knob 2: 74
Knob 3: 71
Knob 4: 76
Knob 5: 77
Knob 6: 93
Knob 7: 73
Knob 8: 75
Knob 9: 114 (strange behavior: values in range [62, 66])
Knob 9 button: 115
Knob 10: 18
Knob 11: 19
Knob 12: 16
Knob 13: 17
Knob 14: 91
Knob 15: 79
Knob 16: 72

* * PADS * *

Pad 1: 22
Pad 2: 23
Pad 3: 24
Pad 4: 25
Pad 5: 26
Pad 6: 27
Pad 7: 28
Pad 8: 29

* * SLIDE * *

Slide 1: 1
".postln;


		});
	}
}