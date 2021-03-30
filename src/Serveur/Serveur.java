package Serveur;// serveur à compléter

import Fenetre.ChatServeur;
import com.sun.deploy.net.socket.UnixDomainSocket;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.util.Arrays;
import java.util.Base64;
import java.util.Scanner;

public class Serveur {

    private Cipher cipher;
    private BufferedReader ins;
    private PrintWriter outs;
    private final SecretKey originalKey;
    private Socket soc;
    private boolean boucle = true;
    private ChatServeur serveur;

    public Serveur(ChatServeur serveur) throws IOException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        this.serveur = serveur;
        ServerSocket s = new ServerSocket(6020);
        Socket soc = s.accept();

        ins = new BufferedReader(
                new InputStreamReader(soc.getInputStream()));
        outs = new PrintWriter(new BufferedWriter(
                new OutputStreamWriter(soc.getOutputStream())), true);

        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
        keyGen.initialize(1024); // 56 = valeur imposée
        KeyPair keypair = keyGen.genKeyPair();
        PrivateKey clePrivee = keypair.getPrivate();
        PublicKey clePublique = keypair.getPublic();
        outs.write(Base64.getEncoder().encodeToString(clePublique.getEncoded()) + "\n");
        outs.flush();
        // il récupère la clé DES chiffrée et la déchiffre avec la clé privée
        String DESkeyEncoded = ins.readLine();

        cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, clePrivee);
        byte[] decode = cipher.doFinal(Base64.getDecoder().decode(new String(DESkeyEncoded)));
        originalKey = new SecretKeySpec(decode, 0, decode.length, "DES");

    }

    public void waitResponse() throws IOException, NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        if (boucle) {
            String content = ins.readLine();
            cipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, originalKey);
            byte[] messageDecode = cipher.doFinal(Base64.getDecoder().decode(content));
            String messageDecodeStr = new String(messageDecode, StandardCharsets.UTF_8);
            System.out.println("Recu : " + messageDecodeStr);
            serveur.addMessage("Recu : " + messageDecodeStr);
            if (ins.equals("stop")) {
                boucle = false;
            }
        } else {
            ins.close();
            outs.close();
            soc.close();
        }

    }

    public void send(String msg) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException, IOException {
        if (boucle) {
            System.out.print("Veuillez saisir votre message : ");

            cipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, originalKey);
            byte[] code = cipher.doFinal(msg.getBytes(StandardCharsets.UTF_8));
            outs.write(Base64.getEncoder().encodeToString(code) + "\n");
            outs.flush();
            if (msg.equals("stop")) {
                boucle = false;
            }
        } else {

            ins.close();
            outs.close();
            soc.close();
        }
    }
}


