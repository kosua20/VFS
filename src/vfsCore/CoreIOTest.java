package vfsCore;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.ArrayList;

import org.junit.Test;

public class CoreIOTest {
	
	@Test
	public void testGetHierarchyBytes() {
		ArrayList<Hierarchy> children = new ArrayList<Hierarchy>();
		Folder test = new Folder(children, "", null);
		test.addChild(new File("file1.txt", 8399, 2383, test));
		test.addChild(new File("file2.jpg", 902, 450000, test));
		Folder folder1 = new Folder(null, "folder1", test);
		folder1.addChild(new File("test3.mp3", 56, 4278900, folder1));
		test.addChild(folder1);
		
		CoreIO cio = new CoreIO("test/testDisk.dsk");
		
		byte[] result = null;
		byte[] test1 = {-84,-19,0,5,115,114,0,14,118,102,115,67,111,114,101,46,70,111,108,100,101,114,-61,-87,99,60,-110,-95,-123,-117,2,0,1,76,0,8,99,104,105,108,100,114,101,110,116,0,21,76,106,97,118,97,47,117,116,105,108,47,65,114,114,97,121,76,105,115,116,59,120,114,0,17,118,102,115,67,111,114,101,46,72,105,101,114,97,114,99,104,121,-114,11,-21,-65,-56,-9,-76,-36,2,0,2,76,0,4,110,97,109,101,116,0,18,76,106,97,118,97,47,108,97,110,103,47,83,116,114,105,110,103,59,76,0,6,112,97,114,101,110,116,116,0,16,76,118,102,115,67,111,114,101,47,70,111,108,100,101,114,59,120,112,116,0,0,112,115,114,0,19,106,97,118,97,46,117,116,105,108,46,65,114,114,97,121,76,105,115,116,120,-127,-46,29,-103,-57,97,-99,3,0,1,73,0,4,115,105,122,101,120,112,0,0,0,3,119,4,0,0,0,3,115,114,0,12,118,102,115,67,111,114,101,46,70,105,108,101,53,-27,-80,4,-92,-12,-110,17,2,0,2,74,0,7,97,100,100,114,101,115,115,74,0,4,115,105,122,101,120,113,0,126,0,2,116,0,9,102,105,108,101,49,46,116,120,116,113,0,126,0,5,0,0,0,0,0,0,32,-49,0,0,0,0,0,0,9,79,115,113,0,126,0,9,116,0,9,102,105,108,101,50,46,106,112,103,113,0,126,0,5,0,0,0,0,0,0,3,-122,0,0,0,0,0,6,-35,-48,115,113,0,126,0,0,116,0,7,102,111,108,100,101,114,49,113,0,126,0,5,115,113,0,126,0,7,0,0,0,1,119,4,0,0,0,1,115,113,0,126,0,9,116,0,9,116,101,115,116,51,46,109,112,51,113,0,126,0,14,0,0,0,0,0,0,0,56,0,0,0,0,0,65,74,116,120,120};
		
		try {
			result = cio.getHierarchyBytes(test);
		} catch (IOException e) {
			System.out.println("IOERROR");
		}
		
		assertArrayEquals(test1, result);
	}

	@Test
	public void testGetHierarchyFromBytes() {
		byte[] test1 = {-84,-19,0,5,115,114,0,14,118,102,115,67,111,114,101,46,70,111,108,100,101,114,-61,-87,99,60,-110,-95,-123,-117,2,0,1,76,0,8,99,104,105,108,100,114,101,110,116,0,21,76,106,97,118,97,47,117,116,105,108,47,65,114,114,97,121,76,105,115,116,59,120,114,0,17,118,102,115,67,111,114,101,46,72,105,101,114,97,114,99,104,121,-114,11,-21,-65,-56,-9,-76,-36,2,0,2,76,0,4,110,97,109,101,116,0,18,76,106,97,118,97,47,108,97,110,103,47,83,116,114,105,110,103,59,76,0,6,112,97,114,101,110,116,116,0,16,76,118,102,115,67,111,114,101,47,70,111,108,100,101,114,59,120,112,116,0,0,112,115,114,0,19,106,97,118,97,46,117,116,105,108,46,65,114,114,97,121,76,105,115,116,120,-127,-46,29,-103,-57,97,-99,3,0,1,73,0,4,115,105,122,101,120,112,0,0,0,3,119,4,0,0,0,3,115,114,0,12,118,102,115,67,111,114,101,46,70,105,108,101,53,-27,-80,4,-92,-12,-110,17,2,0,2,74,0,7,97,100,100,114,101,115,115,74,0,4,115,105,122,101,120,113,0,126,0,2,116,0,9,102,105,108,101,49,46,116,120,116,113,0,126,0,5,0,0,0,0,0,0,32,-49,0,0,0,0,0,0,9,79,115,113,0,126,0,9,116,0,9,102,105,108,101,50,46,106,112,103,113,0,126,0,5,0,0,0,0,0,0,3,-122,0,0,0,0,0,6,-35,-48,115,113,0,126,0,0,116,0,7,102,111,108,100,101,114,49,113,0,126,0,5,115,113,0,126,0,7,0,0,0,1,119,4,0,0,0,1,115,113,0,126,0,9,116,0,9,116,101,115,116,51,46,109,112,51,113,0,126,0,14,0,0,0,0,0,0,0,56,0,0,0,0,0,65,74,116,120,120};
		Hierarchy h1 = null;
		try {
			CoreIO cio = new CoreIO("test/testDisk.dsk");
			h1 = cio.getHierarchyFromBytes(test1);
		} catch (Exception e) {
			System.out.println("IOERROR");
		}
		assertNotNull(h1);
		Folder h2 = (Folder)h1;
		System.out.println(h2.getName() + " "+h2.getParent());
		for(Hierarchy h3:h2.getChildren()){
			System.out.print(h3.getName());
			if (h3 instanceof vfsCore.File){
				System.out.println(" "+((vfsCore.File)h3).getSize());
			}
		}
	}

	@Test
	public void testSizeOfFolder() {
		CoreIO cio = new CoreIO("test/testDisk.dsk");
		assertEquals(891716, cio.sizeOfFolder(new java.io.File("test/ressources")));
	}

}
