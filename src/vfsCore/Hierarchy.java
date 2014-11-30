package vfsCore;

import java.util.ArrayList;

public class Hierarchy {
	private ArrayList<Hierarchy> childrens;
	private Node node;
	private double size = 0;
	
	public Hierarchy(ArrayList<Hierarchy> childrens, Node node) {
		super();
		this.childrens = childrens;
		this.node = node;
	}
	
	public void addChild(Hierarchy newChild){
		this.childrens.add(newChild);
		updateSize();
	}
	
	public void removeChild(Hierarchy child){
		this.childrens.remove(child);
		updateSize();
	}
	
	public double getSize(){
		//Potential loop, check on tests
		updateSize();
		return this.size;
	}
	
	public void updateSize() {
		double tempSize = 0;
		for(Hierarchy child:childrens){
			tempSize = tempSize + child.getSize();
		}
		this.size = this.node.getSize() + tempSize;
	}
	
	
	
	
	
}
