
import java.io.File;
import java.io.Serializable;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Omar
 */
public class Instruction implements Serializable{
    int action;
    File file;

    public Instruction(int action, File file) {
        this.action = action;
        this.file = file;
    }    
    
    public int getAction(){
        return action;
    }
    public File getFile(){
        return file;
    }
}
