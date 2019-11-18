/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game_package;

import java.awt.Color;
import java.awt.Rectangle;

/**
 *
 * @author raphael.omelo
 */
public class PlayerModel {

    private String userNumber;
    private String user_x;
    private String user_y;
    private Rectangle rect;
    private Color color;
    private boolean colidindo;

    public PlayerModel(String n, String x, String y, Rectangle rect) {
        userNumber = n;
        user_x = x;
        user_y = y;
        this.rect = rect;
        this.color = Color.BLACK;
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
    
    public boolean estaColidindo(){
        return colidindo;
    }
    
    public void setColidindo(boolean newState){
        if(newState){
            color = Color.RED;
        }else{
            color = Color.BLACK;
        }
        
        this.colidindo = newState;
    }
    
    public Color getColor(){
        return this.color;
    }
}
