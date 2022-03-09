/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Omar
 */
public class Archivo {
    String name;
    String path;
    int type; // -> 0 => file, 1 => directory

    public Archivo(String name, String path, int type) {
        this.name = name;
        this.path = path;
        this.type = type;
    }        
    
    public String getName(){
        return name;
    }
    public String getPath(){
        return path;
    }
    public int getType(){
        return type;
    }    
}
