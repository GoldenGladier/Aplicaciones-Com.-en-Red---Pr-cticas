
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Omar
 */
public class DataClient {
    public static void main(String[] args) {
        try{
            int pto = 1234;
            int max = 65535;
            int bandC;
            DatagramSocket cl = new DatagramSocket();
            String dir="127.0.0.1";
            InetAddress dst= InetAddress.getByName(dir);
            //Se envia solicitud de juego
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            //byte[] b = new byte[max];
            DataOutputStream dos = new DataOutputStream(baos);
            dos.writeInt(1); // option 1 - Start New Game
            dos.flush();
            byte[] b = baos.toByteArray();
            DatagramPacket p = new DatagramPacket(b,b.length,dst,pto);
            cl.send(p);
            
            System.out.println("Cliente esperando respuesta del servidor...");            
            
            DatagramPacket pres = new DatagramPacket(new byte[max],max);
            cl.receive(pres);
            DataInputStream dis = new DataInputStream(new ByteArrayInputStream(pres.getData()));
            //System.out.println("dis: " + dis.readInt());
            bandC = dis.readInt();
            //Llego un 1 como aceptación
            if(bandC == 1){
                //cl.connect(pres.getAddress(), cl.getPort());
                System.out.println("Esta listo para comenzar partida");
                FrmJuego nuevaPartida = new FrmJuego();
                nuevaPartida.setVisible(true);
                int cerrarVen;
                while(true){
                    cerrarVen = nuevaPartida.getCerrar();
                    System.out.println("cerrar: " + cerrarVen);
                    if (cerrarVen == -1){
                        System.out.println("Ventana cerrada cliente");
                        dos.writeInt(2); // option 2 - Finish the Game
                        dos.flush();
                        cl.send(p);
                    }
                }
                
            }
            if(bandC == 0){// no acepto
                System.out.println("Hay otro jugador, Tendras que esperar");
            }
            
            
            //String eco = new String(pres.getData(),0,pres.getLength());
            
            
//            DataOutputStream dos = new DataOutputStream(baos);
//            dos.writeInt(1); // option 1 - Start New Game
//            dos.flush();
        }
        catch(Exception e){
            e.printStackTrace();
        }//catch    
    }//main
}
