import java.io.*;
import java.io.IOException; // I/O error exception handling
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption; // file copying
import java.util.Arrays;
import java.util.concurrent.TimeUnit; // TimeUnit sleep functions resides here
import java.util.Scanner;

class Mcramd 
{
    private static Process MinecraftServer = null;
    private static String java_binary = "" + System.getProperty("java.home") +
                                       "/bin/java";
    private static boolean serverStarted = false;
    private static boolean debugMode = false;
	private static String sourceDir = null;
	private static String destinationDir = null;

    public static void debug(String msg) 
    {
        if (debugMode == true) 
        {
            System.out.println("DEBUG: " + msg);
        }
    }

    public static String readSock(String fileName) 
    {
        File fileToRead = new File(fileName);
        FileReader fileReader = null;
        
        try 
        {
            fileReader = new FileReader(fileToRead);
        } catch (IOException e) {
            e.printStackTrace();
        }
            
        BufferedReader fileBufferReader = new BufferedReader(fileReader);
        String returnSocket = null; // initlalize the variable before
                                    // we enter the try/catch statement
    
        try 
        {
            // this will read the first/top line only
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
        String mountCmd = ("mount -t ramfs -o defaults,noatime,size=" +
                          mountRAM + "M ramfs " + destinationDir);
        File runDir = new File("/tmp");
        execCmd(mountCmd, runDir);
    }
    
    public static void mountRAMWindows(String mountRAM, String destinationDir) 
    {
        String imdiskExecutable = "C:/Windows/System32/imdisk.exe";
        
        if (! new File(imdiskExecutable).isFile()) 
        {
            System.out.println("ERROR. imdisk is not installed.");
            System.exit(1);
        }
        
        // automatic formatting example command:
        // runas /user:Administrator "C:/Windows/System32/imdisk.exe -a -s 512M -m M: -p \"/fs:ntfs /q /y\""
        // that, in itself, formats the partition with Windows' built-in 
        // "format" utility
        // due to the security risks presented by enabling the Administrator
		// and the complexity it adds to automatically formatting,
        // MCRAM will instead prompt the user to format the drive.
        // for reference, this explains how to enable the Administrator account:
        // https://www.petri.com/enable-the-windows-7-administrator-account

        // example mountCmd: C:/Windows/System32/imdisk.exe" -a -s 512M -m M: 
        String mountCmd = (imdiskExecutable + " -a -s " + mountRAM + "M -m " + 
                           destinationDir.substring(0, 2));
        // use the Windows temporary directory
        File runDir = new File("C:/Users/" + System.getProperty("user.name") +
                               "/AppData/Local/Temp"); 
        execCmd(mountCmd, runDir);
        System.out.println("PLEASE BE CAREFUL WHEN FORMATTING DRIVES "+ 
                           "AS YOU CAN LOSE ALL OF YOUR DATA IF YOU " +
                           "FORMAT THE WRONG DRIVE! MCRAM AND IT\'S " + 
                           "CREATOR ARE NOT RESPONSIBLE FOR ANY DATA LOSS");
        System.out.println("Please navigate to [ This PC (or My Computer) " + 
                           "> right-click on the drive letter " + 
                           destinationDir + " > Format... > Start ]");
        Scanner stdin = new Scanner(System.in);
        System.out.println("Please press ENTER when you are done " +
                           "formatting the RAM disk.");
        stdin.nextLine();
    }

    // return the contents of a directory
    public static File[] dir(File dirname) 
    {
        File[] listOfFiles = dirname.listFiles();
        return listOfFiles;
    }

    // copy files and folders recursively
    public static void cpDir(File src_dir, File dst_dir) throws IOException 
    {
        debug("cpDir - src_dir=" + src_dir);
        debug("cpDir - dst_dir=" + dst_dir);
        // initiate our variables outside of our loops so they are in the scope of this method
        Path src_path = src_dir.toPath();
        Path dst_path = dst_dir.toPath();    
        
         // if the source and destination are only files, then just copy and exit 
        if ((! src_dir.isDirectory() && ! dst_dir.isDirectory())) 
        {
            Files.copy(src_path, dst_path, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.COPY_ATTRIBUTES);
        } else if (src_dir.isDirectory())   
        {
            if (! Files.exists(dst_path) && (dst_path != dst_path.getRoot())) 
            {
                Files.copy(src_path, dst_path, StandardCopyOption.REPLACE_EXISTING);
            }
            
            File[] listOfFiles = dir(src_dir);
   
            for(File filename : listOfFiles) 
            {
                Path src_file = filename.toPath();
                Path dst_file = new File(dst_dir.getAbsolutePath() + "/" +  filename.getName()).toPath();

                if (filename.isDirectory()) 
                {
                    // copy the directory and it's attributes
                    if (! Files.exists(dst_file)) 
                    {
                        Files.copy(src_file, dst_file, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.COPY_ATTRIBUTES);
                    }
                    
                    File[] subDir = dir(filename);
                        
                    // copy the sub directories
                    for(File filetmp : subDir) 
                    {
                        // create the new source and destination file names
                        // for the sub directories
                        File src_subFile = new File(src_dir.getAbsolutePath() + "/" + filename.getName() +
                                                "/" + filetmp.getName());
                        File dst_subFile = new File(dst_dir.getAbsolutePath() + "/" + filename.getName() + 
                                                    "/" + filetmp.getName());
                        // copy the sub directory and it's contents (inception!)
                        cpDir(src_subFile, dst_subFile);
                    } 
                } else 
                {
                    Files.copy(src_file, dst_file, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.COPY_ATTRIBUTES);
                }       
            }
        }
    }
    
    // send standard input to a process
    public static void stdin(Process p, String command) 
    {
		debug("stdin - command=" + command);
        // a Scanner is used to push standard input to the process   
        Scanner stdin_scanner = new Scanner(System.in);
        PrintWriter stdin = new PrintWriter(MinecraftServer.getOutputStream());
        stdin.println(command); // the newline is used to signify the command ends
        stdin.flush(); // this writes the command to standard input to execute it
    }

    // run a native CLI command
    public static Process execCmd(String runCmd, File runDir) 
    {
		debug("execCmd - runCmd: " + runCmd);
        Process p = null; /* a variable must be defined outside of the 
                             try/catch scope and be assigned a value of at least null */
    
        try 
        {
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
        // Minecraft needs to be run from the directory that the eula.txt is in
        // this is where the minecraft_server*.jar should exist
        File runDirFile = new File(runDir);
        // the "runCmd" variable should end up looking similar to this:
        // java -Xmx1024M -Xms1024M -jar minecraft_server.jar nogui
        String runCmd = (java_binary + " -Xmx" + javaExecRAM + "M -Xms" + 
                         javaExecRAM + "M -jar " + mcJar + " nogui");
        debug("runCmd=" + runCmd);
        MinecraftServer = execCmd(runCmd, runDirFile);
        serverStarted = true;
    }
    
    public static String findSocket()
    {
                String foundSocket = null;
                String socketUnix = "/tmp/mcramd.sock"; 
                String socketWindows = "C:/Users/" + System.getProperty("user.name") +
                                       "/AppData/Local/Temp/mcramd.sock";
                String socketText = null;
                
                // convert our socket strings to a Path and then use the
                // Files class to see if it exists
                if (Files.exists(Paths.get(socketUnix))) 
                {
                    foundSocket = socketUnix;
                } else if (Files.exists(Paths.get(socketWindows))) 
                {
                    foundSocket = socketWindows;
                } else 
                {
                    System.out.println("No socket file found.");
                    System.exit(1);
                }
                return foundSocket;

	}
	
	public static void writeToFile(String fileName, String text) 
    {
        try 
        {
            PrintWriter editor = new PrintWriter(fileName, "UTF-8");
            editor.println(text);
            editor.close();
        } catch (IOException e) 
        {
            e.printStackTrace();
        }   
    }

    public static void main(String[] args) 
    {
        // we will be creating two background threads
        //
        // 1st thread
        // start the Minecraft server and 
        // sync it back to the disk over a specified amount of time
        Runnable startAndSync = new Runnable() {
            public void run() {
                // initalize the object from our class
                Mcramd mcramd = new Mcramd();
                    
				String socketFile = findSocket();
                String socketText = mcramd.readSock(socketFile);
                String[] sockSplit = socketText.split(",");

                // read the socket until it recieves the start command
                while (sockSplit[0].contains("mcramd:start") == false) 
                {
                    socketText = mcramd.readSock(socketFile);
                    sockSplit = socketText.split(",");
                    
                    try 
                    {
                        TimeUnit.SECONDS.sleep(1);
                    } catch(InterruptedException e) {
                        e.printStackTrace();
                    }                        
                }
                // full mcramd:start options:
                // (1) <mount_directory>, (2) <tmpfs_size_in_MB>, 
                // (3) <source_directory>, (4) <java_RAM_exec_size_in_MB>,
                // (5) <sync_time>, (6) <operating_system_name>, 
                // (7) debug mode (true or false) 
                String minecraftFullJarPath = sockSplit[3];
                destinationDir = sockSplit[1];
                String[] syncTime = sockSplit[5].split(":");
                debugMode = Boolean.valueOf(sockSplit[7].split(":")[1]);
                // extract the syncing time (in minutes) variable
                // and convert it to an integer
                int syncTimeInt = Integer.parseInt(syncTime[1]);
                
                if ( ! minecraftFullJarPath.endsWith(".jar")) 
                {
                    System.out.println("No jar file provided.");
                    System.exit(1); 
                }
                
                String[] minecraftFullJarPathSplit = minecraftFullJarPath.split("/");
                // grab the last part of the split which should be
                // the Minecraft server jar file
                String minecraftJar = minecraftFullJarPathSplit[minecraftFullJarPathSplit.length - 1];
                debug("minecraftJar: " + minecraftJar);
                
                // get the full path to the Minecraft server's directory;
                // it should be each value in the array besides the last one
                for (int count = 0; count < minecraftFullJarPathSplit.length - 1; count++) 
                {
                    if (count == 0) 
                    {
                        // the first entry should not start with
                        // a "/"
                        sourceDir = minecraftFullJarPathSplit[count];
                    } else 
                    {
                        sourceDir = sourceDir + "/" + minecraftFullJarPathSplit[count];
                    }
                }
                                    
                String mountRAM = sockSplit[2];
                String javaExecRAM = sockSplit[4];
                String OS = sockSplit[6];
                
                if (OS.contains("linux")) 
                {
                    mountRAMLinux(mountRAM, destinationDir);
                } else if (OS.contains("mac")) 
                {
                    mountRAMMac(mountRAM, destinationDir);
                } else if (OS.contains("windows")) 
                {
                    mountRAMWindows(mountRAM, destinationDir);
                } else 
                {
                    System.out.println("Unsupported operating system.");
                    System.exit(1);
                } 
                // copy the files into the mounted RAM disk
                try 
                {
                    cpDir(new File(sourceDir), new File(destinationDir));
                } catch(IOException e)
                {
                    e.printStackTrace();
                }
                // finally, we start the Minecraft server
                mcramd.startMinecraftServer(destinationDir, minecraftJar, javaExecRAM);
            
                // 0 means MCRAM will not sync the server back to the disk
                if (syncTimeInt != 0) 
                {
                    String command = null;
                    
                    while (true)
                    {
						// wait this long before saving again
                        try 
                        {
                            TimeUnit.MINUTES.sleep(syncTimeInt);
                        } catch(InterruptedException e) 
                        {
                            e.printStackTrace();
                        }
						
                        // save the server
                        stdin(MinecraftServer, "save all");
                        
                        try 
                        {
                            TimeUnit.SECONDS.sleep(1);
                        } catch(InterruptedException e) 
                        {
                            e.printStackTrace();
                        }
                        // stop the saving to avoid corruption
                        stdin(MinecraftServer, "save-off");
                        
                        try 
                        {
                            TimeUnit.SECONDS.sleep(1);
                        } catch(InterruptedException e) 
                        {
                            e.printStackTrace();
                        }
                        
                        // copy the server back from RAM to the disk
                        try 
                        {
                            cpDir(new File(destinationDir), new File(sourceDir));
                        } catch(IOException e)
                        {
                            e.printStackTrace();
                        }
                        // finally, turn saving back on again
                        stdin(MinecraftServer, "save-on");
                        stdin(MinecraftServer, "say MCRAM saved changes to disk.");
                    }
                }
            } 
        }; // end of the first Runnable thread method
        
        // 2nd thread
        // listen to a socket for further commands
        Runnable mcramListen = new Runnable() {
            public void run() {
                
                while (true) 
                {
                    if (serverStarted != true) 
                    {
                        try 
                        {
                            TimeUnit.SECONDS.sleep(1);
                        } catch(InterruptedException e) {
                            e.printStackTrace();
                        }
                    
                    } else 
                    {
                        System.out.println("The Minecraft server has started.");
                        break;  
                    }                                   
                }
                
				String socketFile = findSocket();
                // read the socket until it recieves the start command
                while (true) 
                {
					String socketText = readSock(socketFile);
					String[] sockSplit = socketText.split(",");
					String command = "";
                
					if (sockSplit[0].contains("mcramd:exec") == true)
					{
						command = sockSplit[1];
						stdin(MinecraftServer, command);
					} else if (sockSplit[0].contains("mcramd:stop") == true)
					{
						command = "stop";
						stdin(MinecraftServer, command);
						// wait for the server to stop and save
						try 
						{
							TimeUnit.SECONDS.sleep(1);
						} catch(InterruptedException e) 
						{
							e.printStackTrace();
						}
						// copy the server back from RAM to the disk
                        try 
                        {
                            cpDir(new File(destinationDir), new File(sourceDir));
                        } catch(IOException e)
                        {
                            e.printStackTrace();
                        }
						System.exit(0);
					}
					
					// we want to clear out the socketFile so the command
					// is not run more than once
					writeToFile(socketFile, "");
					
					try 
                    {
                        TimeUnit.SECONDS.sleep(1);
                    } catch(InterruptedException e) 
                    {
                        e.printStackTrace();
                    }
				}
                         
            }
        }; 
                    
        // spawn MCRAMD off as a background thread
        Thread MCRAMStart = new Thread(startAndSync);
        MCRAMStart.start();
        Thread mcramListenStart = new Thread(mcramListen);
        mcramListenStart.start();
    }
}
