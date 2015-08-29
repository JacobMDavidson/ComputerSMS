package com.jacobmdavidson.computersms;

import android.util.Base64;

import java.io.UnsupportedEncodingException;
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
import javax.crypto.spec.SecretKeySpec;

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
            keyPairGenerator = KeyPairGenerator.getInstance("DH");
            keyPairGenerator.initialize(1024);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        // Generate the Key Pairs
        KeyPair keyPair = keyPairGenerator.genKeyPair();

        publicKey = keyPair.getPublic();
        privateKey = keyPair.getPrivate();


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
            return new String(Base64.encode(cipherText, Base64.DEFAULT));

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

            byte[] cipherTextBytes = Base64.decode(cipherText, Base64.DEFAULT);
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


}
