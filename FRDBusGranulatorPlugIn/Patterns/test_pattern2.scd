{ | frdbusgranulatorplugin |
	Routine{
		var size = 100;
		loop{
			size.do({ | id |
				frdbusgranulatorplugin.spawn(amp: id * 0.15 / size, dur: 0.15, atk: 0.05, curve: 2, pan: (id * 2 / size) - 1);
				((size - id) * 0.5 / size).wait;
			});
		}
	}
}