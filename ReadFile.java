import java.io.*;
import java.util.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
public class ReadFile{
	int offset, length;
	String filename, description;
	byte[] buffer; 
	ReadFile(int _offset, int _length, String _filename, String _description) {
		offset = _offset;
		length = _length;
		filename = _filename;
		description = _description;
		buffer = new byte[length];
	}
	public void readBytes() {
		
		// reading a file 
		File file = new File(filename);
		
		try {
		    FileInputStream fin = new FileInputStream(file);



		//creating a Buffer

		fin.skip(offset);
		fin.read(buffer);
		fin.close();
		// return buffer;
		
		} catch (IOException e) {
		    e.printStackTrace();
		}
	}
	public void ToASCII(){
		try{

			String result = new String(buffer, "ASCII");
			// System.out.println(information);
			System.out.println(result);
		}catch(Exception e){}
	
	}

	public void ToDECIMAL() {
		StringBuilder sb = new StringBuilder(buffer.length * 2);
		for(int i=buffer.length-1;i>=0;i--){
			// buffer[i]=03
			// if(buffer[i]==00)
		  sb.append(String.format("%02x", buffer[i] & 0xff));
		}
		String s=sb.toString();
		System.out.print(Integer.parseInt(s, 16));
		System.out.println(" == "+s);
}

}