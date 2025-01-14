package tictactoe;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class PlayerSocket {
    private static PlayerSocket instance;
    private Socket socket;
    private DataInputStream dis;
    private DataOutputStream dos;
    private static final String IP = "127.0.0.1";
    private static final int PORT = 5005;

    private PlayerSocket() {
        try {
            socket = new Socket(IP, PORT);
            dis = new DataInputStream(socket.getInputStream());
            dos = new DataOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            System.err.println("Error connecting to server: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static PlayerSocket getInstance() {
        if (instance == null) {
            synchronized (PlayerSocket.class) {
                if (instance == null) {
                instance = new PlayerSocket();
            }
            }
        }
        return instance;
    }
    public synchronized Socket getSocket() {
        return socket;
    }
     public void initializeConnection() throws IOException {
        if (socket == null || socket.isClosed()) {
            
            socket = new Socket(IP, PORT);
            dis = new DataInputStream(socket.getInputStream());
            dos = new DataOutputStream(socket.getOutputStream());
        }
    }

    public synchronized DataInputStream getDataInputStream() throws IOException {
        initializeConnection();
        return dis;
    }

    public synchronized DataOutputStream getDataOutputStream() throws IOException {
        initializeConnection();
        return dos;
    }


    public void closeConnection() {
        try {
            if (dis != null) dis.close();
            if (dos != null) dos.close();
            if (socket != null) socket.close();
        } catch (IOException e) {
            System.err.println("Error closing connection: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    
}
