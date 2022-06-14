
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.zip.DataFormatException;

public class Wget {

    private static final ArrayList<URL> AbsUrl = new ArrayList<URL>();

    public static void main(String[] args) {
        long TInicio, TFin, tiempo;
        Thread t1 = null, t2 = null, t3 = null, t4 = null, t5 = null;
        TInicio = System.currentTimeMillis();
        try {
            System.out.println("Bienvenido al simulador del comando WGET");
            String nombreCarpeta = "Practica5_prueba";
            CreateDirectory(nombreCarpeta);
            try {
                Thread.sleep(5 * 1000);
            } catch (Exception e) {
                System.out.println(e);
            }
            System.out.println("Escriba el link del cual desea descargar los recursos");
            URL url = getURLPage();
            System.out.println("PATH: " + url.getPath());
            Document html = Jsoup.connect(url.toString()).get();
            File index = new File(nombreCarpeta + "/index.html");
            if (index.createNewFile()) {
                Elements imagesEditarRuta = html.getElementsByTag("img");
                for (Element image : imagesEditarRuta) {
                    String rutaRelativa = image.attr("src");
                    if (rutaRelativa.startsWith("/")) {
                        rutaRelativa = rutaRelativa.substring(1);
                    }
                    image.attr("src", rutaRelativa);
                    System.out.println("Ruta Style: " + rutaRelativa);
                    String rutaAbsoluta = url.toString() + rutaRelativa;
                    URL urlAbsoluto = new URL(rutaAbsoluta);
                    if (AbsUrl.contains(urlAbsoluto)) {
                        continue;
                    }
                    AbsUrl.add(urlAbsoluto);
                }
                System.out.println();
                CreateDirectory(nombreCarpeta + url.getPath());
                FileWriter myWriter = new FileWriter(nombreCarpeta + url.getPath() + "index.html");
                BufferedWriter bw = new BufferedWriter(myWriter);
                PrintWriter wr = new PrintWriter(bw);
                wr.append(html.toString());
                wr.close();
                bw.close();
            }
            System.out.println("Leyendo los directorios del sitio...");
            setLinks(url);
            System.out.println("Generando las carpetas del sitio...");
            for (int i = 0; i < AbsUrl.size(); i++) {
                setLinks(AbsUrl.get(i));
            }
            int i = 0;
            while (i < AbsUrl.size()) {
                String directorio = nombreCarpeta + AbsUrl.get(i).getPath();
                if (directorio.endsWith("/")) {
                    CreateDirectory(directorio);
                    i++;
                } else {
                    ClassThrds hilo1 = new ClassThrds(AbsUrl.get(i++), directorio, "Thread_1");
                    directorio = nombreCarpeta + AbsUrl.get(i).getPath();
                    ClassThrds hilo2 = new ClassThrds(AbsUrl.get(i++), directorio, "Thread_2");
                    directorio = nombreCarpeta + AbsUrl.get(i).getPath();
                    ClassThrds hilo3 = new ClassThrds(AbsUrl.get(i++), directorio, "Thread_3");
                    directorio = nombreCarpeta + AbsUrl.get(i).getPath();
                    ClassThrds hilo4 = new ClassThrds(AbsUrl.get(i++), directorio, "Thread_4");
                    directorio = nombreCarpeta + AbsUrl.get(i).getPath();
                    ClassThrds hilo5 = new ClassThrds(AbsUrl.get(i++), directorio, "Thread_5");
                    directorio = nombreCarpeta + AbsUrl.get(i).getPath();
                    t1 = new Thread(hilo1);
                    t2 = new Thread(hilo2);
                    t3 = new Thread(hilo3);
                    t4 = new Thread(hilo4);
                    t5 = new Thread(hilo5);
                    t1.start();
                    t2.start();
                    t3.start();
                    t4.start();
                    t5.start();
                    ClassWG.Download(AbsUrl.get(i), directorio);
                    i++;
                }
            }
            while (true) {
                if (t1.isAlive() || t2.isAlive() || t3.isAlive() || t4.isAlive() || t5.isAlive()) {
                } else {
                    System.out.println("Acabaron mis hilos, me rompo");
                    break;
                }
            }
        } catch (DataFormatException e) {
            System.out.println("Error en el nombre del directorio " + e.getMessage());
        } catch (NoSuchElementException e) {
            System.out.println("No ingresaste el nombre de la carpeta." + e.getMessage());
        } catch (Exception e) {
            System.out.println("Error general: " + e.getMessage());
        }
        TFin = System.currentTimeMillis();
        tiempo = TFin - TInicio;
        System.out.println("Tiempo de ejecución en milisegundos: " + tiempo);
        System.out.println("Tiempo de ejecucion en segundos: " + tiempo / 1000.0);
    }

    private static URL getAtriUrl(URL url, String atributo) {
        if (atributo.startsWith("//")) {
            return null;
        }
        URL urlBienFormada;
        String host = url.getHost();
        try {
            if (atributo.contains(host)) {
                return new URL(atributo);
            } else { // Absoluta:  http://google.com.mx/dgdhrt
                if (atributo.startsWith("http")) {
                    return null;
                }
                if (atributo.startsWith("../")) {
                    return null;
                }
                if (atributo.startsWith("#")) {
                    return null;
                }
                if (atributo.startsWith("?")) {
                    return null;
                }
                if (atributo.startsWith("//")) {
                    return null;
                }
                System.out.println("Atribut: " + atributo);
                if (atributo.startsWith("./")) {
                    atributo = atributo.substring(1);
                    if (atributo.startsWith("/../")) {
                        atributo = atributo.substring(2);
                    }
                    atributo = url.getProtocol() + "://" + url.getHost() + atributo;
                    return new URL(atributo);
                }
                if (atributo.startsWith("/")) {
                    atributo = url.getProtocol() + "://" + url.getHost() + atributo;
                    return new URL(atributo);
                } else {
                    atributo = url.getProtocol() + "://" + url.getHost() + url.getPath() + atributo;
                    return new URL(atributo);
                }
            }
        } catch (MalformedURLException e) {
            System.out.println("Error en convertir el atributo: " + atributo);
        }
        return null;
    }

    private static ArrayList<URL> obtenerLlaveAtributo(Elements elementos, URL url) {
        ArrayList<URL> linksObtenidos = new ArrayList<>();
        String linksNuevos;
        URL urlNueva;
        for (Element elemento : elementos) {
            if (elemento.hasAttr("src")) {
                linksNuevos = elemento.attr("src");
                urlNueva = getAtriUrl(url, linksNuevos);
                if (urlNueva != null) {
                    if (!AbsUrl.contains(urlNueva)) {
                        if (!linksObtenidos.contains(urlNueva)) {
                            linksObtenidos.add(urlNueva);
                        }
                    }
                }
            } else if (elemento.hasAttr("href")) {
                linksNuevos = elemento.attr("href");
                urlNueva = getAtriUrl(url, linksNuevos);
                if (urlNueva != null) {
                    if (!AbsUrl.contains(urlNueva)) {
                        if (!linksObtenidos.contains(urlNueva)) {
                            linksObtenidos.add(urlNueva);
                        }
                    }
                }
            }
        }
        return linksObtenidos;
    }

    private static void setLinks(URL url) {
        //ArrayList<URL> links = new ArrayList<URL>();
        Elements href = getAtributElements(url, "href");
        Elements src = getAtributElements(url, "src");
        if (href != null && src != null) {
            AbsUrl.addAll(obtenerLlaveAtributo(href, url));
            AbsUrl.addAll(obtenerLlaveAtributo(src, url));
        }
    }

    private static Elements getAtributElements(URL url, String atributo) {
        try {
            if (url.getPath().contains(".")) {
                return null;
            }
            Document doc = Jsoup.connect(url.toString()).get();
            return doc.getElementsByAttribute(atributo);
        } catch (HttpStatusException err) {
            System.out.println("Fallo Fetching url . url prohibido o caido: " + err);
        } catch (IOException e) {
        }
        return null;
    }

    private static URL getURLPage() {
        Scanner entrada = new Scanner(System.in);
        try {
            String url = entrada.nextLine();
            if (url.endsWith("/")) {
                return new URL(url);
            } else {
                url = url + "/";
                return new URL(url);
            }
        } catch (MalformedURLException e) {
            System.out.println("URL incorrect");
            return null;
        }
    }

    private static void CreateDirectory(String Directory) throws DataFormatException {
        if (Directory != null) {
            if (Directory.matches("[-_. A-Za-z0-9áéíóúÃ?ÉÃ?ÓÚ/]+")) { // \ / : * ? " < > |
                File file = new File(Directory);
                if (file.mkdirs()) {
                    System.out.println("Un momento...");
                } else {
                    System.out.println("Se sobrescribiran los datos");
                    file.delete();
                    file.mkdirs();
                }
            }
        }
    }

}
