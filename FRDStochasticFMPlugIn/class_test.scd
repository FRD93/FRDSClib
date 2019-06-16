
(
t = TempoClock(tempo: 5, beats: 4, queueSize: 32768);
f = FRDStochasticFMPlugIn(tempoClock: t, freq_min: 100, freq_max: 4000, freq_step: 41, freq_curr: 180, imod_min: 0.1);
f.play(t, [1, 0, 0]);
f.showGUI;
)
f.play;
f.stop;

f.calcDurWait
f.next
d = f.dict

t.tempo_(5)

d.at(\Parameters).at(\complexity)
d.at(\Parameters).at(\complexity).at(\curr)


[0, 1, 2, 3, 4, 5, 6, 7][3..]

s.options.memSize
s.options.numPrivateAudioBusChannels
s.options.memSize = 2097152
s.options.numPrivateAudioBusChannels = 32768
s.quit
s.boot
b = Bus.audio(s, 1)
b.free

s.freqscope

TempoClock.new()

(0..5).clip(2, 3)



(

)
























