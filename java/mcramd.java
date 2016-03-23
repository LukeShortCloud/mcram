import java.io.IOException;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption; // file/folder copying

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


//    public static void copyFiles( File from, File to ) {
//        Files.copy(from.toPath(), to.toPath());
//    }

    public static void main(String[] args) {
        System.out.println("Hello world");

        String dirname = "/tmp/";
        File[] listOfFiles = dir(dirname);
        String dir2 = "/tmp/tmp2/"; //DELME
        try {
            cp(dir2, listOfFiles);
        } catch(IOException e){
            e.printStackTrace();
        }
    }

}
