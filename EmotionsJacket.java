import java.io.File;
import java.io.IOException;
import java.util.Scanner;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import gnu.io.UnsupportedCommOperationException;
import SerialCommunication.SerialComm;

public class EmotionsJacket{
	static SerialComm serial = new SerialComm("COM3", 9600);
	
	public static void main(String[] args) throws IOException{
		int i=0;
		long startTime;
		double playbackTime;
		String[][] insts;
		
		//Find and open video file
		Process video = null;
		JFrame jf = new JFrame();
		final JFileChooser fc = new JFileChooser();
		fc.setCurrentDirectory(new File(System.getProperty("user.home")));
		int result = fc.showOpenDialog(jf);
		if (result == JFileChooser.APPROVE_OPTION) {
		    File videoFile = fc.getSelectedFile();
		    File instFile = new File(videoFile.toString()+".txt");
			insts = getInstructions(instFile);
		    ProcessBuilder pb = new ProcessBuilder("C:\\Program Files\\VideoLAN\\VLC\\vlc.exe", videoFile.toString());
		    
		    //Open Serial Communication with Arduino
			try {
				serial.initialize();
				System.out.println("Serial Port Initialized");
			} catch (UnsupportedCommOperationException e) {
				e.printStackTrace();
			}
		    
			video = pb.start();
			startTime = System.currentTimeMillis();
			while(video.isAlive()){
				playbackTime = (System.currentTimeMillis()-startTime)/1000;
				if(i<insts.length && playbackTime >= Double.parseDouble(insts[i][0])){
					System.out.println("Time: "+playbackTime+" sending: "+insts[i][1]);
					serial.write(insts[i][1]);
					i++;
				}
			}
		}
		
		serial.close();
		System.out.println("Serial Communication Closed");
		jf.dispose();
	}
	
	public static String[][] getInstructions(File instFile) throws IOException{
		String line;
		String splitLine[];
		Scanner sc = new Scanner(instFile);
		int numLines = 0;
		while(sc.hasNext()){
			numLines++;
			sc.next();
		}
		String[][] insts = new String[numLines][2];
		sc.close();
		
		sc = new Scanner(instFile);
		for(int i=0; i<numLines; i++){
			line = sc.nextLine();
			splitLine = line.split(":");
			insts[i][0] = splitLine[0];
			insts[i][1] = splitLine[1];
		}
		
		sc.close();
		return insts;
	}
}
