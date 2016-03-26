import java.util.Scanner; // stdin
import java.io.File; // file and folder handling

class mcramctl {
	
	public mcramctl() {
		//contrusctor 
	}

	public static String[] interactiveMode() {

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

    public static void help() {

        System.out.printf("mcramctl options\n" + 
                          "\t--help, -h\tdisplay these help options\n" +
                          "\t--source-dir, -sd\tprovide the Minecraft server directory to sync\n" +
                          "\t--destination-dir, -dd\tprovide an empty directory to mount a RAM disk onto\n" +
                          "\t--run-ram, -rr\tspecify (in MB) the amount of RAM to run the Minecraft server\n" +
                          "\t--mount-ram, -mr\tspecify (in MB) the amount of RAM to use for mounting the RAM disk\n" +
                          "\t--version, -v\tshow current MCRAM version\n");
                          // "--quiet, -q\tsurpress output\n");

    }

	public static void main(String args[]) {

		mcramctl mcramObj = new mcramctl(); // create an object

        // sort through the command line arguments given
        for (String arg : args) {

            switch (arg) {
                case "--help":
                case "-h":
                    mcramObj.help();
                    break;
                case "--verbose":  
                case "-v":
                    System.out.println("MCRAM version: 1.0.0-dev");
                    break;
                default:
                    break;
            }

        }

        // only run the interactive mode if no commands are given
		if (args.length == 0) {
            String interactiveAnswers[] = mcramObj.interactiveMode();
        }

	}



}
