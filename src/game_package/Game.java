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
import java.awt.Rectangle;
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
    private static String userName;
    private static  Integer x = 0;
    static private Integer y = 0;
    static private Rectangle rect;

    static private  MulticastSocket working_socket;
    static final int MULTICAST_PORT = 4447;
    static final String MULTICAST_IP_ADDRESS = "230.230.231.1";
    static InetAddress group = null;
    
    static private int colorCONT;
    
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
        g.setColor(getColor());
        //Desenha o nome do usuario
        g.drawRect(rect.x, rect.y, rect.height, rect.width);
        g.fill3DRect(rect.x, rect.y, rect.height, rect.width, true);
        g.setColor(Color.WHITE);
        g.drawString(username.toString(), x+3, y+15);

        for (PlayerModel p : players) {
            g.setColor(getColor());
            g.drawRect(p.getRect().x, p.getRect().y, p.getRect().height, p.getRect().width);
            g.fill3DRect(p.getRect().x, p.getRect().y, p.getRect().height, p.getRect().width, true);
            g.setColor(Color.WHITE);
            g.drawString(p.getUserNumber(), Integer.parseInt(p.getUser_x()) + 3, Integer.parseInt(p.getUser_y()) + 15);
        }
    }

    //Controla acoes do teclado
    public void MoveObject(KeyEvent e) {
        int keyCode = e.getKeyCode();
        int offset = 5;
        switch (keyCode) {
            case KeyEvent.VK_UP:
                y = y - offset;
                rect.y = y;
                break;
            case KeyEvent.VK_DOWN:
                y = y + offset;
                rect.y = y;
                break;
            case KeyEvent.VK_LEFT:
                x = x - offset;
                rect.x = x;
                break;
            case KeyEvent.VK_RIGHT:
                x = x + offset;
                rect.x = x;
                break;
        }
        
        String msg = "";
        msg += this.username.toString() + ",";
        msg += this.x.toString() + ",";
        msg += this.y.toString();
        //System.out.println("Cliente manda: " + msg + "\n");
        sendMsg(msg.getBytes());

        //Redesenha com a nova posição
        repaint();
        checkCollision();
    }
    
    public static void sendMsg(byte[] msg) {        
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
        frame.setSize(700, 700);
        //Nome do usuario gerado aleatoriamente
        Random rnd = new Random();
        username = (rnd.nextInt(100));
        userName = username.toString();
        //Posição inicial do texto
        x = rnd.nextInt(600);
        y = rnd.nextInt(600);
        
        
        
        
        frame.setVisible(true);
        
        rect = new Rectangle(x,y,20,20);

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
         
       startGame();
    }
    
    public static void attEnemy(PlayerModel enemy){
        boolean checkEqual = false;
        System.out.println("Atualizando inimigo");
        
        if (!enemy.getUserNumber().equals(userName)) {
            for (PlayerModel p : players) {
                if (p.getUserNumber().equals(enemy.getUserNumber())) {
                    checkEqual = true;
                    p.setUser_x(enemy.getUser_x());
                    p.setUser_y(enemy.getUser_y());
                    p.attRect(Integer.parseInt(enemy.getUser_x()), Integer.parseInt(enemy.getUser_y()));
                    frame.repaint();
                }
            }
            if (!checkEqual) {
                System.out.println("Adicionou inimigo na lista");
                players.add(enemy);
            }
        }
        
        checkCollision();
    }
    
    public static void checkCollision() {
        for (PlayerModel p : players) {
            if(rect.intersects(p.getRect())){
                System.out.println("Colisão detectada");
                colorCONT++;
                if(colorCONT == 5){
                    colorCONT = 0;
                }
            }
        }
    }
    
    public static Color getColor(){
        Color color = null;
        switch(colorCONT){
            case 0:
                color = Color.BLACK;
                break;
            case 1:
                color = Color.RED;
                break;
            case 2:
                color = Color.GREEN;
                break;
            case 3:
                color = Color.ORANGE;
                break;
            case 4:
                color = Color.MAGENTA;
                break;
        }
        
        return color;
    }

    public static void startGame() {
        //Redesenha com a nova posição

        while (true) {
            String msg = "";
            msg += username.toString() + ",";
            msg += x.toString() + ",";
            msg += y.toString();
            //System.out.println("Cliente manda: " + msg + "\n");
            sendMsg(msg.getBytes());
            frame.repaint();
            checkCollision();
            
            System.out.println(working_socket.isConnected());

            try {
                Thread.sleep(300);
            } catch (InterruptedException ex) {
                Logger.getLogger(Game.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}














//x: 0, 1200 y: 0, 600