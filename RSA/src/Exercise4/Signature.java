package Exercise4;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;


import javax.crypto.Cipher;
import java.io.File;
import java.io.FileOutputStream;
import java.math.BigInteger;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;
import java.util.Date;

public class Signature {
    public static void main(String[] args) throws Exception {
        String clear = "SecretKey is just an interface that requires provider-specific implementation. SecretKeySpec is a concrete class that allows for easy construction of SecretKey from existing key material. So, in order to get SecretKey, you need to use either appropriate factory class or SecretKeySpec, as a shortcut.";
        String clearFake = "Hi, LOL joke";
        KeyPair keyPair = genRSAKeyPair();
        X509Certificate x509Certificate = generateCertificate(keyPair, "SHA256WithRSA", "manhMin",365);
        File fileX509Cer = createFile(new File("E:/X509 Certificate"));
        //save file x509Certificate
        FileOutputStream fileOutputStream = new FileOutputStream(fileX509Cer);
        fileOutputStream.write(x509Certificate.getEncoded());
        fileOutputStream.close();
        String signature = genDigitalSignature(clear, keyPair,"SHA-256");
        System.out.println("Signature : "+signature);
        System.out.println("=================================================================================");
        String authenticate = decryptAndAuthenticate(signature, x509Certificate, clear, "SHA-256");
        System.out.println(authenticate);

    }



    public static KeyPair genRSAKeyPair() throws Exception{
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(2048);
        KeyPair keyPair = keyPairGenerator.generateKeyPair();
        PublicKey publicKey = keyPair.getPublic();
        PrivateKey privateKey = keyPair.getPrivate();
        File priKeyFile = createFile(new File("E:/privateKey.rsa"));
        File pubKeyFile = createFile(new File("E:/publicKey.rsa"));

        //Save file public key
        FileOutputStream fileOutputStreamPub = new FileOutputStream(pubKeyFile);
        fileOutputStreamPub.write(publicKey.getEncoded());
        fileOutputStreamPub.close();

        //Save file private key
        FileOutputStream fileOutputStreamPri = new FileOutputStream(priKeyFile);
        fileOutputStreamPri.write(privateKey.getEncoded());
        fileOutputStreamPri.close();

        System.out.println("Generate Success!");
        return keyPair;
    }

    public static File createFile(File file) throws Exception{
        if(!file.exists()){
            file.createNewFile();
        }
        else {
            file.delete();
            file.createNewFile();
        }
        return file;
    }
    public static X509Certificate generateCertificate(final KeyPair keyPair,
                                                      final String hashAlgorithm,
                                                      final String cn,
                                                      final int days) throws OperatorCreationException, CertificateException {
        final Instant now = Instant.now();
        final Date notBefore = Date.from(now);
        final Date notAfter = Date.from(now.plus(Duration.ofDays(days)));

        final ContentSigner contentSigner = new JcaContentSignerBuilder(hashAlgorithm).build(keyPair.getPrivate());
        final X500Name x500Name = new X500Name("CN="+cn);
        final X509v3CertificateBuilder certificateBuilder =
                new JcaX509v3CertificateBuilder(x500Name,
                        BigInteger.valueOf(now.toEpochMilli()),
                        notBefore,
                        notAfter,
                        x500Name,
                        keyPair.getPublic());
        return new JcaX509CertificateConverter()
                .setProvider(new BouncyCastleProvider()).getCertificate(certificateBuilder.build(contentSigner));
    }

    public static String genDigitalSignature(String clear, KeyPair keyPair, String  hashAlgorithm) throws Exception{
        //read clear to byte array
        byte[] clearData = clear.getBytes();

        //hashing clear
        MessageDigest messageDigest = MessageDigest.getInstance(hashAlgorithm);
        messageDigest.update(clearData);
        byte[] clearByte = new byte[16];
        System.arraycopy(messageDigest.digest(), 0, clearByte, 0, clearByte.length);

        //use private key to encrypt hashed clear
        PrivateKey privateKey = keyPair.getPrivate();
        System.out.println(privateKey);
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, privateKey);
        byte[] clearEncrypted = cipher.doFinal(clearByte);

        String clearEncodedBase64 = Base64.getEncoder().encodeToString(clearEncrypted);
        return clearEncodedBase64;
    }

    public static String decryptAndAuthenticate(String signature, X509Certificate x509Certificate, String clear, String hashAlgorithm) throws Exception{
        //decode signature to byte array
        byte[] signatureByte = new byte[256];
        System.arraycopy(Base64.getDecoder().decode(signature), 0, signatureByte, 0, signatureByte.length);

        //get public key form certificate
        PublicKey publicKey = x509Certificate.getPublicKey();
        System.out.println(publicKey);

        //decrypt signature with public key
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, publicKey);
        byte[] signatureDecrypted = cipher.doFinal(signatureByte);
        String signatureDecryptedBase64 = Base64.getEncoder().encodeToString(signatureDecrypted);

        //hashing clear data
        MessageDigest messageDigest = MessageDigest.getInstance(hashAlgorithm);
        messageDigest.update(clear.getBytes());
        byte[] clearHashed = new byte[16];
        System.arraycopy(messageDigest.digest(), 0, clearHashed, 0 , clearHashed.length);
        String clearHashedBase64 = Base64.getEncoder().encodeToString(clearHashed);

        //compare hashed clear data with decrypted signature and return result
        if(!clearHashedBase64.equals(signatureDecryptedBase64)){
            return new String("Data have been changed, not trust!");
        }
        else return new String("Data is integrated, trust !");
    }


}
