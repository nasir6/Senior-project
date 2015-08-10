import java.io.*;
import java.util.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
// System.out.println();
public class bmp{
	int[] offSet = {0, 2, 10, 14, 18, 22, 26, 28, 30, 34, 38, 42, 46, 50};
	int[] len = {2, 4, 4, 4, 4, 4, 2, 2, 4, 4, 4, 4, 4, 4};
	int[] flag = {0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1};
	String filename = "test.bmp";
	String[] information = {"BM", "File Size", 
	"file Offset to Raster Data", "InfoHeader size", 
	"bitmap width", "bitmap height", "Number of planes", 
	"Bits per pixel", "Compression", "Size of compressed Image", 
	"X pixels/meter", "Y pixels/meter", "Num of Used Colors", "Num of Important Colors"};
	bmp(){
		System.out.println(len.length+" == "+offSet.length+" == "+information.length);
		for(int i = 0; i < offSet.length ; i++) {
			ReadFile nasir = new ReadFile(offSet[i], len[i], filename, information[i]);
			nasir.readBytes();
			if(flag[i] == 0) {
				nasir.ToASCII();
			} else {
				nasir.ToDECIMAL();
			}
		}
	}	
} 