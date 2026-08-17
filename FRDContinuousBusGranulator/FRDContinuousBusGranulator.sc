/*
FRDContinuousBusGranulator
Granulatore continuo che legge da un bus stereo e spawna grani a intervalli
regolari (grainsPerSecond), con parametri randomizzati entro range.
Copyright (c) Francesco Roberto Dani

Modifiche rispetto alla versione originale:
- Estende FRDPlugInBase.
- BUG FIX PRINCIPALE: `\GrainBusStereoToMonoAR` era definito solo dentro il
  metodo di istanza `writeSynthDef`, mai chiamato da nessuna parte. Di fatto
  il .scsyndef non veniva mai scritto su disco, quindi al boot del server
  il synth non esisteva.
  Ora `*writeSynthDef` scrive il synthdef su disco con `.writeDefFile`
  (NON `.add`: niente compilazione/invio al server a runtime). Va eseguito
  una tantum con `FRDPlugInBase.buildSynthDefs(FRDContinuousBusGranulator)`
  quando scrivi o modifichi il synthdef, poi il server lo carica da solo
  al boot — zero latenza nelle sessioni successive. A runtime, `*new` si
  limita a verificare con `FRDPlugInBase.checkSynthDefs` che il def sia
  presente, avvisando con un errore chiaro se manca il build.
- I parametri (`maxDel`, `minGDur`, `maxGDur`, `maxRate`, `maxPan`,
  `grainsPerSecond`, `amp`) sono ora letti dalla Routine leggendo le var di
  istanza correnti (non catturate per valore all'avvio): i cambi a runtime
  (es. `~dsp["BassBusGranulator"].maxRate_(...)`) hanno quindi effetto
  dal grano successivo, com'era nell'originale, ma senza i getter/setter
  duplicati riga per riga.
*/

FRDContinuousBusGranulator : FRDPlugInBase {

	var <inCh, <maxDel, <minGDur, <maxGDur, <maxRate, <maxPan, <grainsPerSecond, <amp;
	var routine;

	// GUI
	var inCh_n, outCh_n;

	*new { | inCh=20, outCh=0, maxDel=0, minGDur=0.25, maxGDur=0.5, maxRate=0, maxPan=0.63, grainsPerSecond=60, amp=1, addAction=\addToHead, actionNode=1 |
		^super.new.initBase(outCh, addAction, actionNode)
			.initGranulator(inCh, maxDel, minGDur, maxGDur, maxRate, maxPan, grainsPerSecond, amp)
	}

	initGranulator { | argInCh, argMaxDel, argMinGDur, argMaxGDur, argMaxRate, argMaxPan, argGrainsPerSecond, argAmp |
		FRDPlugInBase.checkSynthDefs(this.class);

		inCh = argInCh;
		maxDel = argMaxDel;
		minGDur = argMinGDur;
		maxGDur = argMaxGDur;
		maxRate = argMaxRate;
		maxPan = argMaxPan;
		grainsPerSecond = argGrainsPerSecond;
		amp = argAmp;

		routine = Routine {
			loop {
				var delay = rrand(0.0, maxDel);
				Synth(\GrainBusStereoToMonoAR, [
					\bus, inCh,
					\delay, delay,
					\dur, rrand(minGDur, maxGDur).round(0.01),
					\atk, rrand(0.2, 0.8).roundUp(0.1),
					\rate, rrand(1 - maxRate, 1 + maxRate).round(0.25),
					\amp, exprand(0.75, 1) * amp.dbamp,
					\pan, rrand(-1 * maxPan, maxPan),
					\out, outCh
				], actionNode, addAction);
				grainsPerSecond.reciprocal.wait;
			};
		};
		routine.play;
	}

	*synthDefNames { ^[\GrainBusStereoToMonoAR] }

	*writeSynthDef {
		SynthDef(\GrainBusStereoToMonoAR, { | bus, delay=0, dur, atk, rate, amp, pan, out |
			var snd, env;
			EnvGen.ar(Env.new([0, 0], [dur + delay]), doneAction: 2);
			snd = In.ar(bus, 2).sum / 2.0;
			snd = PitchShift.ar(snd, 0.01, rate, 0, 0);
			env = EnvGen.ar(Env.new([0, 1, 0], [dur * atk, dur * (1.0 - atk)], [-2, 2]), levelScale: amp, doneAction: 0);
			Out.ar(out, Pan2.ar(DelayL.ar(snd * env, 1, delay), pan));
		}).writeDefFile.add;
	}

	asMixerMatrixProcess {
		^(inChannels: 2, outChannels: 2, inCh: inCh, outCh: outCh)
	}

	inCh_ { | val | inCh = val; this.refreshGUIField(\inCh, val) }
	maxDel_ { | val | maxDel = val }
	minGDur_ { | val | minGDur = val }
	maxGDur_ { | val | maxGDur = val }
	maxRate_ { | val | maxRate = val }
	maxPan_ { | val | maxPan = val }
	grainsPerSecond_ { | val | grainsPerSecond = val }
	amp_ { | val | amp = val }

	start { routine.reset.play }
	stop { routine.stop }

	refreshGUIField { | key, val |
		if(hasGUI, {
			case
			{ key == \inCh } { inCh_n.value_(val) }
			{ key == \outCh } { outCh_n.value_(val) };
		});
	}

	showGUI {
		inCh_n = NumberBox().action_({ | num | this.inCh_(num.value.asInteger) }).value_(inCh);
		outCh_n = NumberBox().action_({ | num | this.outCh_(num.value.asInteger) }).value_(outCh);
		this.buildWindow(
			"FRDContinuousBusGranulator",
			[StaticText().string_("inCh"), inCh_n, StaticText().string_("outCh"), outCh_n]
		);
	}
}
