FRDRain {
	var win, hpnoise, white, brown, pink, gray, dust, drops;
	var dustDensity=100, dropDensity=100, dropDecay=0.2, dropFFreq=100, dropFRq=1, bpWet=0.5, bpFreq=5000,
	bpRq=1, rWet=0.5, rateRedux=0.5, bits=16;
	var synth = nil;
	var hpnoise_s, white_s, brown_s, gray_s, pink_s, dust_s, drops_s, dropDensityDecay_s2, dropFreqRq_s2, bpFreqRq_s2, dropFFreq_s,
	dropFRq_s, bpFreq_s, bpRq_s, bpWet_s, rWet_s, rateRedux_s, bits_s, start_b;

	*new {
		^super.new.init();
	}

	init {


		SynthDef(\Ambience, { | hpnoise=0, white=0, pink=0, brown=0, gray=0, dust=0, dustDensity=100,
			drops=0, dropDensity=10, dropDecay=0.3, dropFFreq=100, dropFRq=1, bpWet=0.5, bpFreq=500,
			bpRq=1, rWet=0.5, rateRedux=0.5, bits=16, fade_in=15, fade_out=5, gate=1 |

			var noise, src;
			var hpNFreq = 1000;
			var bpF;
			var redux;
			var aenv = EnvGen.ar(Env.adsr(fade_in, releaseTime: fade_out, sustainLevel: 1, curve: [2, 0, -2]), gate);

			hpnoise = {HPF.ar(PinkNoise.ar(hpnoise.dbamp), hpNFreq)}!2;
			white   = {WhiteNoise.ar(white.dbamp)}!2;
			pink    = {PinkNoise.ar(pink.dbamp)}!2;
			brown   = {BrownNoise.ar(brown.dbamp)}!2;
			gray    = {GrayNoise.ar(gray.dbamp)}!2;
			dust    = {Dust2.ar(dustDensity, dust.dbamp)}!2;

			src = Mix([white, pink, brown, gray, dust, hpnoise]);

			drops = {Decay.ar(Dust.ar(dropDensity, drops.dbamp), dropDecay) * PinkNoise.ar}!2;

			src = Mix([src, drops]);

			bpF = BPF.ar(src, bpFreq, bpRq);
			src = SelectX.ar(bpWet, [src, bpF]);

			src = VBJonVerb.ar(src);
			src = src + CombC.ar(src, 2, 2, 6, 0.12);
			src = src + CombC.ar(src, 3, 3, 16, 0.07);
			src = src * aenv;

			redux = Latch.ar(src, Impulse.ar(SampleRate.ir * (rateRedux/2)));
			redux = redux.round(0.5 ** bits);
			Out.ar(0, SelectX.ar(1 + rWet, [src - redux, src, redux]));
		}).add;

	}
	showGUI {
		win = Window.new("Generatore di perturbazione").front;

		hpnoise= -12;
		white= -36;
		brown= -18;
		pink= -12;
		gray= -18;
		dust= -24;
		drops= -18;

		hpnoise_s = Slider().orientation_(\vertical).action_({ | val |
			hpnoise = Map(val.value, 0.0, 1.0, -48, 0);
			if(synth != nil, {
				synth.set(\hpnoise, hpnoise);
			})
		}).value_(Map(hpnoise, -48, 0, 0.0, 1.0));
		white_s = Slider().orientation_(\vertical).action_({ | val |
			white = Map(val.value, 0.0, 1.0, -48, 0);
			if(synth != nil, {
				synth.set(\white, white);
			})
		}).value_(Map(white, -48, 0, 0.0, 1.0));
		brown_s = Slider().orientation_(\vertical).action_({ | val |
			brown = Map(val.value, 0.0, 1.0, -48, 0);
			if(synth != nil, {
				synth.set(\brown, brown);
			})
		}).value_(Map(brown, -48, 0, 0.0, 1.0));
		gray_s = Slider().orientation_(\vertical).action_({ | val |
			gray = Map(val.value, 0.0, 1.0, -48, 0);
			if(synth != nil, {
				synth.set(\gray, gray);
			})
		}).value_(Map(gray, -48, 0, 0.0, 1.0));
		pink_s = Slider().orientation_(\vertical).action_({ | val |
			pink = Map(val.value, 0.0, 1.0, -48, 0);
			if(synth != nil, {
				synth.set(\pink, pink);
			})
		}).value_(Map(pink, -48, 0, 0.0, 1.0));
		dust_s = Slider().orientation_(\vertical).action_({ | val |
			dust = Map(val.value, 0.0, 1.0, -48, 0);
			if(synth != nil, {
				synth.set(\dust, dust);
			})
		}).value_(Map(dust, -48, 0, 0.0, 1.0));
		drops_s = Slider().orientation_(\vertical).action_({ | val |
			drops = Map(val.value, 0.0, 1.0, -48, 0);
			if(synth != nil, {
				synth.set(\drops, drops);
			})
		}).value_(Map(drops, -48, 0, 0.0, 1.0));

		dropDensityDecay_s2 = Slider2D().action_({ | val |
			dropDensity = Map(val.x, 0.0, 1.0, 1, Server.default.sampleRate/2);
			dropDecay = Map(val.x, 0.0, 1.0, 0.001, 1);
		}).setXY(Map(dropDensity, 1, Server.default.sampleRate/2, 0.0, 1.0), Map(dropDecay, 0.001, 1, 0.0, 1.0));

		dropFreqRq_s2 = Slider2D().action_({ | val |
			dropFFreq = Map(val.x, 0.0, 1.0, 300, 9000);
			dropFRq = Map(val.x, 0.0, 1.0, 0.0, 2.0);
		}).setXY(Map(dropFFreq, 300, 9000, 0.0, 1.0), Map(dropFRq, 0.0, 2.0, 0.0, 1.0));

		bpFreqRq_s2 = Slider2D().action_({ | val |
			bpFreq = Map(val.x, 0.0, 1.0, 300, 9000);
			bpRq = Map(val.x, 0.0, 1.0, 0.0, 2.0);
		}).setXY(Map(bpFreq, 300, 9000, 0.0, 1.0), Map(bpRq, 0.0, 2.0, 0.0, 1.0));

		bpWet_s = Slider().action_({ | val |
			bpWet = val.value;
			if(synth != nil, {
				synth.set(\bpWet, bpWet);
			})
		}).value_(bpWet);
		rWet_s = Slider().action_({ | val |
			rWet = Map(val.value, 0.0, 1.0, -1, 1);
			if(synth != nil, {
				synth.set(\rWet, rWet);
			})
		}).value_(Map(rWet, -1, 1, 0.0, 1.0));
		rateRedux_s = Slider().action_({ | val |
			rateRedux = val.value;
			if(synth != nil, {
				synth.set(\rateRedux, rateRedux);
			})
		}).value_(rateRedux);
		bits_s = Slider().action_({ | val |
			bits = Map(val.value, 0.0, 1.0, 1, 32);
			if(synth != nil, {
				synth.set(\bits, bits);
			})
		}).value_(Map(bits, 1, 32, 0.0, 1.0));

		start_b = Button().states_([["Play"], ["Stop"]]).action_({ | val |
			if(val.value == 1, {
				synth = Synth(\Ambience, [\hpnoise, hpnoise, \white, white, \pink, pink, \brown, brown, \gray, gray, \dust, dust, \dustDensity, dustDensity,
					\drops, drops, \dropDensity, dropDensity, \dropDecay, dropDecay, \dropFFreq, dropFFreq, \dropFRq, dropFRq, \bpWet, bpWet, \bpFreq, bpFreq,
					\bpRq, bpRq, \rWet, rWet, \rateRedux, rateRedux, \bits, bits]);
			}, {
				synth.release;
				synth = nil;
			});
		});

		win.layout_(
			GridLayout.columns(
				[StaticText().string_("HP Noise").align_(\center), hpnoise_s, [start_b, columns:14]],
				[StaticText().string_("White").align_(\center), white_s],
				[StaticText().string_("Pink").align_(\center), pink_s],
				[StaticText().string_("Gray").align_(\center), gray_s],
				[StaticText().string_("Brown").align_(\center), brown_s],
				[StaticText().string_("Dust").align_(\center), dust_s],
				[StaticText().string_("Drops").align_(\center), drops_s],
				[StaticText().string_("Drops: (x=density, y=decay)").align_(\center), dropDensityDecay_s2],
				[StaticText().string_("Drops: (x=freq, y=1/Q)").align_(\center), dropFreqRq_s2],
				[StaticText().string_("BandPass: (x=freq, y=1/Q)").align_(\center), bpFreqRq_s2],
				[StaticText().string_("BP Wet").align_(\center), bpWet_s],
				[StaticText().string_("Bit Crush rate").align_(\center), rateRedux_s],
				[StaticText().string_("Bit Crush bits").align_(\center), bits_s],
				[StaticText().string_("Bit Crush wet").align_(\center), rWet_s],
			)
		);
	}


	writeSynthDef {
		SynthDef(\Ambience, { | hpnoise=0, white=0, pink=0, brown=0, gray=0, dust=0, dustDensity=100,
			drops=0, dropDensity=10, dropDecay=0.3, dropFFreq=100, dropFRq=1, bpWet=0.5, bpFreq=500,
			bpRq=1, rWet=0.5, rateRedux=0.5, bits=16, fade_in=15, fade_out=5, gate=1 |

			var noise, src;
			var hpNFreq = 1000;
			var bpF;
			var redux;
			var aenv = EnvGen.ar(Env.adsr(fade_in, releaseTime: fade_out, sustainLevel: 1, curve: [2, 0, -2]), gate);

			hpnoise = {HPF.ar(PinkNoise.ar(hpnoise.dbamp), hpNFreq)}!2;
			white   = {WhiteNoise.ar(white.dbamp)}!2;
			pink    = {PinkNoise.ar(pink.dbamp)}!2;
			brown   = {BrownNoise.ar(brown.dbamp)}!2;
			gray    = {GrayNoise.ar(gray.dbamp)}!2;
			dust    = {Dust2.ar(dustDensity, dust.dbamp)}!2;

			src = Mix([white, pink, brown, gray, dust, hpnoise]);

			drops = {Decay.ar(Dust.ar(dropDensity, drops.dbamp), dropDecay) * PinkNoise.ar}!2;

			src = Mix([src, drops]);

			bpF = BPF.ar(src, bpFreq, bpRq);
			src = SelectX.ar(bpWet, [src, bpF]);

			src = VBJonVerb.ar(src);
			src = src + CombC.ar(src, 2, 2, 6, 0.12);
			src = src + CombC.ar(src, 3, 3, 16, 0.07);
			src = src * aenv;

			redux = Latch.ar(src, Impulse.ar(SampleRate.ir * (rateRedux/2)));
			redux = redux.round(0.5 ** bits);
			Out.ar(0, SelectX.ar(1 + rWet, [src - redux, src, redux]));
		}).writeDefFile;
	}



}