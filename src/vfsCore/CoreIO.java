package vfsCore;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.RandomAccessFile;
/**
 * 
 * @author simon
 *
 */
public class CoreIO {
	/**
	 * convert the Hierarchy to a byte array
	 * @param h1 : the hierarchy
	 * @return an array of bytes
	 */
	public byte[] getHierarchyBytes(Hierarchy h1){
		byte[] hierarchyBytes = null;
		try{
			//We want to get the serialized version of a hierarchy
			ByteArrayOutputStream bos = new ByteArrayOutputStream() ;
			 
			 ObjectOutputStream out = new ObjectOutputStream(bos) ;
			
			//We write the object to an output stream, and store it in a byte array
			out.writeObject(h1);
			
			

			// Get the bytes of the serialized object
			hierarchyBytes = bos.toByteArray();
			bos.close();
			out.close();
		} catch (IOException e) {
			System.out.println("Error writing file");	
		} 
		
		return hierarchyBytes;
	}
	
	public boolean saveHierarchyToFile(Hierarchy h1, String filePath){
		try (RandomAccessFile rAF = new RandomAccessFile(new File(filePath), "rw")) {
			rAF.seek(rAF.length()-8);
			long startingPosition = rAF.readLong();
			rAF.seek(startingPosition);
			rAF.write(getHierarchyBytes(h1));
			rAF.writeLong(startingPosition);
			rAF.setLength(rAF.getFilePointer());
			return true;
		} catch (FileNotFoundException e) {
			System.out.println("File not found.");
			return false;
		} catch (Exception e) {
			System.out.println("Error when saving the hierarchy...");
			return false;
		}
	}
	
	public Hierarchy loadHierarchyTreeFromFile(String filePath){
		try (RandomAccessFile rAF = new RandomAccessFile(new File(filePath), "r")) {
			rAF.seek(rAF.length()-8);
			long startingPosition = rAF.readLong();
			rAF.seek(startingPosition);
			long sizeOfArray = rAF.length() - 8 - startingPosition;
			byte[] hierarchyBytes = new byte[(int) sizeOfArray];
			rAF.read(hierarchyBytes);
			
			try{
				ByteArrayInputStream bis = new ByteArrayInputStream(hierarchyBytes) ;
				ObjectInputStream oin = new ObjectInputStream(bis) ;
				Hierarchy h1 = (Hierarchy) oin.readObject();
				
				oin.close();				
				bis.close();
				
				return h1;
			} catch (IOException e) {
				System.out.println("Error loading file");	
			} 
			
		} catch (FileNotFoundException e) {
			System.out.println("File not found.");
			
		} catch (Exception e) {
			System.out.println("Error when loading the hierarchy...");
			
		}
		return null;
	}
}
