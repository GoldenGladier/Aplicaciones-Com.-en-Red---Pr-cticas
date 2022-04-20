/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Test;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Collections;
import java.util.Enumeration;

/**
 *
 * @author Reina
 */
public class PruebaInterfaz {

    public static void main(String[] args) {
        NetworkInterface in;
        in = getNetwork();
        System.out.printf("inter " +in.getName() + "\n");
    }

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
}
