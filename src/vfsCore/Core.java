package vfsCore;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;


public class Core {
	private Hierarchy fullHierarchy;
	private Hierarchy currentNode;
	
	
	public boolean createDisk(String diskPath, int i) {
		
		try (RandomAccessFile randomAccessFile = new RandomAccessFile(new File(diskPath), "rw")) {

				
				randomAccessFile.seek(randomAccessFile.length());

				// obtain file channel from the RandomAccessFile
				try (FileChannel fileChannel = randomAccessFile.getChannel()) {

					ByteBuffer buf = ByteBuffer.allocate(1000);
					buf.clear();
					buf.putInt(i);

					buf.flip();

					while (buf.hasRemaining()) {
						fileChannel.write(buf);
					}

				} catch (IOException e) {
					e.printStackTrace();
				}

			} catch (IOException e) {
				e.printStackTrace();
			}

		
		
		return false;
	}
}
