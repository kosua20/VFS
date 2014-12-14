package vfsCore.exceptions;

/**
 * this exception is thrown when we are moving/creating/renaming/copying files and folders
 * with a name similar to one of the destination folder's element
 * @author michaellavner
 *
 */

public class AlreadyExistException extends Exception {

	/**
	 * serial UID for serialization
	 */
	private static final long serialVersionUID = 2638619326069676550L;
	
	public AlreadyExistException(String e){
		super(e);
	}
	

}
