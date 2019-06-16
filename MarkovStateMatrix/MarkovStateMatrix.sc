MarkovStateMatrix {

	var map;

	// new method
	*new { | size=32, kind=\ripple, center, mul=1, plot=false |
		^super.new.init(size, kind, center, mul, plot)
	}


	// init method
	init { | size, kind, center, mul, plot |
		center = center.clip(0, 1.0) * size.neg;
		map = switch(kind,
			// sin(10(x^2+y^2))/10
			\ripple, Array.fill(size, { | x | Array.fill(size, { | y |
				sin(10 * ((((x+center.x)/size)*mul).pow(2)+(((y+center.y)/size)*mul).pow(2)))/10
			})}).flat.normalize.clump(size),
			// (0.4^2-(0.6-(x^2+y^2)^0.5)^2)^0.5
			\torus, Array.fill(size, { | x | Array.fill(size, { | y |
				(0.4.pow(2)-(0.6-((((x+center.x)/size)*mul).pow(2)+(((y+center.y)/size)*mul).pow(2)).pow(0.5)).pow(2)).pow(0.5);
			})}).flat.normalize.collect({|val| if(val.isNaN.not, {val}, {0})}).clump(size)

		);

		if(plot, {
			Window.new(bounds: Rect(20, 20, size, size)).front.drawFunc_({ | view |
				view.bounds.width.do({ | x |
					view.bounds.width.do({ | y |
						Pen.moveTo(x@y);
						Pen.strokeColor_(Color(map[x][y], map[x][y], map[x][y]));
						Pen.fillColor_(Color(map[x][y], map[x][y], map[x][y]));
						Pen.addRect(Rect(x, y, 1, 1));
						Pen.fillStroke;
					})
				})
			})
		});

		^map
	}

	kinds {
		^[\ripple, \torus]
	}

}
