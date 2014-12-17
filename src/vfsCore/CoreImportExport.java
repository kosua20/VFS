package vfsCore;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import vfsCore.Compressor.Compressor;
import vfsCore.Compressor.ZipCompressor;

/**
 * The CoreImportExport is an intermediate class between the generality of the Core class and the specificity of the CoreIO class.
 * It provides a bridge between those two classes, specialized in the import and export of files and folders. 
 * @author simon
 *
 */
public class CoreImportExport {
	/**
	 * the CoreIO instance associated with this CoreImportExport
	 */
	private CoreIO cio;
	/**
	 * attributes for the compression. Compressor is an interface.
	 */
	private boolean isCompressionEnabled = true;
	private Compressor compressor;
	/**
	 * Constructor, using a CoreIO parameter
	 * @param cio
	 */
	public CoreImportExport(CoreIO cio) {
		super();
		this.cio = cio;
		/*By default, we are enabling a Zip compressor using the java.util.zip utilities
		But any compression process conforming to the Compressor class can be used*/
		this.compressor = new ZipCompressor();
	}
	
	public CoreImportExport(CoreIO cio, boolean compression) {
		super();
		this.cio = cio;
		this.isCompressionEnabled = compression;
		if (compression){
			this.compressor = new ZipCompressor();
		}
	}
	

	//------------------//
	//IMPORTING ELEMENTS//
	//------------------//
	
	/**
	 * imports a file(java.io.File) to the root of the VFS disk, and give it the specified name
	 * @param fileToAdd the File to import
	 * @param name the name of this file on the VFS
	 * @return the VFS File corresponding to the imported file
	 * @throws IOException
	 * @throws FileNotFoundException
	 */
	public vfsCore.File importFile(File fileToAdd, String name) throws IOException, FileNotFoundException{
		File toImport;
		if (!isCompressionEnabled){
			//Without compression
			toImport = fileToAdd;
		} else {
			//With compression
			toImport = compressor.compress(fileToAdd);
		}
		//We write it to the VFS disk
		long address = cio.writeToDisk(toImport);
		//We create a new File (subclass of Hierarchy) element ((we have not interest to the parent element in the case of a file)
		vfsCore.File hFile = new vfsCore.File(name, address, toImport.length(),null);
		if (isCompressionEnabled){
			//We delete the temp file
			toImport.delete();
		}
		return hFile;
	}
	
	/**
	 * imports a folder, and its files and sub-folders to the root of the VFS disk, preserving the hierarchy
	 * @param folderToAdd a java.io.File element corresponding to the folder
	 * @param name the name of the folder on the VFS disk
	 * @return a Folder (subclass of Hierarchy) corresponding to the imported folder
	 * @throws IOException
	 * @throws FileNotFoundException
	 */
	public Folder importFolder(File folderToAdd, String name) throws IOException, FileNotFoundException{
		Folder folder1 = new Folder(new ArrayList<Hierarchy>(), name,null);
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
			if(!isCompressionEnabled){
				//We call readFromAdress, with the size of the file
				cio.readFromAdress(file.getAddress(), destination, file.getSize());
			} else {
				//With compression
				File temp = File.createTempFile(file.getName(), ".zip");
				//We call readFromAdress, with the size of the file
				cio.readFromAdress(file.getAddress(), temp.getAbsolutePath(), file.getSize());
				compressor.expand(temp, destination);
				temp.delete();
			}
			return true;
		} catch (FileNotFoundException e) {
			System.out.println("Fichier non trouvé");
			e.printStackTrace();
			return false;
		} catch (Exception e) {
			System.out.println("Error with the CoreIO");
			e.printStackTrace();
			return false;
		}
	}
	
	/**
	 * exports a folder from the VFS disk, and its files and subfolders, on the host file system. The hierarchy is conserved
	 * @param folder the hierarchy Folder to export
	 * @param destination the name on the folder in the host file system
	 */
	public boolean exportFolder(Folder folder, String destination){
		//Creating the folder
		//System.out.println("exporting folder" + folder.getName());
		File folder1 = new File(destination);
		folder1.mkdir();
		boolean success = true;
		//Importing sub-directories and files
		for(Hierarchy file1:folder.getChildren()){
			if (file1 instanceof vfsCore.File){
				//Exporting the file to the correct sub-directory
				success = success && (exportFile((vfsCore.File)file1, destination+File.separator+file1.getName()));
			} else {
				//Exporting the directory to the correct sub-directory
				success = success && (exportFolder((Folder)file1, destination+File.separator+file1.getName()));
			}
	    }
		return success;
	}
}
