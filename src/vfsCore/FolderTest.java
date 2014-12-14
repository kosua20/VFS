package vfsCore;

import static org.junit.Assert.*;

import org.junit.Test;

import vfsCore.exceptions.fileNotFound;

/**
 * tests for folders (hierarchy) methods
 * @author michaellavner
 *
 */

public class FolderTest {
	/* IMPORTANT
	 * createFolderAtPath()
	 * renameFolderAtPath()
	 * createFileAtPath()
	 * renameFileAtPath()
	 * were already almost directly tested in CoreTest
	 */
	
	//Helpers
	
	public Folder initFolder(Hierarchy h1){
		Folder f1 = new Folder(null, "folder1", null);
		Folder f2 = new Folder(null, "folder2", f1);
		Folder f3 = new Folder(null, "folder3", f1);
		Folder f4 = new Folder(null, "folder4", f2);
		Folder f5 = new Folder(null, "folder4", f2);
		File fi1 = new File("file1", 1, 3455, f1);
		File fi2 = new File("file2", 6789, 45678, f1);
		File fi3 = new File("file3", 6781, 788, f2);
		File fi5 = new File("file5", 66789, 98765, f3);
		File fi6 = new File("file6", 6000, 5678, f5);
		f5.addChild(fi6);
		f3.addChild(fi5);
		f3.addChild(h1);
		f2.addChild(fi3);
		f1.addChild(fi1);
		f1.addChild(fi2);
		f1.addChild(f2);
		f1.addChild(f3);
		f2.addChild(f4);
		f2.addChild(f5);
		return f1;
	}
	
	//Tests
	@Test
	public void testAddChild() {
		Folder f1 = new Folder(null,"tru",null);
		File c1 = new File("file.gt", 56, 4689, null);
		f1.addChild(c1);
		assertEquals(1,f1.getChildren().size());
		assertTrue(f1.getChildren().contains(c1));
	}

	@Test
	public void testRemoveChild() {
		Folder f1 = initFolder(new File("file4", 6654, 4557678, null));
		Hierarchy h1 = f1.getChildren().get(0);
		f1.removeChild(h1);
		assertEquals(3, f1.getChildren().size());
		assertFalse(f1.getChildren().contains(h1));
	}

	@Test
	public void testFindChild() {
		File fi = new File("thing.txt", null);
		Folder f1 = initFolder(fi);
		try {
			assertEquals(fi,f1.findChild("/folder3/thing.txt"));
		} catch (fileNotFound e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	
	public boolean helperAlreadyExist(Folder f1, String name){
		try {
				f1.alreadyExist(name);
				return false;
			} catch (Exception e){
				return true;
			}
	}

	@Test
	public void testAlreadyExist() {
		Folder f1 = initFolder(new File("file4", 6654, 4557678, null));
		assertTrue(helperAlreadyExist(f1, "file2"));
		assertFalse(helperAlreadyExist(f1, "file18.jpj"));
		assertTrue(helperAlreadyExist(f1, "folder3"));
	}

	@Test
	public void testHasAsChild() {
		Folder fi4 = new Folder(null, "fi4", null);
		Folder fi5 = new Folder(null, "fi5", null);
		Folder f1 = initFolder(fi4);
		assertTrue(f1.hasAsChild(fi4));
		assertFalse(f1.hasAsChild(fi5));
	}		

}
