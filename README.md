# FRDSClib
SuperCollider library for live electronics.
author: Francesco Roberto Dani
mail: f.r.d@hotmail.it
license: GPL v3

# Installation

Move the FRDSClib folder into ```Platform.userExtensionsDir() folder.```, and run the following code to install FRDSClib.
```
(
var path = "".resolveRelative;
FRDBufferGranulator().writeSynthDef();
FRDChimes().writeSynthDef();
FRDContinuousBufferGranulator(Buffer.alloc(s, 44100)).writeSynthDef();
FRDContinuousBusGranulator().writeSynthDef();
FRDDX7PlugIn().writeSynthDef();
FRDExplosion().writeSynthDef();
FRDLiveInputPlugIn().writeSynthDef();
FRDLooperPlugIn().writeSynthDef();
FRDMIDIBassSynth().writeSynthDef();
FRDMIDIDrums().writeSynthDef();
FRDMIDISampler().writeSynthDef();
FRDMono2StereoPlugIn().writeSynthDef();
FRDPitchTrackRingModulatorPlugIn().writeSynthDef();
FRDRingModulatorPlugIn().writeSynthDef();
FRDSampler("").writeSynthDef();
FRDSpatPlugIn().writeDefFile();
FRDThunders().writeSynthDef();

["backend/", "audio_widgets/"].do({ | subpath |
	PathName(path ++ subpath).filesDo({ | file |
		file = file.asAbsolutePath;
		f = File(file, "r");
		f = f.readAllString.interpret;
	});
});
0.exit();
)
```