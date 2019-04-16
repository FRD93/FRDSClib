/*
MixerMatrix
A matrix-like mixer plug-in for connecting synths
Copyright ©2018, Francesco Roberto Dani


*/

FRDMixerMatrixPlugIn {

	// System variables
	var routes, records, presetPath_r;
	// GUI variables
	var width, height, spacing, buttonsWidth, mainWindow;
	var inlets, outlets;

	*new {
		^super.new.init()
	}

	init {
		routes = Dictionary.new;
		records = Dictionary.new;
		inlets = Array.newClear();
		outlets =Array.newClear();
		presetPath_r = "".resolveRelative ++ "Preset/";
		this.showGUI();
	}

	addProcess { | name, inChannels, outChannels, inCh, outCh |
		records.put(name.asSymbol, Dictionary.new
			.put(\inChannels, inChannels)
			.put(\outChannels, outChannels)
			.put(\inCh, inCh)
			.put(\outCh, outCh)
		);
		if(mainWindow.class == Window, {mainWindow.refresh});
	}

	addFRDProcess { | name, frdPlugIn |
		records.put(name.asSymbol, frdPlugIn.asMixerMatrixProcess);
		if(mainWindow.class == Window, {mainWindow.refresh});
	}

	/*
	addProcessA { | parametersArray |
		records.put(parametersArray[0].asSymbol, Dictionary.new
			.put(\inChannels, parametersArray[1])
			.put(\outChannels, parametersArray[2])
			.put(\inCh, parametersArray[3])
			.put(\outCh, parametersArray[4])
		);
		if(mainWindow.class == Window, {mainWindow.refresh});
	}
	*/

	removeProcess { | name |
		records.removeAt(name);
		if(mainWindow.class == Window, {mainWindow.refresh});
	}

	getProcess { | name |
		^records.at(name)
	}

	route { | fromProcess, toProcess, fadeTime=1 |

		fromProcess = fromProcess.asSymbol;
		toProcess = toProcess.asSymbol;

		routes.put(fromProcess++toProcess, Synth(
			\Route++records.at(fromProcess).at(\outChannels)++records.at(toProcess).at(\inChannels), [
				\inCh, records.at(fromProcess).at(\outCh),
				\outCh, records.at(toProcess).at(\inCh),
				\fadeTime, fadeTime
		]));

		if(mainWindow.class == Window, {mainWindow.refresh});
		("Added" + ( fromProcess++toProcess ) ++ ", fade in in" + fadeTime + "seconds.").postln;
	}

	unroute { | fromProcess, toProcess, fadeTime=1 |
		routes.at(fromProcess++toProcess).postln;
		routes.at(fromProcess++toProcess).release( fadeTime );
		routes.removeAt(fromProcess++toProcess);
		if(mainWindow.class == Window, {mainWindow.refresh});
		("Removed" + ( fromProcess++toProcess ) ++ ", fade out in" + fadeTime + "seconds.").postln;
	}

	isRouted { | fromProcess, toProcess |
		if ( routes.at(fromProcess++toProcess) != nil, {
			^true
		}, {
			^false
		});
	}

	showGUI { | wid=300, hei=300, border=10, buttW=120 |

		if(mainWindow.class == Window, {mainWindow.close});

		width = wid;
		height = hei;
		spacing = border;
		buttonsWidth = buttW;

		mainWindow = Window.new("Mixer Matrix", Rect(20, 20, width, height)).background_( Color.black ).front;

		mainWindow.drawFunc = { | view |
			var width=view.bounds.width, height=view.bounds.height;
			var vspace=40, hspace=40;
			// Check if record has input and(or) output channels. If does, add to inlets/outlets respectively
			inlets = [];
			outlets = [];
			records.keysValuesDo({ | recordName, recordParams, index |
				if( records.at(recordName.asSymbol).at(\inChannels) != 0, {
					inlets = inlets.add( recordName );
				} );
				if( records.at(recordName.asSymbol).at(\outChannels) != 0, {
					outlets = outlets.add( recordName );
				} );
			});

			Pen.width_( 2 );
			Pen.color_( Color.white );
			outlets.do( { | outletName, outletID |
				// Draw names
				outletName.asString.drawAtPoint( ( ( ( width - hspace - 20 ) * outletID / outlets.size ) + 16 )@( vspace - 20 ), Font("Courier", 12), Color.red );
				// Draw grid lines
				Pen.moveTo( ( ( ( width - hspace - 20 ) * outletID / outlets.size ) + 20 )@vspace );
				Pen.lineTo( ( ( ( width - hspace - 20 ) * outletID / outlets.size ) + 20 )@( height-20 ) );
			} );
			inlets.do( { | inletName, inletID |
				// Draw names
				inletName.asString.drawAtPoint( ( width-hspace+10 )@( ( ( height - vspace - 20 ) * inletID / inlets.size ) + 10 + vspace ), Font("Courier", 12), Color.red );
				// Draw grid lines
				Pen.moveTo( 20@( ( ( height - vspace - 20 ) * inletID / inlets.size ) + 20 + vspace ) );
				Pen.lineTo( ( width-hspace )@( ( ( height - vspace - 20 ) * inletID / inlets.size ) + 20 + vspace ) );
			} );
			Pen.stroke;
			Pen.width_( 4 );
			Pen.color_( Color.red( 0.9, 0.8 ) );
			// Draw connections
			routes.keysDo( { | connection |
				inlets.do( { | inletName, inletID |
					outlets.do( { | outletName, outletID |
						if( connection == ( outletName++inletName ), {
							Pen.addOval( Rect( ( ( ( width - hspace - 20 ) * outletID / outlets.size ) + 20 - 5 ), ( ( ( height - vspace - 20 ) * inletID / inlets.size ) + 20 + vspace - 5 ), 10, 10 ) );
						} );
					} );
				} );
			} );
			Pen.stroke( 4 );

		};

		mainWindow.view.mouseDownAction_( { | view, x, y, modifiers, buttonNumber, clickCount |
			var width=view.bounds.width, height=view.bounds.height;
			var vspace=40, hspace=40;
			inlets.do( { | inletName, inletID |
				outlets.do( { | outletName, outletID |
					// Check if clicking on a cross
					if( ( x >= ( ( ( width - hspace - 20 ) * outletID / outlets.size ) + 20 - 10 ) ) && ( x <= ( ( ( width - hspace - 20 ) * outletID / outlets.size ) + 20 + 10 ) ) && ( y >= ( ( ( height - vspace - 20 ) * inletID / inlets.size ) + 20 + vspace - 10 ) ) && ( y <= ( ( ( height - vspace - 20 ) * inletID / inlets.size ) + 20 + vspace + 10 ) ), {
						// If unrouted, route, otherwise unroute
						if( this.isRouted( outletName.asSymbol, inletName.asSymbol ) == false, {
							// Check if not trying to route itself!
							 if(outletName.asSymbol != inletName.asSymbol, {
								this.route( outletName.asSymbol, inletName.asSymbol, 1 );
							});

						}, {
							this.unroute( outletName.asSymbol, inletName.asSymbol, 1 );
						});
					} );
				} );
			} );
			mainWindow.refresh();
		} );

		mainWindow.refresh();

	}

	writeDefFile {
		8.do( { | nIn |
			8.do( { | nOut |
				SynthDef( \Route++(nIn+1)++(nOut+1), { | inCh=0, outCh=0, fadeTime=1, amp=1, gate=1 |
					var in, out, env;
					//env = EnvGen.ar( Env.adsr( fadeTime, 0, 1, fadeTime ), gate, doneAction: 2 );
					//env = EnvGen.ar(Env.new([0, 1, 1, 0], [fadeTime, 1, fadeTime], [-1, 0, 1], 2), gate, doneAction: 2);
					env = EnvGen.ar(Env.asr(fadeTime, amp, fadeTime, [-1, 1]), gate, doneAction: 2);
					in = InFeedback.ar( inCh, (nIn+1) ) * env;
					if( nIn > nOut, {
						in = in[0..nOut];
					} );
					if( nIn < nOut, {
						in = ( in ! 8 ).flat.keep( nOut+1 );
					} );
					Out.ar( outCh, in );
				} ).writeDefFile.add;
			} );
		} );
	}




		presetPath {
		^presetPath_r
	}
	presetPath_ { | presetPath |
		presetPath_r = presetPath;
		^presetPath_r
	}




	writePreset { | name="tmp" |
		var tmp;
		tmp = File.open( presetPath_r ++ name ++ "_records.scd", "w" );
		tmp.write( "Dictionary.new" );
		records.keysValuesDo( { | recordName, recordValue |
			tmp.write( ".put( \\" ++ recordName ++ ", Dictionary.new" );
			records[recordName].keysValuesDo( { | recordPName, recordPValue |
				tmp.write( ".put( \\" ++ recordPName ++ ", " ++ recordPValue ++ " ) " );
			} );
			tmp.write( ") " );
		});
		tmp.close();

		tmp = File.open( presetPath_r ++ name ++ "_routes.scd", "w" );
		tmp.write( "[ " );
		routes.keysDo( { | route |
			tmp.write( "\"" ++ route ++ "\", " );
		});
		tmp.write( " ]" );
		tmp.close();
	}




	readPreset { | name, fadeIn=1, fadeOut=1 |
		// Open new records file
		records = File.open( presetPath_r ++ name ++ "_records.scd", "r" ).readAllString.interpret;
		// Remove all existing routes
		routes.keysValuesDo( { | name, synth |
			synth.release(fadeOut);
			("Removed" + name ++ ", fade out in" + fadeOut + "seconds.").postln;
		} );
		File.open( presetPath_r ++ name ++ "_routes.scd", "r" ).readAllString.interpret.do( { | route |
			var from, to;
			records.keysDo( { | name |
				name = name.asString;
				if( route.find( name.asString ) != nil, {
					if( route.find( name ) == 0, {
						from = name;
					}, {
						to = name;
					} );
				} );
			} );
			this.route( from, to, fadeIn );
		} );
	}

}




