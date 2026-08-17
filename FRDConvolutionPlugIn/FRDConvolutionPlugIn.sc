/*
FRDConvolutionPlugIn
Riverbero a convoluzione a partizioni (PartConv) con IR caricabili da cartella "IR/".
Copyright (c) Francesco Roberto Dani

Modifiche rispetto alla versione originale:
- Estende FRDPlugInBase.
- RIMOSSO `writeSynthDef` (scriveva un SynthDef `\FRDReverb` completamente
  scollegato dal resto della classe: il synth realmente usato è `\FRDConvolution`,
  definito dentro `loadConvFile`. Era dead code che confondeva la lettura).
- Il synthdef `\FRDConvolution` viene ancora ricompilato ad ogni `loadConvFile`
  perché dipende dalla dimensione del buffer IR (bufsize cambia per ogni file).
  Questo NON è un bug: è inerente al fatto che PartConv richiede bufnum fisso
  al momento della definizione del synth. Lasciato così ma documentato, perché
  in una vera classe base "tutte le SynthDef una volta sola" questo è
  un'eccezione legittima.
- Aggiunto controllo sull'esistenza del file IR prima di provare a caricarlo.
*/

FRDConvolutionPlugIn : FRDPlugInBase {

	var <inCh, <inGain=0, <outGain=0, <convName;
	var irPath, <convNameList, conv;
	var fftsize = 2048, irspectrum;

	// GUI
	var inCh_n, outCh_n, inGain_s, outGain_s, convFiles_p;

	*new { | inCh=20, outCh=0, inGain=0, outGain=0, convName="conv1.wav", addAction=\addToTail, actionNode=1 |
		^super.new.initBase(outCh, addAction, actionNode).initConv(inCh, inGain, outGain, convName)
	}

	initConv { | argInCh, argInGain, argOutGain, argConvName |
		irPath = "".resolveRelative ++ "IR/";
		if(File.exists(irPath), {
			convNameList = PathName(irPath).files.collect(_.fileName);
		}, {
			"FRDConvolutionPlugIn: cartella IR/ non trovata in %".format(irPath).warn;
			convNameList = [];
		});

		inCh = argInCh;
		inGain = argInGain;
		outGain = argOutGain;
		this.loadConvFile(argConvName);
	}

	loadConvFile { | fileName |
		if(File.exists(irPath ++ fileName).not, {
			"FRDConvolutionPlugIn: IR '%' non trovato in %".format(fileName, irPath).warn;
			^this;
		});
		convName = fileName;

		Routine {
			var irbuffer, bufsize;
			if(conv.notNil, { conv.free });

			irbuffer = Buffer.read(Server.local, irPath ++ fileName);
			0.1.wait;
			bufsize = PartConv.calcBufSize(fftsize, irbuffer);
			irspectrum = Buffer.alloc(Server.local, bufsize, 1);
			irspectrum.preparePartConv(irbuffer, fftsize);
			irbuffer.free; // il tempo-dominio non serve più, solo lo spettro

			// SynthDef ricompilata ad ogni cambio IR: bufsize/irspectrum.bufnum
			// cambiano per ogni file, vedi nota in cima al file.
			SynthDef(\FRDConvolution, { | inCh=20, outCh=0, inGain=0, outGain=0 |
				var input, drySig;
				drySig = In.ar(inCh, 2) * inGain.dbamp;
				input = drySig * 0.0125;
				input = [PartConv.ar(input[0], fftsize, irspectrum.bufnum), PartConv.ar(input[1], fftsize, irspectrum.bufnum)];
				input = CompanderD.ar(input, 0.6, 1, 0.1);
				input = Limiter.ar(input);
				Out.ar(outCh, drySig + (input * outGain.dbamp));
			}).add;

			0.1.wait;
			conv = Synth(\FRDConvolution, [\inCh, inCh, \outCh, outCh, \inGain, inGain, \outGain, outGain], actionNode, addAction);
		}.play(AppClock);
	}

	asMixerMatrixProcess {
		^(inChannels: 2, outChannels: 2, inCh: inCh, outCh: outCh)
	}

	inCh_ { | val | inCh = val; if(conv.notNil, { conv.set(\inCh, val) }); this.refreshGUIField(\inCh, val) }
	inGain_ { | val | inGain = val; if(conv.notNil, { conv.set(\inGain, val) }); this.refreshGUIField(\inGain, val) }
	outGain_ { | val | outGain = val; if(conv.notNil, { conv.set(\outGain, val) }); this.refreshGUIField(\outGain, val) }

	onParamChanged { | key, val |
		if(conv.notNil, { conv.set(key, val) });
	}

	refreshGUIField { | key, val |
		if(hasGUI, {
			case
			{ key == \inCh } { inCh_n.value_(val) }
			{ key == \outCh } { outCh_n.value_(val) }
			{ key == \inGain } { inGain_s.value_(val.dbamp) }
			{ key == \outGain } { outGain_s.value_(val.dbamp) };
		});
	}

	showGUI {
		inCh_n = NumberBox().action_({ | num | this.inCh_(num.value.asInteger) }).value_(inCh);
		outCh_n = NumberBox().action_({ | num | this.outCh_(num.value.asInteger) }).value_(outCh);
		convFiles_p = PopUpMenu().items_(convNameList).action_({ | item | this.loadConvFile(convNameList.at(item.value)) });
		inGain_s = Slider().action_({ | val | this.inGain_(val.value.ampdb) }).value_(inGain.dbamp);
		outGain_s = Slider().action_({ | val | this.outGain_(val.value.ampdb) }).value_(outGain.dbamp);

		this.buildWindow(
			"FRDConvolutionPlugIn",
			[StaticText().string_("inCh"), inCh_n, StaticText().string_("outCh"), outCh_n, StaticText().string_("Conv File"), convFiles_p],
			[
				VLayout(StaticText().string_("inGain"), inGain_s),
				VLayout(StaticText().string_("outGain"), outGain_s)
			]
		);
	}
}
