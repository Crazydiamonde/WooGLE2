package com.WooGLEFX.File;

import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.BufferedBlockCipher;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.engines.AESEngine;
import org.bouncycastle.crypto.modes.CBCBlockCipher;
import org.bouncycastle.crypto.params.KeyParameter;

import java.io.*;

/**
 * Encrypt/decrypt .bin files in AES format (Windows/Linux).
 *
 * @author David Croft
 * @version $Revision: 186$
 */
public class AESBinFormat
{
    private static final byte[] KEY = {0x0D, 0x06, 0x07, 0x07, 0x0C, 0x01, 0x08, 0x05,
            0x06, 0x09, 0x09, 0x04, 0x06, 0x0D, 0x03, 0x0F,
            0x03, 0x06, 0x0E, 0x01, 0x0E, 0x02, 0x07, 0x0B};

    private static final byte EOF_MARKER = (byte) 0xFD;

    private AESBinFormat()
    {
    }

    public static byte[] decodeFile(File file) throws IOException
    {
        int fileSize = (int) file.length();

        try (InputStream is = new FileInputStream(file)) {
            byte[] inputBytes = new byte[fileSize];

            int offset = 0;
            int numRead;

            while (offset < fileSize && ((numRead = is.read(inputBytes, offset, fileSize - offset)) != -1)) {
                offset += numRead;
            }

            if (offset < fileSize) {
                throw new IOException("Short read of " + file + ", expected " + fileSize + " but got " + offset);
            }
            return decode(inputBytes);
        }
    }

    // Java Crypto API - can't use because user will have to install 192-bit policy file.
//    SecretKey key = new SecretKeySpec(KEY, "AES");
//    Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");//CBC
//    cipher.init(Cipher.DECRYPT_MODE, key);
//    byte[] decrypted = cipher.doFinal(bytes);

    private static byte[] decode(byte[] inputBytes) throws IOException
    {
        BufferedBlockCipher cipher = getCipher(false);

        byte[] outputBytes = new byte[cipher.getOutputSize(inputBytes.length)];

        int outputLen = cipher.processBytes(inputBytes, 0, inputBytes.length, outputBytes, 0);

        try {

            outputLen += cipher.doFinal(outputBytes, outputLen);
        }
        catch (InvalidCipherTextException e) {
            throw new IOException("Can't decrypt file: " + e.getLocalizedMessage());
        }

        for (int i = outputLen - 16; i < outputLen; ++i) {
            byte b = outputBytes[i];
            if (b == EOF_MARKER) {
                outputLen = i;
                break;
            }
        }

        byte[] finalBytes = new byte[outputLen];
        System.arraycopy(outputBytes, 0, finalBytes, 0, outputLen);
        return finalBytes;
    }

    public static void encodeFile(File file, byte[] input) throws IOException
    {
        byte[] bytes = encode(input);
        try (OutputStream os = new FileOutputStream(file)) {
            os.write(bytes);
        }
    }

    private static byte[] encode(byte[] inputBytes) throws IOException
    {
        /* If input was multiple of 16, NO padding. Example: res\levels\BulletinBoardSystem\BulletinBoardSystem.level.bin */
        /* Otherwise pad to next 16 byte boundary */

        int origSize = inputBytes.length;
        if (origSize % 16 != 0) {
            int padding = 16 - origSize % 16;

            int newSize = origSize + padding;

            byte[] newInputBytes = new byte[newSize];
            System.arraycopy(inputBytes, 0, newInputBytes, 0, origSize);
            inputBytes = newInputBytes;

            /* Write up to 4 0xFD bytes immediately after the original file. The remainder can stay as the 0x00 provided by Arrays.copyOf. */
            for (int i = origSize; i < origSize + 4 && i < newSize; ++i) {
                inputBytes[i] = EOF_MARKER;
            }
        }

        BufferedBlockCipher cipher = getCipher(true);

        byte[] outputBytes = new byte[cipher.getOutputSize(inputBytes.length)];

        int outputLen = cipher.processBytes(inputBytes, 0, inputBytes.length, outputBytes, 0);

        try {
            outputLen += cipher.doFinal(outputBytes, outputLen);
        }
        catch (InvalidCipherTextException e) {
            throw new IOException("Can't encrypt file: " + e.getLocalizedMessage());
        }

        byte[] finalBytes = new byte[outputLen];
        System.arraycopy(outputBytes, 0, finalBytes, 0, outputLen);
        return finalBytes;
    }

    private static BufferedBlockCipher getCipher(boolean forEncryption)
    {
        BlockCipher engine = new AESEngine();
        BufferedBlockCipher cipher = new BufferedBlockCipher(new CBCBlockCipher(engine));

        cipher.init(forEncryption, new KeyParameter(KEY));
        return cipher;
    }
}