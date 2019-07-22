FRDMarkovChainStateMatrix {

	var path, transitionMatrix_m, states_m, current_state_m;

	// new method
	*new { | states, transitionMatrix, initialState |
		^super.new.init(states, transitionMatrix, initialState)
	}


	// init method
	init { | states, transitionMatrix, initialState |
		path =  "".resolveRelative;
		states_m = states;
		this.stateMatrix_(transitionMatrix, false);
		this.impose(initialState);
	}


	// Normalize and save to transitionMatrix_m
	stateMatrix_ { | transitionMatrix, plot=false |
		transitionMatrix_m = transitionMatrix.flop.collect({|item| item.normalize.pow(4).normalizeSum;}).flop;
		if(plot, {
			Window.new(bounds: Rect(20, 20, states_m.size, states_m.size)).front.drawFunc_({ | view |
				view.bounds.width.do({ | x |
					view.bounds.width.do({ | y |
						Pen.moveTo(x@y);
						Pen.strokeColor_(Color(transitionMatrix_m[x][y], transitionMatrix_m[x][y], transitionMatrix_m[x][y]));
						Pen.fillColor_(Color(transitionMatrix_m[x][y], transitionMatrix_m[x][y], transitionMatrix_m[x][y]));
						Pen.addRect(Rect(x, y, 1, 1));
						Pen.fillStroke;
					})
				})
			})
		});
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
		}, {current_state_m = states_m[0]});
		^current_state_m
	}


}


