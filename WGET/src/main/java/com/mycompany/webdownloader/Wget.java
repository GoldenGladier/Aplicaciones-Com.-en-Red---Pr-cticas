package com.mycompany.webdownloader;
import java.net.URL;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.*; 

public class Wget {
    static ManejadorL DescargaLA = new ManejadorL();
    static final int MAX_T = 5; 
    
    
    public static void main(String[] args) {
        Scanner sc= new Scanner(System.in); //System.in is a standard input stream
        System.out.println("Ingresa el link del sitio web a descargar: ");
        String url= sc.nextLine(); //reads string.
       
        try {
            URL webUrl = new URL(url);
            ExecutorService pool = Executors.newFixedThreadPool(MAX_T);
            pool.execute(new ManejadorA(webUrl, DescargaLA));            
        } catch (Exception e) {
           e.printStackTrace();
        }
        
    }
}
