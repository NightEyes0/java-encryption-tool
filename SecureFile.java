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

        Console console = System.console();
        String password;
        Scanner scanner = new Scanner(System.in); //  both fallback and our new prompt
        
        if (console != null) {
            password = new String(console.readPassword("[?] Enter secure password (keystrokes hidden): "));
        } else {
            System.out.print("[?] Enter secure password: ");
            password = scanner.nextLine();
        }

        try {
            byte[] fileData = Files.readAllBytes(Paths.get(fileName));
            
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashedKey = digest.digest(password.getBytes("UTF-8"));
            SecretKeySpec secretKey = new SecretKeySpec(hashedKey, "AES");

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");

            if (mode.equals("encrypt")) {
                
                byte[] iv = new byte[16];
                new SecureRandom().nextBytes(iv);
                IvParameterSpec ivSpec = new IvParameterSpec(iv);

                cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivSpec);
                byte[] encryptedData = cipher.doFinal(fileData);
                
                byte[] combined = new byte[iv.length + encryptedData.length];
                System.arraycopy(iv, 0, combined, 0, iv.length);
                System.arraycopy(encryptedData, 0, combined, iv.length, encryptedData.length);
                
                String outFileName = fileName + ".enc";
                Files.write(Paths.get(outFileName), combined);
                System.out.println("[+] SUCCESS: " + fileName + " secured with AES-256/CBC.");
                
                //  INTERACTIVE CLEANUP
                System.out.print("[?] Do you want to permanently delete the original unprotected file? (y/n): ");
                String deleteChoice = scanner.nextLine().trim().toLowerCase();
                
                if (deleteChoice.equals("y") || deleteChoice.equals("yes")) {
                    if (targetFile.delete()) {
                        System.out.println("[+] Original file wiped from disk.");
                    } else {
                        System.out.println("[!] Warning: Could not delete the original file (might be locked by another program).");
                    }
                } else {
                    System.out.println("[-] Original file preserved.");
                }
                
            } else if (mode.equals("decrypt")) {
                
                byte[] iv = new byte[16];
                System.arraycopy(fileData, 0, iv, 0, 16);
                IvParameterSpec ivSpec = new IvParameterSpec(iv);
                
                byte[] actualEncryptedData = new byte[fileData.length - 16];
                System.arraycopy(fileData, 16, actualEncryptedData, 0, actualEncryptedData.length);
                
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