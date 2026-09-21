import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
//CRYPTOGRAPHY IMPORTS
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.security.Key;

public class SecureFile {
    
    // AES requires a 16-byte (128-bit) key.
    private static final String SECRET_KEY = "MySuperSecretKey"; 

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

        try {
            byte[] fileData = Files.readAllBytes(Paths.get(fileName));
            
            //  Convert our 16-character string into a raw AES cryptographic key
            Key aesKey = new SecretKeySpec(SECRET_KEY.getBytes(), "AES");
            
            // Initialize the AES Cipher (The engine scrambling)
            Cipher cipher = Cipher.getInstance("AES");

            if (mode.equals("encrypt")) {
                //Turn the engine on in ENCRYPT mode
                cipher.init(Cipher.ENCRYPT_MODE, aesKey);
                
                // Scramble the bytes
                byte[] encryptedData = cipher.doFinal(fileData);
                
                String outFileName = fileName + ".enc";
                Files.write(Paths.get(outFileName), encryptedData);
                System.out.println("[+] SUCCESS: File encrypted and saved as " + outFileName);
                
            } else if (mode.equals("decrypt")) {
                //  Turn the engine on in DECRYPT mode
                cipher.init(Cipher.DECRYPT_MODE, aesKey);
                
                //  Un-scramble the bytes!
                byte[] decryptedData = cipher.doFinal(fileData);
                
                String outFileName = fileName.replace(".enc", ".dec");
                Files.write(Paths.get(outFileName), decryptedData);
                System.out.println("[-] SUCCESS: File decrypted and saved as " + outFileName);
                
            } else {
                System.out.println("Error: Unknown command '" + mode + "'. Use 'encrypt' or 'decrypt'.");
            }

        } catch (Exception e) {
            //  catches both File AND Cryptography errors
            System.out.println("Fatal Error: Could not process file - " + e.getMessage());
        }
    }
}