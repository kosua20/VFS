package vfsCore;

import java.util.ArrayList;

public class SearchVisitor implements Visitor {
	
	private ArrayList<Hierarchy> found = new ArrayList<Hierarchy>();
	private String search;
	
	public SearchVisitor(String search) {
		super();
		this.search = search;
	}

	@Override
	public void visit(Hierarchy object) {}

	@Override
	public void visit(Folder folder) {
		if (folder.getChildrens()!=null){
			for(Hierarchy child:folder.getChildrens()){
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
