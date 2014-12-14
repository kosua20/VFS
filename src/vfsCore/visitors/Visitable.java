package vfsCore.visitors;
/**
 * The visitable interface is part of the implementation of the Visitor pattern. 
 * It will be implemented by the Hierarchy class and its subclasses, allowing us to visit those
 * @author Simon Rodriguez
 *
 */
public interface Visitable {
	public void accept(Visitor visitor);
}
