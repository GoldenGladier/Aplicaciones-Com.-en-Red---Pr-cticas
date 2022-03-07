
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Reina
 */
public class SRecibe {
    public static void main(String[] args){
      try{
          int pto = 8000;
          ServerSocket s = new ServerSocket(pto);
          ServerSocket s_datos = new ServerSocket(pto+1);
          s.setReuseAddress(true);
          s_datos.setReuseAddress(true);
          System.out.println("Servidor iniciado esperando por archivos...");
          File f = new File("");
          String ruta = f.getAbsolutePath();
          String carpeta="misArchivos";
          String ruta_archivos = ruta+"\\"+carpeta+"\\";
          System.out.println("ruta:"+ruta_archivos);
          File f2 = new File(ruta_archivos);
          f2.mkdirs();
          f2.setWritable(true);
          
          for(;;){
              Socket server = s.accept();
              System.out.println("Cliente conectado desde "+server.getInetAddress()+":"+server.getPort());
              DataInputStream dis = new DataInputStream(server.getInputStream());
              
              int bandera = dis.readInt();
              System.out.println("Option: " + bandera);
              
              switch(bandera){
                  case 1:
                      // Ejecutar funcion #1
                      String name = dis.readUTF();
                      long size = dis.readLong();                      
                      recibeArchivo(size, ruta_archivos + name, dis);
                      break;
                  case 2:
                      // Ejecutar funcion #2
                      break;
                  case 3:
                      // Ejecutar funcion #3
                      break;
              }
//              String nombre = dis.readUTF();
//              long tam = dis.readLong();
//              
//              File f3 = new File(nombre);
//              if(f3.isDirectory())
//                  System.out.println("Hola soy un directorio\n");
//                  
//              System.out.println("Comienza descarga del archivo "+nombre+" de "+tam+" bytes\n\n");
//              DataOutputStream dos = new DataOutputStream(new FileOutputStream(ruta_archivos+nombre));
//              long recibidos=0;
//              int l=0, porcentaje=0;
//              while(recibidos<tam){
//                  byte[] b = new byte[1500];
//                  l = dis.read(b);
//                  System.out.println("leidos: "+l);
//                  dos.write(b,0,l);
//                  dos.flush();
//                  recibidos = recibidos + l;
//                  porcentaje = (int)((recibidos*100)/tam);
//                  System.out.print("\rRecibido el "+ porcentaje +" % del archivo");
//              }//while
//              System.out.println("Archivo recibido..");
              //dos.close();
              dis.close();
              server.close();
          }//for
          
      }catch(Exception e){
          e.printStackTrace();
      }  
    }//main
    
    public static void recibeArchivo(long size, String nombre, DataInputStream dis) throws FileNotFoundException, IOException{
        /* long tam = dis.readLong();
        String pathDestino = dis.readUTF();
        nombre = rutaServer + pathDestino; */

        System.out.println("\nSe recibe el archivo " + nombre + " con " + size + "bytes");
        DataOutputStream dos = new DataOutputStream(new FileOutputStream(nombre)); // OutputStream

        long recibidos = 0;
        int n = 0, porciento = 0;
        byte[] b = new byte[2000];

        while (recibidos < size) {
            n = dis.read(b);
            dos.write(b, 0, n);
            dos.flush();
            recibidos += n;
            porciento = (int) ((recibidos * 100) / size);
            System.out.println("\r Recibiendo el " + porciento + "% --- " + recibidos + "/" + size + " bytes");
        } // while

        System.out.println("\nArchivo " + nombre + " de tamanio: " + size + " recibido.");
        dos.close();
        dis.close();        
    }
    
    
    private void menu(){
        System.out.println("1. Subir archivo");
        System.out.println("2. Descargar archivo");
        System.out.println("3. Crear carpeta");
        System.out.println("4. Crear archivos");
        System.out.println("5. Subir carpeta");
        System.out.println("6. Descargar carpeta");
        System.out.println("7. Borrar carpeta");
        System.out.println("8. Borrar archivo");
        //Listar archivos 
    }
}
