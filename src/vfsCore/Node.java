package vfsCore;

public class Node {
	private String name;
	private boolean isDirectory;
	private double size = 0;
	//Maybe need the adress of the beginning of the file
	private double adress = -1;
	
	public void setSize(double newSize){
		this.size = newSize;
	}
	public double getSize(){
		return this.size;
	}
	protected String getName() {
		return name;
	}
	protected void setName(String name) {
		this.name = name;
	}
	protected boolean isDirectory() {
		return isDirectory;
	}
	protected void setDirectory(boolean isDirectory) {
		this.isDirectory = isDirectory;
	}
	protected double getAdress() {
		return adress;
	}
	protected void setAdress(double adress) {
		this.adress = adress;
	}
	
	
}
