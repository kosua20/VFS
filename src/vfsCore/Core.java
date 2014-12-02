package vfsCore;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;


public class Core {
	private Hierarchy fullHierarchy;
	private Hierarchy currentNode;
	
	public boolean createDisk(String diskPath, long size) {
		
		try (RandomAccessFile randomAccessFile = new RandomAccessFile(new File(diskPath), "rw")) {
				
				
				
				
				randomAccessFile.setLength(size);
				randomAccessFile.seek(randomAccessFile.length());
				randomAccessFile.writeLong(size);
				return true;
				//Create a new hierarchy of type folder root, and serialize it.
				/*
				try (FileChannel fileChannel = randomAccessFile.getChannel()) {

					ByteBuffer buf = ByteBuffer.allocate(100);
					buf.clear();
					buf.flip();

					while (buf.hasRemaining()) {
						fileChannel.write(buf);
					}
					

				} catch (IOException e) {
					System.out.println("Error writing file");
					return false;
				}*/

			} catch (IOException e) {
				System.out.println("Error writing file");
				return false;
			}
		

		
		
		//return false;
	}
}
