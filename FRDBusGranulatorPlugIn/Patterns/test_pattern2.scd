{ | frdbusgranulatorplugin |
	Routine{
		var size = 100;
		size.do({ | id |
			frdbusgranulatorplugin.spawn(amp: id * 0.5 / size, dur: 0.05, atk: 0.05, curve: 2, pan: (id * 2 / size) - 1);
			((size - id) * 0.1 / size).wait;
		});
	}
}