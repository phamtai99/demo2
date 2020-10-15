package com.company;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Scanner;

public class AES {
    public static void main(String[] args) throws NoSuchAlgorithmException,
            NoSuchPaddingException, InvalidKeyException,
            IllegalBlockSizeException, BadPaddingException {


        Scanner str= new Scanner(System.in);
        System.out.println(" Enter your SECRET-KEY:  ");
        String SecretKey=str.nextLine();
        System.out.println("Your SECRET-KEY "+ SecretKey);
        SecretKeySpec sKeySpec= new SecretKeySpec(SecretKey.getBytes(),"AES");
        System.out.println(" Enter your plantext : ");
        String plantext= str.nextLine();
        System.out.println(" Your plantext: "+ plantext);


//        Encrypted
        Cipher cipher=Cipher.getInstance("AES/ECB/PKCS5PADDING");
        cipher.init(Cipher.ENCRYPT_MODE,sKeySpec);
        byte[] byteEncrypted=cipher.doFinal(plantext.getBytes());
        String encrypted = Base64.getEncoder().encodeToString(byteEncrypted);

//        Decrypted
        cipher.init(Cipher.DECRYPT_MODE,sKeySpec);
        byte [] byteDecrypte=cipher.doFinal(byteEncrypted);
        String decrypted=new String(byteDecrypte);


        System.out.println(" Encrypted plantext :"+encrypted);
        System.out.println(" Decrypted plantext :"+decrypted);

    }
}
