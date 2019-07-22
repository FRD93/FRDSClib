FRDRandomWalk {
	var min_m, max_m, step_m, current_m;

	// new method
	*new { | min=0, max=1, step=0.1, initialState=0.5 |
		^super.new.init(min, max, step, initialState)
	}


	// init method
	init { | min, max, step, initialState |
		min_m = min;
		max_m = max;
		if(max_m < min_m, {var tmp = max_m; max_m = min_m; min_m = max_m;});
		step_m = step;
		current_m = initialState;
	}

	// Set new current state
	setState { | state |
		current_m = state;
		^current_m
	}


	// Set min boundary
	min {
		^min_m
	}
	min_ { | min |
		min_m = min;
		if(max_m < min_m, {var tmp = max_m; max_m = min_m; min_m = max_m;});
		^this
	}


	// Set max boundary
	max {
		^max_m
	}
	max_ { | max |
		max_m = max;
		if(max_m < min_m, {var tmp = max_m; max_m = min_m; min_m = max_m;});
		^this
	}

	// Seet step
	step {
		^step_m
	}
	step_ { | step |
		step_m = step;
		^this
	}

	// Calc next state
	next {
		current_m = current_m + (step_m * [-1, 0, 1].choose);
		while ( { current_m > max_m }, { current_m = current_m - step_m });
		while ( { current_m < min_m  }, { current_m = current_m + step_m });
		^current_m
	}

	// Calc next state with weights
	nextw { | down=0.75, curr=0, up=0.25 |
		var weights = [down, curr, up].normalizeSum;
		current_m = current_m + (step_m * [-1, 0, 1].wchoose(weights));
		while ( { current_m > max_m }, { current_m = current_m - step_m });
		while ( { current_m < min_m  }, { current_m = current_m + step_m });
		^current_m
	}

	getState {
		^current_m
	}





}