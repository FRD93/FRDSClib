FRDSequencer {

	var patterns, patternObjects, addPatternButton, savePatternChainButton, loadPatternChainButton, scroll, canvas, layout;
	var thisPath;
	var sampleArgs_InstrArgs;

	*new {
		^super.new.init()
	}

	// init method
	init {
		sampleArgs_InstrArgs = [
			[ \Kick,     [ \amp, \dur, \midiInit, \midiEnd, \outBus ], [1, 0.35, 127, 31, 0] ],
			[ \KickTone, [ \freq, \dur, \amp, \dist, \atk, \outBus ], [55, 0.8, 1, 2, 0.01, 0] ],
			[ \Snare,    [ \amp, \dur, \midiInit, \midiEnd, \outBus ], [1, 0.4, 88, 33, 0] ],
			[ \Clap,     [ \amp, \shift, \body, \outBus ], [1, 1, 1, 0] ],
			[ \Hat,      [ \amp, \hpf, \shift, \outBus ], [1, 18000, 1, 0] ],
//			[ \Harmonic, [ \freq, \amp, \dur, \atk, \durcoeff, \pan, \outCh ], [128, 1, 0.8, 0.01, 0.1, 0, 0] ],
//			[ \FMNoise,  [ \dur, \amp, \fcar, \fmod, \fmod2, \fmod3, \imod, \imod2, \imod3, \outBus ] ],
//			[ \FMNoise2, [ \dur, \amp, \fcar, \fmod, \fmod2, \fmod3, \imod, \imod2, \imod3, \outBus ] ],
		];

		thisPath = "".resolveRelative;
	}

	showGUI {
		// Pattern management

		// Add Pattern
		addPatternButton = Button().states_( [ [ "Add Pattern" ] ] ).action_( {
			var window = Window.new( "Add Pattern" ).alwaysOnTop_( true ).front;
			// Make a new pattern
			var newButton = Button().states_( [ [ "Make new Pattern" ] ] ).action_( {
				var numBeats = 4, resolution = 4, repetitions = 1;
				var newWindow = Window.new( "Make new Pattern" ).alwaysOnTop_( true ).front;
				var numBeatsButton = NumberBox().action_( { | val |
					numBeats = val.value.asInteger;
				} ).valueAction_( numBeats );
				var resolutionButton = NumberBox().action_( { | val |
					resolution = val.value.asInteger;
					resolution.postln;
				} ).valueAction_( resolution );
				var repetitionButton = NumberBox().action_( { | val |
					repetitions = val.value.asInteger;
				} ).valueAction_( repetitions );
				var processButton = Button().states_( [ [ "Create" ] ] ).action_( {
					var pattern = this.addPattern( sampleArgs_InstrArgs, numBeats, resolution, repetitions, nil );
					// Add pattern's view
					patterns.add( pattern.view );
					// Add pattern to pattern list
					//patternObjects.insert( index, pattern );
					patternObjects = patternObjects.add( pattern );
					newWindow.close();
				} );
				newWindow.view.layout = VLayout(
					HLayout( StaticText().string_( "Num Beats:" ), numBeatsButton ),
					HLayout( StaticText().string_( "Resolution:" ), resolutionButton ),
					HLayout( StaticText().string_( "Number of repeats:" ), repetitionButton ),
					processButton
				);
				window.close;
			} );
			// Choose a pattern from saved presets
			var fromFileButton = Button().states_( [ [ "Choose from file" ] ] ).action_( {
				Dialog.openPanel( { | filePath |
					var pattern = this.addPattern( sampleArgs_InstrArgs, 4, 4, 4, filePath );
					// Add pattern's view
					patterns.add( pattern.view );
					// Add pattern to pattern list
					patternObjects = patternObjects.add( pattern );
					window.close;
				}, path: thisPath ++ "preset/" );
			} );
			window.view.layout = VLayout(
				newButton, fromFileButton
			);
		} );


		// Save Pattern Chain
		savePatternChainButton = Button().states_( [ [ "Save Pattern Chain" ] ] ).action_( {
			Dialog.savePanel( { | filePath |
				var file = File.new( filePath, "w" );
				file.write( "[" );
				patternObjects.do( { | pattern |
					file.write( "\"" ++ pattern.name.asString ++ "\", " );
				}, path: thisPath );
				file.write( "];");
				file.close;
			} );
		} );


		// Load Pattern Chain
		loadPatternChainButton = Button().states_( [ [ "Load Pattern Chain" ] ] ).action_( {
			Dialog.openPanel( { | filePath |
				var directory = PathName( filePath ).pathOnly;
				var names = File.new( filePath, "r" ).readAllString.interpret.value;
				patternObjects.do( { | pattern |
					pattern.view.remove;
				} );
				patternObjects = Array.newClear();
				names.do( { | name |
					var pattern;
					pattern = this.addPattern( sampleArgs_InstrArgs, 4, 4, 4, ( "" ++ directory ++ name ++ ".pattern" ).asString );
					// Add pattern's view
					patterns.add( pattern.view );
					// Add pattern to pattern list
					patternObjects = patternObjects.add( pattern );
				} );
			}, path: thisPath );
		} );

		// Main GUI variables
		scroll = ScrollView.new( bounds:Rect( 0, 0, Window.screenBounds.width, 300 ) );
		canvas = View();
		layout = VLayout(
			HLayout(
				addPatternButton, savePatternChainButton, loadPatternChainButton, nil
			),
			//patternsView
			HLayout()
		);
		layout.add( View() );
		canvas.layout = layout;
		scroll.canvas = canvas;
		patterns = canvas.layout.children[ 1 ];
		scroll.alwaysOnTop_( true ).front;


		// Pattern objects
		patternObjects = Array.newClear( 0 );

	}










	addPattern { | myInstrArgs, beats, resolution, repetitions, presetName=nil |

		var pattern = ();

		pattern.instrArgs = myInstrArgs.copy;
		pattern.numbeats = beats.copy;
		pattern.resolution = resolution.copy;
		pattern.repetitions = repetitions.copy;


		pattern.view = View().background_( Color.rand );


		pattern.textFuncs = Array.fill( pattern.numbeats * pattern.resolution, { | bID |
			Array.fill( pattern.instrArgs.size, { | iID |
				Array.fill( pattern.instrArgs[ iID ][ 1 ].size, { | id |
					"{ | onsetID, repetitionID |\n%\n}".format(pattern.instrArgs[iID][2][id]);
				} );
			} );
		} );

		pattern.onsets = Array.fill( pattern.numbeats * pattern.resolution, { | bID |
			Array.fill( pattern.instrArgs.size, { | iID |
				0;
			} );
		} );

		pattern.onsets.size.postln;

		// Load preset if needed
		if( presetName != nil, {
			var return;
			pattern.name = PathName( presetName ).fileNameWithoutExtension;
			return = File.new( presetName, "r" ).readAllString.interpret.value;
			pattern.instrArgs = return[ 0 ];
			pattern.onsets = return[ 1 ];
			pattern.textFuncs = return[ 2 ];
			pattern.numbeats = return[ 3 ];
			pattern.resolution = return[ 4 ];
			pattern.repetitions = return[ 5 ];
		}, {
			pattern.name = "Unsaved";
		} );

		pattern.drawPatternView = { | this |
			pattern.removeButton = Button().states_( [ [ "Remove" ] ] ).mouseDownAction_( { pattern.view.remove } );

			pattern.addInstrButton = Button().states_( [ [ "Add Instr" ] ] ).action_( {
				var window = Window.new("Add Instrument").alwaysOnTop_( true ).front;
				var text = TextView().string_( "[ \\NAME, [ \\PARAM1, \\PARAM2, \\etc ], [val1, val2, etc] ]" );
				var addBtn = Button().states_( [ [ "Add" ] ] ).action_( {
					if( text.string.interpret.class == Array, {
						// Add instr to instrArgs
						pattern.instrArgs = pattern.instrArgs.add( text.string.interpret );
						// Add instr to textFuncs
						pattern.textFuncs.do( { | beatFuncs, bID |
							beatFuncs.do( { | instrFunc, iID |
								//var numArgs = text.string[1].findAll( "," ).size + 1;
								var numArgs = text.string.interpret[1].size;
								var argStrings = numArgs.collect({|argID|
									"{ | onsetID, repetitionID |\n%\n}".format(text.string.interpret[2][argID]);
								});
								//pattern.textFuncs[ bID ] = pattern.textFuncs.at( bID ).add( "{ | index |\n1\n}" ! numArgs );
								pattern.textFuncs[ bID ] = pattern.textFuncs.at( bID ).add( argStrings );
							} );
						} );
						// Add instr to onsets
						pattern.onsets.do( { | beatOnsets, bID |
							beatOnsets.do( { | instrOnset, iID |
								pattern.onsets[ bID ] = pattern.onsets.at( bID ).add( 0 );
							} );
						} );
						// Redraw parent view
						pattern.view.children.do( { | child | child.remove } );
						pattern.drawPatternView.value();
						// Close window
						window.close;
					}, { "Wrong string... retry!".postln; } );
				} );
				window.view.layout = VLayout( text, addBtn );
			} );

			pattern.removeInstrButton = Button().states_( [ [ "Remove Instr" ] ] ).action_( {
				var window = Window.new("Remove Instrument").alwaysOnTop_( true ).front;
				var instrs = pattern.instrArgs.collect( { | instrArg, instrID |
					Button().states_( [ [ instrArg[ 0 ].asString ] ] ).action_( {
						// Remove instrument from instrArgs
						pattern.instrArgs.removeAt( instrID );
						// Remove instrument's textFuncs from textFuncs
						pattern.textFuncs.do( { | beatFuncs, bID |
							beatFuncs.do( { | instrFunc, iID |
								if( iID == instrID, {
									pattern.textFuncs.at( bID ).removeAt( iID );
								} );
							} );
						} );
						// Remove instrument's onsets from onsets
						pattern.onsets.do( { | beatOnsets, bID |
							beatOnsets.do( { | instrOnset, iID |
								if( iID == instrID, {
									pattern.onsets.at( bID ).removeAt( iID );
								} );
							} );
						} );
						// Redraw parent view
						pattern.view.children.do( { | child | child.remove } );
						pattern.drawPatternView.value();
						// Close window
						window.close;
					} );
				} );
				window.view.layout = VLayout( *instrs );
			} );

			pattern.saveButton = Button().states_( [ [ "Save to file" ] ] ).action_( {
				Dialog.savePanel( { | fileName |
					var file = File.new( fileName, "w" );

					pattern.name = PathName( fileName ).fileNameWithoutExtension;

					// Write beats
					file.write( "var beats = " ++ pattern.numbeats ++ ";\n" );

					// Write resolution
					file.write( "var resolution = " ++ pattern.resolution ++ ";\n" );

					// Write repetitions
					file.write( "var repetitions = " ++ pattern.repetitions ++ ";\n" );

					// Write instrArgs
					file.write( "var instrArgs = [ " );
					pattern.instrArgs.do( { | instrArg |
						file.write( "[ \\" ++ instrArg[ 0 ] ++ " , [ " );
						instrArg[ 1 ].do( { | argo |
							file.write( "\\" ++ argo ++ " , " );
						} );
						file.write( "] ],\n" );
					} );
					file.write( "];\n" );

					// Write onsets
					file.write( "var onsets = [ " );
					pattern.onsets.do( { | onset |
						file.write( onset.asString );
						file.write( ",\n" );
					} );
					file.write( "];\n" );

					// Write textFuncs
					file.write( "var textFuncs = [ " );
					pattern.textFuncs.do( { | textFunc |
						file.write( "[\n" );
						textFunc.do( { | singleTextFunc, id |
							file.write( "[\n" );
							singleTextFunc.do( { | singleArgTextFunc |
								file.write( "\"" ++ singleArgTextFunc.asString ++ "\",\n" );
							} );
							file.write( "],\n" );
						} );
						file.write( "],\n" );
					} );
					file.write( "];\n" );

					// Write returns
					file.write( "[ instrArgs, onsets, textFuncs, beats, resolution, repetitions ]" );

					// Close file
					file.close;
				}, path: thisPath );

			} );

			pattern.loadButton = Button().states_( [ [ "Load from file" ] ] ).action_( {
				Dialog.openPanel( { | fileName |
					var return;
					pattern.name = PathName( fileName ).fileNameWithoutExtension;
					return = File.new( fileName, "r" ).readAllString.interpret.value;
					pattern.instrArgs = return[ 0 ];
					pattern.onsets = return[ 1 ];
					pattern.textFuncs = return[ 2 ];
					pattern.numbeats = return[ 3 ];
					pattern.resolution = return[ 4 ];
					pattern.repetitions = return [ 5 ];
					pattern.view.children.do( { | child | child.remove } );
					pattern.drawPatternView.value();
				}, path: thisPath );
			} );


			pattern.randomizeButton = Button().states_([["Randomize"]]).action_({
				pattern.buttons .do({|butts|
					butts.do({|butt|
						butt.valueAction_(0.5.coin.asInteger);
					});
				});
			});

			pattern.gameOfLifeButton = Button().states_([["GOL", Color.black, Color.red], ["GOL", Color.black, Color.green]]).valueAction_(0);

			pattern.labels = pattern.instrArgs.collect( { | instrArg, instrID |
				StaticText().string_( instrArg[ 0 ] ).align_( \right );
			} );

			pattern.buttons = pattern.instrArgs.collect( { | instrArg, instrID |
				(pattern.numbeats * pattern.resolution).collect( { | beatID |
					Button().mouseDownAction_( { | view, x, y, mod, rightClick |
						rightClick = rightClick.asBoolean;
						if( rightClick, {
							// What happens when right-clicking the button
							var window = Window.new( "Instr:" + instrArg[ 0 ].asString + ", beat:" + beatID, Rect( 0, 0, 200, 30 ), scroll:true ).front;
							var params = instrArg[ 1 ].collect( { | argument, argID |
								Button().action_( {
									var window, paramFunc, saveButton, saveAllButton;
									window = Window.new( argument.asString + "function of instrument" + instrArg[ 0 ].asString + "at beat" + beatID, Rect( 0, 0, 300, 300 ), scroll:true ).onClose_({}).front;
									paramFunc = TextView().string_( pattern.textFuncs[ beatID ][ instrID ][ argID ] );
									saveButton = Button().states_( [ [ "Save" ] ] ).action_( { pattern.textFuncs[ beatID ][ instrID ][ argID ] = paramFunc.string; window.close; } );
									saveAllButton = Button().states_( [ [ "Save for all" ] ] ).action_( { (pattern.numbeats * pattern.resolution).do( { | beat, allBeatsID | pattern.textFuncs[ allBeatsID ][ instrID ][ argID ] = paramFunc.string; } ); window.close; } );
									window.view.layout = VLayout(
										HLayout( *paramFunc ),
										HLayout( VLayout( saveButton, *saveAllButton ) ),
									);
								} ).states_( [ [ argument.asString ] ] )
							} );
							window.view.layout = HLayout(
								VLayout( *params ),
							);
						});
					} ).states_( [
						[ "", Color.black, Color.grey( 0.2 ) ],
						[ "", Color.black, Color.gray( 0.8 ) ]
					] ).action_( { | val |
						// What happens when left-clicking the button
						pattern.onsets[ beatID ][ instrID ] = val.value;
					} ).value_( pattern.onsets[ beatID ][ instrID ] );
				} );
			} );

			pattern.repetitionsText = TextField().string_(pattern.repetitions.asString).action_({|val| pattern.repetitions = val.value.asInteger});

			pattern.view.layout_(
				HLayout(
					VLayout(
						StaticText().string_( pattern.name ).align_( \center ).background_( Color.white ),
						HLayout(
							pattern.saveButton, pattern.loadButton, pattern.removeButton
						),
						nil,
						HLayout(
							StaticText().string_("Repeats: ").align_(\center), pattern.repetitionsText, nil
						),
						HLayout(
							pattern.randomizeButton, pattern.gameOfLifeButton
						),
						nil,
						HLayout(
							pattern.addInstrButton, pattern.removeInstrButton
						),
					),
					VLayout( *pattern.labels ),
					VLayout( *pattern.instrArgs.collect( { | instr, id | HLayout( *pattern.buttons[ id ] ) } ) )
				)
			);
		};

		// Draw pattern
		pattern.drawPatternView.value();

		// Return pattern object
		^pattern
	}











	playPattern { | pattern, clock, quant |
		var routine = Routine{
			pattern.repetitions.do( { | repetitionID |


				// GAME OF LIFE
				Routine{
					if(pattern.gameOfLifeButton.value == 1, {
						pattern.onsets.do({|onsetList, index|
							var last = pattern.onsets[(index-1).clip(0, pattern.onsets.size - 1)];
							var next = pattern.onsets[(index+1).clip(0, pattern.onsets.size - 1)];

							onsetList.do({|onset, oid|
								var neighbors = 0;
								if(oid > 0, {neighbors = neighbors + onsetList[oid-1];});
								if(oid < ( onset.size-1), {neighbors = neighbors + onsetList[oid+1];});
								if(last != onsetList, {
									neighbors = neighbors + last[oid];
									if(oid > 0, {neighbors = neighbors + last[oid-1];});
									if(oid < ( onset.size-1), {neighbors = neighbors + last[oid+1];});
								});
								if(next != onsetList, {
									neighbors = neighbors + next[oid];
									if(oid > 0, {neighbors = neighbors + next[oid-1];});
									if(oid < ( onset.size-1), {neighbors = neighbors + next[oid+1];});
								});

								if((onsetList[oid] == 1) && (neighbors < 2), {pattern.buttons.flop[index][oid].valueAction_(0)});
								if((onsetList[oid] == 1) && (neighbors > 3), {pattern.buttons.flop[index][oid].valueAction_(0)});
								if((onsetList[oid] == 0) && (neighbors == 3), {pattern.buttons.flop[index][oid].valueAction_(1)});
								if((onsetList[oid] == 0) && (neighbors == 0), {pattern.buttons.flop[index][oid].valueAction_(1)});
							});
						});
					});
				}.play(AppClock);



				pattern.onsets.do( { | onsetList, onsetID |
					pattern.instrArgs.do( { | instrArg, instrArgID |
						if( onsetList[ instrArgID ] == 1, {
							var tf = pattern.textFuncs.at( onsetID ).at( instrArgID ).collect( { | func | func.interpret.value( onsetID, repetitionID ) } );
							// Un Synth chiamato \code valuterà solo le funzioni al suo interno, senza richiamare l'istanza di Synth
							if(instrArg[ 0 ] != \code, {Synth( instrArg[ 0 ], [ instrArg[ 1 ], tf ].flop.flat );});
						} );
					} );
					pattern.resolution.reciprocal.wait;
				} );
			} );
		}.play( clock, quant );
		^routine
	}



	patterns {
		^patternObjects
	}







}