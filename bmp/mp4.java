import java.io.*;
import java.util.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.awt.Image;
import java.awt.image.BufferedImage;
import org.jcodec.api.FrameGrab;

public class mp4{
	int ReadBox(String boxId, int offSet){
		String filename = "mp4.mp4";
		int count =100;
		int size=0;
		String type;	
		while(count>0){														
			ReadFile boxSize = new ReadFile(offSet, 4, filename, "information[i]");
			boxSize.readBytes();
			ReadFile boxType = new ReadFile(offSet+4, 4, filename, "information[i]");
			boxType.readBytes();
			type = boxType.ToASCII();
			// System.out.println(type);
			size=boxSize.ToDECIMAL();
			if(size==0){
				// System.out.println("0 size");
				offSet=offSet+4;
				// System.out.println(offSet);
				} else if(size==1){
				// System.out.println("set size to 64 bit");
				} else{
					offSet=offSet+size;
				}
				
			// count--;
			if(type.equals(boxId)){
				// System.out.println(offSet);
				System.out.println("found "+boxId);
				break;
			}
		}

		// System.out.println(size);
		// System.out.println(offSet);
		return offSet-size;
	}
	public static void main(String args[]){
		int frameNumber = 150;
		BufferedImage frame = FrameGrab.getFrame(new File("mp4.mp4"), frameNumber);
		ImageIO.write(frame, "png", new File("frame_150.png"));



		mp4 test = new mp4();
		int ret = test.ReadBox("moov",0);
		// System.out.println(ret);
		ret = test.ReadBox("trak",ret+8);
		ret = test.ReadBox("mdia",ret+8);
		ret = test.ReadBox("minf",ret+8);
		int stblOffset = test.ReadBox("stbl",ret+8);
		ret = test.ReadBox("stsc",stblOffset+8);
		ReadFile chunkCount = new ReadFile(ret+8 + 4, 4,"mp4.mp4", "information[i]");
		chunkCount.readBytes();
		System.out.println(chunkCount.ToDECIMAL());
		ReadFile firstChunk = new ReadFile(ret+8 + 4 + 4, 4,"mp4.mp4", "information[i]");
		firstChunk.readBytes();
		System.out.println(firstChunk.ToDECIMAL());
		ReadFile samplePerChunk = new ReadFile(ret+8 + 4 + 4 + 4, 4,"mp4.mp4", "information[i]");
		samplePerChunk.readBytes();
		System.out.println(samplePerChunk.ToDECIMAL());
		ReadFile sDI = new ReadFile(ret+8 + 4 + 4 + 4 + 4, 4,"mp4.mp4", "information[i]");
		sDI.readBytes();
		System.out.println(sDI.ToDECIMAL());


		ret = test.ReadBox("stco" ,stblOffset + 8);
		ReadFile chunkCount1 = new ReadFile(ret+8 + 4, 4,"mp4.mp4", "information[i]");
		chunkCount1.readBytes();
		System.out.println(chunkCount1.ToDECIMAL());
		// int[] chunkOffsetArray = new [chunkCount1.ToDECIMAL()]
		int n = 4;
		// for(int i=0;i<chunkCount1.ToDECIMAL();i++){
			ReadFile chunkOffset = new ReadFile(ret+8 + 4 +n, 4,"mp4.mp4", "information[i]");
			chunkOffset.readBytes();
			System.out.println("chunkOffset == "+chunkOffset.ToDECIMAL());
			n=n+4;
		// }
		ret = test.ReadBox("stsz" ,stblOffset + 8);
		ReadFile sampleSize = new ReadFile(ret+8 + 4, 4,"mp4.mp4", "information[i]");
		sampleSize.readBytes();
		System.out.println(sampleSize.ToDECIMAL());
		ReadFile sampleCount = new ReadFile(ret+8 + 4+4, 4,"mp4.mp4", "information[i]");
		sampleCount.readBytes();
		System.out.println(sampleCount.ToDECIMAL());
		ReadFile firstSample = new ReadFile(ret+8 + 4+4+4, 4,"mp4.mp4", "information[i]");
		firstSample.readBytes();
		System.out.println("firstSample Size == "+firstSample.ToDECIMAL());

		ReadFile image = new ReadFile(chunkOffset.ToDECIMAL()+8, firstSample.ToDECIMAL(),"mp4.mp4", "information[i]");
		image.readBytes();
		image.ToImage();
		// System.out.println("firstSample Size == "+image.ToDECIMAL());








		
	}
}