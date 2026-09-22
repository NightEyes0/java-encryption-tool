import java.io.File;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.io.Console;
import java.util.Scanner;
import java.security.MessageDigest;
import java.security.SecureRandom;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import javax.crypto.spec.IvParameterSpec;
import java.io.FileInputStream;  
import java.io.FileOutputStream; 
import javax.crypto.CipherOutputStream; 
import javax.crypto.CipherInputStream;  

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
        Scanner scanner = new Scanner(System.in); 
        
        if (console != null) {
            password = new String(console.readPassword("[?] Enter secure password (keystrokes hidden): "));
        } else {
            System.out.print("[?] Enter secure password: ");
            password = scanner.nextLine();
        }

       
        // START THE PERFORMANCE TIMER
        
        long startTime = System.currentTimeMillis();

        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashedKey = digest.digest(password.getBytes("UTF-8"));
            SecretKeySpec secretKey = new SecretKeySpec(hashedKey, "AES");

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");

            if (mode.equals("encrypt")) {
                
                byte[] iv = new byte[16];
                new SecureRandom().nextBytes(iv);
                IvParameterSpec ivSpec = new IvParameterSpec(iv);

                cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivSpec);
                String outFileName = fileName + ".enc";
                
                try (FileInputStream fis = new FileInputStream(targetFile);
                     FileOutputStream fos = new FileOutputStream(outFileName)) {
                    
                    fos.write(iv); 
                    
                    try (CipherOutputStream cos = new CipherOutputStream(fos, cipher)) {
                        byte[] buffer = new byte[64 * 1024]; 
                        int bytesRead;
                        
                        while ((bytesRead = fis.read(buffer)) != -1) {
                            cos.write(buffer, 0, bytesRead);
                        }
                    }
                }
                
                System.out.println("[+] SUCCESS: " + fileName + " secured with AES-256/CBC using 64KB chunks.");
                
                System.out.print("[?] Do you want to securely shred the original file? (y/n): ");
                String deleteChoice = scanner.nextLine().trim().toLowerCase();
                
                if (deleteChoice.equals("y") || deleteChoice.equals("yes")) {
                    try {
                        RandomAccessFile raf = new RandomAccessFile(targetFile, "rw");
                        byte[] garbage = new byte[(int) raf.length()];
                        new SecureRandom().nextBytes(garbage); 
                        raf.seek(0); 
                        raf.write(garbage); 
                        raf.close();
                        
                        if (targetFile.delete()) { 
                            System.out.println("[+] Original file SECURELY SHREDDED (unrecoverable) and wiped from disk.");
                        } else {
                            System.out.println("[!] Warning: Could not delete the file after shredding.");
                        }
                    } catch (Exception ex) {
                        System.out.println("[!] Warning: Secure shred failed - " + ex.getMessage());
                    }
                } else {
                    System.out.println("[-] Original file preserved.");
                }
                
            } else if (mode.equals("decrypt")) {
                
                String outFileName = fileName.replace(".enc", ".dec");
                
                try (FileInputStream fis = new FileInputStream(targetFile)) {
                    
                    byte[] iv = new byte[16];
                    fis.read(iv); 
                    IvParameterSpec ivSpec = new IvParameterSpec(iv);
                    
                    cipher.init(Cipher.DECRYPT_MODE, secretKey, ivSpec);
                    
                    try (CipherInputStream cis = new CipherInputStream(fis, cipher);
                         FileOutputStream fos = new FileOutputStream(outFileName)) {
                        
                        byte[] buffer = new byte[64 * 1024]; 
                        int bytesRead;
                        
                        while ((bytesRead = cis.read(buffer)) != -1) {
                            fos.write(buffer, 0, bytesRead);
                        }
                    }
                }
                
                System.out.println("[-] SUCCESS: " + fileName + " decrypted using 64KB chunks.");
                
            } else {
                System.out.println("Error: Unknown command.");
                return;
            }

            
            //STOP THE TIMER AND PRINT ELAPSED TIME
           
            long endTime = System.currentTimeMillis();
            System.out.println("[i] Operation completed in " + (endTime - startTime) + " milliseconds.");

        } catch (javax.crypto.BadPaddingException e) {
            System.out.println("\n[!] CRITICAL ERROR: Incorrect password or corrupted file!");
        } catch (Exception e) {
            System.out.println("\n[!] Fatal Error: Could not process file - " + e.getMessage());
        }
    }
}