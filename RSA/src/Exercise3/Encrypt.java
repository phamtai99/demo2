package Exercise3;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

public class Encrypt {
    public static void main(String[] args) {
        try{
            // đọc file chứa publickey
            FileInputStream fis=new FileInputStream("D:/publicKey.rsa");
            byte [] b=new byte[fis.available()];
            fis.read(b);
            fis.close();

            // tạo public key
            X509EncodedKeySpec spec =new X509EncodedKeySpec(b);
            KeyFactory factory=KeyFactory.getInstance("RSA");
            PublicKey publickey=factory.generatePublic(spec);

            // mã hóa dữ liệu
            String SecretKey="phamthetai100219";
            String plantext=" hello tai";
            SecretKeySpec sKeySpec= new SecretKeySpec(SecretKey.getBytes(),"AES");
            Cipher cipher =Cipher.getInstance("AES/ECB/PKCS5PADDING");
            cipher.init(Cipher.ENCRYPT_MODE,sKeySpec);
            byte[] byteEncrypted=cipher.doFinal(plantext.getBytes());
            String encrypted = Base64.getEncoder().encodeToString(byteEncrypted);
            System.out.println("Plantext :"+plantext);
            System.out.println("Encrypt plantext :"+encrypted);



            Cipher c=Cipher.getInstance("RSA");
            c.init(Cipher.ENCRYPT_MODE,publickey);
            byte[] encryptOut = c.doFinal(SecretKey.getBytes());
          //  System.out.println(encryptOut.length);
            String strEncrypt=Base64.getEncoder().encodeToString(encryptOut);
//            System.out.println("secret-key :"+sKeySpec);
//            System.out.println("Encrypt secret-key :"+ strEncrypt);



// giải mã
            FileInputStream fiss = new FileInputStream("D:/privateKey.rsa");
            byte[] bp=new byte[fiss.available()];
            fiss.read(bp);
            fiss.close();



// tao private key
            PKCS8EncodedKeySpec specs= new PKCS8EncodedKeySpec(bp);
            KeyFactory keyfactorys=KeyFactory.getInstance("RSA");
            PrivateKey privatekey=keyfactorys.generatePrivate(specs);



            c.init(Cipher.DECRYPT_MODE,privatekey);
            byte[] decoded = Base64.getDecoder().decode(strEncrypt.getBytes());
           // System.out.println(decoded.length);
            byte[] decryptOut=c.doFinal(decoded);
          //  System.out.println(decryptOut.length);
            String decrypt=new String(decryptOut);
         //   System.out.println("Decrypt secret-key1: "+decrypt);

            SecretKey originalKey = new SecretKeySpec(decryptOut,"AES");
            System.out.println("Decrypt secret-key: "+originalKey);

            cipher.init(Cipher.DECRYPT_MODE,sKeySpec);
            byte [] byteDecrypte=cipher.doFinal(byteEncrypted);
            String decrypted=new String(byteDecrypte);
            System.out.println("decrypt plantext: "+decrypted);


       } catch (IOException | InvalidKeySpecException | NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException e) {
            e.printStackTrace();
        }
    }
}

