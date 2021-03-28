FRDMarkovChain {

	var path, transitionMatrix_m, states_m, current_state_m;

	// new method
	*new { | states, transitionMatrix, initialState |
		^super.new.init(states, transitionMatrix, initialState)
	}


	// init method
	init { | states, transitionMatrix, initialState |
		path =  "".resolveRelative;
		states_m = states;
		this.acquireTransitionMatrix(transitionMatrix);
		this.impose(initialState);
	}


	// Normalize and save to transitionMatrix_m
	acquireTransitionMatrix { | transitionMatrix |
		transitionMatrix_m = transitionMatrix.flop.collect({|item| item.normalizeSum}).flop;
		^transitionMatrix_m
	}


	// Calc next step in the chain
	next {
		var prob = transitionMatrix_m.flop[states_m.find([current_state_m])];
		current_state_m = states_m.wchoose(prob);
		^current_state_m
	}


	// Impose next step in the chain
	impose { | state |
		if(states_m.find([state]) != nil, {
			current_state_m = state;
		})
		^current_state_m
	}


}


