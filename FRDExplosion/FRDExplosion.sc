FRDExplosion {
	var synth, reverb, bus, routine, win, play_b, hpf_s, amp_s, r1_s, r2_s, r3_s, t1_s, t2_s, t3_s;
	var hpf, rate, amp, r1, r2, r3, t1, t2, t3;

	*new {
		^super.new.init();
	}

	init {

		hpf = 2500;
		rate = 0.75;
		amp = 0.8;
		r1 = 70;
		r2 = 10;
		r3 = 90;
		t1 = 15;
		t2 = 10;
		t3 = 20;

		bus = Bus.audio(Server.default, 2);
		reverb = Synth.tail(Server.default, \ExplosionRev, [\in, bus, \out, 0, \r1, r1, \r2, r2, \r3, r3, \t1, t1, \t2, t2, \t3, t3]);

	}

	play {
		Synth(\ExplosionSpike, [\amp, rrand(amp / 4, amp), \out, bus]);
	}

	showGUI {

		win = Window.new("Explosion generator").onClose_({
			reverb.free;
			bus.free;
		}).front;
		play_b  = Button().states_([["Play"]]).action_({ | val |
			Synth(\ExplosionSpike, [\amp, rrand(amp / 4, amp), \out, bus]);
		});
		hpf_s = Slider().action_({ | val |
			hpf = Map(val.value, 0.0, 1.0, 100, 5000);
			if(synth != nil, {
				synth.set(\hpf, hpf);
			});
		}).value_(Map(hpf, 100, 5000, 0.0, 1.0));

		r1_s =  Slider().action_({ | val |
			r1 = Map(val.value, 0.0, 1.0, 5, 360);
			r1.postln;
			reverb.set(\r1, r1);
		}).value_(Map(r1, 5, 360, 0.0, 1.0));
		r2_s =  Slider().action_({ | val |
			r2 = Map(val.value, 0.0, 1.0, 5, 300);
			reverb.set(\r2, r2);
		}).value_(Map(r2, 5, 300, 0.0, 1.0));
		r3_s =  Slider().action_({ | val |
			r3 = Map(val.value, 0.0, 1.0, 5, 300);
			reverb.set(\r3, r3);
		}).value_(Map(r3, 5, 300, 0.0, 1.0));

		t1_s =  Slider().action_({ | val |
			t1 = Map(val.value, 0.0, 1.0, 0.1, 30);
			reverb.set(\t1, t1);
		}).value_(Map(t1, 0.1, 30, 0.0, 1.0));
		t2_s =  Slider().action_({ | val |
			t2 = Map(val.value, 0.0, 1.0, 0.1, 30);
			reverb.set(\t2, t2);
		}).value_(Map(t2, 0.1, 30, 0.0, 1.0));
		t3_s =  Slider().action_({ | val |
			t3 = Map(val.value, 0.0, 1.0, 0.1, 30);
			reverb.set(\t3, t3);
		}).value_(Map(t3, 0.1, 300, 0.0, 1.0));

		amp_s = Slider().action_({ | val |
			amp = val.value;
			if(synth != nil, {
				synth.set(\amp, amp);
			});
		}).value_(amp);
		win.layout_(
			GridLayout.columns(
				[StaticText().string_("Rev1 Time"), t1_s, [play_b, columns:4]],
				[StaticText().string_("Rev2 Time"), t2_s],
				[StaticText().string_("Rev3 Time"), t3_s],
				[StaticText().string_("Amp"), amp_s],
			)
		)
	}


	writeSynthDef {
		SynthDef(\ExplosionSpike, { | amp=0.1, out=0 |
			var sig = PinkNoise.ar(1) * LFNoise1.ar(250);
			var env = EnvGen.ar(Env.perc(0.001, Rand(0.35, 0.5)));
			sig = HPF.ar(sig, 20);

			sig = sig + Pan2.ar(CombC.ar(sig, 1.5, ExpRand(0.014, 1.0), Rand(0.2, 1.7), Rand(0.1, 0.25)), Rand(-0.5, 0.5));
			sig = sig + Pan2.ar(CombC.ar(sig, 1.5, ExpRand(0.014, 1.0), Rand(0.2, 1.7), Rand(0.1, 0.25)), Rand(-0.5, 0.5)) + Pan2.ar(CombC.ar(sig, 1.5, ExpRand(0.014, 1.0), Rand(0.2, 1.7), Rand(0.1, 0.25)), Rand(-0.5, 0.5));
			sig = sig / 3;
			sig = (sig * 8).tanh;
			sig = sig - (sig * 1).cubed;
			sig = LPF.ar(sig, (LFNoise1.ar(1).exprange(100, 5000) * EnvGen.ar(Env.new([1, 0.025], [0.15]))).clip(100, 19000));
			sig = Limiter.ar(sig);
			sig = Pan2.ar(sig, Rand(-0.2, 0.2));
			Out.ar(out, sig * env * amp);
			DetectSilence.ar(CombC.ar(sig, 0.2, 0.1, 1), doneAction: 2);
		}).writeDefFile.add;
		SynthDef(\ExplosionRev, { | amp=0.1, r1=370, r2=270, r3=470, t1=15, t2=10, t3=3, in=20, out=0 |
			var sig = In.ar(in, 2);
			r1 = Lag2.ar(K2A.ar(r1), 1);
			r2 = Lag2.ar(K2A.ar(r2), 1);
			r3 = Lag2.ar(K2A.ar(r3), 1);
			t1 = Lag2.ar(K2A.ar(t1), 1);
			t2 = Lag2.ar(K2A.ar(t2), 1);
			t3 = Lag2.ar(K2A.ar(t3), 1);
			sig = GVerb.ar(sig * 0.75, 10, 0.13);
			sig =  GVerb.ar(sig, r1, t1, 0.2, 0.7, 75, 0.50, maxroomsize: 800, mul: 0.5) + GVerb.ar(sig, r2, t2, 0.7, 0.9, 50, 0.50, maxroomsize: 800, mul: 0.5);
			sig = GVerb.ar(sig, r3, t3, 0.9, 0.2, 100, 0.30, maxroomsize: 800, mul: 0.8);
			sig = LeakDC.ar(Limiter.ar(sig));
			Out.ar(out, sig);
		}).writeDefFile.add;
	}
}

