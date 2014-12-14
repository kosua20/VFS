package vfsCore;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.RandomAccessFile;
/**
 * The CoreIO manages all the input/output stuff. It is an interface between the Core manipulating the hierarchy, and the .dsk file where the VFS disk is stored
 * The .dsk has a specific structure, described in our report. It is composed of (and in this order) : 
 * - the data of the files stored on the VFS, 
 * - a serialized version of the hierarchy, 
 * - the desired size of the disk (ie the size of the part of the file dedicated to storing the data).
 * 
 * @author Simon Rodriguez
 *
 */
public class CoreIO {
	//ATTRIBUTES, CONSTRUCTOR, GETTERS/SETTERS
	private String diskName;
	public CoreIO(String diskName) {
		super();
		this.diskName = diskName;
	}
	protected String getDiskName() {
		return diskName;
	}
	protected void setDiskName(String diskName) {
		this.diskName = diskName;
	}
	
	
	
	//--------------------------------//
	//SAVING AND LOADING THE HIERARCHY//
	//--------------------------------//
	
	/**
	 * converts the Hierarchy to a byte array
	 * Here we want to add the serialized version of the hierarchy to an already existing file (the .dsk), at an arbitrary position. 
	 * Thus, we need to convert the serialized hierarchy to an array of bytes to be able to write it byte-by-byte.
	 * @param h1 : the hierarchy we want to convert
	 * @return an array of bytes
	 * @throws IOException 
	 */
	public byte[] getHierarchyBytes(Hierarchy h1) throws IOException{
		byte[] hierarchyBytes = null;
		ByteArrayOutputStream baos = null;
		ObjectOutputStream out = null;
		try {
			//We want to get the serialized version of a hierarchy
			baos = new ByteArrayOutputStream();
			out = new ObjectOutputStream(baos);
			//We write the object to an object output stream, which then transfers it to the byte stream.
			out.writeObject(h1);
			//We convert the bytes stored in the stream to an array
			hierarchyBytes = baos.toByteArray();
		} finally {
			//Closing the streams
			if (baos !=null){baos.close();}
			if (out !=null){out.close();}
		}
		return hierarchyBytes;
	}
	
	/**
	 * converts a correctly-formatted byte array to a Hierarchy object, de-serializing it.
	 * @param hierarchyBytes the bytes composing the hierarchy serialized representation
	 * @return the de-serialized Hierarchy object
	 * @throws IOException 
	 * @throws ClassNotFoundException 
	 */
	public Hierarchy getHierarchyFromBytes(byte[] hierarchyBytes) throws IOException, ClassNotFoundException{
		Hierarchy h1 = null;
		ByteArrayInputStream bis = null;
		ObjectInputStream oin = null;
		try {
			//Inputs stream to read the byte array and convert it to a serialized representation
			bis = new ByteArrayInputStream(hierarchyBytes) ;
			oin = new ObjectInputStream(bis) ;
			//and then deserializing it
			h1 = (Hierarchy) oin.readObject();
		} finally {
			//Closing streams
			if (bis !=null){bis.close();}
			if (oin !=null){oin.close();}
		}
		return h1;
		
		
	}
	
	/**
	 * Allows us to save a hierarchy object in a .dsk file, without damaging the data already stored in it.
	 * @param h1 the hierarchy we want to save in the .dsk file 
	 * @throws FileNotFoundException
	 * @throws IOException 
	 */
	public void saveHierarchyToFile(Hierarchy h1) throws FileNotFoundException, IOException{
		//RandomAccessFile allows us to arbitrarily move a pointer in the content of the file
		RandomAccessFile rAF = null;
		try {
			rAF = new RandomAccessFile(new File(this.getDiskName()), "rw");
			//We read the size of the data partition (stored as a long on 8 bytes, at the end of the file)
			rAF.seek(rAF.length()-8);
			long startingPosition = rAF.readLong();
			//We place the file reader at the end of the data zone, and thus at the beginning of the serialized hierarchy part
			rAF.seek(startingPosition);
			//We write the byte array corresponding to serialized hierarchy
			rAF.write(getHierarchyBytes(h1));
			//We store the size of the data partition
			rAF.writeLong(startingPosition);
			//We truncate the file, thus deleting any previously stored serialization
			rAF.setLength(rAF.getFilePointer());
		} finally {
			if (rAF!=null){rAF.close();}
		}
	}
	
	/**
	 * loads a Hierarchy from a .dsk file (VFS file on the host system)
	 * @return the hierarchy stored in the VFS, as a Hierarchy object
	 * @throws FileNotFoundException
	 * @throws IOException 
	 * @throws ClassNotFoundException 
	 */
	public Hierarchy loadHierarchyTreeFromFile() throws FileNotFoundException, IOException, ClassNotFoundException{
		RandomAccessFile rAF = null;
		Hierarchy h1 = null;
		try {
			rAF= new RandomAccessFile(new File(this.getDiskName()), "r"); 
			//Reading the size of the data partition
			rAF.seek(rAF.length()-8);
			long startingPosition = rAF.readLong();
			//Setting the position at the beginning of the serialized hierarchy part of the .dsk file
			rAF.seek(startingPosition);
			//Length of the portion of the .dsk to read
			long sizeOfArray = rAF.length() - 8 - startingPosition;
			//Array to store the byte representation of the serialization
			byte[] hierarchyBytes = new byte[(int) sizeOfArray];
			//Reading the bytes into the array
			rAF.read(hierarchyBytes);
			//Convert the byte array to a Hierarchy object, and returning it
			h1 = getHierarchyFromBytes(hierarchyBytes);
		} finally {
			if (rAF!=null){rAF.close();}
		}
		return h1;	
	}
	
	
	
	
	//-------------------------------------//
	//READING AND WRITING FILES ON THE DISK//
	//-------------------------------------//
	
	/**
	 * read the content of a file stored on the VFS disk, using the adress of the first 1kB block and following a linked list.
	 * the content is written through a buffer to the project folder on the host file system
	 * @param adress the address of the first block of the file
	 * @param destination the path where to export the file
	 * @param size size of the file
	 * @throws IOException 
	 * @throws FileNotFoundException
	 */
	public void readFromAdress(long adress, String destination, long size) throws IOException, FileNotFoundException{
		RandomAccessFile rAF = null;
		FileOutputStream fOS = null;
		try {
			rAF = new RandomAccessFile(new File(this.getDiskName()), "r");
			File exportFile = new File(destination);
			fOS =  new FileOutputStream(exportFile); 
			long position = adress;
			while (position >= 0){
				rAF.seek(position*(1024+8+1));
				//Buffer
				byte[] store = new byte[1024];
				//Read from the VFS, write to the host
				rAF.read(store);
				fOS.write(store);
				//get the next position
				position = rAF.readLong();
			}
			//Truncating the extra zeroes at the end of the file
			fOS.getChannel().truncate(size);
		} finally {
			if (rAF!=null){rAF.close();}
			if (fOS!=null){fOS.close();}
		}
	}
	
	/**
	 * write the content of a java.io.File to the VFS disk, by blocks of 1kB
	 * @param file a File object containing the information about the file to import
	 * @return the address of the first block of the file on the VFS disk
	 * @throws IOException
	 * @throws FileNotFoundException
	 */
	public long writeToDisk(File file) throws IOException, FileNotFoundException {
		long firstAdress = -1;
		RandomAccessFile rAF = null;
		FileInputStream fIS = null;
		try {
			rAF = new RandomAccessFile(new File(this.getDiskName()), "rw");
			//We'll need the size of the partition for safety reason
			rAF.seek(rAF.length()-8);
			long diskSize = rAF.readLong();
			rAF.seek(0);
			//New input stream
			fIS = new FileInputStream(file);
			byte[] readTemp = new byte[1024];
			//getting the next available block of 1kB
			long currentAdress=getNextEmptyBlockStartingFrom(0, rAF,diskSize);
			firstAdress = currentAdress;
			int i = 0;
			//If the file is bigger than 1kB
			if (file.length()>1024){
				//We loop to write 1kB block at a time
				while (i*1024 < file.length()){
					rAF.seek(currentAdress * (1024 + 8 +1));
					//New buffer
					readTemp = new byte[1024];
					//Reading and writing
					fIS.read(readTemp);
					rAF.write(readTemp);
					//Getting the address of the next empty block
					long nextAdress=getNextEmptyBlockStartingFrom(currentAdress+1, rAF, diskSize);
					//(just a small position reset because of the method call on previous line (shared RandomAccessFile)
					rAF.seek(currentAdress * (1024 + 8 +1)+1024);
					//Storing the address and a "dirty bit"
					rAF.writeLong(nextAdress);
					rAF.write(1);
					currentAdress = nextAdress;
					i++;
				}
			}
			//Last kilobyte of the file, with special treatment
			rAF.seek(currentAdress * (1024 + 8+1));
			readTemp = new byte[1024];
			fIS.read(readTemp);
			rAF.write(readTemp);
			//Convention : EndOfFile -> address = -1
			rAF.writeLong(-1);
			rAF.write(1);
			//The file is now completely written
		} finally {
			if (rAF !=null){rAF.close();}
			if (fIS !=null){fIS.close();}
		}
		//We return the address of the first block of the file
		return firstAdress;
	}
	
	
	
	//--------------------------//
	//DELETING AND COPYING FILES//
	//--------------------------//
	
	/**
	 * removes the file at the specified address, overwriting its blocks with zeroes and marking them as free
	 * @param address the adress of the first block of the file to delete
	 * @throws IOException
	 * @throws FileNotFoundException
	 */
	public void removeFileAtAddress(long address) throws IOException, FileNotFoundException {
		RandomAccessFile rAF = null;
		try {
			rAF = new RandomAccessFile(new File(this.getDiskName()), "rw");
			long position = address;
			while (position >= 0){
				rAF.seek(position*(1024+8+1));
				//We erase all bytes because when writing a new file over this block, we won't be sure the block will be entirely re-filled
				byte[] eraser = new byte[1024];
				rAF.write(eraser);
				//Getting the next position
				position = rAF.readLong();
				//erasing the position (safety measure)
				rAF.seek(rAF.getFilePointer()-8);
				rAF.writeLong(-1);
				//marking the block as empty
				rAF.write(0);
			}
		}  finally {
			if (rAF!=null){rAF.close();}
		}
	}
	
	/**
	 * duplicates a file on the disk, and returns the address of the first block of the duplicated file
	 * @param address the address of the file to copy
	 * @return the adress of the copy of the file
	 * @throws IOException
	 * @throws FileNotFoundException
	 */
	public long copyFileAtAddress(long address) throws IOException, FileNotFoundException {
		long firstAddress = -1;
		RandomAccessFile rAF = null;
		try {
			rAF = new RandomAccessFile(new File(this.getDiskName()), "rw");
			//We'll need the size of the partition for safety reason
			rAF.seek(rAF.length()-8);
			long diskSize = rAF.readLong();
			rAF.seek(0);
			//Initialization
			long positionOriginal = address;
			long positionCopy = getNextEmptyBlockStartingFrom(0, rAF, diskSize);
			firstAddress = positionCopy;
			while (positionOriginal >= 0){
				//New buffer
				byte[] readTemp = new byte[1024];
				//Reading the original version of the file
				rAF.seek(positionOriginal * (1024+8+1));
				rAF.read(readTemp);
				//Next block of the original version
				positionOriginal = rAF.readLong();
				//Writing to the copy version
				rAF.seek(positionCopy * (1024+8+1));
				rAF.write(readTemp);
				//Next block of the copy version, if we need it (ie positionOriginal != -1)
				if (positionOriginal >=0){
					positionCopy = getNextEmptyBlockStartingFrom(positionCopy, rAF, diskSize);
				} else {
					positionCopy = -1;
				}
				rAF.writeLong(positionCopy);
				rAF.write(1);
			}
			//The file is now completely copied
		}finally { 
			if (rAF !=null){rAF.close();}
		}
		//We return the address of the first block of the copied file
		return firstAddress;
	}
	
	
	
	
	//---------//
	//UTILITIES//
	//---------//
	
	/**
	* Returns the size of a folder, recursively browsing its sub-folders and files, on the host file system
	* @param folder the folder we want to know the size of, as a java.io.File element
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
	
	/**
	 * gets the next empty block of 1kB on the VFS disk file, starting from a specified adress
	 * @param adress 
	 * @param rAF a RandomAccessFile used to arbitrarily browse the file
	 * @param size the size of the VFS partition, for bounds checking
	 * @return the address of the next empty 1kB block
	 * @throws IOException
	 */
	public long getNextEmptyBlockStartingFrom(long adress, RandomAccessFile rAF, long size) throws IOException {
		long i = adress;
		long savedPosition = rAF.getFilePointer();
		int dirty = 1;
		while ((dirty != 0)){
			if (i*1024>=size){ throw new IOException("plus de blocs libres");}
			rAF.seek(i*(1024 + 8+1)+1024+8);
			dirty = rAF.read();
			i = i + 1;
		}
		//Leaving the random access file positioned as we found it
		rAF.seek(savedPosition);
		//no need to close as this method is called on a already existing RandomAccessFile, in a try...finally loop
		return i-1;
	}

	/**
	 * returns the size of the vfs partition, in bytes
	 * @return returns the size of the vfs partition, in bytes
	 * @throws IOException
	 * @throws FileNotFoundException
	 */
	public long sizeOfDisk() throws IOException, FileNotFoundException {
		RandomAccessFile rAF = null;
		long diskSize = 0;
		try {
			rAF = new RandomAccessFile(new File(this.getDiskName()), "r");
			//We'll need the size of the partition for safety reason
			rAF.seek(rAF.length()-8);
			//We want to return the size in true bytes (ie 1kB = 1024B)
			diskSize =(rAF.readLong()/1033)*1024;
		} finally {
			if (rAF != null){rAF.close();}
		}
		return diskSize;
	}
	
}
