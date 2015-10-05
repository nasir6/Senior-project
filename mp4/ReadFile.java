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
	public byte Getbyte(){
		return buffer[0];
	}
 	public String ToASCII(){
		try{
			String result = new String(buffer, "ASCII");
			// System.out.println(information);
			// System.out.println(result);
			return result;
		}catch(Exception e){

		}
		return "nil";
	}
	public int ToUint(){
		StringBuilder sb = new StringBuilder(buffer.length * 2);
		// for(int i=buffer.length-1;i>=0;i--){
		for (int i=0;i<buffer.length;i++) {
		  sb.append(String.format("%02x", buffer[i] & 0xff));
		}
		String s=sb.toString();
		System.out.println(s);
		return Integer.parseUnsignedInt(s, 16);
		// return Integer.parseInt(s, 16);
	}
	public int ToDECIMAL() {
		StringBuilder sb = new StringBuilder(buffer.length * 2);
		for (int i=0;i<buffer.length;i++) {
		  sb.append(String.format("%02x", buffer[i] & 0xff));
		}
		String s=sb.toString();
		return Integer.parseInt(s, 16);
}

		public boolean[] ToBits() {
			boolean[] result = new boolean[Byte.SIZE*buffer.length];
			int offset = 0;
			for (byte b : buffer) {
			for (int i=0; i<Byte.SIZE; i++) result[i+offset] = (b >> i & 0x1) != 0x0;
			offset+=Byte.SIZE;
			}
			return result;
		}
		public String TobitString(){
			byte b1 = buffer[0];
			String s1 = String.format("%8s", Integer.toBinaryString(b1 & 0xFF)).replace(' ', '0');
			// System.out.println(s1); // 10000001
			return s1;
		}
	public String ToHex(){
		StringBuilder sb = new StringBuilder(buffer.length * 2);
		// for(int i=buffer.length-1;i>=0;i--){
		for (int i=0;i<buffer.length;i++) {
			// buffer[i]
			// System.out.println(buffer[i]);
		  sb.append(String.format("%02x", buffer[i] & 0xff));
			// sb.append(buffer[i]);
		}
		String s=sb.toString();
		// System.out.print(Integer.parseInt(s, 16));
		// System.out.println(" == "+s);
		return s;
	} 
	public void ToImage(){
		// System.out.println(buffer[2]);
		// int a;
		// for (int k =0;k<buffer.length ;k++ ) {
		// 	System.out.print(buffer[k]);
		// }
		// boolean[] ret = convert(buffer);

		// for(int i=0;i<ret.length;i++){
			// if(ret[i]==false){
				// System.out.print(0);
			// }else{
				// System.out.print(1);
			// }
			// System.out.print(ret[i]);
		// }
		int[] intArray = new int[buffer.length];
		System.out.println(intArray.length);
	    int byteIdx = 0;
	    for (int pixel = 0; pixel < intArray.length;pixel++) {
	        // int rByte = (int) buffer[byteIdx++] & 0xFF;
	        // int gByte = (int) buffer[byteIdx++] & 0xFF;
	        // int bByte = (int) buffer[byteIdx++] & 0xFF;
	        // int rgb = (rByte << 16) | (gByte << 8) | bByte;
	        intArray[pixel] = (int)buffer[pixel]&0XFF;
	    }
	    // intArray = (int)buffer;

    BufferedImage image = null;
    // if(depth == 8) {
    int w=16;
    int h=8;
        image = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
    // }

    WritableRaster raster = (WritableRaster) image.getData();
    raster.setPixels(0, 0, w, h, intArray);
    image.setData(raster);
	// }
     byte[] imageData = null;
    try {
        ByteArrayOutputStream bas = new ByteArrayOutputStream();
        ImageIO.write(image, "bmp", new File("image.png"));
        imageData = bas.toByteArray();
        bas.close();
    } catch (Exception e) {
        e.printStackTrace();
    }
		// System.out.println(buffer);
		for (int i=0;i<buffer.length;i++){
			// System.out.print(" "+buffer[i]);
		}
		System.out.println(buffer.length);
		// byte[] b = {-119, 80, 78, 71, 13, 10, 26, 10, 0, 0, 0, 13, 73, 72, 68, 82,
		//       0, 0, 0, 15, 0, 0, 0, 15, 8, 6, 0, 0, 0, 59, -42, -107,
		//       74, 0, 0, 0, 64, 73, 68, 65, 84, 120, -38, 99, 96, -64, 14, -2,
		//       99, -63, 68, 1, 100, -59, -1, -79, -120, 17, -44, -8, 31, -121, 28, 81,
		//       26, -1, -29, 113, 13, 78, -51, 100, -125, -1, -108, 24, 64, 86, -24, -30,
		//       11, 101, -6, -37, 76, -106, -97, 25, 104, 17, 96, -76, 77, 97, 20, -89,
		//       109, -110, 114, 21, 0, -82, -127, 56, -56, 56, 76, -17, -42, 0, 0, 0,
		//       0, 73, 69, 78, 68, -82, 66, 96, -126};
		// try {
		// 	InputStream in = new ByteArrayInputStream(buffer);
		// 	BufferedImage bImageFromConvert = ImageIO.read(in);
			// ImageIO.write(bImageFromConvert, "png", new File(
			// 			"image.png"));

		// 	} catch (IOException e) {
		// 		System.out.println(e.getMessage());
		// 	}


	}

}