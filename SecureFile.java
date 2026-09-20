public class SecureFile {
    public static void main(String[] args) {
        
        // 1. Check if the user typed at least 2 words after the program name
        if (args.length < 2) {
            System.out.println("Error: You need to give me a command and a filename.");
            System.out.println("Example: java SecureFile encrypt secret.txt");
            return; // This instantly stops the program
        }

        // 2. If they did type 2 words, grab them from the args array
        String mode = args[0];
        String fileName = args[1];

        // 3. Print them back out to prove Java captured them
        System.out.println("Mode captured: " + mode);
        System.out.println("File captured: " + fileName);
    }
}