o = Buffer.readChannel( s, "/Users/admin/Documents/SassofonoLiveElectronics/Rec/SC_181113_170139.aiff", 44100 * 125, 441000, 0 );
o = Buffer.readChannel( s, "/Users/admin/Desktop/rutto.wav", 0, -1, 0 );
o.play
(
//c = { Out.ar( 20, SinOsc.ar( 1220 + SinOsc.ar( 0.1, 0, 900 ), 0, 0.2 ) * Decay2.ar( Dust2.ar( 8 ) ) ); }.play;
//f = SFGrainShuffle( o, 0.5, 300, 0.8, 0.15, 0.5, 1, 0, 5, 20 );
c = {var trig = Dust2.kr( 2 ); Out.ar( 20, PlayBuf.ar( 1, o, TExpRand.kr(0.25, 2, trig ).round(0.25), trig ) ) }.play;
m = MixerMatrix.new();
d = FeedDel.new(22, 24, 2, 0.25, 0.7, 2, 1, 1);
p = Spat.new(26, 28, 2);
m.addProcess( "Synth", 0, 2, 0, 20 );
m.addProcess( "Out", 2, 2, 0, 0 );
m.addProcess( "FeedDel", 2, 2, 22, 24 );
m.addProcess( "Spat", 2, 2, 26, 28 );
//m.writePresetFile();
//m.readPresetFile("tmp");
/*
m.addProcess( "P3", 4, 2, 20, 22 );
m.addProcess( "P4", 1, 1, 20, 22 );
m.addProcess( "P5", 0, 2, 20, 22 );
m.addProcess( "P6", 0, 2, 20, 22 );
m.addProcess( "P7", 0, 4, 20, 22 );
m.addProcess( "P8", 0, 1, 20, 22 );*/

)

f.play;

f.rate_( 0.5 );
c.free
s.options.numInputBusChannels_( 2 );
s.options.numOutputBusChannels_( 2 );

d = FeedDel.new(22, 24, 2, 0.25, 0.7, 2, 1, 1);
d.showGUI
d.writePresetFile()
d.readPresetFile("tmp")
o = Buffer.readChannel( s, "/Users/admin/Documents/SassofonoLiveElectronics/Rec/SC_181113_170139.aiff", 44100 * 125, 441000, 0 );

f.play
o.play
o.plot
{SinOsc.ar( 300, 0, 0.1 ) ! 2}.play


a = (11@10)
a.y = 8
a

p = Spat.new(26, 28, 2);



(
k = [
	Synth( \Drone1, [ \freq, 2867, \lpf, 5000, \detune, 0.4, \amp, 0.01, \lagT, 11 ] ),
	Synth( \Drone1, [ \freq, 2007, \lpf, 4000, \detune, 0.3, \amp, 0.01, \lagT, 17 ] ),
	Synth( \Drone1, [ \freq, 1369, \lpf, 3000, \detune, 0.7, \amp, 0.01, \lagT, 19 ] ),
	Synth( \Drone1, [ \freq, 3769, \lpf, 8000, \detune, 0.5, \amp, 0.01, \lagT, 13 ] ),
];
)
k[0].set( \freq, 1012 );
k[1].set( \freq, 3112 );
k.collect({|ki|ki.free})



k = Synth( \Drone1, [ \freq, 30 ] );
k.set( \freq, 5028 );
k.set( \lpf, 3538 );
k.set( \detune, 5 );

k.set( \lpf, 0 );
k.set( \lpf, 3538 );
(
SynthDef( \Drone1, { | freq=120, amp=0.5, lpf=3000, detune=1, gate=1, lagT=1, outCh=0 |
	var env, osc, feed;
	feed = LocalIn.ar( 1 );

	freq = Lag2.ar( K2A.ar( freq ), lagT );
	lpf = Lag2.ar( K2A.ar( lpf ), lagT );
	detune = Lag2.ar( K2A.ar( detune ), lagT );

	freq = freq + ( [ 0.01, -0.02, 0.045, - 0.09 ] * detune );

	osc = LFSaw.ar( freq, 0, 1 ).sum / freq.size;
	osc = BLowPass4.ar( osc, lpf );
	osc = osc * SinOsc.ar( freq * 1.5, 0, 0.25, 0.75 );
	osc = osc.sum / freq.size;
	osc = osc + ( feed * 0.5 );
	LocalOut.ar( DelayC.ar( osc, 1, 0.08 * SinOsc.ar( 0.095, 0, 0.09, 1 ) ) );

	env = EnvGen.ar( Env.adsr( lagT, 0, 1, lagT, 1 ), gate, amp, 0, 1, 2 );
	osc = osc * env;

	Out.ar( outCh, osc );
} ).add;
)




s.freqscope






