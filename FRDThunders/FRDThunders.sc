FRDThunders {
	var synth, reverb, bus, routine, win, play_b, hpf_s, rate_s, amp_s, r1_s, r2_s, r3_s, t1_s, t2_s, t3_s, dry1_s, dry2_s, dry3_s;
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
		SynthDef(\Thunders, { | hpf=2500, rate=1, fade_in=5, fade_out=5, gate=1, amp=1 |
			var aenv = EnvGen.ar(Env.adsr(fade_in, releaseTime: fade_out, sustainLevel: 1, curve: [2, 0, -2]), gate);
			var sig = LPF.ar(
				10 * HPF.ar(PinkNoise.ar(LFNoise1.kr(3 * rate).clip(0,1)*LFNoise1.kr(2 * rate).clip(0,1) ** 1.8), 20)
				,LFNoise1.kr(1 * rate).exprange(100,hpf)
			).tanh;
			//sig = sig + CombC.ar(sig, 1, 0.8, 1, Lag2.ar(LFNoise1.ar(1, 1), 0.1));
			sig = Limiter.ar(
				(
					GVerb.ar(
						sig,
						//0.92, 0.1, 1, 0.9, 0.5
						270,30,0.9,drylevel:0.8, maxroomsize: 300
					) * Line.kr(0,0.7,30)
				)
			);
			//sig = sig - GVerb.ar(sig, 800, 5, 0.1, 1, 15, 0.2, maxroomsize: 1000, mul: 0.05);
			Out.ar(0, sig * aenv * amp);
		}).add;
		SynthDef(\ThunderSpike, { | amp=0.1, out=0 |
			var sig = PinkNoise.ar(1) * LFNoise1.ar(250);
			var env = EnvGen.ar(Env.perc(0.1 * (1.0 - amp), Rand(0.1, 0.5)));
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
		}).add;
		SynthDef(\ThunderRev, { | amp=0.1, r1=370, r2=270, r3=470, t1=15, t2=10, t3=3, dry1=0.1, dry2=0.2, dry3=0.01, in=20, out=0 |
			var sig = In.ar(in, 2);
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
			sig =  GVerb.ar(sig, r1, t1, 0.2, 0.7, 75, dry1, maxroomsize: 1000, mul: 0.5) + GVerb.ar(sig, r2, t2, 0.7, 1, 50, dry2, maxroomsize: 1000, mul: 0.5);
			sig = GVerb.ar(sig, r3, t3, 0.9, 0.2, 100, dry3, maxroomsize: 1000, mul: 0.8);

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
				bus = Bus.audio(Server.default, 2);
				reverb = Synth.tail(Server.default, \ThunderRev, [\in, bus, \out, 0]);
				routine = Routine{
					loop{
						var amp = rrand(0.25, 0.75);
						(3..10).choose.do({ | id |
							Synth.head(Server.default, \ThunderSpike, [\amp,amp + rrand(amp.neg / 2, amp / 2), \out, bus]);
							rrand(0.05, 0.1).wait;
						});
						//Synth.head(Server.default, \ThunderSpike, [\amp, rrand(0.25, 0.75), \out, q]);
						rrand(rate.reciprocal / 2, rate.reciprocal).wait;
					}
				}.play;
				//synth = Synth(\Thunders, [\hpf, hpf, \rate, rate, \fade_in, 5, \fade_out, 5, \gate, 1]);
			}, {
				routine.stop;
				reverb.free;
				bus.free;
			});
		});
		hpf_s = Slider().action_({ | val |
			hpf = Map(val.value, 0.0, 1.0, 100, 5000);
			if(synth != nil, {
				synth.set(\hpf, hpf);
			});
		}).value_(Map(hpf, 100, 5000, 0.0, 1.0));
		rate_s = Slider().action_({ | val |
			rate = Map(val.value, 0.0, 1.0, 0.01, 0.3);
			if(synth != nil, {
				synth.set(\rate, rate);
			});
		}).value_(Map(rate, 0.01, 0.3, 0.0, 1.0));

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
			t3 = Map(val.value, 0.0, 1.0, 0.1, 300);
			reverb.set(\t3, t3);
		}).value_(Map(t3, 0.1, 300, 0.0, 1.0));

		dry1_s =  Slider().action_({ | val |
			dry1 = val.value;
			reverb.set(\dry1, dry1);
		}).value_(dry1);
		dry2_s =  Slider().action_({ | val |
			dry2 = val.value;
			reverb.set(\dry2, dry2);
		}).value_(dry2);
		dry3_s =  Slider().action_({ | val |
			dry3 = val.value;
			reverb.set(\dry3, dry3);
		}).value_(dry3);


		amp_s = Slider().action_({ | val |
			amp = val.value;
			if(synth != nil, {
				synth.set(\amp, amp);
			});
		}).value_(amp);
		win.layout_(
			GridLayout.columns(
				[StaticText().string_("HPF"), hpf_s, [play_b, columns:12]],
				[StaticText().string_("Rate"), rate_s],
				[StaticText().string_("Rev1 Room"), r1_s],
				[StaticText().string_("Rev1 Time"), t1_s],
				[StaticText().string_("Rev1 Dry"), dry1_s],
				[StaticText().string_("Rev2 Room"), r2_s],
				[StaticText().string_("Rev2 Time"), t2_s],
				[StaticText().string_("Rev2 Dry"), dry2_s],
				[StaticText().string_("Rev3 Room"), r3_s],
				[StaticText().string_("Rev3 Time"), t3_s],
				[StaticText().string_("Rev3 Dry"), dry3_s],
				[StaticText().string_("Amp"), amp_s],
			)
		)
	}


	writeSynthDef {
		SynthDef(\Thunders, { | hpf=2500, rate=1, fade_in=5, fade_out=5, gate=1, amp=1 |
			var aenv = EnvGen.ar(Env.adsr(fade_in, releaseTime: fade_out, sustainLevel: 1, curve: [2, 0, -2]), gate);
			var sig = LPF.ar(
				10 * HPF.ar(PinkNoise.ar(LFNoise1.kr(3 * rate).clip(0,1)*LFNoise1.kr(2 * rate).clip(0,1) ** 1.8), 20)
				,LFNoise1.kr(1 * rate).exprange(100,hpf)
			).tanh;
			//sig = sig + CombC.ar(sig, 1, 0.8, 1, Lag2.ar(LFNoise1.ar(1, 1), 0.1));
			sig = Limiter.ar(
				(
					GVerb.ar(
						sig,
						//0.92, 0.1, 1, 0.9, 0.5
						270,30,0.9,drylevel:0.8, maxroomsize: 300
					) * Line.kr(0,0.7,30)
				)
			);
			//sig = sig - GVerb.ar(sig, 800, 5, 0.1, 1, 15, 0.2, maxroomsize: 1000, mul: 0.05);
			Out.ar(0, sig * aenv * amp);
		}).writeDefFile;
		SynthDef(\ThunderSpike, { | amp=0.1, out=0 |
			var sig = PinkNoise.ar(1) * LFNoise1.ar(250);
			var env = EnvGen.ar(Env.perc(0.1 * (1.0 - amp), Rand(0.1, 0.5)));
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
		}).writeDefFile;
		SynthDef(\ThunderRev, { | amp=0.1, r1=370, r2=270, r3=470, t1=15, t2=10, t3=3, dry1=0.1, dry2=0.2, dry3=0.01, in=20, out=0 |
			var sig = In.ar(in, 2);
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
			sig =  GVerb.ar(sig, r1, t1, 0.2, 0.7, 75, dry1, maxroomsize: 1000, mul: 0.5) + GVerb.ar(sig, r2, t2, 0.7, 1, 50, dry2, maxroomsize: 1000, mul: 0.5);
			sig = GVerb.ar(sig, r3, t3, 0.9, 0.2, 100, dry3, maxroomsize: 1000, mul: 0.8);

			Out.ar(out, sig);
		}).writeDefFile;
	}
}

