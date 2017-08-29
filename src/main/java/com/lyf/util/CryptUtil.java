package com.lyf.util;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * @Package : com.lyf.util
 * @Class : CryptUtil
 * @Description : AES加解密工具类
 * @Author : liuya
 * @CreateDate : 2017-08-28 星期一 14:30:10
 * @Version : V1.0.0
 * @Copyright : 2017 liuya Inc. All rights reserved.
 */
public class CryptUtil {
    /**
     * @Method : initAESCipher
     * @Description : 初始化 AES Cipher
     * @param sKey :
     * @param cipherMode :
     * @return : javax.crypto.Cipher
     * @author : liuya
     * @CreateDate : 2017-08-28 星期一 14:30:35
     */
    public static Cipher initAESCipher(String sKey, int cipherMode) {
        //创建Key gen
        KeyGenerator keyGenerator = null;
        Cipher cipher = null;
        try {
            keyGenerator = KeyGenerator.getInstance("AES");
            keyGenerator.init(128, new SecureRandom(sKey.getBytes()));
            SecretKey secretKey = keyGenerator.generateKey();
            byte[] codeFormat = secretKey.getEncoded();
            SecretKeySpec key = new SecretKeySpec(codeFormat, "AES");
            cipher = Cipher.getInstance("AES");
            //初始化
            cipher.init(cipherMode, key);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (InvalidKeyException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        return cipher;
    }

    /**
     * @Method : encryptFile
     * @Description : 对文件进行AES加密
     * @param sourceFile :
     * @param fileType :
     * @param sKey :
     * @return : java.io.File
     * @author : liuya
     * @CreateDate : 2017-08-28 星期一 14:30:58
     */
    public static File encryptFile(File sourceFile, String fileType, String sKey) {
        //新建临时加密文件
        File encrypfile = null;
        InputStream inputStream = null;
        OutputStream outputStream = null;
        try {
            inputStream = new FileInputStream(sourceFile);
            File path = sourceFile.getParentFile();

            encrypfile = File.createTempFile(sourceFile.getName(), fileType, path);
            outputStream = new FileOutputStream(encrypfile);
            Cipher cipher = initAESCipher(sKey, Cipher.ENCRYPT_MODE);
            //以加密流写入文件
            CipherInputStream cipherInputStream = new CipherInputStream(inputStream, cipher);
            byte[] cache = new byte[1024];
            int nRead = 0;
            while ((nRead = cipherInputStream.read(cache)) != -1) {
                outputStream.write(cache, 0, nRead);
                outputStream.flush();
            }
            cipherInputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } finally {
            try {
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
            try {
                outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        }
        return encrypfile;
    }

    /**
     * @Method : decryptFile
     * @Description : AES方式解密文件
     * @param sourceFile :
     * @param fileType :
     * @param sKey :
     * @return : java.io.File
     * @author : liuya
     * @CreateDate : 2017-08-28 星期一 14:31:32
     */
    public static File decryptFile(File sourceFile, String fileType, String sKey) {
        File decryptFile = null;
        InputStream inputStream = null;
        OutputStream outputStream = null;
        try {
            File path = sourceFile.getParentFile();
            decryptFile = File.createTempFile(sourceFile.getName(), fileType, path);
            Cipher cipher = initAESCipher(sKey, Cipher.DECRYPT_MODE);
            inputStream = new FileInputStream(sourceFile);
            outputStream = new FileOutputStream(decryptFile);
            CipherOutputStream cipherOutputStream = new CipherOutputStream(outputStream, cipher);
            byte[] buffer = new byte[1024];
            int r;
            while ((r = inputStream.read(buffer)) >= 0) {
                cipherOutputStream.write(buffer, 0, r);
            }
            cipherOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } finally {
            try {
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
            try {
                outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        }
        return decryptFile;
    }

    /**
     * @Method : getProjectRootPath
     * @Description : 获取工程跟路径
     * @return : java.lang.String
     * @author : liuya
     * @CreateDate : 2017-08-28 星期一 14:32:42
     */
    public static String getProjectRootPath() {
        File file = new File("");
        String projectPath = "";
        try {
            projectPath = file.getCanonicalPath();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return projectPath;
    }

    /**
     * @Method : readPropertiesFile
     * @Description : 读取*.properties格式属性文件
     * @Param filePath :
     * @ReturnType : java.com.lyf.util.Map<java.lang.String,java.lang.Object>
     * @Author : liuyang
     * @CreateDate : 2017-08-19 星期六 19:17:52
     */
    public static Map<String, Object> readPropertiesFile(String filePath) {
        Map<String, Object> propertiesMap = new HashMap<String, Object>();
        InputStream is = null;
        Properties pro = new Properties();
        try {
            is = new FileInputStream(filePath);
            pro.load(is);
            Enumeration keys = pro.keys();
            while (keys.hasMoreElements()) {
                String key = (String) keys.nextElement();
                propertiesMap.put(key, pro.getProperty(key));
            }
        } catch (FileNotFoundException e) {
            System.out.println("文件不存在！");
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (is != null) {
                    is.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return propertiesMap;
    }
}

