package vfsCore;

/**
 * this is a class representing one of the two types of hierarchy element.
 * files contains extra fileds such as address and size
 * @author michaellavner
 *
 */

public class File extends Hierarchy{
	/**
	 * generated serialVersionUID used to make proper serialization
	 */
	private static final long serialVersionUID = 3883703787670770193L;
	
	
	private long address=-1;
	private long size = 0;

	public File(String name, long address, long size, Folder parent) {
		super(name, parent);
		this.address=address;
		this.size=size;
	}
	
	public File(String name, Folder parent){
		super(name, parent);
	}
	

	/**
	 * @return the address
	 */
	public long getAddress() {
		return address;
	}

	/**
	 * @param address the address to set
	 */
	public void setAddress(long address) {
		this.address = address;
	}

	/**
	 * @return the size
	 */
	public long getSize() {
		return size;
	}

	/**
	 * @param size the size to set
	 */
	public void setSize(long size) {
		this.size = size;
	}
	
	
	
}
