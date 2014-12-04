package vfsCore;

import java.util.ArrayList;

public class MainTests {

	public static void main(String[] args) {
		Core c =  new Core();
		/*c.deleteDisk("test.dsk");
		c.createDisk("test.dsk", 23000);
		c.openDisk("test.dsk");
		//c.importElement("file 2 é.pdf", "file 2 é.pdf");
		c.importElement("truc","truc");*/
		
		
		c.openDisk("test.dsk");
		//c.exportElement("file 2 é.pdf","truc.pdf");
		c.exportElement("truc", "truc1");
	}

}
