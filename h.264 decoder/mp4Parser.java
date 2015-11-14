public class mp4Parser{
	String filename = "mp4.mp4";
	byte spsData[];
	byte ppsData[];
	int ReadBox(String boxId, int offSet){
		int count =100;
		int size=0;
		String type;
		while(count>0){													
			ReadFile boxSize = new ReadFile(offSet, 4, filename);
			boxSize.readBytes();
			ReadFile boxType = new ReadFile(offSet+4, 4, filename);
			boxType.readBytes();
			type = boxType.ToASCII();
			size=boxSize.ToDECIMAL();
			if(size==0){
				offSet=offSet+4;
				} else if(size==1){
				} else{
					offSet=offSet+size;
				}
			if(type.equals(boxId)){
				System.out.println("found "+boxId);
				break;
			}
		}
		return offSet-size;
	}
	public void sampleInfo(int stsdOffset){
		// extarct first two nal units of sps and pps	
		ReadFile r1 = new ReadFile(stsdOffset+8, 4,filename);
		r1.readBytes();
		System.out.println("stsd version "+ r1.ToDECIMAL());
		stsdOffset=stsdOffset+4+8;
		//4 bytes number of descriptions 
		r1 = new ReadFile(stsdOffset, 4,filename);
		r1.readBytes();
		System.out.println("stsd descriptions " +r1.ToDECIMAL());
		stsdOffset=stsdOffset+4;
		
		// 4 bytes description length = long unsigned length
	 	// 4 bytes description visual format = long ASCII text string 'mp4v'
	
		r1 = new ReadFile(stsdOffset, 4,filename);
		r1.readBytes();
		System.out.println("stsd descriptions length " +r1.ToDECIMAL());
		stsdOffset=stsdOffset+4;

		int avccOffset = stsdOffset-8+ r1.ToDECIMAL();
		r1 = new ReadFile(stsdOffset, 4,filename);
		r1.readBytes();
		if(r1.ToASCII().equals("avc1")){
			System.out.println("avc1 format");
		}else{
			System.out.println("not avc1 format");
		}
		
		stsdOffset=stsdOffset+16;
		// 6 bytes reserved = 48-bit value set to zero
		r1 = new ReadFile(stsdOffset, 6,filename);
		r1.readBytes();
		System.out.println("reserved zero bits "+r1.ToDECIMAL());
		stsdOffset+=6;
		// 37 skip bytes
		stsdOffset+=37;
		r1 = new ReadFile(stsdOffset, 31,filename);
		r1.readBytes();
		System.out.println("Encoder info "+r1.ToASCII());
		stsdOffset+=31;
		// 2 bytes video pixel depth = short unsigned bit depth
  //                  - colors are 1 (Monochrome), 2 (4), 4 (16), 8 (256)
  //                  - colors are 16 (1000s), 24 (Ms), 32 (Ms+A)
  //                  - grays are 33 (B/W), 34 (4), 36 (16), 40(256)

		r1 = new ReadFile(stsdOffset, 2,filename);
		r1.readBytes();
		System.out.println("video pixel depth "+ r1.ToDECIMAL());
		stsdOffset+=2;
		r1 = new ReadFile(stsdOffset, 2,filename);
		r1.readBytes();
		System.out.println("video color table id  "+ r1.ToDECIMAL());
		stsdOffset+=2;
		System.out.println("avccOffset == "+avccOffset+" stsdOffset == "+stsdOffset);
		// for (int i=0;i<15 ;i++ ) {
		stsdOffset-=4;
		r1 = new ReadFile(stsdOffset, 1,filename);
		r1.readBytes();
		System.out.println("configuration version "+ r1.ToDECIMAL());
	// }
		stsdOffset+=1;
		r1 = new ReadFile(stsdOffset, 1,filename);
		r1.readBytes();
		System.out.println("profile indication  "+ r1.ToDECIMAL());
		stsdOffset+=1;
		r1 = new ReadFile(stsdOffset, 1,filename);
		r1.readBytes();
		System.out.println("profile compatibility "+ r1.ToDECIMAL());
		stsdOffset+=1;
		r1 = new ReadFile(stsdOffset, 1,filename);
		r1.readBytes();
		System.out.println("avc level indication "+ r1.ToDECIMAL());
		stsdOffset+=1;

		r1 = new ReadFile(stsdOffset, 1,filename);
		r1.readBytes();
		String bits = r1.TobitString();
		// System.out.println("bits "+bits);
		int nalUnitLengthminusone = Integer.parseInt(""+bits.charAt(6)+bits.charAt(7), 2)+1;
		System.out.println("nalunit length  "+ nalUnitLengthminusone);
		stsdOffset+=1;
		r1 = new ReadFile(stsdOffset, 1,filename);
		r1.readBytes();
		bits = r1.TobitString();
		// System.out.println("bits "+bits);
		int numOfSeqParametersets = Integer.parseInt(""+bits.charAt(3)+bits.charAt(4)+bits.charAt(5)+bits.charAt(6)+bits.charAt(7), 2);
		System.out.println("numOfSeqParametersets  "+ numOfSeqParametersets);
		stsdOffset+=1;
		for (int sps=0;sps<numOfSeqParametersets ;sps++ ) {
			r1 = new ReadFile(stsdOffset, 2,filename);
			r1.readBytes();
			int seqParameterLength= r1.ToDECIMAL();
			System.out.println("seq parameter length "+ seqParameterLength);
			stsdOffset+=2;	
			r1 = new ReadFile(stsdOffset, seqParameterLength,filename);
			r1.readBytes();
			System.out.println("numOfSeqParameterset "+ r1.ToHex());
			spsData=r1.Getbytes();
			stsdOffset+=seqParameterLength;
		}
		r1 = new ReadFile(stsdOffset, 1,filename);
		r1.readBytes();
		int numOfPictureParameterSets = r1.ToDECIMAL();
		System.out.println("numOfPictureParameterSets "+ numOfPictureParameterSets);
		stsdOffset+=1;

		for (int pps = 0; pps<numOfPictureParameterSets;pps++ ) {

			r1 = new ReadFile(stsdOffset, 2,filename);
			r1.readBytes();
			int picParameterLength= r1.ToDECIMAL();
			System.out.println("pic parameter length "+ picParameterLength);
			stsdOffset+=2;	
			r1 = new ReadFile(stsdOffset, picParameterLength,filename);
			r1.readBytes();
			System.out.println("numOfpicParameterset "+ r1.ToHex());
			ppsData=r1.Getbytes();
			stsdOffset+=picParameterLength;	
		}
	}
	public void parseMp4(){
		int boxOffset = ReadBox("moov",0);
		boxOffset = ReadBox("trak",boxOffset+8);
		boxOffset = ReadBox("mdia",boxOffset+8);
		boxOffset = ReadBox("minf",boxOffset+8);
		int stblOffset = ReadBox("stbl",boxOffset+8);
		int stsdOffset = ReadBox("stsd",stblOffset+8);
		// to get sps and pps
		sampleInfo(stsdOffset);
		// int avc1Offset = ReadBox("avc1",stsdOffset+8);
		boxOffset = ReadBox("stsc",stblOffset+8);
		ReadFile chunkCount = new ReadFile(boxOffset+8 + 4, 4,filename);
		chunkCount.readBytes();
		System.out.println(chunkCount.ToDECIMAL());
		ReadFile firstChunk = new ReadFile(boxOffset+8 + 4 + 4, 4,filename);
		firstChunk.readBytes();
		System.out.println(firstChunk.ToDECIMAL());
		ReadFile samplePerChunk = new ReadFile(boxOffset+8 + 4 + 4 + 4, 4,filename);
		samplePerChunk.readBytes();
		System.out.println(samplePerChunk.ToDECIMAL());
		ReadFile sDI = new ReadFile(boxOffset+8 + 4 + 4 + 4 + 4, 4,filename);
		sDI.readBytes();
		System.out.println(sDI.ToDECIMAL());
		boxOffset = ReadBox("stco" ,stblOffset + 8);
		ReadFile chunkCount1 = new ReadFile(boxOffset+8 + 4, 4,filename);
		chunkCount1.readBytes();
		System.out.println(chunkCount1.ToDECIMAL());
		int n = 4;
		ReadFile chunkOffset = new ReadFile(boxOffset+8 + 4 +n, 4,filename);
		chunkOffset.readBytes();
		System.out.println("chunkOffset == "+chunkOffset.ToDECIMAL());
		n=n+4;
		boxOffset = ReadBox("stsz" ,stblOffset + 8);
		ReadFile sampleSize = new ReadFile(boxOffset+8 + 4, 4,filename);
		sampleSize.readBytes();
		System.out.println(sampleSize.ToDECIMAL());
		ReadFile sampleCount = new ReadFile(boxOffset+8 + 4+4, 4,filename);
		sampleCount.readBytes();
		System.out.println(sampleCount.ToDECIMAL());
		ReadFile firstSample = new ReadFile(boxOffset+8 + 4+4+4, 4,filename);
		firstSample.readBytes();
		System.out.println("firstSample Size == "+firstSample.ToDECIMAL());
		/// stss key frame 
		boxOffset = ReadBox("stss",stblOffset+8);
		ReadFile keyFrame = new ReadFile(boxOffset+8 + 4, 4,filename);
		keyFrame.readBytes();
		System.out.println("Number of keyFrame = "+keyFrame.ToDECIMAL());

		ReadFile keyFrameOffset = new ReadFile(boxOffset+8 + 4+4, 4,filename);
		keyFrameOffset.readBytes();
		System.out.println("offSet of keyFrame = "+keyFrameOffset.ToDECIMAL());
		ReadFile image = new ReadFile(chunkOffset.ToDECIMAL(),4,filename);
		image.readBytes();
		System.out.println("Relative Time == "+image.ToDECIMAL());		
	}
	mp4Parser(String filename_){
		filename=filename_;
		parseMp4();
	}
	public static void main(String args[]){
		mp4Parser test = new mp4Parser("mp4.mp4");
		test.parseMp4();

	}
}