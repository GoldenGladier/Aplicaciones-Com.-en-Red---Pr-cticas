
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Arrays;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Omar
 */
public class IntegrateEcoDatServer {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
      try{  
          int pto=1234;
          String dir="127.0.0.1";
          InetAddress dst= InetAddress.getByName(dir);
          int tam = 10; // establecemos el tamaño en 10 bytes
          BufferedReader br= new BufferedReader(new InputStreamReader(System.in));
          DatagramSocket cl = new DatagramSocket();
          while(true){
              System.out.println("Escribe un mensaje, <Enter> para enviar, \"salir\" para terminar: ");
              String msj = br.readLine();
              if(msj.compareToIgnoreCase("salir")==0){
                  System.out.println("Termina programa");
                  br.close();
                  cl.close();
                  System.exit(0);
              }else{
                  byte[]b = msj.getBytes();
                  if(b.length > tam){
                      byte[]b_eco = new byte[b.length];
                      System.out.println("b_eco: " + b_eco.length + " bytes");
                      int tp = (int)(b.length / tam);
                      // Si sobran bytes
                      int totalFragments = tp;
                      if(b.length % tam > 0){
                          totalFragments++;
                      }
    //                  if(b.length%tam>0)
    //                      tp=tp+1;
                      for(int j=0; j < tp; j++){
                          //byte[] tmp = new byte[tam];
                          byte []tmp = Arrays.copyOfRange(b, j*tam, ((j*tam)+(tam)));
                          System.out.println("tmp tam " + tmp.length);
                          
                          // DATAGRAMA (Structure: No. fragment, No. f. totals, size fragment, fragment)
                            ByteArrayOutputStream baos = new ByteArrayOutputStream();   
                            DataOutputStream dos = new DataOutputStream(baos);
                            
                            dos.writeInt(j + 1); // No. fragment
                            dos.writeInt(totalFragments); // Total
                            dos.writeInt(tmp.length); // Size fragment
                            dos.write(tmp);
                            dos.flush();
                            
                            byte[] byteDataPacket = baos.toByteArray();
                                                                              
                                        // datagram packet: msg, size msg, destination, port
                          DatagramPacket p = new DatagramPacket(byteDataPacket, byteDataPacket.length, dst, pto);
                          cl.send(p);
                          System.out.println("Enviando fragmento "+(j+1)+" de "+totalFragments+"\ndesde:"+(j*tam)+" hasta "+((j*tam)+(tam)));
                          // Recibiendo acuse
                          DatagramPacket p1 = new DatagramPacket(new byte[tam],tam);
                          cl.receive(p1);
                          
                            // DATAGRAMA (Structure: No. fragment, No. f. totals, size fragment, fragment)
                          DataInputStream dis = new DataInputStream(new ByteArrayInputStream(p.getData()));            
                          int acuse_nFrag = dis.readInt();
                          int acuse_tFrag = dis.readInt();
                          int acuse_szFrag = dis.readInt();
                          byte[] bMsg = new byte[acuse_szFrag];
                          int x = dis.read(bMsg);
                          String msg = new String(bMsg);                          
                          
                          //byte[]bp1 = p1.getData();
                          for(int i=0; i<tam;i++){
                              //System.out.println((j*tam)+i+"->"+i);
                              b_eco[(j*tam)+i]=bMsg[i];
                          }//for
                      }//for
                      if(b.length%tam > 0){ //bytes sobrantes  
                          //tp=tp+1;
                          int sobrantes = b.length%tam;
                          System.out.println("Sobrantes:"+sobrantes);
                          System.out.println("b: "+b.length+" ultimo pedazo desde "+tp*tam+" hasta "+((tp*tam)+sobrantes));
                          System.out.println("Enviando fragmento " + totalFragments + " de " + totalFragments);
                          byte[] tmp = Arrays.copyOfRange(b, tp*tam, ((tp*tam)+sobrantes));
                          System.out.println("tmp tam "+tmp.length);
                          
                          // DATAGRAMA (Structure: No. fragment, No. f. totals, size fragment, fragment)
                            ByteArrayOutputStream baos = new ByteArrayOutputStream();   
                            DataOutputStream dos = new DataOutputStream(baos);
                            
                            dos.writeInt(totalFragments); // No. fragment
                            dos.writeInt(totalFragments); // Total
                            dos.writeInt(tmp.length); // Size fragment
                            dos.write(tmp);
                            dos.flush();
                            
                            byte[] byteDataPacket = baos.toByteArray();                          
                          
                          DatagramPacket p = new DatagramPacket(byteDataPacket, byteDataPacket.length, dst, pto);
                          cl.send(p);
                          // Recibiendo acuse
                          DatagramPacket p1= new DatagramPacket(new byte[tam],tam);
                          cl.receive(p1);
                          
                            // DATAGRAMA (Structure: No. fragment, No. f. totals, size fragment, fragment)
                          DataInputStream dis = new DataInputStream(new ByteArrayInputStream(p.getData()));            
                          int acuse_nFrag = dis.readInt();
                          int acuse_tFrag = dis.readInt();
                          int acuse_szFrag = dis.readInt();
                          byte[] bMsg = new byte[acuse_szFrag];
                          int x = dis.read(bMsg);
                          String msg = new String(bMsg);                          
                          
                          //byte[]bp1 = p1.getData();
                          for(int i=0; i<sobrantes;i++){
                              //System.out.println((j*tam)+i+"->"+i);
                              b_eco[(tp*tam)+i]=bMsg[i];
                          }//for                          
                          
//                          byte[]bp1 = p1.getData();
//                          for(int i=0; i<sobrantes;i++){
//                              //System.out.println((tp*tam)+i+"->"+i);
//                              b_eco[(tp*tam)+i] = bp1[i];
//                          }//for
                      }//if

                      String eco = new String(b_eco);
                      System.out.println("Eco recibido: " + eco);
                  }else{
                        // DATAGRAMA (Structure: No. fragment, No. f. totals, size fragment, fragment)
                          ByteArrayOutputStream baos = new ByteArrayOutputStream();   
                          DataOutputStream dos = new DataOutputStream(baos);

                          dos.writeInt(1); // No. fragment
                          dos.writeInt(1); // Total
                          dos.writeInt(b.length); // Size fragment
                          dos.write(b);
                          dos.flush();

                          byte[] byteDataPacket = baos.toByteArray();                          

                      DatagramPacket p = new DatagramPacket(byteDataPacket, byteDataPacket.length, dst, pto);
                      cl.send(p);
                        // Recibiendo acuse   
                      DatagramPacket p1 = new DatagramPacket(new byte[65535],65535);
                      cl.receive(p1);
                      
                        // DATAGRAMA (Structure: No. fragment, No. f. totals, size fragment, fragment)
                        DataInputStream dis = new DataInputStream(new ByteArrayInputStream(p.getData()));            
                        int acuse_nFrag = dis.readInt();
                        int acuse_tFrag = dis.readInt();
                        int acuse_szFrag = dis.readInt();
                        byte[] bMsg = new byte[acuse_szFrag];
                        int x = dis.read(bMsg);
                        String msg = new String(bMsg);                         
                      
                      //String eco = new String(p1.getData(), 0, p1.getLength());
                      System.out.println("Eco recibido: " + msg);
                  }//else
              }//else
          }//while
      }catch(Exception e){
          e.printStackTrace();
      }//catch
    }
    
}
