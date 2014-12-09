package vfsCore;

public class SizeVisitor implements Visitor{

	private long sizeUsed = 0;
	
	@Override
	public void visit(Hierarchy object){}
	
	public void visit(Folder folder) {
		//For a folder, its size is zero
		if (folder.getChildrens()==null){
			sizeUsed = sizeUsed+0;
		} else {
			//and we add the size of its children using the same SizeVisitor
			for(Hierarchy child:folder.getChildrens()){
				child.accept(this);
			}
		}
	}

	@Override
	public void visit(vfsCore.File file) {
		//We use the size store in the File element
		sizeUsed = sizeUsed + file.getSize();
		
	}
	
	public long getSizeUsed(){
		return sizeUsed;
	}

}
