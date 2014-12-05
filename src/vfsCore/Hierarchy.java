package vfsCore;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.StringTokenizer;

public class Hierarchy implements Serializable, Visitable {
	
	//-----------------------------------------//
	//ATTRIBUTES, CONSTRUCTORS, GETTERS/SETTERS//
	//-----------------------------------------//
	/**
	 * generated serialVersionUID used to make proper serialization
	 */
	private static final long serialVersionUID = -8211210286637009700L;
	/**
	 * arraylist of children of a hierarchy element
	 */
	private ArrayList<Hierarchy> childrens = new ArrayList<Hierarchy>();
	/**
	 * name of hierarchy element
	 */
	private String name;
	/**
	 * the parent
	 */
	private Hierarchy parent; 
	
	/**
	 * constructor hierarchy using fields 
	 * @param childrens
	 * @param name
	 */
	public Hierarchy(ArrayList<Hierarchy> childrens, String name, Hierarchy parent) {
		super();
		this.childrens = childrens;
		this.name = name;
		this.parent=parent;
	}

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
	public Hierarchy(String name, Hierarchy parent) {
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
	public Hierarchy getParent() {
		return parent;
	}
	/**
	 * @param parent the parent to set
	 */
	public void setParent(Hierarchy parent) {
		this.parent = parent;
	}

	
	
	//---------------------//
	//MANAGING THE CHILDREN//
	//---------------------//
	
	/**
	 * Add a child to the hierarchy, might be a folder of a file
	 * @param newChild
	 */
	public void addChild(Hierarchy newChild){
		if (this.childrens == null){
			this.childrens = new ArrayList<Hierarchy>();
		}
		this.childrens.add(newChild);
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
	 * method to go to a path
	 * will be used in every method that need to access a specific directory or file in the hierarchy
	 * @param path : path must be a string like "/folder1/subfold1/file.extension" 
	 * @throws fileNotFound 
	 */
	public Hierarchy findChild(String path) throws fileNotFound{
		StringTokenizer st  = new StringTokenizer(path, "/");
		Hierarchy h1 = this;
		loopOverToken : while(st.hasMoreTokens()){
			if (h1.getChildrens() == null){
				h1.setChildrens(new ArrayList<Hierarchy>());
			}
			String currentItem = st.nextToken();
			for(Hierarchy child : h1.getChildrens())
			{	
				
				if(currentItem.equalsIgnoreCase(child.getName()))
				{
					h1=child;
					continue loopOverToken;
				}
				
			}
			throw new fileNotFound("Chemin inexistant");
		}
		
		return h1;
	}
	
	/**
	 * method to create a folder at a specific path, with a specified name
	 * @param path : the string of the path (without the folder name)
	 * @param nom : the name of the folder
	 * @throws fileNotFound : exception if path is not found
	 * @throws BadPathInstanceException : exception is the path is of wrong instance
	 */
	public void createFolderAtPath(String path, String nom) throws fileNotFound, BadPathInstanceException{
		Hierarchy child = findChild(path);
		if(child instanceof Folder){
			child.addChild(new Folder(null, nom, child));
		} else {
			throw new BadPathInstanceException("Attention vous essayer de creer un dossier dans un fichier !");
		}
	}
	
	
	
	/**
	 * rename a folder at a specified path
	 * @param path
	 * @param name
	 * @throws fileNotFound
	 * @throws BadPathInstanceException
	 */
	public void renameFolderAtPath(String path, String name) throws fileNotFound, BadPathInstanceException{
		Hierarchy child = findChild(path);
		if(child instanceof Folder){
			child.setName(name);
		}else{
			throw new BadPathInstanceException("Attention vous essayez de renommer un fichier alors que vous devriez renommer un dossier");
		}
	}
	
	
	/**
	 * method to create a file at a specific path, with a specified name
	 * @param path : the string of the path
	 * @param nom : the name of the folder
	 * @throws fileNotFound : exception if path is not found
	 * @throws BadPathInstanceException : exception is the path is of wrong instance
	 */
	public void createFileAtPath(String path, String nom) throws fileNotFound, BadPathInstanceException{
		Hierarchy child = findChild(path);
		if(child instanceof Folder){
			child.addChild(new File(nom, child));
		} else {
			throw new BadPathInstanceException("Attention vous creer un ficher");
		}
	}
	
	
	
	/**
	 * rename a file at a specified path
	 * @param path
	 * @param name
	 * @throws fileNotFound
	 * @throws BadPathInstanceException
	 */
	public void renameFileAtPath(String path, String name) throws fileNotFound, BadPathInstanceException{
		Hierarchy child = findChild(path);
		if(child instanceof File){
			child.setName(name);
		}else{
			throw new BadPathInstanceException("Attention vous essayez de renommer un dossier alors que vous devriez renommer un fichier");
		}
	}
	
	



	@Override
	public void accept(Visitor visitor) {
		if (this instanceof Folder){
			visitor.visit((Folder)this);
		} else if (this instanceof vfsCore.File){
			visitor.visit((vfsCore.File)this);
		}
	}



	
	
	
	
	
}
