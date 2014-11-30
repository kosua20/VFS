package vfsCore;

import static org.junit.Assert.*;

import org.junit.Test;
//TDD
public class CoreTest {
	Core testCore = new Core();
	//Requirements
	
	//1 and 2
	@Test
	public void testCreationOfDisk(){
		assertTrue(testCore.createDisk("test/testDisk.vfsd", 23000));
	}
	
	//3
	@Test
	public void testOpeningOfDisk(){
		assertTrue(testCore.openDisk("test/testDisk.vfsd"));
	} 
	
	//4
	@Test
	public void testDeletionOfDisk(){
		assertTrue(testCore.deleteDisk("test/testDisk.vfsd"));
	}
	
	//5
	//Folders
	@Test
	public void testCreationOfFolder(){
		assertTrue(testCore.createFolderAtPath("/","folder1"));
	}
	@Test
	public void testDeletionOfFolder(){
		assertTrue(testCore.deleteFolderAtPath("/folder1"));
	}
	@Test
	public void testRenamingFolder(){
		assertTrue(testCore.renameFolderAtPath("/folder1","folderNew"));
	}
	//Files
	@Test
	public void testCreationOfFile(){
		assertTrue(testCore.createFileAtPath("/","file1.txt"));
	}
	@Test
	public void testDeletionOfFile(){
		assertTrue(testCore.deleteFileAtPath("/file1.txt"));
	}
	@Test
	public void testRenamingFile(){
		assertTrue(testCore.renameFileAtPath("/file1.txt","newFile2.jpg"));
	}
	
	//6
	@Test
	public void testList(){
		assertTrue(testCore.list());
	}
	@Test
	public void testGo(){
		assertTrue(testCore.goTo("folder1/folder2"));
		assertTrue(testCore.goTo("/folder1/folder2"));
	}
	@Test
	public void testGoToParent(){
		assertTrue(testCore.goToParent());
	}
	
	//7
	@Test
	public void testMoveElement(){
		assertTrue(testCore.moveElement("/file1.txt","/folder1/file1.txt"));
		assertTrue(testCore.moveElement("/folder2","/folder1/folder3/folder2"));
	}
	@Test
	public void testCopyElement(){
		assertTrue(testCore.copyElement("/file1.txt","/folder1/file1.txt"));
		assertTrue(testCore.copyElement("/folder2","/folder1/folder3/folder2"));
	}
	
	//8
	@Test
	public void testImportElement(){
		assertTrue(testCore.importElement("test/test1.txt","/file1.txt"));
		assertTrue(testCore.importElement("test/test2","/folder2"));
	}
	
	//9
	@Test
	public void testExportElement(){
		assertTrue(testCore.exportElement("/file1.txt","test/test1.txt"));
		assertTrue(testCore.exportElement("/folder2","test/test2"));
	}
	
	//10
	@Test
	public void testFreeSpace(){
		assertEquals(22000,testCore.getFreeSpace());
	}
	@Test
	public void testUsedSpace(){
		assertEquals(1000,testCore.getUsedSpace());
	}
	@Test
	public void testTotalSize(){
		assertEquals(23000,testCore.getTotalSize());
	}
	
	//11
	@Test
	public void testSearch(){
		assertTrue(testCore.searchFile("truc"));
	}
	
}
