package vfsCore;

import java.util.ArrayList;
import java.util.StringTokenizer;

import vfsCore.exceptions.AlreadyExistException;
import vfsCore.exceptions.BadPathInstanceException;
import vfsCore.exceptions.fileNotFound;

public class Folder extends Hierarchy{
	
	
	
	
	/**
	 * Generated serial UID
	 */
	private static final long serialVersionUID = -4347834853440256629L;
	/**
	 * arraylist of children of a folder element
	 */
	private ArrayList<Hierarchy> childrens = new ArrayList<Hierarchy>();
	/**
	 * constructor
	 * @param childrens
	 * @param name
	 * @param parent
	 */
	public Folder(ArrayList<Hierarchy> childrens, String name, Folder parent) {
		super(name, parent);
		if (childrens != null){
			for(Hierarchy child:childrens){
				child.setParent(this);
			}
			this.childrens = childrens;
		} else {
			this.childrens = new ArrayList<Hierarchy>();
		}
	}
	
		//---------------------//
		//MANAGING THE CHILDREN//
		//---------------------//
		
		/**
		 * Add a child to the hierarchy, might be a folder or a file
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
				if(h1 instanceof Folder){
					String currentItem = st.nextToken();
					for(Hierarchy child : ((Folder)h1).getChildrens())
					{	
						if(currentItem.equalsIgnoreCase(child.getName())){
							h1=child;
							continue loopOverToken;
						}
					}
					throw new fileNotFound("Chemin inexistant");
				}
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
				} else if (element instanceof Folder){
					if(((Folder)element).hasAsChild(potentialChild)){
					return true;
					}
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
				((Folder)child).alreadyExist(nom);
				((Folder)child).addChild(new Folder(null, nom, (Folder)child));
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
				((Folder)child).alreadyExist(nom);
				((Folder)child).addChild(new File(nom, (Folder)child));
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

	
	
	

}
