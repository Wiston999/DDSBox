/*
 * Copyright (c) 2014. Olmo Jiménez Alaminos, Víctor Cabezas Lucena.
 *
 * This file is part of DDSBox.
 *
 * DDSBox is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * DDSBox is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with DDSBox.  If not, see <http://www.gnu.org/licenses/>.
 */

package es.ugr.ddsbox.utils;

import com.sun.org.apache.xml.internal.security.exceptions.Base64DecodingException;
import com.sun.org.apache.xml.internal.security.utils.Base64;
import es.ugr.ddsbox.models.User;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.spec.SecretKeySpec;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

public class SecurityUtils {
    public static String configDir;

    public static void generateKeysRSA(User user){
        try {
            final KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
            keyGen.initialize(1024);
            final KeyPair key = keyGen.generateKeyPair();

            user.setPublicKey(Base64.encode(key.getPublic().getEncoded()));
            user.setPrivateKey(Base64.encode(key.getPrivate().getEncoded()));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static byte[] encryptRSA(String keyS, byte[] data){

        PublicKey key = null;
        try {
            key = KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(Base64.decode(keyS)));
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (Base64DecodingException e) {
            e.printStackTrace();
        }

        byte[] cipherData = null;
        try {
            final Cipher cipher = Cipher.getInstance("RSA");

            cipher.init(Cipher.ENCRYPT_MODE, key);
            cipherData = cipher.doFinal(data);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return cipherData;
    }

    public static byte[] decryptRSA(String keyS, byte[] data){

        PrivateKey key = null;
        try {
            key = KeyFactory.getInstance("RSA").generatePrivate(new PKCS8EncodedKeySpec(Base64.decode(keyS)));
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (Base64DecodingException e) {
            e.printStackTrace();
        }

        byte[] dectyptedData = null;

        try {
            final Cipher cipher = Cipher.getInstance("RSA");

            cipher.init(Cipher.DECRYPT_MODE, key);
            dectyptedData = cipher.doFinal(data);

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return dectyptedData;
    }

    public static String generateKeyAES(){
        try{
            KeyGenerator generator = KeyGenerator.getInstance("AES");
            generator.init(128);

            Key key = generator.generateKey();

            return Base64.encode(key.getEncoded());
        }
        catch (Exception e){
            e.printStackTrace();
        }

        return null;
    }

    public static byte[] encryptAES(String keyS, byte[] data){

        Key key = null;
        try {
            key = new SecretKeySpec(Base64.decode(keyS), "AES");
        } catch (Base64DecodingException e) {
            e.printStackTrace();
        }

        byte[] dectyptedData = null;

        try {
            final Cipher cipher = Cipher.getInstance("AES");

            cipher.init(Cipher.ENCRYPT_MODE, key);
            dectyptedData = cipher.doFinal(data);

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return dectyptedData;
    }

    public static byte[] decryptAES(String keyS, byte[] data){

        Key key = null;
        try {
            key = new SecretKeySpec(Base64.decode(keyS), "AES");
        } catch (Base64DecodingException e) {
            e.printStackTrace();
        }

        byte[] dectyptedData = null;

        try {
            final Cipher cipher = Cipher.getInstance("AES");

            cipher.init(Cipher.DECRYPT_MODE, key);
            dectyptedData = cipher.doFinal(data);

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return dectyptedData;
    }

}
