import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.*;

public class Client extends JFrame implements ActionListener {
    private JTextField serverPortTextField;
    private JButton connectButton;
    private JLabel statusLabel;
    private JTextField messageTextField;
    private JButton sendButton;

    private Socket clientSocket;
    private DataOutputStream clientOutput;

    private boolean isConnected = false;

    private int serverPort = 0; // Store the server port entered by the user

    public Client() {
        setTitle("Client App");
        setSize(300, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridLayout(5, 2));

        JLabel serverPortLabel = new JLabel("Server Port:");
        serverPortTextField = new JTextField();
        connectButton = new JButton("Connect");
        connectButton.addActionListener(this);

        // Set the preferred size for the button and center it
        connectButton.setPreferredSize(new Dimension(100, 30));
        connectButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        statusLabel = new JLabel("Status:");

        JLabel messageLabel = new JLabel("Message:");
        messageTextField = new JTextField();
        sendButton = new JButton("Send");
        sendButton.addActionListener(this);

        // Set the preferred size for the button and center it
        sendButton.setPreferredSize(new Dimension(100, 30));
        sendButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        add(serverPortLabel);
        add(serverPortTextField);
        add(connectButton);
        add(new JPanel()); // Placeholder for button alignment
        add(statusLabel);
        add(new JPanel()); // Placeholder for status text
        add(messageLabel);
        add(messageTextField);
        add(sendButton);
        add(new JPanel()); // Placeholder for button alignment
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == connectButton && !isConnected) {
            String serverAddress = "localhost";
            serverPort = Integer.parseInt(serverPortTextField.getText());

            if (serverPort != 0) {
                try {
                    clientSocket = new Socket(serverAddress, serverPort);
                    statusLabel.setText("Status: Connected");
                    clientOutput = new DataOutputStream(clientSocket.getOutputStream());
                    isConnected = true;
                } catch (ConnectException ex) {
                    // Handle the connection error and show "Port closed" message
                    statusLabel.setText("Status: This port is closed");
                    ex.printStackTrace();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            } else {
                statusLabel.setText("Status: This port is closed");
            }
        } else if (e.getSource() == sendButton && isConnected) {
            try {
                String message = messageTextField.getText();
                clientOutput.writeUTF(message);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Client client = new Client();
            client.setVisible(true);
        });
    }
}
