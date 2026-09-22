# Java AES-256 File Encryption Engine 

A command-line file encryption utility built in Java. This application uses the Advanced Encryption Standard (AES-256 in CBC mode) to securely encrypt and decrypt files of any size using byte-level stream processing.

## Enterprise Security Features
* **AES-256/CBC Cryptography:** Replaced ECB mode with Cipher Block Chaining (CBC) using randomly generated 16-byte(IVs) to prevent pattern recognition attacks.
* **SHA-256 Key Derivation:** User passwords are mathematically hashed via `MessageDigest` into secure 256-bit cryptographic keys.
* **Secure File Shredding:** After encryption, users are prompted to securely shred the original file. The tool uses `RandomAccessFile` to overwrite the original data on the hard drive with cryptographically secure random bytes before deletion, preventing forensic recovery.
* **Interactive Secure Prompts:** Implements `java.io.Console` to mask user keystrokes during password entry, preventing shoulder-surfing and terminal logging.

## Performance Engineering
* **No RAM Limitations (Streaming I/O):** Upgraded from `Files.readAllBytes()` to `CipherInputStream` and `CipherOutputStream`. The engine streams data through a 64KB buffer, allowing it to encrypt multi-gigabyte video files while consuming less than 2MB of RAM.
* **Execution Profiling:** Built-in timers track encryption/decryption speeds

## Usage
Compile the Java engine:
javac SecureFile.java


Encrypt a target file:
java SecureFile encrypt my_video.mp4
# Prompts for hidden password
# Streams the file through AES-256/CBC
# Prompts for secure forensic shredding of the original file


Decrypt a protected file:

Bash
java SecureFile decrypt my_video.mp4.enc
# Restores the original file only if the correct password is provided