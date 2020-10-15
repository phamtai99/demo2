package Exercise3;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.*;

public class genarateKey {
    public static void main(String[] args) {
        try{
            SecureRandom sr=new SecureRandom();
            KeyPairGenerator kpg= KeyPairGenerator.getInstance("RSA");
            kpg.initialize(2048,sr);

            // khởi tạo cặp khóa

            KeyPair kp=kpg.genKeyPair();
            PublicKey publickey=kp.getPublic();
            PrivateKey privateKey=kp.getPrivate();
            File publickeyFile =createKeyFile(new File("D:/publicKey.rsa"));
            File privateKeyFile= createKeyFile(new File("D:/privateKey.rsa"));

            // lưu PublicKey
            FileOutputStream fos=new FileOutputStream(publickeyFile);
            fos.write(publickey.getEncoded());
            fos.close();

            // lưu private key

            fos=new FileOutputStream(privateKeyFile);
            fos.write(privateKey.getEncoded());
            fos.close();


        } catch (NoSuchAlgorithmException | IOException e) {
            e.printStackTrace();
        }
    }

    private static File createKeyFile(File file) throws IOException{
        if(!file.exists()){
            file.createNewFile();
        }else {
           file.delete();
           file.createNewFile();

        }
        return file;
    }
}
