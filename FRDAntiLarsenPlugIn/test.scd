(
SynthDef( \FRDAntiLarsen, { | inCh=20, outCh=0, multiplier=10, coeff=0.8 |
	var input, chain, outliers, size=128;
	var mean, max;
	input = InFeedback.ar(inCh, 1);
	chain = FFT(LocalBuf(size), input, wintype: 1);
	outliers = PV_Copy(chain, LocalBuf(size));
	chain = chain.pvcalc(size, { | mags, phases |
		mean = mags.sum / mags.size;
		mean = 0;
		mags.do({| mag |
			mean = mean + mag;
		});

		[mags, phases]
	});
	Poll.ar(Impulse.ar(1), mean, \sum);
	outliers = PV_MagAbove(outliers, multiplier * mean);
	chain = chain.pvcalc2(outliers, size, { | magnitudes1, phases1, magnitudes2, phases2 |
		magnitudes1 = magnitudes1 - (magnitudes2 * coeff);
		[magnitudes1, phases1]
	});
	chain = IFFT(chain);
	Out.ar( outCh, chain );
} ).writeDefFile.add;

)


(
SynthDef(\test, { | outCh=20 |
	var sig;
	sig = SinOsc.ar(1024, 0, 0.5);
	Out.ar(outCh, sig);
}).add;
SynthDef(\noise, { | outCh=20 |
	var sig;
	sig = BBandPass.ar(WhiteNoise.ar(1), 1200, 1);
	Out.ar(outCh, sig);
}).add;
)


o = Synth(\test, [\outCh, 20]);
o = Synth(\noise, [\outCh, 20]);
c = Synth(\FRDAntiLarsen, [\inCh, 20, \outCh, 21, \multiplier, 8, \coeff, 1]);
s.scope

c.set(\multiplier, 0.1);
c.set(\coeff, 1);
c.set(\coeff, 0.9);
c.set(\coeff, 0.75);
c.set(\coeff, 0.5);
c.set(\coeff, 0.25);
c.set(\coeff, 0);

s.freqscope