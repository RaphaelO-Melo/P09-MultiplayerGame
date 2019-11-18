/*
 * Classe GameListener
 *
 * Classe onde é criada a Thread do jogador, esta mesma escuta o grupo esperando
 * outros integrantes mandarem uma mensagem para tratar a mesma
 */
package game_package;

import java.awt.Rectangle;
import java.io.IOException;
import java.net.InetAddress;
import java.net.DatagramPacket;
import javax.swing.JOptionPane;
import java.net.MulticastSocket;

/**
 * @author Raphael Melo
 */
public class GameListener extends Thread {
    private InetAddress group;
    private MulticastSocket working_socket;
 
    //Construtor da classe
    public GameListener(MulticastSocket working_socket, InetAddress group) {
        this.group = group;
        this.working_socket = working_socket;
    }

    @Override
    public void run() {
        while (true) {
            recvMsg();
        }
    }

    /**
     * Método que recebee trata as mensagens recebidas no grupo
    */
    public void recvMsg() {
        byte[] msg = new byte[1000];
        try {
            DatagramPacket packet = new DatagramPacket(msg, msg.length);
            working_socket.receive(packet);          
            String msgS = new String(packet.getData()).trim();
            //Separa a string através da vírgula
            String[] userInfo = msgS.split(",");
            System.out.println("------- THREAD CLIENTE -------" + "\n" + 
                               "Cliente: " + userInfo[0] + "\n" +
                               "X: " + userInfo[1] + "\n" +
                               "Y: " + userInfo[2] + "\n" + 
                               "------------------------------\n");
                      
            //Cria um retângulo para o modelo do cliente com as informações 
            //obtidas na mensagem recebida
            Rectangle rect = new Rectangle(Integer.parseInt(userInfo[1]),
                                           Integer.parseInt(userInfo[2]),20,20);
            //Atualiza o inimigo na main class passando o novo modelo de jogador
            Game.attEnemy(new PlayerModel(userInfo[0],
                                          userInfo[1],
                                          userInfo[2], 
                                          rect));
            
        } catch (IOException | NumberFormatException err) {
            JOptionPane.showMessageDialog(null, err.getMessage(), null,
                    JOptionPane.PLAIN_MESSAGE, null);
        }
    }
    
    void close() {
        try {
            working_socket.leaveGroup(group);
            working_socket.close();
        } catch (IOException err) {
            JOptionPane.showMessageDialog(null, err.getMessage(), null,
                    JOptionPane.PLAIN_MESSAGE, null);
        }
    }
}
