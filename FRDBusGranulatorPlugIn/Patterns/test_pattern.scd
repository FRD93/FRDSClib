{ | frdbusgranulatorplugin |
	Routine{
		var size = 100;
		loop{
			size.do({ | id |
				frdbusgranulatorplugin.spawn(amp: ((id + 1) * 0.15 / size).pow(2) * 0.7, dur: 0.05, atk: 0.05, curve: 2, pan: (id * 2 / size) - 1);
				((size - id) * 0.5 / size).wait;
			});
		}
	}
}