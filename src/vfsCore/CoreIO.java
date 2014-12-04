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
		
		//We want to get the serialized version of a hierarchy
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ObjectOutputStream out = new ObjectOutputStream(baos);
		//We write the object to an object output stream, which then transfers it to the byte stream.
		out.writeObject(h1);
		//We convert the bytes stored in the stream to an array
		hierarchyBytes = baos.toByteArray();
		//Closing the streams
		baos.close();
		out.close();
		
		return hierarchyBytes;
	}
	
	/**
	 * converts a correctly-formatted byte array to a Hierarchy object, de-serializing it.
	 * @param hierarchyBytes the bytes composing the hierarchy serialized representation
	 * @return the de-serialized Hierarchy object
	 * @throws CoreIOException 
	 * @throws IOException 
	 * @throws ClassNotFoundException 
	 */
	public Hierarchy getHierarchyFromBytes(byte[] hierarchyBytes) throws CoreIOException, IOException, ClassNotFoundException{
		Hierarchy h1 = null;
		
		//Inputs stream to read the byte array and convert it to a serialized representation
		ByteArrayInputStream bis = new ByteArrayInputStream(hierarchyBytes) ;
		ObjectInputStream oin = new ObjectInputStream(bis) ;
		//and then deserializing it
		h1 = (Hierarchy) oin.readObject();
		//Closing streams
		oin.close();				
		bis.close();
		return h1;
		
		
	}
	
	/**
	 * Allows us to save a hierarchy object in a .dsk file, without damaging the data already stored in it.
	 * @param h1 the hierarchy we want to save in the .dsk file
	 * @return true if success, false in any other case.
	 * @throws CoreIOException 
	 * @throws fileNotFound 
	 */
	public boolean saveHierarchyToFile(Hierarchy h1) throws CoreIOException, fileNotFound{
		//RandomAccessFile allows us to arbitrarily move a pointer in the content of the file
		try (RandomAccessFile rAF = new RandomAccessFile(new File(this.getDiskName()), "rw")) {
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
			rAF.close();
			return true;
		} catch (FileNotFoundException e) {
			throw new fileNotFound("");
		} catch (Exception e) {
			throw new CoreIOException("Export error");
		}
	}
	
	/**
	 * loads a Hierarchy from a .dsk file (VFS file on the host system)
	 * @return the hierarchy stored in the VFS, as a Hierarchy object
	 * @throws fileNotFound, CoreIOException 
	 */
	public Hierarchy loadHierarchyTreeFromFile() throws fileNotFound, CoreIOException{
		try (RandomAccessFile rAF = new RandomAccessFile(new File(this.getDiskName()), "r")) {
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
			Hierarchy h1 = getHierarchyFromBytes(hierarchyBytes);
			rAF.close();
			return h1;
		} catch (FileNotFoundException e) {
			throw new fileNotFound("");
		} catch (Exception e) {
			throw new CoreIOException("Import error");
		}
		
	}
	
	/**
	 * read the content of a file stored on the VFS disk, using the adress of the first 1kB block and following a linked list
	 * the content is written through a buffer to the work folder on the host file system
	 * @param adress the address of the first block of the file
	 * @param destination the path where to export the file
	 * @param size size of the file
	 * @return a boolean denoting the success of the operation
	 * @throws fileNotFound
	 * @throws CoreIOException
	 */
	public boolean readFromAdress(long adress, String destination, long size) throws fileNotFound, CoreIOException{
		try {
			RandomAccessFile rAF = new RandomAccessFile(new File(this.getDiskName()), "r");
			File exportFile = new File(destination);
			FileOutputStream fOS = new FileOutputStream(exportFile); 
			long position = adress;
			int compteur = 0;
			int trueSize = 1024;
			while (position != -1){
				//Position on the next block
				rAF.seek(position*(1024+8+1));
				//Small adjustment for the last kB, to avoid writing unnecessary zeroes
				if (((compteur+1)*1024 > size)&&(size - compteur*1024 > 0)&&(size>1024)){
					trueSize = (int) (1024-((compteur+1)*1024-size));
				}
				//Buffer
				byte[] store = new byte[trueSize];
				//Read from the VFS, write to the host
				rAF.read(store);
				fOS.write(store);
				//get the next position
				position = rAF.readLong();
				compteur++;	
			}
			rAF.close();
			fOS.close();
			return true;
		} catch (FileNotFoundException e) {
			throw new fileNotFound("Import error");
			
		} catch (IOException e) {
			throw new CoreIOException("Import error");
			
		}
	}
	
	/**
	 * write the content of a java.io.File to the VFS disk, by blocks of 1kB
	 * @param file a File object containing the information about the file to import
	 * @return the adress of the first block of the file on the VFS disk
	 * @throws IOException
	 * @throws CoreIOException
	 */
	public long writeToDisk(File file) throws IOException, CoreIOException{
		long firstAdress = -1;
		RandomAccessFile rAF = new RandomAccessFile(new File(this.getDiskName()), "rw");
		//We'll need the size of the partition for safety reason
		rAF.seek(rAF.length()-8);
		long diskSize = rAF.readLong();
		rAF.seek(0);
		//New input stream
		FileInputStream fIS = new FileInputStream(file);
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
				//Getting the adress of the next empty block
				long nextAdress=getNextEmptyBlockStartingFrom(currentAdress+1, rAF, diskSize);
				//(just a small position reset because of the method call on previous line (shared RandomAccessFile)
				rAF.seek(currentAdress * (1024 + 8 +1)+1024);
				//Storing the adress and a "dirty bit"
				rAF.writeLong(nextAdress);
				rAF.write(1);
				currentAdress = nextAdress;
				i++;
			}
		}
		//Last kilobyte of the file, with special treatment
		System.out.println("Last kB");
		rAF.seek(currentAdress * (1024 + 8+1));
		readTemp = new byte[1024];
		fIS.read(readTemp);
		rAF.write(readTemp);
		//Convention : EndOfFile -> address = -1
		rAF.writeLong(-1);
		rAF.write(1);
		//The file is now completely written
		rAF.close();
		fIS.close();
		//We return the address of the first block of the file
		return firstAdress;
	}
	
	/**
	 * get the next empty block of 1kB on the VFS disk file, starting from a specified adress
	 * @param adress 
	 * @param rAF a RandomAccessFile used to arbitrarily browse the file
	 * @param size the size of the VFS partition, for bounds checking
	 * @return the adress of the next empty 1kB block
	 * @throws IOException
	 * @throws CoreIOException
	 */
	public long getNextEmptyBlockStartingFrom(long adress, RandomAccessFile rAF, long size) throws IOException, CoreIOException {
		long i = adress;
		int dirty = 1;
		while ((dirty != 0)){
			if (i*1024>=size){ throw new CoreIOException("plus de blocs libres");}
			rAF.seek(i*(1024 + 8+1)+1024+8);
			dirty = rAF.read();
			i = i + 1;
		}
		return i-1;
	}
}
