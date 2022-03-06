/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package practica02.buscaminas;
import javax.swing.JButton;

/**
 *
 * @author Omar
 */
public class Bloque extends JButton{
    private boolean mina;
    
    public Bloque(){
        super();
        double random = Math.random();
        if(random > 0.9){
            mina = true;
        }
        else mina = false;                
    }
    
    public boolean isMina(){
        return mina;
    }
}
