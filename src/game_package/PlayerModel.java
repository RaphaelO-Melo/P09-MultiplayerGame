/*
 * Classe PlayerModel
 * 
 * Classe responável por criar o modelo de jogador, nela estão as informações
 * do usuário, como ID, posição e retângulo de colisão/desenho
 */
package game_package;

import java.awt.Rectangle;

/**
 *
 * @author Raphael Melo
 */
public class PlayerModel {

    private String userNumber;
    private String user_x;
    private String user_y;
    private Rectangle rect;

    //Construtor da classe
    public PlayerModel(String n, String x, String y, Rectangle rect) {
        user_x = x;
        user_y = y;
        userNumber = n;
        this.rect = rect;
    }

    public String getUserNumber() {
        return userNumber;
    }

    public String getUser_x() {
        return user_x;
    }

    public String getUser_y() {
        return user_y;
    }
    
    public void setUser_x(String x){
        this.user_x = x;        
    }
    
    public void setUser_y(String y){
        this.user_y = y;        
    }
    
    public void attRect(int x, int y){
        this.rect.x = x;
        this.rect.y = y;
    }
    
    public Rectangle getRect(){
        return this.rect;
    }
}
