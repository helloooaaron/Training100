package test.fb.com.security;

import android.os.Build;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.Arrays;
import java.util.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;

import test.fb.com.BuildConfig;

public class MyKeyStore {
    private static final String LOG_TAG = MyKeyStore.class.getSimpleName();
    private static final String ANDROID_KEYSTORE = "AndroidKeyStore";
    private static final String IV_SEPARATOR = "|";
    public static final String KEY_ALIAS_PER_USE = BuildConfig.APPLICATION_ID + ".secretKey_per_use";
    public static final String KEY_ALIAS_TIMEOUT = BuildConfig.APPLICATION_ID + ".secretKey_timeout";

    @RequiresApi(api = Build.VERSION_CODES.M)
    private static @Nullable SecretKey getOrGenerateKey(final String alias) {
        if (!KEY_ALIAS_PER_USE.equals(alias) && !KEY_ALIAS_TIMEOUT.equals(alias)) return null;
        try {
            KeyStore keyStore = KeyStore.getInstance(ANDROID_KEYSTORE);
            keyStore.load(null);
            SecretKey key = (SecretKey) keyStore.getKey(alias, null);
            if (key != null) {
                // Return existing secretKey
                Log.i(LOG_TAG, "Return existing key " + alias);
                return key;
            }
        } catch (KeyStoreException | CertificateException | IOException | NoSuchAlgorithmException | UnrecoverableKeyException e) {
            Log.e(LOG_TAG, "KeyStore exception.", e);
            return null;
        }

        // Generate new key for the first time
        Log.i(LOG_TAG, "Generating new key " + alias);
        KeyGenParameterSpec.Builder builder = new KeyGenParameterSpec.Builder(
                    alias,KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT)
                .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7)
                .setUserAuthenticationRequired(true); // Use of key require valid authentication
        if (KEY_ALIAS_TIMEOUT.equals(alias)) {
            // Set authentication valid duration for key with timeout
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                builder.setUserAuthenticationParameters(30, KeyProperties.AUTH_BIOMETRIC_STRONG | KeyProperties.AUTH_DEVICE_CREDENTIAL);
            } else {
                builder.setUserAuthenticationValidityDurationSeconds(30);
            }
        }
        try {
            KeyGenerator keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, ANDROID_KEYSTORE);
            keyGenerator.init(builder.build());
            return keyGenerator.generateKey();
        } catch (NoSuchAlgorithmException | NoSuchProviderException | InvalidAlgorithmParameterException e) {
            Log.e(LOG_TAG, "KeyGenerator exception.", e);
            return null;
        }
    }

    private static Cipher getCipher() throws NoSuchPaddingException, NoSuchAlgorithmException {
        // Transformation: "algo/mode/padding"
        return Cipher.getInstance(String.format("%s/%s/%s",
            KeyProperties.KEY_ALGORITHM_AES,
            KeyProperties.BLOCK_MODE_CBC,
            KeyProperties.ENCRYPTION_PADDING_PKCS7));
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static String encryptData(@NonNull String alias, @NonNull String plainText) throws BadPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException {
        // Init Cipher
        Cipher cipher = getCipher();
        cipher.init(Cipher.ENCRYPT_MODE, getOrGenerateKey(alias));
        // Perform encryption with cipher
        byte[] byteArray = cipher.doFinal(plainText.getBytes());
        // Store IV with encrypted data in the form of: "IV_base64|encrypted_data_base64"
        StringBuilder sb = new StringBuilder()
            .append(Base64.getEncoder().encodeToString(cipher.getIV()))
            .append(IV_SEPARATOR)
            .append(Base64.getEncoder().encodeToString(byteArray));
        return sb.toString();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static String decryptData(@NonNull String alias, @NonNull String encryptedText) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException, BadPaddingException, IllegalBlockSizeException {
        Log.i(LOG_TAG, encryptedText);
        String[] splits = encryptedText.split("\\|");
        if (splits.length != 2) return "Invalid encrypted data";
        /*
         * IV (Initialization Vector) is needed to decrypt the data
         * It's a fixed-size input to a crypto primitive. It's required to be random or pseudorandom.
         * The point of an IV is to tolerate the use of the same key to encrypt several distinct msgs.
         * It's required for block algorithm (CBC) by AndroidKeyStore provider.
         * IV can be stored in the public storage, along with encrypted data.
         */
        IvParameterSpec iv = new IvParameterSpec(Base64.getDecoder().decode(splits[0]));
        Cipher cipher = getCipher();
        cipher.init(Cipher.DECRYPT_MODE, getOrGenerateKey(alias), iv);
        byte[] byteArray = Base64.getDecoder().decode(splits[1]);
        return new String(cipher.doFinal(byteArray));
    }

    static interface CryptoOperation {
        public String apply(String s1, String s2) throws Exception;
    }
}
