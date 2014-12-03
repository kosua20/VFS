package vfsCore;

import java.util.ArrayList;
import java.util.StringTokenizer;

public class Folder extends Hierarchy{
	
	/**
	 * StringTokenizer use in methods to split path
	 */
	StringTokenizer st;
	
	
	/**
	 * Generated serial UID
	 */
	private static final long serialVersionUID = -4347834853440256629L;

	/**
	 * constructor over the superclass arguments
	 * @param childrens
	 * @param name
	 */
	public Folder(ArrayList<Hierarchy> childrens, String name) {
		super(childrens, name);
	}

	/**
	 * method to go to a path
	 * will be used in every method that need to access a specific directory or file in the hierarchy
	 * @param path : path must be a string like "/folder1/subfold1/file.extension"
	 * @throws fileNotFound 
	 */
	public Hierarchy findChild(String path) throws fileNotFound{
		st = new StringTokenizer(path, "/");
		Hierarchy h1 = this;
		loopOverToken : while(st.hasMoreTokens()){
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
			child.addChild(new Folder(null, nom));
		} else {
			throw new BadPathInstanceException("Attention vous essayer de creer un dossier dans un fichier !");
		}
	}
	
	public void deleteFolderAtPath(String path){
		
	}

}
