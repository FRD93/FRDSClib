FRDDX7PlugIn {


	// new method
	*new {
		^super.new.init()
	}


	// init method
	init {

	}

	randSeed_ { | seed |
		thisThread.randSeed_(seed);
	}


	// Spawn synth with absolute duration (Rx) times
	spawnModuleAbsolute { | fmod=440, imod=2, feedback=0.0, extCh=128, outCh=100, l4=0, l1=1, l2=0.7, l3=0.2, r1=0.01, r2=0.1, r3=0.7, r4=1 |
		^Synth.head(Server.local, \FRDDX7_module, [\fmod, fmod, \imod, imod, \feedback, feedback, \extCh, extCh, \outCh, outCh, \l4, l4, \l1, l1, \l2, l2, \l3, l3, \r1, r1, \r2, r2, \r3, r3, \r4, r4]);
	}



	// Spawn synth with relative duration (Rx) times
	spawnModuleRelative { | fmod=440, imod=2, feedback=0.0, extCh=128, outCh=100, dur=1, l4=0, l1=1, l2=0.7, l3=0.2, r1=0.01, r2=0.1, r3=0.7, r4=1 |
		var rs = [r1, r2, r3, r4].normalizeSum * dur;
		^Synth.head(Server.local, \FRDDX7_module, [\fmod, fmod, \imod, imod, \feedback, feedback, \extCh, extCh, \outCh, outCh, \l4, l4, \l1, l1, \l2, l2, \l3, l3, \r1, rs[0], \r2, rs[1], \r3, rs[2], \r4, rs[3]]);
	}



	// Create random module patch in relative mode
	createRandomRelativePatch { | numCarriers=1, freq=440, amp=0.5, dur=3, harmonic=true, imod=#[0.3, 1], outCh=0, nrtTimeStamp=nil |
		if(nrtTimeStamp == nil, {
			Routine{
				var modules, carriers, modulators, params, indexes, freqs, imods;
				var patch, lastModulators;
				var busses = 6.collect({ Bus.audio(Server.local, 1) });
				numCarriers = numCarriers.clip(0, 5);
				Server.local.sync;
				if(harmonic == true, {freqs = 6.collect({freq * [0.5, 1, 0.75, 1.5, 2, 3, 4].choose})}, {freqs = 6.collect({rrand(5, 1000)})});
				freqs = freqs.clip(20, 20000);
				imods = 6.collect({rrand(imod[0], imod[1])});
				modules = 6.collect({ | id |
					//this.spawnModuleRelative(fmod: freqs[id], imod: imods[id], feedback: exprand(0.001, 1.0), outCh: busses[id], dur: dur, l4: rrand(0.0, 1.0), l1: rrand(0.0, 1.0), l2: rrand(0.0, 1.0), l3: rrand(0.0, 1.0), r1: rrand(0.0, 1.0), r2: rrand(0.0, 1.0), r3: rrand(0.0, 1.0), r4: rrand(0.0, 1.0) )
					if( id < numCarriers, {
						this.spawnModuleRelative(fmod: freqs[id], imod: imods[id], feedback: exprand(0.001, 1.0), outCh: busses[id], dur: dur, l4: 0, l1: rrand(0.01, 1.0), l2: rrand(0.01, 1.0), l3: rrand(0.01, 1.0), r1: rrand(0.01, 1.0), r2: rrand(0.01, 1.0), r3: rrand(0.01, 1.0), r4: 1 )
					}, {
						this.spawnModuleRelative(fmod: freqs[id], imod: imods[id], feedback: exprand(0.001, 1.0), outCh: busses[id], dur: dur, l4: rrand(0.01, 1.0), l1: rrand(0.01, 1.0), l2: rrand(0.01, 1.0), l3: rrand(0.01, 1.0), r1: rrand(0.01, 1.0), r2: rrand(0.01, 1.0), r3: rrand(0.01, 1.0), r4: rrand(0.4, 1.0) )
					})
				});
				indexes = (0..5).scramble;
				carriers = indexes[0..numCarriers-1];
				modulators = indexes[numCarriers..];
				if(modulators.size == 1, {modulators = modulators[0] ! 2;});
				patch = modulators.collect({|mi| var myM = modulators.copy; myM.remove(mi); [mi, myM.choose] }).collect({|tuple| tuple.sort});
				lastModulators = patch.collect({|mp| mp[0]}).as(Set).as(Array).sort;
				patch.do({ | pa |
					modules[pa[0]].set(\extCh, busses[pa[1]]);
				});

				carriers.do({ | cID, id |
					modules[cID].set(\extCh, busses[modulators[id % lastModulators.size]]);
					lastModulators.do({|iModID, id2|
						if(0.5.coin, {
							modules[iModID].set(\outCh, busses[modulators[id % lastModulators.size]]);
						});
					});
					modules[cID].set(\isCarrier, 1);
					modules[cID].set(\l4, 0);
					modules[cID].set(\fmod, freq);
					modules[cID].set(\feedback, 0);
					modules[cID].set(\imod, ((10.0 / freqs[cID]).pow(0.33)) * amp * carriers.size.reciprocal); // Psychoacoustic amplitude compensation for carriers
					modules[cID].set(\outCh, outCh);
				});
				// Must free buffers when done!
				(dur+0.1).wait;
				Server.local.sync;
				busses.do({|bus| bus.free(clear: true)});
			}.play;
		});
	}


	// Create random module patch in relative mode NRT
	createRandomRelativePatchNRT { | numCarriers=1, freq=440, amp=0.5, dur=3, harmonic=true, imod=#[0.3, 1], outCh=0, nrtTimeStamp=0, startNode=1002, startPrivateBus=40 |
	// 		var rs = [r1, r2, r3, r4].normalizeSum * dur;
	// ^Synth.head(Server.local, \FRDDX7_module, [\fmod, fmod, \imod, imod, \feedback, feedback, \extCh, extCh, \outCh, outCh, \l4, l4, \l1, l1, \l2, l2, \l3, l3, \r1, rs[0], \r2, rs[1], \r3, rs[2], \r4, rs[3]]);
	var score = [];
	var carriers, modulators, params, indexes, freqs, imods;
	var patch, lastModulators;
	var busses = 6.collect({ | id | startPrivateBus + id });
	var modules = 6.collect({ | id |  startNode + id });
	numCarriers = numCarriers.clip(0, 5);
	if(harmonic == true, {freqs = 6.collect({freq * [0.5, 1, 0.75, 1.5, 2, 3, 4].choose})}, {freqs = 6.collect({rrand(5, 1000)})});
	freqs = freqs.clip(20, 20000);
	imods = 6.collect({rrand(imod[0], imod[1])});

	6.do({|id|
		var li = {rrand(0.0, 1.0)} ! 6;
		var rs = ({rrand(0.0, 1.0)} ! 6).normalizeSum * dur;
		score = score ++ [[
			nrtTimeStamp, [\s_new, \FRDDX7_module, modules[id], 0, 0, \fmod, freqs[id], \imod, imods[id], \feedback, exprand(0.001, 1.0), \extCh, 128, \outCh, busses[id], \l4, li[3], \l1, li[0], \l2, li[1], \l3, li[2], \r1, rs[0], \r2, rs[1], \r3, rs[2], \r4, rs[3]]
		]];
	});

	indexes = (0..5).scramble;
	carriers = indexes[0..numCarriers-1];
	modulators = indexes[numCarriers..];
	if(modulators.size == 1, {modulators = modulators[0] ! 2;});
	patch = modulators.collect({|mi| var myM = modulators.copy; myM.remove(mi); [mi, myM.choose] }).collect({|tuple| tuple.sort});
	lastModulators = patch.collect({|mp| mp[0]}).as(Set).as(Array).sort;

	patch.do({ | pa |
		score = score ++ [[
			nrtTimeStamp, [\n_set, modules[pa[0]], \extCh, busses[pa[1]] ]
		]];
	});

	carriers.do({ | cID, id |
		score = score ++ [[
			nrtTimeStamp, [\n_set, modules[cID], \extCh, busses[modulators[id % lastModulators.size]] ]
		]];

		lastModulators.do({|iModID, id2|
			if(0.5.coin, {
				score = score ++ [[
					nrtTimeStamp, [\n_set, modules[iModID], \outCh, busses[modulators[id % lastModulators.size]] ]
				]];
			});
		});

		score = score ++ [[
			nrtTimeStamp, [\n_set, modules[cID], \isCarrier, 1, \l4, 0, \fmod, freq, \feedback, 0, \imod,  ((10.0 / freqs[cID]).pow(0.33)) * amp * carriers.size.reciprocal, \outCh, outCh]
		]];

	});
	^score
}



	// Create default module patch in relative mode
	createDefaultRelativePatch { | kind=1, outCh=0, amp=0.5, dur=8, param1, param2, param3, param4, param5, param6 |
		Routine{
			var params = [param1, param2, param3, param4, param5, param6];
			var busses = 6.collect({ Bus.audio(Server.local, 1) });
			var modules;
			Server.local.sync();
			modules = 6.collect({ | id | this.spawnModuleRelative(fmod: params[id].at(\fmod), imod: params[id].at(\imod), feedback: params[id].at(\feedback), outCh: busses[id], dur: dur, l4: params[id].at(\l4), l1: params[id].at(\l1), l2: params[id].at(\l2), l3: params[id].at(\l3), r1: params[id].at(\r1), r2: params[id].at(\r2), r3: params[id].at(\r3), r4: params[id].at(\r4) ) });

			kind = kind.clip(1, 32);
			switch( kind,
				/* PATCH 1 */
				1, {
					// Set unwanted feedbacks to 0
					[0, 1, 2, 3, 4].do({|id| modules[id].set(\feedback, 0)});
					// row 2->1
					modules[0].set(\extCh, busses[1]);
					// row [6]->5->4->3
					modules[4].set(\extCh, busses[5]);
					modules[3].set(\extCh, busses[4]);
					modules[2].set(\extCh, busses[3]);
					// outputs: 1 & 3
					modules[0].set(\isCarrier, 1);
					modules[0].set(\l4, 0);
					modules[0].set(\outCh, outCh);
					modules[0].set(\imod, ((10.0 / params[0].at(\fmod)).pow(0.33)) * (amp / 2)); // Psychoacoustic amplitude compensation for carriers
					modules[2].set(\outCh, outCh);
					modules[2].set(\isCarrier, 1);
					modules[2].set(\l4, 0);
					modules[2].set(\imod, ((10.0 / params[2].at(\fmod)).pow(0.33)) * (amp / 2)); // Psychoacoustic amplitude compensation for carriers
				},
				/* PATCH 2 */
				2, {
					// Set unwanted feedbacks to 0
					[0, 2, 3, 4, 5].do({|id| modules[id].set(\feedback, 0)});
					// row [2]->1
					modules[0].set(\extCh, busses[1]);
					// row 6->5->4->3
					modules[4].set(\extCh, busses[5]);
					modules[3].set(\extCh, busses[4]);
					modules[2].set(\extCh, busses[3]);
					// outputs: 1 & 3
					modules[0].set(\isCarrier, 1);
					modules[0].set(\l4, 0);
					modules[0].set(\outCh, outCh);
					modules[0].set(\imod, ((10.0 / params[0].at(\fmod)).pow(0.33)) * (amp / 2)); // Psychoacoustic amplitude compensation for carriers
					modules[2].set(\isCarrier, 1);
					modules[2].set(\l4, 0);
					modules[2].set(\outCh, outCh);
					modules[2].set(\imod, ((10.0 / params[2].at(\fmod)).pow(0.33)) * (amp / 2)); // Psychoacoustic amplitude compensation for carriers
				},
				/* PATCH 3 */
				3, {
					// Set unwanted feedbacks to 0
					[0, 1, 2, 3, 4].do({|id| modules[id].set(\feedback, 0)});
					// row 3->2->1
					modules[1].set(\extCh, busses[2]);
					modules[0].set(\extCh, busses[1]);
					// row [6]->5->4
					modules[4].set(\extCh, busses[5]);
					modules[3].set(\extCh, busses[4]);
					// outputs: 1 & 4
					modules[0].set(\isCarrier, 1);
					modules[0].set(\l4, 0);
					modules[0].set(\outCh, outCh);
					modules[0].set(\imod, ((10.0 / params[0].at(\fmod)).pow(0.33)) * (amp / 2)); // Psychoacoustic amplitude compensation for carriers
					modules[3].set(\outCh, outCh);
					modules[3].set(\l4, 0);
					modules[3].set(\isCarrier, 1);
					modules[3].set(\imod, ((10.0 / params[3].at(\fmod)).pow(0.33)) * (amp / 2)); // Psychoacoustic amplitude compensation for carriers
				},
				/* PATCH 4 */
				4, {
					// Set unwanted feedbacks to 0
					[0, 1, 2, 3, 4, 5].do({|id| modules[id].set(\feedback, 0)});
					// row 3->2->1
					modules[1].set(\extCh, busses[2]);
					modules[0].set(\extCh, busses[1]);
					// row [6->5->4]
					//modules[5].set(\extCh, busses[3]);
					modules[4].set(\extCh, busses[5]);
					modules[3].set(\extCh, busses[4]);
					// outputs: 1 & 4
					modules[0].set(\isCarrier, 1);
					modules[0].set(\l4, 0);
					modules[0].set(\outCh, outCh);
					modules[0].set(\imod, ((10.0 / params[0].at(\fmod)).pow(0.33)) * (amp / 2)); // Psychoacoustic amplitude compensation for carriers
					modules[3].set(\isCarrier, 1);
					modules[3].set(\l4, 0);
					modules[3].set(\outCh, outCh);
					modules[3].set(\imod, ((10.0 / params[3].at(\fmod)).pow(0.33)) * (amp / 2)); // Psychoacoustic amplitude compensation for carriers
				},
				/* PATCH 5 */
				5, {
					// Set unwanted feedbacks to 0
					[0, 1, 2, 3, 4].do({|id| modules[id].set(\feedback, 0)});
					// row 2->1
					modules[0].set(\extCh, busses[1]);
					// row 4->3
					modules[2].set(\extCh, busses[3]);
					// row [6]->5
					modules[4].set(\extCh, busses[5]);
					// outputs: 1 & 3 & 5
					modules[0].set(\isCarrier, 1);
					modules[0].set(\l4, 0);
					modules[0].set(\outCh, outCh);
					modules[0].set(\imod, ((10.0 / params[0].at(\fmod)).pow(0.33)) * (amp / 3)); // Psychoacoustic amplitude compensation for carriers
					modules[2].set(\isCarrier, 1);
					modules[2].set(\l4, 0);
					modules[2].set(\outCh, outCh);
					modules[2].set(\imod, ((10.0 / params[2].at(\fmod)).pow(0.33)) * (amp / 3)); // Psychoacoustic amplitude compensation for carriers
					modules[4].set(\isCarrier, 1);
					modules[4].set(\l4, 0);
					modules[4].set(\outCh, outCh);
					modules[4].set(\imod, ((10.0 / params[4].at(\fmod)).pow(0.33)) * (amp / 3)); // Psychoacoustic amplitude compensation for carriers
				},
				/* PATCH 6 */
				6, {
					// Set unwanted feedbacks to 0
					[0, 1, 2, 3, 4, 5].do({|id| modules[id].set(\feedback, 0)});
					// row 2->1
					modules[0].set(\extCh, busses[1]);
					// row 4->3
					modules[2].set(\extCh, busses[3]);
					// row [6->5]
					modules[4].set(\extCh, busses[5]);
					modules[5].set(\extCh, busses[4]);
					// outputs: 1 & 3 & 5
					modules[0].set(\isCarrier, 1);
					modules[0].set(\l4, 0);
					modules[0].set(\outCh, outCh);
					modules[0].set(\imod, ((10.0 / params[0].at(\fmod)).pow(0.33)) * (amp / 3)); // Psychoacoustic amplitude compensation for carriers
					modules[2].set(\isCarrier, 1);
					modules[2].set(\l4, 0);
					modules[2].set(\outCh, outCh);
					modules[2].set(\imod, ((10.0 / params[2].at(\fmod)).pow(0.33)) * (amp / 3)); // Psychoacoustic amplitude compensation for carriers
					modules[4].set(\isCarrier, 1);
					modules[4].set(\l4, 0);
					modules[4].set(\outCh, outCh);
					modules[4].set(\imod, ((10.0 / params[4].at(\fmod)).pow(0.33)) * (amp / 3)); // Psychoacoustic amplitude compensation for carriers
				},
				/* PATCH 7 */
				7, {
					// Set unwanted feedbacks to 0
					[0, 1, 2, 3, 4].do({|id| modules[id].set(\feedback, 0)});
					// row 2->1
					modules[0].set(\extCh, busses[1]);
					// row [4]->3
					modules[2].set(\extCh, busses[3]);
					// row 6->5->3
					modules[4].set(\extCh, busses[5]);
					modules[4].set(\outCh, busses[2]);
					// outputs: 1 & 3 & 5
					modules[0].set(\isCarrier, 1);
					modules[0].set(\l4, 0);
					modules[0].set(\outCh, outCh);
					modules[0].set(\imod, ((10.0 / params[0].at(\fmod)).pow(0.33)) * (amp / 3)); // Psychoacoustic amplitude compensation for carriers
					modules[2].set(\isCarrier, 1);
					modules[2].set(\l4, 0);
					modules[2].set(\outCh, outCh);
					modules[2].set(\imod, ((10.0 / params[2].at(\fmod)).pow(0.33)) * (amp / 3)); // Psychoacoustic amplitude compensation for carriers
					modules[4].set(\isCarrier, 1);
					modules[4].set(\l4, 0);
					modules[4].set(\outCh, outCh);
					modules[4].set(\imod, ((10.0 / params[4].at(\fmod)).pow(0.33)) * (amp / 3)); // Psychoacoustic amplitude compensation for carriers
				},
				/* PATCH 8 */
				8, {
					// Set unwanted feedbacks to 0
					[0, 1, 2, 4, 5].do({|id| modules[id].set(\feedback, 0)});
					// row 2->1
					modules[0].set(\extCh, busses[1]);
					// row 4->3
					modules[2].set(\extCh, busses[3]);
					// row [6]->5->3
					modules[4].set(\extCh, busses[5]);
					modules[4].set(\outCh, busses[2]);
					// outputs: 1 & 3 & 5
					modules[0].set(\isCarrier, 1);
					modules[0].set(\l4, 0);
					modules[0].set(\outCh, outCh);
					modules[0].set(\imod, ((10.0 / params[0].at(\fmod)).pow(0.33)) * (amp / 3)); // Psychoacoustic amplitude compensation for carriers
					modules[2].set(\isCarrier, 1);
					modules[2].set(\l4, 0);
					modules[2].set(\outCh, outCh);
					modules[2].set(\imod, ((10.0 / params[2].at(\fmod)).pow(0.33)) * (amp / 3)); // Psychoacoustic amplitude compensation for carriers
					modules[4].set(\isCarrier, 1);
					modules[4].set(\l4, 0);
					modules[4].set(\outCh, outCh);
					modules[4].set(\imod, ((10.0 / params[4].at(\fmod)).pow(0.33)) * (amp / 3)); // Psychoacoustic amplitude compensation for carriers
				},
				/* PATCH 9 */
				9, {
					// Set unwanted feedbacks to 0
					[0, 2, 3, 4, 5].do({|id| modules[id].set(\feedback, 0)});
					// row [2]->1
					modules[0].set(\extCh, busses[1]);
					// row 4->3
					modules[2].set(\extCh, busses[3]);
					// row 6->5->3
					modules[4].set(\extCh, busses[5]);
					modules[4].set(\outCh, busses[2]);
					// outputs: 1 & 3 & 5
					modules[0].set(\isCarrier, 1);
					modules[0].set(\l4, 0);
					modules[0].set(\outCh, outCh);
					modules[0].set(\imod, ((10.0 / params[0].at(\fmod)).pow(0.33)) * (amp / 3)); // Psychoacoustic amplitude compensation for carriers
					modules[2].set(\isCarrier, 1);
					modules[2].set(\l4, 0);
					modules[2].set(\outCh, outCh);
					modules[2].set(\imod, ((10.0 / params[2].at(\fmod)).pow(0.33)) * (amp / 3)); // Psychoacoustic amplitude compensation for carriers
					modules[4].set(\isCarrier, 1);
					modules[4].set(\l4, 0);
					modules[4].set(\outCh, outCh);
					modules[4].set(\imod, ((10.0 / params[4].at(\fmod)).pow(0.33)) * (amp / 3)); // Psychoacoustic amplitude compensation for carriers
				},
				/* PATCH 10 */
				10, {
					// Set unwanted feedbacks to 0
					[0, 1, 2, 3, 4].do({|id| modules[id].set(\feedback, 0)});
					// row 2->1
					modules[0].set(\extCh, busses[1]);
					// row 3->1
					modules[2].set(\outCh, busses[1]);
					// row [6]->5->4
					modules[4].set(\extCh, busses[5]);
					modules[3].set(\extCh, busses[4]);
					// outputs: 1 & 4
					modules[0].set(\isCarrier, 1);
					modules[0].set(\l4, 0);
					modules[0].set(\outCh, outCh);
					modules[0].set(\imod, ((10.0 / params[0].at(\fmod)).pow(0.33)) * (amp / 2)); // Psychoacoustic amplitude compensation for carriers
					modules[3].set(\isCarrier, 1);
					modules[3].set(\l4, 0);
					modules[3].set(\outCh, outCh);
					modules[3].set(\imod, ((10.0 / params[3].at(\fmod)).pow(0.33)) * (amp / 2)); // Psychoacoustic amplitude compensation for carriers
				},
				/* PATCH 11 */
				11, {
					// Set unwanted feedbacks to 0
					[0, 1, 3, 4, 5].do({|id| modules[id].set(\feedback, 0)});
					// row 2->1
					modules[0].set(\extCh, busses[1]);
					// row 3->1
					modules[2].set(\outCh, busses[1]);
					// row [6]->5->4
					modules[4].set(\extCh, busses[5]);
					modules[3].set(\extCh, busses[4]);
					// outputs: 1 & 4
					modules[0].set(\isCarrier, 1);
					modules[0].set(\l4, 0);
					modules[0].set(\outCh, outCh);
					modules[0].set(\imod, ((10.0 / params[0].at(\fmod)).pow(0.33)) * (amp / 2)); // Psychoacoustic amplitude compensation for carriers
					modules[3].set(\isCarrier, 1);
					modules[3].set(\l4, 0);
					modules[3].set(\outCh, outCh);
					modules[3].set(\imod, ((10.0 / params[3].at(\fmod)).pow(0.33)) * (amp / 2)); // Psychoacoustic amplitude compensation for carriers
				},
				/* PATCH 12 */
				12, {
					// Set unwanted feedbacks to 0
					[0, 1, 2, 3, 4].do({|id| modules[id].set(\feedback, 0)});
					// row 2->1
					modules[0].set(\extCh, busses[1]);
					// row 3->1
					modules[2].set(\outCh, busses[1]);
					// row 4->1
					modules[3].set(\outCh, busses[1]);
					// row [6]->5
					modules[4].set(\extCh, busses[5]);
					// outputs: 1 & 5
					modules[0].set(\isCarrier, 1);
					modules[0].set(\l4, 0);
					modules[0].set(\outCh, outCh);
					modules[0].set(\imod, ((10.0 / params[0].at(\fmod)).pow(0.33)) * (amp / 2)); // Psychoacoustic amplitude compensation for carriers
					modules[4].set(\isCarrier, 1);
					modules[4].set(\l4, 0);
					modules[4].set(\outCh, outCh);
					modules[4].set(\imod, ((10.0 / params[4].at(\fmod)).pow(0.33)) * (amp / 2)); // Psychoacoustic amplitude compensation for carriers
				},
				/* PATCH 13 */
				13, {
					// Set unwanted feedbacks to 0
					[0, 1, 2, 3, 4].do({|id| modules[id].set(\feedback, 0)});
					// row 2->1
					modules[0].set(\extCh, busses[1]);
					// row 3->1
					modules[2].set(\outCh, busses[1]);
					// row [4]->1
					modules[3].set(\outCh, busses[1]);
					// row 6->5
					modules[4].set(\extCh, busses[5]);
					// outputs: 1 & 5
					modules[0].set(\isCarrier, 1);
					modules[0].set(\l4, 0);
					modules[0].set(\outCh, outCh);
					modules[0].set(\imod, ((10.0 / params[0].at(\fmod)).pow(0.33)) * (amp / 2)); // Psychoacoustic amplitude compensation for carriers
					modules[4].set(\isCarrier, 1);
					modules[4].set(\l4, 0);
					modules[4].set(\outCh, outCh);
					modules[4].set(\imod, ((10.0 / params[4].at(\fmod)).pow(0.33)) * (amp / 2)); // Psychoacoustic amplitude compensation for carriers
				},
				/* PATCH 14 */
				14, {
					// Set unwanted feedbacks to 0
					[0, 2, 3, 4, 5].do({|id| modules[id].set(\feedback, 0)});
					// row [2]->1
					modules[0].set(\extCh, busses[1]);
					// row 5->4
					modules[4].set(\outCh, busses[3]);
					// row 6->4
					modules[5].set(\outCh, busses[3]);
					// row 4->3
					modules[2].set(\extCh, busses[3]);
					// outputs: 1 & 3
					modules[0].set(\isCarrier, 1);
					modules[0].set(\l4, 0);
					modules[0].set(\outCh, outCh);
					modules[0].set(\imod, ((10.0 / params[0].at(\fmod)).pow(0.33)) * (amp / 2)); // Psychoacoustic amplitude compensation for carriers
					modules[2].set(\isCarrier, 1);
					modules[2].set(\l4, 0);
					modules[2].set(\outCh, outCh);
					modules[2].set(\imod, ((10.0 / params[2].at(\fmod)).pow(0.33)) * (amp / 2)); // Psychoacoustic amplitude compensation for carriers
				},
				/* PATCH 15 */
				15, {
					// Set unwanted feedbacks to 0
					[0, 2, 3, 4].do({|id| modules[id].set(\feedback, 0)});
					// row [2]->1
					modules[0].set(\extCh, busses[1]);
					// row 5->4
					modules[3].set(\extCh, busses[4]);
					// row [6]->4
					modules[5].set(\outCh, busses[4]);
					// row 4->3
					modules[2].set(\extCh, busses[3]);
					// outputs: 1 & 3
					modules[0].set(\isCarrier, 1);
					modules[0].set(\l4, 0);
					modules[0].set(\outCh, outCh);
					modules[0].set(\imod, ((10.0 / params[0].at(\fmod)).pow(0.33)) * (amp / 2)); // Psychoacoustic amplitude compensation for carriers
					modules[2].set(\isCarrier, 1);
					modules[2].set(\l4, 0);
					modules[2].set(\outCh, outCh);
					modules[2].set(\imod, ((10.0 / params[2].at(\fmod)).pow(0.33)) * (amp / 2)); // Psychoacoustic amplitude compensation for carriers
				},
				/* PATCH 16 */
				16, {
					// Set unwanted feedbacks to 0
					[0, 1, 2, 3, 4].do({|id| modules[id].set(\feedback, 0)});
					// row [2]->1
					modules[0].set(\extCh, busses[1]);
					// row [6]->5->1
					modules[4].set(\extCh, busses[5]);
					modules[4].set(\outCh, busses[1]);
					// row 4->3->1
					modules[2].set(\extCh, busses[3]);
					modules[2].set(\outCh, busses[1]);
					// outputs: 1
					modules[0].set(\isCarrier, 1);
					modules[0].set(\l4, 0);
					modules[0].set(\outCh, outCh);
					modules[0].set(\imod, ((10.0 / params[0].at(\fmod)).pow(0.33)) * (amp / 2)); // Psychoacoustic amplitude compensation for carriers
				},
				/* PATCH 17 */
				17, {
					// Set unwanted feedbacks to 0
					[0, 2, 3, 4, 5].do({|id| modules[id].set(\feedback, 0)});
					// row [2]->1
					modules[0].set(\extCh, busses[1]);
					// row [6]->5->1
					modules[4].set(\extCh, busses[5]);
					modules[4].set(\outCh, busses[1]);
					// row 4->3->1
					modules[2].set(\extCh, busses[3]);
					modules[2].set(\outCh, busses[1]);
					// outputs: 1
					modules[0].set(\isCarrier, 1);
					modules[0].set(\l4, 0);
					modules[0].set(\outCh, outCh);
					modules[0].set(\imod, ((10.0 / params[0].at(\fmod)).pow(0.33)) * (amp / 2)); // Psychoacoustic amplitude compensation for carriers
				},
				/* PATCH 18 */
				18, {
					// Set unwanted feedbacks to 0
					[0, 1, 3, 4, 5].do({|id| modules[id].set(\feedback, 0)});
					// row [2]->1
					modules[0].set(\extCh, busses[1]);
					// row 3->1
					modules[2].set(\outCh, busses[1]);
					// row 6->5->1
					modules[4].set(\extCh, busses[5]);
					modules[3].set(\extCh, busses[4]);
					modules[3].set(\outCh, busses[1]);
					// outputs: 1
					modules[0].set(\isCarrier, 1);
					modules[0].set(\l4, 0);
					modules[0].set(\outCh, outCh);
					modules[0].set(\imod, ((10.0 / params[0].at(\fmod)).pow(0.33)) * (amp / 2)); // Psychoacoustic amplitude compensation for carriers
				},
				/* PATCH 19 */
				19, {
					// Set unwanted feedbacks to 0
					[0, 1, 2, 3, 5].do({|id| modules[id].set(\feedback, 0)});
					// row 5->4
					modules[4].set(\extCh, busses[5]);
					// row 5->6
					modules[5].set(\extCh, busses[4]);
					// row 3->2->1
					modules[1].set(\extCh, busses[2]);
					modules[0].set(\extCh, busses[1]);
					// outputs: 1 & 4 & 6
					modules[0].set(\isCarrier, 1);
					modules[0].set(\l4, 0);
					modules[0].set(\outCh, outCh);
					modules[0].set(\imod, ((10.0 / params[0].at(\fmod)).pow(0.33)) * (amp / 2)); // Psychoacoustic amplitude compensation for carriers
					modules[3].set(\isCarrier, 1);
					modules[3].set(\l4, 0);
					modules[3].set(\outCh, outCh);
					modules[3].set(\imod, ((10.0 / params[3].at(\fmod)).pow(0.33)) * (amp / 2)); // Psychoacoustic amplitude compensation for carriers
					modules[5].set(\isCarrier, 1);
					modules[5].set(\l4, 0);
					modules[5].set(\outCh, outCh);
					modules[5].set(\imod, ((10.0 / params[5].at(\fmod)).pow(0.33)) * (amp / 2)); // Psychoacoustic amplitude compensation for carriers
				},
				/* PATCH 20 */
				20, {
					// Set unwanted feedbacks to 0
					[0, 2, 3, 4, 5].do({|id| modules[id].set(\feedback, 0)});
					// row 2->1
					modules[0].set(\extCh, busses[1]);
					// row 2->3
					modules[2].set(\extCh, busses[1]);
					// row 5->4
					modules[3].set(\extCh, busses[4]);
					// row 6->4
					modules[5].set(\outCh, busses[3]);
					// outputs: 1 & 3 & 4
					modules[0].set(\isCarrier, 1);
					modules[0].set(\l4, 0);
					modules[0].set(\outCh, outCh);
					modules[0].set(\imod, ((10.0 / params[0].at(\fmod)).pow(0.33)) * (amp / 3)); // Psychoacoustic amplitude compensation for carriers
					modules[2].set(\isCarrier, 1);
					modules[2].set(\l4, 0);
					modules[2].set(\outCh, outCh);
					modules[2].set(\imod, ((10.0 / params[2].at(\fmod)).pow(0.33)) * (amp / 3)); // Psychoacoustic amplitude compensation for carriers
					modules[3].set(\isCarrier, 1);
					modules[3].set(\l4, 0);
					modules[3].set(\outCh, outCh);
					modules[3].set(\imod, ((10.0 / params[3].at(\fmod)).pow(0.33)) * (amp / 3)); // Psychoacoustic amplitude compensation for carriers
				},
				/* PATCH 21 */
				21, {
					// Set unwanted feedbacks to 0
					[0, 2, 3, 4, 5].do({|id| modules[id].set(\feedback, 0)});
					// row 2->1
					modules[0].set(\extCh, busses[1]);
					// row 2->3
					modules[2].set(\extCh, busses[1]);
					// row 5->4
					modules[3].set(\extCh, busses[4]);
					// row 5->6
					modules[4].set(\outCh, busses[5]);
					// outputs: 1 & 3 & 4 & 6
					modules[0].set(\isCarrier, 1);
					modules[0].set(\l4, 0);
					modules[0].set(\outCh, outCh);
					modules[0].set(\imod, ((10.0 / params[0].at(\fmod)).pow(0.33)) * (amp / 4)); // Psychoacoustic amplitude compensation for carriers
					modules[2].set(\isCarrier, 1);
					modules[2].set(\l4, 0);
					modules[2].set(\outCh, outCh);
					modules[2].set(\imod, ((10.0 / params[2].at(\fmod)).pow(0.33)) * (amp / 4)); // Psychoacoustic amplitude compensation for carriers
					modules[3].set(\isCarrier, 1);
					modules[3].set(\l4, 0);
					modules[3].set(\outCh, outCh);
					modules[3].set(\imod, ((10.0 / params[3].at(\fmod)).pow(0.33)) * (amp / 4)); // Psychoacoustic amplitude compensation for carriers
					modules[5].set(\isCarrier, 1);
					modules[5].set(\l4, 0);
					modules[5].set(\outCh, outCh);
					modules[5].set(\imod, ((10.0 / params[5].at(\fmod)).pow(0.33)) * (amp / 4)); // Psychoacoustic amplitude compensation for carriers
				},
				/* PATCH 22 */
				22, {
					// Set unwanted feedbacks to 0
					[0, 2, 3, 4, 5].do({|id| modules[id].set(\feedback, 0)});
					// row 2->1
					modules[0].set(\extCh, busses[1]);
					// row 5->3
					modules[2].set(\extCh, busses[4]);
					// row 5->4
					modules[3].set(\extCh, busses[4]);
					// row 5->6
					modules[5].set(\extCh, busses[4]);
					// outputs: 1 & 3 & 4 & 6
					modules[0].set(\isCarrier, 1);
					modules[0].set(\l4, 0);
					modules[0].set(\outCh, outCh);
					modules[0].set(\imod, ((10.0 / params[0].at(\fmod)).pow(0.33)) * (amp / 4)); // Psychoacoustic amplitude compensation for carriers
					modules[2].set(\isCarrier, 1);
					modules[2].set(\l4, 0);
					modules[2].set(\outCh, outCh);
					modules[2].set(\imod, ((10.0 / params[2].at(\fmod)).pow(0.33)) * (amp / 4)); // Psychoacoustic amplitude compensation for carriers
					modules[3].set(\isCarrier, 1);
					modules[3].set(\l4, 0);
					modules[3].set(\outCh, outCh);
					modules[3].set(\imod, ((10.0 / params[3].at(\fmod)).pow(0.33)) * (amp / 4)); // Psychoacoustic amplitude compensation for carriers
					modules[5].set(\isCarrier, 1);
					modules[5].set(\l4, 0);
					modules[5].set(\outCh, outCh);
					modules[5].set(\imod, ((10.0 / params[5].at(\fmod)).pow(0.33)) * (amp / 4)); // Psychoacoustic amplitude compensation for carriers
				},
				/* PATCH 23 */
				23, {
					// Set unwanted feedbacks to 0
					[0, 1, 2, 3, 5].do({|id| modules[id].set(\feedback, 0)});
					// row 3->2
					modules[1].set(\extCh, busses[2]);
					// row 5->4
					modules[3].set(\extCh, busses[4]);
					// row 5->6
					modules[5].set(\extCh, busses[4]);
					// outputs: 1 & 2 & 4 & 6
					modules[0].set(\isCarrier, 1);
					modules[0].set(\l4, 0);
					modules[0].set(\outCh, outCh);
					modules[0].set(\imod, ((10.0 / params[0].at(\fmod)).pow(0.33)) * (amp / 4)); // Psychoacoustic amplitude compensation for carriers
					modules[1].set(\isCarrier, 1);
					modules[1].set(\l4, 0);
					modules[1].set(\outCh, outCh);
					modules[1].set(\imod, ((10.0 / params[1].at(\fmod)).pow(0.33)) * (amp / 4)); // Psychoacoustic amplitude compensation for carriers
					modules[3].set(\isCarrier, 1);
					modules[3].set(\l4, 0);
					modules[3].set(\outCh, outCh);
					modules[3].set(\imod, ((10.0 / params[3].at(\fmod)).pow(0.33)) * (amp / 4)); // Psychoacoustic amplitude compensation for carriers
					modules[5].set(\isCarrier, 1);
					modules[5].set(\l4, 0);
					modules[5].set(\outCh, outCh);
					modules[5].set(\imod, ((10.0 / params[5].at(\fmod)).pow(0.33)) * (amp / 4)); // Psychoacoustic amplitude compensation for carriers
				},
				/* PATCH 24 */
				24, {
					// Set unwanted feedbacks to 0
					[0, 1, 2, 3, 5].do({|id| modules[id].set(\feedback, 0)});
					// row 5->3
					modules[2].set(\extCh, busses[4]);
					// row 5->4
					modules[3].set(\extCh, busses[4]);
					// row 5->6
					modules[5].set(\extCh, busses[4]);
					// outputs: 1 & 2 & 3 & 4 & 6
					modules[0].set(\isCarrier, 1);
					modules[0].set(\l4, 0);
					modules[0].set(\outCh, outCh);
					modules[0].set(\imod, ((10.0 / params[0].at(\fmod)).pow(0.33)) * (amp / 5)); // Psychoacoustic amplitude compensation for carriers
					modules[1].set(\isCarrier, 1);
					modules[1].set(\l4, 0);
					modules[1].set(\outCh, outCh);
					modules[1].set(\imod, ((10.0 / params[1].at(\fmod)).pow(0.33)) * (amp / 5)); // Psychoacoustic amplitude compensation for carriers
					modules[2].set(\isCarrier, 1);
					modules[2].set(\l4, 0);
					modules[2].set(\outCh, outCh);
					modules[2].set(\imod, ((10.0 / params[2].at(\fmod)).pow(0.33)) * (amp / 5)); // Psychoacoustic amplitude compensation for carriers
					modules[3].set(\isCarrier, 1);
					modules[3].set(\l4, 0);
					modules[3].set(\outCh, outCh);
					modules[3].set(\imod, ((10.0 / params[3].at(\fmod)).pow(0.33)) * (amp / 5)); // Psychoacoustic amplitude compensation for carriers
					modules[5].set(\isCarrier, 1);
					modules[5].set(\l4, 0);
					modules[5].set(\outCh, outCh);
					modules[5].set(\imod, ((10.0 / params[5].at(\fmod)).pow(0.33)) * (amp / 5)); // Psychoacoustic amplitude compensation for carriers
				},
				/* PATCH 25 */
				25, {
					// Set unwanted feedbacks to 0
					[0, 1, 2, 3, 5].do({|id| modules[id].set(\feedback, 0)});
					// row 5->4
					modules[3].set(\extCh, busses[4]);
					// row 5->6
					modules[5].set(\extCh, busses[4]);
					// outputs: 1 & 2 & 3 & 4 & 6
					modules[0].set(\isCarrier, 1);
					modules[0].set(\l4, 0);
					modules[0].set(\outCh, outCh);
					modules[0].set(\imod, ((10.0 / params[0].at(\fmod)).pow(0.33)) * (amp / 5)); // Psychoacoustic amplitude compensation for carriers
					modules[1].set(\isCarrier, 1);
					modules[1].set(\l4, 0);
					modules[1].set(\outCh, outCh);
					modules[1].set(\imod, ((10.0 / params[1].at(\fmod)).pow(0.33)) * (amp / 5)); // Psychoacoustic amplitude compensation for carriers
					modules[2].set(\isCarrier, 1);
					modules[2].set(\l4, 0);
					modules[2].set(\outCh, outCh);
					modules[2].set(\imod, ((10.0 / params[2].at(\fmod)).pow(0.33)) * (amp / 5)); // Psychoacoustic amplitude compensation for carriers
					modules[3].set(\isCarrier, 1);
					modules[3].set(\l4, 0);
					modules[3].set(\outCh, outCh);
					modules[3].set(\imod, ((10.0 / params[3].at(\fmod)).pow(0.33)) * (amp / 5)); // Psychoacoustic amplitude compensation for carriers
					modules[5].set(\isCarrier, 1);
					modules[5].set(\l4, 0);
					modules[5].set(\outCh, outCh);
					modules[5].set(\imod, ((10.0 / params[5].at(\fmod)).pow(0.33)) * (amp / 5)); // Psychoacoustic amplitude compensation for carriers
				},
				/* PATCH 26 */
				26, {
					// Set unwanted feedbacks to 0
					[0, 1, 2, 3, 4].do({|id| modules[id].set(\feedback, 0)});
					// row 3->2
					modules[1].set(\extCh, busses[2]);
					// row 5->4
					modules[3].set(\extCh, busses[4]);
					// row 6->4
					modules[5].set(\outCh, busses[4]);
					// outputs: 1 & 2 & 4
					modules[0].set(\isCarrier, 1);
					modules[0].set(\l4, 0);
					modules[0].set(\outCh, outCh);
					modules[0].set(\imod, ((10.0 / params[0].at(\fmod)).pow(0.33)) * (amp / 3)); // Psychoacoustic amplitude compensation for carriers
					modules[1].set(\isCarrier, 1);
					modules[1].set(\l4, 0);
					modules[1].set(\outCh, outCh);
					modules[1].set(\imod, ((10.0 / params[1].at(\fmod)).pow(0.33)) * (amp / 3)); // Psychoacoustic amplitude compensation for carriers
					modules[3].set(\isCarrier, 1);
					modules[3].set(\l4, 0);
					modules[3].set(\outCh, outCh);
					modules[3].set(\imod, ((10.0 / params[3].at(\fmod)).pow(0.33)) * (amp / 3)); // Psychoacoustic amplitude compensation for carriers
				},
				/* PATCH 27 */
				27, {
					// Set unwanted feedbacks to 0
					[0, 1, 3, 4, 5].do({|id| modules[id].set(\feedback, 0)});
					// row 3->2
					modules[1].set(\extCh, busses[2]);
					// row 5->4
					modules[3].set(\extCh, busses[4]);
					// row 6->4
					modules[5].set(\outCh, busses[4]);
					// outputs: 1 & 2 & 4
					modules[0].set(\isCarrier, 1);
					modules[0].set(\l4, 0);
					modules[0].set(\outCh, outCh);
					modules[0].set(\imod, ((10.0 / params[0].at(\fmod)).pow(0.33)) * (amp / 3)); // Psychoacoustic amplitude compensation for carriers
					modules[1].set(\isCarrier, 1);
					modules[1].set(\l4, 0);
					modules[1].set(\outCh, outCh);
					modules[1].set(\imod, ((10.0 / params[1].at(\fmod)).pow(0.33)) * (amp / 3)); // Psychoacoustic amplitude compensation for carriers
					modules[3].set(\isCarrier, 1);
					modules[3].set(\l4, 0);
					modules[3].set(\outCh, outCh);
					modules[3].set(\imod, ((10.0 / params[3].at(\fmod)).pow(0.33)) * (amp / 3)); // Psychoacoustic amplitude compensation for carriers
				},
				/* PATCH 28 */
				28, {
					// Set unwanted feedbacks to 0
					[0, 1, 2, 3, 5].do({|id| modules[id].set(\feedback, 0)});
					// row 2->1
					modules[0].set(\extCh, busses[1]);
					// row 5->4->3
					modules[3].set(\extCh, busses[4]);
					modules[2].set(\extCh, busses[3]);
					// outputs: 1 & 3 & 6
					modules[0].set(\isCarrier, 1);
					modules[0].set(\l4, 0);
					modules[0].set(\outCh, outCh);
					modules[0].set(\imod, ((10.0 / params[0].at(\fmod)).pow(0.33)) * (amp / 3)); // Psychoacoustic amplitude compensation for carriers
					modules[2].set(\isCarrier, 1);
					modules[2].set(\l4, 0);
					modules[2].set(\outCh, outCh);
					modules[2].set(\imod, ((10.0 / params[2].at(\fmod)).pow(0.33)) * (amp / 3)); // Psychoacoustic amplitude compensation for carriers
					modules[5].set(\isCarrier, 1);
					modules[5].set(\l4, 0);
					modules[5].set(\outCh, outCh);
					modules[5].set(\imod, ((10.0 / params[5].at(\fmod)).pow(0.33)) * (amp / 3)); // Psychoacoustic amplitude compensation for carriers
				},
				/* PATCH 29 */
				29, {
					// Set unwanted feedbacks to 0
					[0, 1, 2, 3, 4].do({|id| modules[id].set(\feedback, 0)});
					// row 4->3
					modules[2].set(\extCh, busses[3]);
					// row 6->5
					modules[4].set(\extCh, busses[5]);
					// outputs: 1 & 2 & 3 & 5
					modules[0].set(\isCarrier, 1);
					modules[0].set(\l4, 0);
					modules[0].set(\outCh, outCh);
					modules[0].set(\imod, ((10.0 / params[0].at(\fmod)).pow(0.33)) * (amp / 4)); // Psychoacoustic amplitude compensation for carriers
					modules[1].set(\isCarrier, 1);
					modules[1].set(\l4, 0);
					modules[1].set(\outCh, outCh);
					modules[1].set(\imod, ((10.0 / params[1].at(\fmod)).pow(0.33)) * (amp / 4)); // Psychoacoustic amplitude compensation for carriers
					modules[2].set(\isCarrier, 1);
					modules[2].set(\l4, 0);
					modules[2].set(\outCh, outCh);
					modules[2].set(\imod, ((10.0 / params[2].at(\fmod)).pow(0.33)) * (amp / 4)); // Psychoacoustic amplitude compensation for carriers
					modules[4].set(\isCarrier, 1);
					modules[4].set(\l4, 0);
					modules[4].set(\outCh, outCh);
					modules[4].set(\imod, ((10.0 / params[4].at(\fmod)).pow(0.33)) * (amp / 4)); // Psychoacoustic amplitude compensation for carriers
				},
				/* PATCH 30 */
				30, {
					// Set unwanted feedbacks to 0
					[0, 1, 2, 3, 5].do({|id| modules[id].set(\feedback, 0)});
					// row 5->4->3
					modules[3].set(\extCh, busses[4]);
					modules[2].set(\extCh, busses[3]);
					// outputs: 1 & 2 & 3 & 6
					modules[0].set(\isCarrier, 1);
					modules[0].set(\l4, 0);
					modules[0].set(\outCh, outCh);
					modules[0].set(\imod, ((10.0 / params[0].at(\fmod)).pow(0.33)) * (amp / 4)); // Psychoacoustic amplitude compensation for carriers
					modules[1].set(\isCarrier, 1);
					modules[1].set(\l4, 0);
					modules[1].set(\outCh, outCh);
					modules[1].set(\imod, ((10.0 / params[1].at(\fmod)).pow(0.33)) * (amp / 4)); // Psychoacoustic amplitude compensation for carriers
					modules[2].set(\isCarrier, 1);
					modules[2].set(\l4, 0);
					modules[2].set(\outCh, outCh);
					modules[2].set(\imod, ((10.0 / params[2].at(\fmod)).pow(0.33)) * (amp / 4)); // Psychoacoustic amplitude compensation for carriers
					modules[5].set(\isCarrier, 1);
					modules[5].set(\l4, 0);
					modules[5].set(\outCh, outCh);
					modules[5].set(\imod, ((10.0 / params[5].at(\fmod)).pow(0.33)) * (amp / 4)); // Psychoacoustic amplitude compensation for carriers
				},
				/* PATCH 31 */
				31, {
					// Set unwanted feedbacks to 0
					[0, 1, 2, 3, 4].do({|id| modules[id].set(\feedback, 0)});
					// row 6->5
					modules[4].set(\extCh, busses[5]);
					// outputs: 1 & 2 & 3 & 4 & 5
					modules[0].set(\isCarrier, 1);
					modules[0].set(\l4, 0);
					modules[0].set(\outCh, outCh);
					modules[0].set(\imod, ((10.0 / params[0].at(\fmod)).pow(0.33)) * (amp / 5)); // Psychoacoustic amplitude compensation for carriers
					modules[1].set(\isCarrier, 1);
					modules[1].set(\l4, 0);
					modules[1].set(\outCh, outCh);
					modules[1].set(\imod, ((10.0 / params[1].at(\fmod)).pow(0.33)) * (amp / 5)); // Psychoacoustic amplitude compensation for carriers
					modules[2].set(\isCarrier, 1);
					modules[2].set(\l4, 0);
					modules[2].set(\outCh, outCh);
					modules[2].set(\imod, ((10.0 / params[2].at(\fmod)).pow(0.33)) * (amp / 5)); // Psychoacoustic amplitude compensation for carriers
					modules[3].set(\isCarrier, 1);
					modules[3].set(\l4, 0);
					modules[3].set(\outCh, outCh);
					modules[3].set(\imod, ((10.0 / params[3].at(\fmod)).pow(0.33)) * (amp / 5)); // Psychoacoustic amplitude compensation for carriers
					modules[4].set(\isCarrier, 1);
					modules[4].set(\l4, 0);
					modules[4].set(\outCh, outCh);
					modules[4].set(\imod, ((10.0 / params[4].at(\fmod)).pow(0.33)) * (amp / 5)); // Psychoacoustic amplitude compensation for carriers
				},
				/* PATCH 32 */
				32, {
					// Set unwanted feedbacks to 0
					[0, 1, 2, 3, 4].do({|id| modules[id].set(\feedback, 0)});
					// outputs: 1 & 2 & 3 & 4 & 5 & 6
					modules[0].set(\isCarrier, 1);
					modules[0].set(\l4, 0);
					modules[0].set(\outCh, outCh);
					modules[0].set(\imod, ((10.0 / params[0].at(\fmod)).pow(0.33)) * (amp / 6)); // Psychoacoustic amplitude compensation for carriers
					modules[1].set(\isCarrier, 1);
					modules[1].set(\l4, 0);
					modules[1].set(\outCh, outCh);
					modules[1].set(\imod, ((10.0 / params[1].at(\fmod)).pow(0.33)) * (amp / 6)); // Psychoacoustic amplitude compensation for carriers
					modules[2].set(\isCarrier, 1);
					modules[2].set(\l4, 0);
					modules[2].set(\outCh, outCh);
					modules[2].set(\imod, ((10.0 / params[2].at(\fmod)).pow(0.33)) * (amp / 6)); // Psychoacoustic amplitude compensation for carriers
					modules[3].set(\isCarrier, 1);
					modules[3].set(\l4, 0);
					modules[3].set(\outCh, outCh);
					modules[3].set(\imod, ((10.0 / params[3].at(\fmod)).pow(0.33)) * (amp / 6)); // Psychoacoustic amplitude compensation for carriers
					modules[4].set(\isCarrier, 1);
					modules[4].set(\l4, 0);
					modules[4].set(\outCh, outCh);
					modules[4].set(\imod, ((10.0 / params[4].at(\fmod)).pow(0.33)) * (amp / 6)); // Psychoacoustic amplitude compensation for carriers
					modules[5].set(\isCarrier, 1);
					modules[5].set(\l4, 0);
					modules[5].set(\outCh, outCh);
					modules[5].set(\imod, ((10.0 / params[5].at(\fmod)).pow(0.33)) * (amp / 6)); // Psychoacoustic amplitude compensation for carriers
				},
			);
			// Must free buffers when done!
			(dur+0.2).wait;
			busses.do({|bus| bus.free(clear: true)});
		}.play(AppClock);
	}


	// Write Synth Definitions to file
	writeSynthDef {
		SynthDef(\FRDDX7_module, { | fmod=440, imod=2, feedback=0.0, extCh=128, outCh=100, l4=0, l1=1, l2=0.7, l3=0.2, r1=0.01, r2=0.1, r3=0.7, r4=1, isCarrier=0 |
			var mod, feed, ext, env;
			l4 = Select.kr(isCarrier > 0, [l4, 0]);
			env = EnvGen.ar(Env.new([l4, l1, l2, l3, l4, l4], [r1, r2, r3, r4, r4], [(l1-l4).sign, (l2-l1).sign, (l3-l2).sign, (l4-l3).sign, 0] * 2), doneAction: 2);
			ext = InFeedback.ar(extCh, 1);
			feed = LocalIn.ar(1);
			fmod = fmod + ext + feed;
			imod = Select.kr(isCarrier > 0, [fmod * imod, imod]);
			mod = SinOsc.ar(fmod, 0,  imod * env);
			LocalOut.ar([mod * feedback]);
			mod = Select.ar(isCarrier > 0, [mod, Limiter.ar(LeakDC.ar(mod))]);
			mod = HPF.ar(mod, 20);

			Select.kr( isCarrier > 0, [ Out.ar(outCh, mod), Out.ar(outCh + [1], mod ) ]);
			//Out.ar(outCh, mod);
		}).writeDefFile.add;
	}

}


