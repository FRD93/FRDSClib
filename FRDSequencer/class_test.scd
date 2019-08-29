(
f = FRDSequencer();
t = TempoClock(2).permanent_(true);
f.showGUI();
)
t.tempo_(0.25)
f.patterns[0]
(
Routine{
	100.do({
		t.tempo_(t.tempo + 0.002);
		0.05.wait;
	})
}.play
)

// One Shot pattern play
f.playPattern(f.patterns[0], t, [1, 0, 0])

t.tempo_(1.5)
// Play Loop
(
c = Routine{
	loop{
	f.patterns.do( { | pattern |
		f.playPattern( pattern, t, [1, 0, 0] );
		((pattern.numbeats * pattern.repetitions) ).wait;
		//(1 / pattern.resolution).wait;
	} );
	}
}.play( t, [1, 0, 0] );
)
c.stop;


d = FRDDX7PlugIn();
d.createRandomRelativePatch(4, 80, 4.9, 4, false, [0.6, 4], [0, 1].choose);





(4 < 3) && (5 < 3)





