package vfsCore.visitors;

import vfsCore.Folder;
import vfsCore.Hierarchy;
/**
 * The visitor interface is part of the implementation of the Visitor pattern. 
 * It allows to visit the different expected visitable classes, ie Hierarchy and its subclasses
 * @author Simon Rodriguez
 *
 */
public interface Visitor {
	public void visit(Hierarchy object);
	public void visit(Folder folder);
	public void visit(vfsCore.File file);
}
