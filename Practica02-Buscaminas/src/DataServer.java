
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
public class DataServer {
    public static void main(String[] args) {
        try{
            int pto=1234;
            
            int tam = 25; // establecemos el tamaño en 10 bytes
            int band = 0;
            DatagramSocket s = new DatagramSocket(pto);
            s.setReuseAddress(true);
            
            System.out.println("Servidor conectado en el puerto:" + s.getLocalPort());
            
            while(true){
                // Recibe petición de usuario para iniciar juego
                System.out.println("Servidor esperando peticion del cliente...");
                //DataInputStream dis = new DataInputStream(s.receive(p));
                DatagramPacket p = new DatagramPacket(new byte[tam],tam);
                s.receive(p);
                InetAddress dir= s.getInetAddress();
                DataInputStream dis = new DataInputStream(new ByteArrayInputStream(p.getData()));

                band = dis.readInt();
                System.out.println("dis: " + band);
                
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                DataOutputStream dos = new DataOutputStream(baos);

                byte[] b = baos.toByteArray();
                DatagramPacket pres = new DatagramPacket(b,b.length,dir,pto);

                if(band == 1){
                    //Validando que solo se conecte un cliente
                    if(!s.isConnected()){
                        s.connect(p.getAddress(), p.getPort());
                        dos.writeInt(1); // 1 - puede conectarse/acepta conexion
                        dos.flush();
                        s.send(p);
                    }
                    else{
                        dos.writeInt(0); // 0 - no puede conectarse/rechaza conexion
                        dos.flush();
                        System.out.println("Hay un jugador activo, no puede conectarse otro cliente");
                    }
                }
                if(band == 2){
                    s.close();
                    System.out.println("Se termino el juego");
                }
                
            }
            
        }
        catch(Exception e){
            e.printStackTrace();
        }//catch    
    }//main    
}
