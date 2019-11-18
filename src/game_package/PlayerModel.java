/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game_package;

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

    public PlayerModel(String n, String x, String y, Rectangle rect) {
        userNumber = n;
        user_x = x;
        user_y = y;
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
