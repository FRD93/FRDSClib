

f = FRDStochasticFMPlugIn()
d = FRDDX7PlugIn()
d.createRandomRelativePatch(numCarriers: 1, freq: exprand(100, 1000), amp: rrand(0.3, 0.6), dur: exprand(0.5, 4), harmonic: 0.5.coin, imod: #[1, 3], outCh: [0, 1].choose);

r = FRDRandomWalk(0, 1, 0.1, 0.5)

r.calcNext

r = FRDRandomWalk(-1, 2, 0.05, 0);
r.calcNextW(0, 0.25, 0.75)

r.get