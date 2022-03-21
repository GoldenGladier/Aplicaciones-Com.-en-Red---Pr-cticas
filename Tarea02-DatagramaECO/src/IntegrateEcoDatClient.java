
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Omar
 */
public class IntegrateEcoDatClient {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
      try{  
          int pto=1234;
          //String msg="";
          DatagramSocket s = new DatagramSocket(pto);
          s.setReuseAddress(true);
         // s.setBroadcast(true);
          System.out.println("Servidor iniciado... esperando datagramas...");
          
          for(;;){
              int seguidorF = 1;
              byte[] b = new byte[65535];
              DatagramPacket p = new DatagramPacket(b, b.length);
              s.receive(p);
              
                // DATAGRAMA (Structure: No. fragment, No. f. totals, size fragment, fragment)
              DataInputStream dis = new DataInputStream(new ByteArrayInputStream(p.getData()));            
              int nFragment = dis.readInt();
              if(nFragment != seguidorF){
                System.out.print("\nEl datagrama " + seguidorF + " se recivio fuera de lugar...");
              }
              seguidorF++;
              int totalFragments = dis.readInt();
              int sizeFragment = dis.readInt();
              byte[] bMsg = new byte[sizeFragment];
              int x = dis.read(bMsg);
              String msg = new String(bMsg);              
              
              //msj = new String(p.getData(), 0, p.getLength());
              System.out.println("\nSe ha recibido datagrama " + nFragment + "/" + totalFragments + " desde " + p.getAddress() + ":" + p.getPort());
              System.out.println("Mensaje: " + msg);
              s.send(p);
          }//for
          
      }catch(Exception e){
          e.printStackTrace();
      }//catch
    }   
}
