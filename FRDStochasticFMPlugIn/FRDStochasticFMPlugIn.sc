/*
MODULAZIONE DEI PARAMETRI DI SINTESI (TIMBRO)

Parametri di FRDDX7PlugIn: (FM Synth)
- numCarriers:   1 to 5
- freq:               10 to 20000
- harmonic:        true or false
- imod:               [FROM, TO]      da 0.01 a inf (10 crea già molto aliasing)
- amp:               0.0 to 1.0
- outCh:             mono output channel

Parametri di FRDRandomWalk (RandomWalk):
- min
- max
- step
- initialState

Parametri di FRDMarkovChainPlugIn (MarkovChain):
- states
- transitionMatrix
- initialState

Parametri aggiuntivi:
- micro_meso_lock: FRDRandomWalk (meso) influisce su tutti i RandomWalk (micro) o no
- macro_meso_lock: MarkovChain (macro) influisce su tutti i RandomWalk (meso) o no

La modulazione diretta dei parametri di sintesi avviene attraverso dei RandomWalk, con singole curve per singoli parametri. Questa parte può essere considerata la micro forma timbrica.
I parametri dei singoli RandomWalk (min, max, step) vengono anch'essi modulati da altri RandomWalk. Questa parte può essere considerata la meso forma timbrica.
I parametri dei RandomWalk della meso forma vengono selezionati attraverso delle MarkovChain. Questa parte può essere considerata la macro forma timbrica.
Le matrici di transizione delle MarkovChain vengono anch'esse modulate da altre MarkovChain. Questa parte serve a controllare varie macro forme timbriche.
*/


/*
MODULAZIONE DEI PARAMETRI RITMICI (FORMA TEMPORALE)

FRDDX7PlugIn Parameters:
- dur: 0.001 inf

Parametri di Routine:
- wait:

Parametri astratti:
- - overlap: isolato..stratificato (influisce su rapporto dur/wait)
- - deviation: ritmico..aritmico (deviazione casuale dal tempo di tempoClock, influisce su wait) [0.0..1.0]
- - speed: lento..veloce (scelta di un multiplo via via più estremo del tempo, influisce su wait) [0.0..1.0]
- - complexity: ripetitivo..aleatorio (scelta fra vari multipli del tempo, influisce su wait) [0.0..1.0]

Parametri esterni:
- - - tempoClock

La modulazione diretta dei parametri ritmici avviene attraverso dei RandomWalk, con singole curve per singoli parametri. Questa parte può essere considerata la micro forma ritmica.
*/



/*
MODULAZIONE DEI PARAMETRI POLIFONICI

Parametri:
- - n_voci: numero di voci
- - similarita: fuga..contrappunto
*/



/*
MODULAZIONE DEI PARAMETRI DI FREQUENZA

Parametri aggiuntivi:
- - registro: gravissimo..acutissimo
*/

FRDStochasticFMPlugIn {

	var dict, gui, dx7, rout;

	// new method
	*new { | nc_min=1, nc_max=5, nc_step=1, nc_curr=2, freq_min=2000, freq_max=15000, freq_step=300.0, freq_curr=2440, harm_step=0.01, harm_curr=0.5, imod_min=0.05, imod_max=5, imod_step=0.05, imod_curr=2.25, amp_min=0.2, amp_max=0.75, amp_step=0.05, amp_curr=0.35, out_min=0, out_max=1, out_step=1, out_curr=0, overlap_min=0.1, overlap_max=8, overlap_step=0.1, overlap_curr=2.5, deviation_min=0.03, deviation_max=0.7, deviation_step=0.05, deviation_curr=0.2, speed_min=0, speed_max=1, speed_step=1, speed_curr=0.35, complexity_min=0.2, complexity_max=0.8, complexity_step=0.05, complexity_curr=0.5 |

		^super.new.init(nc_min, nc_max, nc_step, nc_curr, freq_min, freq_max, freq_step, freq_curr, harm_step, harm_curr, imod_min, imod_max, imod_step, imod_curr, amp_min, amp_max, amp_step, amp_curr, out_min, out_max, out_step, out_curr, overlap_min, overlap_max, overlap_step, overlap_curr, deviation_min, deviation_max, deviation_step, deviation_curr, speed_min, speed_max, speed_step, speed_curr, complexity_min, complexity_max, complexity_step, complexity_curr)
	}


	// init method
	init { | nc_min, nc_max, nc_step, nc_curr, freq_min, freq_max, freq_step, freq_curr, harm_step, harm_curr, imod_min, imod_max, imod_step, imod_curr, amp_min, amp_max, amp_step, amp_curr, out_min, out_max, out_step, out_curr, overlap_min, overlap_max, overlap_step, overlap_curr, deviation_min, deviation_max, deviation_step, deviation_curr, speed_min, speed_max, speed_step, speed_curr, complexity_min, complexity_max, complexity_step, complexity_curr |
		// Dictionary to store all the parameters
		dict = Dictionary.new()
		// Parametri
		.put(\Parameters, Dictionary.new()
			// PARAMETRI DI SINTESI (TIMBRO)
			// Number of carriers
			.put(\numCarriers, Dictionary.new()
				.put(\min, nc_min)
				.put(\max, nc_max)
				.put(\step, nc_step)
				.put(\curr, nc_curr)
				.put(\type, Integer)
				.put(\Boundaries, Dictionary.new()
					.put(\min, 1)
					.put(\max, 5)
				)
			)
			// Frequency
			.put(\freq, Dictionary.new()
				.put(\min, freq_min)
				.put(\max, freq_max)
				.put(\step, freq_step)
				.put(\curr, freq_curr)
				.put(\type, Float)
				.put(\Boundaries, Dictionary.new()
					.put(\min, 5)
					.put(\max, 20000)
				)
			)
			// Harmonic
			.put(\harmonic, Dictionary.new()
				.put(\min, 0)
				.put(\max, 1)
				.put(\step, harm_step)
				.put(\curr, harm_curr)
				.put(\type, Boolean)
				.put(\Boundaries, Dictionary.new()
					.put(\min, 0.0)
					.put(\max, 1.0)
				)
			)
			// Modulation Index
			.put(\imod, Dictionary.new()
				.put(\min, imod_min)
				.put(\max, imod_max)
				.put(\step, imod_step)
				.put(\curr, imod_curr)
				.put(\type, Float)
				.put(\Boundaries, Dictionary.new()
					.put(\min, 0.0)
					.put(\max, 50)
				)
			)
			// Amplitude
			.put(\amp, Dictionary.new()
				.put(\min, amp_min)
				.put(\max, amp_max)
				.put(\step, amp_step)
				.put(\curr, amp_curr)
				.put(\type, Float)
				.put(\Boundaries, Dictionary.new()
					.put(\min, 0.0)
					.put(\max, 1.0)
				)
			)
			// Out Channel
			.put(\outCh, Dictionary.new()
				.put(\min, out_min)
				.put(\max, out_max)
				.put(\step, out_step)
				.put(\curr, out_curr)
				.put(\type, Integer)
				.put(\Boundaries, Dictionary.new()
					.put(\min, 0)
					.put(\max, 5000)
				)
			)
			// PARAMETRI RITMICI (FORMA TEMPORALE)
			// Overlap
			.put(\overlap, Dictionary.new()
				.put(\min, overlap_min)
				.put(\max, overlap_max)
				.put(\step, overlap_step)
				.put(\curr, overlap_curr)
				.put(\type, Float)
				.put(\Boundaries, Dictionary.new()
					.put(\min, 0.1)
					.put(\max, 10.0)
				)
			)
			// Deviation
			.put(\deviation, Dictionary.new()
				.put(\min, deviation_min)
				.put(\max, deviation_max)
				.put(\step, deviation_step)
				.put(\curr, deviation_curr)
				.put(\type, Float)
				.put(\Boundaries, Dictionary.new()
					.put(\min, 0.0)
					.put(\max, 1.0)
				)
			)
			// Speed
			.put(\speed, Dictionary.new()
				.put(\min, speed_min)
				.put(\max, speed_max)
				.put(\step, speed_step)
				.put(\curr, speed_curr)
				.put(\type, Float)
				.put(\Boundaries, Dictionary.new()
					.put(\min, 0.0)
					.put(\max, 1.0)
				)
			)
			// Complexity
			.put(\complexity, Dictionary.new()
				.put(\min, complexity_min)
				.put(\max, complexity_max)
				.put(\step, complexity_step)
				.put(\curr, complexity_curr)
				.put(\type, Float)
				.put(\Boundaries, Dictionary.new()
					.put(\min, 0.0)
					.put(\max, 1.0)
				)
			)
		)
		// RandomWalks
		.put(\RandomWalks, Dictionary.new());
		dict.at(\Parameters).keysValuesDo({ | key, kdict |
			dict.at(\RandomWalks).put(key, FRDRandomWalk(min: kdict.at(\min), max: kdict.at(\max), step: kdict.at(\step), initialState: kdict.at(\curr)));
		});
		// InternalParameters
		dict.put(\InternalParameters, Dictionary.new()
			.put(\wait, 0.1)
			.put(\dur, 0.1)
		);

		// GUI dictionary initially empty
		gui = Dictionary();


		// FRDDX7PlugIn instance
		dx7 = FRDDX7PlugIn();


	}


	// Get the parameters dictionary
	params {
		^dict.at(\Parameters)
	}

	// Set a parameter with [\min, \max, \step]
	param_ { | name, min, max, step |
		dict.at(\Parameters).at(name.asSymbol).put(\min, min).put(\max, max).put(\step, step);
		dict.at(\RandomWalks).at(name.asSymbol).min_(min).max_(max).step_(step);
	}

	// Function to calculate all next current values
	next {
		this.calcDurWait();
		dict.at(\RandomWalks).keysValuesDo({ | key, walk |
			dict.at(\Parameters).at(key).put(\curr, walk.next());
		});
	}


	// Function to calculate dur and wait parameters from abstract rhythm parameters
	calcDurWait {
		var complexity;
		// Fare in modo che overlap possa essere effettuato prima o dopo
		// il calcolo di rhythm e complexity?
		complexity = [0.125, 0.1875, 0.25, 0.375, 0.5, 0.75, 1, 1.5, 2, 3, 4, 5, 6].reverse;
		complexity = complexity.wchoose(Gaussian(complexity.size - 1, dict.at(\Parameters).at(\speed).at(\curr), dict.at(\Parameters).at(\complexity).at(\curr)));
		complexity = complexity + (dict.at(\Parameters).at(\deviation).at(\curr).pow(6) * complexity * [-1, 1].choose);
		complexity = complexity.clip(0.01, inf);
		dict.at(\InternalParameters).put(\wait, complexity);
		dict.at(\InternalParameters).put(\dur, (complexity * dict.at(\Parameters).at(\overlap).at(\curr)));
	}


	play { | clock, quant |
		// Master Routine
		rout = Routine{
			loop{
				{
					this.next();
					dx7.createRandomRelativePatch(
						numCarriers: dict.at(\Parameters).at(\numCarriers).at(\curr),
						freq: dict.at(\Parameters).at(\freq).at(\curr),
						amp: dict.at(\Parameters).at(\amp).at(\curr),
						dur: dict.at(\InternalParameters).at(\dur) * clock.tempo.reciprocal,
						harmonic: dict.at(\Parameters).at(\harmonic).at(\curr).coin,
						imod: dict.at(\Parameters).at(\imod).at(\curr) ! 2,
						outCh: dict.at(\Parameters).at(\outCh).at(\curr)
					);
				}.fork;
				dict.at(\InternalParameters).at(\wait).wait;
			}
		}.play(clock, quant);
	}

	stop {
		rout.stop;
	}


	dict {
		^dict
	}


	showGUI {
		var hlayouts = [];
		// Main Window
		gui.put(\Window, Window.new("FRDStochasticFMPlugIn", Rect(100, 100, 20, 20)).front.onClose_({gui = Dictionary()}));
		// RangeSlider for Parameters
		gui.put(\Parameters, Dictionary.new());
		dict.at(\Parameters).keysValuesDo({ | name, params |

			hlayouts = hlayouts ++ HLayout(RangeSlider().minWidth_(100).maxHeight_(20).orientation_(\horizontal).action_({ | val |
				// Set \min \max for parameter
				dict.at(\Parameters).at(name).put(\min, MapE(val.lo,
					0,
					1,
					dict.at(\Parameters).at(name).at(\Boundaries).at(\min),
					dict.at(\Parameters).at(name).at(\Boundaries).at(\max)
				));
				dict.at(\Parameters).at(name).put(\max, MapE(val.hi,
					0,
					1,
					dict.at(\Parameters).at(name).at(\Boundaries).at(\min),
					dict.at(\Parameters).at(name).at(\Boundaries).at(\max)
				));
				// Update RandomWalk boundaries
				dict.at(\RandomWalks).at(name).min_(dict.at(\Parameters).at(name).at(\min));
				dict.at(\RandomWalks).at(name).max_(dict.at(\Parameters).at(name).at(\max));
				// Post
				[name, dict.at(\Parameters).at(name).at(\min), dict.at(\Parameters).at(name).at(\max)].postln;
			})
			// Set values to \curr
			.lo_(MapL(
				dict.at(\Parameters).at(name).at(\min),
				dict.at(\Parameters).at(name).at(\Boundaries).at(\min),
				dict.at(\Parameters).at(name).at(\Boundaries).at(\max),
				0,
				1
			).sqrt)
			.hi_(MapL(
				dict.at(\Parameters).at(name).at(\max),
				dict.at(\Parameters).at(name).at(\Boundaries).at(\min),
				dict.at(\Parameters).at(name).at(\Boundaries).at(\max),
				0,
				1
			).sqrt), [StaticText().string_(name.asString).minWidth_(80), align:\right])

		});

		gui.at(\Window).layout_(
			VLayout(
				*hlayouts
			)
		)
	}


}