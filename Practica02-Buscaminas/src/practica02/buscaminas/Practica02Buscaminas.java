/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package practica02.buscaminas;
import practica02.buscaminas.Bloque;

/**
 *
 * @author Omar
 */
public class Practica02Buscaminas {

    /**
     * @param args the command line arguments
     */        
    public static void main(String[] args) {
        BuscaminasPrincipal window = new BuscaminasPrincipal();
        window.setVisible(true);
        
        window.paintAll(window.getGraphics()); 
        startGame(window);
    }
    
    public static void startGame(BuscaminasPrincipal window){
        int nFilas = 16;
        int nColumnas = 16;
        int casillas = nFilas * nColumnas;
        int nMinas = 0;
        
        window.JPanel_tablero.setLayout(new java.awt.GridLayout(nFilas, nColumnas));
        
        for(int i = 0; i < nFilas; i++){
            for(int j = 0; j < nColumnas; j++){
                Bloque tmp = new Bloque();
                tmp.setVisible(true);
                window.JPanel_tablero.add(tmp);
                System.out.println("Bloque " + (j+1) + " creado");
            }
        }
    }   
    
}
