package vfsCore.visitors;

import vfsCore.Folder;
import vfsCore.Hierarchy;
/**
 * The SizeVisitor is designed to calculate the size of any given Hierarchy, taking into account all its subfolders and files.
 * It has a long property denoting the size of the Hierarchy.
 * @author simon
 *
 */
public class SizeVisitor implements Visitor{

	private long sizeUsed = 0;
	
	@Override
	public void visit(Hierarchy object){}
	
	@Override
	public void visit(Folder folder) {
		//For a folder, its size is zero
		if (folder.getChildren()==null){
			sizeUsed = sizeUsed+0;
		} else {
			//and we add the size of its children using the same SizeVisitor
			for(Hierarchy child:folder.getChildren()){
				child.accept(this);
			}
		}
	}

	@Override
	public void visit(vfsCore.File file) {
		//We use the size store in the File element
		sizeUsed = sizeUsed + file.getSize();
		
	}
	
	/**
	 * 
	 * @return the size of the visited elements
	 */
	public long getSizeUsed(){
		return sizeUsed;
	}

}
