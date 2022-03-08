
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;
import javax.swing.JFileChooser;
import javax.swing.UIManager;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Reina
 */
public class CEnvia {
    public static void main(String[] args){
        try{
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            JFileChooser jf = new JFileChooser();
            
            int pto = 8000;
            String dir = "127.0.0.1";
            //Socket cl = new Socket(dir, pto);
            System.out.println("Conexion con servidor establecida... Iniciando Menú...");
            int optionMenu = 0;
            Scanner scan = new Scanner (System.in);                            
            
            while(optionMenu != -1){
                System.out.println("----- Menu -----");
                System.out.println("1. Subir archivo.");
                System.out.println("2. Descargar archivo.");
                System.out.println("3. Subir multiples archivos.");
                System.out.println("5. Salir.");
                System.out.println("Seleccione una opción: ");
                optionMenu = scan.nextInt();
                
                switch(optionMenu){
                    case 1:   
                        enviarArchivo(jf, dir, pto);
                        break;
                        
                    case 2:   
                        descargarArchivo(dir, pto);
                        break;
                    case 3:
                        enviarMultiplesArchivos(jf, dir, pto);
                        break;
                    case 5:
                        System.exit(0);
                        break;
                }
            }
            
        }catch(Exception e){
            e.printStackTrace();
        }//catch
    }//main
    
    public static void enviarArchivo(JFileChooser inputFile, String direction, int port) throws IOException {
        Socket cliente = new Socket(direction, port);
        System.out.println("Abriendo file chooser");
         
        int r = inputFile.showOpenDialog(null);
        
        System.out.println("Abierto");
        if(r==JFileChooser.APPROVE_OPTION){
            File f = inputFile.getSelectedFile();
            String nombre = f.getName();
            String path = f.getAbsolutePath();
            long tam = f.length();
            System.out.println("Preparandose pare enviar archivo "+path+" de "+tam+" bytes\n\n");

            DataOutputStream dos = new DataOutputStream(cliente.getOutputStream());
            DataInputStream dis = new DataInputStream(new FileInputStream(path)); 
            // ---- BANDERA en 1 ----
            dos.writeInt(1); 
            dos.flush();
            
            // ---- Informacion del archivo ----
            dos.writeUTF(nombre);
            dos.flush();
            dos.writeLong(tam);
            dos.flush();
            // -----
            long enviados = 0;
            int l=0, porcentaje=0;
            
            while(enviados < tam){
                byte[] b = new byte[1500];
                l=dis.read(b);
                System.out.println(" enviados: "+l);
                dos.write(b,0,l);
                dos.flush();
                enviados = enviados + l;
                porcentaje = (int)((enviados * 100) / tam);
                System.out.print("\rEnviado el "+porcentaje+"% del archivo");
            }//while
            System.out.println("\nArchivo enviado...");
            dis.close();
            dos.close();
            cliente.close();
        }//if        
    }
    
    public static void enviarMultiplesArchivos(JFileChooser inputFile, String direction, int port) throws IOException {
        System.out.println("Abriendo file chooser de seleccion multiple");
        
        inputFile.setMultiSelectionEnabled(true);
        
        int r = inputFile.showOpenDialog(null);
        
        System.out.println("Abierto");
        if(r==JFileChooser.APPROVE_OPTION){
            File[] files = inputFile.getSelectedFiles();
            for(int i = 0; i < files.length; i++){
                File tempFile = files[i];
                System.out.println((i+1) + ". " + tempFile.getName() + " - " + tempFile.getPath());
                
                
                Socket cliente = new Socket(direction, port);
                String nombre = tempFile.getName();
                String path = tempFile.getAbsolutePath();
                long tam = tempFile.length();
                System.out.println("Preparandose pare enviar archivo " + path + " de " + tam + " bytes\n\n");

                DataOutputStream dos = new DataOutputStream(cliente.getOutputStream());
                DataInputStream dis = new DataInputStream(new FileInputStream(path)); 
                // ---- BANDERA en 1 ----
                dos.writeInt(1); 
                dos.flush();

                // ---- Informacion del archivo ----
                dos.writeUTF(nombre);
                dos.flush();
                dos.writeLong(tam);
                dos.flush();
                // -----
                long enviados = 0;
                int l=0, porcentaje=0;

                while(enviados < tam){
                    byte[] b = new byte[1500];
                    l=dis.read(b);
                    System.out.println(" Enviados: " + l);
                    dos.write(b,0,l);
                    dos.flush();
                    enviados = enviados + l;
                    porcentaje = (int)((enviados * 100) / tam);
                    System.out.print("Enviado el " + porcentaje + "% del archivo");
                }//while
                System.out.println("\nSend " + nombre + "... Done.");
                dis.close();
                dos.close();
                cliente.close();                                
                
            }//for FILES

        }//if        
    }    
    
    public static void descargarArchivo(String direction, int port) throws IOException {
        Socket cliente = new Socket(direction, port);
        System.out.println("Abriendo file chooser");
         
        
        File f1 = new File("");
        String ruta = f1.getAbsolutePath();
        String carpeta="misArchivos";
        String ruta_archivos = ruta+"\\"+carpeta+"\\";
        System.out.println("ruta:"+ruta_archivos);
          
        JFileChooser inputFile = new JFileChooser(ruta_archivos);
        int r = inputFile.showOpenDialog(null);
        
        System.out.println("Abierto");
        if(r==JFileChooser.APPROVE_OPTION){
            File f = inputFile.getSelectedFile();
            String nombre = f.getName();
            String path = f.getAbsolutePath();
            System.out.println("p: "+ path);
            long tam = f.length();
            System.out.println("Preparandose pare descargar archivo "+path+" de "+tam+" bytes\n\n");

            DataOutputStream dos = new DataOutputStream(cliente.getOutputStream());
            DataInputStream dis = new DataInputStream(new FileInputStream(path)); 
            // ---- BANDERA en 1 ----
            dos.writeInt(2); 
            dos.flush();
            
            // ---- Informacion del archivo ----
            dos.writeUTF(nombre);
            dos.flush();
            dos.writeLong(tam);
            dos.flush();
            // -----
            long enviados = 0;
            int l=0, porcentaje=0;
            
            while(enviados < tam){
                byte[] b = new byte[1500];
                l=dis.read(b);
                System.out.println(" descargados: "+l);
                dos.write(b,0,l);
                dos.flush();
                enviados = enviados + l;
                porcentaje = (int)((enviados * 100) / tam);
                System.out.print("\rDescargando el "+porcentaje+"% del archivo");
            }//while
            System.out.println("\nArchivo descargado...");
            dis.close();
            dos.close();
            cliente.close();
        }//if        
    }
    
}
        
/***********************Copia de seguridad xD**********************************
//import java.io.DataInputStream;
//import java.io.DataOutputStream;
//import java.io.File;
//import java.io.FileInputStream;
//import java.io.IOException;
//import java.net.Socket;
//import java.util.Scanner;
//import javax.swing.JFileChooser;
//import javax.swing.UIManager;
//
///*
// * To change this license header, choose License Headers in Project Properties.
// * To change this template file, choose Tools | Templates
// * and open the template in the editor.
// */
//
///**
// *
// * @author Reina
// */
//public class CEnvia {
//    public static void main(String[] args){
//        try{
//            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
//            JFileChooser jf = new JFileChooser();
//            
//            int pto = 8000;
//            String dir = "127.0.0.1";
//            //Socket cl = new Socket(dir, pto);
//            System.out.println("Conexion con servidor establecida... Iniciando Menú...");
//            int optionMenu = 0;
//            Scanner scan = new Scanner (System.in);                            
//            
//            while(optionMenu != -1){
//                System.out.println("----- Menu -----");
//                System.out.println("1. Subir archivo.");
//                System.out.println("2. Eliminar archivo.");
//                System.out.println("5. Salir.");
//                System.out.println("Seleccione una opción: ");
//                optionMenu = scan.nextInt();
//                
//                switch(optionMenu){
//                    case 1:   
//                        enviarArchivo(jf, dir, pto);
//                        break;
//                     
//                    case 5:
//                        System.exit(0);
//                        break;
//                }
//            }
//            
//        }catch(Exception e){
//            e.printStackTrace();
//        }//catch
//    }//main
//    
//    public static void enviarArchivo(JFileChooser inputFile, String direction, int port) throws IOException {
//        Socket cliente = new Socket(direction, port);
//        System.out.println("Abriendo file chooser");
//         
//        int r = inputFile.showOpenDialog(null);
//        
//        System.out.println("Abierto");
//        if(r==JFileChooser.APPROVE_OPTION){
//            File f = inputFile.getSelectedFile();
//            String nombre = f.getName();
//            String path = f.getAbsolutePath();
//            long tam = f.length();
//            System.out.println("Preparandose pare enviar archivo "+path+" de "+tam+" bytes\n\n");
//
//            DataOutputStream dos = new DataOutputStream(cliente.getOutputStream());
//            DataInputStream dis = new DataInputStream(new FileInputStream(path)); 
//            // ---- BANDERA en 1 ----
//            dos.writeInt(1); 
//            dos.flush();
//            
//            // ---- Informacion del archivo ----
//            dos.writeUTF(nombre);
//            dos.flush();
//            dos.writeLong(tam);
//            dos.flush();
//            // -----
//            long enviados = 0;
//            int l=0, porcentaje=0;
//            
//            while(enviados < tam){
//                byte[] b = new byte[1500];
//                l=dis.read(b);
//                System.out.println(" enviados: "+l);
//                dos.write(b,0,l);
//                dos.flush();
//                enviados = enviados + l;
//                porcentaje = (int)((enviados * 100) / tam);
//                System.out.print("\rEnviado el "+porcentaje+"% del archivo");
//            }//while
//            System.out.println("\nArchivo enviado...");
//            dis.close();
//            dos.close();
//            cliente.close();
//        }//if        
//    }   
//}


//*******************************Primer intento fallido**********************
//import java.io.DataInputStream;
//import java.io.DataOutputStream;
//import java.io.File;
//import java.io.FileInputStream;
//import java.io.IOException;
//import java.net.Socket;
//import java.util.Scanner;
//import javax.swing.JFileChooser;
//import javax.swing.UIManager;
//
///*
// * To change this license header, choose License Headers in Project Properties.
// * To change this template file, choose Tools | Templates
// * and open the template in the editor.
// */
//
///**
// *
// * @author Reina
// */
//public class CEnvia {
//    public static void main(String[] args){
//        try{
//            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
//            JFileChooser jf = new JFileChooser();
//            //jf.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
//            jf.setMultiSelectionEnabled(true);
//            
//            int pto = 8000;
//            String dir = "127.0.0.1";
//            //Socket cl = new Socket(dir, pto);
//            System.out.println("Conexion con servidor establecida... Iniciando Menú...");
//            int optionMenu = 0;
//            Scanner scan = new Scanner (System.in); 
//            
//            while(optionMenu != -1){
//                System.out.println("----- Menu -----");
//                System.out.println("1. Envíar archivo.");
//                System.out.println("5. Salir.");
//                System.out.println("Seleccione una opción: ");
//                optionMenu = scan.nextInt();
//                
//                switch(optionMenu){
//                    case 1:   
//                        enviarArchivo(jf, dir, pto);
//                        break;
//                     
//                    case 5:
//                        System.exit(0);
//                        break;
//                }
//            }
//            
//        }catch(Exception e){
//            e.printStackTrace();
//        }//catch
//    }//main
//    
//    public static void enviarArchivo(JFileChooser inputFile, String direction, int port) throws IOException {
//        Socket cliente = new Socket(direction, port);
//        //Socket cliente_arc = new Socket(direction, port+1);
//        System.out.println("Abriendo file chooser");
//         
//        int r = inputFile.showOpenDialog(null);
//        
//        System.out.println("Abierto");
//        if(r==JFileChooser.APPROVE_OPTION){
//            File[] f = inputFile.getSelectedFiles();
//            int n_archivos = f.length;
//            
//            for(int i = 0; i < n_archivos; i++){
//                String nombre = f[i].getName();
//                String path = f[i].getAbsolutePath();
//                long tam = f[i].length();
//                System.out.println("Preparandose pare enviar archivo "+path+" de "+tam+" bytes\n\n");
//
//                DataOutputStream dos = new DataOutputStream(cliente.getOutputStream());
//                DataInputStream dis = new DataInputStream(new FileInputStream(path)); 
//                // ---- BANDERA en 1 ----
//                dos.writeInt(1); 
//                dos.flush();
//                dos.writeInt(n_archivos);
//                
////                DataOutputStream dosn = new DataOutputStream(cliente.getOutputStream());
////                //Numero de archivos
////                dosn.writeInt(n_archivos); 
////                dosn.flush();
//
//                // ---- Informacion del archivo ----
//                dos.writeUTF(nombre);
//                dos.flush();
//                dos.writeLong(tam);
//                dos.flush();
//                // -----
//                long enviados = 0;
//                int l=0, porcentaje=0;
//
//                while(enviados < tam){
//                    byte[] b = new byte[1500];
//                    l=dis.read(b);
//                    System.out.println(" enviados: "+l);
//                    dos.write(b,0,l);
//                    dos.flush();
//                    enviados = enviados + l;
//                    porcentaje = (int)((enviados * 100) / tam);
//                    System.out.print("\rEnviado el "+porcentaje+"% del archivo: " + nombre);
//                }//while
//                System.out.println("\nArchivo " + nombre + " enviado...");
//                dis.close();
//                dos.close();
//                cliente.close();
//            }//for
//        }//if
////            String nombre = f.getName();
////            String path = f.getAbsolutePath();
////            long tam = f.length();
////            System.out.println("Preparandose pare enviar archivo "+path+" de "+tam+" bytes\n\n");
////
////            DataOutputStream dos = new DataOutputStream(cliente.getOutputStream());
////            DataInputStream dis = new DataInputStream(new FileInputStream(path)); 
////            // ---- BANDERA en 1 ----
////            dos.writeInt(1); 
////            dos.flush();
////            
////            // ---- Informacion del archivo ----
////            dos.writeUTF(nombre);
////            dos.flush();
////            dos.writeLong(tam);
////            dos.flush();
////            // -----
////            long enviados = 0;
////            int l=0, porcentaje=0;
////            
////            while(enviados < tam){
////                byte[] b = new byte[1500];
////                l=dis.read(b);
////                System.out.println("enviados: "+l);
////                dos.write(b,0,l);
////                dos.flush();
////                enviados = enviados + l;
////                porcentaje = (int)((enviados * 100) / tam);
////                System.out.print("\rEnviado el "+porcentaje+"% del archivo");
////            }//while
////            System.out.println("\nArchivo enviado...");
////            dis.close();
////            dos.close();
////            cliente.close();
////        }//if  
////        else if(r==JFileChooser.CANCEL_OPTION){
////            //System.out.println("cancelado");
////        }
//    }
//    
//}
