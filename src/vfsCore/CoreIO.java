package vfsCore;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
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
	 * @param filePath the position of the .dsk file in the host system
	 * @return true if success, false in any other case.
	 * @throws CoreIOException 
	 * @throws fileNotFound 
	 */
	public boolean saveHierarchyToFile(Hierarchy h1, String filePath) throws CoreIOException, fileNotFound{
		//RandomAccessFile allows us to arbitrarily move a pointer in the content of the file
		try (RandomAccessFile rAF = new RandomAccessFile(new File(filePath), "rw")) {
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
			return true;
		} catch (FileNotFoundException e) {
			throw new fileNotFound("");
		} catch (Exception e) {
			throw new CoreIOException("Export error");
		}
	}
	
	/**
	 * loads a Hierarchy from a .dsk file (VFS file on the host system)
	 * @param filePath the path of the .dsk file
	 * @return the hierarchy stored in the VFS, as a Hierarchy object
	 * @throws fileNotFound, CoreIOException 
	 */
	public Hierarchy loadHierarchyTreeFromFile(String filePath) throws fileNotFound, CoreIOException{
		try (RandomAccessFile rAF = new RandomAccessFile(new File(filePath), "r")) {
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
			return h1;
		} catch (FileNotFoundException e) {
			throw new fileNotFound("");
		} catch (Exception e) {
			throw new CoreIOException("Import error");
		}
		
	}
}
