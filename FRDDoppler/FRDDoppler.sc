/*
* * DopplerSF * *
Sintetizzatore per simulare l'effetto doppler su un file audio
©2021, Francesco Roberto Dani

Argomenti:
- buf: buffer contenente il file audio da processare (MONO)
- pos: posizione temporale del transito
- distance: distanza della sorgente sonora dal punto di ascolto (metri)
- speed: velocità della sorgente sonora (m/s)
- direction: (1 = da SX a DX, -1 = da DX a SX)
*/

FRDDoppler {
	var win, synthdef_bytes, buf, synth, rout, pos, width, distance, speed, direction;
	var open_butt, play_butt, rec_butt, pos_sli, width_sli, distance_sli, speed_sli, direction_sli;

	*new {
		^super.new.init();
	}

	init {
		buf = nil;
		synth = nil;
		rout = nil;
		pos = 0.5;
		width = 0.01;
		distance = 43;
		speed = 5;
		direction = 1;
		synthdef_bytes = SynthDef(\DopplerSF, { | buf, pos=0.5, width=0.2, distance=10000, speed=5, direction=1, out=0 |
			var signal = PlayBuf.ar(1, buf, doneAction: 2);
			var buf_dur = BufDur.ir(buf);
			var cross_dur = (distance / speed).clip(0.0001, buf_dur);
			var doppler_env = EnvGen.ar(Env.new([distance, distance.neg], [(buf_dur * pos) - (width / 2), width, (buf_dur * (1.0 - pos)) - (width / 2)]));
			var radial_distance = (doppler_env.squared + speed.squared).sqrt;
			var doppler_shift = DelayC.ar(signal, 1.0, radial_distance / 350.0);
			var amp = (radial_distance.max(1.0)).reciprocal;
			var result = Pan2.ar(doppler_shift * amp, doppler_env * direction / distance.abs);
			Out.ar(out, result);
		}).add.asBytes;
	}

	showGUI {
		win =  Window.new("SF Doppler Effect Generator");
		open_butt = Button().states_([["Apri SF"]]).action_({ | val |
			Dialog.openPanel({ | path |
				path.postln;
				buf = Buffer.readChannel(Server.default, path, 0, -1, [0]);
			})
		});
		play_butt = Button().states_([["Play"], ["Playing...", Color.black, Color.green(0.8)]]).action_({ | val |
			if(val.value == 1, {
				rout = Routine{
					synth = Synth(\DopplerSF, [\buf, buf, \pos, pos, \width, width, \distance, distance, \speed, speed, \direction, direction]);
					buf.duration.wait;
					play_butt.value_(0);
				}.play(AppClock);
			}, {
				rout.stop;
				synth.free;
			});
		});
		rec_butt = Button().states_([["Render"]]).action_({ | val |
			Dialog.savePanel({ | path |
				var score = Score([
					[0.0, ['/d_recv', synthdef_bytes]],
					[0.0, ['/b_allocReadChannel', 1, buf.path, 0, -1, 0]],
					[0.0, ['/s_new', \DopplerSF, -1, 0, 0, \buf, 1, \pos, pos, \width, width, \distance, distance, \speed, speed, \direction, direction]],
					[buf.duration, '/c_set', 0, 0]
				]);
				score.recordNRT(
					outputFilePath: path,
					headerFormat: "wav",
					sampleFormat: "int16"
				);
			});
		});
		pos_sli = Slider().orientation_(\horizontal).action_({ | val |
			pos = val.value;
		}).valueAction_(pos);
		width_sli = Slider().orientation_(\horizontal).action_({ | val |
			width = Map(val.value, 0.0, 1.0, 0.0001, 1.0);

		}).valueAction_(width);
		distance_sli = Slider().orientation_(\horizontal).action_({ | val |
			distance = Map(val.value, 0.0, 1.0, 2, 100);
		}).valueAction_(Map(distance, 2, 100, 0.0, 1.0));
		speed_sli = Slider().orientation_(\horizontal).action_({ | val |
			speed = Map(val.value, 0.0, 1.0, 2, 100);
		}).valueAction_(Map(speed, 2, 100, 0.0, 1.0));
		direction_sli = Slider().orientation_(\horizontal).action_({ | val |
			direction = Map(val.value, 0.0, 1.0, -1.0, 1.0);
		}).valueAction_(Map(direction, -1.0, 1.0, 0.0, 1.0));
		win.layout_(
			GridLayout.rows(
				[open_butt, play_butt, rec_butt],
				[StaticText().string_("Pos:"), [pos_sli, columns:2]],
				[StaticText().string_("Width:"), [width_sli, columns:2]],
				[StaticText().string_("Distance:"), [distance_sli, columns:2]],
				[StaticText().string_("Speed:"), [speed_sli, columns:2]],
				[StaticText().string_("Direction:"), [direction_sli, columns:2]],
			)
		);
		win.front;
	}

	writeSynthDef {
		SynthDef(\DopplerSF, { | buf, pos=0.5, width=0.2, distance=10000, speed=5, direction=1, out=0 |
			var signal = PlayBuf.ar(1, buf, doneAction: 2);
			var buf_dur = BufDur.ir(buf);
			var cross_dur = (distance / speed).clip(0.0001, buf_dur);
			var doppler_env = EnvGen.ar(Env.new([distance, distance.neg], [(buf_dur * pos) - (width / 2), width, (buf_dur * (1.0 - pos)) - (width / 2)]));
			var radial_distance = (doppler_env.squared + speed.squared).sqrt;
			var doppler_shift = DelayC.ar(signal, 1.0, radial_distance / 350.0);
			var amp = (radial_distance.max(1.0)).reciprocal;
			var result = Pan2.ar(doppler_shift * amp, doppler_env * direction / distance.abs);
			Out.ar(out, result);
		}).writeDefFile;
	}
}
