package vfsCore;



public class MainTests {

	public static void main(String[] args) {
		Core c =  new Core();
		c.deleteDisk("test.dsk");
		c.createDisk("test.dsk", 8000);
		c.openDisk("test.dsk");
		
		c.importElement("test/ressources/test2","truc2");
		
		
		
		System.out.println(c.getUsedSpace());
		System.out.println(c.getTotalSpace());
		System.out.println(c.getFreeSpace());
		c.list();
		c.goTo("truc2");
		c.list();
		c.goToParent();
		c.list();
		c.goToParent();
		c.list();
		
		c.printSearch("various.pdf");
	}

}
