package tictactoe;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;

public class PlayerSocket {
    private static Socket socket;
    private static DataInputStream dis;
    private static PrintStream ps;
    private static String ip = "127.0.0.1";
    private static int portNum = 5005; 
    

    public static void setSocket(Socket aSocket) {
        socket = aSocket;
    }
    
    public PlayerSocket(){
    
        try {
            socket = new Socket(ip, portNum);
            dis = new DataInputStream(socket.getInputStream());
            ps = new PrintStream(socket.getOutputStream());
            
        } catch (IOException iOException) {
            iOException.printStackTrace();  // TODO: NEEDS AN ALERT
        }
    
    }

    public static DataInputStream getDis() {
        return dis;
    }

    public static PrintStream getPs() {
        return ps;
    }

    public static String getIp() {
        return ip;
    }

    public static void setIp(String ip) {
        PlayerSocket.ip = ip;
    }

    public static int getPortNum() {
        return portNum;
    }

    public static void setPortNum(int portNum) {
        PlayerSocket.portNum = portNum;
    }
    
    public static Socket getSocket(){
        return socket;
    }
    
}