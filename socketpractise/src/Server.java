import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.*;
import javax.swing.SwingWorker;

public class Server extends JFrame implements ActionListener {
    private JTextField portTextField;
    private JButton openButton;
    private JLabel statusLabel;
    private JLabel clientMessageLabel;

    private ServerSocket serverSocket;
    private Socket clientSocket;
    private DataInputStream clientInput;
    private DataOutputStream clientOutput;

    private String clientMessage = "";

    public Server() {
        setTitle("Server App");
        setSize(400, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridLayout(4, 2));

        JLabel portLabel = new JLabel("Port:");
        portTextField = new JTextField();
        openButton = new JButton("Open");
        openButton.addActionListener(this);

        // Set the preferred size for the button and center it
        openButton.setPreferredSize(new Dimension(100, 30));
        openButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        statusLabel = new JLabel("Status:");
        clientMessageLabel = new JLabel("Client Message:");

        add(portLabel);
        add(portTextField);
        add(openButton);
        add(new JPanel()); // Placeholder for button alignment
        add(statusLabel);
        add(new JPanel()); // Placeholder for status text
        add(clientMessageLabel);
        add(new JPanel()); // Placeholder for client message text
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == openButton) {
            int port = Integer.parseInt(portTextField.getText());
            openButton.setEnabled(false); // Disable the button during operation
            statusLabel.setText("Status: Opening Port...");

            SwingWorker<Void, String> worker = new SwingWorker<Void, String>() {
                @Override
                protected Void doInBackground() {
                    try {
                        serverSocket = new ServerSocket(port);
                        publish("Status: Port is open & ready");

                        clientSocket = serverSocket.accept();
                        publish("Status: Client Connected");
                        clientInput = new DataInputStream(clientSocket.getInputStream());
                        clientOutput = new DataOutputStream(clientSocket.getOutputStream());

                        while (true) {
                            String message = clientInput.readUTF();
                            clientMessage = "Client Message: " + message;
                            publish(clientMessage);
                        }
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    } finally {
                        try {
                            if (clientSocket != null) {
                                clientSocket.close();
                            }
                            if (serverSocket != null) {
                                serverSocket.close();
                            }
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }
                        openButton.setEnabled(true); // Re-enable the button when the operation is finished
                    }
                    return null;
                }

                @Override
                protected void process(java.util.List<String> chunks) {
                    for (String message : chunks) {
                        if (message.startsWith("Client Message:")) {
                            clientMessageLabel.setText(message);
                        } else {
                            statusLabel.setText(message);
                        }
                    }
                }
            };

            worker.execute();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Server server = new Server();
            server.setVisible(true);
        });
    }
}
