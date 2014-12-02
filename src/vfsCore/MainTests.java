package vfsCore;

import java.util.ArrayList;

public class MainTests {

	public static void main(String[] args) {
		Core c =  new Core();
		c.createDisk("test.dsk", 23);
		c.openDisk("test.dsk");
	}

}
