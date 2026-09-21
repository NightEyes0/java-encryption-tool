import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.io.IOException;

public class SecureFile {
    public static void main(String[] args) {
        
        if (args.length < 2) {
            System.out.println("Usage: java SecureFile <encrypt/decrypt> <filename>");
            return;
        }

        String mode = args[0].toLowerCase();
        String fileName = args[1];

        File targetFile = new File(fileName);
        if (!targetFile.exists()) {
            System.out.println("Error: Cannot find the file '" + fileName + "'");
            return; 
        }

        //  Try-Catch Block for File I/O
        try {
            //  Read the file as raw bytes
            byte[] fileData = Files.readAllBytes(Paths.get(fileName));
            System.out.println("[*] Successfully read " + fileData.length + " bytes from " + fileName);

            if (mode.equals("encrypt")) {
                //  Create the new encrypted file name
                String outFileName = fileName + ".enc";
                
                //  Write the bytes to the new file (No encryption yet, just copying)
                Files.write(Paths.get(outFileName), fileData);
                System.out.println("[+] SUCCESS: File saved as " + outFileName);
                
            } else if (mode.equals("decrypt")) {
                //  Strip the .enc off the name for the decrypted file
                String outFileName = fileName.replace(".enc", ".dec");
                
                //  Write the bytes back to disk
                Files.write(Paths.get(outFileName), fileData);
                System.out.println("[-] SUCCESS: File saved as " + outFileName);
                
            } else {
                System.out.println("Error: Unknown command '" + mode + "'. Use 'encrypt' or 'decrypt'.");
            }

        } catch (IOException e) {
            // If anything goes wrong reading/writing, Java jumps down here instead of crashing
            System.out.println("Fatal Error: Could not process file - " + e.getMessage());
        }
    }
}