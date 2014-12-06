package vfsCore;

public class SizeVisitor implements Visitor{

	private long sizeUsed = 0;
	
	@Override
	public void visit(Hierarchy object){}
	
	public void visit(Folder folder) {
		
		if (folder.getChildrens()==null){
			sizeUsed = sizeUsed+0;
		} else {
			for(Hierarchy child:folder.getChildrens()){
				child.accept(this);
			}
		}
	}

	@Override
	public void visit(vfsCore.File file) {
		sizeUsed = sizeUsed + file.getSize();
		
	}
	
	public long getSizeUsed(){
		return sizeUsed;
	}

}
