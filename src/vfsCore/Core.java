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
	private Hierarchy currentHierarchy;
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
				cio.saveHierarchyToFile(new Folder(null, "", null));
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
			currentHierarchy = fullHierarchy;
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

	/**
	 * imports an element from the host file system. For now, this method copy an element from the project directory to the root of the VFS
	 * @param homePath the name of the element on the host disk
	 * @param VFSPath the name of the element on the vfs
	 * @return  true if the operation is successful
	 */
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
					//And we save the modified Hierarchy
					cio.saveHierarchyToFile(fullHierarchy);
					return true;
				} catch (IOException | CoreIOException | fileNotFound e) {
					System.out.println("Error importing the file");
					return false;
				}
			} else {
				//It's a folder
				try {
					//We import it
					vfsCore.Folder hFolder = importFolder(fileToAdd, VFSPath);
					//For now, we add them to the root
					fullHierarchy.addChild(hFolder);
					//And we save the modified Hierarchy
					cio.saveHierarchyToFile(fullHierarchy);
					return true;
				} catch (IOException | CoreIOException | fileNotFound e) {
					System.out.println("Error importing the folder");
					return false;
				}
			}
		} else {
			System.out.println("The element doesn't exists");
			return false;
		}
	}
	
	/**
	 * imports a file(java.io.File) to the root of the VFS disk, and give it the specfied name
	 * @param fileToAdd the File to import
	 * @param name the name of this file on the VFS
	 * @return the VFS File corresponding to the imported file
	 * @throws IOException
	 * @throws CoreIOException
	 */
	public vfsCore.File importFile(File fileToAdd, String name) throws IOException, CoreIOException{
		//Here check against the remaining space fileToAdd.length();
		//We write it to the VFS disk
		long address = cio.writeToDisk(fileToAdd);
		//We create a new File (subclass of Hierarchy) element
		vfsCore.File hFile = new vfsCore.File(name, address, fileToAdd.length(), fullHierarchy);
		return hFile;
	}
	
	/**
	 * imports a folder, and its files and subfolders to the root of the VFS disk, preserving the hierarchy
	 * @param folderToAdd a java.io.File element corresponding to the folder
	 * @return a Folder (subclass of Hierarchy) corresponding to the imported folder
	 * @throws IOException
	 * @throws CoreIOException
	 */
	public Folder importFolder(File folderToAdd, String name) throws IOException, CoreIOException{
		Folder folder1 = new Folder(new ArrayList<Hierarchy>(), name, fullHierarchy);
		//Here check sizeOfFolder(folderToAdd) against available space
		//We recursively browse the directory and its files & sub-directories
		for(File file1:folderToAdd.listFiles()){
			if (file1.isFile()){
				folder1.addChild(importFile(file1, file1.getName()));
			} else {
				folder1.addChild(importFolder(file1, file1.getName()));
			}
	    }
		return folder1;
	}
	
	/**
	 * exports an element from the VFS .dsk to the project directory (for now) on the host file system 
	 * @param VFSPath the path of the element in the VFS hierarchy
	 * @param homePath the name of the element once exported to the host disk
	 * @return true if the operation is successful
	 */
	public boolean exportElement(String VFSPath, String homePath){
		try {
			//We find the pointer to the Hierarchy element at the given VFSPath
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
	
	/**
	 * exports a File (vfsCore.File) to the host system, with the name destination (in the next version, this will allow to specify any path)
	 * @param file the file to export
	 * @param destination the name of the file once exported
	 * @return true if the operation is successful
	 */
	public boolean exportFile(vfsCore.File file, String destination){
		try {
			System.out.println("Exporting file" + file.getName());
			//We call readFromAdress, with the size of the file
			return cio.readFromAdress(file.getAddress(), destination, file.getSize());
		} catch (fileNotFound e) {
			System.out.println("Fichier non trouvé");
			return false;
		} catch (CoreIOException e) {
			System.out.println("Error with the coreIO");
			return false;
		}
	}
	
	/**
	 * exports a folder from the VFS disk, and its files and subfolders, on the host file system. The hierarchy is conserved
	 * @param folder the hierarchy Folder to export
	 * @param destination the name on the folder in the host file system
	 */
	public void exportFolder(Folder folder, String destination){
		//Creating the folder
		System.out.println("exporting folder" + folder.getName());
		File folder1 = new File(destination);
		folder1.mkdir();
		//Importing subdirectories and files
		for(Hierarchy file1:folder.getChildrens()){
			if (file1 instanceof vfsCore.File){
				//Exporting the file to the correct subdirectory
				exportFile((vfsCore.File)file1, destination+File.separator+file1.getName());
			} else {
				//Exporting the directory to the correct subdirectory
				exportFolder((Folder)file1, destination+File.separator+file1.getName());
			}
	    }
	}
}
