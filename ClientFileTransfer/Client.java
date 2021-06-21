package basepackage;

import java.io.*;
import java.net.Socket;
import java.awt.event.*;
import javax.swing.*;

public class Client extends JFrame {

    private DataInputStream is;
    private DataOutputStream os;
    private BufferedInputStream bis;
    private BufferedOutputStream bos;
    private Socket socket;
    private File file;
    private JPanel panel;
    private JTextField textbox;
    private JButton openButton;
    private JButton sendButton;
    private JFileChooser fileChooser;

    public Client() {
        super("Client File Transfer");
        panel = new JPanel();

        sendButton = new JButton("Send File");
        sendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                try {
                    if (file != null) {
                        sendFile(file);
                        file = null;
                        textbox.setText(null);
                    }
                } catch (IOException e) {
                    JOptionPane.showMessageDialog(null, "Error: " + e);
                }
            }
        });

        openButton = new JButton("Open File");
        openButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                try {
                    openFile();
                } catch (IOException e) {
                    JOptionPane.showMessageDialog(null, "Error: " + e);
                }
            }
        });

        textbox = new JTextField(20);
        textbox.setEditable(false);

        panel.add(openButton);
        panel.add(textbox);
        panel.add(sendButton);

        add(panel);
        setSize(300,150);
        setVisible(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    public void connect() {

        try {
            socket = new Socket("localhost", 1991);
            receiveFile(socket);
            socket.close();

        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Server disconnected");
        }
    }

    public void receiveFile(Socket socket) throws IOException {

        int bytesRead;
        byte[] fileBytes = new byte[1024];
        is = new DataInputStream(socket.getInputStream());

        while (true) {
            String fileName = is.readUTF();
            bos = new BufferedOutputStream(new FileOutputStream(fileName));

            do {
                if ((bytesRead = is.read(fileBytes, 0, fileBytes.length)) > 0) {
                    bos.write(fileBytes, 0, bytesRead);
                    bos.flush();
                }
            } while (bytesRead == fileBytes.length);

            JOptionPane.showMessageDialog(null, "File received from Server");
        }
    }

    public void openFile() throws IOException {

        fileChooser = new JFileChooser();
        int action = fileChooser.showOpenDialog(this);

        if (action == JFileChooser.APPROVE_OPTION) {
            file = fileChooser.getSelectedFile();
            textbox.setText(file.getName());
        }
    }

    public void sendFile(File file) throws IOException {

        byte[] fileBytes = new byte[(int) file.length()]; // file can have a max length of 2,147,483,647 bytes

        bis = new BufferedInputStream(new FileInputStream(file));
        bis.read(fileBytes, 0, fileBytes.length);

        os = new DataOutputStream(socket.getOutputStream());
        os.writeUTF(file.getName());
        os.write(fileBytes, 0, fileBytes.length);
    }
}