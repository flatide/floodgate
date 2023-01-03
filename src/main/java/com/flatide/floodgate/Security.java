package com.flatide.floodgate;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.security.AlgorithmParameters;
import java.security.KeyStore;
import java.security.KeyStore.SecretKeyEntry;
import java.security.SecureRandom;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

public class Security {
    private SecretKey secretKey = null;
    private String key = null;
    private String keyPassword = null;
    
    private Integer hashCount = 0;

    private Security() {
    }

    public static Security getInstance() {
        return LazyHolder.instance;
    }

    private static class LazyHolder {
        private static final Security instance = new Security();
    }

    public Integer getHashCount() {
        return hashCount;
    }

    public String getKey() {
        return key;
    }

    public String encryptAES256(String msg) throws Exception {
        return encryptAES256(msg, key);
    }

    public String decryptAES256(String msg) throws Exception {
        return decryptAES256(msg, key);
    }

    private String byteToString(byte[] msg) {
        String result = "";
        for( byte ch : msg ) {
            result += String.format("%x", ch);
        }

        return result;
    }

    private String decryptAES256(String msg, String key) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");

        ByteBuffer buffer = ByteBuffer.wrap(Base64.getDecoder().decode(msg));
        byte[] saltBytes = new byte[20];
        buffer.get(saltBytes, 0, saltBytes.length);

        byte[] ivBytes = new byte[cipher.getBlockSize()];
        buffer.get(ivBytes, 0, ivBytes.length);

        byte[] encryptedTextBytes = new byte[buffer.capacity() - saltBytes.length - ivBytes.length];
        buffer.get(encryptedTextBytes);

        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        PBEKeySpec spec = new PBEKeySpec(key.toCharArray(), saltBytes, hashCount, 256);

        SecretKey secretKey = factory.generateSecret(spec);
        SecretKeySpec secret = new SecretKeySpec(secretKey.getEncoded(), "AES");

        cipher.init(Cipher.DECRYPT_MODE, secret, new IvParameterSpec(ivBytes));

        byte[] decryptedTextBytes = cipher.doFinal(encryptedTextBytes);

        return new String(decryptedTextBytes);
    }

    private String encryptAES256(String msg, String key) throws Exception {
        SecureRandom random = new SecureRandom();

        byte bytes[] = new byte[20];
        random.nextBytes(bytes);

        byte[] saltBytes = bytes;

        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");

        PBEKeySpec spec = new PBEKeySpec(key.toCharArray(), saltBytes, hashCount, 256);

        SecretKey secretKey = factory.generateSecret(spec);
        SecretKeySpec secret = new SecretKeySpec(secretKey.getEncoded(), "AES");

        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");

        cipher.init(Cipher.ENCRYPT_MODE, secret);

        AlgorithmParameters params = cipher.getParameters();

        byte[] ivBytes = params.getParameterSpec(IvParameterSpec.class).getIV();

        byte[] encryptedTextBytes = cipher.doFinal(msg.getBytes("UTF-8"));

        byte[] buffer = new byte[saltBytes.length + ivBytes.length + encryptedTextBytes.length];

        System.arraycopy(saltBytes, 0, buffer, 0, saltBytes.length);
        System.arraycopy(ivBytes, 0, buffer, saltBytes.length, ivBytes.length);
        System.arraycopy(encryptedTextBytes, 0, buffer, saltBytes.length + ivBytes.length, encryptedTextBytes.length);

        return Base64.getEncoder().encodeToString(buffer);
    }

    public void loadKeyFromFile(String path, String alias) throws Exception {
        this.secretKey = getKeyFromKeystore(new FileInputStream(path), alias, this.keyPassword);
        this.key = byteToString(secretKey.getEncoded());
    }

    public void loadKeyFromByteArray(byte[] key, String alias) throws Exception {
        this.secretKey = getKeyFromKeystore(new ByteArrayInputStream(key), alias, this.keyPassword);
        this.key = byteToString(secretKey.getEncoded());
    }

    public void loadKeyFromDB(String url, String user, String passwd, String keyAlias, String keyPassword) throws Exception {
        Connection conn = null;
        PreparedStatement preparedStatement = null;
        try {
            conn = DriverManager.getConnection(url, user, passwd);

            String tableSQL = "SELECT * FROM (SELECT * FROM API_CONFIGURE ORDER BY IMPORT_DATE DESC) WHERE rownum = 1";

            preparedStatement = conn.prepareStatement(tableSQL);
            ResultSet rs = preparedStatement.executeQuery();

            if( rs.next() ) {
                Blob data = rs.getBlob("SECURE_KEY");

                this.keyPassword = rs.getString("PASSWORD");
                this.hashCount = Integer.parseInt( rs.getString("HASH_COUNT") );

                this.secretKey = getKeyFromKeystore(data.getBinaryStream(), keyAlias, this.keyPassword);
                this.key = byteToString(secretKey.getEncoded());
            }
        } finally {
            if( preparedStatement != null ) {
                preparedStatement.close();
            }

            if( conn != null ) {
                conn.close();
            }
        }
    }

    public void loadPasswordFromDB(String url, String user, String passwd) throws Exception {
        Connection conn = null;
        PreparedStatement preparedStatement = null;
        try {
            conn = DriverManager.getConnection(url, user, passwd);

            String tableSQL = "SELECT * FROM (SELECT * FROM API_CONFIGURE ORDER BY IMPORT_DATE DESC) WHERE rownum = 1";

            preparedStatement = conn.prepareStatement(tableSQL);
            ResultSet rs = preparedStatement.executeQuery();

            if( rs.next() ) {
                this.keyPassword = rs.getString("PASSWORD");
                this.hashCount = Integer.parseInt( rs.getString("HASH_COUNT") );
            }

        } finally {
            if (preparedStatement != null ) {
                preparedStatement.close();
            }

            if (conn != null ) {
                conn.close();
            }
        }
    }

    public SecretKey getKeyFromKeystore(InputStream is, String alias, String password) throws Exception {
        KeyStore ks = KeyStore.getInstance("JKS");
        ks.load( is, password.toCharArray() );

        SecretKeyEntry entry = (KeyStore.SecretKeyEntry) ks.getEntry(alias, new KeyStore.PasswordProtection(password.toCharArray()) );
        return entry.getSecretKey();
    }
}
