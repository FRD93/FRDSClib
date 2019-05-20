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

Parametri aggiuntivi:
- - presenza: rarefatto..frenetico
- - ritmicita: ritmico..aritmico
- - complessita: ripetitivo..aleatorio


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

	var dict;

	// new method
	*new { | nc_min=1, nc_max=5, nc_step=1, nc_curr=2, freq_min=20, freq_max=20000, freq_step=1.0, freq_curr=440, harm_curr=false, imod_min=0.05, imod_max=5, imod_step=0.01, imod_curr=0.25 |
		^super.new.init(nc_min, nc_max, nc_step, nc_curr, freq_min, freq_max, freq_step, freq_curr, harm_curr, imod_min, imod_max, imod_step, imod_curr)
	}


	// init method
	init { | nc_min, nc_max, nc_step, nc_curr, freq_min, freq_max, freq_step, freq_curr, harm_curr, imod_min, imod_max, imod_step, imod_curr |
		// Dictionary to store all the parameters
		dict = Dictionary.new()
		// Number of carriers
		.put(\numCarriers, Dictionary.new()
			.put(\min, nc_min)
			.put(\max, nc_max)
			.put(\step, nc_step)
			.put(\curr, nc_curr)
			.put(\type, Integer)
		)
		// Frequency
		.put(\freq, Dictionary.new()
			.put(\min, freq_min)
			.put(\max, freq_max)
			.put(\step, freq_step)
			.put(\curr, freq_curr)
			.put(\type, Float)
		)
		// Harmonic
		.put(\harmonic, Dictionary.new()
			.put(\min, false)
			.put(\max, true)
			.put(\step, 1)
			.put(\curr, harm_curr)
			.put(\type, Boolean)
		)
		// Modulation Index
		.put(\imod, Dictionary.new()
			.put(\min, imod_min)
			.put(\max, imod_max)
			.put(\step, imod_step)
			.put(\curr, imod_curr)
			.put(\type, Float)
		)


	}




}