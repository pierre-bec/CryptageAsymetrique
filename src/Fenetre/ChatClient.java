package Fenetre;

import Client.Client;

import java.awt.*;
import java.awt.event.*;
import java.util.Arrays;
import javax.swing.*;


public class ChatClient extends JFrame implements ActionListener,
        WindowListener {

    private JButton btSend;
    private JTextArea textArea;
    private JTextField chat;
    private Client client;


    public ChatClient() {

        setTitle("Chat du Client.Client");
        setBounds(500, 500, 450, 250);

        JPanel panHaut = new JPanel();
        JPanel panBas = new JPanel();
        panHaut.setLayout(new BorderLayout());
        panBas.setLayout(new FlowLayout());

        Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());
        textArea = new JTextArea("",10,5);
        textArea.setEditable(false);
        textArea.setHighlighter(null);
        chat = new JTextField(30);
        btSend = new JButton("Envoyer");
        panBas.add(chat);
        panBas.add(btSend);
        panHaut.add(textArea);

        contentPane.add(panHaut, "North");
        contentPane.add(panBas, "South");
        btSend.addActionListener(this);
        addWindowListener(this);
        setVisible(true);

        try {
            client = new Client(this);
        } catch (Exception e) {
            System.out.println(e.toString());
        }


    }

    public void actionPerformed(ActionEvent e) {

        try {
            if (e.getSource() == btSend) {
                addMessage("Vous : "+chat.getText());
                client.send(chat.getText());
                client.waitResponse();
                chat.setText("");
            }

        } catch (Exception exeption) {
            System.out.println(Arrays.toString(exeption.getStackTrace()));
        }
    }

    public void addMessage(String line) {
        this.textArea.setText(textArea.getText()+"\n"+line);
    }

    public void windowClosing(WindowEvent arg0) {
        System.out.println("Au revoir");
        System.exit(0);
    }

    public void windowActivated(WindowEvent arg0) {
    }

    public void windowClosed(WindowEvent arg0) {
    }

    public void windowDeactivated(WindowEvent arg0) {
    }

    public void windowDeiconified(WindowEvent arg0) {
    }

    public void windowIconified(WindowEvent arg0) {
    }

    public void windowOpened(WindowEvent arg0) {
    }


    public static void main(String[] args) {
        new ChatClient();
    }

}
