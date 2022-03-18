
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
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
            DatagramSocket s = new DatagramSocket(pto);
            s.setReuseAddress(true);
            
            System.out.println("Cliente esperando respuesta del servidor...");            
            byte[] b = new byte[65535];
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            DataOutputStream dos = new DataOutputStream(baos);
            dos.writeInt(1); // option 1 - Start New Game
            dos.flush();
        }
        catch(Exception e){
            e.printStackTrace();
        }//catch    
    }//main
}
