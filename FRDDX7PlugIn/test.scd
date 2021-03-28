
(
d = FRDDX7PlugIn();
r = Routine{
	loop{
		d.createRandomRelativePatch(numCarriers: 1, freq: exprand(50, 10000), amp: rrand(0.6, 0.85), dur: exprand(0.05, 2), harmonic: 0.5.coin, imod: #[0.1, 5], outCh: [0, 1].choose);
		rrand(0.05, 0.2).wait;
	}
}.play;
)
r.stop


(
d = FRDDX7PlugIn();
r = Routine{
	inf.do({|id|
		if(id%4 == 0, {thisThread.randSeed_(86735)});
		d.createRandomRelativePatch(numCarriers: 1, freq: exprand(50, 10000) * ((id+1)%4) / 2, amp: rrand(0.1, 0.5), dur: 0.2, harmonic: id%2, imod: #[0.1, 5], outCh: [0, 1].choose);
//		0.2.wait;
		if(id%8 == 0, {0.1.wait;}, {if(id%9 == 0, {0.1.wait;}, {0.2.wait});});

	});
}.play;
)
r.stop


(
d = FRDDX7PlugIn();
r = Routine{
	loop{
		d.createRandomRelativePatch(numCarriers: 1, freq: exprand(50, 10000), amp: rrand(0.1, 0.5), dur: exprand(0.05, 12), harmonic: 0.5.coin, imod: #[0.1, 5], outCh: [0, 1].choose);
		exprand(0.05, 2).wait;
	}
}.play;
)
r.stop



// NRT
(
var dur = 6;
Routine{
	(48..98).do({ | id |
		var score = d.createRandomRelativePatchNRT(numCarriers: 3, freq: id.midicps, amp: 2, dur: dur, harmonic: false, imod: [rrand(0.5, 4), rrand(0.5, 13)]);
		score = score ++ [[dur + 1, [\c_set, 0, 0]]];
		o = ServerOptions.new.numOutputBusChannels = 2;
		Score.recordNRT(score, "help-oscFile", "/Users/admin/Downloads/test/" ++ id ++ ".aiff", options: o); // synthesize
		score.dopostln;
		1.wait;
	});
}.play(AppClock);
)





s.record

s.freqscope
s.scope
0.5.coin

(
d = FRDDX7PlugIn();
d.createDefaultRelativePatch(kind: 4, outCh: 0, amp: 1, dur: 1,
	param1: (fmod: 50.midicps, imod: 10, feedback: 0, l4: 0, l1: 1, l2: 0.7, l3: 0.3, r1: 0.01, r2:0.3, r3:0.5, r4:1),
	param2: (fmod: 50.midicps, imod: 10, feedback: 0, l4: 0, l1: 1, l2: 0.7, l3: 0.3, r1: 0.01, r2:0.3, r3:0.5, r4:1),
	param3: (fmod: 50.midicps, imod: 10, feedback: 0, l4: 0, l1: 1, l2: 0.7, l3: 0.3, r1: 0.01, r2:0.3, r3:0.5, r4:1),
	param4: (fmod: 50.midicps, imod: 10, feedback: 0, l4: 0, l1: 1, l2: 0.7, l3: 0.3, r1: 0.01, r2:0.3, r3:0.5, r4:1),
	param5: (fmod: 50.midicps, imod: 10, feedback: 0, l4: 0, l1: 1, l2: 0.7, l3: 0.3, r1: 0.01, r2:0.3, r3:0.5, r4:1),
	param6: (fmod: 50.midicps, imod: 10, feedback: 0, l4: 0, l1: 1, l2: 0.7, l3: 0.3, r1: 0.01, r2:0.3, r3:0.5, r4:1),
);
)

// [3, 4, 15, 16, 17, 18], 19, 28, 30

(
d = FRDDX7PlugIn();
Routine{
	loop{
		d.createDefaultRelativePatch(kind: 4, outCh: 0, amp: 0.5, dur: 1,
			param1: (fmod: 120, imod: 1, feedback: 0, l4: 0, l1: 1, l2: 0.7, l3: 0.3, r1: 0.01, r2:0.3, r3:0.5, r4:1),
			param2: (fmod: 120, imod: 1, feedback: 0, l4: 0, l1: 1, l2: 0.7, l3: 0.3, r1: 0.01, r2:0.3, r3:0.5, r4:1),
			param3: (fmod: 120, imod: 1, feedback: 0, l4: 0, l1: 1, l2: 0.7, l3: 0.3, r1: 0.01, r2:0.3, r3:0.5, r4:1),
			param4: (fmod: 120, imod: 1, feedback: 0, l4: 0, l1: 1, l2: 0.7, l3: 0.3, r1: 0.01, r2:0.3, r3:0.5, r4:1),
			param5: (fmod: 120, imod: 1, feedback: 0, l4: 0, l1: 1, l2: 0.7, l3: 0.3, r1: 0.01, r2:0.3, r3:0.5, r4:1),
			param6: (fmod: 120, imod: 1, feedback: 0, l4: 0, l1: 1, l2: 0.7, l3: 0.3, r1: 0.01, r2:0.3, r3:0.5, r4:1),
		);
		0.5.wait;
	}
}.play
)



(
d = FRDDX7PlugIn();
Routine{
	loop{
		d.createDefaultRelativePatch(kind: [1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32].choose, outCh: 0, amp: 0.5, dur: 1,
			param1: (fmod: 120, imod: 1, feedback: 0, l4: 0, l1: 1, l2: 0.7, l3: 0.3, r1: 0.01, r2:0.3, r3:0.5, r4:1),
			param2: (fmod: 120, imod: 1, feedback: 0, l4: 0, l1: 1, l2: 0.7, l3: 0.3, r1: 0.01, r2:0.3, r3:0.5, r4:1),
			param3: (fmod: 120, imod: 1, feedback: 0, l4: 0, l1: 1, l2: 0.7, l3: 0.3, r1: 0.01, r2:0.3, r3:0.5, r4:1),
			param4: (fmod: 120, imod: 1, feedback: 0, l4: 0, l1: 1, l2: 0.7, l3: 0.3, r1: 0.01, r2:0.3, r3:0.5, r4:1),
			param5: (fmod: 120, imod: 1, feedback: 0, l4: 0, l1: 1, l2: 0.7, l3: 0.3, r1: 0.01, r2:0.3, r3:0.5, r4:1),
			param6: (fmod: 120, imod: 1, feedback: 0, l4: 0, l1: 1, l2: 0.7, l3: 0.3, r1: 0.01, r2:0.3, r3:0.5, r4:1),
		);
		0.5.wait;
	}
}.play
)



(
d = FRDDX7PlugIn();
Routine{
	loop{
		d.createDefaultRelativePatch(kind: [1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32].choose, outCh: [0, 1].choose, amp: 0.5, dur: 1,
			param1: (fmod: 120, imod: 1, feedback: 0, l4: 0, l1: 1, l2: 0.7, l3: 0.3, r1: 0.01, r2:0.3, r3:0.5, r4:1),
			param2: (fmod: 180, imod: 1, feedback: 0, l4: 0, l1: 1, l2: 0.7, l3: 0.3, r1: 0.01, r2:0.3, r3:0.5, r4:1),
			param3: (fmod: 240, imod: 1, feedback: 0, l4: 0, l1: 1, l2: 0.7, l3: 0.3, r1: 0.01, r2:0.3, r3:0.5, r4:1),
			param4: (fmod: 480, imod: 1, feedback: 0, l4: 0, l1: 1, l2: 0.7, l3: 0.3, r1: 0.01, r2:0.3, r3:0.5, r4:1),
			param5: (fmod: 360, imod: 1, feedback: 0, l4: 0, l1: 1, l2: 0.7, l3: 0.3, r1: 0.01, r2:0.3, r3:0.5, r4:1),
			param6: (fmod: 120, imod: 1, feedback: 0, l4: 0, l1: 1, l2: 0.7, l3: 0.3, r1: 0.01, r2:0.3, r3:0.5, r4:1),
		);
		0.125.wait;
	}
}.play
)




(
d = FRDDX7PlugIn();
Routine{
	~freq = 42.midicps;
	loop{

		var round = 1;

		d.createDefaultRelativePatch(kind: [1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32].choose, outCh: [0, 1].choose, amp: rrand(0.2, 0.7), dur: exprand(0.30, 3.0),
			param1: (fmod: ~freq, imod: rrand(0.3, 10), feedback: exprand(0.1, 0.9), l4: 0, l1: 1, l2: 0.7, l3: 0.3, r1: 0.1, r2:0.3, r3:0.5, r4:1),
			param2: (fmod: ~freq * 1.5, imod: rrand(0.3, 1), feedback: exprand(0.1, 0.9), l4: 0, l1: 1, l2: 0.7, l3: 0.3, r1: 0.1, r2:0.3, r3:0.5, r4:1),
			param3: (fmod:~freq * 2.01, imod: rrand(0.3, 1.3), feedback: exprand(0.1, 0.9), l4: 0, l1: 1, l2: 0.7, l3: 0.3, r1: 0.1, r2:0.3, r3:0.5, r4:1),
			param4: (fmod: ~freq * 4.009, imod: rrand(0.3, 1.8), feedback: exprand(0.1, 0.5), l4: 0, l1: 1, l2: 0.7, l3: 0.3, r1: 0.1, r2:0.3, r3:0.5, r4:1),
			param5: (fmod: ~freq * 5.05, imod: rrand(0.3, 1), feedback: exprand(0.1, 0.5), l4: 0, l1: 1, l2: 0.7, l3: 0.3, r1: 0.1, r2:0.3, r3:0.5, r4:1),
			param6: (fmod: ~freq * 6, imod: rrand(0.3, 0.5), feedback: exprand(0.1, 0.9), l4: 0, l1: 1, l2: 0.7, l3: 0.3, r1: 0.1, r2:0.3, r3:0.5, r4:1)
		);
		0.125.wait;
	}
}.play
)
~freq = 44.midicps;



(
d = FRDDX7PlugIn();
r = Routine{
	loop{
		var kind = [1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32].choose;
		d.createDefaultRelativePatch(kind: kind, outCh: [0, 1].choose, amp: rrand(0.1, 0.4), dur: exprand(0.30, 18.0),
			param1: (fmod: exprand(150, 1800), imod: rrand(0.3, 10), feedback: exprand(0.1, 0.9), l4: 0, l1: 1, l2: 0.7, l3: 0.3, r1: rrand(0.1, 1), r2:0.3, r3:0.5, r4:rrand(0.1, 1)),
			param2: (fmod: exprand(100, 300), imod: rrand(0.3, 1), feedback: exprand(0.1, 0.9), l4: 0, l1: 1, l2: 0.7, l3: 0.3, r1: rrand(0.1, 1), r2:0.3, r3:0.5, r4:rrand(0.1, 1)),
			param3: (fmod: exprand(50, 1500), imod: rrand(0.3, 7), feedback: exprand(0.1, 0.9), l4: 0, l1: 1, l2: 0.7, l3: 0.3, r1: rrand(0.1, 1), r2:0.3, r3:0.5, r4:rrand(0.1, 1)),
			param4: (fmod: exprand(100, 1130), imod: rrand(0.3, 5), feedback: exprand(0.1, 0.5), l4: 0, l1: 1, l2: 0.7, l3: 0.3, r1: rrand(0.1, 1), r2:0.3, r3:0.5, r4:rrand(0.1, 1)),
			param5: (fmod: exprand(100, 300), imod: rrand(0.3, 1), feedback: exprand(0.1, 0.5), l4: 0, l1: 1, l2: 0.7, l3: 0.3, r1: rrand(0.1, 1), r2:0.3, r3:0.5, r4:rrand(0.1, 1)),
			param6: (fmod: exprand(100, 1000), imod: rrand(0.3, 3), feedback: exprand(0.1, 0.9), l4: 0, l1: 1, l2: 0.7, l3: 0.3, r1: rrand(0.1, 1), r2:0.3, r3:0.5, r4:rrand(0.1, 1))
		);
		rrand(0.1, 0.9).wait;
	}
}.play(AppClock);
)
r.stop;


(
d = FRDDX7PlugIn();
Routine{
	loop{
		var freq = 49.midicps;
		var round = 9;
		d.createDefaultRelativePatch(kind: [1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32].choose, outCh: [0, 1].choose, amp: rrand(0.1, 0.4), dur: exprand(0.30, 1.0),
			param1: (fmod: exprand(20, 130).round(round).midicps, imod: rrand(0.3, 10), feedback: exprand(0.1, 0.9), l4: 0, l1: 1, l2: 0.7, l3: 0.3, r1: 0.1, r2:0.3, r3:0.5, r4:1),
			param2: (fmod: exprand(20, 130).round(round).midicps, imod: rrand(0.3, 1), feedback: exprand(0.1, 0.9), l4: 0, l1: 1, l2: 0.7, l3: 0.3, r1: 0.1, r2:0.3, r3:0.5, r4:1),
			param3: (fmod: exprand(20, 130).round(round).midicps, imod: rrand(0.3, 10), feedback: exprand(0.1, 0.9), l4: 0, l1: 1, l2: 0.7, l3: 0.3, r1: 0.1, r2:0.3, r3:0.5, r4:1),
			param4: (fmod: exprand(20, 130).round(round).midicps, imod: rrand(0.3, 10), feedback: exprand(0.1, 0.5), l4: 0, l1: 1, l2: 0.7, l3: 0.3, r1: 0.1, r2:0.3, r3:0.5, r4:1),
			param5: (fmod: exprand(20, 130).round(round).midicps, imod: rrand(0.3, 100), feedback: exprand(0.1, 0.5), l4: 0, l1: 1, l2: 0.7, l3: 0.3, r1: 0.1, r2:0.3, r3:0.5, r4:1),
			param6: (fmod: exprand(20, 130).round(round).midicps, imod: rrand(0.3, 10), feedback: exprand(0.1, 0.9), l4: 0, l1: 1, l2: 0.7, l3: 0.3, r1: 0.1, r2:0.3, r3:0.5, r4:1)
		);
		0.125.wait;
	}
}.play
)







(
d = FRDDX7PlugIn();
r = Routine{
	loop{
		var kind = [1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32].choose;
		kind.postln;
		d.createDefaultRelativePatch(kind: kind, outCh: [0, 1].choose, amp: rrand(0.1, 0.4), dur: exprand(1.30, 18.0),
			param1: (fmod: exprand(150, 1800), imod: rrand(0.3, 10), feedback: exprand(0.1, 0.9), l4: exprand(0.1, 0.9), l1: exprand(0.1, 0.9), l2: exprand(0.1, 0.9), l3: exprand(0.1, 0.9), r1: rrand(0.1, 1), r2:exprand(0.1, 0.9), r3:exprand(0.1, 0.9), r4:rrand(0.1, 1)),
			param2: (fmod: exprand(100, 1000), imod: rrand(0.3, 5), feedback: exprand(0.1, 0.9), l4: exprand(0.1, 0.9), l1: exprand(0.1, 0.9), l2: exprand(0.1, 0.9), l3: exprand(0.1, 0.9), r1: rrand(0.1, 1), r2:exprand(0.1, 0.9), r3:exprand(0.1, 0.9), r4:rrand(0.1, 1)),
			param3: (fmod: exprand(50, 1500), imod: rrand(0.3, 7), feedback: exprand(0.1, 0.9), l4: exprand(0.1, 0.9), l1: exprand(0.1, 0.9), l2: exprand(0.1, 0.9), l3: exprand(0.1, 0.9), r1: rrand(0.1, 1), r2:exprand(0.1, 0.9), r3:exprand(0.1, 0.9), r4:rrand(0.1, 1)),
			param4: (fmod: exprand(100, 1130), imod: rrand(0.3, 2.5), feedback: exprand(0.1, 0.5), l4: exprand(0.1, 0.9), l1: exprand(0.1, 0.9), l2: exprand(0.1, 0.9), l3: exprand(0.1, 0.9), r1: rrand(0.1, 1), r2:exprand(0.1, 0.9), r3:exprand(0.1, 0.9), r4:rrand(0.1, 1)),
			param5: (fmod: exprand(100, 900), imod: rrand(0.3, 1), feedback: exprand(0.1, 0.5), l4: exprand(0.1, 0.9), l1: exprand(0.1, 0.9), l2: exprand(0.1, 0.9), l3: exprand(0.1, 0.9), r1: rrand(0.1, 1), r2:exprand(0.1, 0.9), r3:exprand(0.1, 0.9), r4:rrand(0.1, 1)),
			param6: (fmod: exprand(30, 100), imod: rrand(0.3, 1.3), feedback: exprand(0.1, 0.9), l4: exprand(0.1, 0.9), l1: exprand(0.1, 0.9), l2: exprand(0.1, 0.9), l3: exprand(0.1, 0.9), r1: rrand(0.1, 1), r2:exprand(0.1, 0.9), r3:exprand(0.1, 0.9), r4:rrand(0.1, 1))
		);
		rrand(0.1, 0.9).wait;
	}
}.play(AppClock);
)



exprand(20, 130).round(3).midicps

20.midicps
130.midicps

(
s.meter;
s.scope;
s.freqscope;
)

m = FRDMarkovChain(["tizio", "caio", "sampronio"], [[0.6, 0.15, 0.25], [0.25, 0.6, 0.25], [0.15, 0.25, 0.5]], "tizio");
m.impose("caio");
m.next;





(
var indexes, modulators, carriers, numCarriers=2;
var patch, lastModulators;
numCarriers = numCarriers.clip(1, 6) - 1;
indexes = (0..5).scramble;
carriers = indexes[0..numCarriers];
modulators = indexes[numCarriers+1..];
[carriers, modulators].postln;
patch = modulators.collect({|mi| var myM = modulators.copy; myM.remove(mi); [mi, myM.choose] }).collect({|tuple| tuple.sort});
patch.postln;
lastModulators = patch.collect({|mp| mp[0]}).as(Set).as(Array).sort;
lastModulators.postln;
if(lastModulators.size > carriers.size, {

});
if(lastModulators.size < carriers.size, {

});
if(lastModulators.size == carriers.size, {

});

nil
)