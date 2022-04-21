/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.TargetDataLine;

/**
 *
 * @author Reina
 */
class JavaSoundRecorder {
    // record duration, in milliseconds
	//static final long RECORD_TIME = 5000;	// 5 seconds
        AudioFormat audioFormat;
        // path of the wav file
	//File wavFile = new File("E:/Test/RecordAudio.wav");
        File wavFile = new File("Audio.wav");

	// format of audio file
	AudioFileFormat.Type fileType = AudioFileFormat.Type.WAVE;

	// the line from which audio data is captured
	TargetDataLine line;
        AudioInputStream ais=null;

	/**
	 * Defines an audio format
	 */
	AudioFormat getAudioFormat() {
		float sampleRate = 8000.0F;//muestras x segundo (ya sea 1 o 2 canales. 8000 muestras x canal)
		int sampleSizeInBits = 16; //#bits usados para almacenar c/muestra (8 o 16 bits valores típicos)
		int channels = 2; //1=mono, 2=stereo
		boolean signed = true; //indica si los datos de la muestra van con signo/sin signo
		boolean bigEndian = false;  //indica el orden de bits(0=little-endian, 1=Big-endian)//importante x tam muestra(1 o 2 bytes)
                /*construye un formato de audio con codificación lineal PCM() con el tamaño de trama especificado al # de bits requeridos para una muestra x canal*/
		AudioFormat format = new AudioFormat(sampleRate, sampleSizeInBits, channels, signed, bigEndian);//codificación PCM(modulación por pulsos codificados)
		return format;
	}

	/**
	 * Captures the sound and record into a WAV file
	 */
	void start(int m) {
		try {
                    BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
                    /**********************************************/
                    //Get and display a list of
                    // available mixers.
                  Mixer.Info[] mixerInfo = AudioSystem.getMixerInfo();
                  //Get everything set up for capture
                  AudioFormat format = getAudioFormat();

                  DataLine.Info info = new DataLine.Info(TargetDataLine.class,format);

                  //Select one of the available
                  // mixers.
                  Mixer mixer = AudioSystem.getMixer(mixerInfo[m]);//3,5
                  //TargetDataLine line;                  
                  // checks if system supports the data line
                  if (!AudioSystem.isLineSupported(info)) {
                        System.out.println("Line not supported");
                        System.exit(0);
                   }//if
                  //Get a TargetDataLine on the selected
                  // mixer.
                  line = (TargetDataLine)mixer.getLine(info);
                  //Prepare the line for use.
			line.open(format);   
			line.start();	// start capturing
//           
			System.out.println("Start capturing...");

			/*AudioInputStream*/ ais = new AudioInputStream(line);
			System.out.println("Start recording...");

			// start recording
			AudioSystem.write(ais, fileType, wavFile);
                        br.close();
                        
		} catch (LineUnavailableException ex) {
			ex.printStackTrace();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}

	/**
	 * Closes the target data line to finish capturing and recording
	 */
	void finish() {
            try{
		line.stop();
		line.close();
                ais.close();
		System.out.println("Finished");
            }catch(Exception e){e.printStackTrace();}
	}
        
        public String getPath(){
            String ruta = wavFile.getAbsolutePath();
            return ruta;
        }
}
