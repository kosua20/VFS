package vfsCore.exceptions;

/**
 * this exception is used when trying to create/move/copy files or folders inside an hierarchy element
 * that is not a folder
 * @author michaellavner
 *
 */

public class BadPathInstanceException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6009242829135625714L;

	public BadPathInstanceException(String e) {
		super(e);
	}

}
