b = Buffer.read(s, Platform.resourceDir +/+ "sounds/a11wlk01.wav");
b = Buffer.read(s, "/Users/admin/Music/SuperCollider Recordings/SC_190318_171306.aiff")

(
SynthDef(\Rev, {|feed=0.5, width=0.5, lowshelff=160, lowshelfg=0, hishelff=2656, hishelfg=0, hpf=5, lpf=20000, wet=0.5, amp=1|
	var sigA, rev;
	sigA = PlayBuf.ar(1, b, 1, loop: 1) * 0.25;
	sigA = Pan2.ar(sigA, SinOsc.ar(0.4, 0, 1));
	rev = FRDReverb.ar(sigA[0], sigA[1], feed, width, lowshelff, lowshelfg, hishelff, hishelfg, hpf, lpf, wet);
	Out.ar(0, rev * amp);
}).add;
)

(
a = Synth(\Rev, [\feed, 0.5, \width, 0.5, \wet, 0.75, \amp, 2]);
a.set(\feed, 0.25);
a.set(\width, 0.6);
a.set(\hishelff, 2656);
a.set(\hishelfg, -8);
a.set(\lowshelff, 360);
a.set(\lowshelfg, 6);
a.set(\hpf, 5);
a.set(\lpf, 15000);
a.set(\wet, 0.38);
a.set(\amp, 2);
)



b.play;


{BLowShelf.ar(BHiShelf.ar(WhiteNoise.ar(0.1), 1200, 1, 0), 160, 1, 30)}.play
s.freqscope