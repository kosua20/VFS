package vfsCore;

import java.util.ArrayList;
import java.util.StringTokenizer;

public class Folder extends Hierarchy{
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
	public Hierarchy foundChild(String path) throws fileNotFound{
		st = new StringTokenizer(path, "/");
		Hierarchy h1 = this;
		loopOverToken : while(st.hasMoreTokens()){
			for(Hierarchy child : h1.getChildrens())
			{	
				if(st.nextToken().equalsIgnoreCase(child.getName()))
				{
					h1=child;
					st.nextToken();
					continue loopOverToken;
				}
			}
			throw new fileNotFound("Chemin inexistant");
		}
		return h1;
	}

}
