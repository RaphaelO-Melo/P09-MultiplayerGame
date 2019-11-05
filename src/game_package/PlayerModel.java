/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game_package;
/**
 *
 * @author raphael.omelo
 */
public class PlayerModel {

    private byte userNumber;

    private byte user_x;
    private byte user_y;

    public PlayerModel(byte n, byte x, byte y) {
        userNumber = n;
        user_x = x;
        user_y = y;
    }

    public byte getUserNumber() {
        return userNumber;
    }

    public byte getUser_x() {
        return user_x;
    }

    public byte getUser_y() {
        return user_y;
    }
    
    public String getConvertedNumber(){
        Byte b = userNumber;
        String resp = b.toString();
        return resp;
    }
    
    public int getConvertedX(){
        Byte b = user_x;
        int resp = b.intValue();
        return resp;
    }
    
    public int getConvertedY(){
        Byte b = user_y;
        int resp = b.intValue();
        return resp;
    }

    public void setUser_x(byte user_x) {
        this.user_x = user_x;
    }

    public void setUser_y(byte user_y) {
        this.user_y = user_y;
    }
}
