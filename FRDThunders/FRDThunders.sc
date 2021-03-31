FRDThunders {
	var synth, reverb, rbus, bus, routine, win, play_b, hpf_s, rate_s, amp_s, r1_s, r2_s, r3_s, t1_s, t2_s, t3_s, dry1_s, dry2_s, dry3_s;
	var hpf, rate, amp, r1, r2, r3, t1, t2, t3, dry1, dry2, dry3;

	*new {
		^super.new.init();
	}

	init {
		hpf = 2500;
		rate = 0.12;
		amp = 0.8;
		r1 = 270;
		r2 = 170;
		r3 = 290;
		t1 = 15;
		t2 = 10;
		t3 = 20;
		dry1=0.01;
		dry2=0.02;
		dry3=0.1;
		SynthDef(\ThunderSpike, { | amp=0.1, out=0, hpf=900 |
			var sig = PinkNoise.ar(1) * LFNoise1.ar(250);
			var env = EnvGen.ar(Env.perc(0.1 * (1.0 - amp), Rand(0.1, 0.5)));
			sig = HPF.ar(sig, 20);

			sig = sig + Pan2.ar(CombC.ar(sig, 1.5, ExpRand(0.014, 1.0), Rand(0.2, 1.7), Rand(0.1, 0.25)), Rand(-0.5, 0.5));
			sig = sig + Pan2.ar(CombC.ar(sig, 1.5, ExpRand(0.014, 1.0), Rand(0.2, 1.7), Rand(0.1, 0.25)), Rand(-0.5, 0.5)) + Pan2.ar(CombC.ar(sig, 1.5, ExpRand(0.014, 1.0), Rand(0.2, 1.7), Rand(0.1, 0.25)), Rand(-0.5, 0.5));
			sig = sig / 3;
			sig = (sig * 8).tanh;
			sig = sig - (sig * 1).cubed;
			sig = LPF.ar(sig, (LFNoise1.ar(1).exprange(Rand(50, 500),  Rand(501, hpf)) * EnvGen.ar(Env.new([1, 0.025], [0.15]))).clip(100, 19000));
			sig = Limiter.ar(sig);
			sig = Pan2.ar(sig, Rand(-0.2, 0.2));
			Out.ar(out, sig * env * amp);
			DetectSilence.ar(CombC.ar(sig * env * amp, 0.2, 0.1, 1), doneAction: 2);
		}).add;
		SynthDef(\ThunderRev, { | amp=0.1, r1=130, r2=320, r3=240, t1=0.075, t2=0.010, t3=0.03, dry1=0.8, dry2=0.6, dry3=0.5, in=20, out=0, gate=1 | // r1=730, r2=620, r3=840
			var sig = In.ar(in, 2);
			var env = EnvGen.ar(Env.adsr(0.001, 0, 1, 3), gate: gate, doneAction: 2);
			r1 = Lag2.ar(K2A.ar(r1), 0.3);
			r2 = Lag2.ar(K2A.ar(r2), 0.3);
			r3 = Lag2.ar(K2A.ar(r3), 0.3);
			t1 = Lag2.ar(K2A.ar(t1), 0.3);
			t2 = Lag2.ar(K2A.ar(t2), 0.3);
			t3 = Lag2.ar(K2A.ar(t3), 0.3);
			dry1 = Lag2.ar(K2A.ar(dry1), 0.3);
			dry2 = Lag2.ar(K2A.ar(dry2), 0.3);
			dry3 = Lag2.ar(K2A.ar(dry3), 0.3);
			//sig = GVerb.ar(sig * 0.75, 10, 0.13);
			sig =  GVerb.ar(sig, r1, t1, 0.82, 0.17, 75, dry1, maxroomsize: 1000, mul: 0.5) + GVerb.ar(sig, r2, t2, 0.7, 0.1, 50, dry2, maxroomsize: 1000, mul: 0.5);
			sig = GVerb.ar(sig, r3, t3, 0.9, 0.2, 100, dry3, maxroomsize: 1000, mul: 0.8);

			Out.ar(out, sig * env);
		}).add;
		SynthDef(\ThunderRevLast, { | amp=0.1, r1=330, t1=10.75, dry1=0.0, in=20, out=0 |
			var sig = In.ar(in, 2);
			r1 = Lag2.ar(K2A.ar(r1), 0.3);
			t1 = Lag2.ar(K2A.ar(t1), 0.3);
			dry1 = Lag2.ar(K2A.ar(dry1), 0.3);
			sig =  (sig * 0.5) + LPF.ar(GVerb.ar(sig, r1, t1, 0.96, 0.1, 75, dry1, maxroomsize: 1000, mul: 0.5), 500);
			sig = Limiter.ar(sig, 0.75 * amp);
			Out.ar(out, sig);
		}).add;
	}

	showGUI {
		win = Window.new("Thunder generator").onClose_({
			reverb.free;
			bus.free;
			routine.stop;
		}).front;
		play_b  = Button().states_([["Play"], ["Stop"]]).action_({ | val |
			if(val.value == 1, {
				rbus = Bus.audio(Server.default, 2);
				reverb = Synth.tail(Server.default, \ThunderRevLast, [\in, rbus, \out, 0]);
				routine = Routine{
					loop{
						var amp = rrand(0.25, 0.75);
						var bus = Bus.audio(Server.default, 2);
						var trev = Synth.before(reverb, \ThunderRev, [\in, bus, \out, rbus]);
						(24..48).choose.do({ | id |
							Synth.head(Server.default, \ThunderSpike, [\amp,amp + rrand(amp.neg / 2, amp / 2), \out, bus, \hpf, hpf]);
							exprand(0.01, 0.1).wait;
						});
						trev.release(2);
						Routine{2.wait; bus.free;}.play(AppClock);

						rrand(rate.reciprocal / 2, rate.reciprocal).wait;
					}
				}.play;
				//synth = Synth(\Thunders, [\hpf, hpf, \rate, rate, \fade_in, 5, \fade_out, 5, \gate, 1]);
			}, {
				routine.stop;
				reverb.free;
				rbus.free;
				reverb = nil;
			});
		});
		hpf_s = Slider().action_({ | val |
			hpf = Map(val.value, 0.0, 1.0, 502, 15000);
			if(synth != nil, {
				synth.set(\hpf, hpf);
			});
		}).value_(Map(hpf, 502, 15000, 0.0, 1.0));
		rate_s = Slider().action_({ | val |
			rate = Map(val.value, 0.0, 1.0, 0.01, 0.3);
			if(synth != nil, {
				synth.set(\rate, rate);
			});
		}).value_(Map(rate, 0.01, 0.3, 0.0, 1.0));
		amp_s = Slider().action_({ | val |
			amp = val.value;
			if(reverb != nil, {
				reverb.set(\amp, amp);
			});
			if(synth != nil, {
				synth.set(\amp, amp);
			});
		}).value_(amp);
		win.layout_(
			GridLayout.columns(
				[StaticText().string_("HPF"), hpf_s, [play_b, columns:3]],
				[StaticText().string_("Rate"), rate_s],
				[StaticText().string_("Amp"), amp_s],
			)
		)
	}


	writeSynthDef {
		SynthDef(\ThunderSpike, { | amp=0.1, out=0, hpf=900 |
			var sig = PinkNoise.ar(1) * LFNoise1.ar(250);
			var env = EnvGen.ar(Env.perc(0.1 * (1.0 - amp), Rand(0.1, 0.5)));
			sig = HPF.ar(sig, 20);

			sig = sig + Pan2.ar(CombC.ar(sig, 1.5, ExpRand(0.014, 1.0), Rand(0.2, 1.7), Rand(0.1, 0.25)), Rand(-0.5, 0.5));
			sig = sig + Pan2.ar(CombC.ar(sig, 1.5, ExpRand(0.014, 1.0), Rand(0.2, 1.7), Rand(0.1, 0.25)), Rand(-0.5, 0.5)) + Pan2.ar(CombC.ar(sig, 1.5, ExpRand(0.014, 1.0), Rand(0.2, 1.7), Rand(0.1, 0.25)), Rand(-0.5, 0.5));
			sig = sig / 3;
			sig = (sig * 8).tanh;
			sig = sig - (sig * 1).cubed;
			sig = LPF.ar(sig, (LFNoise1.ar(1).exprange(Rand(50, 500),  Rand(501, hpf)) * EnvGen.ar(Env.new([1, 0.025], [0.15]))).clip(100, 19000));
			sig = Limiter.ar(sig);
			sig = Pan2.ar(sig, Rand(-0.2, 0.2));
			Out.ar(out, sig * env * amp);
			DetectSilence.ar(CombC.ar(sig * env * amp, 0.2, 0.1, 1), doneAction: 2);
		}).writeDefFile;
		SynthDef(\ThunderRev, { | amp=0.1, r1=130, r2=320, r3=240, t1=0.075, t2=0.010, t3=0.03, dry1=0.8, dry2=0.6, dry3=0.5, in=20, out=0, gate=1 | // r1=730, r2=620, r3=840
			var sig = In.ar(in, 2);
			var env = EnvGen.ar(Env.adsr(0.001, 0, 1, 3), gate: gate, doneAction: 2);
			r1 = Lag2.ar(K2A.ar(r1), 0.3);
			r2 = Lag2.ar(K2A.ar(r2), 0.3);
			r3 = Lag2.ar(K2A.ar(r3), 0.3);
			t1 = Lag2.ar(K2A.ar(t1), 0.3);
			t2 = Lag2.ar(K2A.ar(t2), 0.3);
			t3 = Lag2.ar(K2A.ar(t3), 0.3);
			dry1 = Lag2.ar(K2A.ar(dry1), 0.3);
			dry2 = Lag2.ar(K2A.ar(dry2), 0.3);
			dry3 = Lag2.ar(K2A.ar(dry3), 0.3);
			//sig = GVerb.ar(sig * 0.75, 10, 0.13);
			sig =  GVerb.ar(sig, r1, t1, 0.82, 0.17, 75, dry1, maxroomsize: 1000, mul: 0.5) + GVerb.ar(sig, r2, t2, 0.7, 0.1, 50, dry2, maxroomsize: 1000, mul: 0.5);
			sig = GVerb.ar(sig, r3, t3, 0.9, 0.2, 100, dry3, maxroomsize: 1000, mul: 0.8);

			Out.ar(out, sig * env);
		}).writeDefFile;
		SynthDef(\ThunderRevLast, { | amp=0.1, r1=330, t1=10.75, dry1=0.0, in=20, out=0 |
			var sig = In.ar(in, 2);
			r1 = Lag2.ar(K2A.ar(r1), 0.3);
			t1 = Lag2.ar(K2A.ar(t1), 0.3);
			dry1 = Lag2.ar(K2A.ar(dry1), 0.3);
			//sig =  (sig * 0.5) + LPF.ar(GVerb.ar(sig, r1, t1, 0.96, 0.1, 75, dry1, maxroomsize: 1000, mul: 0.5), 500);
			sig = (sig) + LPF.ar(CombC.ar(sig, 2, [0.12, 0.22, 0.31, 0.443, 0.6787, 0.731, 0.81, 0.996], 6).sum / 8, 1000);
			sig = Limiter.ar(sig, 0.75 * amp);
			Out.ar(out, sig);
		}).writeDefFile;
	}
}

