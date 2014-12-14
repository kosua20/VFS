package vfsCore.visitors;

import java.util.ArrayList;

import vfsCore.File;
import vfsCore.Folder;
import vfsCore.Hierarchy;

public class SearchVisitor implements Visitor {
	//This array will be used to store the found files corresponding to the search
	private ArrayList<Hierarchy> found = new ArrayList<Hierarchy>();
	//The name we want to find
	private String search;
	
	public SearchVisitor(String search) {
		super();
		this.search = search;
	}

	@Override
	public void visit(Hierarchy object) {}

	@Override
	public void visit(Folder folder) {
		//If the folder has chilren, and as we are only looking for files, we need to go deeper in the hierarchy
		if (folder.getChildren()!=null){
			for(Hierarchy child:folder.getChildren()){
				child.accept(this);
			}
		}
	}

	@Override
	public void visit(File file) {
		if (file.getName().equalsIgnoreCase(search)){
			found.add(file);
		}
	}
	
	public ArrayList<Hierarchy> getElements(){
		return found;
	}

}
