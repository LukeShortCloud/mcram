//package mcram;
import java.io.*;
import java.io.IOException; // I/O error exception handling
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption; // file copying
import java.util.Arrays;
import java.util.concurrent.TimeUnit; // TimeUnit sleep functions resides here
import java.util.Scanner;

class Mcramd {

    public static Process p;
    
    
	public static String readSock(String fileName) {
	
		File fileToRead = new File(fileName);
		FileReader fileReader = null;
		
		try 
        {
			fileReader = new FileReader(fileToRead);
		} catch (IOException e) 
        {
			e.printStackTrace();
		}
			
		BufferedReader fileBufferReader = new BufferedReader(fileReader);
		String returnSocket = null; // initlalize the variable before
									// we enter the try/catch statement
	
		try {
			returnSocket = fileBufferReader.readLine();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return returnSocket;
		
	}

    public static void mountRAMMac() 
    {
        System.out.println("Stub");
    }

    public static void mountRAMLinux(String mountram, String destinationdir) 
    {
        String mountCmd = ("mount -t tmpfs -o defaults,noatime,size=" +
                          mountram + "M tmpfs " + destinationdir);
        System.out.println(mountCmd);
        File runDir = new File("/tmp");
        execCmd(mountCmd, runDir);
    }
    
    public static void mountRAMWindows() 
    {
        System.out.println("Stub");
        
    }

    // return the contents of a directory
    public static File[] dir(String dirname) 
    {
        // initate a File-type variable that can be used to get the contents of a directory
        File directory = new File(dirname);
        File[] listOfFiles = directory.listFiles();
        
        for (File file: listOfFiles)
        {
            System.out.println(file.getName());
        }
        return listOfFiles;
    }

    // copy files
    public static void cp(String dst_dir, File[] listOfFiles) throws IOException  
    {
        for(File filename : listOfFiles) 
        {
            Path src_file = filename.toPath();
            Path dst_file = new File(dst_dir + filename.getName()).toPath();
            //Files.copy(src_file, dst_file, StandardCopyOption.REPLACE_EXISTING);
            Files.copy(src_file, dst_file, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.COPY_ATTRIBUTES);
        }
    }
    
    // send standard input to a process
    public static void stdin(Process p, String command) 
    {
        // a Scanner is used to push standard input to the process   
        Scanner stdin_scanner = new Scanner(System.in);
        PrintWriter stdin = new PrintWriter(p.getOutputStream());
        stdin.println(command);
        stdin.flush();  
    }

    // run a native CLI command
    public static Process execCmd(String runCmd, File runDir) 
    {
        Process p = null; /* a variable must be defined outside of the 
                             try/catch scope and be assigned a value of at least null */
    
        try {
            // we are not using any special environment variables
            String defaultEnv[] = {""}; 
            p = Runtime.getRuntime().exec(runCmd, defaultEnv, runDir);
        } catch(IOException e) {
            e.printStackTrace();
        }
        return p;
    }

    public static void main(String[] args) 
    {
        Runnable r = new Runnable() 
        {
            public void run() 
            {
                // initalize the object from our class
                Mcramd mcramd = new Mcramd();
                    
                // first we want to start the Minecraft server (after mounting RAM)
                String java_prefix = System.getProperty("java.home");
                // Minecraft needs to be run from the directory that the eula.txt is in
                // this is where the minecraft_server*.jar should exist
                File runDir = new File("C:/path/to/minecraft/server/");
                String runCmd = (java_prefix + "/bin/java" + 
                                 " -jar C:/path/to/minecraft/server/minecraft_server.1.9.jar");
                Process mc_server = mcramd.execCmd(runCmd, runDir);

                    Runnable r2 = new Runnable() 
                    {
                        public void run() 
                        {
                            String command = "say Thread 2 works."; 
                            mcramd.stdin(mc_server, command);                          
                        }
                    };
                    
                    
                while (true) 
                {    
                    String mountram = "512";
                    String destinationdir = "/ram";
                    mcramd.mountRAMLinux(mountram, destinationdir);
                    
                    String sockContent = mcramd.readSock("/tmp/mcramd.sock");
                    String[] sockSplit = sockContent.split(",");
                    for (String value : sockSplit) {
                        System.out.println(value);
                    }
                    
                    try {
                        TimeUnit.SECONDS.sleep(10);
                    } catch(InterruptedException e) {
                        e.printStackTrace();
                    }
                    
                    /* example copy below:
                    String dirname = "/tmp/tmp1/";
                    File[] listOfFiles = dir(dirname);
                    String dir2 = "/tmp/tmp2/";
                    try {
                        cp(dir2, listOfFiles);
                    } catch(IOException e){
                        e.printStackTrace();
                    }
                    */

                    // this will save the running Minecraft server
                    String command = "save"; 
                    java_prefix = System.getProperty("java.home");
                    // Minecraft needs to be run from the directory that the eula.txt is in
                    // this is where the minecraft_server*.jar should exist
                    runDir = new File("C:/path/to/minecraft/server/");
                    runCmd = (java_prefix + "/bin/java" + 
                                     " -jar C:/path/to/minecraft/server/minecraft_server.1.9.jar");
                    
                    int count = 1;

                    while (count < 6) 
                    {
                        try {
                            TimeUnit.SECONDS.sleep(10);
                        } catch(InterruptedException e) {
                            e.printStackTrace();
                        }
                        mcramd.stdin(mc_server, command); 
                        count++;
                    }
                    command = "stop"; // finally, stop the server after testing
                    mcramd.stdin(mc_server, command);   
                    
                    Thread mcramdThread2 = new Thread(r2);
                    mcramdThread2.start();
                }

            }

        }; // end of Runnable method
        // spawn MCRAMD off as a background thread
        Thread mcramdThread = new Thread(r);
        mcramdThread.start();
    }
}
