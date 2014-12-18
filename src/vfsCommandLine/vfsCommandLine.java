package vfsCommandLine;
import java.util.Scanner;

import vfsCore.*;
public class vfsCommandLine {
	/**
	 * Keeps reference to the currently opened disk, via its Core instance.
	 */
	private Core core = null;
	
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
			//We then restore each argument, replacing the escaped characters by their originals
			for(int i = 1;i<arguments.length;i++){
				arguments[i] = arguments[i].replaceAll("\\\\\\s", " ");
			}
			//Checking the validity before using the result of the split
			if (arguments.length == 0){ continue clLoop;}
			
			try{
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
						ls(arguments);
						break;
					case "cd":
						cd(arguments);
						break;
					case "cp":
						cp(arguments);
						break;
					case "mv":
						mv(arguments);
						break;
					case "rm":
						rm(arguments);
						break;
					case "free":
						free(arguments);
						break;
					case "find":
						find(arguments);
						break;
					case "impvfs":
						impvfs(arguments);
						break;
					case "expvfs":
						expvfs(arguments);
						break;
					default:
						System.out.println("Command unknown");
						break;	
				}
			} catch (ExecutionErrorException e){
				//System.out.println("The VFS Core encountered an error.");
				e.printStackTrace();
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
		case 1:
			core.list(false);
			break;
		case 2:
			if (args[1].equalsIgnoreCase("-l")){
				core.list(true);
			} else {
				core.goTo(args[1]);
				core.list(false);
			}
			break;
		case 3:
			core.goTo(args[2]);
			core.list(true);
			break;
		default:
			throw new SyntaxException();
		}
	}

	private void cd(String[] args) throws ExecutionErrorException, SyntaxException, CoreNotInitalisedException{
		// TODO Auto-generated method stub
		
	}

	private void cp(String[] args) throws ExecutionErrorException, SyntaxException, CoreNotInitalisedException{
		// TODO Auto-generated method stub
		
	}
	
	private void mv(String[] args) throws ExecutionErrorException, SyntaxException, CoreNotInitalisedException{
		// TODO Auto-generated method stub
		
	}
	
	private void rm(String[] args) throws ExecutionErrorException, SyntaxException, CoreNotInitalisedException{
		// TODO Auto-generated method stub
		
	}
	
	private void free(String[] args) throws ExecutionErrorException, SyntaxException, CoreNotInitalisedException{
		// TODO Auto-generated method stub
		
	}
	
	private void find(String[] args) throws ExecutionErrorException, SyntaxException, CoreNotInitalisedException{
		
	}

	/**
	 * create a new vfs at the specified path, with the specified size (in kB)
	 * @param args the passed arguments
	 * @throws SyntaxException
	 * @throws ExecutionErrorException
	 */
	public void crvfs(String[] args) throws SyntaxException, ExecutionErrorException{
		if (args.length != 3){
			throw new SyntaxException();
		}
		core = new Core();
		if(core.createDisk(args[1], Long.valueOf(args[2]))){
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
		if (args.length != 2){
			throw new SyntaxException();
		}
		core = new Core();
		if(core.deleteDisk(args[1])){
			System.out.println("VFS "+core.getDiskpath()+" has been deleted.");
		}  else {
			throw new ExecutionErrorException();
		}
		core = null;
	}
	
	/**
	 * Open the vfs passed in the arguments
	 * @param args args the passed arguments
	 * @throws ExecutionErrorException
	 * @throws SyntaxException
	 */
	public void opvfs(String[] args) throws ExecutionErrorException, SyntaxException{
		if (args.length != 2){
			throw new SyntaxException();
		}
		core = new Core();
		if(core.openDisk(args[1])){
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
		if (args.length != 3){ throw new SyntaxException();}
		if(!core.importElement(args[1], args[2])){
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
		if (args.length != 3){ throw new SyntaxException();}
		if(!core.exportElement(args[1], args[2])){
			throw new ExecutionErrorException();
		}
	}
	
	/**
	 * display a useful list of commands, detailed with their arguments
	 */
	public void displayHelp(){
		System.out.println("———————————————Managing VFS disks———————————————\ncrvfs <vfspath> <dim>\t\tcreate a new VFS disk at the specified path and with size dim (in kB)\nopvfs <vfspath>\t\t\topen the existing VFS disk at the specified path\nrmvfs <vfspath>\t\t\tdelete the existing VFS disk at the specified path\n———————————————Using the VFS disk———————————————\nls <args> <pathname>\t\tlist the content of the folder at the specified path in the VFS, if args=‘-l’ displays the size of each element too\ncd <pathname>\t\t\tchange current directory on the VFS, ‘cd ..’ goes to the parent directory\nmv <oldpath> <newpath>\t\tmove the element at path oldpath to newpath (name included)\ncp <sourcepath> <targetpath>\tcopy the element at path sourcepath to targetpath (name included)\nrm <pathname>\t\t\tremove the element at the specified path on the VFS\nfree\t\t\t\tdisplay the total size, used space and free space available on the VFS disk\nfind <filename>\t\t\tfind elements in the VFS with the corresponding name, and displays their paths\n————————————Exporting and importing————————————-\nimpvfs <hostpath> <vfspath>\timport the elements located at the specified path on the host into the VFS, at the specified path\nexpvfs <vfspath> <hostpath>\texport the elements located at the specified path on the VFS to the host, at the specified path");
	}
}
