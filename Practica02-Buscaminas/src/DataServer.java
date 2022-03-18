
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
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
            String dir="127.0.0.1";
            InetAddress dst= InetAddress.getByName(dir);
            int tam = 25; // establecemos el tamaño en 10 bytes
            BufferedReader br= new BufferedReader(new InputStreamReader(System.in));
            DatagramSocket cl = new DatagramSocket();
            
            // Recibe petición de usuario para iniciar juego
            System.out.println("Servidor esperando peticion del cliente...");                        
            DataInputStream dis = new DataInputStream());            
            
            
            DatagramPacket p1 = new DatagramPacket(new byte[tam],tam);
            cl.receive(p1);
            System.out.println(p1);
        }
        catch(Exception e){
            e.printStackTrace();
        }//catch    
    }//main    
}
