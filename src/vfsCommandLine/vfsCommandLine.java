package vfsCommandLine;
import java.util.Scanner;

import vfsCore.*;
public class vfsCommandLine {
	
	private boolean diskOpen=false;
	
	
	public static void main(String[] args){
		String input;
		Scanner reader = new Scanner(System.in);
		Core core = null;
		clLoop:while(true){
			//User input
			System.out.print("vfscl: ");
			input = reader.nextLine();
			
			//We are immediately treating the EXIT COMMAND
			if(input.equalsIgnoreCase("exit")){ break; }
			
			//Else, string treatment
			String[] arguments = input.split(" ");
			//Checking the validity before using the result of the split
			if (arguments.length == 0){
				continue clLoop;
			}
			
			//FIRST SWITCH, managing the disk 
			//(it need to be outside the main switch loop, where a loaded disk is needed)
			switch(arguments[0]){
				case "crvfs":
					core = new Core();
					if(core.createDisk(arguments[1], Long.valueOf(arguments[2]))){
						System.out.println("VFS "+core.getDiskpath()+" ("+core.getTotalSpace()+"B size) has been created.");
					} 
					continue clLoop;
				case "rmvfs":
					core = new Core();
					if(core.deleteDisk(arguments[1])){
						System.out.println("VFS "+core.getDiskpath()+" has been deleted.");
					} 
					core = null;
					continue clLoop;
				case "opvfs":
					core = new Core();
					if(core.openDisk(arguments[1])){
						System.out.println("VFS "+core.getDiskpath()+" ("+core.getTotalSpace()+"B size) has been opened.");
					} 
					continue clLoop;
				case "help":
					displayHelp();
					continue clLoop;
				default:
					break;
			}
			
			//If, and only if, a core has been loaded, we can perform other operations
			if (core != null){
				//MAIN SWITCH
				switch(arguments[0]){
					case "ls":
						core.list();
						break;
					case "cd":
						System.out.println(arguments[0]);
						break;
					case "cp":
						System.out.println(arguments[0]);
						break;
					case "mv":
						System.out.println(arguments[0]);
						break;
					case "rm":
						System.out.println(arguments[0]);
						break;
					case "free":
						System.out.println(arguments[0]);
						break;
					case "find":
						System.out.println(arguments[0]);
						break;
					case "impvfs":
						System.out.println(arguments[0]);
						break;
					case "expvfs":
						System.out.println(arguments[0]);
						break;
					default:
						System.out.println("");
						continue clLoop;
				}		
			} else {
				System.out.println("Please open an existing vfs (opvfs path/to/vfs) or create a new one (crvfs path/to/vfs sizeInBytes) before executing other commands");
				continue clLoop;
			}
		}
		reader.close();
		System.exit(0);
	}
	
	public static void displayHelp(){
		System.out.println("42");
	}
}
