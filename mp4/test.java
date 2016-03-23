import java.io.BufferedReader;
import java.io.File;
import java.io.*;
import java.util.*;
import java.io.File;
import java.math.BigInteger;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.awt.image.WritableRaster;
import java.awt.Image;
import java.awt.image.BufferedImage;
public class test{
	
	public static void main(String args[]){
		// a.replaceAll("\\s+","");

		try{
			File fout = new File("out.txt");
			FileOutputStream fos = new FileOutputStream(fout);
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));
			FileInputStream fstream = new FileInputStream("table9.5.txt");
			BufferedReader br = new BufferedReader(new InputStreamReader(fstream));
			String strLine;
			while ((strLine = br.readLine()) != null){
				strLine=strLine.replaceAll("\\s+","");
			  	// System.out.println (strLine);
				bw.write(strLine);
				bw.newLine();
			}

			//Close the input stream
			br.close();
			bw.close();

		}catch (IOException e) {
		    e.printStackTrace();
		}
		// try{
			// File fout = new File("out.txt");
			// FileOutputStream fos = new FileOutputStream(fout);
		 
			// BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));
		 
			// for (int i = 0; i < 10; i++) {
			// 	bw.write("something");
			// 	bw.newLine();
			// }
		 
			// bw.close();
		// }catch (IOException e) {
		    // e.printStackTrace();
		// }


		
	}
}