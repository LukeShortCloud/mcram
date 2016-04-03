//package mcram;
import java.io.File; // file and folder handling
import java.io.IOException; // I/O error exception handling
import java.io.PrintWriter;
import java.util.Scanner; // stdin

class Mcramctl {
    
    public Mcramctl() 
    {
        //contrusctor 
    }

	// find the current operating system
	public static String findOS() 
	{	
		String osName = System.getProperty("os.name");
		String shortOSName = null;
		
		if (osName.toLowerCase().contains("mac")) {
			shortOSName = "mac";
		} else if (osName.toLowerCase().contains("linux")) {
			shortOSName = "linux";
		} else if (osName.toLowerCase().contains("windows")) {
			shortOSName = "windows";
		} else {
			System.out.println("Unsupported operating system. Exiting...");
			System.exit(1);
		}	
		return shortOSName;
	}

	public static void writeToFile(String fileName, String text) 
	{
		try {
			PrintWriter editor = new PrintWriter(fileName, "UTF-8");
			editor.println(text);
			editor.close();
		} catch (IOException e) {
			e.printStackTrace();
		}	
	}

    public static String[] interactiveMode() 
    {
        // get source directory
        System.out.println("What folder is your Minecraft server located in?");
        Scanner sourceScanFolder = new Scanner(System.in); // stdin
        String sourceFolder = sourceScanFolder.next(); // convert from Scanner to String
        // get destination directory
        System.out.println("What folder would you like use for mounting the RAM disk?");
        Scanner destinationScanFolder = new Scanner(System.in); 
        String destinationFolder = destinationScanFolder.next();
        // get size for RAM disk
        System.out.println("How much RAM (in MB) do you want to use for mounting Minecraft into RAM?");
        Scanner MBofMountScan = new Scanner(System.in);
        String MBofMount = MBofMountScan.next();
        // get the amount of RAM that Java should use for running the server
        System.out.println("How much RAM (in MB) do you want to use for running Minecraft?");
        Scanner MBofRunScan = new Scanner(System.in);
        String MBofRun = MBofRunScan.next();

        String[] interactiveAnswers = {sourceFolder, destinationFolder, MBofMount, MBofRun};    
        return interactiveAnswers;
    }

    public static void help() 
    {
        System.out.printf("mcramctl options\n" + 
                          "\t--help, -h\tdisplay these help options\n" +
                          "\t--source-dir, -sd\tprovide the Minecraft server directory to sync\n" +
                          "\t--destination-dir, -dd\tprovide an empty directory to mount a RAM disk onto\n" +
                          "\t--run-ram, -rr\tspecify (in MB) the amount of RAM to run the Minecraft server\n" +
                          "\t--mount-ram, -mr\tspecify (in MB) the amount of RAM to use for mounting the RAM disk\n" +
                          "\t--version, -v\tshow current MCRAM version\n");
                          // "--quiet, -q\tsurpress output\n");
    }

    public static void main(String args[]) 
    {
        Mcramctl mcramctl = new Mcramctl(); // create an object      
		// initiate variables that will be needed later
		String sourceDir = null;
		String destinationDir = null;
		String cmd = null;
		String runRAM = null;
		String mountRAM = null;
		String shortOSName = mcramctl.findOS();
		String[] startCheck = null;

        // only run the interactive mode if no commands are given
        if (args.length == 0) 
        {
            String interactiveAnswers[] = mcramctl.interactiveMode();
        } 
        else 
        {
            // sort through the command line arguments given
            for (int counter = 0; counter < args.length; counter++) 
            {
                switch (args[counter]) 
                {
					case "--destination-dir":
                    case "--dd":
						destinationDir = args[counter + 1];
						counter++;
						break;
					case "--execute":
                    case "-e":
						cmd = args[counter + 1];
						counter++;
						break;
                    case "--help":
                    case "-h":
                        mcramctl.help();
                        break;
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
                    case "--sd":
						sourceDir = args[counter + 1];
						counter++;
						break;
                    case "--verbose":  
                    case "-v":
                        System.out.println("MCRAM version: 1.0.0-dev");
                        break;
                    default:
                        break;
                }
            }
            
            String fileName = "/tmp/mcramd.sock";
			
			// "mcramd:stop" = TBD
			//
			// "mcramd:exec" = exec a command on the server
			// Example: mcramd:exec,say Hello Minecraft World!
			
			String text = null;
			
			if (cmd == null) 
			{
				// seperate our command string by commas "," for 
				// mcramd to process through
				text = ("mcramd:start" + "," + destinationDir +
					    "," + mountRAM + "," + sourceDir + "," +
					    runRAM + "," + shortOSName);
				// "mcramd:start" = start Minecraft server
				// full mcramd:start options:
				// (1) <mount_directory>, (2) <tmpfs_size_in_MB>, 
				// (3) <source_directory>, (4) <java_RAM_exec_size_in_MB>,
				// (5) <operating_system_name>
				// Example: 
				// mcramd:start,/tmpfs,512,/home/user/mc_server,1024,linux
            }
            else 
            {
				text = cmd;
			}
			
			if (text.contains(",null")) 
			{
				System.out.println("Required option(s) are missing " +
								   "from string:");
				System.out.println(text);
				System.exit(1);	
			}		
            mcramctl.writeToFile(fileName, text);
        }
	}
} 
