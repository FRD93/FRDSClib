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
		this.setTransitionMatrix(transitionMatrix);
		this.impose(initialState);
	}


	// Normalize and save to transitionMatrix_m
	setTransitionMatrix { | transitionMatrix |
		transitionMatrix_m = transitionMatrix.collect({|item| item.normalizeSum});
		^transitionMatrix_m
	}

	reinit { | states, transitionMatrix, initialState=nil |
		states_m = states;
		this.setTransitionMatrix(transitionMatrix);
		if(initialState.notNil == true, {
			this.impose(initialState);
		});
	}


	// Calc next step in the chain
	next {
		var prob = transitionMatrix_m[states_m.find([current_state_m])];
		current_state_m = states_m.wchoose(prob);
		^current_state_m
	}

	// Get current state
	current {
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


