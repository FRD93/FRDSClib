FRDSampler {
	var buffers, synths, out;

	*new { | path, outCh=0 |
		^super.new.init(path, outCh);
	}

	init { | path, outCh |
		buffers = Dictionary.new;
		PathName(path).filesDo({ | fpath |
			if((fpath.extension == "wav") || (fpath.extension == "aif") || (fpath.extension == "aiff"), {
				buffers.put(fpath.fileNameWithoutExtension, Buffer.read(Server.local, fpath.asAbsolutePath));
			});
		});
		synths = Dictionary.new;
		buffers.keysDo({ | key | synths.put(key, nil) });
		out = outCh;
	}

	playSample { | sample, rate=1, amp=0.5, panFrom=0, panTo=0, addAction='addToHead', target=nil |
		synths.put(sample, Synth(\FRDSampler, [\buf, buffers.at(sample), \rate, rate, \amp, amp, \panA, panFrom, \panB, panTo, \out, out], target, addAction));
	}

	isPlayingSample { | sample |
		^synths.at(sample).isPlaying
	}

	stopSample { | sample |
		synths.at(sample).stop;
		synths.put(sample, nil);
	}

	setSample { | sample, param, value |
		synths.at(sample).set(param, value);
	}

	getSample { | sample |
		^synths.at(sample)
	}

	outCh {
		^out
	}

	samples {
		^buffers.keys
	}

	getSampleBuffer { | sample |
		^buffers.at(sample)
	}

	writeSynthDef {
		SynthDef(\FRDSampler, { | buf, rate, amp, panA, panB, out, lagTime=5 |
			var snd = PlayBuf.ar(1, buf, rate, doneAction: 2);
			var pan = EnvGen.ar(Env.new([panA, panB], [BufDur.ir(buf)]));
			Out.ar(out, Pan2.ar(snd, pan, Lag2.ar(K2A.ar(amp), lagTime)));
		}).writeDefFile.add;
	}
}