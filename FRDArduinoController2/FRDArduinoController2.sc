FRDArduinoController2 {
	var addr, baud, port, callback;
	var values, bindings;

	var j1, j2, j3, j4, j5;
	var joy_step = 0.01;



	*new { | addr="/dev/cu.wchusbserial1410", baud=9600 |
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
					str.postln;
					/* QUICK JOYSTICK TEST */
					if(str[0] == "Joy1X", { try{ j1.x_(j1.x + (str[1].asInteger * joy_step)); } });
					if(str[0] == "Joy1Y", { try{ j1.y_(j1.y + (str[1].asInteger * joy_step)); } });

					if(str[0] == "Joy2X", { try{ j2.x_(j2.x + (str[1].asInteger * joy_step)); } });
					if(str[0] == "Joy2Y", { try{ j2.y_(j2.y + (str[1].asInteger * joy_step)); } });

					if(str[0] == "Joy3X", { try{ j3.x_(j3.x + (str[1].asInteger * joy_step)); } });
					if(str[0] == "Joy3Y", { try{ j3.y_(j3.y + (str[1].asInteger * joy_step)); } });

					if(str[0] == "Joy4X", { try{ j4.x_(j4.x + (str[1].asInteger * joy_step)); } });
					if(str[0] == "Joy4Y", { try{ j4.y_(j4.y + (str[1].asInteger * joy_step)); } });

					if(str[0] == "Joy5X", { try{ j5.x_(j5.x + (str[1].asInteger * joy_step)); } });
					if(str[0] == "Joy5Y", { try{ j5.y_(j5.y + (str[1].asInteger * joy_step)); } });
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


	showGUI {

		var win;
		win = Window.new("Joysticks").front;
		j1 = Slider2D().setXY(0.5, 0.5);
		j2 = Slider2D().setXY(0.5, 0.5);
		j3 = Slider2D().setXY(0.5, 0.5);
		j4 = Slider2D().setXY(0.5, 0.5);
		j5 = Slider2D().setXY(0.5, 0.5);
		win.layout_(
			HLayout(
				j1, j2, j3, j4, j5
			)
		)
	}






}








































