
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;
import javax.swing.JFileChooser;

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
    public static String separator = System.getProperty("file.separator");
    
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
          
          // ------ Variables de apoyo para las diferentes acciones ------
          long size;
          String name;
          String directoryName;
          int bandera;
          
          for(;;){
              Socket server = s.accept();
              System.out.println("Cliente conectado desde "+server.getInetAddress()+":"+server.getPort());
              DataInputStream dis = new DataInputStream(server.getInputStream());
              DataOutputStream dos = new DataOutputStream(server.getOutputStream()); //OutputStream
              
              bandera = dis.readInt();
              System.out.println("Option: " + bandera);
                      
              switch(bandera){
                  case 1: //Recibir un archivo
                      // Ejecutar funcion #1
                      size = dis.readLong();
                      System.out.println("Tamaño de archivo: " + size);
                      name = dis.readUTF();
                      System.out.println("Nombre de archivo: " + name);                      
                      recibeArchivo(size, ruta_archivos + name, dis);
                      break;
                  case 2: //Descargar un archivo
                      // Ejecutar funcion #2
                      //size = dis.readLong();
                      name = dis.readUTF();
                      System.out.println("SOLICITUD DE DESCARGA: " + name);
                      //System.out.println("Tamaño de archivo: " + size); 
                      File archivoDes = new File(ruta_archivos + name); // ya viene con separador
                      System.out.println("--> " + archivoDes.getAbsolutePath());
                      //System.out.println("Aqui");
                      
                      mandaArchivo(archivoDes, ruta_archivos, dos);
                      break;
                  case 3: // Recibir zip
                      // Ejecutar funcion #3
                      size = dis.readLong();
                      System.out.println("Tamaño de archivo: " + size);
                      name = dis.readUTF();
                      System.out.println("Nombre de archivo: " + name);                      
                      recibeArchivo(size, ruta_archivos + name, dis);
                      UnzipFile(ruta_archivos + name, ruta_archivos);
                      break;
                  case 4: // Envíar estructura de carpeta (vista)
                      // Ejecutar funcion #4
                      
                      //directoryName = "";
                      //directoryName = dis.readUTF();
                      //File f2 = new File(ruta_archivos);
                      actualizarDirectorioCliente(server, dis, f2);
                      break;       
                  case 5: // Envíar estructura de una carpeta especifica (vista)
                      // Ejecutar funcion #5
                      
                      directoryName = "";
                      directoryName = dis.readUTF();
                      System.out.println("BUSCANDO LA CARPETA: " + ruta_archivos + separator + directoryName);
                      File newDirectory = new File(ruta_archivos + separator + directoryName);
                      actualizarDirectorioCliente(server, dis, newDirectory);
                      break;      
                  case 8: // Eliminar un archivo
                      // Ejecutar funcion #6
                      name = dis.readUTF();
                      System.out.println("Borrar: " + ruta_archivos + separator + name);
                      File archivoE = new File(ruta_archivos + separator + name);
                      eliminarArch_Carp(archivoE);
                      name = "";
                      break;  
                  case 10: // Crear directorio (para uso interno del sistema)
                      // Ejecutar funcion #10
                      directoryName = dis.readUTF();
                      System.out.println("Creando directorio " + directoryName);
                        File directorio = new File(ruta_archivos + "/" + directoryName);
                        if (!directorio.exists()) {
                            if (directorio.mkdirs()) {
                                System.out.println("Directorio creado");
                            } else {
                                System.out.println("Error al crear directorio");
                            }
                        }                      
                      break;
              }
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
    
    public static void mandaArchivo(File archivo, String pathS, DataOutputStream dos) throws FileNotFoundException, IOException{
        String nombre = archivo.getName();
        //String rutaDestino = dis.readUTF() + separator + nombre;
        long tam = 0;
        int zip = 0;
        if(archivo.isDirectory()){
            zip = 1;
            String pathZipDirectory = archivo.getParent() + "/" + archivo.getName() + ".zip";
            System.out.println("ZIP: " + pathZipDirectory);
            FileOutputStream fos = new FileOutputStream(pathZipDirectory);
            ZipOutputStream zipOut = new ZipOutputStream(fos);    
            zipFile(archivo, archivo.getName(), zipOut);
            zipOut.close();                       
            fos.close();
            
            archivo = new File(pathZipDirectory);
        }
        
        tam = archivo.length();
        //String path = pathS + separator + nombre;
        System.out.println("Preparandose pare enviar archivo " + nombre + " de " + tam + " bytes\n\n");
        
        // ---- Informacion del archivo ----
        dos.writeLong(tam);
        dos.flush();   
        // ----- ----- ----- ----- ----- ---
        
        DataInputStream disArchivo = new DataInputStream(new FileInputStream(archivo.getAbsolutePath())); // InputStream
        
        long enviados = 0;
        int l=0, porcentaje=0;

        while(enviados < tam){
            byte[] b = new byte[1500];
            l = disArchivo.read(b);
            //System.out.println(" enviados: "+l);
            dos.write(b, 0, l);
            dos.flush();
            enviados += l;
            porcentaje = (int)((enviados * 100) / tam);
            System.out.print("\rEnviado el "+porcentaje+"% del archivo");
        }//while
        System.out.println("\nArchivo " + nombre + " enviado...");                
        
        disArchivo.close();
        dos.close();
        
        if(zip == 1){
            eliminarArch_Carp(archivo);
            zip = 0;
        }
    }

    private static void zipFile(File fileToZip, String fileName, ZipOutputStream zipOut) throws IOException {
        if (fileToZip.isHidden()) {
            return;
        }
        if (fileToZip.isDirectory()) {
            if (fileName.endsWith("/")) {
                zipOut.putNextEntry(new ZipEntry(fileName));
                zipOut.closeEntry();
            } else {
                zipOut.putNextEntry(new ZipEntry(fileName + "/"));
                zipOut.closeEntry();
            }
            File[] children = fileToZip.listFiles();
            for (File childFile : children) {
                zipFile(childFile, fileName + "/" + childFile.getName(), zipOut);
            }
            return;
        }
        FileInputStream fis = new FileInputStream(fileToZip);
        ZipEntry zipEntry = new ZipEntry(fileName);
        zipOut.putNextEntry(zipEntry);
        byte[] bytes = new byte[1024];
        int length;
        while ((length = fis.read(bytes)) >= 0) {
            zipOut.write(bytes, 0, length);
        }
        fis.close();
    }   
    
    public static void UnzipFile(String ZipPath, String destPath) throws FileNotFoundException, IOException {        
        File destDir = new File(destPath);
        byte[] buffer = new byte[1024];
        ZipInputStream zis = new ZipInputStream(new FileInputStream(ZipPath));
        ZipEntry zipEntry = zis.getNextEntry();
        while (zipEntry != null) {
             File newFile = newFile(destDir, zipEntry);
             if (zipEntry.isDirectory()) {
                 if (!newFile.isDirectory() && !newFile.mkdirs()) {
                     throw new IOException("Failed to create directory " + newFile);
                 }
             } else {
                 // fix for Windows-created archives
                 File parent = newFile.getParentFile();
                 if (!parent.isDirectory() && !parent.mkdirs()) {
                     throw new IOException("Failed to create directory " + parent);
                 }

                 // write file content
                 FileOutputStream fos = new FileOutputStream(newFile);
                 int len;
                 while ((len = zis.read(buffer)) > 0) {
                     fos.write(buffer, 0, len);
                 }
                 fos.close();
             }
         zipEntry = zis.getNextEntry();
        }           
        zis.closeEntry();
        zis.close();
        System.out.println(destDir.getName() + " descomprimido...");
        File fichero = new File(ZipPath);
        if (fichero.delete())
           System.out.println("El fichero ha sido borrado satisfactoriamente");
        else
           System.out.println("El fichero no puede ser borrado");        
            }    
    
    public static File newFile(File destinationDir, ZipEntry zipEntry) throws IOException {
        File destFile = new File(destinationDir, zipEntry.getName());

        String destDirPath = destinationDir.getCanonicalPath();
        String destFilePath = destFile.getCanonicalPath();

        if (!destFilePath.startsWith(destDirPath + File.separator)) {
            throw new IOException("Entry is outside of the target dir: " + zipEntry.getName());
        }

        return destFile;
    }    
    
    public static void actualizarDirectorioCliente(Socket server, DataInputStream dis, File files) throws IOException {        
        System.out.println("Actualizando info: " + files.getAbsolutePath());
        File[] list = files.listFiles();
        
        DataOutputStream dos = new DataOutputStream(server.getOutputStream()); // OutputStream    
        dos.writeInt(list.length);
        dos.flush();
        
        int tipo = 0;
        int bandera = 0;
        String info = "";
        String rutaActual = "";
        
        System.out.println("---------- ACTUALIZANDO ---------- ");
        for (File f : list) {
           
            if (f.isDirectory()) {
                tipo = 1;
                info = separator + f.getName();
                System.out.println("+ " + separator + f.getName());                
            } //if
            else {
                tipo = 0;
                info = f.getName();
                System.out.println(info);
            } //else  
            
            dos.writeUTF(f.getName()); // name
            dos.flush();
            dos.writeInt(tipo); // type
            dos.flush();
            dos.writeUTF(info); // path
            dos.flush();            
        }
        System.out.println("--------------------------------- ");
        dos.close();
        
    }    
    
    public static void listarDirectorio(String path, String parent, DataOutputStream dos) throws IOException{
        File directory = new File(path);
        String directoryName = directory.getName();
        int tipo = 0;
        String info = "";
        String name = "";
        
        if(directory.isDirectory()){
            File[] files = directory.listFiles();
            for(File file : files){
                if(file.isDirectory()){
                    tipo = 1;
                    System.out.println("+ " + parent + separator + directoryName + separator + file.getName());                                        
                }
                else{
                    tipo = 0;
                    System.out.println("- " + parent + separator + directoryName + separator + file.getName());
                }   
                
                info = parent + separator + directoryName + separator + file.getName();
                name = file.getName();
                        
                dos.writeUTF(name); // name
                dos.flush();
                dos.writeInt(tipo); // type
                dos.flush();
                dos.writeUTF(info); // path
                dos.flush();       
                
//                if(tipo == 1){
//                    parent = separator + directoryName;
//                    listarDirectorio(path + separator + file.getName(), parent, dos);
//                }
            }
        }
        
    }
    
    private static void eliminarArch_Carp(File archivo) {
        if (!archivo.exists()){
            return;
        }

        if (archivo.isDirectory()) {
            for (File f : archivo.listFiles()) {
                eliminarArch_Carp(f);
            }
        }
        archivo.delete();
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

//import java.io.DataInputStream;
//import java.io.DataOutputStream;
//import java.io.File;
//import java.io.FileNotFoundException;
//import java.io.FileOutputStream;
//import java.io.IOException;
//import java.net.ServerSocket;
//import java.net.Socket;
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
//public class SRecibe {
//    public static void main(String[] args){
//      try{
//          int pto = 8000;
//          ServerSocket s = new ServerSocket(pto);
//          ServerSocket s_arc = new ServerSocket(pto+1);
//          s.setReuseAddress(true);
//          s_arc.setReuseAddress(true);
//          System.out.println("Servidor iniciado esperando por archivos...");
//          File f = new File("");
//          String ruta = f.getAbsolutePath();
//          String carpeta="misArchivos";
//          String ruta_archivos = ruta+"\\"+carpeta+"\\";
//          System.out.println("ruta:"+ruta_archivos);
//          File f2 = new File(ruta_archivos);
//          f2.mkdirs();
//          f2.setWritable(true);
//          
//          for(;;){
//              Socket server = s.accept();
//              System.out.println("Cliente conectado desde "+server.getInetAddress()+":"+server.getPort());
//              DataInputStream dis = new DataInputStream(server.getInputStream());
//              
//              int bandera = dis.readInt();
//              System.out.println("Option: " + bandera);
//              
////              Socket serverA = s_arc.accept();
////              System.out.println("Cliente conectado desde "+serverA.getInetAddress()+":"+serverA.getPort());
////              DataInputStream disn = new DataInputStream(serverA.getInputStream());
//              
//              int n_archivos = dis.readInt();
//              System.out.println("\nn: " + n_archivos);
//              
//              
//              switch(bandera){
//                  case 1:
//                      // Ejecutar funcion #1
//                      String name = dis.readUTF();
//                      long size = dis.readLong();                      
//                      recibeArchivoCar(size, ruta_archivos + name, dis, n_archivos);
//                      break;
//                  case 2:
//                      // Ejecutar funcion #2
//                      break;
//                  case 3:
//                      // Ejecutar funcion #3
//                      break;
//              }
////              String nombre = dis.readUTF();
////              long tam = dis.readLong();
////              
////              File f3 = new File(nombre);
////              if(f3.isDirectory())
////                  System.out.println("Hola soy un directorio\n");
////                  
////              System.out.println("Comienza descarga del archivo "+nombre+" de "+tam+" bytes\n\n");
////              DataOutputStream dos = new DataOutputStream(new FileOutputStream(ruta_archivos+nombre));
////              long recibidos=0;
////              int l=0, porcentaje=0;
////              while(recibidos<tam){
////                  byte[] b = new byte[1500];
////                  l = dis.read(b);
////                  System.out.println("leidos: "+l);
////                  dos.write(b,0,l);
////                  dos.flush();
////                  recibidos = recibidos + l;
////                  porcentaje = (int)((recibidos*100)/tam);
////                  System.out.print("\rRecibido el "+ porcentaje +" % del archivo");
////              }//while
////              System.out.println("Archivo recibido..");
//              //dos.close();
//              dis.close();
//              server.close();
//          }//for
//          
//      }catch(Exception e){
//          e.printStackTrace();
//      }  
//    }//main
//    
//    public static void recibeArchivoCar(long size, String nombre, DataInputStream dis, int n_archivos) throws FileNotFoundException, IOException{
//        /* long tam = dis.readLong();
//        String pathDestino = dis.readUTF();
//        nombre = rutaServer + pathDestino; */
//        
//        for(int i = 0; i < n_archivos; i++){
//            System.out.println("\nSe recibe el archivo " + nombre + " con " + size + "bytes");
//            DataOutputStream dos = new DataOutputStream(new FileOutputStream(nombre)); // OutputStream
//
//            long recibidos = 0;
//            int n = 0, porciento = 0;
//            byte[] b = new byte[2000];
//
//            while (recibidos < size) {
//                n = dis.read(b);
//                dos.write(b, 0, n);
//                dos.flush();
//                recibidos += n;
//                porciento = (int) ((recibidos * 100) / size);
//                System.out.println("\r Recibiendo el " + porciento + "% --- " + recibidos + "/" + size + " bytes");
//            } // while
//
//            System.out.println("\nArchivo " + nombre + " de tamanio: " + size + " recibido.");
//            dos.close();
//            dis.close(); 
//        }
//
////        System.out.println("\nSe recibe el archivo " + nombre + " con " + size + "bytes");
////        DataOutputStream dos = new DataOutputStream(new FileOutputStream(nombre)); // OutputStream
////
////        long recibidos = 0;
////        int n = 0, porciento = 0;
////        byte[] b = new byte[2000];
////
////        while (recibidos < size) {
////            n = dis.read(b);
////            dos.write(b, 0, n);
////            dos.flush();
////            recibidos += n;
////            porciento = (int) ((recibidos * 100) / size);
////            System.out.println("\r Recibiendo el " + porciento + "% --- " + recibidos + "/" + size + " bytes");
////        } // while
////
////        System.out.println("\nArchivo " + nombre + " de tamanio: " + size + " recibido.");
////        dos.close();
////        dis.close();        
//    }
//    
//    
//    private void menu(){
//        System.out.println("1. Subir archivo");
//        System.out.println("2. Descargar archivo");
//        System.out.println("3. Crear carpeta");
//        System.out.println("4. Crear archivos");
//        System.out.println("5. Subir carpeta");
//        System.out.println("6. Descargar carpeta");
//        System.out.println("7. Borrar carpeta");
//        System.out.println("8. Borrar archivo");
//        //Listar archivos 
//    }
//}
