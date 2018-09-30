package edu.ucdenver.ccp.knowtator.io;

import java.io.File;

public interface BasicIOUtil<I extends BasicIO> {
	void read(I reader, File file);

	void write(I writer, File file);
}
