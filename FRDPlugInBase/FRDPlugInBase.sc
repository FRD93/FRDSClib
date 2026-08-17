/*
FRDPlugInBase
Classe base astratta per la libreria di plugin FRD*.
Copyright (c) Francesco Roberto Dani

Scopo:
- Eliminare la duplicazione di boilerplate presente in tutte le sottoclassi
  (FRDBufferGranulator, FRDContinuousBusGranulator, FRDConvolutionPlugIn, ...):
  gestione outCh/addAction/actionNode, GUI con finestra standard, flag hasGUI.
- Risolvere il bug ricorrente per cui `writeSynthDef` era un metodo di ISTANZA
  mai chiamato da nessuna parte: nessun .scsyndef veniva mai scritto su disco,
  quindi al boot del server il SynthDef non esisteva finché non lo si
  richiamava a mano.

IMPORTANTE - modello di compilazione a due fasi:
1) BUILD (manuale, quando scrivi o modifichi un synthdef): chiami
   `FRDPlugInBase.buildSynthDefs(LaClasse)`, che esegue `*writeSynthDef`
   scrivendo i .scsyndef su disco con `.writeDefFile` E inviandoli subito
   al server con `.add`. Da rifare solo se cambi il codice del synthdef,
   non ad ogni sessione — ma non è più strettamente necessario farlo a
   mano, vedi punto 2.
2) RUNTIME (ogni volta che avvii il server/il progetto): SuperCollider
   carica automaticamente tutti i .scsyndef dalla cartella synthdefs al
   boot del server — nessuna ricompilazione, nessuna latenza, in questo
   caso comune. `FRDPlugInBase.checkSynthDefs` verifica che i def attesi
   siano già in SynthDescLib; se manca qualcosa (es. prima esecuzione in
   assoluto, prima ancora di aver mai fatto il build), lo costruisce al
   volo in automatico chiamando `*writeSynthDef` — nessun riavvio richiesto,
   funziona già in questa sessione. Da quel momento il file è anche su
   disco, quindi le sessioni successive lo troveranno già caricato al boot
   e questo controllo tornerà a essere un no-op.

Nota sul nome del metodo: si chiama `writeSynthDef` (non `defineSynthDefs` o
`buildSynthDefs`) per coerenza con le classi esistenti della libreria — ma è
ora un metodo di CLASSE (`*writeSynthDef`), non di istanza, e va chiamato
una volta sola quando scrivi/modifichi un synthdef, non ad ogni sessione.
Non va confuso con `SynthDef#writeDefFile` (di Synth/SynthDef), che è il
metodo di libreria SC usato internamente per scrivere davvero il file.

Come usarla in una sottoclasse:

FRDEsempio : FRDPlugInBase {
	var <inCh;

	*new { | inCh=20, outCh=0, addAction=\addToHead, actionNode=1 |
		^super.new.initBase(outCh, addAction, actionNode).initEsempio(inCh)
	}

	initEsempio { | argInCh |
		FRDPlugInBase.checkSynthDefs(this.class);
		inCh = argInCh;
	}

	inCh_ { | val |
		inCh = val;
		this.onParamChanged(\inCh, val);
	}

	// hook chiamato da outCh_ / dai setter dei parametri quando c'è un synth vivo da aggiornare
	onParamChanged { | key, val |
		if(synth.notNil, { synth.set(key, val) });
	}

	asMixerMatrixProcess {
		^(inChannels: 2, outChannels: 2, inCh: inCh, outCh: outCh)
	}

	*synthDefNames { ^[\FRDEsempioSynth] }
	*writeSynthDef {
		SynthDef(\FRDEsempioSynth, { ... }).writeDefFile.add;
	}
}

Per compilare/scrivere su disco i synthdef di tutte le classi della libreria
dopo averle scritte o modificate (una tantum, non ad ogni avvio):

	FRDPlugInBase.buildSynthDefs(FRDContinuousBusGranulator);
	FRDMixerMatrixPlugIn.writeSynthDef; // caso a parte, vedi nota nel suo file

Poi riavvia il server: SuperCollider carica i .scsyndef dalla cartella
synthdefs in automatico, senza bisogno di richiamare nulla a runtime.
*/

FRDPlugInBase {

	classvar <checkedClasses; // Set di classi già verificate in questa sessione (evita di ricontrollare ad ogni new)

	var <outCh, <addAction, <actionNode;
	var <hasGUI = false;
	var <window;

	*initClass {
		checkedClasses = Set.new;
	}

	// Da chiamare come PRIMA cosa nel `*new` di ogni sottoclasse.
	initBase { | argOutCh=0, argAddAction=\addToHead, argActionNode=1 |
		outCh = argOutCh;
		addAction = argAddAction;
		actionNode = argActionNode;
		^this
	}

	/*
	* FASE 2 (runtime, leggera): verifica che i SynthDef attesi dalla classe
	* siano già in SynthDescLib (cioè caricati dal server al boot dai
	* .scsyndef su disco). Se manca qualcosa, esegue il build al volo
	* (scrive su disco E invia subito al server con `.add`, così funziona
	* già in questa sessione senza dover riavviare) tramite `*writeSynthDef`.
	* Da quel momento in poi il .scsyndef è su disco: le sessioni future lo
	* troveranno già caricato al boot e questo controllo diventerà un no-op
	* — nessuna latenza a regime, nessun passaggio manuale.
	* Verificato/costruito una sola volta per classe per sessione.
	*/
	*checkSynthDefs { | aClass |
		if(checkedClasses.includes(aClass).not, {
			if(aClass.respondsTo(\synthDefNames), {
				var missing = aClass.synthDefNames.select({ | defName | SynthDescLib.global.at(defName).isNil });
				if(missing.notEmpty, {
					(
						"% : SynthDef mancanti (%) — li costruisco ora (build automatico "
						"al volo). Da questo momento sono anche su disco: le prossime "
						"sessioni li caricheranno al boot senza bisogno di questo passaggio."
					).format(aClass.name, missing).warn;
					FRDPlugInBase.buildSynthDefs(aClass);
				});
			});
			checkedClasses.add(aClass);
		});
	}

	/*
	* FASE 1 (build, una tantum): scrive su disco i .scsyndef della classe
	* tramite *writeSynthDef (che ogni sottoclasse implementa usando
	* `.writeDefFile`, SENZA `.add`: non serve inviarli al server adesso,
	* verranno caricati automaticamente al prossimo boot). Da richiamare a
	* mano solo quando scrivi o modifichi un synthdef, non ad ogni avvio.
	*/
	*buildSynthDefs { | aClass |
		if(aClass.respondsTo(\writeSynthDef), {
			aClass.writeSynthDef;
			("% : SynthDef scritti su disco e caricati sul server (già attivi in questa sessione).").format(aClass.name).postln;
		}, {
			Error("% deve implementare *writeSynthDef".format(aClass.name)).throw;
		});
	}

	// outCh (canale di output) — comune a tutti i plugin
	outCh_ { | val |
		outCh = val;
		this.onParamChanged(\outCh, val);
		this.refreshGUIField(\outCh, val);
	}

	addAction_ { | val | addAction = val }
	actionNode_ { | val | actionNode = val }

	/*
	* Hook da sovrascrivere nelle sottoclassi: se esiste un Synth persistente
	* che deve ricevere .set(key, val) quando un parametro cambia, farlo qui.
	* Di default non fa nulla (alcuni plugin, es. FRDBufferGranulator, non
	* hanno un synth persistente da aggiornare).
	*/
	onParamChanged { | key, val | }

	// Hook per aggiornare il widget GUI corrispondente, se la finestra è aperta.
	// Le sottoclassi tengono le proprie reference ai widget e implementano questo.
	refreshGUIField { | key, val | }

	/*
	* Da sovrascrivere: ogni plugin deve poter descrivere se stesso per
	* l'integrazione con FRDMixerMatrixPlugIn.
	*/
	asMixerMatrixProcess {
		^this.subclassResponsibility(\asMixerMatrixProcess)
	}

	/*
	* Costruisce una finestra standard: colonna di controlli a sinistra,
	* colonne aggiuntive (es. slider) a destra. Le sottoclassi passano i
	* propri widget già istanziati; qui si gestisce solo il layout/apertura/chiusura.
	*/
	buildWindow { | title, leftWidgets, rightColumns=#[] |
		hasGUI = true;
		window = Window(title, Rect(width: 20, height: 20)).onClose_({ hasGUI = false });
		window.layout_(
			HLayout(VLayout(*leftWidgets), *rightColumns)
		);
		window.front;
		^window
	}

	closeGUI {
		if(window.notNil, { window.close });
	}
}
