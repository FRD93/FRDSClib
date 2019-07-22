
(
t = TempoClock(tempo: 5, beats: 4, queueSize: 32768);
f = FRDStochasticFMPlugIn(tempoClock: t, freq_min: 100, freq_max: 4000, freq_step: 41, freq_curr: 180, imod_min: 0.1);
f.play(t, [1, 0, 0]);
f.showGUI;
)
f.play;
f.stop;

f.calcDurWait
f.next
d = f.dict

t.tempo_(5)

d.at(\Parameters).at(\complexity)
d.at(\Parameters).at(\complexity).at(\curr)


[0, 1, 2, 3, 4, 5, 6, 7][3..]

s.options.memSize
s.options.numPrivateAudioBusChannels
s.options.memSize = 2097152
s.options.numPrivateAudioBusChannels = 32768
s.quit
s.boot
b = Bus.audio(s, 1)
b.free

s.freqscope

TempoClock.new()

(0..5).clip(2, 3)


MarkovStateMatrix(400, \ripple, 0.5@0.5, 0.1, plot:true);
MarkovStateMatrix(400, \torus, 0.5@0.0, 0.5, plot:true);
MarkovStateMatrix(400, \bumps, 1@1, 1, plot:true);
MarkovStateMatrix(400, \fences, 0@0.5, 1, plot:true);


(
MarkovStateMatrix(300, \ripple, 0.5@0.5, 1, plot:true);
MarkovStateMatrix(300, \torus, 0.5@0.5, 0.5, plot:true);
MarkovStateMatrix(300, \bumps, 0.5@0.5, 2, plot:true);
MarkovStateMatrix(300, \fences, 0.5@0.5, 1, plot:true);

)

(0..30).size
f = FRDMarkovChain([0, 1, 3], [[0, 0, 1], [1, 0, 0.3], [1, 0, 0.1]], 0)
(
var size = 100;
f = FRDMarkovChainStateMatrix((0..size-1), MarkovStateMatrix(size, \torus, 0.5@0.0, 0.5, plot:false), (2).asInteger);
1000.collect({f.next}).plot;
)

(
var size = 100;
f = FRDMarkovChainStateMatrix((0..size-1), MarkovStateMatrix(size, \bumps, 0.5@0.5, 0.1, plot:false), (2).asInteger);
1000.collect({f.next}).plot;
)

(
var size = 100;
f = FRDMarkovChainStateMatrix((0..size-1), MarkovStateMatrix(size, \fences, 0@0, 1, plot:false), (2).asInteger);
1000.collect({f.next}).plot;
)

(
var size = 100;
f = FRDMarkovChainStateMatrix((0..size-1), MarkovStateMatrix(size, \ripple, 0.5@0.5, 0.1, plot:false), (2).asInteger);
1000.collect({f.next}).plot;
)










f.params
f.param_(\freq, 100, 5000, 1)
f.param_(\overlap, 3, 10, 0.4)
f.param_(\imod, 0.3, 5, 0.1)



(
var fm_plugin;
t = TempoClock(tempo: 5, beats: 4, queueSize: 32768);
fm_plugin = FRDStochasticFMPlugIn(tempoClock: t, freq_min: 100, freq_max: 4000, freq_step: 41, freq_curr: 180, imod_min: 0.1);



Routine{
	var size = 100;

	var numCarriers_chain_params, numCarriers_chains, numCarriers_mms;
	var freq_chain_params, freq_chains, freq_mms;
	var imod_chain_params, imod_chains, imod_mms;
	var harmonic_chain_params, harmonic_chains, harmonic_mms;
	var deviation_chain_params, deviation_chains, deviation_mms;
	var overlap_chain_params, overlap_chains, overlap_mms;
	var complexity_chain_params, complexity_chains, complexity_mms;
	var speed_chain_params, speed_chains, speed_mms;


	/* PARAMETERS OF MARKOV CHAINS */
	numCarriers_chain_params = [\ripple, 0@0.5, 0.1];
	freq_chain_params             = [\fences, 0@0.5, 0.1];
	imod_chain_params            = [\ripple, 0@0.5, 0.1];
	harmonic_chain_params     = [\bumps, 0@0.5, 0.1];
	deviation_chain_params      = [\ripple, 0@0.5, 0.1];
	overlap_chain_params        = [\bumps, 0@0.5, 0.1];
	complexity_chain_params   = [\ripple, 0@0.5, 0.1];
	speed_chain_params          = [\fences, 0@0.5, 0.1];

	/* MARKOV CHAINS */
	numCarriers_chains = [
		FRDMarkovChainStateMatrix((0..size-1), MarkovStateMatrix(size, numCarriers_chain_params[0], numCarriers_chain_params[1], numCarriers_chain_params[2], plot:false), rrand(0, size-1)), // min
		FRDMarkovChainStateMatrix((0..size-1), MarkovStateMatrix(size, numCarriers_chain_params[0], numCarriers_chain_params[1], numCarriers_chain_params[2], plot:false), rrand(0, size-1)), // max
		FRDMarkovChainStateMatrix((0..size-1), MarkovStateMatrix(size, numCarriers_chain_params[0], numCarriers_chain_params[1], numCarriers_chain_params[2], plot:false), rrand(0, size-1)) // step
	];
	freq_chains = [
		FRDMarkovChainStateMatrix((0..size-1), MarkovStateMatrix(size, freq_chain_params[0], freq_chain_params[1], freq_chain_params[2], plot:false), rrand(0, size-1)), // min
		FRDMarkovChainStateMatrix((0..size-1), MarkovStateMatrix(size, freq_chain_params[0], freq_chain_params[1], freq_chain_params[2], plot:false), rrand(0, size-1)), // max
		FRDMarkovChainStateMatrix((0..size-1), MarkovStateMatrix(size, freq_chain_params[0], freq_chain_params[1], freq_chain_params[2], plot:false), rrand(0, size-1)) // step
	];
	imod_chains = [
		FRDMarkovChainStateMatrix((0..size-1), MarkovStateMatrix(size, imod_chain_params[0], imod_chain_params[1], imod_chain_params[2], plot:false), rrand(0, size-1)), // min
		FRDMarkovChainStateMatrix((0..size-1), MarkovStateMatrix(size, imod_chain_params[0], imod_chain_params[1], imod_chain_params[2], plot:false), rrand(0, size-1)), // max
		FRDMarkovChainStateMatrix((0..size-1), MarkovStateMatrix(size, imod_chain_params[0], imod_chain_params[1], imod_chain_params[2], plot:false), rrand(0, size-1)) // step
	];
	harmonic_chains = [
		FRDMarkovChainStateMatrix((0..size-1), MarkovStateMatrix(size, harmonic_chain_params[0], harmonic_chain_params[1], harmonic_chain_params[2], plot:false), rrand(0, size-1)), // min
		FRDMarkovChainStateMatrix((0..size-1), MarkovStateMatrix(size, harmonic_chain_params[0], harmonic_chain_params[1], harmonic_chain_params[2], plot:false), rrand(0, size-1)), // max
		FRDMarkovChainStateMatrix((0..size-1), MarkovStateMatrix(size, harmonic_chain_params[0], harmonic_chain_params[1], harmonic_chain_params[2], plot:false), rrand(0, size-1)) // step
	];
	deviation_chains = [
		FRDMarkovChainStateMatrix((0..size-1), MarkovStateMatrix(size, deviation_chain_params[0], deviation_chain_params[1], deviation_chain_params[2], plot:false), rrand(0, size-1)), // min
		FRDMarkovChainStateMatrix((0..size-1), MarkovStateMatrix(size, deviation_chain_params[0], deviation_chain_params[1], deviation_chain_params[2], plot:false), rrand(0, size-1)), // max
		FRDMarkovChainStateMatrix((0..size-1), MarkovStateMatrix(size, deviation_chain_params[0], deviation_chain_params[1], deviation_chain_params[2], plot:false), rrand(0, size-1)) // step
	];
	overlap_chains = [
		FRDMarkovChainStateMatrix((0..size-1), MarkovStateMatrix(size, overlap_chain_params[0], overlap_chain_params[1], overlap_chain_params[2], plot:false), rrand(0, size-1)), // min
		FRDMarkovChainStateMatrix((0..size-1), MarkovStateMatrix(size, overlap_chain_params[0], overlap_chain_params[1], overlap_chain_params[2], plot:false), rrand(0, size-1)), // max
		FRDMarkovChainStateMatrix((0..size-1), MarkovStateMatrix(size, overlap_chain_params[0], overlap_chain_params[1], overlap_chain_params[2], plot:false), rrand(0, size-1)) // step
	];
	complexity_chains = [
		FRDMarkovChainStateMatrix((0..size-1), MarkovStateMatrix(size, complexity_chain_params[0], complexity_chain_params[1], complexity_chain_params[2], plot:false), rrand(0, size-1)), // min
		FRDMarkovChainStateMatrix((0..size-1), MarkovStateMatrix(size, complexity_chain_params[0], complexity_chain_params[1], complexity_chain_params[2], plot:false), rrand(0, size-1)), // max
		FRDMarkovChainStateMatrix((0..size-1), MarkovStateMatrix(size, complexity_chain_params[0], complexity_chain_params[1], complexity_chain_params[2], plot:false), rrand(0, size-1)) // step
	];
	speed_chains = [
		FRDMarkovChainStateMatrix((0..size-1), MarkovStateMatrix(size, speed_chain_params[0], speed_chain_params[1], speed_chain_params[2], plot:false), rrand(0, size-1)), // min
		FRDMarkovChainStateMatrix((0..size-1), MarkovStateMatrix(size, speed_chain_params[0], speed_chain_params[1], speed_chain_params[2], plot:false), rrand(0, size-1)), // max
		FRDMarkovChainStateMatrix((0..size-1), MarkovStateMatrix(size, speed_chain_params[0], speed_chain_params[1], speed_chain_params[2], plot:false), rrand(0, size-1)) // step
	];

	5.wait;
	//s.record;
	1.wait;
	fm_plugin.play(t, [1, 0, 0]);

	300.do({ | id |
		/* PARAMETER RANGES */
		numCarriers_mms = numCarriers_chains.collect({|chain| Map(chain.next, 0, size-1, 1, 5).asInteger});
		freq_mms = freq_chains.collect({|chain| MapE(chain.next, 0, size-1, 20, 2000)});
		imod_mms = imod_chains.collect({|chain| MapE(chain.next, 0, size-1, 0.03, 2)});
		harmonic_mms = harmonic_chains.collect({|chain| MapL(chain.next, 0, size-1, 0, 1.0)});
		deviation_mms = deviation_chains.collect({|chain| Map(chain.next, 0, size-1, 0.01, 1.0)});
		overlap_mms = overlap_chains.collect({|chain| MapL(chain.next, 0, size-1, 0.4, 10)});
		complexity_mms = complexity_chains.collect({|chain| MapL(chain.next, 0, size-1, 0.2, 0.9)});
		speed_mms = speed_chains.collect({|chain| Map(chain.next, 0, size-1, 0.2, 0.9)});

		/* PARAMETER SET */
		fm_plugin.param_(\numCarriers, numCarriers_mms[0], numCarriers_mms[1], numCarriers_mms[2]);
		fm_plugin.param_(\freq, freq_mms[0], freq_mms[1], freq_mms[2]);
		fm_plugin.param_(\imod, imod_mms[0], imod_mms[1], imod_mms[2]);
		fm_plugin.param_(\harmonic, harmonic_mms[0], harmonic_mms[1], harmonic_mms[2]);
		fm_plugin.param_(\deviation, deviation_mms[0], deviation_mms[1], deviation_mms[2]);
		fm_plugin.param_(\overlap, overlap_mms[0], overlap_mms[1], overlap_mms[2]);
		fm_plugin.param_(\complexity, complexity_mms[0], complexity_mms[1], complexity_mms[2]);
		fm_plugin.param_(\speed, speed_mms[0], speed_mms[1], speed_mms[2]);
/*		*/
		1.wait;

	});
	20.wait;
	//s.stopRecording;
	"Ho finito".postln;
}.play
)

MapL(0.5, 0, 1, 0, 1)






