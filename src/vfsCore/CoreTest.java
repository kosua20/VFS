package vfsCore;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.Test;
//TDD
public class CoreTest {
	Core testCore = new Core();
	//Helpers
	public void resetDisk(){
		testCore.deleteDisk("test/testDisk.dsk");
		testCore.createDisk("test/testDisk.dsk", 8000);
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
		testCore.importElement("test/ressources/test1.txt","/file1.txt");
		testCore.importElement("test/ressources/test2","/folder2");
		
	}
	
	//1 and 2
	@Test
	public void testCreationOfDisk(){
		assertTrue(testCore.createDisk("test/testDisk.dsk", 8000));
		
	}
	
	//3
	@Test
	public void testOpeningOfDisk(){
		testCore.createDisk("test/testDisk.dsk", 8000);
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
		System.out.println("Test delete");
		testCore.list();
		assertTrue(testCore.deleteElementAtPath("/folder2"));
		testCore.list();
		assertTrue(testCore.deleteElementAtPath("/folder3/subfolder4"));
		testCore.goTo("/folder3");
		testCore.list();
		testCore.goToParent();
		assertFalse(testCore.deleteElementAtPath("/folder167777"));
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
		assertTrue(testCore.moveElement("/file1.txt","/folder1/file1.txt"));
		testCore.list();
		testCore.goTo("/folder1");
		testCore.list();
		testCore.goToParent();
		testCore.list();
		assertTrue(testCore.moveElement("/folder1","/folder2/subfolder2/folder1"));
		testCore.list();
		testCore.goTo("/folder2/subfolder2");
		testCore.list();
		testCore.goTo("folder1");
		testCore.list();
		assertFalse(testCore.moveElement("/folder2","/folder1/folder3/folder3"));
		System.out.println("End of test");
	}
	
	@Test
	public void preventMoveOfFolderInItself(){
		createDiskWithData();
		assertFalse(testCore.moveElement("/folder2", "/folder2/sub1/folder2"));
	}
	
	@Test
	public void testCopyElement(){
		createDiskWithData();
		System.out.println("Test copy element");
		testCore.list();
		//testing for a file
		assertTrue(testCore.copyElementAtPath("/file1.txt","/folder2/file1.txt"));
		testCore.list();
		testCore.goTo("/folder2");
		testCore.list();
		testCore.goToParent();
		testCore.createFolderAtPath("/","folder3");
		//testing for a folder
		assertTrue(testCore.copyElementAtPath("/folder2","/folder3/folder2"));
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
		System.out.println("Test import");
		assertTrue(testCore.importElement("test/ressources/test1.txt","/file1.txt"));
		assertTrue(testCore.importElement("test/ressources/test2","/folder2"));
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
		assertEquals(8000*1024-739467,testCore.getFreeSpace());
	}
	@Test
	public void testUsedSpace(){
		createDiskWithData();
		assertEquals(739467,testCore.getUsedSpace());
		System.out.println("Used size : "+testCore.getUsedSpace());
	}
	@Test
	public void testTotalSize(){
		createDiskWithData();
		assertEquals(8000*1024,testCore.getTotalSpace());
	}
	
	//Tests for files/folders bigger than the VFS disk
	/* IMPORTANT :
	 * we commented this test out because we are not providing the files used for it, 
	 * in order to diminish the weight of our compressed project
	 */
	/*
	@Test
	public void rejectFileTooBig(){
		createDiskWithData();
		//In Import
		//(file of size 40MB)
		assertFalse(testCore.importElement("test/Ecran.psd", "ecran.psd"));
		//In Copy
		//(file of size 8MB, copied 2 times)
		testCore.importElement("test/cartes.psd", "cartes1.psd");
		testCore.copyElementAtPath("cartes1.psd", "cartes2.psd");
		assertFalse(testCore.copyElementAtPath("cartes1.psd", "cartes3.psd"));
		//Test with a folder
		assertFalse(testCore.importElement("test/heavy", "heavy"));
	}*/
	
	@Test
	public void rejectElementAlreadyExists(){
		createArborescence();
		System.out.println("Test Already Exists");
		assertFalse(testCore.renameFolderAtPath("/folder2/subfolder1", "subfolder2"));
		testCore.goTo("/folder2");
		testCore.list();
		System.out.println("End of test \n\n");
		
	}
	
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
