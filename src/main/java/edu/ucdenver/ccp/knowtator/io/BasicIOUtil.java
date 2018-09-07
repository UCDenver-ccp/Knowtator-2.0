package edu.ucdenver.ccp.knowtator.io;

import java.io.File;
import java.io.IOException;

public interface BasicIOUtil<I extends BasicIO> {
	void read(I reader, File file) throws IOException;

	void write(I writer, File file) throws IOException;
}
