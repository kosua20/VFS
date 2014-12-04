package vfsCore;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.StringTokenizer;

public class Hierarchy implements Serializable {
	
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
	private Hierarchy parent; /*changer constructeur en question*/
	
	/**
	 * StringTokenizer use in methods to split path
	 */
	StringTokenizer st;

	
	
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
	 * Add a child to the hierarchy might be a folder of a file
	 * @param newChild
	 */
	public void addChild(Hierarchy newChild){
		if (this.childrens == null){
			this.childrens = new ArrayList<Hierarchy>();
		}
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
	
	
	
	
	
	/**
	 * method to go to a path
	 * will be used in every method that need to access a specific directory or file in the hierarchy
	 * @param path : path must be a string like "/folder1/subfold1/file.extension"
	 * @throws fileNotFound 
	 */
	public Hierarchy findChild(String path) throws fileNotFound{
		st = new StringTokenizer(path, "/");
		Hierarchy h1 = null;
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
	 * @param path : the string of the path
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
	 * method to delete a folder and all its content at a specified path
	 * @param path
	 * @throws fileNotFound
	 * @throws BadPathInstanceException
	 */
	public void deleteFolderAtPath(String path) throws fileNotFound, BadPathInstanceException{
		Hierarchy child = findChild(path);
		deleteFolderOfHierarchy(child);
		
		
	}
	
	/**
	 * the "tool" method to delete a given in parameter hierarchy
	 * @param child
	 * @throws BadPathInstanceException
	 */
	public void deleteFolderOfHierarchy(Hierarchy child) throws BadPathInstanceException{
		if (child instanceof Folder){
			//deleting the subfolders and subfiles
			for(Hierarchy subpath : child.getChildrens())
			{		
				if(subpath instanceof Folder){
					deleteFolderOfHierarchy(subpath);
				}
				child.removeChild(subpath);
			}
			//deleting the folder it self
			this.removeChild(child);
		}else{
			throw new BadPathInstanceException("Attention vous devez selectionner un DOSSIER a supprimer");
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
		if(child instanceof File){
			child.addChild(new File(nom, child));
		} else {
			throw new BadPathInstanceException("Attention vous creer un ficher");
		}
	}
	
	/**
	 * 
	 * @param path
	 * @throws BadPathInstanceException 
	 * @throws fileNotFound 
	 */
	public void deleteFileAtPath(String path) throws BadPathInstanceException, fileNotFound{
		Hierarchy child = findChild(path);
		if(child instanceof File){
			this.removeChild(child);
		} else {
			throw new BadPathInstanceException("vous essayer de supprimer un dossier alors que vous devirez supprimer un fichier");
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
	
	public Hierarchy goToParent(){
		return this.parent;
	}
	
	public void copyElement(String departure, String destination) throws fileNotFound, BadPathInstanceException{
		Hierarchy toBeCopied = findChild(departure);
		Hierarchy finalStop = findChild(destination);
		if(finalStop instanceof Folder){
			finalStop.addChild(toBeCopied);
		}else{
			throw new BadPathInstanceException("attention vous essayer de copier un element dans un fichier !!");
		}
		
	}
	
	public void moveElement(String departure, String destination) throws fileNotFound, BadPathInstanceException{
		Hierarchy toBeMoved = findChild(departure);
		Hierarchy finalStop = findChild(destination);
		if(finalStop instanceof Folder){
			finalStop.addChild(toBeMoved);
			deleteFolderOfHierarchy(toBeMoved);
		}else{
			throw new BadPathInstanceException("attention vous essayer de copier un element dans un fichier !!");
		}
	}
	
	
	
	
}
