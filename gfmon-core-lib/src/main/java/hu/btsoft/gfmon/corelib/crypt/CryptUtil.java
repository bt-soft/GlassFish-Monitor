/*
 *  ------------------------------------------------------------------------------------
 *
 *  GF Monitor project
 *
 *  Module:  gfmon-core-lib (gfmon-core-lib)
 *  File:    CryptUtil.java
 *  Created: 2017.12.26. 15:48:24
 *
 *  ------------------------------------------------------------------------------------
 */
package hu.btsoft.gfmon.corelib.crypt;

import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Base64;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

/**
 * Kódolás/dekódolás beégetett 'sóval' :)
 * (https://stackoverflow.com/questions/23561104/how-to-encrypt-and-decrypt-string-with-my-passphrase-in-java-pc-not-mobile-plat alapján)
 *
 * @author BT
 */
@Slf4j
public class CryptUtil {

    //Kulcs
    private static final char[] SECRET_KEY = "_#@GF-Monitor-Password-Crypter@#_".toCharArray();

    // 8-byte só
    private static final byte[] SALT = {
        (byte) 0x03, (byte) 0x9B, (byte) 0x35, (byte) 0x32,
        (byte) 0x56, (byte) 0xC8, (byte) 0xE3, (byte) 0xA9
    };

    // Iteration count
    private static final int ITERATION_COUNT = 8;

    /**
     * Kódolás
     *
     * @param plainText kódolatlan szöveg
     *
     * @return kódolt szöveg, vagy null, ha hiba van
     */
    public static String encrypt(String plainText) {

        if (StringUtils.isAllEmpty(plainText)) {
            return null;
        }

        try {
            //Key generation for enc and desc
            KeySpec keySpec = new PBEKeySpec(SECRET_KEY, SALT, ITERATION_COUNT);
            SecretKey key = SecretKeyFactory.getInstance("PBEWithMD5AndDES").generateSecret(keySpec);

            // Prepare the parameter to the ciphers
            AlgorithmParameterSpec paramSpec = new PBEParameterSpec(SALT, ITERATION_COUNT);

            //Enc process
            Cipher ecipher = Cipher.getInstance(key.getAlgorithm());
            ecipher.init(Cipher.ENCRYPT_MODE, key, paramSpec);

            String encStr = new String(Base64.getEncoder().encode(ecipher.doFinal(plainText.getBytes("UTF-8"))));

            return encStr;

        } catch (InvalidKeySpecException | NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | InvalidAlgorithmParameterException | UnsupportedEncodingException | IllegalBlockSizeException | BadPaddingException e) {
            log.error("Kódolási hiba", e);
        }

        return null;
    }

    /**
     * Dekódolás
     *
     * @param encryptedText kódolt szöveg
     *
     * @return dekódolt szöveg, vagy null, ha hiba van
     */
    public static String decrypt(String encryptedText) {

        if (StringUtils.isAllEmpty(encryptedText)) {
            return null;
        }

        try {
            KeySpec keySpec = new PBEKeySpec(SECRET_KEY, SALT, ITERATION_COUNT);
            SecretKey key = SecretKeyFactory.getInstance("PBEWithMD5AndDES").generateSecret(keySpec);

            AlgorithmParameterSpec paramSpec = new PBEParameterSpec(SALT, ITERATION_COUNT);

            Cipher dcipher = Cipher.getInstance(key.getAlgorithm());
            dcipher.init(Cipher.DECRYPT_MODE, key, paramSpec);

            String plainStr = new String(dcipher.doFinal(Base64.getDecoder().decode(encryptedText)), "UTF-8");

            return plainStr;

        } catch (InvalidKeySpecException | NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | InvalidAlgorithmParameterException | IllegalBlockSizeException | BadPaddingException | UnsupportedEncodingException e) {
            log.error("Dekódolási hiba", e);
        }

        return null;
    }

}
