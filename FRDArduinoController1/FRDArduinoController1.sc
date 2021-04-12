FRDArduinoController1 {
	var addr, baud, port, callback;
	var values, bindings;

	*new { | addr="", baud=9600 |
		^super.new.init(addr, baud);
	}

	init { | addr="", baud=9600 |
		bindings  = Dictionary.new;
		values = Dictionary.new;
		this.connect(addr, baud);
	}



	connect { | addr="", baud=9600 |
		addr = addr;
		baud = baud;
		try{
			port.close;
		};
		try{
			callback.stop;
		};

		port = SerialPort(addr, baud);
		callback = Routine{
			var str, byte, data;
			loop{
				if(port.read==10, {
					str = "";
					while({byte = port.read; byte !=13 }, {
						str= str++byte.asAscii;
					});
					str = str.split($ );
					str[1] = str[1].replace("+", "");
					if(bindings[str[0]].size > 0, {
						if(str[0].contains("e"), { // Con encoder incrementa/decrementa il valore di step
							values[str[0]] = values[str[0]] + (bindings[str[0]][2] * str[1].interpret);
							bindings[str[0]][0].perform(bindings[str[0]][1].asSymbol, values[str[0]]);
						}, { // Con bottone cambia lo stato
							values[str[0]] = str[1].interpret;
							if(bindings[str[0]][1].class == Array, {
								bindings[str[0]][0].perform(bindings[str[0]][1][values[str[0]]].asSymbol, values[str[0]]);
							}, {
								bindings[str[0]][0].perform(bindings[str[0]][1].asSymbol, values[str[0]]);
							});
						});
					});
				});
			}
		};
		callback.play(AppClock);
	}

	disconnect {
		callback.stop;
		port.close;
	}

	/* KEYS: ["ENCODER0", "ENCODER1", "ENCODER2", "ENCODER3", "BUTTON0", "BUTTON1", "BUTTON2", "BUTTON3"] */
	bind { | key, obj, func, val=0, step=0.1 |
		values[key] = val;
		bindings[key] = [obj, func, step];
		[bindings[key], values[key]].postln;
	}

	unbind { | key, id |
		bindings[key].removeAt(id);
	}

	unbindKey { | key |
		bindings[key] = [];
	}







}

