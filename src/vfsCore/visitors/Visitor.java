package vfsCore.visitors;

import vfsCore.File;
import vfsCore.Folder;
import vfsCore.Hierarchy;

public interface Visitor {
	public void visit(Hierarchy object);
	public void visit(Folder folder);
	public void visit(vfsCore.File file);
}
