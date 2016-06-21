import java.io.File; // file and folder handling
import java.io.IOException; // I/O error exception handling
import java.io.PrintWriter;
import java.util.Scanner; // stdin

class mcramctl 
{
    
    public static String socketText = null;
    // this is only turned on if "verbose" debugging mode is enabled
    public static boolean debugMode = false;

    public static void debug(String msg) 
    {
        if (debugMode == true) 
        {
            System.out.println("DEBUG: " + msg);
        }
    }
    
    
    public mcramctl() 
    {
        //contrusctor 
    }

    // find the current operating system
    public static String findOS() 
    {   
        String osName = System.getProperty("os.name");
        String shortOSName = null;
        
        if (osName.toLowerCase().contains("mac")) 
        {
            shortOSName = "mac";
        } else if (osName.toLowerCase().contains("linux")) 
        {
            shortOSName = "linux";
        } else if (osName.toLowerCase().contains("windows")) 
        {
            shortOSName = "windows";
        } else 
        {
            System.out.println("Unsupported operating system. Exiting...");
            System.exit(1);
        }   
        debug("os=" + shortOSName);
        return shortOSName;
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

    public static String[] interactiveMode() 
    {
        Scanner stdin = new Scanner(System.in); // standard input from the user
        // get source directory
        System.out.println("What folder is your Minecraft server located in?");
        String sourceFolder = stdin.next(); // convert from Scanner to String
        // get destination directory
        System.out.println("What folder would you like use for mounting the RAM disk?"); 
        String destinationFolder = stdin.next();
        // get size for RAM disk
        System.out.println("How much RAM (in MB) do you want to use for mounting Minecraft into RAM?");
        String MBofMount = stdin.next();
        // get the amount of RAM that Java should use for running the server
        System.out.println("How much RAM (in MB) do you want to use for running Minecraft?");
        String MBofRun = stdin.next();
        // sync time
        System.out.println("How long (in minutes) to wait before syncing back to the disk?");
        String syncTime = stdin.next();

        String[] interactiveAnswers = {sourceFolder, destinationFolder, 
                                       MBofMount, MBofRun, syncTime};    
        return interactiveAnswers;
    }

    public static void help() 
    {
        System.out.printf("mcramctl options\n" + 
                          "\t--help, -h\n" +
                              "\t\tdisplay these help options\n" +
                          "\t--source-dir, -s\n" + 
                              "\t\tprovide the full path to the Minecraft server\'s jar file\n" +
                              "\t\tWindows: folders should be seperated by \"/\" and not \"\\\"\n" + 
                          "\t--destination-dir, -d\n" + 
                              "\t\twhere to mount the RAM disk to\n" +
                              "\t\tUnix: provide an empty directory\n" +
                              "\t\t\tdefault: /mcram/\n" +
                              "\t\tWindows: provide an unused drive letter\n" +
                              "\t\t\tdefault: M:/\n" + 
                          "\t--run-ram, -rr\n" +
                              "\t\tspecify (in MB) the amount of RAM to run the Minecraft server\n" +
                              "\t\tdefault: 512\n" +
                          "\t--mount-ram, -mr\n" + 
                              "\t\tspecify (in MB) the amount of RAM to use for mounting the RAM disk\n" +
                              "\t\tdefault: 512\n" +
                          "\t--sync-time, -t\n" + 
                              "\t\tset how long to wait (in minutes) before copying the server back to the disk\n" +
                              "\t\tdefault: 60\n" +
                          "\t--execute, -e\n" +
						      "\t\tsend a command to the Minecraft server\n" +
                          "\t--verbose, -v\n" +
                              "\t\tdisplay debugging information\n" +
                          "\t--version, -V\n" +
                              "\t\tshow current MCRAM version\n");
    }

    public static void main(String args[]) 
    {
        mcramctl mcramctl = new mcramctl(); // create an object      
        // initiate variables that will be needed later
        String sourceDir = null;
        String destinationDir = null;
        boolean isExec = false;
        String cmd = "";
        String runRAM = "512";
        String mountRAM = "512";
        String syncTime = "60";
        String shortOSName = mcramctl.findOS();
        String socketFile = null;
        String[] startCheck = null;

        // only run the interactive mode if 
        // no command line arguments are given
        if (args.length == 0) 
        {
            String interactiveAnswers[] = mcramctl.interactiveMode();
            // seperate our command string by commas "," for 
            // mcramd to process through
            socketText = ("mcramd:start" + "," + interactiveAnswers[1] +
                    "," + interactiveAnswers[2] + "," + interactiveAnswers[0] + "," +
                    interactiveAnswers[3] + "," + "syncTime:" +
                    interactiveAnswers[4] + "," + shortOSName);
        } else 
        {
            // sort through the command line arguments
            for (int counter = 0; counter < args.length; counter++) 
            {
                switch (args[counter]) 
                {
                    case "--destination-dir":
                    case "-d":
                        destinationDir = args[counter + 1];
                        counter++;
                        break;
                    case "--execute":
                    case "-e":
						cmd = "/";
						for (int execCounter = counter + 1; execCounter < args.length; execCounter++)
						{
							cmd = cmd + args[execCounter] + " ";
						}
						isExec = true;
						counter = args.length;
                        break;
                    case "--help":
                    case "-h":
                        mcramctl.help();
                        System.exit(0);
                    case "--mount-ram":
                    case "-mr":
                        mountRAM = args[counter + 1];
                        counter++;
                        break;
                    case "--run-ram":
                    case "-rr":
                        runRAM = args[counter + 1];
                        counter++;
                        break;
                    case "--source-dir":
                    case "-s":
                        sourceDir = args[counter + 1];
                        counter++;
                        break;
                    case "--sync-time":
                    case "-t":
                        syncTime = args[counter + 1];
                        counter++;
                        break;
                    case "--verbose":
                    case "-v":
                        debugMode = true;
                        break;
                    case "--version":  
                    case "-V":
                        System.out.println("MCRAM version: 1.0.0-alpha");
                        System.exit(0);
                    default:
                        break;
                }
            }

            // default options
            if (destinationDir == null) 
            {
                if (shortOSName.equals("linux") || shortOSName.equals("mac")) 
                {
                    destinationDir = "/mcram/";
                } else if (shortOSName.equals("windows")) 
                {
                    destinationDir = "M:/";
                }
            }
            
            // "mcramd:start" = start the server
            // "mcramd:stop" = TBD
            // "mcramd:exec" = exec a command on the server
				// Example: mcramd:exec,say Hello Minecraft World!
            
            if (isExec == false) 
            {
                // seperate our command string by commas "," for 
                // mcramd to process through
                socketText = ("mcramd:start" + "," + destinationDir +
                        "," + mountRAM + "," + sourceDir + "," +
                        runRAM + "," + "syncTime:" +
                        syncTime + "," + shortOSName + ",debug:" + debugMode);
                // "mcramd:start" = start Minecraft server
                // full mcramd:start options:
                // (1) <mount_directory>, (2) <tmpfs_size_in_MB>, 
                // (3) <source_directory>, (4) <java_RAM_exec_size_in_MB>,
                // (5) <sync_time>, (6) <operating_system_name>
                // (7) debug mode (true or false) 
                // Example: 
                // mcramd:start,/tmpfs,512,/home/user/mc_server,1024,linux,debug:false
                // OR
                // mcramd:start,R:/,1024,C:/Users/Steve/server/,1024,windows,debug:true
            } else 
            {
                socketText = "mcramd:exec," + cmd;
            }
            
            debug("socketText=" + socketText);
            
            if (socketText.contains(",null")) 
            {
                System.out.println("Required option(s) are missing " +
                                   "from string:");
                System.out.println(socketText);
                System.exit(1); 
            }
        }
        
        
        if (shortOSName.equals("linux") || shortOSName.equals("mac")) 
        {
            socketFile = "/tmp/mcramd.sock";
        } else if (shortOSName.equals("windows")) 
        {
            socketFile = "C:/Users/" + System.getProperty("user.name") +
                     "/AppData/Local/Temp/mcramd.sock";
        } else 
        {
            System.out.println("Unsupported operating system.");
            System.exit(1);
        }
        
        debug("socketFile=" + socketFile);   
        mcramctl.writeToFile(socketFile, socketText);
        // if a command is being sent to a running server then
        // do not try to start a new Minecraft server as a background thread;
        // this will cause MCRAM to "lock-up"
        if (isExec == false)
        {
			Mcramd.main(args); // execute the MCRAM daemon class
		}
    }
} 
