package vfsCore;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.Test;
//TDD
public class CoreTest {
	Core testCore = new Core();
	//Requirements
	public void resetDisk(){
		testCore.deleteDisk("test/testDisk.dsk");
		testCore.createDisk("test/testDisk.dsk", 23000);
		testCore.openDisk("test/testDisk.dsk");
	}
	
	public void createArborescence(){
		resetDisk();
		testCore.createFolderAtPath("/","folder1");
		testCore.createFolderAtPath("/","folder2");
		testCore.createFolderAtPath("/folder2","subfolder1");
		testCore.createFolderAtPath("/folder2","subfolder2");
		testCore.createFileAtPath("/", "file2.txt");
		testCore.createFileAtPath("/", "file1.txt");
		testCore.createFileAtPath("/folder2", "file277.txt");
		testCore.createFileAtPath("/folder2/subfolder1", "file300.txt");
	}
	
	public void createDiskWithData(){
		resetDisk();
		testCore.importElement("test/test1.txt","file1.txt");
		testCore.importElement("test/test2","folder2");
		
	}
	
	//1 and 2
	@Test
	public void testCreationOfDisk(){
		assertTrue(testCore.createDisk("test/testDisk.dsk", 23000));
		
	}
	
	//3
	@Test
	public void testOpeningOfDisk(){
		testCore.createDisk("test/testDisk.dsk", 23000);
		assertTrue(testCore.openDisk("test/testDisk.dsk"));
	} 
	
	//4
	@Test
	public void testDeletionOfDisk(){
		testCore.createDisk("test/testDisk2.dsk", 20000);
		assertTrue(testCore.deleteDisk("test/testDisk2.dsk"));
	}
	
	//5
	//Folders
	@Test
	public void testCreationOfFolder(){
		resetDisk();
		System.out.println("Test Create");
		assertTrue(testCore.createFolderAtPath("/","folder1"));
		testCore.list();
	}
	@Test
	public void testExceptionIfAlreadyExists(){
		resetDisk();
		testCore.createFolderAtPath("/","folder1");
		assertFalse(testCore.createFolderAtPath("/", "folder1"));
	}
	@Test
	public void testExceptionIfIntermediateDirectoriesDontExist(){
		resetDisk();
		assertFalse(testCore.createFolderAtPath("/thingy", "folder1"));
	}
	@Test
	public void testDeletionOfFolder(){
		resetDisk();
		testCore.createFolderAtPath("/","folder2");
		testCore.createFolderAtPath("/","folder3");
		testCore.createFolderAtPath("/folder3","subfolder4");
		testCore.createFolderAtPath("/folder3/subfolder4","subsubfolder5");
		testCore.createFileAtPath("/folder3/subfolder4","th.txt");
		System.out.println("Delete liste 1");
		testCore.list();
		assertTrue(testCore.deleteFolderAtPath("/folder2"));
		testCore.list();
		assertTrue(testCore.deleteFolderAtPath("/folder3/subfolder4"));
		testCore.goTo("/folder3");
		testCore.list();
		testCore.goToParent();
		assertFalse(testCore.deleteFolderAtPath("/folder167777"));
		System.out.println("End of test");
	}
	@Test
	public void testRenamingFolder(){
		createArborescence();
		System.out.println("Rename list 1");
		testCore.list();
		assertTrue(testCore.renameFolderAtPath("/folder1","folderNew"));
		System.out.println("Rename list 2");
		testCore.list();
		System.out.println("End of test");
	}
	//Files
	@Test
	public void testCreationOfFile(){
		resetDisk();
		assertTrue(testCore.createFileAtPath("/","file1.txt"));
	}
	@Test
	public void testDeletionOfFile(){
		resetDisk();
		testCore.createFolderAtPath("/","folder9");
		testCore.createFileAtPath("/folder9","file1.txt");
		assertFalse(testCore.deleteFileAtPath("/file1.txt"));
		assertTrue(testCore.deleteFileAtPath("/folder9/file1.txt"));
	}
	@Test
	public void testRenamingFile(){
		createArborescence();
		assertTrue(testCore.renameFileAtPath("/file1.txt","newFile2.jpg"));
		assertFalse(testCore.renameFileAtPath("/folder1/filrer.jpeg","newFile.gif"));
		assertTrue(testCore.renameFileAtPath("/folder2/file277.txt","newFile12.mp3"));
	}
	
	//6
	@Test
	public void testList(){
		createArborescence();
		System.out.println("Test list");
		testCore.list();
		System.out.println("End of test");
	}
	@Test
	public void testGo(){
		createArborescence();
		assertTrue(testCore.goTo("/folder2/subfolder1"));
		System.out.println("Test Go");
		testCore.list();
		createArborescence();
		assertFalse(testCore.goTo("/folder1/folder2"));
		System.out.println("End of test");
	}
	@Test
	public void testGoToParent(){
		createArborescence();
		testCore.goTo("/folder2/subfolder1");
		System.out.println("test GoToParent");
		testCore.list();
		assertTrue(testCore.goToParent());
		testCore.list();
		testCore.goToParent();
		assertFalse(testCore.goToParent());
		System.out.println("End of test");
	}
	
	//7
	@Test
	public void testMoveElement(){
		createArborescence();
		System.out.println("Test move element");
		testCore.list();
		assertTrue(testCore.moveElement("/file1.txt","/folder1"));
		testCore.list();
		testCore.goTo("/folder1");
		testCore.list();
		testCore.goToParent();
		testCore.list();
		assertTrue(testCore.moveElement("/folder1","/folder2/subfolder2"));
		testCore.list();
		testCore.goTo("/folder2/subfolder2");
		testCore.list();
		testCore.goTo("folder1");
		testCore.list();
		assertFalse(testCore.moveElement("/folder2","/folder1/folder3/folder2"));
		System.out.println("End of test");
	}
	@Test
	public void testCopyElement(){
		createDiskWithData();
		System.out.println("Test copy element");
		testCore.list();
		//tetsing for a file
		assertTrue(testCore.copyElementAtPath("/file1.txt","/folder2"));
		testCore.list();
		testCore.goTo("/folder2");
		testCore.list();
		testCore.goToParent();
		testCore.createFolderAtPath("/","folder3");
		//testing for a folder
		assertTrue(testCore.copyElementAtPath("/folder2","/folder3"));
		testCore.goTo("/folder3/folder2");
		testCore.list();
		//Checking that deleting the original element doesn't delete the data of the copy
		testCore.goToParent();
		testCore.goToParent();
		testCore.deleteFileAtPath("/file1.txt");
		testCore.goTo("/folder2");
		testCore.list();
		assertTrue(testCore.exportElement("/folder2/file1.txt", "test/testCopy1.txt"));
		System.out.println("End of test");
	}
	
	//8
	@Test
	public void testImportElement(){
		resetDisk();
		assertTrue(testCore.importElement("test/test1.txt","file1.txt"));
		assertTrue(testCore.importElement("test/test2","folder2"));
		System.out.println("Test import");
		testCore.list();
		testCore.goTo("folder2");
		testCore.list();
		System.out.println("End of test");
	}
	
	//9
	@Test
	public void testExportElement(){
		createDiskWithData();
		System.out.println("Test export");
		assertTrue(testCore.exportElement("/file1.txt","test/test23.txt"));
		assertTrue(testCore.exportElement("/folder2","test/test34"));
		System.out.println("End of test");
	}
	
	//10
	@Test
	public void testFreeSpace(){
		createDiskWithData();
		assertEquals(23000*1024-562669,testCore.getFreeSpace());
	}
	@Test
	public void testUsedSpace(){
		createDiskWithData();
		assertEquals(562669,testCore.getUsedSpace());
	}
	@Test
	public void testTotalSize(){
		createDiskWithData();
		assertEquals(23000*1024,testCore.getTotalSpace());
	}
	
	//Tests for files/folders bigger than teh VFS disk
	
	
	//11
	@Test
	public void testSearch(){
		createDiskWithData();
		System.out.println("Test search");
		assertTrue(testCore.searchFile("truc").equals(new ArrayList<Hierarchy>()));
		testCore.printSearch("t3.jpg");
		System.out.println("End of test\n\n");
	}
	
}
