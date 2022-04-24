/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
//package Test;
//
//import java.io.ByteArrayInputStream;
//import java.io.DataInputStream;
//import java.io.DataOutputStream;
//import java.io.File;
//import java.io.FileNotFoundException;
//import java.io.FileOutputStream;
//import java.io.IOException;
//import java.net.DatagramPacket;
//import java.net.MulticastSocket;
//import javax.swing.JFrame;
//import Test.GUI1;
//
///**
// *
// * @author Reina
// */
//public class Recibe extends Thread {
//
//        MulticastSocket s;
//        JFrame componente;
//
//        public Recibe(MulticastSocket m, JFrame componente) {
//            this.s = m;
//            this.componente = componente;
//        }
//
//        public void recibeArchivo(long size, int totalFragments, String nombre) throws FileNotFoundException, IOException {
//            System.out.println("\nSe recibe el archivo " + nombre + " con " + size + "bytes");
//            DataOutputStream dos = new DataOutputStream(new FileOutputStream(nombre)); // OutputStream
//
//            long recibidos = 0;
//            int n = 0, porciento = 0;
//            //byte[] b = new byte[65000];
//            int nFragment = 0;
//            int totalFragments_;
//            int sizeFragment;
//            while (nFragment < totalFragments) {
//                byte[] b = new byte[65535];
//                DatagramPacket p = new DatagramPacket(b, b.length);
//                s.receive(p);
//
//                // DATAGRAMA (Structure: No. fragment, No. f. totals, size fragment, fragment)
//                DataInputStream dis = new DataInputStream(new ByteArrayInputStream(p.getData()));
//                int hash_dest = dis.readInt();
//                if (hash_dest == 0 || hash_dest == origen_hash) {
//                    int hash_orig = dis.readInt();
//                    int tipo = dis.readInt();
//                    if (tipo == 4) {
//                        nFragment = dis.readInt();
//                        totalFragments_ = dis.readInt();
//                        sizeFragment = dis.readInt();
//                        byte[] bMsg = new byte[sizeFragment];
//                        n = dis.read(bMsg);
//
//                        //n = dis.read(b);
//                        dos.write(b, 0, n);
//                        dos.flush();
//                        recibidos++;
//                        porciento = (int) ((recibidos * 100) / totalFragments);
//                        System.out.println("\r Recibiendo el " + porciento + "% ---- " + nFragment + "/" + totalFragments);
//                    }
//
//                } else {
//                    System.out.println("\nSe ha descartado el datagrama ");
//                }
//
//                dis.close();
//
//            } // while
//
//            if (recibidos == totalFragments) {
//                System.out.println("\nArchivo recibido exitosamente ");
//            }
//
//            System.out.println("\nArchivo " + nombre + " de tamanio: " + size + " recibido.");
//            dos.close();
//        }
//
//        public void run() {
//            try {
//
//                File f = new File("");
//                String ruta = f.getAbsolutePath();
//                String carpeta = "misArchivos\\" + nameUser;
//                String ruta_archivos = ruta + "\\" + carpeta + "\\";
//                System.out.println("ruta:" + ruta_archivos);
//                File f2 = new File(ruta_archivos);
//                f2.mkdirs();
//                f2.setWritable(true);
//
//                for (;;) {
//                    byte[] b = new byte[65000]; //https://en.wikipedia.org/wiki/User_Datagram_Protocol#:~:text=The%20field%20size%20sets%20a,20%2Dbyte%20IP%20header).
//                    DatagramPacket p = new DatagramPacket(b, b.length);
//                    s.receive(p);
//
//                    DataInputStream dis = new DataInputStream(new ByteArrayInputStream(p.getData()));
//                    int hash_dest = dis.readInt();
//                    int hash_orig = dis.readInt();
//                    if (hash_dest == 0 || hash_dest == origen_hash || hash_orig == origen_hash) {
//                        int tipo = dis.readInt();
//                        if (tipo == 1 && hash_orig != origen_hash) {//mensaje inicio
//                            System.out.println("Recibiendo mensaje tipo 1");
//                            String name = dis.readUTF();
//                            System.out.println("Nombre: " + name);
//                            System.out.println("origen: " + hash_orig);
//                            System.out.println("destino: " + hash_dest);
//                            System.out.println("hash nombre: " + name.hashCode());
//                            if (!usuarios.containsKey(name)) {
//                                System.out.println("usuario añadido");
//                                usuarios.put(name, hash_orig);
//                                usuarios_i.put(hash_orig, name);
//                                cboUsers.addItem(name);
//                            } else {
//                                System.out.println("usuario encontrado");
//                                System.out.println(usuarios.get(name));
//
//                            }
//                            System.out.println("Mappings of HashMap usuarios are : "
//                                    + usuarios);
//                            send_msg_5(origen_hash, nameUser);
//                        } else if (tipo == 2) {//mensaje texto
//                            System.out.println("Recibiendo mensaje tipo 2");
//                            if (!usuarios.containsValue(hash_orig)) { //Si no esta registrado no lee el mensaje
//                                System.out.println("Usuario no registrado, por tanto no se lee el mensaje");
//                                //usuarios
//                                continue;
//                            }
//                            int sizeFragment = dis.readInt();
//                            byte[] bMsg = new byte[sizeFragment];
//                            int x = dis.read(bMsg);
//                            String msg = new String(bMsg);
//
//                            System.out.println(msg);
//                            msg = formatEmojis(msg);
//                            //mostrar mensaje en ventana
//                            //tmp_m = txtMensaje.getText();
//                            if (hash_dest == 0) {
//                                mensaje_medio = mensaje_medio + "  <tr>\n"
//                                        + "    <td>" + usuarios_i.get(hash_orig) + " dice: </td>\n"
//                                        + "    <td>" + msg + "</td>\n"
//                                        + "  </tr>";
//                            } else {
//                                mensaje_medio = mensaje_medio + "  <tr>\n"
//                                        //+ "    <td>privado</td>\n"
//                                        + "    <td>" + usuarios_i.get(hash_orig) + " dice: </td>\n"
//                                        + "    <td>" + msg + "</td>\n"
//                                        + "  </tr>";
//                            }
//
//                            epCanalTxt.setText(mensaje_inicio + mensaje_medio + mensaje_final);
//
//                            componente.repaint();
//                        } else if (tipo == 3) {//mensaje para recibir archivo
//                            // Ejecutar funcion #1
//                            long size = dis.readLong();
//                            System.out.println("Tamaño de archivo: " + size);
//                            int totalFragments = dis.readInt();
//                            System.out.println("Total Fragmentos: " + totalFragments);
//                            String name = dis.readUTF();
//                            System.out.println("Nombre de archivo: " + name);
//
//                            if (hash_dest == 0) {
//                                mensaje_medio = mensaje_medio + "  <tr>\n"
//                                        + "    <td>" + usuarios_i.get(hash_orig) + " envia: </td>\n"
//                                        + "    <td>" + name + "</td>\n"
//                                        + "  </tr>";
//                            } else {
//                                mensaje_medio = mensaje_medio + "  <tr>\n"
//                                        //+ "    <td>privado</td>\n"
//                                        + "    <td>" + usuarios_i.get(hash_orig) + " envia: </td>\n"
//                                        + "    <td>" + name + "</td>\n"
//                                        + "  </tr>";
//                            }
//
//                            epCanalTxt.setText(mensaje_inicio + mensaje_medio + mensaje_final);
//
//                            componente.repaint();
//                            if (hash_orig != origen_hash) {
//                                recibeArchivo(size, totalFragments, ruta_archivos + name);
//                            }
//                        } else if (tipo == 5 && hash_orig != origen_hash) {//mensaje inicio
//                            System.out.println("Recibiendo mensaje tipo 1");
//                            String name = dis.readUTF();
//                            System.out.println("Nombre: " + name);
//                            System.out.println("origen: " + hash_orig);
//                            System.out.println("destino: " + hash_dest);
//                            System.out.println("hash nombre: " + name.hashCode());
//                            if (!usuarios.containsKey(name)) {
//                                System.out.println("usuario añadido");
//                                usuarios.put(name, hash_orig);
//                                usuarios_i.put(hash_orig, name);
//                                cboUsers.addItem(name);
//                            } else {
//                                System.out.println("usuario encontrado");
//                                System.out.println(usuarios.get(name));
//
//                            }
//                            System.out.println("Mappings of HashMap usuarios are : "
//                                    + usuarios);
//                        }
//                    }
//                    dis.close();
//                } //for
//            } catch (Exception e) {
//                e.printStackTrace();
//            }//catch
//        }//run
//    }//class
