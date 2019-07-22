(
f = FRDSequencer();
t = TempoClock(0.5).permanent_(true);
f.showGUI();
)

f.patterns[0]
f.playPattern(f.patterns[0], t, [1, 0, 0])