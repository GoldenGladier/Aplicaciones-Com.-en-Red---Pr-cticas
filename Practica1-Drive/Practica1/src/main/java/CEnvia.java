
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
                System.out.println("1. Envíar archivo.");
                System.out.println("5. Salir.");
                System.out.println("Seleccione una opción: ");
                optionMenu = scan.nextInt();
                
                switch(optionMenu){
                    case 1:   
                        enviarArchivo(jf, dir, pto);
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
                System.out.println("enviados: "+l);
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
    
}
