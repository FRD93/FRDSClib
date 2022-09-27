FRDSFGranulator {

	var win, sfv, delete, setrate, buf, sf, sndrout, ctrlrout, ctrlseed, ctrlrandspeed, ctrlseedrout, netaddr, sfv_selectionrout, sfv_selections;
	var pan, pan2, pos, duration, which, rate, randrate, wait, outCh=0, name_sfg, atk=0.5;
	var amp = 0.7, durScale = 8, minGrainsPerSecond = 8, maxGrainsPerSecond = 64, ctrlDelta=0.1;
	var amp_r, durScale_r, minGrainsPerSecond_r, maxGrainsPerSecond_r, ctrlDelta_r, rate_r, pan_r;

	*new { | name="SFG1", sf_path, video_addr=57200, parent=nil |

		^super.new.init(name, sf_path, video_addr, parent);

	}

	init {  | name="SFG1", sf_path, video_addr=57200, parent=nil |

		name_sfg = name;

		SynthDef(\FRDSFGranulatorGrain, { | buf=0, dur=1, amp=0.7, rate=1, atk=0.5, pos=0, pan=0, outCh=0 |
			var play = PlayBuf.ar(1, buf, rate, 1, pos, 0, 0);
			var env = EnvGen.ar(Env.new([0, 1, 0], [atk, 1.0 - atk], 'wel'), 1, 1, 0, dur, 2);
			play = Pan2.ar(play, pan);
			Out.ar(outCh, play*env*amp);
		}).add;
		SynthDef(\ReplyBang, { SendReply.ar(Impulse.ar(0), '/bang'); EnvGen.ar(Env.new([0,0],[0.001]), doneAction:2); }).add;

		pos=[];
		duration=[];
		pan2 = 0.0;
		buf = Buffer.read(Server.default, sf_path);
		netaddr = NetAddr("localhost", video_addr);
		sf = SoundFile.new;
		sf.openRead(sf_path);
		if( parent==nil, {
			win = Window.new(name, Rect(200, 290, 300, 80)).onClose_({sndrout.stop; ctrlrout.stop;});
		}, {
			win = parent;
		} );
		sfv = SoundFileView.new(win);
		sfv.soundfile_(sf);
		sfv.read(0, sf.numFrames);
		sfv.readWithTask;
		sfv.refresh;
		sfv.gridOn_(true);
		sfv.gridResolution_(000);
		sfv.gridColor_(Color.green(0.6));
		sfv.timeCursorColor_(Color.black);
		sfv.setSelectionColor(0, Color.red(0.8, 0.8));
		sfv.mouseDownAction_({
			sfv.setSelectionColor(sfv.currentSelection, Color.new255(rrand(128, 255), rrand(128, 255), rrand(128, 255), rrand(128, 255)));
		});
		sfv.mouseUpAction_({
			("mouseUp, current selection " ++ sfv.currentSelection++" is now:"++ sfv.selections[sfv.currentSelection]).postln;
			sfv.currentSelection_(sfv.currentSelection+1);
		});

		sfv_selectionrout = Routine{
			loop{
				sfv_selections = sfv.selections;
				0.1.wait;
			}
		}.play( AppClock );

		// Bottone per togliere selezioni
		delete = Button.new(win).states_([["-", Color.black, Color.red(0.6,0.8)]]).action_({
			var done = false;
			sfv.selections.size.do({|id|
				if(sfv.selections[(sfv.selections.size-1)-id] != [0, 0], {
					if(done == false, {
						sfv.selections[(sfv.selections.size-1)-id].postln;
						sfv.setSelection((sfv.selections.size-1)-id, [0, 0]);
					});
					done = true;
				});
			});
		});
		// Slider per governare il rate dei grani
		randrate = [0.1, 4];
		setrate = RangeSlider(win).action_({|val|
			randrate = [(val.lo.pow(2)*3.9)+0.1,(val.hi.pow(2)*3.9)+0.1];
			randrate.postln;
		});


		// Layout Window
		win.layout_(
			VLayout(
				sfv,
				HLayout(
					delete, setrate, NumberBox(win).action_({|val| randrate = val.value!2})
				)
			)
		);

		// Routine per generare i grani
		sndrout = Routine{
			inf.do({|idL|
				if(pos!=nil,{
					var type=0;
					var which=0;
					Synth(\ReplyBang);
					Synth(\FRDSFGranulatorGrain, [\buf, buf, \dur, duration, \amp, amp, \rate, rate, \atk, atk, \pos, pos, \pan, pan, \outCh, outCh]);
					// VIDEO SYNC
					if(rate >= 2, {which = 0}, {which = 1});
					if(pos >= (buf.numFrames * 2 / 3), {type=1;});
					if((pos > (buf.numFrames / 3)) && (pos < (buf.numFrames * 2 / 3)), {type=0});
					if(pos <= (buf.numFrames / 3), {type= -1});
					netaddr.sendMsg("/test", ""++which++","++duration++","++amp++","++0.5++","++type++"");
				});
				wait.reciprocal.wait;
			});
		};
		// Routine per generare i parametri dei grani
		ctrlrout =  Routine{
			inf.do({|idL|
				// rate
				if(idL%5==0, {
					rate = rrand(randrate[0], randrate[1]);
				});
				// wait
				if(idL%10==0, {
					wait = rrand(minGrainsPerSecond, maxGrainsPerSecond).roundUp(minGrainsPerSecond);
				});
				// duration
				duration = durScale / wait;
				// pos
				pos=[];
				// pan
				//pan = rrand( -1.0, 1.0 );
				pan = rrand(pan2.neg, pan2);
				sfv_selections.size.do({|id|
					if(sfv_selections[id][1] != 0, {
						pos = pos.add(rrand(sfv_selections[id][0], sfv_selections[id][0]+sfv_selections[id][1]));
					});
				});
				which = pos.size.rand;
				pos = pos[which];
				ctrlDelta.wait;

				// Set rand seed
				if( ctrlseed != nil, {
					if( idL % ctrlrandspeed == 0, {
						ctrlrout.randSeed_( ctrlseed );
					} );
				} );
			});
		};



	}

	start { | clock, quant |
		ctrlrout.reset.play( clock, quant );
		sndrout.reset.play( clock, quant );
	}

	startWithBang {
		OSCdef('sfgran_'++name_sfg, { ctrlrout.reset.play(AppClock); sndrout.reset.play(AppClock); (name_sfg+"banged!").postln; }, '/bang').oneShot;
	}

	stop {
		ctrlrout.stop;
		sndrout.stop;
	}


	outCh {
		^outCh;
	}


	outCh_ { | ch |
		outCh = ch;
	}


	asProcessA {
		// ^[ NAME , N_IN_CH , N_OUT_CH , CH_IN , CH_OUT ]
		^[name_sfg.asSymbol, nil, 2, nil, outCh];
	}

	// Get a Dictionary for integration in FRDMixerMatrixPlugIn
	asMixerMatrixProcess {
		^Dictionary.new.put(\inCh, nil).put(\outCh, outCh).put(\inChannels, 0).put(\outChannels, 2)
	}

	showGUI {
		win.front;
	}

	addSelection { | selectionNumber=0, selectionStart=0, selectionLength=1 |
		sfv.setSelection(selectionNumber, [selectionStart, selectionLength]);
	}

	removeSelection { | selectionNumber |
		sfv.setSelection(selectionNumber, [0, 0]);
	}

	rate_ { | minRateRange, maxRateRange=0 |
		if(maxRateRange <= minRateRange, {
			randrate = [minRateRange, minRateRange];
		}, {
			randrate = [minRateRange, maxRateRange];
		})
	}

	rate {
		^rate;
	}

	amp_ { | newAmp |
		amp = newAmp;
	}

	amp {
		^amp
	}

	pan_ { | newPan |
		pan2 = newPan;
	}

	pan {
		^pan
	}

	lineTo { | param, from, to, time=1, round=0, power=2 |
		var size = time * 200;
		var deltaV = to - from;
		var deltaT = time / size;
		power = (from > to).if({1/power}, {power});
		case
		{param == "amp"}	{
			amp_r.stop;
			amp_r = Routine{size.do({|id| amp = (from + (((id+1)/size).pow(power)*deltaV)).round(round); deltaT.wait;})}.play;
		}
		{param == "durScale"}	{
			durScale_r.stop;
			durScale_r = Routine{size.do({|id| durScale = (from + (((id+1)/size).pow(power)*deltaV)).round(round); deltaT.wait;})}.play;
		}
		{param == "minGrainsPerSecond"}	{
			minGrainsPerSecond_r.stop;
			minGrainsPerSecond_r = Routine{size.do({|id| minGrainsPerSecond = (from + (((id+1)/size).pow(power)*deltaV)).round(round); deltaT.wait;})}.play;
		}
		{param == "maxGrainsPerSecond"}	{
			maxGrainsPerSecond_r.stop;
			maxGrainsPerSecond_r = Routine{size.do({|id| maxGrainsPerSecond = (from + (((id+1)/size).pow(power)*deltaV)).round(round); deltaT.wait;})}.play;
		}
		{param == "ctrlDelta"}	{
			ctrlDelta_r.stop;
			ctrlDelta_r = Routine{size.do({|id| ctrlDelta = (from + (((id+1)/size).pow(power)*deltaV)).round(round); deltaT.wait;})}.play;
		}
		{param == "rate"}	{
			rate_r.stop;
			rate_r = Routine{size.do({|id| rate = (from + (((id+1)/size).pow(power)*deltaV)).round(round); randrate = [rate, rate]; deltaT.wait;})}.play;
		}
		{param == "pan"}	{
			pan_r.stop;
			pan_r = Routine{size.do({|id| pan = (from + (((id+1)/size).pow(power)*deltaV)).round(round); deltaT.wait;})}.play;
		};

	}

	sfFrames {
		^sf.numFrames;
	}

	durScale_ { | newDurScale |
		durScale = newDurScale;
	}

	durScale {
		^durScale;
	}

	atk_{ | newAtk |
		atk = newAtk;
	}

	minGrainsPerSecond_ { |newMinGPS |
		minGrainsPerSecond = newMinGPS;
	}

	minGrainsPerSecond {
		^minGrainsPerSecond;
	}

	maxGrainsPerSecond_ { | newMaxGPS |
		maxGrainsPerSecond = newMaxGPS;
	}

	maxGrainsPerSecond {
		^maxGrainsPerSecond;
	}

	ctrlDelta_ { | newCTRLDelta |
		ctrlDelta = newCTRLDelta;
	}

	ctrlDelta {
		^ctrlDelta;
	}

	ctrlRandSeed_ { | seed = 1956 |
		ctrlseed = seed;
		ctrlrout.randSeed_( ctrlseed );
	}

	ctrlRandSpeed_ { | speed = 1 |
		ctrlrandspeed = speed;
	}

	ctrlRandSeedLoop_ { | seed = 1956, speed = 1 |
		ctrlseed = seed;
		ctrlrandspeed = speed;
		ctrlseedrout.stop();
		ctrlseedrout = Routine{
			loop{
				//this.ctrlRandSeed_( ctrlseed );
				ctrlrout.randSeed_( ctrlseed );
				ctrlrandspeed.wait;
			}
		}.reset.play( AppClock );
	}

	stopRandSeedLoop {
		ctrlseedrout.stop();
	}



}