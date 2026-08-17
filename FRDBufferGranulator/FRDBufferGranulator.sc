/*
FRDBufferGranulator
Granulatore che legge pattern ritmici da file .scd in una cartella "Patterns/"
e li riproduce leggendo grani da un Buffer.
Copyright (c) Francesco Roberto Dani

Modifiche rispetto alla versione originale:
- Estende FRDPlugInBase: outCh/addAction/actionNode/hasGUI/window ereditati,
  niente più boilerplate duplicato.
- Rimosse `presetPath`, `presets`, `input`, `inCh_r`: erano dichiarate ma mai
  usate (o usate in modo inconsistente: `outCh_` chiamava `input.set(...)`
  ma `input` non veniva mai assegnato — bug silenzioso nell'originale).
- `writeSynthDef` è sparita come metodo di istanza: il synthdef `\GrainBufMonoAR`
  è già definito centralmente in synthdefs.scd (non duplicato qui). Se in futuro
  serve `\GrainBufMonoRM` (variante ring-modulata, presente ma inutilizzata
  nell'originale), va aggiunta a synthdefs.scd solo quando viene davvero usata.
- `patterns_r` ora gestisce esplicitamente il caso di cartella "Patterns/"
  assente o vuota, invece di fallire silenziosamente/rumorosamente in console.
*/

FRDBufferGranulator : FRDPlugInBase {

	var <buf, <patternDuration=1, <amp=1, <rate=1;
	var patternsPath, <patternNames, routines;

	// GUI
	var outCh_n, patterns_b;

	*new { | buf, outCh=0, addAction=\addToHead, actionNode=1 |
		^super.new.initBase(outCh, addAction, actionNode).initGranulator(buf)
	}

	initGranulator { | argBuf |
		buf = argBuf;
		patternsPath = "".resolveRelative ++ "Patterns/";
		routines = Dictionary.new;

		if(File.exists(patternsPath).not, {
			"FRDBufferGranulator: cartella Patterns/ non trovata in %".format(patternsPath).warn;
			patternNames = [];
			^this;
		});

		patternNames = PathName(patternsPath).files.collect(_.fileNameWithoutExtension);
		patternNames.do({ | pattern |
			var code = File(patternsPath ++ pattern ++ ".scd", "r").readAllString;
			routines.put(pattern.asString, code.interpret.value(this));
		});
	}

	// Get a Dictionary per l'integrazione in FRDMixerMatrixPlugIn
	asMixerMatrixProcess {
		^(inChannels: 0, outChannels: 2, outCh: outCh)
	}

	buf_ { | val | buf = val }
	patternDuration_ { | val | patternDuration = val }
	amp_ { | val | amp = val }
	rate_ { | val | rate = val }

	playPattern { | pattern |
		if(routines.includesKey(pattern.asString).not, {
			"FRDBufferGranulator: pattern '%' non trovato. Disponibili: %".format(pattern, patternNames).warn;
			^this;
		});
		routines.at(pattern.asString).reset.play;
	}

	refreshGUIField { | key, val |
		if(hasGUI && (key == \outCh), { outCh_n.value_(val) });
	}

	showGUI {
		outCh_n = NumberBox().action_({ | num | this.outCh_(num.value.asInteger) }).value_(outCh);
		patterns_b = patternNames.collect({ | pattern |
			Button().states_([[pattern, Color.black, Color.red], [pattern, Color.black, Color.green]])
				.action_({ | val |
					if(val.value == 1, { this.playPattern(pattern) }, { routines.at(pattern.asString).stop });
				});
		});
		this.buildWindow(
			"FRDBufferGranulator",
			[StaticText().string_("outCh"), outCh_n],
			[VLayout(*patterns_b)]
		);
	}
}
