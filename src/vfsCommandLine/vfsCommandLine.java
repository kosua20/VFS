package vfsCommandLine;
import java.util.Scanner;

import vfsCore.*;
public class vfsCommandLine {
	private Core core = null;
	
	public static void main(String[] args){
		vfsCommandLine vfsCL = new vfsCommandLine();
		vfsCL.mainLoop();
	}
	
	public void mainLoop(){
		String input;
		Scanner reader = new Scanner(System.in);
		clLoop:while(true){
			
			//User input
			System.out.print("vfscl: ");
			input = reader.nextLine();
			
			//We are immediately treating the EXIT COMMAND
			if(input.equalsIgnoreCase("exit")){	break clLoop; }
			
			//Else, string treatment
			String[] arguments = input.split(" ");
			
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
				System.out.println("The VFS Core encountered an error.");
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
	
	private void ls(String[] args) throws ExecutionErrorException, SyntaxException, CoreNotInitalisedException{
		// TODO Auto-generated method stub
		
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
		// TODO Auto-generated method stub
		
	}

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

	private void impvfs(String[] args) throws ExecutionErrorException, SyntaxException, CoreNotInitalisedException{
		// TODO Auto-generated method stub
		
	}
	
	private void expvfs(String[] args) throws ExecutionErrorException, SyntaxException, CoreNotInitalisedException{
		// TODO Auto-generated method stub
		
	}
	
	public void displayHelp(){
		System.out.println("42");
	}
}
