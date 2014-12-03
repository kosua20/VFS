package vfsCore;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;


public class Core {
	private Hierarchy fullHierarchy;
	private Hierarchy currentNode;
	private CoreIO cio;
	
	/**
	 * create a VFS disk at the specified path on the host system, with the specified size for the data partition
	 * @param diskPath the path where the Core should create
	 * @param size the size of the VFS data partition, in kilobytes
	 * @return true if the operation is successful
	 */
	public boolean createDisk(String diskPath, long size) {
		size = size * 1000;
		cio = new CoreIO(diskPath);
		try (RandomAccessFile randomAccessFile = new RandomAccessFile(new File(diskPath), "rw")) {
				randomAccessFile.setLength(size);
				randomAccessFile.seek(randomAccessFile.length());
				randomAccessFile.writeLong(size);
				randomAccessFile.close();
				cio.saveHierarchyToFile(new Folder(null, ""));
				return true;
			} catch (IOException e) {
				System.out.println("Error writing file");
				return false;
			} catch (CoreIOException e) {
				System.out.println("Error encountered by CoreIO");
				return false;
			} catch (fileNotFound e) {
				System.out.println("The file doesn't exist");
				return false;
			}
		
	}

	/**
	 * Open an already created disk and load its hierarchy in memory
	 * @param filePath the path to the .dsk file in the host file system
	 * @return true if the operation is successful
	 */
	public boolean openDisk(String filePath) {
		cio = new CoreIO(filePath);
		try {
			fullHierarchy = cio.loadHierarchyTreeFromFile();
			currentNode = fullHierarchy;
			System.out.println(currentNode);
			return true;
		} catch (CoreIOException e) {
			System.out.println("Error encountered by CoreIO");
			return false;
		} catch (fileNotFound e) {
			System.out.println("The file doesn't exist");
			return false;
		}
		
	}

	/**
	 * Delete a disk
	 * @param filePath the path to the .dsk file in the host file system
	 * @return true if the operation is successful
	 */
	public boolean deleteDisk(String filePath) {
		try {
			File disk = new File(filePath);
			disk.delete();
			return true;
		} catch (NullPointerException e){
			System.out.println("Error with the file");
			return false;
		}
		
	}
	
	
}
