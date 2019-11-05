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

    
    public byte[] recvMsg() {
        byte[] msg = new byte[100];
        
        try {
            DatagramPacket packet = new DatagramPacket(msg, msg.length);
            working_socket.receive(packet);
            msg = packet.getData();
            
            System.out.println("O usuário " + msg[0] + " mandou as coordenadas: \n");
            System.out.println("X recebido em valor byte: " + msg[1]);
            System.out.println("Y recebido em valor byte: " + msg[2] + "\n");
            
            if (players != null) {
                for (PlayerModel p : players) {
                    if(p.getUserNumber() == msg[0]){
                        p.setUser_x(msg[1]);
                        p.setUser_y(msg[2]);
                    }
                    if(players.isEmpty()){
                        players.add(new PlayerModel(msg[0],msg[1],msg[2]));
                    }
                }
            }
            
                    
        } catch (Exception err) {
            JOptionPane.showMessageDialog(null, err.getMessage(), null,
                    JOptionPane.PLAIN_MESSAGE, null);
        }
        return msg;
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
