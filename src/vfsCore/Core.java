package vfsCore;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;


public class Core {
	private Hierarchy fullHierarchy;
	private Hierarchy currentNode;
	
	/**
	 * create a VFS disk at the specified path on the host system, with the specified size for the data partition
	 * @param diskPath the path where the Core should create
	 * @param size the size of the VFS data partition, in bytes
	 * @return
	 */
	public boolean createDisk(String diskPath, long size) {
		try (RandomAccessFile randomAccessFile = new RandomAccessFile(new File(diskPath), "rw")) {
				randomAccessFile.setLength(size);
				randomAccessFile.seek(randomAccessFile.length());
				randomAccessFile.writeLong(size);
				return true;
			} catch (IOException e) {
				System.out.println("Error writing file");
				return false;
			}
	}
}
