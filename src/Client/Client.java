package Client;
// le client à compléter

import Fenetre.ChatClient;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;
import java.util.Base64;
import java.util.Scanner;

public class Client {

    private Cipher cipher;
    private final BufferedReader ins;
    private final PrintWriter outs;
    private Socket s;
    private Key key;
    private boolean boucle = true;
    private ChatClient fenetreClient;

    public Client(ChatClient fenetreClient) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPaddingException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        this.fenetreClient = fenetreClient;
        s = new Socket("localhost",6020);

        ins = new BufferedReader(
                new InputStreamReader(s.getInputStream()) );
        outs = new PrintWriter( new BufferedWriter(
                new OutputStreamWriter(s.getOutputStream())), true);


        KeyGenerator keyGen = KeyGenerator.getInstance("DES");
        keyGen.init(56); // 56 = valeur imposée
        key = keyGen.generateKey();

        String keyEncoded = ins.readLine();
        // decode the base64 encoded string
        byte[] decodedKey = Base64.getDecoder().decode(new String(keyEncoded));
// rebuild key using SecretKeySpec
        PublicKey publicKey = KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(decodedKey));
        // chiffre la clé DES et l'envoie au serveur
        cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        byte[] CleDEScode = cipher.doFinal(key.getEncoded());
        outs.write(Base64.getEncoder().encodeToString(CleDEScode)+"\n");
        outs.flush();
    }

    public void send(String str) throws IOException, NoSuchPaddingException, NoSuchAlgorithmException, BadPaddingException, IllegalBlockSizeException, InvalidKeyException {
        if (boucle) {
            System.out.print("Veuillez saisir votre message : ");
            cipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, key);
            byte[] code = cipher.doFinal(str.getBytes(StandardCharsets.UTF_8));
            outs.write(Base64.getEncoder().encodeToString(code)+"\n");
            outs.flush();
            if(str.equals("stop")) { boucle = false;}
        } else {
            ins.close();
            outs.close();
            s.close();
        }

    }

    public void waitResponse() throws IOException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException, NoSuchPaddingException, NoSuchAlgorithmException {
        if (boucle) {
        String content = ins.readLine();

        cipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, key);
        System.out.println(content);
        byte[] messageDecode = cipher.doFinal(Base64.getDecoder().decode(content));
        String messageDecodeStr = new String(messageDecode,  StandardCharsets.UTF_8);
            System.out.println("Recu : "+messageDecodeStr);
        fenetreClient.addMessage("Recu : "+messageDecodeStr);

        if(content.equals("stop")) { boucle = false;}
        } else {
            ins.close();
            outs.close();
            s.close();
        }
    }
}


