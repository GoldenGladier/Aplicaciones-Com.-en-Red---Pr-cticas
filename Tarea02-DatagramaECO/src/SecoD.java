import java.net.*;
import java.io.*;
/**
 *
 * @author axele
 */
public class SecoD {
    public static void main(String[] args){
      try{  
          int pto=1234;
          String msj="";
          DatagramSocket s = new DatagramSocket(pto);
          s.setReuseAddress(true);
         // s.setBroadcast(true);
          System.out.println("Servidor iniciado... esperando datagramas...");
          for(;;){
              byte[] b = new byte[65535];
              DatagramPacket p = new DatagramPacket(b, b.length);
              s.receive(p);
              msj = new String(p.getData(), 0, p.getLength());
              System.out.println("\nSe ha recibido datagrama desde "+p.getAddress()+":"+p.getPort()+"\nMensaje: "+msj);
              s.send(p);
          }//for
          
      }catch(Exception e){
          e.printStackTrace();
      }//catch
        
    }//main
}
