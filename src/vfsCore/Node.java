package vfsCore;

public class Node {
	private String name;
	private boolean isDirectory;
	private double size = 0;
	
	public void setSize(double newSize){
		this.size = newSize;
	}
	public double getSize(){
		return this.size;
	}
}
