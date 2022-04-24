package Test;

import java.awt.Color;
import java.awt.Dimension;
import javax.swing.ImageIcon;
import java.awt.Toolkit;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;
import java.net.SocketAddress;
import java.net.SocketException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Mixer;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Axel
 */
public class GUI1 extends javax.swing.JFrame {

    String path, tmp_u = "", tmp_m = "";
    int bandera;
    String mensaje_inicio = "", mensaje_medio = "", mensaje_final = "";
    String nameUser;

    /*Variables para creacion del multicast*/
    BufferedReader br;
    String msj;

    HashMap<String, Integer> usuarios = new HashMap<String, Integer>();
    HashMap<Integer, String> usuarios_i = new HashMap<Integer, String>();

    /*Datos para conexion al socket*/
    MulticastSocket socket;
    String dir6 = "ff3e::1234:1";
    InetAddress gpo;
    int pto = 1234;
    int tam = 65500; // establecemos el tamaño maximo
    //String ni_name = "wlan2"; //nombre de interfaz
    int origen_hash;

    /*Variable para grabar audio*/
    final JavaSoundRecorder recorder = new JavaSoundRecorder();
    Audio manejoAudio;
    
    /*Getters*/

    public HashMap<String, Integer> getUsuarios() {
        return usuarios;
    }

    public HashMap<Integer, String> getUsuarios_i() {
        return usuarios_i;
    }
    

    /*Envia el nombre de usuario*/
    public int send_msg_1(int origen, String username) {
        try {
            int tipo = 1;
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            DataOutputStream dos = new DataOutputStream(baos);

            dos.writeInt(0); //
            dos.writeInt(origen); //
            dos.writeInt(tipo);
            dos.writeUTF(username); //
            //dos.write(msg);
            dos.flush();

            byte[] byteDataPacket = baos.toByteArray();

            // datagram packet: msg, size msg, destination, port
            DatagramPacket p = new DatagramPacket(byteDataPacket, byteDataPacket.length, gpo, pto);
            socket.send(p);
        } catch (IOException ex) {
            ex.printStackTrace();
            return -1;
        }
        return 0;
    }

    /*Enviar texto (mensaje normal)*/
    public int send_msg_2(int destino, int origen, byte[] msg) {
        try {
            int tipo = 2;
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            DataOutputStream dos = new DataOutputStream(baos);

            dos.writeInt(destino); // No. fragment
            dos.writeInt(origen); // Total
            dos.writeInt(tipo);
            dos.writeInt(msg.length); // Size fragment
            dos.write(msg);
            dos.flush();

            byte[] byteDataPacket = baos.toByteArray();

            // datagram packet: msg, size msg, destination, port
            DatagramPacket p = new DatagramPacket(byteDataPacket, byteDataPacket.length, gpo, pto);
            socket.send(p);
        } catch (IOException ex) {
            ex.printStackTrace();
            return -1;
        }
        return 0;
    }

    /*Envia el nombre del archivo y su destino (a quien va dirigido) del archivo -> Nota: Este siempre se envia junto con el tipo 4*/
    public int send_msg_3(int destino, int origen, long tam, int totalFragments, String msg) {
        try {
            int tipo = 3;
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            DataOutputStream dos = new DataOutputStream(baos);

            dos.writeInt(destino); // No. fragment
            dos.writeInt(origen); // Total
            dos.writeInt(tipo);
            dos.writeLong(tam);
            dos.writeInt(totalFragments);
            dos.writeUTF(msg);
            dos.flush();

            byte[] byteDataPacket = baos.toByteArray();

            // datagram packet: msg, size msg, destination, port
            DatagramPacket p = new DatagramPacket(byteDataPacket, byteDataPacket.length, gpo, pto);
            socket.send(p);
        } catch (IOException ex) {
            ex.printStackTrace();
            return -1;
        }
        return 0;
    }

    /*Envia los datos del archivo -> Recordar que este siempre se usa despues del tipo 3*/
    public int send_msg_4(int destino, int origen, int index, int fin, byte[] msg) {
        try {
            int tipo = 4;
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            DataOutputStream dos = new DataOutputStream(baos);

            dos.writeInt(destino); // No. fragment
            dos.writeInt(origen); // Total
            dos.writeInt(tipo);
            dos.writeInt(index);
            dos.writeInt(fin);
            dos.writeInt(msg.length); // Size fragment
            dos.write(msg);
            dos.flush();

            byte[] byteDataPacket = baos.toByteArray();

            // datagram packet: msg, size msg, destination, port
            DatagramPacket p = new DatagramPacket(byteDataPacket, byteDataPacket.length, gpo, pto);
            socket.send(p);           
        } catch (IOException ex) {
            ex.printStackTrace();
            return -1;
        }
        return 0;
    }

    /*Es la contestacion al mensaje tipo uno*/
    public int send_msg_5(int origen, String username) {
        try {
            int tipo = 5;
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            DataOutputStream dos = new DataOutputStream(baos);

            dos.writeInt(0); //
            dos.writeInt(origen); //
            dos.writeInt(tipo);
            dos.writeUTF(username); //
            //dos.write(msg);
            dos.flush();

            byte[] byteDataPacket = baos.toByteArray();

            // datagram packet: msg, size msg, destination, port
            DatagramPacket p = new DatagramPacket(byteDataPacket, byteDataPacket.length, gpo, pto);
            socket.send(p);
        } catch (IOException ex) {
            ex.printStackTrace();
            return -1;
        }
        return 0;
    }

    /*Creando el multicast*/
    public void init_network() {
        try {

            //NetworkInterface ni = NetworkInterface.getByName(ni_name);
            NetworkInterface ni;
            ni = getNetwork();
            socket = new MulticastSocket(pto);
            socket.setReuseAddress(true);
            SocketAddress dirm;
            gpo = InetAddress.getByName(dir6);
            try {
                dirm = new InetSocketAddress(gpo, pto);
            } catch (Exception e) {
                e.printStackTrace();
                return;
            }//catch
            socket.joinGroup(dirm, ni);
        } catch (SocketException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }//catch
    }

    /*Metodo para obtener la interfaz multicast*/
    static public NetworkInterface getNetwork() {
        NetworkInterface ni = null;
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            Enumeration<NetworkInterface> nets = NetworkInterface.getNetworkInterfaces();

            int dir, dir_max = 0;

            int band;
            for (NetworkInterface netint : Collections.list(nets)) {
                //Detectando si soporta multicast
                band = (netint.supportsMulticast()) ? 1 : 0;
                if (band == 1) {
                    Enumeration<InetAddress> inetAddresses = netint.getInetAddresses();
                    dir = 0;
                    for (InetAddress inetAddress : Collections.list(inetAddresses)) {
                        //System.out.printf("Direccion: %s\n", inetAddress);
                        dir++;
                    }
                    if (dir > dir_max) {
                        dir_max = dir;
                        ni = netint;
                    }
                }
            }//for
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ni;
    }

    /*Envia el archivo (CAMBIOS SABADO)*/
    public void enviarArchivo(int destino_hash, File f) throws IOException {
        //int destino_hash=destino.hashCode();

        //init_network();
        
        //Para que tenga estilo el JFileChooser

        String nombre = f.getName();
        String path = f.getAbsolutePath();
        long file_size = f.length();

        System.out.println("\nPreparandose pare enviar archivo " + path + " de " + file_size + " bytes");
        System.out.println("A -> " + destino_hash + " -> desde -> " + origen_hash + "\n");

        DataInputStream dis = new DataInputStream(new FileInputStream(path));

        int enviados = 0;
        int l = 0, porcentaje = 0;
        int tp = (int) (file_size / tam);
        int lastPacket = (int) (file_size % tam);
        byte[] b;
        // Si sobran bytes
        int totalFragments = tp;
        if (file_size % tam > 0) {
            totalFragments++;
        }
        send_msg_3(destino_hash, origen_hash, file_size, totalFragments, nombre);

        while (enviados < totalFragments) {
            try {
                Thread.sleep(100);//agrega tiempo para que el receptor sea capaz de procesar todos los paquetes
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
            if (enviados < totalFragments - 1) {
                b = new byte[tam]; //https://en.wikipedia.org/wiki/User_Datagram_Protocol#:~:text=The%20field%20size%20sets%20a,20%2Dbyte%20IP%20header).
            } else {
                b = new byte[lastPacket];
            }
            l = dis.read(b);
            send_msg_4(destino_hash, origen_hash, enviados + 1, totalFragments, b);
            enviados = enviados + 1;
            porcentaje = (int) ((enviados * 100) / totalFragments);
            System.out.print("\rEnviado el " + porcentaje + "% del archivo (" + enviados + "/" + totalFragments + ")" + " enviados: " + l);
        }//while
        //}//if     
    }


    /**
     * Creates new form GUI
     */
    public GUI1() {

        //Ventana de dialogo para obtener nombre de usuario
        nameUser = JOptionPane.showInputDialog(null, "Nombre de usuario: ", "Bienvenido", JOptionPane.QUESTION_MESSAGE);

        /*Validacion**/
        //Si no se escribe nada entonces se advierte al usuario y pide de nuevo el nombre
        if ("".equals(nameUser)) {
            JOptionPane.showMessageDialog(null, "Debes ingresar un nombre de usuario", "Advertencia", JOptionPane.WARNING_MESSAGE);
            nameUser = JOptionPane.showInputDialog(null, "Nombre de usuario: ", "Bienvenido", JOptionPane.QUESTION_MESSAGE);

        }
        //Si se cancela se advierte al usuario y se termina la aplicación
        if (nameUser == null) {
            JOptionPane.showMessageDialog(null, "La aplicacion fue cancelada", "Cancelando", JOptionPane.ERROR_MESSAGE);
            System.exit(0);
        }

        initComponents();

        //Titulo de ventana
        this.setTitle("Chat: " + nameUser);

        //Conexion a multicast
        init_network();

        origen_hash = nameUser.hashCode();
        usuarios.put(nameUser, origen_hash);
        usuarios.put("publico", 0);

        usuarios_i.put(origen_hash, nameUser);
        usuarios_i.put(0, "publico");

        send_msg_1(origen_hash, nameUser);

        /*Ajuste de tamaño de ventana*/
        Toolkit tk = Toolkit.getDefaultToolkit();
        Dimension d = tk.getScreenSize();
        int ancho = (int) d.getWidth() / 2;
        int alto = (int) d.getHeight() / 2;
        this.setSize(ancho, alto);

        this.setLocationRelativeTo(null); //Centra la ventana

        bandera = 0;
        File f = new File("");
        String ruta = f.getAbsolutePath();
        path = ruta;
        mensaje_inicio = "<head><base href=\"file:" + ruta + "\\\">"
                + "<style>#usuarios {"
                + "font-family: Arial, Helvetica, sans-serif;"
                + "border-collapse: collapse;"
                + "width: 100%;"
                + "} #usuarios td, #usuarios th {"
                + "border: 0px solid #ddd;"
                + " padding: 8px;"
                + "}#usuarios tr:nth-child(even){background-color: #f2f2f2;}"
                + "#usuarios tr:hover {background-color: #ddd;}"
                + "#usuarios th {"
                + " padding-top: 12px;"
                + "padding-bottom: 12px;"
                + "text-align: left;"
                + "background-color: #04AA6D;"
                + "color: white;}"
                + "</style>"
                + "</head>"
                + "<table id=\"usuarios\">\n";
        /*   "  <tr>\n" +
            "    <th>Usuario</th>\n" +
            "    <th>Mensaje</th>\n" +
            "  </tr>";*/

        mensaje_final = "</table>";

        ImageIcon foto_cara = obtenerImageIcon("risa.gif");
        ImageIcon foto_wow = obtenerImageIcon("wow.gif");

        /*Creando ImageIcons - De iconos de interfaz grafica*/
        ImageIcon adjuntar = obtenerImageIcon("archivoIc.png");
        ImageIcon sendA = obtenerImageIcon("audio.png");


        //ImageIcon foto = new ImageIcon("C:\\Users\\52552\\Documents\\NetBeansProjects\\tttttttttt\\cara.gif");
//        Icon icono_cara = new ImageIcon(foto_cara.getImage().getScaledInstance(jLabel4.getWidth(), jLabel4.getHeight(), Image.SCALE_DEFAULT));
//        jLabel4.setIcon(icono_cara);
//        Icon icono_wow = new ImageIcon(foto_wow.getImage().getScaledInstance(jLabel2.getWidth(), jLabel2.getHeight(), Image.SCALE_DEFAULT));
//        jLabel2.setIcon(icono_wow);
        //jEditorPane1.setText(mensaje_inicio);
        //bandera=1;
        this.repaint();

        new Thread(() -> {
            try {
                Recibe r = new Recibe(socket, this);
                r.start();
                r.join(); //tal vez no vaya
            } catch (Exception e) {
            }
        }).start();
    }

    /*Metodo para obtener la ruta absoluta de la imagen*/
    public ImageIcon obtenerImageIcon(String nombre_imagen) {
        File img = new File(".\\src\\Imagenes\\" + nombre_imagen);
        return new ImageIcon(img.getAbsolutePath());
    }

    /*Reemplaza el dimbolo Emoji con la imagen de emoji*/
    public String formatEmojis(String cad) {

        String emojis[] = new String[]{":)", ":(", ":0", ";("};
        String emojis_img[] = new String[]{
            "<img src = '.\\src\\Imagenes\\feliz.png' width='20' height='20'>",
            "<img src = '.\\src\\Imagenes\\triste.png' width='20' height='20'>",
            "<img src = '.\\src\\Imagenes\\sorpresa.png' width='20' height='20'>",
            "<img src = '.\\src\\Imagenes\\enojado.png' width='20' height='20'>"};
        for (int i = 0; i < emojis.length; i++) {
            cad = cad.replace(emojis[i], emojis_img[i]);
        }
        return cad;
    }

    class Recibe extends Thread {

        MulticastSocket s;
        JFrame componente;

        public Recibe(MulticastSocket m, JFrame componente) {
            this.s = m;
            this.componente = componente;
        }

        public void recibeArchivo(long size, int totalFragments, String nombre) throws FileNotFoundException, IOException {
            System.out.println("\nSe recibe el archivo " + nombre + " con " + size + " bytes");
            DataOutputStream dos = new DataOutputStream(new FileOutputStream(nombre)); // OutputStream

            long recibidos = 0;
            int n = 0, porciento = 0;
            //byte[] b = new byte[65000];
            int nFragment = 0;
            int totalFragments_;
            int sizeFragment;
            while (nFragment < totalFragments) {
                byte[] b = new byte[65535];
                DatagramPacket p = new DatagramPacket(b, b.length);
                s.receive(p);

                // DATAGRAMA (Structure: No. fragment, No. f. totals, size fragment, fragment)
                DataInputStream dis = new DataInputStream(new ByteArrayInputStream(p.getData()));
                int hash_dest = dis.readInt();
                if (hash_dest == 0 || hash_dest == origen_hash) {
                    int hash_orig = dis.readInt();
                    int tipo = dis.readInt();
                    if (tipo == 4) {
                        nFragment = dis.readInt();
                        totalFragments_ = dis.readInt();
                        sizeFragment = dis.readInt();
                        byte[] bMsg = new byte[sizeFragment];
                        n = dis.read(bMsg);

                        //n = dis.read(b);
                        dos.write(bMsg, 0, n);
                        dos.flush();
                        recibidos++;
                        porciento = (int) ((recibidos * 100) / totalFragments);
                        System.out.println("\r Recibiendo el " + porciento + "% ---- " + nFragment + "/" + totalFragments);
                    }

                } else {
                    System.out.println("\nSe ha descartado el datagrama ");
                }

                dis.close();

            } // while

            if (recibidos == totalFragments) {
                System.out.println("\nArchivo recibido exitosamente ");
            }

            System.out.println("\nArchivo " + nombre + " de tamanio: " + size + " recibido.");
            dos.close();
        }

        public void run() {
            try {

                File f = new File("");
                String ruta = f.getAbsolutePath();
                String carpeta = "misArchivos\\" + nameUser;
                String ruta_archivos = ruta + "\\" + carpeta + "\\";
                System.out.println("ruta:" + ruta_archivos);
                File f2 = new File(ruta_archivos);
                f2.mkdirs();
                f2.setWritable(true);

                for (;;) {
                    byte[] b = new byte[65000]; //https://en.wikipedia.org/wiki/User_Datagram_Protocol#:~:text=The%20field%20size%20sets%20a,20%2Dbyte%20IP%20header).
                    DatagramPacket p = new DatagramPacket(b, b.length);
                    s.receive(p);

                    DataInputStream dis = new DataInputStream(new ByteArrayInputStream(p.getData()));
                    int hash_dest = dis.readInt();
                    int hash_orig = dis.readInt();
                    if (hash_dest == 0 || hash_dest == origen_hash || hash_orig == origen_hash) {
                        int tipo = dis.readInt();
                        if (tipo == 1 && hash_orig != origen_hash) {//mensaje inicio
                            System.out.println("Recibiendo mensaje tipo 1");
                            String name = dis.readUTF();
                            System.out.println("Nombre: " + name);
                            System.out.println("origen: " + hash_orig);
                            System.out.println("destino: " + hash_dest);
                            System.out.println("hash nombre: " + name.hashCode());
                            if (!usuarios.containsKey(name)) {
                                System.out.println("usuario añadido");
                                usuarios.put(name, hash_orig);
                                usuarios_i.put(hash_orig, name);
                                cboUsers.addItem(name);
                            } else {
                                System.out.println("usuario encontrado");
                                System.out.println(usuarios.get(name));

                            }
                            mensaje_medio += "<tr id=\"nuevo_usuario\">"
                                    + "<td>" + "<span style=\"color:#8D8DAA\">" + usuarios_i.get(hash_orig) +  " se ha unido al chat. </span> </td>\n"
                                    + "</tr>";
                            epCanalTxt.setText(mensaje_inicio + mensaje_medio + mensaje_final);
                            
                            
                            System.out.println("Mappings of HashMap usuarios are : "
                                    + usuarios);
                            send_msg_5(origen_hash, nameUser);
                        } else if (tipo == 2) {//mensaje texto
                            System.out.println("Recibiendo mensaje tipo 2");
                            if (!usuarios.containsValue(hash_orig)) { //Si no esta registrado no lee el mensaje
                                System.out.println("Usuario no registrado, por tanto no se lee el mensaje");
                                //usuarios
                                continue;
                            }
                            int sizeFragment = dis.readInt();
                            byte[] bMsg = new byte[sizeFragment];
                            int x = dis.read(bMsg);
                            String msg = new String(bMsg);

                            System.out.println(msg);
                            msg = formatEmojis(msg);
                            //mostrar mensaje en ventana
                            //tmp_m = txtMensaje.getText();
                            if (hash_dest == 0) {
                                mensaje_medio = mensaje_medio + "  <tr>\n"
                                        + "    <td>" + usuarios_i.get(hash_orig) + " dice: </td>\n"
                                        + "    <td>" + msg + "</td>\n"
                                        + "  </tr>";
                            } else {
                                mensaje_medio = mensaje_medio + "  <tr>\n"
                                        //+ "    <td>privado</td>\n"
                                        + "    <td>" + usuarios_i.get(hash_orig) + " dice: </td>\n"
                                        + "    <td>" + msg + "</td>\n"
                                        + "  </tr>";
                            }

                            epCanalTxt.setText(mensaje_inicio + mensaje_medio + mensaje_final);

                            componente.repaint();
                        } else if (tipo == 3) {//mensaje para recibir archivo
                            // Ejecutar funcion #1
                            long size = dis.readLong();
                            System.out.println("Tamaño de archivo: " + size);
                            int totalFragments = dis.readInt();
                            System.out.println("Total Fragmentos: " + totalFragments);
                            String name = dis.readUTF();
                            System.out.println("Nombre de archivo: " + name);

                            if (hash_dest == 0) {
                                mensaje_medio = mensaje_medio + "  <tr>\n"
                                        + "    <td>" + usuarios_i.get(hash_orig) + " envia: </td>\n"
                                        + "    <td>" + name + "</td>\n"
                                        + "  </tr>";
                            } else {
                                mensaje_medio = mensaje_medio + "  <tr>\n"
                                        //+ "    <td>privado</td>\n"
                                        + "    <td>" + usuarios_i.get(hash_orig) + " envia: </td>\n"
                                        + "    <td>" + name + "</td>\n"
                                        + "  </tr>";
                            }

                            epCanalTxt.setText(mensaje_inicio + mensaje_medio + mensaje_final);

                            componente.repaint();
                            if (hash_orig != origen_hash) {
                                recibeArchivo(size, totalFragments, ruta_archivos + name);
                            }
                        } else if (tipo == 5 && hash_orig != origen_hash) {//mensaje inicio
                            System.out.println("Recibiendo mensaje tipo 1");
                            String name = dis.readUTF();
                            System.out.println("Nombre: " + name);
                            System.out.println("origen: " + hash_orig);
                            System.out.println("destino: " + hash_dest);
                            System.out.println("hash nombre: " + name.hashCode());
                            if (!usuarios.containsKey(name)) {
                                System.out.println("usuario añadido");
                                usuarios.put(name, hash_orig);
                                usuarios_i.put(hash_orig, name);
                                cboUsers.addItem(name);
                            } else {
                                System.out.println("usuario encontrado");
                                System.out.println(usuarios.get(name));

                            }
                            System.out.println("Mappings of HashMap usuarios are : "
                                    + usuarios);
                        }
                    }
                    dis.close();
                } //for
            } catch (Exception e) {
                e.printStackTrace();
            }//catch
        }//run
    }//class

    /*Metodo para detectar un emoji*/
    public void isEmoji(String cad) {
        String emojis[] = new String[]{":)", ":(", ":0", ";("};
        boolean emo;
        for (int i = 0; i < 4; i++) {
            emo = cad.contains(emojis[i]);
        }
    }

    /*Clase para manejar Audio*/
    class Audio extends Thread {

        @Override
        public void run() {
            try {
                BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
                Mixer.Info[] mixerInfo = AudioSystem.getMixerInfo();
                int line_port = 0;
                for(int cnt = 0; cnt < mixerInfo.length;cnt++){
                    if( (mixerInfo[cnt].getName().contains("Microphone Array")) && (!mixerInfo[cnt].getName().contains("Port")) ){
                        line_port = cnt;
                        break;
                    }
                }//end for loop                
                recorder.start(line_port);
                br.close();
            } catch (Exception e) {
                e.printStackTrace();
            }//catch
        }

        public void finish() {
            recorder.finish();
        }

        public String getRuta() {
            return recorder.getPath();
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane2 = new javax.swing.JScrollPane();
        jEditorPane2 = new javax.swing.JEditorPane();
        jLabel6 = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jScrollPane1 = new javax.swing.JScrollPane();
        epCanalTxt = new javax.swing.JEditorPane();
        jLabel1 = new javax.swing.JLabel();
        txtMensaje = new javax.swing.JTextField();
        btnEnviar = new javax.swing.JButton();
        jSeparator1 = new javax.swing.JSeparator();
        jSeparator3 = new javax.swing.JSeparator();
        cboUsers = new javax.swing.JComboBox<>();
        jLabel7 = new javax.swing.JLabel();
        refreshUsers = new javax.swing.JButton();
        btnEnviarArchivo = new javax.swing.JButton();
        tbtnAudio = new javax.swing.JToggleButton();

        jScrollPane2.setViewportView(jEditorPane2);

        jLabel6.setText("jLabel6");

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane3.setViewportView(jTable1);

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setMinimumSize(new java.awt.Dimension(400, 450));
        setSize(new java.awt.Dimension(1428, 1100));
        getContentPane().setLayout(null);

        epCanalTxt.setEditable(false);
        epCanalTxt.setContentType("text/html");
        jScrollPane1.setViewportView(epCanalTxt);

        getContentPane().add(jScrollPane1);
        jScrollPane1.setBounds(20, 40, 440, 200);

        jLabel1.setText("Escribe aquí:");
        getContentPane().add(jLabel1);
        jLabel1.setBounds(20, 280, 90, 16);
        getContentPane().add(txtMensaje);
        txtMensaje.setBounds(100, 270, 280, 30);

        btnEnviar.setText("Enviar");
        btnEnviar.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnEnviarMouseClicked(evt);
            }
        });
        btnEnviar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEnviarActionPerformed(evt);
            }
        });
        getContentPane().add(btnEnviar);
        btnEnviar.setBounds(410, 270, 67, 25);
        getContentPane().add(jSeparator1);
        jSeparator1.setBounds(20, 330, 380, 10);

        jSeparator3.setOrientation(javax.swing.SwingConstants.VERTICAL);
        getContentPane().add(jSeparator3);
        jSeparator3.setBounds(520, 20, 10, 380);

        cboUsers.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "publico" }));
        cboUsers.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboUsersActionPerformed(evt);
            }
        });
        getContentPane().add(cboUsers);
        cboUsers.setBounds(550, 90, 100, 22);

        jLabel7.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel7.setText("Conectados");
        getContentPane().add(jLabel7);
        jLabel7.setBounds(550, 60, 80, 14);

        refreshUsers.setText("Actualizar");
        refreshUsers.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                refreshUsersActionPerformed(evt);
            }
        });
        getContentPane().add(refreshUsers);
        refreshUsers.setBounds(540, 30, 140, 25);

        btnEnviarArchivo.setText("Enviar archivo");
        btnEnviarArchivo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEnviarArchivoActionPerformed(evt);
            }
        });
        getContentPane().add(btnEnviarArchivo);
        btnEnviarArchivo.setBounds(330, 360, 110, 25);

        tbtnAudio.setText("Grabar Audio");
        tbtnAudio.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tbtnAudioMouseClicked(evt);
            }
        });
        getContentPane().add(tbtnAudio);
        tbtnAudio.setBounds(210, 360, 105, 25);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnEnviarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEnviarActionPerformed
//        try {
//            gpo = InetAddress.getByName(dir6); //Asignando valor de direccion de grupo
//
//            //System.out.println("Escribe un mensaje para ser enviado:");
//            String mensaje = txtMensaje.getText();
//
//            //Contruccion de paquete de datos (Nombre de usuario, tipo de mensaje[publico o privado], mensaje a enviar)
//            String cadena = nameUser + "'!" + bandera + "'!" + mensaje;
//            byte[] b = cadena.getBytes();
//            DatagramPacket p = new DatagramPacket(b, b.length, gpo, pto);
//            socket.send(p);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }//GEN-LAST:event_btnEnviarActionPerformed

    private void btnEnviarMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnEnviarMouseClicked
        //tmp_u = jTextField2.getText();
        try {

            //System.out.println("Escribe un mensaje para ser enviado:");
            String mensaje = txtMensaje.getText();
            String destino = (String) cboUsers.getSelectedItem();
            System.out.println("Enviando mensaje a: " + destino);
            //Contruccion de paquete de datos (Nombre de usuario, tipo de mensaje[publico o privado], mensaje a enviar)
            send_msg_2(usuarios.get(destino), origen_hash, mensaje.getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        }

        txtMensaje.setText("");

        // TODO add your handling code here:
    }//GEN-LAST:event_btnEnviarMouseClicked

    private void cboUsersActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboUsersActionPerformed
        // TODO add your handling code here:
        JComboBox comboBox = (JComboBox) evt.getSource();

        Object selected = comboBox.getSelectedItem();
        if (selected.toString().equals("publico"))
            System.out.println("Seleccionando publico");
        else
            System.out.println("seleccionando a: " + selected.toString());
    }//GEN-LAST:event_cboUsersActionPerformed

    private void refreshUsersActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_refreshUsersActionPerformed
        usuarios = new HashMap<String, Integer>();
        usuarios_i = new HashMap<Integer, String>();

        /*DefaultComboBoxModel model = (DefaultComboBoxModel)cboUsers.getModel();
        model.removeAllElements();
        cboUsers.addItem("publico");*/
        cboUsers.setModel(new DefaultComboBoxModel(new String[]{"publico"}));
        cboUsers.setSelectedIndex(0);

        //cboUsers.addItem("juanito");
        usuarios.put(nameUser, origen_hash);
        usuarios.put("publico", 0);

        usuarios_i.put(origen_hash, nameUser);
        usuarios_i.put(0, "publico");

        send_msg_1(origen_hash, nameUser);
    }//GEN-LAST:event_refreshUsersActionPerformed

    private void btnEnviarArchivoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEnviarArchivoActionPerformed
        try {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {
                Logger.getLogger(GUI1.class.getName()).log(Level.SEVERE, null, ex);
            }

            String destino = (String) cboUsers.getSelectedItem();
            System.out.println("Enviando archivo a: " + destino);
            //Contruccion de paquete de datos (Nombre de usuario, tipo de mensaje[publico o privado], mensaje a enviar)            

            JFileChooser inputFile = new JFileChooser();
            System.out.println("Abriendo file chooser");

            inputFile.setMultiSelectionEnabled(false);
            inputFile.setFileSelectionMode(JFileChooser.FILES_ONLY);
            int r = inputFile.showOpenDialog(null);

            System.out.println("Abierto");
            if (r == JFileChooser.APPROVE_OPTION) {
                File f = inputFile.getSelectedFile();
                enviarArchivo(usuarios.get(destino), f);
            }
            else{
                System.out.println("OCURRIO UN ERROR AL ENVIAR EL ARCHIVO.");
            }

        } catch (IOException ex) {
            ex.printStackTrace();
        }

    }//GEN-LAST:event_btnEnviarArchivoActionPerformed

    private void tbtnAudioMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tbtnAudioMouseClicked
        System.out.println("estado: " + tbtnAudio.getModel().isSelected());

        try {
        if (tbtnAudio.getModel().isSelected()) {
            manejoAudio = new Audio();

            manejoAudio.start();
        } else {
            manejoAudio.finish();
            String pathA = manejoAudio.getRuta();
            System.out.println("ruta Audio: " + pathA);
            String destino = (String) cboUsers.getSelectedItem();
            System.out.println("Enviando audio a: " + destino);
            //Contruccion de paquete de datos (Nombre de usuario, tipo de mensaje[publico o privado], mensaje a enviar)
            File new_audio = new File(pathA);
            enviarArchivo(usuarios.get(destino), new_audio);
        }
        } catch (IOException ex) {
            ex.printStackTrace();
        }

    }//GEN-LAST:event_tbtnAudioMouseClicked

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;

                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(GUI1.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);

        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(GUI1.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);

        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(GUI1.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);

        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(GUI1.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new GUI1().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnEnviar;
    private javax.swing.JButton btnEnviarArchivo;
    private javax.swing.JComboBox<String> cboUsers;
    private javax.swing.JEditorPane epCanalTxt;
    private javax.swing.JEditorPane jEditorPane2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JTable jTable1;
    private javax.swing.JButton refreshUsers;
    private javax.swing.JToggleButton tbtnAudio;
    private javax.swing.JTextField txtMensaje;
    // End of variables declaration//GEN-END:variables
}
