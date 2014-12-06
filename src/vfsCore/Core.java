package vfsCore;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.util.ArrayList;


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

	
	
	//-----------------------//
	//MOVING IN THE HIERARCHY//
	//-----------------------//
	
		public boolean goTo(String path){
			try {
				currentHierarchy = currentHierarchy.findChild(path);
				return true;
			} catch (fileNotFound e) {
				System.out.println("Sorry, the folder doesn't exists");
				return false;
			}
		}
		public void goToParent() {
			if (currentHierarchy.equals(fullHierarchy)){
				System.out.println("You already are on the root of the vfs disk");
			} else {
				currentHierarchy = currentHierarchy.getParent();
			}
		}
		
		public boolean list(){
			String s = "Current: "+currentHierarchy.getName()+"\n";
			for(Hierarchy child:currentHierarchy.getChildrens()){
				s = s+"- "+child.getName()+" ";
				if (child instanceof vfsCore.File){
					s = s +" f "+((vfsCore.File)child).getSize()+"o";
				}else {
					s = s + " F";
				}
				s = s+"\n";
			}
			System.out.println(s);
			return true;
			
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
					hFile.setParent(fullHierarchy);
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
				//Checking its size against the available space on the vfs disk
				if (getFreeSpace()<=cio.sizeOfFolder(fileToAdd)){
					System.out.println("Not enough available space !");
					return false;
				}
				try {
					//We import it
					vfsCore.Folder hFolder = cie.importFolder(fileToAdd, VFSPath,fullHierarchy);
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
	public void deleteFolderAtPath(String path) throws fileNotFound, BadPathInstanceException{
		Hierarchy child = fullHierarchy.findChild(path);
		deleteFolderOfHierarchy(child);
	}
	
	/**
	 * the "tool" method to delete a given in parameter hierarchy
	 * @param child
	 * @throws BadPathInstanceException
	 */
	public void deleteFolderOfHierarchy(Hierarchy child) throws BadPathInstanceException{
		if (child instanceof Folder){
			//deleting the sub-folders and subfiles
			for(Hierarchy subpath : child.getChildrens())
			{		
				if(subpath instanceof Folder){
					deleteFolderOfHierarchy(subpath);
				} else if (subpath instanceof vfsCore.File){
					//If it is a file, we delete it through CoreIO
					try {
						cio.removeFileAtAddress(((vfsCore.File) subpath).getAddress());
					} catch (fileNotFound e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (CoreIOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				//In all cases, we remove the element from the children list
				child.removeChild(subpath);
			}
			//deleting the folder itself
			child.getParent().removeChild(child);
		}else{
			throw new BadPathInstanceException("Attention vous devez selectionner un DOSSIER a supprimer");
		}
	}
	/**
	 * 
	 * @param path
	 * @throws BadPathInstanceException 
	 * @throws fileNotFound 
	 */
	public boolean deleteFileAtPath(String path) throws BadPathInstanceException, fileNotFound{
		Hierarchy child = fullHierarchy.findChild(path);
		if(child instanceof vfsCore.File){
			try {
				cio.removeFileAtAddress(((vfsCore.File) child).getAddress());
			} catch (CoreIOException e) {
				System.out.println("CoreIO exception");
				return false;
			}
			child.getParent().removeChild(child);
			return true;
		} else {
			throw new BadPathInstanceException("vous essayer de supprimer un dossier alors que vous devirez supprimer un fichier");
		}
		
	}
	
	
	
	//---------------------------//
	//COPYING AND MOVING ELEMENTS//
	//---------------------------//
	
	public boolean copyElementAtPath(String departure, String destination){
		try {
			Hierarchy toBeCopied = fullHierarchy.findChild(departure);
			Hierarchy finalStop = fullHierarchy.findChild(destination);
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
		}	
	}
	
	public boolean copyElement(Hierarchy original, Folder destinationFolder){
		//Thank to the check in copyElementAtPath, we are sure destinationfolder is a Folder
			if (original instanceof vfsCore.File){
				//we want to copy a single file
				if (((vfsCore.File) original).getSize() >= getFreeSpace()){
					System.out.println("Pas assez de place sur le disque");
					return false;
				}
				long newAdress = -1;
				try {
					newAdress = cio.copyFileAtAddress(((vfsCore.File) original).getAddress());
				} catch (IOException | CoreIOException e) {
					System.out.println("Error in the CoreIO");
					return false;
				} 
				destinationFolder.addChild(new vfsCore.File(original.getName(), newAdress, ((vfsCore.File) original).getSize(), destinationFolder));
				return true;
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
				return true;
			}
			
	}
	/**
	 * move an element originally at departure to the folder designated by destination
	 * @param departure the element to move
	 * @param destination the folder were the element should be moved
	 * @throws fileNotFound
	 * @throws BadPathInstanceException
	 */
	public void moveElement(String departure, String destination) throws fileNotFound, BadPathInstanceException{
		Hierarchy toBeMoved = fullHierarchy.findChild(departure);
		Hierarchy finalStop = fullHierarchy.findChild(destination);
		if(finalStop instanceof Folder){
			finalStop.addChild(toBeMoved);
			toBeMoved.getParent().removeChild(toBeMoved);
			toBeMoved.setParent(finalStop);
		}else{
			throw new BadPathInstanceException("attention vous essayer de copier un element dans un fichier !!");
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

	
	
}
