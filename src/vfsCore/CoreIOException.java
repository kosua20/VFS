package vfsCore;

public class CoreIOException extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = -1176938465792141198L;
	private String description ="CoreIO Exception thrown";
	public CoreIOException(String string) {
		this.setDescription(string);
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}

}
