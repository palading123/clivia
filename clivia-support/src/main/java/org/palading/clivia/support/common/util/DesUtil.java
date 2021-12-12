/*
 * Licensed to the Apache Software Foundation (ASF) under one or more contributor license agreements. See the NOTICE
 * file distributed with this work for additional information regarding copyright ownership. The ASF licenses this file
 * to You under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the
 * License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package org.palading.clivia.support.common.util;

import javax.crypto.*;
import javax.crypto.spec.DESKeySpec;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;

/**
 * @author palading_cr
 * @title DesEncryptUtil
 * @project clivia /7
 */
public class DesUtil {

    // Encryption algorithm name

    private static final String KEY_ALGORITHM = "DES";

    // Algorithm name / encryption mode / filling mode

    private static final String CIPHER_ALGORITHM = "DES/ECB/PKCS5Padding";

    /**
     * Encrypts the string according to the key
     *
     * @author palading_cr
     */
    public static String encrypt(String data, String secureKey) {
        try {
            DESKeySpec desKeySpec = new DESKeySpec(secureKey.getBytes());
            SecretKeyFactory secretKeyFactory = SecretKeyFactory.getInstance(KEY_ALGORITHM);
            SecretKey secretKey = secretKeyFactory.generateSecret(desKeySpec);
            Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            byte[] result = cipher.doFinal(data.getBytes());
            return Base64.getEncoder().encodeToString(result);
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (Exception e) {
        }
        return null;

    }

    /**
     * Decrypt the string according to the key
     *
     * @author palading_cr
     */
    public static String decrypt(String data, String secureKey) {
        byte[] bs = Base64.getDecoder().decode(data);
        try {
            DESKeySpec desKeySpec = new DESKeySpec(secureKey.getBytes());
            SecretKeyFactory secretKeyFactory = SecretKeyFactory.getInstance(KEY_ALGORITHM);
            SecretKey secretKey = secretKeyFactory.generateSecret(desKeySpec);
            Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            byte[] result = cipher.doFinal(bs);
            return new String(result);
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        }
        return null;

    }

    public static void main(String[] args) {
        String s = "{\"name\":\"小陈\",\"value\":\"25岁\",\"house\":{\"houseAddr\":\"武汉\",\"houseName\":\"光谷未来城\"}}";
        String psd = "asdasdas";
        System.out.println(encrypt(s, psd));
        System.out.println(decrypt(s, psd));
    }

}
