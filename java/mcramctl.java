import java.util.Scanner; // stdin
import java.io.File; // file and folder handling

class mcramctl {
	
	public mcramctl() {
		//contrusctor 
	}


	public static void welcome() {
		System.out.println("Welcome to MCRAM version 0.5!");
		System.out.println("Please report any bugs or feature requests to: https://github.com/ekultails/mcram");
	}

	public static String[] getLocations() {
		System.out.println("What folder is your Minecraft server located in?");
		Scanner sourceScanFolder = new Scanner(System.in); // stdin
		String sourceFolder = sourceScanFolder.next(); // convert from Scanner to String

		System.out.println("What folder would you like use for temporarily mounting RAM?");
		Scanner destinationScanFolder = new Scanner(System.in); 
		String destinationFolder = destinationScanFolder.next();

		System.out.printf("%s to %s\n",sourceFolder, destinationFolder ); // verify variable #DELME
		String[] folderLocations = {sourceFolder, destinationFolder}; 
		return folderLocations; // add to string and include destinationFolder #FIXME
	}

	public static String[] getRAMInfo() {
		System.out.println("How much RAM (in MB) do you want to use for mounting Minecraft into RAM?");
		Scanner MBofMountScan = new Scanner(System.in);
		String MBofMount = MBofMountScan.next();

		System.out.println("How much RAM (in MB) do you want to use for running Minecraft?");
		Scanner MBofRunScan = new Scanner(System.in);
		String MBofRun = MBofRunScan.next();
		
		System.out.printf("Mount %sMB, run with %sMB", MBofMount, MBofRun); // #DELME
		String[] RAMInfo = {MBofMount, MBofRun};
		return RAMInfo; 

	}

	public static void main(String args[]) {
		mcramctl mcramObj = new mcramctl(); // create an object
		mcramObj.welcome();
		mcramObj.getLocations(); // run a test method #DELME
		mcramObj.getRAMInfo();
	}



}
