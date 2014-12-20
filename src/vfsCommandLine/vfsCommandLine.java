package vfsCommandLine;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import vfsCore.*;
import vfsCore.visitors.SearchVisitor;
public class vfsCommandLine {
	/**
	 * Keeps reference to the currently opened disk, via its Core instance.
	 */
	private Core core = null;
	private Map<String, Core> openCores = new HashMap<>();
	
	public static void main(String[] args){
		vfsCommandLine vfsCL = new vfsCommandLine();
		vfsCL.mainLoop();
	}
	
	/**
	 * Executes the main loop of the Command-Line
	 */
	public void mainLoop(){
		String input;
		Scanner reader = new Scanner(System.in);
		System.out.println("Welcome in the VFS disk command-line manager. Escape spaces in paths using a \\ prefix. If you need help, just type 'help'");
		clLoop:while(true){
			
			//User input
			System.out.print("vfscl: ");
			input = reader.nextLine();
			
			//We are immediately treating the EXIT COMMAND
			if(input.equalsIgnoreCase("exit")){	break clLoop; }
			
			//Else, string treatment, we split using a regex to avoid splitting escaped spaces in paths
			String[] arguments = input.split("(?<!\\\\)\\s");
			
			//Checking the validity before using the result of the split
			if (arguments.length == 0){ 
				//Not enough arguments to work, so the command is unusable
				System.out.println("Unknown command");
				continue clLoop;
			}
			
			//We then restore each path in the arguments, to its correct form, replacing the escaped characters by their originals
			for(int i = 1;i<arguments.length;i++){
				arguments[i] = arguments[i].replaceAll("\\\\\\s", " ");
			}
			
			try{
				String[] arguments1 = arguments;
				/*This part is dedicated to finding the correct core for managing the VFS disk passed in arguments, or else initializing it
				First, if the command is not a command where we don't need a specifically initialized core (help + those about the VFS disks themselves), 
				we have to deal with it*/
				if (arguments.length>1&&!arguments[0].equals("crvfs") && !arguments[0].equals("opvfs") && !arguments[0].equals("rmvfs")){
					//We search in the HashMap if a core with the same name exists
					core = openCores.get(arguments[1]);
					//If the core doesn't exist, the openCores.get() method returns null
					if (core == null){
						//In this case, we execute an opvfs command with the correct extracted arguments before calling the real command
						String[] arguments2 = {arguments[0],arguments[1]};
						opvfs(arguments2);
					}
				}
			
				//Main switch
				switch(arguments[0]){
					case "crvfs":
						crvfs(arguments);
						break;
					case "rmvfs":
						rmvfs(arguments);
						break;
					case "opvfs":
						opvfs(arguments);
						break;
					case "help":
						displayHelp();
						break;
					case "ls":
						ls(arguments1);
						break;
					case "cd":
						cd(arguments1);
						break;
					case "cp":
						cp(arguments1);
						break;
					case "mv":
						mv(arguments1);
						break;
					case "rm":
						rm(arguments1);
						break;
					case "free":
						free(arguments1);
						break;
					case "find":
						find(arguments1);
						break;
					case "impvfs":
						impvfs(arguments1);
						break;
					case "expvfs":
						expvfs(arguments1);
						break;
					default:
						System.out.println("Command unknown");
						break;	
				}
			} catch (ExecutionErrorException e){
				
				//e.printStackTrace();
			} catch (SyntaxException e){
				System.out.println("Wrong syntax");
			} catch (CoreNotInitalisedException e){
				System.out.println("Please open an existing vfs (opvfs path/to/vfs) or create a new one (crvfs path/to/vfs sizeInBytes) before executing other commands");
			}
		}
		reader.close();
		System.exit(0);
	}
	
	/**
	 * Lists the element at the current or designated path, with the -l option used to display sizes
	 * @param args the passed arguments
	 * @throws SyntaxException
	 * @throws CoreNotInitalisedException
	 */
	private void ls(String[] args) throws SyntaxException, CoreNotInitalisedException{
		if (core == null){ throw new CoreNotInitalisedException();}
		switch (args.length) {
		case 2:
			core.list(false);
			break;
		case 3:
			if (args[2].equalsIgnoreCase("-l")){
				core.list(true);
			} else {
				core.goTo(args[2]);
				core.list(false);
			}
			break;
		case 4:
			core.goTo(args[3]);
			core.list(true);
			break;
		default:
			throw new SyntaxException();
		}
	}
	/**
	 * to change the current position in the VFS
	 * .. must go to parent
	 * . represent current directory
	 * @param args vfsName and path
	 * @throws ExecutionErrorException
	 * @throws SyntaxException
	 * @throws CoreNotInitalisedException
	 */
	private void cd(String[] args) throws ExecutionErrorException, SyntaxException, CoreNotInitalisedException{
		if (core == null){ throw new CoreNotInitalisedException();}
		if(args.length!=3){throw new SyntaxException();}
		switch (args[2]){
		case ".":
			break;
		case "..":
			core.goToParent();
			break;
		default :
			core.goTo(args[2]);
		}
		
	}
	
	/**
	 * to copy, within the VFS in argument, the content of a file/directory whose absolute name 
	 * is source path into a target file/directory whose absolute name is the last argument.
	 * @param args
	 * @throws ExecutionErrorException
	 * @throws SyntaxException
	 * @throws CoreNotInitalisedException
	 */
	private void cp(String[] args) throws ExecutionErrorException, SyntaxException, CoreNotInitalisedException{
		if (core == null){ throw new CoreNotInitalisedException();}
		if(args.length!=3){throw new SyntaxException();}
		if(!core.copyElementAtPath(args[2], args[3])){
			throw new ExecutionErrorException();
		}
		
	}
	
	/**
	 * to change the name of a file/directory with absolute name oldpath (args[2)] in the new absolute name newpath (args[3]) 
	 * of the VFS named vfsname (args[1]).
	 * @param args
	 * @throws ExecutionErrorException
	 * @throws SyntaxException
	 * @throws CoreNotInitalisedException
	 */
	private void mv(String[] args) throws ExecutionErrorException, SyntaxException, CoreNotInitalisedException{
		if (core == null){ throw new CoreNotInitalisedException();}
		if(args.length!=4){throw new SyntaxException();}
		if(!core.moveElement(args[2], args[3])){
			throw new ExecutionErrorException();
		}
		
	}
	
	/**
	 * to remove a file/directory with absolute name pathname (last of 2 arguments) from the VFS passed in argument.
	 * @param args
	 * @throws ExecutionErrorException
	 * @throws SyntaxException
	 * @throws CoreNotInitalisedException
	 */
	private void rm(String[] args) throws ExecutionErrorException, SyntaxException, CoreNotInitalisedException{
		if (core == null){ throw new CoreNotInitalisedException();}
		if(args.length!=3){throw new SyntaxException();}
		if(!core.deleteElementAtPath(args[2])){
			throw new ExecutionErrorException();
		}
		
	}
	
	/**
	 * to display the quantity of free/occupied space for VFS named in argument
	 * @param args
	 * @throws ExecutionErrorException
	 * @throws SyntaxException
	 * @throws CoreNotInitalisedException
	 */
	private void free(String[] args) throws ExecutionErrorException, SyntaxException, CoreNotInitalisedException{
		if (core == null){ throw new CoreNotInitalisedException();}
		if(args.length!=2){throw new SyntaxException();}
		long space = core.getFreeSpace();
		System.out.println(space);
		
		
	}
	
	/**
	 * to search if a file named filename is stored in the VFS named in argument, 
	 * shall return the absolute path of the sought file if it is present in the VFS, null otherwise
	 * This search action will be done by using the implemented visitor pattern
	 * @param args
	 * @throws ExecutionErrorException
	 * @throws SyntaxException
	 * @throws CoreNotInitalisedException
	 */
	private void find(String[] args) throws ExecutionErrorException, SyntaxException, CoreNotInitalisedException{
		SearchVisitor visitor = new SearchVisitor(args[2]);
		
	}

	/**
	 * create a new vfs at the specified path, with the specified size (in kB)
	 * @param args the passed arguments
	 * @throws SyntaxException
	 * @throws ExecutionErrorException
	 */
	public void crvfs(String[] args) throws SyntaxException, ExecutionErrorException{
		if (args.length != 4){throw new SyntaxException();}
		Core newCore = new Core();
		core = newCore;
		if(core.createDisk(args[2], Long.valueOf(args[3]))){
			//We add the Core to the list of opened cores
			openCores.put(args[2], newCore);
			//We set the current core to the new one
			core = newCore;
			System.out.println("VFS "+core.getDiskpath()+" ("+core.getTotalSpace()+"B size) has been created.");
		} else {
			throw new ExecutionErrorException();
		}
	}
	
	/**
	 * Remove the VFS disk at the specified path if it exists
	 * @param args the passed arguments
	 * @throws ExecutionErrorException
	 * @throws SyntaxException
	 */
	public void rmvfs(String[] args) throws ExecutionErrorException, SyntaxException{
		if (args.length != 3){throw new SyntaxException();}
		Core newCore = new Core();
		core = newCore;
		if(core.deleteDisk(args[2])){
			//We also remove the maybe existing Core dedicated to this disk in the openCores list
			openCores.remove(args[2]);
			System.out.println("VFS "+core.getDiskpath()+" has been deleted.");
		}  else {
			throw new ExecutionErrorException();
		}
		//We set the current core to null
		core = null;
	}
	
	/**
	 * Open the vfs passed in the arguments
	 * @param args args the passed arguments
	 * @throws ExecutionErrorException
	 * @throws SyntaxException
	 */
	public void opvfs(String[] args) throws ExecutionErrorException, SyntaxException{
		if (args.length != 3){ throw new SyntaxException();}
		Core newCore = new Core();
		core = newCore;
		if(core.openDisk(args[2])){
			//put either create a new (key,value) or replace the previous Core dedicated to this disk
			openCores.put(args[2], newCore);
			core = newCore;
			System.out.println("VFS "+core.getDiskpath()+" ("+core.getTotalSpace()+"B size) has been opened.");
		}  else {
			throw new ExecutionErrorException();
		}
	}

	/**
	 * imports the elements passed as arguments from the command-line loop
	 * @param args the passed arguments
	 * @throws ExecutionErrorException
	 * @throws SyntaxException
	 * @throws CoreNotInitalisedException
	 */
	private void impvfs(String[] args) throws ExecutionErrorException, SyntaxException, CoreNotInitalisedException{
		if (core == null){ throw new CoreNotInitalisedException();}
		if (args.length != 4){ throw new SyntaxException();}
		if(!core.importElement(args[2], args[3])){
			throw new ExecutionErrorException();
		}
		
	}
	
	/**
	 * exports the elements passed as arguments from the command-line loop
	 * @param args the passed arguments
	 * @throws ExecutionErrorException
	 * @throws SyntaxException
	 * @throws CoreNotInitalisedException
	 */
	private void expvfs(String[] args) throws ExecutionErrorException, SyntaxException, CoreNotInitalisedException{
		if (core == null){ throw new CoreNotInitalisedException();}
		if (args.length != 4){ throw new SyntaxException();}
		if(!core.exportElement(args[2], args[3])){
			throw new ExecutionErrorException();
		}
	}
	
	/**
	 * display a useful list of commands, detailed with their arguments
	 */
	public void displayHelp(){
		System.out.println("--------------------------Managing VFS disks--------------------------\n"
				+ "crvfs <vfsname> <dim>\t\t\tcreate a new VFS disk with the specified name and size (in kB)\n"
				+ "opvfs <vfname>\t\t\t\topen the existing VFS disk with the specified name\n"
				+ "rmvfs <vfsname>\t\t\t\tdelete the existing VFS disk with the specified name\n"
				+ "--------------------------Using the VFS disk--------------------------\n"
				+ "ls <vfsname> <args> <pathname>\t\tlist the content of the folder at the specified path in the VFS, if args='-l' displays the size of each element too\n"
				+ "cd <vfsname> <pathname>\t\t\tchange current directory on the VFS, 'cd ..' goes to the parent directory\n"
				+ "mv <vfsname> <oldpath> <newpath>\tmove the element at path oldpath to newpath (name included)\n"
				+ "cp <vfsname> <sourcepath> <targetpath>\tcopy the element at path sourcepath to targetpath (name included)\n"
				+ "rm <vfsname> <pathname>\t\t\tremove the element at the specified path on the VFS\n"
				+ "free <vfsname>\t\t\t\tdisplay the total size, used space and free space available on the VFS disk\n"
				+ "find <vfsname> <filename>\t\tfind elements in the VFS with the corresponding name, and displays their paths\n"
				+ "--------------------------Exporting and importing---------------------------\n"
				+ "impvfs <vfsname> <hostpath> <vfspath>\timport the elements located at the specified path on the host into the VFS, at the specified path\n"
				+ "expvfs <vfsname> <vfspath> <hostpath>\texport the elements located at the specified path on the VFS to the host, at the specified path");
	}
}
