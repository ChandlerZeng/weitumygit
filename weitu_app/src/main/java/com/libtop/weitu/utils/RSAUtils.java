package com.libtop.weitu.utils;


import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.InvalidAlgorithmParameterException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.RSAKeyGenParameterSpec;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.Cipher;


public class RSAUtils
{
    private static String ALGORITHM = "RSA";


    public static Map<String, BigInteger> generateKey()
    {
        Map<String, BigInteger> map = new HashMap<String, BigInteger>();
        RSAKeyGenParameterSpec spec = new RSAKeyGenParameterSpec(1024, RSAKeyGenParameterSpec.F4);
        SecureRandom random = new SecureRandom();
        KeyPairGenerator keygen;
        try
        {
            keygen = KeyPairGenerator.getInstance(ALGORITHM);
            keygen.initialize(spec, random);
        }
        catch (NoSuchAlgorithmException e)
        {
            throw new RuntimeException(e);
        }
        catch (InvalidAlgorithmParameterException e)
        {
            throw new RuntimeException(e);
        }
        KeyPair kp = keygen.generateKeyPair();
        RSAPublicKey publicKey = (RSAPublicKey) kp.getPublic();
        RSAPrivateKey privateKey = (RSAPrivateKey) kp.getPrivate();
        BigInteger publicModulus = publicKey.getModulus();
        BigInteger privateExponent = privateKey.getPrivateExponent();
        map.put("publicModulus", publicModulus);
        map.put("privateExponent", privateExponent);
        return map;
    }


    public static byte[] getBytes(String text)
    {
        try
        {
            return text.getBytes("UTF-8");
        }
        catch (UnsupportedEncodingException e)
        {
            return null;
        }
    }


    public static String getString(byte[] bytes)
    {
        try
        {
            return new String(bytes, "UTF-8");
        }
        catch (UnsupportedEncodingException e)
        {
            return null;
        }
    }


    public static byte[] encrypt(String text, String publicKey)
    {
        BigInteger modulus = new BigInteger(publicKey, 36);
        byte[] data = getBytes(text);
        return encrypt(data, modulus);
    }


    public static byte[] encrypt(byte[] data, String publicKey)
    {
        BigInteger modulus = new BigInteger(publicKey, 36);
        return encrypt(data, modulus);
    }


    public static byte[] encrypt(byte[] data, BigInteger modulus)
    {
        try
        {
            KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);
            RSAPublicKeySpec publicKeySpec = new RSAPublicKeySpec(modulus, RSAKeyGenParameterSpec.F4);
            Key key = keyFactory.generatePublic(publicKeySpec);
            return encrypt(data, key);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }
    }


    public static String decrypt(String text, String publicKey, String privateKey)
    {
        byte[] data = ArrayUtils.hexToByte(text);
        BigInteger modulus = new BigInteger(publicKey, 36);
        BigInteger privateExponent = new BigInteger(privateKey, 36);
        byte[] bytes = decrypt(data, modulus, privateExponent);
        return getString(bytes);
    }


    public static byte[] decrypt(byte[] data, String publicKey, String privateKey) throws Exception
    {
        BigInteger modulus = new BigInteger(publicKey, 36);
        BigInteger privateExponent = new BigInteger(privateKey, 36);
        return decrypt(data, modulus, privateExponent);
    }


    public static byte[] decrypt(byte[] data, BigInteger modulus, BigInteger privateExponent)
    {
        try
        {
            KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);
            RSAPrivateKeySpec privateKeySpec = new RSAPrivateKeySpec(modulus, privateExponent);
            Key key = keyFactory.generatePrivate(privateKeySpec);
            return decrypt(data, key);
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }


    private static byte[] encrypt(byte[] data, Key key) throws Exception
    {
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.ENCRYPT_MODE, key);

        byte[] dataReturn = null;
        for (int i = 0; i < data.length; i += 100)
        {
            byte[] doFinal = cipher.doFinal(ArrayUtils.subarray(data, i, i + 100));
            dataReturn = ArrayUtils.addAll(dataReturn, doFinal);
        }
        return dataReturn;
    }


    private static byte[] decrypt(byte[] data, Key key) throws Exception
    {
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.DECRYPT_MODE, key);

        byte[] dataReturn = null;
        for (int i = 0; i < data.length; i += 128)
        {
            byte[] doFinal = cipher.doFinal(ArrayUtils.subarray(data, i, i + 128));
            dataReturn = ArrayUtils.addAll(dataReturn, doFinal);
        }
        return dataReturn;
    }

}
