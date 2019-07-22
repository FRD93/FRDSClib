var beats = 32;
var instrArgs = [ [ \Kick , [ \amp , \dur , \midiInit , \midiEnd , \outBus , ] ],
[ \KickTone , [ \freq , \dur , \amp , \dist , \atk , \outBus , ] ],
[ \Snare , [ \amp , \dur , \midiInit , \midiEnd , \outBus , ] ],
[ \Clap , [ \amp , \shift , \body , \outBus , ] ],
[ \Hat , [ \amp , \hpf , \shift , \outBus , ] ],
];
var onsets = [ [ 0, 0, 0, 0, 0 ],
[ 0, 0, 0, 0, 0 ],
[ 0, 0, 0, 0, 0 ],
[ 0, 0, 0, 0, 0 ],
[ 0, 0, 0, 0, 0 ],
[ 0, 0, 0, 0, 0 ],
[ 0, 0, 0, 0, 0 ],
[ 0, 0, 0, 0, 0 ],
[ 0, 0, 0, 0, 0 ],
[ 0, 0, 0, 0, 0 ],
[ 0, 0, 0, 0, 0 ],
[ 0, 0, 0, 0, 0 ],
[ 0, 0, 0, 0, 0 ],
[ 0, 0, 0, 0, 0 ],
[ 0, 0, 0, 0, 0 ],
[ 0, 0, 0, 0, 0 ],
[ 0, 0, 0, 0, 0 ],
[ 0, 0, 0, 0, 0 ],
[ 0, 0, 0, 0, 0 ],
[ 0, 0, 0, 0, 0 ],
[ 0, 0, 0, 0, 0 ],
[ 0, 0, 0, 0, 0 ],
[ 0, 0, 0, 0, 0 ],
[ 0, 0, 0, 0, 0 ],
[ 0, 0, 0, 0, 0 ],
[ 0, 0, 0, 0, 0 ],
[ 0, 0, 0, 0, 0 ],
[ 0, 0, 0, 0, 0 ],
[ 0, 0, 0, 0, 0 ],
[ 0, 0, 0, 0, 0 ],
[ 0, 0, 0, 0, 0 ],
[ 0, 0, 0, 0, 0 ],
];
var textFuncs = [ [
[
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
],
[
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
],
[
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
],
[
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
],
[
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
],
],
[
[
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
],
[
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
],
[
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
],
[
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
],
[
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
],
],
[
[
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
],
[
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
],
[
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
],
[
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
],
[
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
],
],
[
[
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
],
[
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
],
[
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
],
[
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
],
[
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
],
],
[
[
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
],
[
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
],
[
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
],
[
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
],
[
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
],
],
[
[
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
],
[
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
],
[
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
],
[
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
],
[
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
],
],
[
[
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
],
[
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
],
[
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
],
[
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
],
[
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
],
],
[
[
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
],
[
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
],
[
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
],
[
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
],
[
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
],
],
[
[
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
],
[
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
],
[
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
],
[
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
],
[
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
],
],
[
[
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
],
[
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
],
[
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
],
[
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
],
[
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
],
],
[
[
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
],
[
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
],
[
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
],
[
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
],
[
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
],
],
[
[
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
],
[
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
],
[
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
],
[
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
],
[
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
],
],
[
[
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
],
[
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
],
[
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
],
[
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
],
[
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
],
],
[
[
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
],
[
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
],
[
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
],
[
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
],
[
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
],
],
[
[
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
],
[
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
],
[
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
],
[
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
],
[
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
],
],
[
[
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
],
[
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
],
[
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
],
[
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
],
[
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
],
],
[
[
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
],
[
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
],
[
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
],
[
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
],
[
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
],
],
[
[
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
],
[
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
],
[
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
],
[
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
],
[
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
],
],
[
[
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
],
[
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
],
[
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
],
[
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
],
[
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
],
],
[
[
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
],
[
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
],
[
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
],
[
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
],
[
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
],
],
[
[
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
],
[
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
],
[
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
],
[
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
],
[
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
],
],
[
[
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
],
[
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
],
[
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
],
[
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
],
[
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
],
],
[
[
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
],
[
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
],
[
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
],
[
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
],
[
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
],
],
[
[
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
],
[
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
],
[
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
],
[
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
],
[
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
],
],
[
[
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
],
[
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
],
[
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
],
[
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
],
[
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
],
],
[
[
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
],
[
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
],
[
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
],
[
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
],
[
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
],
],
[
[
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
],
[
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
],
[
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
],
[
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
],
[
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
],
],
[
[
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
],
[
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
],
[
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
],
[
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
],
[
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
],
],
[
[
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
],
[
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
],
[
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
],
[
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
],
[
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
],
],
[
[
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
],
[
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
],
[
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
],
[
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
],
[
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
],
],
[
[
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
],
[
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
],
[
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
],
[
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
],
[
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
],
],
[
[
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
],
[
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
],
[
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
],
[
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
],
[
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
"{ | index |
0
}",
],
],
];
[ instrArgs, onsets, textFuncs, beats ]