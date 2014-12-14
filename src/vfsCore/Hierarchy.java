package vfsCore;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.StringTokenizer;

import vfsCore.exceptions.AlreadyExistException;
import vfsCore.exceptions.BadPathInstanceException;
import vfsCore.exceptions.fileNotFound;
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
		if (childrens != null){
			for(Hierarchy child:childrens){
				child.setParent(this);
			}
			this.childrens = childrens;
		} else {
			this.childrens = new ArrayList<Hierarchy>();
		}
		
		this.name = name;
		this.parent=parent;
	}

	/**
	 * @param name
	 */
	public Hierarchy(String name) {
		super();
		this.name = name;
		this.childrens = new ArrayList<Hierarchy>();
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
		this.childrens = new ArrayList<Hierarchy>();
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
		newChild.setParent(this);
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
	 * @return the children
	 */
	public ArrayList<Hierarchy> getChildrens() {
		return childrens;
	}

	/**
	 * @param childrens the children to set
	 */
	public void setChildrens(ArrayList<Hierarchy> childrens) {
		if (childrens != null){
			for(Hierarchy child:childrens){
				child.setParent(this);
			}
			this.childrens = childrens;
		} else {
			this.childrens = new ArrayList<Hierarchy>();
		}
		
	}

	
	/**
	 * Method to follow a path in the current Hierarchy and return the Hierarchy element at the given path
	 * will be used in every method that need to access a specific directory or file in the hierarchy
	 * @param path : path must be a string like "/folder1/subfold1/file.extension" or "C:\\folder1\\subfold1\\file.extension"
	 * @return the Hierarchy element at the given path, if it exists
	 * @throws fileNotFound 
	 */
	public Hierarchy findChild(String path) throws fileNotFound{
		StringTokenizer st  = new StringTokenizer(path, java.io.File.separator);
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
	 * Method to verify that there is not an element with same name (&extension in case of file). 
	 * Will be used in every method for creating, moving and copying files & folders
	 * @throws AlreadyExistException
	 */
	public void alreadyExist(String name) throws AlreadyExistException{
		if (this.childrens == null){return;}
		for(Hierarchy element : this.childrens){
			if(element.name.equalsIgnoreCase(name)){
				throw new AlreadyExistException("Attention un fichier ou dossier du meme nom existe deja !");
			}			
		}
		return;
	}
	
	/**
	 * checks if a Hierarchy element is contained in a sub-folder of the current Hierarchy
	 * @param potentialChild
	 * @return true if potentialChild is in the subfolders of the Hierarchy element
	 */
	public boolean hasAsChild(Hierarchy potentialChild){
		if (this.childrens == null){return false;}
		for(Hierarchy element : this.childrens){
			if (element.equals(potentialChild)){
				return true;
			} else if (element.hasAsChild(potentialChild)){
				return true;
			}
		}
		return false;
	}
	
	/**
	 * method to create a folder at a specific path, with a specified name
	 * @param path : the string of the path (without the folder name)
	 * @param nom : the name of the folder
	 * @throws fileNotFound : exception if path is not found
	 * @throws BadPathInstanceException : exception is the path is of wrong instance
	 * @throws AlreadyExistException 
	 */
	public void createFolderAtPath(String path, String nom) throws fileNotFound, BadPathInstanceException, AlreadyExistException{
		Hierarchy child = findChild(path);
		if(child instanceof Folder){
			child.alreadyExist(nom);
			child.addChild(new Folder(null, nom, child));
		} else {
			throw new BadPathInstanceException("Attention vous essayer de creer un dossier dans un fichier !");
		}
	}
	
	
	
	/**
	 * rename a folder at a specified path
	 * @param path the path o the folder to rename
	 * @param name the new name of the folder
	 * @throws fileNotFound
	 * @throws BadPathInstanceException
	 * @throws AlreadyExistException 
	 */
	public void renameFolderAtPath(String path, String name) throws fileNotFound, BadPathInstanceException, AlreadyExistException{
		Hierarchy child = findChild(path);
		if(child instanceof Folder){
			child.getParent().alreadyExist(name);
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
	 * @throws AlreadyExistException 
	 */
	public void createFileAtPath(String path, String nom) throws fileNotFound, BadPathInstanceException, AlreadyExistException{
		Hierarchy child = findChild(path);
		if(child instanceof Folder){
			child.alreadyExist(nom);
			child.addChild(new File(nom, child));
		} else {
			throw new BadPathInstanceException("Attention vous creer un ficher");
		}
	}
	
	
	/**
	 * rename a file at a specified path
	 * @param path the path of the file to rename
	 * @param name the new name of the file, including the extension
	 * @throws fileNotFound
	 * @throws BadPathInstanceException
	 * @throws AlreadyExistException 
	 */
	public void renameFileAtPath(String path, String name) throws fileNotFound, BadPathInstanceException, AlreadyExistException{
		Hierarchy child = findChild(path);
		if(child instanceof File){
			child.getParent().alreadyExist(name);
			child.setName(name);
		}else{
			throw new BadPathInstanceException("Attention vous essayez de renommer un dossier alors que vous devriez renommer un fichier");
		}
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
