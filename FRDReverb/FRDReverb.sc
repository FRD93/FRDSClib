FRDReverb {
	*ar {|fl, fr, feed = 0.1, width = 0.8, lowshelff=160, lowshelfg=0, hishelff=2625, hishelfg=0, hpf=10, lpf=20000, wet = 0.5|

		var size = 16;

		var cdur = ControlDur.ir;
		var feed_delays, delays_fl, delays_fr, lin, rctrig, out;


		feed = feed.clip(0.0, 1.0);
		width = 1.0 - width.clip(0.0, 1.0);
		wet = wet.clip(0.0, 1.0);
		lowshelff = lowshelff.clip(5.0, 20000.0);
		lowshelfg = lowshelfg.clip(-60.0, 20.0);
		hishelff = hishelff.clip(5.0, 20000.0);
		hishelfg = hishelfg.clip(-60.0, 20.0);


		hpf = hpf.clip(5, 20000);
		lpf = lpf.clip(5, 20000);

		feed = Lag.ar(K2A.ar(feed), 0.1);
		width = Lag.ar(K2A.ar(width), 0.1);
		wet = Lag.ar(K2A.ar(wet), 0.1);
		lowshelff = Lag.ar(K2A.ar(lowshelff), 0.1);
		lowshelfg = Lag.ar(K2A.ar(lowshelfg), 0.1);
		hishelff = Lag.ar(K2A.ar(hishelff), 0.1);
		hishelfg = Lag.ar(K2A.ar(hishelfg), 0.1);


		hpf = Lag.ar(K2A.ar(hpf), 0.1);
		lpf = Lag.ar(K2A.ar(lpf), 0.1);

		feed_delays = LocalIn.ar(size * 2);

		delays_fl = size.collect({|id|
			//DelayC.ar(BLowShelf.ar(BHiShelf.ar(fl, hishelff, 1, hishelfg), lowshelff, 1, lowshelfg) + feed_delays[id] + (feed_delays[id+size] * width), 0.15, rrand(0.005, 0.15) - cdur, feed );
			DelayC.ar(fl + ((feed_delays[id] + (feed_delays[id+size] * width))), 0.15, rrand(0.005, 0.15) - cdur, feed );
		});

		delays_fr = size.collect({|id|
			//DelayC.ar(BLowShelf.ar(BHiShelf.ar(fl, hishelff, 1, hishelfg), lowshelff, 1, lowshelfg) + feed_delays[id+size] + (feed_delays[id] * width), 0.15, rrand(0.005, 0.15) - cdur, feed );
			DelayC.ar(fr + ((feed_delays[id+size] + (feed_delays[id] * width)) ), 0.15, rrand(0.005, 0.15) - cdur, feed );
		});


		LocalOut.ar(delays_fl ++ delays_fr);

		//^[SelectX.ar(wet, [fl, HPF.ar(LPF.ar(delays_fl.sum, lpf), hpf)]), SelectX.ar(wet, [fr, HPF.ar(LPF.ar(delays_fr.sum, lpf), hpf)])]
		//^[SelectX.ar(wet, [fl, BLowShelf.ar(BHiShelf.ar(delays_fl.sum, hishelff, 1, hishelfg), lowshelff, 1, lowshelfg)]), SelectX.ar(wet, [fr, BLowShelf.ar(BHiShelf.ar(delays_fr.sum, hishelff, 1, hishelfg), lowshelff, 1, lowshelfg)])]

		//out = [(fl * (1.0 - wet)) + (BLowShelf.ar(BHiShelf.ar(delays_fl.sum, hishelff, 1, hishelfg), lowshelff, 1, lowshelfg) * wet), (fr * (1.0 - wet)) + (BLowShelf.ar(BHiShelf.ar(delays_fr.sum, hishelff, 1, hishelfg), lowshelff, 1, lowshelfg) * wet)];

		out = [0, 0];

		out[0] = (fl * (1.0 - wet)) +
		(BHiShelf.ar(BLowShelf.ar(delays_fl.sum, lowshelff, 1, lowshelfg), hishelff, 1, hishelfg) * wet);
		out[1] = (fr * (1.0 - wet)) +
		(BHiShelf.ar(BLowShelf.ar(delays_fr.sum, lowshelff, 1, lowshelfg), hishelff, 1, hishelfg) * wet);

		out = LeakDC.ar(out);
		^out



	}
}


