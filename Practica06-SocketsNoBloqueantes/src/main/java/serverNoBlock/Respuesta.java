/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package serverNoBlock;

import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.SocketChannel;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.StringTokenizer;

/**
 *
 * @author Omar
 */
public class Respuesta {

    private Mimes mime;
    private String headers;
    int bufferSize = 10240;
    private ByteBuffer b2;

    public void response(String statusLine, SocketChannel ch) {
        try {
            mime = new Mimes();
            System.out.println(statusLine);
            if (statusLine == null) {
                String empty = "<html><head><title>Servidor WEB</title><body> <br>statusLinea Vacia</br></body></html>";
                b2 = ByteBuffer.wrap(empty.getBytes());
                ch.write(b2);
            } else {
                headers = "HTTP/1.1 200 OK \r\n"
                        + "Server: webServer/1.0 \n"
                        + "Content-Type: text/html \n"
                        + "Date: " + new Date() + " \n\r\n";

                if (statusLine.startsWith("GET")) {
                    if (statusLine.indexOf("?") == -1) {
                        String fileName = getFileName(statusLine);
                        fileName = getMetadata(fileName);
                        System.out.println("Archivo: " + fileName);
                        b2 = ByteBuffer.wrap(headers.getBytes());
                        ch.write(b2);
                        sendSource(fileName, ch);
                    } else {
                        String[] reqLineas = statusLine.split("\n");
                        String response = getParameters(reqLineas[0]);
                        b2 = ByteBuffer.wrap(headers.getBytes());
                        ch.write(b2);
                        b2 = ByteBuffer.wrap(response.getBytes());
                        ch.write(b2);
                        System.out.println("Respuesta GET: \n" + response);
                    }
                } else if (statusLine.startsWith("POST")) {
                    String response = getParameters(statusLine);
                    b2 = ByteBuffer.wrap(headers.getBytes());
                    ch.write(b2);
                    b2 = ByteBuffer.wrap(response.getBytes());
                    ch.write(b2);
                    System.out.println("Respuesta POST: \n" + response);
                } else if (statusLine.startsWith("HEAD")) {
                    String fileName = getFileName(statusLine);
                    System.out.println("Respuesta HEAD:");
                    fileName = getMetadata(fileName);
                    b2 = ByteBuffer.wrap(headers.getBytes());
                    ch.write(b2);
                } else if (statusLine.startsWith("DELETE")) {
                    String fileName = getFileName(statusLine);
                    deleteSource(fileName, ch);
                } else if (statusLine.startsWith("PUT")) {
                    String fileName = getFileName(statusLine);
                    System.out.println("file: " + fileName);
                    Path path = Paths.get("");
                    String directoryName = path.toAbsolutePath().toString();

                    
                    String contenido = "Contenido de ejemplo para " + fileName;
                    File fichero = new File(directoryName + "\\Archivos\\" +fileName);
                    fichero.createNewFile();
                    
                    System.out.println("Fichero: "+ fichero.getAbsolutePath());

                    FileWriter fw = new FileWriter(fichero);
                    BufferedWriter bw = new BufferedWriter(fw);
                    bw.write(contenido);
                    bw.close();
                    //System.out.println("Archivo: " + fileName + " eliminado");
                    String delete = headers
                            + "<html><head><meta charset='UTF-8'><title>202 Succesuful</title></head>"
                            + "<body><h1>202 Recurso eliminado exitosamente.</h1>"
                            + "<p>El recurso " + fileName + " ha sido creado exitosamente en el servidor.</p>"
                            + "</body></html>";
                    b2 = ByteBuffer.wrap(delete.getBytes());
                    ch.write(b2);

                    //deleteSource(fileName, ch);
                } else {
                    String error501 = "HTTP/1.1 501 Not Implemented \r\n"
                            + "Server: webServer/1.0 \n"
                            + "Content-Type: text/html \n"
                            + "Date: " + new Date() + " \n\r\n";

                    b2 = ByteBuffer.wrap(error501.getBytes());
                    ch.write(b2);
                    sendSource("error/501.html", ch);
                    System.out.println("Respuesta ERROR 501: \n" + error501);
                }
                System.out.println("Cliente Atendido\n");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void responsePost(String[] statusLine, SocketChannel ch) {
        try {
            mime = new Mimes();
            System.out.println(statusLine);
            if (statusLine == null) {
                String empty = "<html><head><title>Servidor WEB</title><body> <br>statusLinea Vacia</br></body></html>";
                b2 = ByteBuffer.wrap(empty.getBytes());
                ch.write(b2);
            } else {
                headers = "HTTP/1.1 200 OK \r\n"
                        + "Server: webServer/1.0 \n"
                        + "Content-Type: text/html \n"
                        + "Date: " + new Date() + " \n\r\n";

                if (statusLine[0].startsWith("POST")) {
                    //StringTokenizer stokens = new StringTokenizer(statusLine, "\n");
                    //String _line_ = stokens.nextToken();
                    //System.out.println("line: " + _line_);
                    String parametros = "";
                    String html = headers
                            + "<!DOCTYPE html><html><head><meta charset='UTF-8'><title>Metodo " + "POST"
                            + "</title></head><body><center>" + "<table border='0'><tr><th>Parametro</th><th>Valor</th></tr>";
                    ;
                    for (String _line_ : statusLine) {
                        System.out.println("line: " + _line_);
                        if (_line_.startsWith("Apellido")) {
                            parametros = _line_;
                            System.out.print("PARAMETROS: ---> " + parametros);
                            break;
                        }
                    }
                    String response = "";
                    StringBuffer respuesta = new StringBuffer();

                    response += "HTTP/1.0 201 Okay \n";
                    String fecha = "Date: " + new Date() + " \n";
                    response += fecha;
                    String tipo_mime = "Content-Type: text/html \n\n";
                    response += tipo_mime;
                    response += "<html><head><title>SERVIDOR WEB</title></head>\n";
                    response += "<body bgcolor=\"#F9CEEE\"><center><h1><br>Parametros Obtenidos Mediante POST...</br></h1><h3><b>\n";
                    response += parametros;
                    response += "</b></h3>\n";
                    response += "</center></body></html>\n\n";
                    System.out.println("Respuesta: " + respuesta);

                    b2 = ByteBuffer.wrap(headers.getBytes());
                    ch.write(b2);
                    b2 = ByteBuffer.wrap(response.getBytes());
                    ch.write(b2);
                    System.out.println("Respuesta POST: \n" + response);

                } else {
                    String error501 = "HTTP/1.1 501 Not Implemented \r\n"
                            + "Server: webServer/1.0 \n"
                            + "Content-Type: text/html \n"
                            + "Date: " + new Date() + " \n\r\n";

                    b2 = ByteBuffer.wrap(error501.getBytes());
                    ch.write(b2);
                    sendSource("error/501.html", ch);
                    System.out.println("Respuesta ERROR 501: \n" + error501);
                }
                System.out.println("Cliente Atendido\n");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String getParameters(String statusLine) {
        String method;
        String line;

        if (statusLine.startsWith("GET")) {
            method = "GET";
            // Line: GET /?Nombre=&Direccion=&Telefono=&Comentarios= HTTP/1.1
            StringTokenizer tokens = new StringTokenizer(statusLine, "?");
            //System.out.println("No esta entrando bien");
            line = tokens.nextToken();
            line = tokens.nextToken();
            tokens = new StringTokenizer(line, " ");
            line = tokens.nextToken();
        } else {
            method = "POST";
            System.out.println("sl: " + statusLine);
            String[] reqLineas = statusLine.split("\n");
            StringTokenizer tokens = new StringTokenizer(statusLine, "\n");
            System.out.println("Tam: " + reqLineas.length);
            int ult = reqLineas.length - 1;
            line = reqLineas[ult];
            System.out.println("Obtener: " + line);

//            while ( !line.startsWith("Apellido") && tokens.hasMoreElements() ) {
//                                                line = tokens.nextToken();     
//                                                System.out.println("lineI: " + line);
//                                            }
        }

        StringTokenizer tokens = new StringTokenizer(line, "\n");
        String html = headers
                + "<!DOCTYPE html><html><head><meta charset='UTF-8'><title>Metodo " + method
                + "</title></head><body><center><h2>Parametros obtenidos, Método "
                + method + "</h2><br>\n" + "<table border='0'><tr><th>Parametro</th><th>Valor</th></tr>";

        while (tokens.hasMoreTokens()) {
            line = tokens.nextToken();
//        	String parameters = tokens.nextToken();
//        	StringTokenizer paramValue = new StringTokenizer( parameters , "=" );
//        	String param = "";
//            String value = "";
//            if ( paramValue.hasMoreTokens() )
//                param = paramValue.nextToken();
//            if ( paramValue.hasMoreTokens() )
//        	    value = paramValue.nextToken();
            //html = html + "<tr><td><b>" + param + "</b></td><td>" + value + "</td></tr>\n";
            System.out.println("linePost: " + line);
        }
        //System.out.println("linePost: " +line);
        html = html + "</table></center></body></html>\r\n";
        return html;
    }

    private String getFileName(String statusLine) {
        // Obtiene el nombre del recurso de la peticion HTTP
        int i = statusLine.indexOf("/");
        int f = statusLine.indexOf(" ", i);
        String resourceName = statusLine.substring(i + 1, f);

        // Si es vacio, entonces se trata del index
        if (resourceName.compareTo("") == 0) {
            resourceName = "Archivos/index.htm";
        }

        return resourceName;
    }

    private String getMetadata(String fileName) {
        String responseHead = "";
        try {
            File file = new File(fileName);
            String statusResponse = "HTTP/1.1 200 OK \r\n";
            if (!file.exists()) {
                fileName = "error/404.html"; // Recurso no encontrado
                statusResponse = "HTTP/1.1 404 Not Found \r\n";
            } else if (file.isDirectory()) {
                fileName = "error/403.html"; // Recurso privado
                statusResponse = "HTTP/1.1 403 Forbidden \r\n";
            }
            DataInputStream d = new DataInputStream(new FileInputStream(fileName));
            int len = d.available();
            int index = fileName.indexOf(".");
            String extension = fileName.substring(index + 1, fileName.length());
            d.close();
            responseHead = statusResponse
                    + "Server: webServer/1.0 \n"
                    + "Content-Type: " + mime.getTipo(extension) + " \n"
                    + "Date: " + new Date() + " \n"
                    + "Content-Length: " + len + " \n\r\n";
            this.headers = responseHead;
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println(responseHead);
        return fileName;
    }

    private void sendSource(String fileName, SocketChannel ch) {
        try {
            File file = new File(fileName);
            if (file.exists() && !file.isDirectory()) {
                ByteBuffer buffer = ByteBuffer.allocate(bufferSize);
                FileInputStream is = new FileInputStream(fileName);
                FileChannel source = is.getChannel();
                int res;
                int counter = 0;
                buffer.clear();
                while ((res = source.read(buffer)) != -1) {
                    counter += res;
                    System.out.print("\rEnviando " + counter + " bytes");
                    buffer.flip();
                    while (buffer.hasRemaining()) {
                        ch.write(buffer);
                    }
                    buffer.clear();
                }
                System.out.print("\n");
                source.close();
                is.close();
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private void deleteSource(String fileName, SocketChannel ch) {
        File f = new File(fileName);
        try {
            if (f.exists()) {
                String[] name = fileName.split("/");
                if (f.delete()) {
                    System.out.println("Archivo: " + fileName + " eliminado");
                    String delete = headers
                            + "<html><head><meta charset='UTF-8'><title>202 Succesuful</title></head>"
                            + "<body><h1>202 Recurso eliminado exitosamente.</h1>"
                            + "<p>El recurso " + name[1] + " ha sido eliminado permanentemente del servidor.</p>"
                            + "</body></html>";
                    b2 = ByteBuffer.wrap(delete.getBytes());
                    ch.write(b2);

                    System.out.println("Respuesta DELETE: \n" + delete);
                } else {
                    System.out.println("El archivo: " + fileName + " no puede ser eliminado");
                    String error = "HTTP/1.1  500 Internal server error \r\n"
                            + "Server: webServer/1.0 \n"
                            + "Content-Type: text/html \n"
                            + "Date: " + new Date() + " \n\r\n"
                            + "<html><head><meta charset='UTF-8'><title>Server Error</title></head>"
                            + "<body><h1>500 Internal server error</h1>"
                            + "<p>El recurso: " + name[1] + " no se puede borrar.</p>"
                            + "</body></html>";
                    b2 = ByteBuffer.wrap(error.getBytes());
                    ch.write(b2);
                    System.out.println("Respuesta DELETE: \n" + error);
                }
            } else {
                String error = "HTTP/1.1 404 Not Found \r\n"
                        + "Server: webServer/1.0 \n"
                        + "Content-Type: text/html \n"
                        + "Date: " + new Date() + " \n\r\n"
                        + "<html><head><meta charset='UTF-8'><title>404 Not found</title></head>"
                        + "<body><h1>404 Not found</h1>"
                        + "<p>El recurso: " + fileName + " no se encontro.</p>"
                        + "</body></html>";
                b2 = ByteBuffer.wrap(error.getBytes());
                ch.write(b2);
                System.out.println("Respuesta DELETE: \n" + error);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("El archivo: " + fileName + " no existe");
    }
}
