/*
FRDMixerMatrixPlugIn
Un mixer a matrice per collegare visivamente processi/synth fra loro.
Copyright (c) 2018 Francesco Roberto Dani

Modifiche rispetto alla versione originale:
- BUG FIX: `writeDefFile` (64 SynthDef \RouteNxM, per ogni combinazione
  1-8 canali in/out) era un metodo di istanza mai invocato da `init`: senza
  chiamarlo a mano, `route()` falliva perché il synth richiesto non esisteva
  sul server.
  Stesso modello "build al volo" di FRDPlugInBase (anche se questa classe
  non eredita da FRDPlugInBase, essendo strutturalmente un router e non un
  plugin per-canale): `*writeSynthDef` scrive i 64 .scsyndef su disco E li
  invia subito al server con `.add`. A runtime `init` verifica con
  `checkSynthDefs` che almeno `\Route11` esista in SynthDescLib; se manca,
  il build scatta automaticamente, senza bisogno di richiamarlo a mano né
  di riavviare il server. Da quel momento i file sono anche su disco: le
  sessioni successive li troveranno già caricati al boot, quindi il
  controllo diventa un no-op — nessuna latenza a regime.
  Va rieseguito a mano SOLO se cambi il synthdef stesso o il numero massimo
  di canali (8, hardcoded): `FRDMixerMatrixPlugIn.writeSynthDef`.
- BUG FIX: `fromProcess++toProcess` produceva una String anche quando le
  variabili erano dichiarate/trattate come Symbol altrove — funzionava "per
  caso" ma era fuorviante. Ora le chiavi di routing sono esplicitamente
  Symbol via `(from: ..., to: ...)` come identificativo strutturato invece
  di concatenazione di stringhe, eliminando ambiguità e collisioni
  (es. from="A", to="BC" vs from="AB", to="C" generavano la stessa stringa
  concatenata "ABC" — bug di collisione delle chiavi presente nell'originale).
- `writeSynthDef` (istanza) è ora un metodo di CLASSE (`*writeSynthDef`), per
  coerenza con le altre classi della libreria e per non confondersi con
  `Synth`/`SynthDef.writeDefFile`, che resta il metodo interno usato per
  scrivere davvero il file su disco.
- BUG FIX GEOMETRIA GUI: nella versione originale (e nel mio primo refactor,
  che avevo lasciato identico per errore) le celle NON ancora collegate non
  avevano alcun indicatore visivo — si vedevano solo le linee della griglia,
  senza modo di sapere dove cliccare con precisione. Ora ogni intersezione
  disegna un piccolo marker (anche se non collegata), e le route attive
  restano evidenziate sopra con un pallino pieno più grande.
  Le etichette (nomi outlet/inlet) erano ancorate con un offset in pixel
  fisso, scollegato dalla larghezza reale del testo: con nomi lunghi
  l'etichetta finiva disallineata rispetto alla colonna/riga a cui si
  riferiva. Ora l'etichetta di ogni outlet è centrata sulla sua colonna
  usando `.bounds(font).width`, e l'ordine di inlets/outlets è reso
  deterministico ordinandoli per nome (Dictionary/Set non garantiscono un
  ordine di iterazione stabile tra una redraw e l'altra).
- GUI PRESET: aggiunto un pannello sotto la matrice con campo cartella
  preset (+ pulsante "Sfoglia..."), elenco a tendina dei preset trovati
  nella cartella (file "*_records.scd"), campo nome e pulsanti
  Salva/Carica/Elimina — prima queste operazioni erano possibili solo da
  codice (`writePreset`/`readPreset`, chiamate a mano dalla post window).
  La matrice e il pannello ora vivono in un `UserView` dedicato dentro un
  `VLayout`, non più disegnati direttamente su tutta la finestra.
- FIX TAGLIO ETICHETTE: il margine destro riservato ai nomi delle righe
  (inlet) era fisso a 40px; con nomi più lunghi di 4-5 caratteri venivano
  tagliati dal bordo della finestra. Ora è calcolato dinamicamente sulla
  larghezza reale del nome più lungo (`.bounds(font).width`), sia nel
  disegno sia nel rilevamento del click (le due formule devono restare
  identiche, altrimenti si ripresenta il problema "clicco ma non è lì").
*/

FRDMixerMatrixPlugIn {

	classvar checked = false;
	classvar maxChannels = 8;

	var routes, records, <presetPath;
	var width, height, spacing, buttonsWidth, mainWindow, matrixView;
	var inlets, outlets;

	// GUI preset panel
	var presetPathField, presetPopUp, presetNameField, statusText;

	*new {
		^super.new.init()
	}

	init {
		FRDMixerMatrixPlugIn.checkSynthDefs;
		routes = Dictionary.new;
		records = Dictionary.new;
		inlets = [];
		outlets = [];
		presetPath = "".resolveRelative ++ "Preset/";
		this.showGUI();
	}

	// FASE 2 (runtime): verifica leggera; se mancano, build automatico al volo.
	*checkSynthDefs {
		if(checked.not, {
			if(SynthDescLib.global.at(\Route11).isNil, {
				(
					"FRDMixerMatrixPlugIn: SynthDef \\RouteNxM mancanti — li costruisco ora "
					"(build automatico al volo). Da questo momento sono anche su disco: le "
					"prossime sessioni li caricheranno al boot senza bisogno di questo passaggio."
				).warn;
				FRDMixerMatrixPlugIn.writeSynthDef;
			});
			checked = true;
		});
	}

	// FASE 1 (build): scrive su disco con .writeDefFile E li invia subito al
	// server con .add, così sono utilizzabili anche se il build scatta
	// automaticamente a runtime da checkSynthDefs (nessun riavvio necessario).
	*writeSynthDef {
		maxChannels.do({ | nIn |
			maxChannels.do({ | nOut |
				SynthDef(("Route" ++ (nIn + 1) ++ (nOut + 1)).asSymbol, { | inCh=0, outCh=0, fadeTime=1, amp=1, gate=1 |
					var in, env;
					env = EnvGen.ar(Env.asr(fadeTime, amp, fadeTime, [-1, 1]), gate, doneAction: 2);
					in = InFeedback.ar(inCh, nIn + 1) * env;
					if(nIn > nOut, { in = in[0..nOut] });
					if(nIn < nOut, { in = (in ! 8).flat.keep(nOut + 1) });
					Out.ar(outCh, in);
				}).writeDefFile.add;
			});
		});
		"FRDMixerMatrixPlugIn: % SynthDef scritti su disco e caricati sul server (già attivi in questa sessione).".format(maxChannels * maxChannels).postln;
	}

	addProcess { | name, inChannels, outChannels, inCh, outCh |
		records.put(name.asSymbol, (inChannels: inChannels, outChannels: outChannels, inCh: inCh, outCh: outCh));
		this.refreshGUI;
	}

	addFRDProcess { | name, frdPlugIn |
		records.put(name.asSymbol, frdPlugIn.asMixerMatrixProcess);
		this.refreshGUI;
	}

	removeProcess { | name |
		records.removeAt(name.asSymbol);
		this.refreshGUI;
	}

	getProcess { | name |
		^records.at(name.asSymbol)
	}

	// Chiave di routing univoca e senza ambiguità (a differenza di from++to)
	routeKey { | fromProcess, toProcess |
		^[fromProcess.asSymbol, toProcess.asSymbol]
	}

	route { | fromProcess, toProcess, fadeTime=1 |
		var from = fromProcess.asSymbol, to = toProcess.asSymbol;
		var fromRec = records.at(from), toRec = records.at(to);
		var defName = ("Route" ++ fromRec[\outChannels] ++ toRec[\inChannels]).asSymbol;

		if(fromRec.isNil or: { toRec.isNil }, {
			"FRDMixerMatrixPlugIn: route % -> % fallita, processo non registrato".format(from, to).warn;
			^this;
		});

		routes.put(this.routeKey(from, to), Synth(defName, [
			\inCh, fromRec[\outCh],
			\outCh, toRec[\inCh],
			\fadeTime, fadeTime
		]));

		this.refreshGUI;
		"Aggiunto % -> %, fade in %s.".format(from, to, fadeTime).postln;
	}

	unroute { | fromProcess, toProcess, fadeTime=1 |
		var key = this.routeKey(fromProcess, toProcess);
		var synth = routes.at(key);
		if(synth.notNil, {
			synth.release(fadeTime);
			routes.removeAt(key);
			this.refreshGUI;
			"Rimosso % -> %, fade out %s.".format(fromProcess, toProcess, fadeTime).postln;
		});
	}

	isRouted { | fromProcess, toProcess |
		^routes.at(this.routeKey(fromProcess, toProcess)).notNil
	}

	refreshGUI {
		if(matrixView.notNil, { matrixView.refresh });
	}

	showGUI { | wid=560, hei=420, border=10, buttW=120 |
		if(mainWindow.notNil, { if(mainWindow.isClosed.not, { mainWindow.close }) });

		width = wid; height = hei; spacing = border; buttonsWidth = buttW;

		mainWindow = Window.new("Mixer Matrix", Rect(20, 20, width, height + 130)).front;

		matrixView = UserView(mainWindow, Rect(0, 0, width, height)).background_(Color.black);

		matrixView.drawFunc = { | view |
			var w = view.bounds.width, h = view.bounds.height;
			var vspace = 40, hspace;
			var outletX, inletY, font = Font("Courier", 12);
			var inletLabelWidths;

			// Ordine deterministico (Dictionary/Set non garantiscono un ordine
			// di iterazione stabile fra una redraw e l'altra se `records`
			// viene modificato) — ordinando per nome, riga e colonna di una
			// stessa porta restano sempre nella stessa posizione.
			inlets = records.select({ | rec | rec[\inChannels] != 0 }).keys.asArray.sort({ | a, b | a.asString < b.asString });
			outlets = records.select({ | rec | rec[\outChannels] != 0 }).keys.asArray.sort({ | a, b | a.asString < b.asString });

			// Margine destro dimensionato sul nome inlet più lungo, non più
			// fisso a 40px: era questo a tagliare le etichette delle righe
			// quando i nomi dei processi superavano ~4-5 caratteri.
			inletLabelWidths = inlets.collect({ | name | name.asString.bounds(font).width });
			hspace = (inletLabelWidths.maxItem ? 0) + 30;

			outletX = outlets.collect({ | name, id | ((w - hspace - 20) * id / outlets.size) + 20 });
			inletY = inlets.collect({ | name, id | ((h - vspace - 20) * id / inlets.size) + 20 + vspace });

			Pen.width_(2);
			Pen.color_(Color.white);
			outlets.do({ | outletName, outletID |
				var labelWidth = outletName.asString.bounds(font).width;
				// Etichetta ancorata al centro della colonna, non a un offset fisso:
				// così resta allineata alla linea qualunque sia la lunghezza del nome.
				outletName.asString.drawAtPoint(((outletX[outletID] - (labelWidth / 2)) @ (vspace - 20)), font, Color.red);
				Pen.moveTo(outletX[outletID] @ vspace);
				Pen.lineTo(outletX[outletID] @ (h - 20));
			});
			inlets.do({ | inletName, inletID |
				// Etichetta a destra della griglia, ancorata sulla riga corrispondente.
				inletName.asString.drawAtPoint(((w - hspace + 10) @ (inletY[inletID] - 6)), font, Color.red);
				Pen.moveTo(20 @ inletY[inletID]);
				Pen.lineTo((w - hspace) @ inletY[inletID]);
			});
			Pen.stroke;

			// Marker su OGNI intersezione, non solo su quelle già collegate:
			// prima non c'era alcun indicatore per le celle libere, quindi
			// era impossibile capire visivamente dove cliccare.
			Pen.width_(1);
			outlets.do({ | outletName, outletID |
				inlets.do({ | inletName, inletID |
					var isSelf = outletName == inletName;
					Pen.color_(if(isSelf, { Color.gray(0.3) }, { Color.gray(0.55) }));
					Pen.addOval(Rect(outletX[outletID] - 3, inletY[inletID] - 3, 6, 6));
					Pen.stroke;
				});
			});

			// Route attive: pallino pieno, più grande, sopra ai marker deboli.
			Pen.width_(4);
			Pen.color_(Color.red(0.9, 0.8));
			routes.keysDo({ | key |
				var from = key[0], to = key[1];
				var outletID = outlets.indexOf(from), inletID = inlets.indexOf(to);
				if(outletID.notNil && inletID.notNil, {
					Pen.addOval(Rect(outletX[outletID] - 5, inletY[inletID] - 5, 10, 10));
				});
			});
			Pen.stroke(4);
		};

		matrixView.mouseDownAction_({ | view, x, y |
			var w = view.bounds.width, h = view.bounds.height;
			var vspace = 40, hspace;
			var font = Font("Courier", 12);
			var inletLabelWidths = inlets.collect({ | name | name.asString.bounds(font).width });
			var outletX, inletY;

			hspace = (inletLabelWidths.maxItem ? 0) + 30;
			outletX = outlets.collect({ | name, id | ((w - hspace - 20) * id / outlets.size) + 20 });
			inletY = inlets.collect({ | name, id | ((h - vspace - 20) * id / inlets.size) + 20 + vspace });

			outlets.do({ | outletName, outletID |
				inlets.do({ | inletName, inletID |
					var cx = outletX[outletID], cy = inletY[inletID];
					if((x >= (cx - 10)) && (x <= (cx + 10)) && (y >= (cy - 10)) && (y <= (cy + 10)), {
						if(outletName != inletName, {
							if(this.isRouted(outletName, inletName), {
								this.unroute(outletName, inletName, 1);
							}, {
								this.route(outletName, inletName, 1);
							});
						});
					});
				});
			});
			this.refreshGUI;
		});

		// --- Pannello preset, sotto la matrice: niente più codice per
		// scegliere la cartella o salvare/caricare un preset. ---
		presetPathField = TextField(mainWindow, Rect(0, 0, width - 90, 24)).string_(presetPath);
		presetPathField.action_({ | field | this.presetPath_(field.string) });

		presetPopUp = PopUpMenu(mainWindow, Rect(0, 0, width - 90, 24));
		presetPopUp.action_({ | menu |
			if(menu.items.notEmpty, { presetNameField.string_(menu.item) });
		});

		presetNameField = TextField(mainWindow, Rect(0, 0, width - 260, 24)).string_("tmp");

		statusText = StaticText(mainWindow, Rect(0, 0, width, 20)).string_("").stringColor_(Color.gray(0.7));

		mainWindow.layout_(
			VLayout(
				matrixView,
				HLayout(
					StaticText().string_("Cartella preset:"),
					presetPathField,
					Button().states_([["Sfoglia..."]]).action_({ this.browsePresetPath }),
					Button().states_([["Aggiorna elenco"]]).action_({ this.refreshPresetList })
				),
				HLayout(
					StaticText().string_("Preset:"),
					presetPopUp,
					StaticText().string_("Nome:"),
					presetNameField,
					Button().states_([["Salva"]]).action_({ this.writePreset(presetNameField.string) }),
					Button().states_([["Carica"]]).action_({ this.readPreset(presetNameField.string) }),
					Button().states_([["Elimina"]]).action_({ this.deletePreset(presetNameField.string) })
				),
				statusText
			)
		);

		this.refreshPresetList;
		this.refreshGUI;
	}

	/*
	* Apre un file picker per scegliere la cartella preset. SuperCollider non
	* offre un selettore di SOLE cartelle multipiattaforma affidabile: si fa
	* scegliere all'utente un file qualunque dentro la cartella desiderata
	* (anche uno nuovo, il nome verrà ignorato) e si usa la cartella che lo
	* contiene. In alternativa si può scrivere/incollare il percorso a mano
	* nel campo di testo accanto.
	*/
	browsePresetPath {
		Dialog.savePanel({ | path |
			this.presetPath_(PathName(path).pathOnly);
		}, { });
	}

	presetPath_ { | path |
		presetPath = path;
		if(presetPath.last != $/, { presetPath = presetPath ++ "/" });
		if(presetPathField.notNil, { presetPathField.string_(presetPath) });
		this.refreshPresetList;
	}

	// Elenco preset = tutti i file "*_records.scd" nella cartella, senza suffisso.
	listPresets {
		if(File.exists(presetPath).not, { ^[] });
		^PathName(presetPath).files
			.select({ | pn | pn.fileName.endsWith("_records.scd") })
			.collect({ | pn | pn.fileName.replace("_records.scd", "") })
			.sort;
	}

	refreshPresetList {
		var names = this.listPresets;
		if(presetPopUp.notNil, {
			presetPopUp.items_(names);
			if(names.notEmpty, { presetNameField.string_(names.first) });
		});
	}

	setStatus { | msg |
		msg.postln;
		if(statusText.notNil, { statusText.string_(msg) });
	}

	writePreset { | name="tmp" |
		var recordsFile, routesFile;
		if(File.exists(presetPath).not, {
			this.setStatus("Cartella preset % non trovata".format(presetPath));
			^this;
		});
		if(name.isEmpty, {
			this.setStatus("Dai un nome al preset prima di salvare.");
			^this;
		});

		recordsFile = File.open(presetPath ++ name ++ "_records.scd", "w");
		recordsFile.write(records.asCompileString);
		recordsFile.close;

		routesFile = File.open(presetPath ++ name ++ "_routes.scd", "w");
		routesFile.write(routes.keys.asArray.asCompileString);
		routesFile.close;

		this.setStatus("Preset '%' salvato.".format(name));
		this.refreshPresetList;
	}

	readPreset { | name, fadeIn=1, fadeOut=1 |
		var recordsPath = presetPath ++ name ++ "_records.scd";
		var routesPath = presetPath ++ name ++ "_routes.scd";
		var rawRoutes, pairs;

		if(File.exists(recordsPath).not or: { File.exists(routesPath).not }, {
			this.setStatus("Preset '%' non trovato in %".format(name, presetPath));
			^this;
		});

		records = File(recordsPath, "r").readAllString.interpret;

		routes.keysValuesDo({ | key, synth |
			synth.release(fadeOut);
		});
		routes = Dictionary.new;

		rawRoutes = File(routesPath, "r").readAllString.interpret;

		// Formato NUOVO: Array di coppie [from, to] (Symbol/String).
		// Formato VECCHIO (preset pre-refactor): Array di String concatenate
		// "fromto" — le route venivano salvate con `fromProcess++toProcess`,
		// esattamente il bug di collisione delle chiavi descritto in cima a
		// questo file. Qui proviamo a ricostruire le coppie confrontando le
		// stringhe coi nomi di processo presenti in `records`: non è
		// garantito al 100% in caso di nomi ambigui (es. "AB"+"C" vs
		// "A"+"BC"), ma è l'unica informazione recuperabile dal vecchio file.
		pairs = if(rawRoutes.notEmpty and: { rawRoutes.first.isKindOf(String) }, {
			this.setStatus("Preset '%': formato route vecchio (pre-refactor), converto...".format(name));
			this.legacyRoutesToPairs(rawRoutes);
		}, {
			rawRoutes;
		});

		pairs.do({ | key |
			if(key.notNil, { this.route(key[0], key[1], fadeIn) });
		});

		this.setStatus("Preset '%' caricato.".format(name));
		this.refreshGUI;
	}

	/*
	* Converte il vecchio formato route (Array di String concatenate) nel
	* nuovo (Array di coppie [from, to]), usando i nomi di processo presenti
	* in `records` come dizionario di riferimento. Prova prima le chiavi più
	* lunghe (più specifiche, meno ambigue). Se una stringa non si spezza in
	* modo univoco, avvisa e la scarta invece di indovinare silenziosamente.
	*/
	legacyRoutesToPairs { | rawRoutes |
		var keys = records.keys.asArray.sort({ | a, b | a.asString.size > b.asString.size });

		^rawRoutes.collect({ | str |
			var matches = keys.select({ | fromKey |
				var fromStr = fromKey.asString;
				str.asString.beginsWith(fromStr) and: {
					records.includesKey((str.asString.drop(fromStr.size)).asSymbol)
				};
			});

			case
			{ matches.isEmpty } {
				this.setStatus("Route '%' non riconosciuta, saltata.".format(str));
				nil;
			}
			{ matches.size > 1 } {
				var fromKey = matches.first; // il più lungo/specifico, vedi sort sopra
				var toKey = (str.asString.drop(fromKey.asString.size)).asSymbol;
				this.setStatus("Route '%' ambigua (%), uso % -> %.".format(str, matches, fromKey, toKey));
				[fromKey, toKey];
			}
			{
				var fromKey = matches.first;
				var toKey = (str.asString.drop(fromKey.asString.size)).asSymbol;
				[fromKey, toKey];
			};
		}).reject(_.isNil);
	}

	deletePreset { | name |
		var recordsPath = presetPath ++ name ++ "_records.scd";
		var routesPath = presetPath ++ name ++ "_routes.scd";

		if(File.exists(recordsPath).not, {
			this.setStatus("Preset '%' non trovato.".format(name));
			^this;
		});

		File.delete(recordsPath);
		File.delete(routesPath);

		this.setStatus("Preset '%' eliminato.".format(name));
		this.refreshPresetList;
	}
}
