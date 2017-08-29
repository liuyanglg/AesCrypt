package com.lyf.main;


import com.lyf.util.CryptUtil;

import java.io.File;
import java.util.Map;
import java.util.Scanner;

public class CryptMain {
    private static String configFile = "/config/config.properties";
    private static String filePath;
    private static String fileType;
    private static String key;

    public static void main(String[] args) {
        init();
    }

    public static void init() {
        String rootPath = CryptUtil.getProjectRootPath();
        configFile = rootPath + configFile;
        Map<String, Object> propertiesMap = CryptUtil.readPropertiesFile(configFile);
        if (propertiesMap != null) {
            fileType = (String) propertiesMap.get("fileType");
            key = (String) propertiesMap.get("key");
        }

        System.out.println("please input the filePath of the file which you want to encrypt or decrypt: ");
        Scanner sc = new Scanner(System.in);
        filePath = sc.nextLine();
        File file = new File(filePath);
        while (!file.exists()) {
            System.out.println("file not exit, please input again or input 'exit' to exit: ");
            filePath = sc.nextLine();
            if (filePath.trim().equals("exit")) {
                return;
            }
            file = new File(filePath);
        }
        System.out.println("input '0' to encrypt or input '1' to decrypt, or input other to exit: '");
        int x = sc.nextInt();
        switch (x) {
            case 0:
                CryptUtil.encryptFile(file, fileType, key);
                System.out.println("encrypt success");
                break;
            case 1:
                CryptUtil.decryptFile(file, fileType, key);
                System.out.println("decrypt success");
                break;
            default:
                System.out.println("exit");
                break;
        }
    }

}
