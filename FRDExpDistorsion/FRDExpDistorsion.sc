FRDExpDistorsion {
	*ar { | in=0, boost=1 |
		in = in * boost;
		^( ( ( in + 0.0001 ) / ( in.abs + 0.0001 ) ) * ( 1.0 - exp( in.abs.neg ) ) );
	}
}