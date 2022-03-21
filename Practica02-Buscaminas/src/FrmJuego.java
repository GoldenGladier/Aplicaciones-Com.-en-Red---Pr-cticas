
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.util.List;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.UIManager;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Omar
 */
public class FrmJuego extends javax.swing.JFrame {
    /**
     * Creates new form FrmJuego
     */
    int numFilas = 16;
    int numColumnas = 16;
    int numMinas = 40;
    // ---- Botones casillas ----
    int anchoControl = 30;
    int altoControl = 30;    
    
    int bandera;
    
    JButton[][] botonesTablero;
    TableroBuscaminas tableroBuscaminas;
    
    // -------- Client conections variables --------
    //UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
    int port = 8000;
    String direction = "127.0.0.1";
    Socket cliente = new Socket(direction, port);      
    DataOutputStream dos = new DataOutputStream(cliente.getOutputStream());
    DataInputStream dis = new DataInputStream(cliente.getInputStream());
    DatagramSocket sDatagram = new DatagramSocket(5555);
    //DataInputStream disDatagram;
    //DataOutputStream datagramDos;
        
    public FrmJuego() throws IOException {
        // -------- Client conections --------
        //cliente =   
        
        // ---- BANDERA en 1 (Solicitud de startGame) ----
//        dos.writeInt(1);
//        dos.flush();
        // -----------------------------------------------        
        
        initComponents();

        juegoNuevo();
        
        this.setSize(new Dimension(botonesTablero[0][numColumnas-1].getX() + botonesTablero[0][numColumnas-1].getWidth() + anchoControl + 10,
                botonesTablero[numFilas-1][0].getY() + botonesTablero[numFilas-1][0].getHeight() + (altoControl*3) ));
        this.setLocationRelativeTo(null);
        
        addWindowListener (new WindowAdapter() {    
            public void windowClosing (WindowEvent e) {
                int bandera = -1; 
                System.out.println("Juego cerrado");
                try {
                    // ---- BANDERA en -1 (Juego cerrado) ----
                    dos.writeInt(-1);
                    dos.flush();  
                    cliente.close();
                    // -----------------------------------------------                                      
                } catch (IOException ex) {
                    Logger.getLogger(FrmJuego.class.getName()).log(Level.SEVERE, null, ex);
                }                
            }    
        }); 
    }
    
    
    public int getCerrar(){
        return bandera;
    }
    
    void descargarControles(){
        if(botonesTablero != null){
            for(int i = 0; i < botonesTablero.length; i++){
                for(int j = 0; j < botonesTablero[i].length; j++){
                    if(botonesTablero[i][j] != null){
                        getContentPane().remove(botonesTablero[i][j]);
                    }
                }
            }
        }
    }
    
    private void juegoNuevo() throws IOException{
        // ---- BANDERA en 1 (Solicitud de startGame) ----
        dos.writeInt(1);
        dos.flush();
        // -----------------------------------------------             
        descargarControles();
        cargarControles();
        //crearTableroBuscaminas();   
        repaint();
    }
    
    private void crearTableroBuscaminas() {
        tableroBuscaminas = new TableroBuscaminas(numFilas, numColumnas, numMinas);
        
        tableroBuscaminas.setEventoPartidaPerdida(new Consumer<List<Casilla>>(){
            @Override
            public void accept(List<Casilla> t){
                for(Casilla CasillaConMina : t){
                    botonesTablero[CasillaConMina.getPosFila()][CasillaConMina.getPosColumna()].setText("*");
                }
            }
        });
        
        tableroBuscaminas.setEventoPartidaGanada(new Consumer<List<Casilla>>(){
            @Override
            public void accept(List<Casilla> t){
                for(Casilla CasillaConMina : t){
                    botonesTablero[CasillaConMina.getPosFila()][CasillaConMina.getPosColumna()].setText(":D");
                }
            }
        });

        //tableroBuscaminas.imprimirTablero();
        
        tableroBuscaminas.setEventoCasillaAbierta(new Consumer<Casilla>(){
            @Override
            public void accept(Casilla t){
                    botonesTablero[t.getPosFila()][t.getPosColumna()].setEnabled(false);   
                    botonesTablero[t.getPosFila()][t.getPosColumna()].setText(t.getNumMinasAlrededor()==0?"":t.getNumMinasAlrededor() + "");
            }
        });
    }

    private void cargarControles(){
        int posXReferencia = 25;
        int posYReferencia = 25;
        
        botonesTablero = new JButton[numFilas][numColumnas];
        for(int i = 0; i < botonesTablero.length; i++){
            for(int j = 0; j < botonesTablero[i].length; j++){
                botonesTablero[i][j] = new JButton();
                botonesTablero[i][j].setName(i + "," + j);
                botonesTablero[i][j].setBorder(null);
                if(i == 0 && j == 0){
                    botonesTablero[i][j].setBounds(posXReferencia, posYReferencia, anchoControl, altoControl);    
                }
                else if(i == 0 && j != 0){
                    botonesTablero[i][j].setBounds(botonesTablero[i][j-1].getX() + botonesTablero[i][j-1].getWidth(), 
                        posYReferencia, anchoControl, altoControl);   
                }
                else{
                    botonesTablero[i][j].setBounds(botonesTablero[i-1][j].getX(), 
                        botonesTablero[i-1][j].getY() + botonesTablero[i-1][j].getHeight(), anchoControl, altoControl);                       
                }
                botonesTablero[i][j].addActionListener(new ActionListener(){
                    @Override
                    public void actionPerformed(ActionEvent e){
                        try {
                            btnClick(e);
                        } catch (IOException ex) {
                            Logger.getLogger(FrmJuego.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                });
                getContentPane().add(botonesTablero[i][j]);
            }
        }
    }
    
    private void btnClick(ActionEvent e) throws IOException{
        JButton btn = (JButton) e.getSource();
        String[] coordenada = btn.getName().split(",");
        int posFila = Integer.parseInt(coordenada[0]);
        int posColumna = Integer.parseInt(coordenada[1]); 
        DatagramPacket p;
        DataInputStream disDatagram;
        int fila, columna, nMinas, nCasillas;     
        //JOptionPane.showMessageDialog(rootPane, posFila + ", " + posColumna);
        System.out.println("Selecciono: " + posFila + ", " + posColumna);
        
        // ---- BANDERA en 2 (button click) ----
        //dos = new DataOutputStream(cliente.getOutputStream());
        dos.writeInt(2);
        dos.flush();
        //dos.close();
        // -----------------------------------------------           
        
        dos.writeInt(posFila);
        dos.flush();
        
        dos.writeInt(posColumna);
        dos.flush();
        
        System.out.println("Datos del boton enviados");
        
        // ---- leyendo bandera del servidor ----
        int serverResponse = dis.readInt();
        System.out.println("BANDERA SERVER: " + serverResponse);
        
        switch(serverResponse){
            case -1: // GAME OVER
                nCasillas = dis.readInt();
                for(int i = 0; i < nCasillas; i++){
                    p = new DatagramPacket(new byte[65535],65535);    
                    sDatagram.receive(p);
                    disDatagram = new DataInputStream(new ByteArrayInputStream(p.getData()));
                    fila = disDatagram.readInt();
                    columna = disDatagram.readInt();
                    nMinas = disDatagram.readInt();
                    disDatagram.close();
                    System.out.println("Activando mina " + fila + ", " + columna + " --> " + nMinas);
                    botonesTablero[fila][columna].setEnabled(false);   
                    botonesTablero[fila][columna].setText("X");  
                }//for                   
                JOptionPane.showMessageDialog(rootPane, "GAME OVER!");
                break;
            case 1: // SE ABRE CASILLA
                p = new DatagramPacket(new byte[65535],65535);    
                sDatagram.receive(p);
                disDatagram = new DataInputStream(new ByteArrayInputStream(p.getData()));
                fila = disDatagram.readInt();
                columna = disDatagram.readInt();
                nMinas = disDatagram.readInt();
                disDatagram.close();
                System.out.println("Activando mina " + fila + ", " + columna + " --> " + nMinas);
                botonesTablero[fila][columna].setEnabled(false);   
                botonesTablero[fila][columna].setText(nMinas==0?"":nMinas + "");                 
                break;
            case 2: // ABRE CASILLAS CON CAÑON DE RIEL
                nCasillas = dis.readInt();
                for(int i = 0; i < nCasillas; i++){
                    p = new DatagramPacket(new byte[65535],65535);    
                    sDatagram.receive(p);
                    disDatagram = new DataInputStream(new ByteArrayInputStream(p.getData()));
                    fila = disDatagram.readInt();
                    columna = disDatagram.readInt();
                    nMinas = disDatagram.readInt();
                    disDatagram.close();
                    System.out.println("Activando mina " + fila + ", " + columna + " --> " + nMinas);
                    botonesTablero[fila][columna].setEnabled(false);   
                    botonesTablero[fila][columna].setText(nMinas==0?"":nMinas + "");  
                }//for                
                break;
            case 10: // WIN
                nCasillas = dis.readInt();
                for(int i = 0; i < nCasillas; i++){
                    p = new DatagramPacket(new byte[65535],65535);    
                    sDatagram.receive(p);
                    disDatagram = new DataInputStream(new ByteArrayInputStream(p.getData()));
                    fila = disDatagram.readInt();
                    columna = disDatagram.readInt();
                    nMinas = disDatagram.readInt();
                    disDatagram.close();
                    if(i == nCasillas-1)
                    {
                        System.out.println("*** Abriendo casilla " + fila + ", " + columna + " --> " + nMinas + " ***");      
                        botonesTablero[fila][columna].setText(nMinas + "");                         
                    }
                    else{
                        System.out.println("Activando mina " + fila + ", " + columna + " --> " + nMinas);      
                        botonesTablero[fila][columna].setText(":D");                         
                    }
                    botonesTablero[fila][columna].setEnabled(false);                        
                }//for  
                JOptionPane.showMessageDialog(rootPane, "WE HAVE A WINNER!");
                break;                
        }                       
        
        //tableroBuscaminas.seleccionarCasilla(posFila, posColumna);        
    }
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        menuNuevoJuego = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jMenu1.setText("Juego");

        menuNuevoJuego.setText("Nuevo");
        menuNuevoJuego.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuNuevoJuegoActionPerformed(evt);
            }
        });
        jMenu1.add(menuNuevoJuego);

        jMenuBar1.add(jMenu1);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 274, Short.MAX_VALUE)
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void menuNuevoJuegoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuNuevoJuegoActionPerformed
        try {
            juegoNuevo();
        } catch (IOException ex) {
            Logger.getLogger(FrmJuego.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_menuNuevoJuegoActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(FrmJuego.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(FrmJuego.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(FrmJuego.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(FrmJuego.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    new FrmJuego().setVisible(true);
                } catch (IOException ex) {
                    //Logger.getLogger(FrmJuego.class.getName()).log(Level.SEVERE, null, ex);
                    System.out.println("¡Ya hay un jugador conectado al servidor!");
                }
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem menuNuevoJuego;
    // End of variables declaration//GEN-END:variables
}
