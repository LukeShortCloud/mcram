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

    public static Process MinecraftServer = null;
	public static String java_binary = System.getProperty("java.home") +
									   "/bin/java";
	public static boolean serverStarted = false;

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

    public static void mountRAMMac(String mountRAM, String destinationDir) 
    {
		// convert from MB to block size;
		// more specifically, convert to kilobytes then bytes and finally
		// divide by bytes in 1 RAM sector (512) 
		// to get the total number of sectors
        int mountRAMSectors = Integer.parseInt(mountRAM) * 1024 * 1024 / 512;
        String mountCmd = ("hdiutil attach -nomount ram://" + 
        				   Integer.toString(mountRAMSectors));
        File runDir = new File("/tmp");
        execCmd(mountCmd, runDir);
    }

    public static void mountRAMLinux(String mountRAM, String destinationDir) 
    {
        String mountCmd = ("mount -t tmpfs -o defaults,noatime,size=" +
                          mountRAM + "M tmpfs " + destinationDir);
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
        PrintWriter stdin = new PrintWriter(MinecraftServer.getOutputStream());
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

	public static void startMinecraftServer(String runDir, String mcJar, String javaExecRAM)
	{
		// Example variables:
		// String runDirString = "/Users/Kylo/mc/";
		// String mcJar = "minecraft_server.1.9.jar";
		//
		// Minecraft needs to be run from the directory that the eula.txt is in
        // this is where the minecraft_server*.jar should exist
        File runDirFile = new File(runDir);
        // the command should end up looking similar to this:
        // java -Xmx1024M -Xms1024M -jar minecraft_server.jar nogui
        String runCmd = (java_binary + " -Xmx" + javaExecRAM + "M -Xms" + 
        				 javaExecRAM + "M -jar " + mcJar + " nogui");
        System.out.println("deubg - runCmd: " + runCmd);
		MinecraftServer = execCmd(runCmd, runDirFile);
		serverStarted = true;
	}

    // "synchronized" makes this thread safe
    // i.e., only one thread at a time can run this
    // if another thread calls this method, it will be queued
   /* public synchronized Process mcExec(String runDirString)
    {
		Process p = null;
        String runCmd = (java_binary +
                         " -jar " + runDirString +
                         " minecraft_server.1.9.jar --nogui");
        File runDirFile = new File("/Users/Kylo/mc/");
        MinecraftServer = this.execCmd(runCmd, runDirString);
        return MinecraftServer;
    } */

    public static void main(String[] args) 
    {
		// we will be creating two background threads
		//
		// 1st thread
		// start the Minecraft server and 
		// sync it back to the disk over a specified amount of time
        Runnable startAndSync = new Runnable() 
        {
            public void run() 
            {
                // initalize the object from our class
                Mcramd mcramd = new Mcramd();
                    
                // first we want to start the Minecraft server (after mounting RAM)
               // File runDir = new File("C:/path/to/minecraft/server/");
               //  String runCmd = (java_prefix + "/bin/java" + 
                //                 " -jar C:/path/to/minecraft/server/minecraft_server.1.9.jar");
               
                    String mountram = "512";
                    String destinationdir = "/ram";
                   // mcramd.mountRAMLinux(mountram, destinationdir);
                    
                    String sockContent = mcramd.readSock("/tmp/mcramd.sock");
                    String[] sockSplit = sockContent.split(",");
                    for (String value : sockSplit) {
                        System.out.println(value);
                    }
                    
                    // this will save the running Minecraft server
                    String command = "say hello"; 
                    // Minecraft needs to be run from the directory that the eula.txt is in
                    // this is where the minecraft_server*.jar should exist
                    //runDir = new File("C:/path/to/minecraft/server/");
                    String runDir = "/Users/Kylo/mc/";
                    String mcJar = "minecraft_server.1.9.2.jar";
                    String javaExecRAM = "512";
                    
                    mcramd.startMinecraftServer(runDir, mcJar, javaExecRAM);
                    
                    int count = 1;

                    while (count < 5) 
                    {
						 mcramd.stdin(MinecraftServer, command);

                        try {
                            TimeUnit.SECONDS.sleep(10);
                        } catch(InterruptedException e) {
                            e.printStackTrace();
                        }
                       
                        count++;
                        
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
                        
                        //mcramd.stdin(mc_server, command); 

                   // command = "stop"; // finally, stop the server after testing
                    //mcramd.stdin(mc_server, command);   
                             
            } 
		}; // end of the first Runnable thread method
		
	    // 2nd thread
		// listen to a socket for further commands
        Runnable mcramListen = new Runnable() 
                    {
                        public void run() 
                        {
							while (true) 
							{
								if (serverStarted != true)
								{
									try {
                            			TimeUnit.SECONDS.sleep(1);
                        			} catch(InterruptedException e) {
                            			e.printStackTrace();
                        			}
								} else {
									break;	
								}									
							}
							int count = 1;
							String command = "say world, thread 2"; 
							
							while (count < 5) 
                    		{
						 		stdin(MinecraftServer, command);

                        		try {
                            		TimeUnit.SECONDS.sleep(10);
                        		} catch(InterruptedException e) {
                            		e.printStackTrace();
                        		}
                        count++;
                        
                    }
                         
                        }
                    }; 
                    
        // spawn MCRAMD off as a background thread
        Thread MCRAMStart = new Thread(startAndSync);
        MCRAMStart.start();
        Thread mcramListenStart = new Thread(mcramListen);
        mcramListenStart.start();
        System.out.println("Hello world.");
	}
}
