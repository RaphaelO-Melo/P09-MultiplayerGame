/******************************************************************************
 *                  Trabalho de Redes III - Multiplayer Game                  *
 *                        Centro Universitário SENAC                          *
 *                                                                            *
 *                  Técnologia em Jogos Digitais IV - 2019                    *
 *                          Raphael Oliveira Melo                             *
 ******************************************************************************/
package game_package;

import java.awt.Color;
import java.util.Random;
import java.awt.Graphics;
import java.awt.Rectangle;
import javax.swing.JFrame;
import javax.swing.JPanel;
import java.io.IOException;
import java.util.ArrayList;
import java.net.InetAddress;
import java.util.logging.Level;
import java.awt.event.KeyEvent;
import java.net.DatagramPacket;
import javax.swing.JOptionPane;
import java.net.MulticastSocket;
import java.util.logging.Logger;
import java.awt.event.KeyAdapter;

/**
 * Classe Game que é onde o objeto jogo é armazenado, ela representa o cliente
 * e é a responsável gerenciar os atributos do jogador e conecta-lo com o grupo
 * MultiCast
 * 
 * @author Raphael Melo
 */
public class Game extends JPanel {

    private static JFrame FRAME;
    private static Rectangle rect;
    private static Color playerColor;
    private static Integer USER_ID, x, y;
    private static InetAddress group = null;
    private static MulticastSocket working_socket;
    private static ArrayList<PlayerModel> players;
    
    private static final int MULTICAST_PORT = 4447;
    private static final String MULTICAST_IP_ADDRESS = "230.230.231.1";    

    /**
     * Construtor da classe Game, onde suas variáveis 
     * são iniciadas e o usuário é conectado com o grupo
    */
    public Game() {
        //Inicia ArrayList dos jogadores
        players = new ArrayList();
        //Monitora eventos do teclado
        this.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                MoveObject(e);
            }
        });
        //Tenta conctar com o grupo MultiCast
        try {
            working_socket = new MulticastSocket(MULTICAST_PORT);
            group = InetAddress.getByName(MULTICAST_IP_ADDRESS);
            working_socket.joinGroup(group);
        } catch (IOException err) {
            JOptionPane.showMessageDialog(null, err.getMessage(), null,
                    JOptionPane.PLAIN_MESSAGE, null);
        }
    }
    
    /**
     * Método que pinta a tela
     * 
     * @param g : contexto gráfico 
    */
    @Override
    public void paint(Graphics g) {
        //Obtem o foco para obter eventos de teclado
        requestFocus();
        //Limpa tela
        g.clearRect(0, 0, getWidth(), getHeight());
        //Define a cor deste jogador
        g.setColor(playerColor);
        //Desenha o quadrado do usuario usando o quadrado usado como hitbox dele
        g.drawRect(rect.x, rect.y, rect.height, rect.width);
        g.fill3DRect(rect.x, rect.y, rect.height, rect.width, true);
        //Desenha o número do usuário
        g.setColor(Color.WHITE);
        g.drawString(USER_ID.toString(), x+3, y+15);

        //Desenha todos os outros jogadores
        for (PlayerModel p : players) {
            //Se os outros jogadores estiverem colidindo com algum outro jogador
            //ou este próprio, também troca a cor dele
            if(outrosJogadoresColidindo(p)){
                g.setColor(Color.RED);
            }else{
                g.setColor(Color.BLACK);
            }  
            
            //Desenha quadrado do outro jogador usando o quadrado de hitox dele
            g.drawRect(p.getRect().x, p.getRect().y, 
                       p.getRect().height, p.getRect().width);
            g.fill3DRect(p.getRect().x, p.getRect().y, 
                         p.getRect().height, p.getRect().width, true);
            //Desenha número do usuário
            g.setColor(Color.WHITE);
            g.drawString(p.getUserNumber(), 
                         Integer.parseInt(p.getUser_x()) + 3, 
                         Integer.parseInt(p.getUser_y()) + 15);
        }
    }

    /**
     * Método que movimenta o jogador
     * 
     * @param e : KeyEvent qu será usado no tratamento para definir a nova
     * posição do usuário
    */
    private void MoveObject(KeyEvent e) {
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
        
        //Configura mensagem que será mandada com as informações do usuário
        String msg = "";
        msg += USER_ID.toString() + ",";
        msg += x.toString() + ",";
        msg += y.toString();
        sendMsg(msg.getBytes());
        //Redesenha a tela com a nova posição
        repaint();
    }
    
    /**
     * Método que manda a mensagem do usuário para o grupo
     * 
     * @param msg : mensagem que será passada 
    */
    private static void sendMsg(byte[] msg) {        
        try {
            InetAddress address = InetAddress.getByName(MULTICAST_IP_ADDRESS);
            DatagramPacket packet = new DatagramPacket(msg, msg.length, address,
                    MULTICAST_PORT);
            working_socket.send(packet);
        } catch (IOException err) {
            JOptionPane.showMessageDialog(null, err.getMessage(), null,
                    JOptionPane.PLAIN_MESSAGE, null);
        }
    }
    
    /**
     * Método que verifica se este jogador colidiu com outros jogadores que 
     * estão preentes na sessão
     * 
     * @return se colidiu
    */
    public static boolean jogadorColidindo() {
        for (PlayerModel p : players) {
            if (rect.intersects(p.getRect())) {
                playerColor = Color.RED;
                return true;
            }
        }
        playerColor = Color.BLACK;
        return false;
    }
    
    /**
     * Método que verifica se os outros jogadores colidiram com alguma coisa
     * @param p :  jogador que será verificado
     * @return se colidiu com algo 
    */
    public static boolean outrosJogadoresColidindo(PlayerModel p) {
        if (rect.intersects(p.getRect())) {
            return true;
        }
        for (PlayerModel otherP : players) {
            if (!p.getUserNumber().equals(otherP.getUserNumber())) {
                if (p.getRect().intersects(otherP.getRect())) {
                    return true;
                }
            }
        }
        return false;
    }
    
    /**
     * Método que é chamado pela thread do jogador para atualizar os inimigos,
     * também é respnsável por adicionar na lista de inimigos um inimigo que
     * ainda não tenha sido adicionado
     * 
     * @param enemy : inimigo que será atualizado
    */
    public static void attEnemy(PlayerModel enemy) {
        boolean checkEqual = false;
        if (!enemy.getUserNumber().equals(USER_ID.toString())) {
            for (PlayerModel p : players) {
                if (p.getUserNumber().equals(enemy.getUserNumber())) {
                    checkEqual = true;
                    p.setUser_x(enemy.getUser_x());
                    p.setUser_y(enemy.getUser_y());
                    p.attRect(Integer.parseInt(enemy.getUser_x()),
                            Integer.parseInt(enemy.getUser_y()));
                    FRAME.repaint();
                }
            }
            if (!checkEqual) {
                players.add(enemy);
            }
        }
    }
    
    /**
     * Método que contém o gameloop do jogo onde o jogo será atualizado
    */
    public static void startGameLoop() {
        while (true) {
            //Verifica se jogador está colidindo com algo
            if (jogadorColidindo()) {
                playerColor = Color.RED;
            } else {
                playerColor = Color.BLACK;
            }
            //Manda uma mensagem para todo o grupo atualizando sua posição atual
            String msg = "";
            msg += USER_ID.toString() + ",";
            msg += x.toString() + ",";
            msg += y.toString();
            sendMsg(msg.getBytes());
            FRAME.repaint();
            try {
                Thread.sleep(100);
            } catch (InterruptedException ex) {
               Logger.getLogger(Game.class.getName()).log(Level.SEVERE,null,ex);
            }
        }
    }

    /**
     * Método main da classe
     * @param args 
    */
    public static void main(String[] args) {
        Random rnd = new Random();
        //Define ID aleatório do usuário entre 0 e 99
        USER_ID = (rnd.nextInt(100));
        //Inicializa JFrame
        FRAME = new JFrame("Janela do jogador: " + USER_ID.toString());
        FRAME.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        Game mCanvas = new Game();
        FRAME.add(mCanvas);
        FRAME.setSize(700, 700);
        //Sorteia o aparecimento do jogador na tela
        x = rnd.nextInt(600);
        y = rnd.nextInt(600);
        FRAME.setVisible(true);
        //Inicializa quadrado do jogador usado no desenho e na hitbox
        rect = new Rectangle(x,y,20,20);
        //Cria Thread de ouvir mensagem
        GameListener thread = new GameListener(working_socket, group);
        thread.start();         
        //Inicializa jogo
        startGameLoop();
    }
}

