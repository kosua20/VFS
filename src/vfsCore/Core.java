package vfsCore;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.util.ArrayList;


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
		size = size * (1024+8+1);
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

	public boolean importElement(String homePath, String VFSPath) {
		File fileToAdd = new File(homePath);
		//Check if the file exists on the host
		if (fileToAdd.exists()){
			//If this is a file
			if (fileToAdd.isFile()){
				try {
					//We import it
					vfsCore.File hFile = importFile(fileToAdd, VFSPath);
					//For now, we add it to the root
					fullHierarchy.addChild(hFile);
					return true;
				} catch (IOException | CoreIOException e) {
					System.out.println("Error importing the file");
					return false;
				}
			} else {
				//It's a folder
				try {
					//We import it
					vfsCore.Folder hFolder = importFolder(fileToAdd);
					//For now, we add them to the root
					fullHierarchy.addChild(hFolder);
					return true;
				} catch (IOException | CoreIOException e) {
					System.out.println("Error importing the folder");
					return false;
				}
			}
		} else {
			System.out.println("The element doesn't exists");
			return false;
		}
	}
	
	public vfsCore.File importFile(File fileToAdd, String name) throws IOException, CoreIOException{
		//Here check against the remaining space fileToAdd.length();
		long address = cio.writeToDisk(fileToAdd);
		vfsCore.File hFile = new vfsCore.File(name, address, fileToAdd.length());
		return hFile;
	}
	
	public Folder importFolder(File folderToAdd) throws IOException, CoreIOException{
		Folder folder1 = new Folder(new ArrayList<Hierarchy>(), folderToAdd.getName());
		//Here check sizeOfFolder(folderToAdd) against available space
		for(File file1:folderToAdd.listFiles()){
			if (file1.isFile()){
				folder1.addChild(importFile(file1, file1.getName()));
			} else {
				folder1.addChild(importFolder(file1));
			}
	    }
		return folder1;
	}
	
	public boolean exportElement(String VFSPath, String homePath){
		try {
			Hierarchy origin = ((Folder)fullHierarchy).findChild(VFSPath);
			if (origin instanceof vfsCore.File){
				exportFile((vfsCore.File)origin, homePath);
			} else {
				exportFolder((Folder)origin, homePath);
			}
		} catch (fileNotFound e) {
			System.out.println("The file doesn't exist");
		}
		return false;	
	}
	
	public boolean exportFile(vfsCore.File file, String destination){
		try {
			return cio.readFromAdress(file.getAddress(), destination, file.getSize());
		} catch (fileNotFound e) {
			System.out.println("Fichier non trouvé");
			return false;
		} catch (CoreIOException e) {
			System.out.println("Error with the coreIO");
			return false;
		}
	}
	
	public void exportFolder(Folder folder, String destination){
		//Creating the folder
		File folder1 = new File(destination);
		folder1.mkdir();
		//Importing subdirectories and files
		for(Hierarchy file1:folder.getChildrens()){
			if (file1 instanceof vfsCore.File){
				exportFile((vfsCore.File)file1, destination+File.separator+folder1.getName());
			} else {
				exportFolder((Folder)file1, destination+File.separator+folder1.getName());
			}
	    }
		
	}
	
	/**
	 * Returns the size of a folder, recursively browsing its sub-folders and files
	 * @param folder the folder we want to know the size of
	 * @return the size of the folder
	 */
	public long sizeOfFolder(File folder){
		long size = 0;
		if (folder.exists()){
			for(File file1:folder.listFiles()){
				if (file1.isFile()){
					size = size + file1.length();
				} else {
					size = size + sizeOfFolder(file1);
				}
		    }
		}
		return size;
	}
	
	
	
}
