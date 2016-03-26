import java.io.*;
import java.util.Scanner;
import java.util.concurrent.TimeUnit; // TimeUnit sleep functions resides here
import java.io.IOException; // I/O error exception handling
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption; // file copying

class mcramd {


    // return the contents of a directory
    public static File[] dir(String dirname) {

        // initate a File-type variable that can be used to get the contents of a directory
        File directory = new File(dirname);

        File[] listOfFiles = directory.listFiles();

        for (File file: listOfFiles){
            System.out.println(file.getName());
        }

        return listOfFiles;

    }

    // copy files
    public static void cp(String dst_dir, File[] listOfFiles) throws IOException  {

        for(File filename : listOfFiles) {
            Path src_file = filename.toPath();
            Path dst_file = new File(dst_dir + filename.getName()).toPath();
            Files.copy(src_file, dst_file, StandardCopyOption.REPLACE_EXISTING);
        }

    }

    public static void stdin(Process p, String command) {
 
        // a Scanner is used to push standard input to the process   
        Scanner stdin_scanner = new Scanner(System.in);
        PrintWriter stdin = new PrintWriter(p.getOutputStream());
        stdin.println(command);
        stdin.flush();  
        
    }


    public static Process mcrun() {
    
        Process p = null; /* a variable must be defined outside of the 
                             try/catch scope and be assigned a value of at least null */
    
        try {
            String java_prefix = System.getProperty("java.home");
            // Minecraft needs to be run from the directory that the eula.txt is in
            // this is where the minecraft_server*.jar should exist
            File dir = new File("C:/path/to/minecraft/server/");
            // we are not using any environment variables
            String blankEnv[] = {""}; 
            p = Runtime.getRuntime().exec(java_prefix + "/bin/java" + " -jar C:/path/to/minecraft/server/minecraft_server.1.9.jar", blankEnv, dir);
        } catch(IOException e) {
            e.printStackTrace();
        }
        
        return p;
        
    }

    public static void main(String[] args) {

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

        // this will run a sample command on the Minecraft server 
        // to print out "hello" to all players
        String command = "say hello";
        mcramd mcramd_obj = new mcramd();
        Process mc_server = mcramd_obj.mcrun();
        
        int count = 1;

        while (count < 6) {

            try {
                TimeUnit.SECONDS.sleep(10);
            } catch(InterruptedException e) {
                e.printStackTrace();
            }
            
            mcramd_obj.stdin(mc_server, command); 
            count++;
        }
        
        command = "stop"; // finally, stop the server after testing
        mcramd_obj.stdin(mc_server, command);   
   }

}
