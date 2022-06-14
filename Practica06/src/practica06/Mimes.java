/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package practica06;
import java.util.HashMap;

/**
 *
 * @author Omar
 */
public class Mimes {
    public static HashMap<String, String> mimeTypes;

	public Mimes() {
		mimeTypes = new HashMap<>();
                //            Extension   Tipo en HTML
		mimeTypes.put("doc", "application/msword");
		mimeTypes.put("pdf", "application/pdf");
		mimeTypes.put("rar", "application/x-rar-compressed");
		mimeTypes.put("mp3", "audio/mpeg");
		mimeTypes.put("jpg", "image/jpeg");
		mimeTypes.put("jpeg", "image/jpeg");
		mimeTypes.put("png", "image/png");
		mimeTypes.put("html", "text/html");
		mimeTypes.put("htm", "text/html");
		mimeTypes.put("c", "text/plain");
		mimeTypes.put("txt", "text/plain");
		mimeTypes.put("java", "text/plain");
		mimeTypes.put("mp4", "video/mp4");
                mimeTypes.put("css", "text/css");
                mimeTypes.put("ico", "image/x-icon");
	}

	public String getTipo(String extension) {
		if(mimeTypes.containsKey(extension))
			return mimeTypes.get(extension);
		else
			return "application/octet-stream";
	}    
}
