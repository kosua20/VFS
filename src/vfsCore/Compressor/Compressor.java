package vfsCore.Compressor;

import java.io.File;
import java.io.IOException;

public interface Compressor {
	public File expand(File compressed, String destination) throws IOException;
	public File compress(File original) throws IOException;
}
