/*
MODULAZIONE DEI PARAMETRI DI SINTESI (TIMBRO)

Parametri di FRDDX7PlugIn: (FM Synth)
- numCarriers:   1 to 5
- freq:               10 to 20000
- amp:               0.0 to 1.0
- harmonic:        true or false
- imod:               [FROM, TO]      da 0.01 a inf (10 crea già molto aliasing)
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

	// new method
	*new {
		^super.new.init()
	}


	// init method
	init {

	}




}