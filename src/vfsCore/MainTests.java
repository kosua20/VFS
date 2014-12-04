package vfsCore;

import java.util.ArrayList;

public class MainTests {

	public static void main(String[] args) {
		Core c =  new Core();
		//c.deleteDisk("test.dsk");
		/*c.createDisk("test.dsk", 8000);
		c.openDisk("test.dsk");
		
		c.importElement("truc","truc2");*/
		
		
		c.openDisk("test.dsk");
		//c.importElement("Planning NDT-soirée.pdf", "file 2 é.pdf");
		//c.exportElement("file 2 é.pdf","/testp/truc.pdf");
		//c.exportElement("truc2", "truc3");
		//c.openDisk("test.dsk");
		System.out.println(c.getUsedSpace());
		System.out.println(c.getTotalSpace());
		c.list();
		c.goTo("truc2");
		c.list();
		c.goToParent();
		c.list();
		c.goToParent();
		c.list();
		//System.out.println(c.getFreeSpace());
	}

}
