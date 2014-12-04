package vfsCore;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class CoreImportExport {
	private CoreIO cio;
	public CoreImportExport(CoreIO cio) {
		super();
		this.cio = cio;
	}
	

	//------------------//
	//IMPORTING ELEMENTS//
	//------------------//
	
	/**
	 * imports a file(java.io.File) to the root of the VFS disk, and give it the specfied name
	 * @param fileToAdd the File to import
	 * @param name the name of this file on the VFS
	 * @return the VFS File corresponding to the imported file
	 * @throws IOException
	 * @throws CoreIOException
	 */
	public vfsCore.File importFile(File fileToAdd, String name) throws IOException, CoreIOException{
		//We write it to the VFS disk
		long address = cio.writeToDisk(fileToAdd);
		//We create a new File (subclass of Hierarchy) element ((we have not interest to the parent element in the case of a file)
		vfsCore.File hFile = new vfsCore.File(name, address, fileToAdd.length(), null);
		return hFile;
	}
	
	/**
	 * imports a folder, and its files and sub-folders to the root of the VFS disk, preserving the hierarchy
	 * @param folderToAdd a java.io.File element corresponding to the folder
	 * @return a Folder (subclass of Hierarchy) corresponding to the imported folder
	 * @throws IOException
	 * @throws CoreIOException
	 */
	public Folder importFolder(File folderToAdd, String name, Hierarchy hierarchy) throws IOException, CoreIOException{
		Folder folder1 = new Folder(new ArrayList<Hierarchy>(), name, hierarchy);
		//We recursively browse the directory and its files & sub-directories
		for(File file1:folderToAdd.listFiles()){
			if (file1.isFile()){
				folder1.addChild(importFile(file1, file1.getName()));
			} else {
				folder1.addChild(importFolder(file1, file1.getName(),folder1));
			}
	    }
		return folder1;
	}
	
	
	
	//------------------//
	//EXPORTING ELEMENTS//
	//------------------//
	
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
		//Importing sub-directories and files
		for(Hierarchy file1:folder.getChildrens()){
			if (file1 instanceof vfsCore.File){
				//Exporting the file to the correct sub-directory
				exportFile((vfsCore.File)file1, destination+File.separator+file1.getName());
			} else {
				//Exporting the directory to the correct sub-directory
				exportFolder((Folder)file1, destination+File.separator+file1.getName());
			}
	    }
	}
}
