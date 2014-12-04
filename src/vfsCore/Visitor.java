package vfsCore;

public interface Visitor {
	public void visit(Hierarchy object);
	public void visit(Folder folder);
	public void visit(vfsCore.File file);
}
