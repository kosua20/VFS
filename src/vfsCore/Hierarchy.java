package vfsCore;

import java.io.Serializable;

import vfsCore.visitors.Visitable;
import vfsCore.visitors.Visitor;

public class Hierarchy implements Serializable, Visitable {
	
	//-----------------------------------------//
	//ATTRIBUTES, CONSTRUCTORS, GETTERS/SETTERS//
	//-----------------------------------------//
	/**
	 * generated serialVersionUID used to make proper serialization
	 */
	private static final long serialVersionUID = -8211210286637009700L;
	
	/**
	 * name of hierarchy element
	 */
	protected String name;
	/**
	 * the parent
	 */
	private Folder parent; 
	

	/**
	 * @param name
	 */
	public Hierarchy(String name) {
		super();
		this.name = name;
	}
	/**
	 * 
	 * @param name
	 * @param parent
	 */
	public Hierarchy(String name, Folder parent) {
		super();
		this.name = name;
		this.parent=parent;
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
	/**
	 * @return the parent
	 */
	public Folder getParent() {
		return parent;
	}
	/**
	 * @param parent the parent to set
	 */
	public void setParent(Folder parent) {
		this.parent = parent;
	}
	
	/**
	 * method used in the visitor pattern
	 * @param Visitor : the concrete visitor visiting a folder or file
	 */
	@Override
	public void accept(Visitor visitor) {
		if (this instanceof Folder){
			visitor.visit((Folder)this);
		} else if (this instanceof vfsCore.File){
			visitor.visit((vfsCore.File)this);
		}
	}	
	
}
