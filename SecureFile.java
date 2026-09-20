public class SecureFile {
    public static void main(String[] args) {
        
        if (args.length < 2) {
            System.out.println("Usage: java SecureFile <encrypt/decrypt> <filename>");
            return;
        }

        // We use toLowerCase() so it works even if the user types "ENCRYPT"
        String mode = args[0].toLowerCase();
        String fileName = args[1];

        //  Route the program based on the mode
        if (mode.equals("encrypt")) {
            System.out.println("[+] Ready to ENCRYPT the file: " + fileName);
            
        } else if (mode.equals("decrypt")) {
            System.out.println("[-] Ready to DECRYPT the file: " + fileName);
            
        } else {
            System.out.println("Error: Unknown command '" + mode + "'. Use 'encrypt' or 'decrypt'.");
        }
    }
}