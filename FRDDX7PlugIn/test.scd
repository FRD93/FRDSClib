(
d = FRDDX7PlugIn();
d.createDefaultRelativePatch(kind: 4, outCh: 0, amp: 0.5, dur: 1,
	param1: (fmod: 50.midicps, imod: 1, feedback: 0, l4: 0, l1: 1, l2: 0.7, l3: 0.3, r1: 0.01, r2:0.3, r3:0.5, r4:1),
	param2: (fmod: 50.midicps, imod: 1, feedback: 0, l4: 0, l1: 1, l2: 0.7, l3: 0.3, r1: 0.01, r2:0.3, r3:0.5, r4:1),
	param3: (fmod: 50.midicps, imod: 1, feedback: 0, l4: 0, l1: 1, l2: 0.7, l3: 0.3, r1: 0.01, r2:0.3, r3:0.5, r4:1),
	param4: (fmod: 50.midicps, imod: 1, feedback: 0, l4: 0, l1: 1, l2: 0.7, l3: 0.3, r1: 0.01, r2:0.3, r3:0.5, r4:1),
	param5: (fmod: 50.midicps, imod: 1, feedback: 0, l4: 0, l1: 1, l2: 0.7, l3: 0.3, r1: 0.01, r2:0.3, r3:0.5, r4:1),
	param6: (fmod: 50.midicps, imod: 1, feedback: 0, l4: 0, l1: 1, l2: 0.7, l3: 0.3, r1: 0.01, r2:0.3, r3:0.5, r4:1),
);
)


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
		d.createDefaultRelativePatch(kind: [1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32].choose, outCh: 0, amp: 0.5, dur: 1,
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
	loop{
		var freq = 89.midicps;
		var round = 1;
		d.createDefaultRelativePatch(kind: [1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32].choose, outCh: 0, amp: rrand(0.1, 0.4), dur: exprand(0.30, 1.0),
			param1: (fmod: freq, imod: rrand(0.3, 10), feedback: exprand(0.1, 0.9), l4: 0, l1: 1, l2: 0.7, l3: 0.3, r1: 0.1, r2:0.3, r3:0.5, r4:1),
			param2: (fmod: freq * 1.5, imod: rrand(0.3, 1), feedback: exprand(0.1, 0.9), l4: 0, l1: 1, l2: 0.7, l3: 0.3, r1: 0.1, r2:0.3, r3:0.5, r4:1),
			param3: (fmod:freq * 2, imod: rrand(0.3, 3), feedback: exprand(0.1, 0.9), l4: 0, l1: 1, l2: 0.7, l3: 0.3, r1: 0.1, r2:0.3, r3:0.5, r4:1),
			param4: (fmod: freq * 4, imod: rrand(0.3, 7), feedback: exprand(0.1, 0.5), l4: 0, l1: 1, l2: 0.7, l3: 0.3, r1: 0.1, r2:0.3, r3:0.5, r4:1),
			param5: (fmod: freq * 5, imod: rrand(0.3, 10), feedback: exprand(0.1, 0.5), l4: 0, l1: 1, l2: 0.7, l3: 0.3, r1: 0.1, r2:0.3, r3:0.5, r4:1),
			param6: (fmod: freq * 6, imod: rrand(0.3, 5), feedback: exprand(0.1, 0.9), l4: 0, l1: 1, l2: 0.7, l3: 0.3, r1: 0.1, r2:0.3, r3:0.5, r4:1)
		);
		0.125.wait;
	}
}.play
)




(
d = FRDDX7PlugIn();
r = Routine{
	loop{
		d.createDefaultRelativePatch(kind: [1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32].choose, outCh: 0, amp: rrand(0.1, 0.4), dur: exprand(0.30, 18.0),
			param1: (fmod: exprand(150, 1800), imod: rrand(0.3, 10), feedback: exprand(0.1, 0.9), l4: 0, l1: 1, l2: 0.7, l3: 0.3, r1: rrand(0.1, 1), r2:0.3, r3:0.5, r4:rrand(0.1, 1)),
			param2: (fmod: exprand(100, 3000), imod: rrand(0.3, 1), feedback: exprand(0.1, 0.9), l4: 0, l1: 1, l2: 0.7, l3: 0.3, r1: rrand(0.1, 1), r2:0.3, r3:0.5, r4:rrand(0.1, 1)),
			param3: (fmod: exprand(50, 1500), imod: rrand(0.3, 7), feedback: exprand(0.1, 0.9), l4: 0, l1: 1, l2: 0.7, l3: 0.3, r1: rrand(0.1, 1), r2:0.3, r3:0.5, r4:rrand(0.1, 1)),
			param4: (fmod: exprand(100, 10130), imod: rrand(0.3, 5), feedback: exprand(0.1, 0.5), l4: 0, l1: 1, l2: 0.7, l3: 0.3, r1: rrand(0.1, 1), r2:0.3, r3:0.5, r4:rrand(0.1, 1)),
			param5: (fmod: exprand(100, 300), imod: rrand(0.3, 1), feedback: exprand(0.1, 0.5), l4: 0, l1: 1, l2: 0.7, l3: 0.3, r1: rrand(0.1, 1), r2:0.3, r3:0.5, r4:rrand(0.1, 1)),
			param6: (fmod: exprand(100, 3000), imod: rrand(0.3, 3), feedback: exprand(0.1, 0.9), l4: 0, l1: 1, l2: 0.7, l3: 0.3, r1: rrand(0.1, 1), r2:0.3, r3:0.5, r4:rrand(0.1, 1))
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
		d.createDefaultRelativePatch(kind: [1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32].choose, outCh: 0, amp: rrand(0.1, 0.4), dur: exprand(0.30, 1.0),
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




exprand(20, 130).round(3).midicps

20.midicps
130.midicps

s.scope
s.freqscope

m = FRDMarkovChain(["tizio", "caio", "sampronio"], [[0.6, 0.15, 0.25], [0.25, 0.6, 0.25], [0.15, 0.25, 0.5]], "tizio");
m.impose("caio");
m.next;