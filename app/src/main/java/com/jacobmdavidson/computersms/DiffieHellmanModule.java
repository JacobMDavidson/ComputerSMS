package com.jacobmdavidson.computersms;

import android.util.Base64;
import android.util.Log;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.MessageDigest;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.KeyAgreement;
import javax.crypto.spec.DHParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import java.security.SecureRandom;
import java.util.Arrays;

/**
 * Uses Diffie Hellman to generate shared AES key, then encrypts/decrypts strings
 * Created by jacobdavidson on 8/27/15.
 */
public class DiffieHellmanModule {

    private static final int AES_KEY_SIZE = 128;
    private KeyPairGenerator keyPairGenerator;
    private PublicKey publicKey;
    private PrivateKey privateKey;
    private SecretKey secretKey;
    private boolean isConnected = false;

    public DiffieHellmanModule() {
        // Init the KeyPairGenerator
        try {
            Log.i(Constants.DEBUGGING.LOG_TAG, "Generating key pair");


            DHParameterSpec dhParameterSpec = new DHParameterSpec(skip1024Modulus, skip1024Base);
            keyPairGenerator = KeyPairGenerator.getInstance("DH");
            keyPairGenerator.initialize(dhParameterSpec);
            Log.i(Constants.DEBUGGING.LOG_TAG, "Done - Generating key pair");
        } catch (NoSuchAlgorithmException e) {
            Log.i(Constants.DEBUGGING.LOG_TAG, "Invalid Algorithm");
        } catch (InvalidAlgorithmParameterException e) {

            Log.i(Constants.DEBUGGING.LOG_TAG, "Invalid Algorithm Parameter");
        }

        // Generate the Key Pairs
        KeyPair keyPair = keyPairGenerator.genKeyPair();

        Log.i(Constants.DEBUGGING.LOG_TAG, "Keypair Generator instantiated");

        publicKey = keyPair.getPublic();
        Log.i(Constants.DEBUGGING.LOG_TAG, "public key generated");

        privateKey = keyPair.getPrivate();

        Log.i(Constants.DEBUGGING.LOG_TAG, "private key generated");


    }

    public PublicKey getPublicKey() {
        return publicKey;
    }

    // Set the public key and generate the shared AES key
    public void generateSecretKey(PublicKey receivedPublicKey, boolean lastPhase) {

        try {
            KeyAgreement keyAgreement = KeyAgreement.getInstance("DH");
            keyAgreement.init(privateKey);
            keyAgreement.doPhase(receivedPublicKey, lastPhase);

            // Generates the shared secret
            byte[] secret = keyAgreement.generateSecret();

            // Generate an AES key
            MessageDigest sha256 = MessageDigest.getInstance("SHA-256");
            byte[] bkey = Arrays.copyOf(sha256.digest(secret), AES_KEY_SIZE / Byte.SIZE);

            secretKey = new SecretKeySpec(bkey, "AES");
            isConnected = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String encryptString( String plainText ) {

        // @ TODO should I really init Cipher each time??
        try {
            // Instantiate the cipher
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            byte[] cipherText = cipher.doFinal(plainText.getBytes());
            return new String(Base64.encode(cipherText, Base64.NO_WRAP));

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
            return null;
        } catch (InvalidKeyException e) {
            e.printStackTrace();
            return null;
        } catch (BadPaddingException e) {
            e.printStackTrace();
            return null;
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
            return null;
        }

    }

    public String decryptString( String cipherText ) {
        // @ TODO should I really init Cipher each time??
        try {
            // Instantiate the cipher
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, secretKey);

            byte[] cipherTextBytes = Base64.decode(cipherText, Base64.NO_WRAP);
            byte[] plainText = cipher.doFinal(cipherTextBytes);
            return new String(plainText, "utf-8");

        } catch (BadPaddingException e) {
            e.printStackTrace();
            return null;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
            return null;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
            return null;
        } catch (InvalidKeyException e) {
            e.printStackTrace();
            return null;
        }
    }


    public boolean isConnected() {
        return isConnected;
    }

    // The 1024 bit Diffie-Hellman modulus values used by SKIP
    private static final byte skip1024ModulusBytes[] = {
            (byte)0xF4, (byte)0x88, (byte)0xFD, (byte)0x58,
            (byte)0x4E, (byte)0x49, (byte)0xDB, (byte)0xCD,
            (byte)0x20, (byte)0xB4, (byte)0x9D, (byte)0xE4,
            (byte)0x91, (byte)0x07, (byte)0x36, (byte)0x6B,
            (byte)0x33, (byte)0x6C, (byte)0x38, (byte)0x0D,
            (byte)0x45, (byte)0x1D, (byte)0x0F, (byte)0x7C,
            (byte)0x88, (byte)0xB3, (byte)0x1C, (byte)0x7C,
            (byte)0x5B, (byte)0x2D, (byte)0x8E, (byte)0xF6,
            (byte)0xF3, (byte)0xC9, (byte)0x23, (byte)0xC0,
            (byte)0x43, (byte)0xF0, (byte)0xA5, (byte)0x5B,
            (byte)0x18, (byte)0x8D, (byte)0x8E, (byte)0xBB,
            (byte)0x55, (byte)0x8C, (byte)0xB8, (byte)0x5D,
            (byte)0x38, (byte)0xD3, (byte)0x34, (byte)0xFD,
            (byte)0x7C, (byte)0x17, (byte)0x57, (byte)0x43,
            (byte)0xA3, (byte)0x1D, (byte)0x18, (byte)0x6C,
            (byte)0xDE, (byte)0x33, (byte)0x21, (byte)0x2C,
            (byte)0xB5, (byte)0x2A, (byte)0xFF, (byte)0x3C,
            (byte)0xE1, (byte)0xB1, (byte)0x29, (byte)0x40,
            (byte)0x18, (byte)0x11, (byte)0x8D, (byte)0x7C,
            (byte)0x84, (byte)0xA7, (byte)0x0A, (byte)0x72,
            (byte)0xD6, (byte)0x86, (byte)0xC4, (byte)0x03,
            (byte)0x19, (byte)0xC8, (byte)0x07, (byte)0x29,
            (byte)0x7A, (byte)0xCA, (byte)0x95, (byte)0x0C,
            (byte)0xD9, (byte)0x96, (byte)0x9F, (byte)0xAB,
            (byte)0xD0, (byte)0x0A, (byte)0x50, (byte)0x9B,
            (byte)0x02, (byte)0x46, (byte)0xD3, (byte)0x08,
            (byte)0x3D, (byte)0x66, (byte)0xA4, (byte)0x5D,
            (byte)0x41, (byte)0x9F, (byte)0x9C, (byte)0x7C,
            (byte)0xBD, (byte)0x89, (byte)0x4B, (byte)0x22,
            (byte)0x19, (byte)0x26, (byte)0xBA, (byte)0xAB,
            (byte)0xA2, (byte)0x5E, (byte)0xC3, (byte)0x55,
            (byte)0xE9, (byte)0x2F, (byte)0x78, (byte)0xC7
    };

    // The SKIP 1024 bit modulus
    private static final BigInteger skip1024Modulus
            = new BigInteger(1, skip1024ModulusBytes);

    // The base used with the SKIP 1024 bit modulus
    private static final BigInteger skip1024Base = BigInteger.valueOf(2);

}
