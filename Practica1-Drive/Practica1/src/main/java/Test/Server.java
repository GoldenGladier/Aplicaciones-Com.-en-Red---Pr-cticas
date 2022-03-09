/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Test;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

/**
 *
 * @author Reina
 */
public class Server {
    //UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            String home = System.getProperty("user.home");
            File file = new File(home+"/Downloads/prueba.txt"); 
            System.out.println(file+"\n");
            
            String sep = System.getProperty("file.separator");
            String rutaServer = "." + sep + "serverP1" + sep;
            
            System.out.println(rutaServer+"\n");
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
