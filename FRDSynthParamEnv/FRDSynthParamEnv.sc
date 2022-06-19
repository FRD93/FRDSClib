FRDSynthParamEnv {
	*new { | synth, param, value, time=1, curve=0 |
		^super.new.init(synth, param, value, time)
	}

	init { | synth, param, value, time=1, curve=0 |
		synth.get( param, { | start_val |
			var length, val_array;
			length = (time * 1000).asInteger;
			val_array = Array.fill( length, { | index | (index + 1) / length; } );
			val_array = val_array.collect({ | idx |
				//idx.postln;
				(idx * (value - start_val)) + start_val;
			});
			Routine{
				val_array.do({ | val |
					//val.postln;
					synth.set(param, val);
					0.001.wait;
				});
			}.play;
		});
	}
}