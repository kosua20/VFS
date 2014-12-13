package vfsCore;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;

import vfsCore.exceptions.AlreadyExistException;
import vfsCore.exceptions.BadPathInstanceException;
import vfsCore.exceptions.fileNotFound;
import vfsCore.visitors.SearchVisitor;
import vfsCore.visitors.SizeVisitor;


public class Core {
	//ATTRIBUTES
	private Hierarchy fullHierarchy;
	private Hierarchy currentHierarchy;
	private CoreIO cio;
	private CoreImportExport cie;
	
	
	
	//----------------------//
	//MANAGING THE VFS DISKS//
	//----------------------//
	
	/**
	 * create a VFS disk at the specified path on the host system, with the specified size for the data partition
	 * @param diskPath the path where the Core should create
	 * @param size the size of the VFS data partition, in kilobytes
	 * @return true if the operation is successful
	 */
	public boolean createDisk(String diskPath, long size) {
		size = size * (1024+8+1);
		cio = new CoreIO(diskPath);
		RandomAccessFile rAF = null;
		try {
				rAF = new RandomAccessFile(new File(diskPath), "rw");
				rAF.setLength(size);
				rAF.seek(rAF.length());
				rAF.writeLong(size);
				fullHierarchy = new Folder(null, "", null);
				currentHierarchy = fullHierarchy;
				return saveFullHierarchyToFile();
			} catch (FileNotFoundException e) {
				System.out.println("The file doesn't exist");
				return false;
			} catch (IOException e) {
				System.out.println("Error with the CoreIO");
				return false;
			} finally {
				if (rAF != null){
					try {
						rAF.close();
					} catch (IOException e) {
						System.out.println("IO error");
						return false;
					}
				}
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
		} catch (FileNotFoundException e) {
			System.out.println("The file doesn't exist");
			return false;
		}catch (IOException e) {
			System.out.println("Error encountered by CoreIO");
			return false;
		}  catch (ClassNotFoundException e){
			System.out.println("Error with the serialisation");
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
		} catch (Exception e){
			System.out.println("Error with the file");
			return false;
		}	
	}

	
	
	//----------------------------------//
	//MOVING IN AND SAVING THE HIERARCHY//
	//----------------------------------//
	
	/**
	 * save the hierarchy to the currently opened VFS .dsk file
	 * @return true if the save is successful
	 */
	public boolean saveFullHierarchyToFile(){
		try {
			cio.saveHierarchyToFile(fullHierarchy);
			return true;
		} catch (FileNotFoundException e) {
			System.out.println("Disk doesn't exist");
			return false;
		} catch (IOException e) {
			System.out.println("Error in the CoreIO");
			return false;
		}
	}
	
	/**
	 * moves to a subHierarchy of the current Hierarchy
	 * @param path the path to the subhierarchy, relative
	 * @return true if successful, false else
	 */
	public boolean goTo(String path){
		try {
			currentHierarchy = currentHierarchy.findChild(path);
			return true;
		} catch (fileNotFound e) {
			System.out.println("Sorry, the folder doesn't exist");
			return false;
		}
	}
	
	/**
	 * moves to the parent of the current element
	 * @return true if move successful, false if we are at the root
	 */
	public  boolean goToParent() {
		if (currentHierarchy.equals(fullHierarchy)){
			System.out.println("You already are on the root of the vfs disk");
			return false;
		} else {
			currentHierarchy = currentHierarchy.getParent();
		}
		return true;
	}
	
	/**
	 * lists the elements stored in the current folder, giving the name of each element, its type (file (f) or Folder (F)), and the size of the files in bytes
	 */
	public void list(){
			String s = "Current: "+currentHierarchy.getName()+"\n";
			for(Hierarchy child:currentHierarchy.getChildrens()){
				s = s+"- "+child.getName()+" ";
				if (child instanceof vfsCore.File){
					s = s +" f "+((vfsCore.File)child).getSize()+"B";
				}else {
					s = s + " F";
				}
				s = s+"\n";
			}
			System.out.println(s);
		}
	
		
		
	//------------------------------//
	//CREATING AND RENAMING ELEMENTS//
	//------------------------------//
	
	/**
	 * create a folder at the specified absolute path	
	 * @param path the folder where we should create the new folder
	 * @param name the name of the new folder
	 * @return true if the operation is successful
	 */
	public boolean createFolderAtPath(String path, String name) {
		try {
			fullHierarchy.createFolderAtPath(path, name);
			return saveFullHierarchyToFile();
		} catch (fileNotFound e) {
			System.out.println("The file doesn't exist");
			return false;
		} catch (BadPathInstanceException e) {
			System.out.println("Please, you need to specify an existing containing folder");
			return false;
		} catch (AlreadyExistException e){
			System.out.println("Please, change the name of the file, this name is already used here");
			return false;
		}
	}

	/**
	 * rename the folder at the specified absolute path
	 * @param path the absolute path to the folder
	 * @param name the new name of the folder
	 * @return true if the operation is successful
	 */
	public boolean renameFolderAtPath(String path, String name) {
		try {
			fullHierarchy.renameFolderAtPath(path, name);
			return saveFullHierarchyToFile();
		} catch (fileNotFound e) {
			System.out.println("The file doesn't exist");
			return false;
		} catch (BadPathInstanceException e) {
			System.out.println("Please, you need to specify a folder");
			return false;
		}catch (AlreadyExistException e){
			System.out.println("Please, change the name of the file, this name is already used here");
			return false;
		}
	}

	/**
	 * create a file at the specified absolute path	
	 * file is initialized with no address
	 * @param path the folder where we should create the new file
	 * @param name the name of the new file
	 * @return true if the operation is successful
	 */
	public boolean createFileAtPath(String path, String name) {
		try {
			fullHierarchy.createFileAtPath(path, name);
			return saveFullHierarchyToFile();
		} catch (fileNotFound e) {
			System.out.println("The file doesn't exist");
			return false;
		} catch (BadPathInstanceException e) {
			System.out.println("Please, you need to specify a folder");
			return false;
		}catch (AlreadyExistException e){
			System.out.println("Please, change the name of the file, this name is already used here");
			return false;
		}
	}

	/**
	 * rename the file at the specified absolute path
	 * @param path the absolute path to the file
	 * @param name the new name of the file
	 * @return true if the operation is successful
	 */
	public boolean renameFileAtPath(String path, String name) {
		try {
			fullHierarchy.renameFileAtPath(path, name);
			return saveFullHierarchyToFile();
		} catch (fileNotFound e) {
			System.out.println("The file doesn't exist");
			return false;
		} catch (BadPathInstanceException e) {
			System.out.println("Please, you need to specify a file");
			return false;
		}catch (AlreadyExistException e){
			System.out.println("Please, change the name of the file, this name is already used here");
			return false;
		}
	}
		
		
	
	//--------------------------------//
	//IMPORTING AND EXPORTING ELEMENTS//
	//--------------------------------//
	
	/**
	 * imports an element from the host file system. For now, this method copy an element from the project directory to the root of the VFS
	 * @param homePath the name of the element on the host disk
	 * @param VFSPath the name of the element on the vfs
	 * @return  true if the operation is successful
	 */
	public boolean importElement(String homePath, String VFSPath) {
		File fileToAdd = new File(homePath);
		//Lazily initializing the Import-Export Core
		if (cie == null){
			cie = new CoreImportExport(cio);
		}
		//Check if the file exists on the host
		if (fileToAdd.exists()){
			if (fileToAdd.isFile()){
				//It's a file
				//Checking its size against the available space on the vfs disk
				if (getFreeSpace()<=fileToAdd.length()){
					System.out.println("Not enough available space !");
					return false;
				}
				try {
					//We import it
					vfsCore.File hFile = cie.importFile(fileToAdd, VFSPath);
					//For now, we add it to the root
					fullHierarchy.addChild(hFile);
					//And we save the modified Hierarchy
					return saveFullHierarchyToFile();
				} catch (FileNotFoundException e){
					System.out.println("File not found !");
					return false;
				} catch (IOException e){
					System.out.println("Error in the CoreIO");
					return false;
				}
			} else {
				//It's a folder
				//Checking its size against the available space on the vfs disk
				if (getFreeSpace()<=cio.sizeOfFolder(fileToAdd)){
					System.out.println("Not enough available space !");
					return false;
				}
				try {
					//We import it
					vfsCore.Folder hFolder = cie.importFolder(fileToAdd, VFSPath);
					//For now, we add them to the root
					fullHierarchy.addChild(hFolder);
					//And we save the modified Hierarchy
					return saveFullHierarchyToFile();
				} catch (FileNotFoundException e){
					System.out.println("Folder not found !");
					return false;
				} catch (IOException e){
					System.out.println("Error in the CoreIO");
					return false;
				}
			}
		} else {
			System.out.println("The element doesn't exists");
			return false;
		}
	}
	
	/**
	 * exports an element from the VFS .dsk to the project directory (for now) on the host file system 
	 * @param VFSPath the path of the element in the VFS hierarchy
	 * @param homePath the name of the element once exported to the host disk
	 * @return true if the operation is successful
	 */
	public boolean exportElement(String VFSPath, String homePath){
		try {
			//Lazily initializing the Import-Export Core
			if (cie == null){
				cie = new CoreImportExport(cio);
			}
			//We find the pointer to the Hierarchy element at the given VFSPath
			Hierarchy origin = ((Folder)fullHierarchy).findChild(VFSPath);
			if (origin instanceof vfsCore.File){
				cie.exportFile((vfsCore.File)origin, homePath);
			} else {
				cie.exportFolder((Folder)origin, homePath);
			}
		} catch (fileNotFound e) {
			System.out.println("The file doesn't exist");
		}
		return false;	
	}

	
	
	
	//-----------------//
	//DELETING ELEMENTS//
	//-----------------//
	
	/**
	 * method to delete a folder and all its content at a specified path
	 * @param path
	 * @throws fileNotFound
	 * @throws BadPathInstanceException
	 */
	public boolean deleteFolderAtPath(String path){
		try {
			Hierarchy child = fullHierarchy.findChild(path);
			deleteFolderOfHierarchy(child);
			return saveFullHierarchyToFile();
		} catch (BadPathInstanceException e) {
			System.out.println("Attention vous devez selectionner un DOSSIER a supprimer");
			return false;
		} catch (FileNotFoundException | fileNotFound e) {
			System.out.println("The file doesn't exist");
			return false;
		} catch (IOException e) {
			System.out.println("CoreIO exception");
			return false;
		}
		
	}
	
	/**
	 * the "tool" method to delete a hierarchy given in parameter 
	 * @param child
	 * @throws BadPathInstanceException
	 * @throws FileNotFound Exception
	 * @throws IOException 
	 */
	public void deleteFolderOfHierarchy(Hierarchy folder) throws BadPathInstanceException, FileNotFoundException, IOException{
		if (folder instanceof Folder){
			//deleting the sub-folders and subfiles
			for(Hierarchy subpath : folder.getChildrens())
			{		
				if(subpath instanceof Folder){
					deleteFolderOfHierarchy(subpath);
				} else if (subpath instanceof vfsCore.File){
					//If it is a file, we delete it through CoreIO
					cio.removeFileAtAddress(((vfsCore.File) subpath).getAddress());
				}
				//In all cases, we remove the element from the children list
				//Potential error, see with the tests
				//maybe only in the case of a file
				folder.removeChild(subpath);
			}
			//deleting the folder itself
			folder.getParent().removeChild(folder);
		}else{
			throw new BadPathInstanceException("Attention vous devez selectionner un DOSSIER a supprimer");
		}
	}
	
	/**
	 * delete a file at a path on the VFS disk
	 * @param path the path to the file in the vfs core (absolute path)
	 * @throws BadPathInstanceException 
	 * @throws fileNotFound 
	 */
	public boolean deleteFileAtPath(String path){
		try {
			Hierarchy child = fullHierarchy.findChild(path);
			if(child instanceof vfsCore.File){
				cio.removeFileAtAddress(((vfsCore.File) child).getAddress());			
				child.getParent().removeChild(child);
				return saveFullHierarchyToFile();
			} else {
				throw new BadPathInstanceException("vous essayer de supprimer un dossier alors que vous devirez supprimer un fichier");
			}
		} catch (BadPathInstanceException e) {
			System.out.println("Vous essayer de supprimer un dossier alors que vous devirez supprimer un fichier");
			return false;
		} catch (fileNotFound | FileNotFoundException e) {
			System.out.println("The file doesn't exist");
			return false;
		} catch (IOException e) {
			System.out.println("CoreIO exception");
			return false;
		}
		
	}
	
	
	
	//---------------------------//
	//COPYING AND MOVING ELEMENTS//
	//---------------------------//
	
	/**
	 * copy an element (given by an absolute path) to another folder on the vfs (absolute path too)
	 * @param departure absolute path to the element to copy
	 * @param destination absolute path to the destination folder of the copy
	 * @return true if the operation is successful
	 */
	public boolean copyElementAtPath(String departure, String destination){
		try {
			Hierarchy toBeCopied = fullHierarchy.findChild(departure);
			Hierarchy finalStop = fullHierarchy.findChild(destination);
			finalStop.alreadyExist(toBeCopied.getName());
			if(finalStop instanceof Folder){
				return copyElement(toBeCopied, (Folder)finalStop);
			} else {
				throw new BadPathInstanceException("attention vous essayer de copier un element dans un fichier !!");
			}
		} catch (fileNotFound e) {
			System.out.println("The file doesn't exist.");
			return false;
		} catch (BadPathInstanceException e) {
			System.out.println("The destination element is a file");
			return false;
		}	catch (AlreadyExistException e){
			System.out.println("Please, change the name of the file, this name is already used here");
			return false;
		}
	}
	
	/**
	 * copy a Hierarchy element to another Hierarchy element
	 * @param original the Hierarchy to copy
	 * @param destinationFolder the Hierarchy where we should copy
	 * @return true if the operation is successful
	 */
	public boolean copyElement(Hierarchy original, Folder destinationFolder){
		//Thank to the check in copyElementAtPath, we are sure destinationFolder is a Folder
			if (original instanceof vfsCore.File){
				//we want to copy a single file
				//We check it's size
				if (((vfsCore.File) original).getSize() >= getFreeSpace()){
					System.out.println("Pas assez de place sur le disque");
					return false;
				}
				long newAdress = -1;
				try {
					//We pass the copy order to the CoreIO
					destinationFolder.alreadyExist(original.getName());
					newAdress = cio.copyFileAtAddress(((vfsCore.File) original).getAddress());
				} catch (FileNotFoundException e){
					System.out.println("The file doesn't exist");
					return false;
				} catch (IOException e) {
					System.out.println("Error in the CoreIO");
					return false;
				} catch (AlreadyExistException e){
					System.out.println("Please, change the name of the file, this name is already used here");
					return false;
				}
				//We add the element to the Hierarchy
				destinationFolder.addChild(new vfsCore.File(original.getName(), newAdress, ((vfsCore.File) original).getSize(), destinationFolder));
				return saveFullHierarchyToFile();
			} else {
				//We want to copy a folder
				original = ((Folder)original);
				//First we check the available size
				SizeVisitor sz = new SizeVisitor();
				sz.visit(original);
				if(sz.getSizeUsed() >= getFreeSpace()){
					System.out.println("Pas assez de place sur le disque");
					return false;
				}
				//Then we create the new folder, empty
				Folder copyFolder = new Folder(new ArrayList<Hierarchy>(),original.getName(),destinationFolder);
				//And we add its children
				for(Hierarchy child:original.getChildrens()){
					copyElement(child, copyFolder);
				}
				//Finally we add the newly constructed hierarchy to the destination folder
				destinationFolder.addChild(copyFolder);
				return saveFullHierarchyToFile();
			}
			
	}
	
	/**
	 * move an element originally at departure to the folder designated by destination
	 * @param departure the element to move
	 * @param destination the folder were the element should be moved
	 * @throws fileNotFound
	 * @throws BadPathInstanceException
	 */
	public boolean moveElement(String departure, String destination){
		Hierarchy toBeMoved;
		try {
			toBeMoved = fullHierarchy.findChild(departure);
			Hierarchy finalStop = fullHierarchy.findChild(destination);
			finalStop.alreadyExist(toBeMoved.getName());
			if(finalStop instanceof Folder){
				toBeMoved.getParent().removeChild(toBeMoved);
				finalStop.addChild(toBeMoved);
				return saveFullHierarchyToFile();
			}else{
				throw new BadPathInstanceException("attention vous essayer de copier un element dans un fichier !!");
			}
		} catch (fileNotFound e) {
			System.out.println("Le fichier n'existe pas");
			return false;
		} catch (BadPathInstanceException e) {
			System.out.println("attention vous essayer de copier un element dans un fichier !!");
			return false;
		}catch (AlreadyExistException e){
			System.out.println("Please, change the name of the file, this name is already used here");
			return false;
		}
		
	}
	
	
	//-----------------//
	//GETTING THE SIZES//
	//-----------------//
	
	/**
	 * return the size, in bytes, used on the vfs data partition
	 * @return the size, in bytes, used on the vfs data partition
	 */
	public long getUsedSpace() {
		SizeVisitor sz = new SizeVisitor();
		sz.visit((Folder)fullHierarchy);
		return sz.getSizeUsed();	
	}
	
	/**
	 * return the size, in bytes, of the vfs data partition
	 * @return return the size, in bytes, of the vfs data partition
	 */
	public long getTotalSpace(){
		try {
			return cio.sizeOfDisk();
		} catch (IOException e) {
			System.out.println("Error reading the size of the disk");
			return -1;
		}
	}
	
	/**
	 * return the size, in bytes, of the unused space on the vfs data partition
	 * @return return the size, in bytes, of the unused space on the vfs data partition
	 */
	public long getFreeSpace(){
		return getTotalSpace()-getUsedSpace();
	}
	
	
	
	//---------------//
	//SEARCHING FILES//
	//---------------//

	/**
	 * returns an array containing all the Files objects whose name is equal to the string given as an argument
	 * @param search the name of the file, including its extension
	 * @return an array containing all the Files objects whose name is equal to the string given as an argument
	 */
	public ArrayList<Hierarchy> searchFile(String search) {
		SearchVisitor sv = new SearchVisitor(search);
		sv.visit((Folder)fullHierarchy);
		return sv.getElements();
	}
	
	public void printSearch(String search){
		ArrayList<Hierarchy> results = searchFile(search);
		
		if (results.isEmpty()){
			System.out.println("No file found.");
		} else {
			System.out.println(results.size() + " file(s) found :");
			for (Hierarchy file:results){
				System.out.println("- "+file.getName()+" of size "+((vfsCore.File)file).getSize()+"B in folder "+file.getParent().getName());
			}
		}
		
	}
	
}
