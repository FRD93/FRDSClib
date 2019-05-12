/*
FRDSpatPlugIn
N-Ch Spatializer
Copyright ©2018, Francesco Roberto Dani
*/

FRDSpatPlugIn {

	// System variables
	var dict, refresh_rout, presetPath_s, temp, window;
	var window, drawView, width, height;

	*new { | inCh=2, outCh=0, numCh=2 |
		^super.new.init( inCh, outCh, numCh )
	}

	init { | inCh=2, outCh=0, numCh=2 |
		var window;
		var view;
		var ppm=20;//pixel per metro
		var thresh=2;//massimo per agganciare
		var loudtrack;
		var sourcetrack;
		var update;

		dict=Dictionary.new;
		dict.put(\H,[24,45]); //dimensioni sala in metri
		dict.put(\L,[[2.5,2.5],[21.5,2.5],[1,14.5],[23,14.5],[1,30.5],[23,30.5],[2.5,40.5],[21.5,40.5]]);
		//coordinate in metri dei diffusori con origine in alto a sinistra
		dict.put(\S,[[5, 3, \First, \wrap],[12, 4, \Second, \fold]]); // coordinate in metri delle sorgenti
		dict.put(\P,[[1000,1001],[1003]]); // processi audio
		dict.put(\T,Dictionary.new); // lista delle traiettorie
		dict.put(\R,Dictionary.new); // lista delle routine di traiettorie


		window=GUI.window.new(\spat,Rect(100,100,dict[\H].first*ppm,dict[\H].last*ppm));
		window.front;
		view=UserView.new(window,Rect(0,0,window.bounds.width,window.bounds.height));
		view.mouseDownAction_({|uv,x,y|
			var distances;
			// Check se sto toccando un altoparlante
			distances=(dict[\L].flop-([x,y]/ppm)).flop.pow(2).flop.sum.sqrt;
			if(Array.newFrom(distances).sort.first<thresh,{
				loudtrack=distances.order.first;
			});
			// Check se sto toccando una sorgente
			distances=(dict[\S].flop.keep(2)-([x,y]/ppm)).flop.pow(2).flop.sum.sqrt;
			if(Array.newFrom(distances).sort.first<thresh,{
				sourcetrack=distances.order.first;
				temp=[];
			});
		});
		view.mouseUpAction_({
			if(sourcetrack != nil, {
				// Aggiungo il tracciato a dict[\T] (da fare!)
				dict[\T].put(sourcetrack, temp);
				// Animazione??
				try({dict[\R].at(sourcetrack).free});
				dict[\R].put(sourcetrack, {
					var mysource = sourcetrack;
					Routine({
						mysource.postln;
						inf.do({ | id |
							if(dict[\S][mysource][3] == \wrap, {
								dict[\S][mysource]= dict[\T][mysource].wrapAt(id) ++ dict[\S][mysource][2..];
							});
							if(dict[\S][mysource][3] == \fold, {
								dict[\S][mysource]= dict[\T][mysource].foldAt(id) ++ dict[\S][mysource][2..];
							});
							if(dict[\S][mysource][3] == \spot, {
								// do nothing...
							});
							update.value;
							window.refresh;
							0.01.wait;
						});
						/*
						dict[\T][mysource].do({|xy|
							xy.postln;
							dict[\S][mysource]=xy ++ dict[\S][mysource][2..];
							window.refresh;
							0.01.wait;
						});*/
					}).play(AppClock);
				}.value);
				sourcetrack=nil;
			});
			if(loudtrack != nil, {
				loudtrack=nil;
			})
		});
		view.mouseMoveAction_({|mm,x,y|
			// Se altoparlante, spostalo
			if(loudtrack!=nil,{
				dict[\L][loudtrack]=[x,y]/ppm;
				//window.refresh;
				update.value;
			});
			// Se sorgente, sposta e registra nel tracciato
			if(sourcetrack!=nil,{
				dict[\S][sourcetrack]=([x,y]/ppm) ++ dict[\S][sourcetrack][2..];
				temp=temp.add([x,y]/ppm);
				//window.refresh;
				update.value;
			});
		});
		window.drawFunc_({
			Pen.color_(Color.blue);
			//	Pen.fillRect(Rect(dict[\C].first*ppm-5,dict[\C].last*ppm-5,10,10));
			dict[\L].do({|coord,counter|
				coord=coord[0..1]*ppm;
				Pen.color_(Color.black);
				Pen.font_(Font.new("Arial",12));
				Pen.stringAtPoint((counter+1).asString,(coord[0]+7)@coord.last);
				Pen.fillRect(Rect(coord[0]-5,coord[1]-5,10,10));
				Pen.color_(Color.red);
				dict[\GG].flop[counter].do({|gain,gcounter|
					Pen.stringAtPoint(gain.asString,(coord[0]-7)@(coord[1]+12+(gcounter*12)));
				});
			});
			dict[\S].do({|coord,counter|
				var name = coord[2].asString;
				coord=coord[0..1]*ppm;
				Pen.color_(Color.red);
				Pen.font_(Font.new("Arial",12));
				Pen.stringAtPoint(name,(coord[0]+7)@coord[1]);
				Pen.fillOval(Rect(coord[0]-5,coord[1]-5,10,10));
			});
		});
		update={
			var all_distances;
			var a;
			var r=6.neg;//free field
			var k;
			var gain;
			all_distances=dict[\S].collect({|coord|
				(dict[\L].flop-coord[0..1]).flop.pow(2).flop.sum.sqrt;
			});
			a=10.pow(r/20);
			k=all_distances.collect({|distances|distances.pow(2).reciprocal.sum.sqrt.reciprocal*2*a});
			gain=all_distances.collect({|distances,counter|k[counter]/(2*distances*a)});
			dict.put(\G,gain);
			dict.put(\GG,gain.ampdb.round(0.1));
		};
		update.value;

		refresh_rout = Routine{
			loop{
				window.refresh;
				0.05.wait;
			}
		}.play(AppClock);
	}


	refreshGUI {
		refresh_rout = Routine{
			loop{
				window.refresh;
				0.05.wait;
			}
		}.play(AppClock);
	}


	presetPath {
		^presetPath_s
	}

	presetPath_ { | presetPath |
		presetPath_s = presetPath;
	}






	writeDefFile {
		SynthDef( \Spat2, { | inCh=0, outCh=0, gains=#[0,0], lagT=1 |
			var in = InFeedback.ar( inCh, 1 );
			Out.ar( outCh, in * Lag.ar( K2A.ar( gains ), lagT ) );
		} ).writeDefFile.add;
		SynthDef( \Spat3, { | inCh=0, outCh=0, gains=#[0,0,0], lagT=1 |
			var in = InFeedback.ar( inCh, 1 );
			Out.ar( outCh, in * Lag.ar( K2A.ar( gains ), lagT ) );
		} ).writeDefFile.add;
		SynthDef( \Spat4, { | inCh=0, outCh=0, gains=#[0,0,0,0], lagT=1 |
			var in = InFeedback.ar( inCh, 1 );
			Out.ar( outCh, in * Lag.ar( K2A.ar( gains ), lagT ) );
		} ).writeDefFile.add;
		SynthDef( \Spat5, { | inCh=0, outCh=0, gains=#[0,0,0,0,0], lagT=1 |
			var in = InFeedback.ar( inCh, 1 );
			Out.ar( outCh, in * Lag.ar( K2A.ar( gains ), lagT ) );
		} ).writeDefFile.add;
		SynthDef( \Spat6, { | inCh=0, outCh=0, gains=#[0,0,0,0,0,0], lagT=1 |
			var in = InFeedback.ar( inCh, 1 );
			Out.ar( outCh, in * Lag.ar( K2A.ar( gains ), lagT ) );
		} ).writeDefFile.add;
		SynthDef( \Spat7, { | inCh=0, outCh=0, gains=#[0,0,0,0,0,0,0], lagT=1 |
			var in = InFeedback.ar( inCh, 1 );
			Out.ar( outCh, in * Lag.ar( K2A.ar( gains ), lagT ) );
		} ).writeDefFile.add;
		SynthDef( \Spat8, { | inCh=0, outCh=0, gains=#[0,0,0,0,0,0,0,0], lagT=1 |
			var in = InFeedback.ar( inCh, 1 );
			Out.ar( outCh, in * Lag.ar( K2A.ar( gains ), lagT ) );
		} ).writeDefFile.add;
	}

	writePresetFile { | name="tmp" |
		var tmp;
		tmp = File.open( presetPath_s ++ name ++ ".scd", "w" );
		tmp.write( "Dictionary.new" );
		dict.keysValuesDo( { | key, val |
			tmp.write( ".put( \\" ++ key ++ ", " ++ val ++ " ) " );
		});
		tmp.close();

	}

	readPresetFile { | name |
		var tmpNCH;
		dict = File.open( presetPath_s ++ name ++ ".scd", "r" ).readAllString.interpret;
		tmpNCH = dict.at( \numCh );
		dict.put( \numCh, 99 );
		this.inCh_( dict.at( \inCh ) );
		this.outCh_( dict.at( \outCh ) );
		this.delT_( dict.at( \delT ) );
		this.delF_( dict.at( \delF ) );
		this.lagT_( dict.at( \lagT ) );
		this.amp_( dict.at( \amp ) );
		this.do_( dict.at( \do ) );
		this.numCh_( tmpNCH );
	}

}








