package Test;

import java.awt.Dimension;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import java.awt.Image;
import java.awt.Toolkit;
import java.io.BufferedReader;
import java.io.File;
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
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import static javax.swing.JOptionPane.WARNING_MESSAGE;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Axel
 */
public class GUI extends javax.swing.JFrame {

    String path, tmp_u = "", tmp_m = "";
    int bandera;
    String mensaje_inicio = "", mensaje_medio = "", mensaje_final = "";
    String nameUser;

    /*Variables para creacion del multicast*/
    MulticastSocket socket;
    BufferedReader br;
    String msj;

    /*Datos para conexion al socket*/
    String dir = "231.1.1.1";
    String dir6 = "ff3e::1234:1";
    int pto = 1234;
    InetAddress gpo;

    /**
     * Creates new form GUI
     */
    public GUI() {

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

        /*Ajuste de tamaño de ventana*/
        Toolkit tk = Toolkit.getDefaultToolkit();
        Dimension d = tk.getScreenSize();
        int ancho = (int) d.getWidth() / 2;
        int alto = (int) d.getHeight() / 2;
        this.setSize(ancho, alto);

        this.setLocationRelativeTo(null); //Centra la ventana

        /*Main*/
//        try {
//            br = new BufferedReader(new InputStreamReader(System.in, "ISO-8859-1"));
//
//            NetworkInterface ni = NetworkInterface.getByName("wlan1");
//            
//            MulticastSocket m = new MulticastSocket(pto);
//            m.setReuseAddress(true);
//            m.setTimeToLive(255);
//            String dir = "231.1.1.1";
//            String dir6 = "ff3e::1234:1";
//            InetAddress gpo = InetAddress.getByName(dir6);
//            //InetAddress gpo = InetAddress.getByName("ff3e:40:2001::1");
//            SocketAddress dirm;
//            try {
//                dirm = new InetSocketAddress(gpo, pto);
//            } catch (Exception e) {
//                e.printStackTrace();
//                return;
//            }//catch
//            m.joinGroup(dirm, ni);
//            System.out.println("Socket unido al grupo " + gpo);
//
//            Recibe r = new Recibe(m);
//            r.start();
//            r.join(); //tal vez no vaya
//        } catch (Exception e) {
//        }
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

        /*Lectura de usuarios existentes*/
        cboUsersC.addItem(nameUser);

        ImageIcon foto_cara = obtenerImageIcon("risa.gif");
        ImageIcon foto_wow = obtenerImageIcon("wow.gif");

        /*Creando ImageIcons - De emojis y de iconos de interfaz grafica*/
        ImageIcon feliz = obtenerImageIcon("feliz.png");
        ImageIcon triste = obtenerImageIcon("triste.png");
        ImageIcon sorp = obtenerImageIcon("sorpresa.png");
        ImageIcon enojado = obtenerImageIcon("enojado.png");

        ImageIcon adjuntar = obtenerImageIcon("clip.png");
        ImageIcon sendA = obtenerImageIcon("audio.png");

        /*Agregando iconos a interfaz grafica*/
        lblArchivo.setIcon(adjuntar);
        lblAudio.setIcon(sendA);

        //ImageIcon foto = new ImageIcon("C:\\Users\\52552\\Documents\\NetBeansProjects\\tttttttttt\\cara.gif");
//        Icon icono_cara = new ImageIcon(foto_cara.getImage().getScaledInstance(jLabel4.getWidth(), jLabel4.getHeight(), Image.SCALE_DEFAULT));
//        jLabel4.setIcon(icono_cara);
//        Icon icono_wow = new ImageIcon(foto_wow.getImage().getScaledInstance(jLabel2.getWidth(), jLabel2.getHeight(), Image.SCALE_DEFAULT));
//        jLabel2.setIcon(icono_wow);
        //jEditorPane1.setText(mensaje_inicio);
        //bandera=1;
        this.repaint();

        /*Main*/
        new Thread(() -> {
            try {
                br = new BufferedReader(new InputStreamReader(System.in, "ISO-8859-1"));

                NetworkInterface ni;
                ni = getNetwork();
                //NetworkInterface ni = NetworkInterface.getByName("wlan1");

                MulticastSocket m = new MulticastSocket(pto);
                m.setReuseAddress(true);
                m.setTimeToLive(255);
                socket = m;
                String dir = "231.1.1.1";
                String dir6 = "ff3e::1234:1";
                InetAddress gpo = InetAddress.getByName(dir6);
                //InetAddress gpo = InetAddress.getByName("ff3e:40:2001::1");
                SocketAddress dirm;
                try {
                    dirm = new InetSocketAddress(gpo, pto);
                } catch (Exception e) {
                    e.printStackTrace();
                    return;
                }//catch
                m.joinGroup(dirm, ni);
                System.out.println("Socket unido al grupo " + gpo);

                Recibe r = new Recibe(m);
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

    /*Metodo para obtener la interfaz multicast*/
    static public NetworkInterface getNetwork() {
        NetworkInterface ni = null;
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            Enumeration<NetworkInterface> nets = NetworkInterface.getNetworkInterfaces();
            
            int z = 0, pto = 9930, pto_dst = 9931, dir, dir_max = 0;

            int band;
            for (NetworkInterface netint : Collections.list(nets)) {
                //Detectando si soporta multicast
                band = (netint.supportsMulticast()) ? 1 : 0;
                if (band == 1) {
                    Enumeration<InetAddress> inetAddresses = netint.getInetAddresses();
                    dir = 0;
                    for (InetAddress inetAddress : Collections.list(inetAddresses)) {
                        System.out.printf("Direccion: %s\n", inetAddress);
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

    class Recibe extends Thread {

        MulticastSocket socket;

        public Recibe(MulticastSocket m) {
            this.socket = m;
        }

        public void run() {
            try {

                for (;;) {
                    DatagramPacket p = new DatagramPacket(new byte[65535], 65535);
                    System.out.println("Listo para recibir mensajes...");

                    socket.receive(p);
                    String tmpCad = new String(p.getData(), 0, p.getLength()); //Pasando el paquete recibido a String
                    String[] cadena = tmpCad.split("'!"); //Se separa la cadena

                    /*Obtencion de datos que se encuentran en la cadena*/
                    String usuario = cadena[0];
                    int tipoMsj = Integer.parseInt(cadena[1]);
                    String msj = cadena[2];

                    mostrarMensaje(usuario, msj);
                    System.out.println("Usuario: " + usuario + " Mensaje recibido: " + msj + " Tipo: " + tipoMsj);
                } //for
            } catch (Exception e) {
                e.printStackTrace();
            }//catch
        }//run
    }//class

    public void envia() {
        try {
            gpo = InetAddress.getByName(dir6); //Asignando valor de direccion de grupo

            //System.out.println("Escribe un mensaje para ser enviado:");
            String mensaje = txtMensaje.getText();

            //Contruccion de paquete de datos (Nombre de usuario, tipo de mensaje[publico o privado], mensaje a enviar)
            String cadena = nameUser + "'!" + bandera + "'!" + mensaje;
            byte[] b = cadena.getBytes();
            DatagramPacket p = new DatagramPacket(b, b.length, gpo, pto);
            socket.send(p);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void enviaNombre() {
        try {
            gpo = InetAddress.getByName(dir6); //Asignando valor de direccion de grupo
            String nombre = nameUser;
            byte[] b = nombre.getBytes();
            DatagramPacket p = new DatagramPacket(b, b.length, gpo, pto);
            socket.send(p);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void mostrarMensaje(String nombre, String mensaje) {
        tmp_m = txtMensaje.getText();
        int pos;
        String simbolo[];
        Emoji carita;
        carita = isEmoji(mensaje);

        pos = carita.getPos();
        simbolo = carita.getEmoji();

        for (int i = 0; i < 4; i++) {
            System.out.println(i + ": " + simbolo[i]);
        }

        for (int i = 0; i < 4; i++) {
            if (pos != -1) {
                switch (simbolo[i]) {
                    case ":)":
                        mensaje = mensaje.replace(simbolo[i], "<img src = '.\\src\\Imagenes\\feliz.png' width='20' height='20'>");
                        break;
                    case ":(":
                        mensaje = mensaje.replace(simbolo[i], "<img src = '.\\src\\Imagenes\\triste.png' width='20' height='20'>");
                        break;
                    case ":0":
                        mensaje = mensaje.replace(simbolo[i], "<img src = '.\\src\\Imagenes\\sorpresa.png' width='20' height='20'>");
                        break;
                    case ";(":
                        mensaje = mensaje.replace(simbolo[i], "<img src = '.\\src\\Imagenes\\enojado.png' width='20' height='20'>");
                        break;
                }
            }
        }

        mensaje_medio = mensaje_medio + "  <tr>\n"
                + "    <td>" + nombre + " dice: </td>\n"
                + "    <td>" + mensaje + "</td>\n"
                + "  </tr>";
        epCanalTxt.setText(mensaje_inicio + mensaje_medio + mensaje_final);

        this.repaint();
        txtMensaje.setText("");
    }

    /*Metodo para detectar un emoji*/
    public Emoji isEmoji(String cad) {
        String emojis[] = new String[]{":)", ":(", ":0", ";("};
        String existenEmos[] = new String[4];
        boolean hayEmo;
        int posE;
        Emoji emo = new Emoji();
        for (int i = 0; i < 4; i++) {
            hayEmo = cad.contains(emojis[i]);
            if (hayEmo) {
                System.out.println("Si hay emoji");
                posE = cad.indexOf(emojis[i]);
                existenEmos[i] = emojis[i];
                System.out.println("El emoji " + emojis[i] + " existe " + existenEmos[i] + " Esta en la posicion: " + posE);
                emo = new Emoji(posE, existenEmos); //Crea la "estructura" emoji
            } else {
                existenEmos[i] = "";
            }
        }
        return emo;
    }

    /*Clase emoji para simular una estructura*/
    class Emoji {

        int pos;
        String emoji[];

        public Emoji(int p, String e[]) {
            this.pos = p;
            this.emoji = e;
        }

        public Emoji() {
            this.pos = -1;
            this.emoji = null;
        }

        public int getPos() {
            return pos;
        }

        public String[] getEmoji() {
            return emoji;
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
        cboUsersC = new javax.swing.JComboBox<>();
        jLabel7 = new javax.swing.JLabel();
        lblArchivo = new javax.swing.JLabel();
        lblAudio = new javax.swing.JLabel();

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
        setTitle("Ejemplo");
        setAlwaysOnTop(true);
        setMinimumSize(new java.awt.Dimension(400, 450));
        setSize(new java.awt.Dimension(1428, 1100));
        getContentPane().setLayout(null);

        epCanalTxt.setEditable(false);
        epCanalTxt.setContentType("text/html");
        jScrollPane1.setViewportView(epCanalTxt);

        getContentPane().add(jScrollPane1);
        jScrollPane1.setBounds(60, 40, 275, 148);

        jLabel1.setText("Escribe aquí:");
        getContentPane().add(jLabel1);
        jLabel1.setBounds(20, 270, 90, 14);
        getContentPane().add(txtMensaje);
        txtMensaje.setBounds(110, 260, 170, 30);

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
        btnEnviar.setBounds(310, 260, 63, 23);
        getContentPane().add(jSeparator1);
        jSeparator1.setBounds(20, 310, 380, 10);

        jSeparator3.setOrientation(javax.swing.SwingConstants.VERTICAL);
        getContentPane().add(jSeparator3);
        jSeparator3.setBounds(420, 10, 10, 380);

        cboUsersC.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Todos" }));
        cboUsersC.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboUsersCActionPerformed(evt);
            }
        });
        getContentPane().add(cboUsersC);
        cboUsersC.setBounds(440, 50, 150, 20);

        jLabel7.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel7.setText("Conectados");
        getContentPane().add(jLabel7);
        jLabel7.setBounds(440, 20, 80, 14);

        lblArchivo.setText("<Clip>");
        lblArchivo.setMaximumSize(new java.awt.Dimension(30, 30));
        lblArchivo.setMinimumSize(new java.awt.Dimension(30, 30));
        lblArchivo.setPreferredSize(new java.awt.Dimension(30, 30));
        lblArchivo.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lblArchivoMouseClicked(evt);
            }
        });
        getContentPane().add(lblArchivo);
        lblArchivo.setBounds(110, 330, 40, 40);

        lblAudio.setText("<Micro>");
        lblAudio.setMaximumSize(new java.awt.Dimension(30, 30));
        lblAudio.setMinimumSize(new java.awt.Dimension(30, 30));
        lblAudio.setPreferredSize(new java.awt.Dimension(30, 30));
        lblAudio.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lblAudioMouseClicked(evt);
            }
        });
        getContentPane().add(lblAudio);
        lblAudio.setBounds(210, 330, 40, 40);

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
        envia();
//        tmp_m = txtMensaje.getText();
//        mensaje_medio = mensaje_medio + "  <tr>\n"
//                + "    <td>" + nameUser + " dice: </td>\n"
//                + "    <td>" + txtMensaje.getText() + "</td>\n"
//                + "  </tr>";
//        epCanalTxt.setText(mensaje_inicio + mensaje_medio + mensaje_final);
//
//        this.repaint();
//        txtMensaje.setText("");

        // TODO add your handling code here:
    }//GEN-LAST:event_btnEnviarMouseClicked

    private void lblArchivoMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblArchivoMouseClicked
        // Enviar archivo

    }//GEN-LAST:event_lblArchivoMouseClicked

    private void lblAudioMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblAudioMouseClicked
        // Grabar Audio
    }//GEN-LAST:event_lblAudioMouseClicked

    private void cboUsersCActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboUsersCActionPerformed
        // Dectentando mensaje privado

    }//GEN-LAST:event_cboUsersCActionPerformed

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
            java.util.logging.Logger.getLogger(GUI.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);

        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(GUI.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);

        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(GUI.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);

        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(GUI.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new GUI().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnEnviar;
    private javax.swing.JComboBox<String> cboUsersC;
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
    private javax.swing.JLabel lblArchivo;
    private javax.swing.JLabel lblAudio;
    private javax.swing.JTextField txtMensaje;
    // End of variables declaration//GEN-END:variables
}
