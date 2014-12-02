package vfsCore;

import java.io.Serializable;
import java.util.ArrayList;

public class Hierarchy implements Serializable {
	
	/**
	 * generated serialVersionUID used to make proper serialization
	 */
	private static final long serialVersionUID = -8211210286637009700L;
	private ArrayList<Hierarchy> childrens;
	private String name;
	

	
	
	/**
	 * constructor hierarchy using fields 
	 * @param childrens
	 * @param name
	 */
	public Hierarchy(ArrayList<Hierarchy> childrens, String name) {
		super();
		this.childrens = childrens;
		this.name = name;
	}
	
	
	
	/**
	 * @param name
	 */
	public Hierarchy(String name) {
		super();
		this.name = name;
	}



	/**
	 * Add a child to the hierarchy might be a folder of a file
	 * @param newChild
	 */
	public void addChild(Hierarchy newChild){
		this.childrens.add(newChild);
		//updateSize();
	}


	/**
	 * remove a child from the hierarchy
	 * We have to override this method for folder concrete class to be able to return
	 * an array list of the subfiles (including those in subfolders) to delete them from VFS
	 * Overriding it for file concrete class to return the address of the deleted file from VFS, for the core to really delete it
	 * @param child
	 */
	public void removeChild(Hierarchy child){
		this.childrens.remove(child);
		//updateSize();
	}
	
	/**
	 * @return the childrens
	 */
	public ArrayList<Hierarchy> getChildrens() {
		return childrens;
	}

	/**
	 * @param childrens the childrens to set
	 */
	public void setChildrens(ArrayList<Hierarchy> childrens) {
		this.childrens = childrens;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	/*public double getSize(){
		//Potential loop, check on tests
		updateSize();
		return this.size;
	}*/
	
	/*public void updateSize() {
		double tempSize = 0;
		for(Hierarchy child:childrens){
			tempSize = tempSize + child.getSize();
		}
		this.size = this.node.getSize() + tempSize;
	}*/
	
	
	
	
	
}
