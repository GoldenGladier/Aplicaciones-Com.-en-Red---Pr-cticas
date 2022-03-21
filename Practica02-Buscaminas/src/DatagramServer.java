
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;
import javax.net.ssl.SSLServerSocket;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Omar
 */
public class DatagramServer {
    public static TableroBuscaminas tableroBuscaminas;
    public static DataOutputStream dos;
    
//    public static ByteArrayOutputStream baos = new ByteArrayOutputStream();    
//    public static DataOutputStream datagramDos = new DataOutputStream(baos);    
    
    public static List <Casilla> casillasAlrededorPorActivar = new LinkedList<>();

    public static void main(String[] args) {
        try{
            int pto = 8000;
            ServerSocket s = new ServerSocket(pto);         
            s.setReuseAddress(true);
            InetAddress dir = InetAddress.getByName("127.0.0.1");    
            Socket server = s.accept();
            DataInputStream dis = new DataInputStream(server.getInputStream());  
            dos = new DataOutputStream(server.getOutputStream());
            

            for(;;){
                int bandera = dis.readInt();
                System.out.println("Option: " + bandera);     
                
                switch(bandera){
                    case -1: // End Game
                        // Ejecutar funcion #-1
                        System.out.println("EndGame");  
                        s.close();
                        
                        System.out.println("Esperando nuevo cliente...");
                        
                        s = new ServerSocket(pto);         
                        s.setReuseAddress(true);
                        server = s.accept();
                        dis = new DataInputStream(server.getInputStream());  
                        dos = new DataOutputStream(server.getOutputStream());                                                
                        break;                    
                    case 1: // StartGame
                        // Ejecutar funcion #1
                        System.out.println("StartGame");    
                        
                        crearTableroBuscaminas();
                        tableroBuscaminas.imprimirTablero();                         
                        
                        break;
                    case 2: // btnClick
                        // Ejecutar funcion #2
                        System.out.println("Button Click event detected");
                        int posFila = dis.readInt();
                        int posColumna = dis.readInt();
                        //tableroBuscaminas.seleccionarCasilla(fila, columna);  
                        int response = seleccionarCasilla(posFila, posColumna);
                        tableroBuscaminas.imprimirTablero();
                        
                        switch(response){
                            case -1: // game over
                                dos.writeInt(-1);
                                dos.flush();     
                                System.out.println("Casillas de Riel - GAME OVER: " + casillasAlrededorPorActivar.size());
                                dos.writeInt(casillasAlrededorPorActivar.size());
                                for (Casilla casilla : casillasAlrededorPorActivar) {
                                    //System.out.println("Abriendo (riel): " + casilla.getPosFila() + ", " + casilla.getPosColumna());
                                    enviarCasilla(casilla.getPosFila(), casilla.getPosColumna(), 
                                            tableroBuscaminas.casillas[casilla.getPosFila()][casilla.getPosColumna()].getNumMinasAlrededor(), dir);                                    
                                }
                                casillasAlrededorPorActivar.clear();                                
                                break;
                            case 1: // done (casilla abierta)
                                dos.writeInt(1);
                                dos.flush();         
                                System.out.println("Abriendo: " + posFila + ", " + posColumna);  
                                enviarCasilla(posFila, posColumna, tableroBuscaminas.casillas[posFila][posColumna].getNumMinasAlrededor(), dir);
                                System.out.println("Casilla enviada");                                      
                                break;      
                            case 2: // cañon de riel (multiples casillas por abrir)
                                dos.writeInt(2);
                                dos.flush();   
                                System.out.println("Casillas de Riel: " + casillasAlrededorPorActivar.size());
                                dos.writeInt(casillasAlrededorPorActivar.size());
                                for (Casilla casilla : casillasAlrededorPorActivar) {
                                    //System.out.println("Abriendo (riel): " + casilla.getPosFila() + ", " + casilla.getPosColumna());
                                    enviarCasilla(casilla.getPosFila(), casilla.getPosColumna(), 
                                            tableroBuscaminas.casillas[casilla.getPosFila()][casilla.getPosColumna()].getNumMinasAlrededor(), dir);                                    
                                }
                                casillasAlrededorPorActivar.clear();
                                break;     
                            case 10: // win
                                dos.writeInt(10);
                                dos.flush();  
                                System.out.println("Casillas de Riel - WIN: " + casillasAlrededorPorActivar.size());
                                System.out.println("TAMAÑO -> " + casillasAlrededorPorActivar.size());
                                dos.writeInt(casillasAlrededorPorActivar.size());
                                for (Casilla casilla : casillasAlrededorPorActivar) {
                                    //System.out.println("Abriendo (riel): " + casilla.getPosFila() + ", " + casilla.getPosColumna());
                                    enviarCasilla(casilla.getPosFila(), casilla.getPosColumna(), 
                                            tableroBuscaminas.casillas[casilla.getPosFila()][casilla.getPosColumna()].getNumMinasAlrededor(), dir);                                    
                                }
                                casillasAlrededorPorActivar.clear();                                
                                break;
                        }
                        break;
                }
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }//catch  
    }
    
    private static void enviarCasilla(int fila, int columna, int nMinas, InetAddress dir) throws IOException{
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream datagramDos = new DataOutputStream(baos);
        // Estructurando datagrama
        datagramDos.writeInt(fila);
        datagramDos.flush();
        datagramDos.writeInt(columna);
        datagramDos.flush();
        datagramDos.writeInt(nMinas);
        datagramDos.flush();
        // Empaquetando datagrama                                
        byte[] b = baos.toByteArray();
        DatagramSocket cl = new DatagramSocket();
        DatagramPacket p = new DatagramPacket(b, b.length, dir, 5555);
        // Enviando datagrama
        cl.send(p);
        datagramDos.close();       
    }
    
    private static void crearTableroBuscaminas() {
        tableroBuscaminas = new TableroBuscaminas(5, 5, 2);
        System.out.println("Tablero inicializado");
        
        tableroBuscaminas.setEventoPartidaPerdida(new Consumer<List<Casilla>>(){
            @Override
            public void accept(List<Casilla> t){
                for(Casilla CasillaConMina : t){
                    //botonesTablero[CasillaConMina.getPosFila()][CasillaConMina.getPosColumna()].setText("*");
                    System.out.println("MINA * -> " + CasillaConMina.getPosFila() + ", " + CasillaConMina.getPosColumna());
                }
            }
        });
        
        tableroBuscaminas.setEventoPartidaGanada(new Consumer<List<Casilla>>(){
            @Override
            public void accept(List<Casilla> t){
                for(Casilla CasillaConMina : t){
                    //botonesTablero[CasillaConMina.getPosFila()][CasillaConMina.getPosColumna()].setText(":D");
                    System.out.println("MINA :D -> " + CasillaConMina.getPosFila() + ", " + CasillaConMina.getPosColumna());                    
                }
            }
        });

        //tableroBuscaminas.imprimirTablero();
        
        tableroBuscaminas.setEventoCasillaAbierta(new Consumer<Casilla>(){
            @Override
            public void accept(Casilla t){
//                    botonesTablero[t.getPosFila()][t.getPosColumna()].setEnabled(false);   
//                    botonesTablero[t.getPosFila()][t.getPosColumna()].setText(t.getNumMinasAlrededor()==0?"":t.getNumMinasAlrededor() + "");
            }
        });
    }
    
    
    public static int seleccionarCasilla(int posFila, int posColumna) throws IOException{  
        int option;
        if(tableroBuscaminas.casillas[posFila][posColumna].isMina()){
            System.out.println("GAME OVER");  
            casillasAlrededorPorActivar = tableroBuscaminas.obtenerCasillasConMinas();            
            option = -1;
            //eventoPartidaPerdida.accept(obtenerCasillasConMinas());
        }
        else if(tableroBuscaminas.casillas[posFila][posColumna].getNumMinasAlrededor() == 0){
            tableroBuscaminas.marcarCasillaAbierta(posFila, posColumna);   
            casillasAlrededorPorActivar.add(new Casilla(posFila, posColumna));
            
            List <Casilla> casillasAlrededor = tableroBuscaminas.obtenerCasillasAlrededor(posFila, posColumna);
            for(Casilla casilla : casillasAlrededor){
                if(!casilla.isAbierta()){
                    //casilla.setAbierta(true);
                    tableroBuscaminas.marcarCasillaAbierta(casilla.getPosFila(), casilla.getPosColumna());   
                    casillasAlrededorPorActivar.add(casilla);                    
                    seleccionarCasilla(casilla.getPosFila(), casilla.getPosColumna());                    
                }
            }
            
            option = 2;
        }
        else{
            System.out.println("Casilla abierta: " + posFila + ", " + posColumna);  
            tableroBuscaminas.marcarCasillaAbierta(posFila, posColumna);
            option = 1;      
        }
        
        if(tableroBuscaminas.partidaGanada()){
            System.out.println("WE HAVE A WINNER");             
            casillasAlrededorPorActivar = tableroBuscaminas.obtenerCasillasConMinas(); 
            casillasAlrededorPorActivar.add(new Casilla(posFila, posColumna));            
            //eventoPartidaGanada.accept(obtenerCasillasConMinas());
            option = 10;
        }        
        return option;
        
    }    
}
