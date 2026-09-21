import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.io.Console;
import java.util.Scanner;
import java.security.MessageDigest;
import java.security.SecureRandom;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import javax.crypto.spec.IvParameterSpec;

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

        // SECURE PASSWORD (Hides keystrokes in the terminal)
        Console console = System.console();
        String password;
        if (console != null) {
            password = new String(console.readPassword("[?] Enter secure password (keystrokes hidden): "));
        } else {
            //  just in case the terminal doesn't support hidden text
            Scanner scanner = new Scanner(System.in);
            System.out.print("[?] Enter secure password: ");
            password = scanner.nextLine();
        }

        try {
            byte[] fileData = Files.readAllBytes(Paths.get(fileName));
            
            //  SHA-256 KEY DERIVATION (Turns password into a 256-bit AES Key)
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashedKey = digest.digest(password.getBytes("UTF-8"));
            SecretKeySpec secretKey = new SecretKeySpec(hashedKey, "AES");

            // UPGRADED CIPHER (AES CBC Mode with PKCS5 Padding)
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");

            if (mode.equals("encrypt")) {
                
                //Generate a random Initialization Vector (IV)
                byte[] iv = new byte[16];
                new SecureRandom().nextBytes(iv);
                IvParameterSpec ivSpec = new IvParameterSpec(iv);

                // Encrypt
                cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivSpec);
                byte[] encryptedData = cipher.doFinal(fileData);
                
                // Combine the IV and the Encrypted Data into one file so we can decrypt it later
                byte[] combined = new byte[iv.length + encryptedData.length];
                System.arraycopy(iv, 0, combined, 0, iv.length);
                System.arraycopy(encryptedData, 0, combined, iv.length, encryptedData.length);
                
                String outFileName = fileName + ".enc";
                Files.write(Paths.get(outFileName), combined);
                System.out.println("[+] SUCCESS: " + fileName + " secured with AES-256/CBC.");
                
            } else if (mode.equals("decrypt")) {
                
                // Extract the IV from the first 16 bytes of the file
                byte[] iv = new byte[16];
                System.arraycopy(fileData, 0, iv, 0, 16);
                IvParameterSpec ivSpec = new IvParameterSpec(iv);
                
                // Extract the actual encrypted data (everything after the first 16 bytes)
                byte[] actualEncryptedData = new byte[fileData.length - 16];
                System.arraycopy(fileData, 16, actualEncryptedData, 0, actualEncryptedData.length);
                
                // Decrypt
                cipher.init(Cipher.DECRYPT_MODE, secretKey, ivSpec);
                byte[] decryptedData = cipher.doFinal(actualEncryptedData);
                
                String outFileName = fileName.replace(".enc", ".dec");
                Files.write(Paths.get(outFileName), decryptedData);
                System.out.println("[-] SUCCESS: " + fileName + " decrypted successfully.");
                
            } else {
                System.out.println("Error: Unknown command.");
            }

        } catch (javax.crypto.BadPaddingException e) {
            System.out.println("\n[!] CRITICAL ERROR: Incorrect password or corrupted file!");
        } catch (Exception e) {
            System.out.println("\n[!] Fatal Error: Could not process file - " + e.getMessage());
        }
    }
}