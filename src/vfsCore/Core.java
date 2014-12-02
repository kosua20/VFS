package vfsCore;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;


public class Core {
	private Hierarchy fullHierarchy;
	private Hierarchy currentNode;
	
	//test
	public boolean createDisk(String diskPath, int size) {
		
		try (RandomAccessFile randomAccessFile = new RandomAccessFile(new File(diskPath), "rw")) {

				
				//randomAccessFile.seek(randomAccessFile.length());
				randomAccessFile.setLength(size);
				randomAccessFile.seek(randomAccessFile.length());
				try (FileChannel fileChannel = randomAccessFile.getChannel()) {

					ByteBuffer buf = ByteBuffer.allocate(100);
					buf.clear();
					buf.putInt(size);

					buf.flip();

					while (buf.hasRemaining()) {
						fileChannel.write(buf);
					}
					return true;

				} catch (IOException e) {
					System.out.println("Error writing file");
					return false;
				}

			} catch (IOException e) {
				System.out.println("Error writing file");
				return false;
			}
		

		
		
		//return false;
	}
}
