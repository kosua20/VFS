package vfsCore.Compressor;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class ZipCompressor implements Compressor {
	
	public File compress(File original) throws IOException{
		byte[] buff = new byte[1024];
		
		FileOutputStream fOS = null;
		ZipOutputStream zOS = null;
		FileInputStream fIS = null;
		try {
			
			File compressed = File.createTempFile(original.getName(), ".zip");
			compressed.deleteOnExit();
			
			fOS = new FileOutputStream(compressed);
			zOS = new ZipOutputStream(fOS);
			fIS =  new FileInputStream(original);
			
			ZipEntry entry = new ZipEntry(original.getName());
			
			zOS.putNextEntry(entry);

			int used;
			while ((used = fIS.read(buff)) > 0) {
				zOS.write(buff,0,used);
			}
			
			return compressed;
			
		} finally {
			zOS.close();
			fOS.close();
			fIS.close();
		}
	}
	
	public File expand(File compressed, String destination) throws IOException {
		byte[] buff = new byte[1024];
		
		FileOutputStream fOS = null;
		ZipInputStream zIS = null;
		FileInputStream fIS = null;
		ZipEntry entry = null;
		try {
			
			File expanded =  new File(destination);
			
			fOS = new FileOutputStream(expanded);
			fIS = new FileInputStream(compressed);
			zIS = new ZipInputStream(fIS);
			
			entry = zIS.getNextEntry();
			if (entry != null){
				int used;
				while ((used = zIS.read(buff)) > 0) {
					fOS.write(buff,0,used);
				}
				
				return expanded;
			} else {
				throw new IOException();
			}
		} finally {
			if (entry != null){zIS.closeEntry();}
			if (zIS != null){zIS.close();}
			if (zIS != null){fOS.close();}
			if (zIS != null){fIS.close();}
		}
	}
}
