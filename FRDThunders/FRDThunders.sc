FRDThunders {
	var synth, reverb, bus, routine, win, play_b, hpf_s, rate_s, amp_s;
	var hpf, rate, amp;

	*new {
		^super.new.init();
	}

	init {
		hpf = 2500;
		rate = 0.75;
		amp = 0.8;
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
		SynthDef(\ThunderRev, { | amp=0.1, in=20, out=0 |
			var sig = In.ar(in, 2);
			sig = GVerb.ar(sig * 0.75, 10, 0.13);
			sig =  GVerb.ar(sig, 370, 15, 0.2, 0.7, 75, 0.50, maxroomsize: 1000, mul: 0.5) + GVerb.ar(sig, 270, 10, 0.7, 1, 50, 0.50, maxroomsize: 1000, mul: 0.5);
			//sig = GVerb.ar(sig, 470, 3, 0.9, 0.2, 100, 0.30, maxroomsize: 1000, mul: 0.8);

			Out.ar(out, sig);
		}).add;
	}

	showGUI {
		win = Window.new("Thunder generator").front;
		play_b  = Button().states_([["Play"], ["Stop"]]).action_({ | val |
			if(val.value == 1, {
				bus = Bus.audio(Server.default, 2);
				reverb = Synth.tail(Server.default, \ThunderRev, [\in, bus, \out, 0]);
				routine = Routine{
					loop{
						Synth(\ThunderSpike, [\amp, rrand(amp / 4, amp), \out, bus]);
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
		amp_s = Slider().action_({ | val |
			amp = val.value;
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
		SynthDef(\ThunderRev, { | amp=0.1, in=20, out=0 |
			var sig = In.ar(in, 2);
			sig = GVerb.ar(sig * 0.75, 10, 0.13);
			sig =  GVerb.ar(sig, 370, 15, 0.2, 0.7, 75, 0.50, maxroomsize: 1000, mul: 0.5) + GVerb.ar(sig, 270, 10, 0.7, 1, 50, 0.50, maxroomsize: 1000, mul: 0.5);
			//sig = GVerb.ar(sig, 470, 3, 0.9, 0.2, 100, 0.30, maxroomsize: 1000, mul: 0.8);

			Out.ar(out, sig);
		}).writeDefFile;
	}
}

