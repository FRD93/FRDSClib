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
		step_m = step;
		current_m = initialState;
	}

	setState { | state |
		current_m = state;
		^current_m
	}

	calcNext {
		current_m = current_m + (step_m * [-1, 0, 1].choose);
		while ( { current_m > max_m }, { current_m = current_m - step_m });
		while ( { current_m < min_m  }, { current_m = current_m + step_m });
		^current_m
	}

	calcNextW { | down=0.75, curr=0, up=0.25 |
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