import java.io.File; // NEW: Tells Java we want to work with files

public class SecureFile {
    public static void main(String[] args) {
        
        if (args.length < 2) {
            System.out.println("Usage: java SecureFile <encrypt/decrypt> <filename>");
            return;
        }

        String mode = args[0].toLowerCase();
        String fileName = args[1];

        // Check if the file actually exists before doing anything
        File targetFile = new File(fileName);
        
        if (!targetFile.exists()) {
            System.out.println("Error: Cannot find the file '" + fileName + "'");
            return; // Stop the program right here
        }

        if (mode.equals("encrypt")) {
            System.out.println("[+] File found! Ready to ENCRYPT: " + fileName);
            
        } else if (mode.equals("decrypt")) {
            System.out.println("[-] File found! Ready to DECRYPT: " + fileName);
            
        } else {
            System.out.println("Error: Unknown command '" + mode + "'. Use 'encrypt' or 'decrypt'.");
        }
    }
}