package vfsCore;

import java.util.ArrayList;

public class MainTests {

	public static void main(String[] args) {
		//Core c = new Core();
		//c.createDisk("test.dsk", 23000);
		//CoreIO cio = new CoreIO();
		//cio.saveHierarchyToFile(null, "test.dsk");
		/*Core c = new Core();
		c.createDisk("test2.dsk", 23000);
		Node n1 = new Node();
		n1.setAdress(12);
		n1.setDirectory(false);
		n1.setName("node1");
		Node n2 = new Node();
		n2.setAdress(-1);
		n2.setDirectory(true);
		n2.setName("node2");
		Node n3 = new Node();
		n3.setAdress(19);
		n3.setDirectory(false);
		n3.setName("node3");
		Node n4 = new Node();
		n4.setAdress(0);
		n4.setDirectory(true);
		n4.setName("root");
		ArrayList<Hierarchy> children = new ArrayList<>();
		ArrayList<Hierarchy> children2 = new ArrayList<>();
		children2.add(new Hierarchy(null, n3));
		children.add(new Hierarchy(null, n1));
		children.add(new Hierarchy(children2, n2));
		Hierarchy h1 = new Hierarchy(children, n4);*/
		CoreIO cio = new CoreIO();
		//cio.saveHierarchyToFile(h1, "test2.dsk");
		Hierarchy h2 = cio.loadHierarchyTreeFromFile("test2.dsk");
		
		System.out.println(h2);
	}

}
