/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game_package;

import java.awt.Color;
import java.awt.Event;
import java.awt.Graphics;
import java.awt.List;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.ArrayList;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

public class Game extends JPanel {

    static public JFrame frame;
    
    static private Integer username;
    static private Integer x = 0;
    static private Integer y = 0;

    static private  MulticastSocket working_socket;
    static final int MULTICAST_PORT = 4447;
    static final String MULTICAST_IP_ADDRESS = "230.230.231.1";
    static InetAddress group = null;
    
    public static ArrayList<PlayerModel> players;
    

    public Game() {
        
        players = new ArrayList();
        //Monitora eventos de teclado
        this.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                MoveObject(e);
            }
        });
        try {
            working_socket = new MulticastSocket(MULTICAST_PORT);
            group = InetAddress.getByName(MULTICAST_IP_ADDRESS);
            working_socket.joinGroup(group);
        } catch (Exception err) {
            JOptionPane.showMessageDialog(null, err.getMessage(), null,
                    JOptionPane.PLAIN_MESSAGE, null);
        }
    }

    //Atualizacao da tela com o nome do usuario na posicao x,y
    public void paint(Graphics g) {
        //Obtem o foco para obter eventos de teclado
        requestFocus();
        g.clearRect(0, 0, getWidth(), getHeight());
        g.setColor(Color.black);
        //Desenha o nome do usuario
        
        
        g.drawString(username.toString(), x, y);
        for(PlayerModel p : players){
            g.drawString(p.getUserNumber(), Integer.parseInt(p.getUser_x()), Integer.parseInt(p.getUser_y()));
        }
    }

    //Controla acoes do teclado
    public void MoveObject(KeyEvent e) {
        int keyCode = e.getKeyCode();
        int offset = 5;
        switch (keyCode) {
            case KeyEvent.VK_UP:
                y = y - offset;
                break;
            case KeyEvent.VK_DOWN:
                y = y + offset;
                break;
            case KeyEvent.VK_LEFT:
                x = x - offset;
                break;
            case KeyEvent.VK_RIGHT:
                x = x + offset;
                break;
        }
        
        String msg = "";
        msg += this.username.toString() + ",";
        msg += this.x.toString() + ",";
        msg += this.y.toString();
        System.out.println("Cliente manda: " + msg + "\n");
        sendMsg(msg.getBytes());

        //Redesenha com a nova posição
        repaint();
    }
    
    public void sendMsg(byte[] msg) {        
        try {
            InetAddress address = InetAddress.getByName(MULTICAST_IP_ADDRESS);
            DatagramPacket packet = new DatagramPacket(msg, msg.length, address,
                    MULTICAST_PORT);
            working_socket.send(packet);
        } catch (Exception err) {
            JOptionPane.showMessageDialog(null, err.getMessage(), null,
                    JOptionPane.PLAIN_MESSAGE, null);
        }
    }
    
    public static void main(String[] args) {
        frame = new JFrame("Mini Canvas Sample");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        Game mCanvas = new Game();
        frame.add(mCanvas);
        frame.setSize(300, 200);
        //Nome do usuario gerado aleatoriamente
        Random rnd = new Random();
        username = (rnd.nextInt(100));
        //Posição inicial do texto
        x = 300 / 2;
        y = 200 / 2;
        frame.setVisible(true);

        //Cria Thread de ouvir mensagem:
        GameListener thread = new GameListener(working_socket, group, players);
        thread.start();
        
        try {
            working_socket = new MulticastSocket(MULTICAST_PORT);
            group = InetAddress.getByName(MULTICAST_IP_ADDRESS);
            working_socket.joinGroup(group);
        } catch (IOException ex) {
            Logger.getLogger(Game.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public static void attEnemy(PlayerModel enemy){
        boolean checkEqual = false;
        
        for(PlayerModel p : players){
            if (p.getUserNumber().equals(enemy.getUserNumber())) {
                checkEqual = true;
                p.setUser_x(enemy.getUser_x());
                p.setUser_y(enemy.getUser_y());

                frame.getGraphics().clearRect(0, 0, frame.getWidth(), frame.getHeight());
                frame.getGraphics().setColor(Color.black);
                frame.getGraphics().drawString(username.toString(), x, y);
                frame.getGraphics().drawString(p.getUserNumber(), Integer.parseInt(p.getUser_x()), Integer.parseInt(p.getUser_y()));

            }
        }
        if(!checkEqual){
            players.add(enemy);
        }      
    }
}
