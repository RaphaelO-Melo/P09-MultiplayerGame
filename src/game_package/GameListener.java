/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game_package;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.ArrayList;
import java.util.Arrays;
import javax.swing.JOptionPane;

/**
 *
 * @author raphael.omelo
 */
public class GameListener extends Thread {
    private MulticastSocket working_socket;
    private InetAddress group;
    private ArrayList<PlayerModel> players;
    
    public GameListener(MulticastSocket working_socket, InetAddress group, ArrayList<PlayerModel> players) {
        this.working_socket = working_socket;
        this.group = group;
        this.players = players;
    }

    @Override
    public void run() {
        while (true) {
            recvMsg();
        }
    }

    
    public void recvMsg() {
        byte[] msg = new byte[1000];
        try {
            DatagramPacket packet = new DatagramPacket(msg, msg.length);
            working_socket.receive(packet);          
            String msgS = new String(packet.getData()).trim();
            String[] userInfo = msgS.split(",");
            
            System.out.println("------- THREAD CLIENTE -------" + "\n" + 
                               "Cliente: " + userInfo[0] + "\n" +
                               "X: " + userInfo[1] + "\n" +
                               "Y: " + userInfo[2] + "\n" + 
                               "------------------------------\n");
            
            Game.attEnemy(new PlayerModel(userInfo[0],userInfo[1],userInfo[2]));
                    
            
        } catch (Exception err) {
            JOptionPane.showMessageDialog(null, err.getMessage(), null,
                    JOptionPane.PLAIN_MESSAGE, null);
        }
    }
    
    void close() {
        try {
            working_socket.leaveGroup(group);
            working_socket.close();
        } catch (Exception err) {
            JOptionPane.showMessageDialog(null, err.getMessage(), null,
                    JOptionPane.PLAIN_MESSAGE, null);
        }
    }
}
