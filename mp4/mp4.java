public class mp4{
	String filename = "mp4.mp4";
	byte spsData[];
	byte ppsData[];
	int chunkNum[]; // stsc
	int samplePerChunk[]; // stsc
	int sDINum[]; // stsc
	int chunkOffsets[]; //stco
	int sampleSizes[]; // stsz
	int keyFrameSampleNumber[]; //  stss
	int sampleOffsets[]; 

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
				// System.out.println("found "+boxId);
				break;
			}
		}

		return offSet-size;
	}
	public void sampleInfo(int stsdOffset){
		// ReadFile r1 = new ReadFile(stsdOffset, 4,filename);
		// r1.readBytes();
		// System.out.println("stsd size "+ r1.ToDECIMAL());
		
		ReadFile r1 = new ReadFile(stsdOffset+8, 4,filename);
		r1.readBytes();
		// System.out.println("stsd version "+ r1.ToDECIMAL());
		stsdOffset=stsdOffset+4+8;
		//4 bytes number of descriptions 
		r1 = new ReadFile(stsdOffset, 4,filename);
		r1.readBytes();
		// System.out.println("stsd descriptions " +r1.ToDECIMAL());
		stsdOffset=stsdOffset+4;
		
		// 4 bytes description length = long unsigned length
	 	// 4 bytes description visual format = long ASCII text string 'mp4v'
	
		r1 = new ReadFile(stsdOffset, 4,filename);
		r1.readBytes();
		// System.out.println("stsd descriptions length " +r1.ToDECIMAL());
		stsdOffset=stsdOffset+4;

		int avccOffset = stsdOffset-8+ r1.ToDECIMAL();
		// System.out.println(av);
		// for (int i=0;i<120;i+=4 ) {
		r1 = new ReadFile(stsdOffset, 4,filename);
		r1.readBytes();
		// System.out.println("stsd descriptions ASCII " +r1.ToASCII());
		if(r1.ToASCII().equals("avc1")){
			// System.out.println("avc1 format");
		}else{
			// System.out.println("not avc1 format");
		}
		
		stsdOffset=stsdOffset+16;
		// 6 bytes reserved = 48-bit value set to zero
		r1 = new ReadFile(stsdOffset, 6,filename);
		r1.readBytes();
		// System.out.println("reserved zero bits "+r1.ToDECIMAL());
		stsdOffset+=6;
		// 37 skip bytes
		stsdOffset+=37;
		r1 = new ReadFile(stsdOffset, 31,filename);
		r1.readBytes();
		// System.out.println("Encoder info "+r1.ToASCII());
		stsdOffset+=31;
		// 2 bytes video pixel depth = short unsigned bit depth
  //                  - colors are 1 (Monochrome), 2 (4), 4 (16), 8 (256)
  //                  - colors are 16 (1000s), 24 (Ms), 32 (Ms+A)
  //                  - grays are 33 (B/W), 34 (4), 36 (16), 40(256)

		r1 = new ReadFile(stsdOffset, 2,filename);
		r1.readBytes();
		// System.out.println("video pixel depth "+ r1.ToDECIMAL());
		stsdOffset+=2;
		r1 = new ReadFile(stsdOffset, 2,filename);
		r1.readBytes();
		// System.out.println("video color table id  "+ r1.ToDECIMAL());
		stsdOffset+=2;
		// System.out.println("avccOffset == "+avccOffset+" stsdOffset == "+stsdOffset);
		// for (int i=0;i<15 ;i++ ) {
		stsdOffset-=4;
		r1 = new ReadFile(stsdOffset, 1,filename);
		r1.readBytes();
		// System.out.println("configuration version "+ r1.ToDECIMAL());
	// }
		stsdOffset+=1;
		r1 = new ReadFile(stsdOffset, 1,filename);
		r1.readBytes();
		// System.out.println("profile indication  "+ r1.ToDECIMAL());
		stsdOffset+=1;
		r1 = new ReadFile(stsdOffset, 1,filename);
		r1.readBytes();
		// System.out.println("profile compatibility "+ r1.ToDECIMAL());
		stsdOffset+=1;
		r1 = new ReadFile(stsdOffset, 1,filename);
		r1.readBytes();
		// System.out.println("avc level indication "+ r1.ToDECIMAL());
		stsdOffset+=1;

		r1 = new ReadFile(stsdOffset, 1,filename);
		r1.readBytes();
		String bits = r1.TobitString();
		// System.out.println("bits "+bits);
		int nalUnitLengthminusone = Integer.parseInt(""+bits.charAt(6)+bits.charAt(7), 2)+1;
		// System.out.println("nalunit length  "+ nalUnitLengthminusone);
		stsdOffset+=1;
		r1 = new ReadFile(stsdOffset, 1,filename);
		r1.readBytes();
		bits = r1.TobitString();
		// System.out.println("bits "+bits);
		int numOfSeqParametersets = Integer.parseInt(""+bits.charAt(3)+bits.charAt(4)+bits.charAt(5)+bits.charAt(6)+bits.charAt(7), 2);
		// System.out.println("numOfSeqParametersets  "+ numOfSeqParametersets);
		stsdOffset+=1;
		for (int sps=0;sps<numOfSeqParametersets ;sps++ ) {
			r1 = new ReadFile(stsdOffset, 2,filename);
			r1.readBytes();
			int seqParameterLength= r1.ToDECIMAL();
			// System.out.println("seq parameter length "+ seqParameterLength);
			stsdOffset+=2;	
			r1 = new ReadFile(stsdOffset+1, seqParameterLength-1,filename);
			r1.readBytes();
			// System.out.println("numOfSeqParameterset "+ r1.ToHex());
			spsData=r1.Getbytes();
			// System.out.println();
			// System.out.println("one byte == "+r1.Getbyte());
			// System.out.println();
			stsdOffset+=seqParameterLength;

		}
		r1 = new ReadFile(stsdOffset, 1,filename);
		r1.readBytes();
		int numOfPictureParameterSets = r1.ToDECIMAL();
		// System.out.println("numOfPictureParameterSets "+ numOfPictureParameterSets);
		stsdOffset+=1;

		for (int pps = 0; pps<numOfPictureParameterSets;pps++ ) {

			r1 = new ReadFile(stsdOffset, 2,filename);
			r1.readBytes();
			int picParameterLength= r1.ToDECIMAL();
			// System.out.println("pic parameter length "+ picParameterLength);
			stsdOffset+=2;	
			r1 = new ReadFile(stsdOffset+1, picParameterLength-1,filename);
			r1.readBytes();
			// System.out.println("numOfpicParameterset "+ r1.ToHex());
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
		sampleInfo(stsdOffset);
		// int avc1Offset = ReadBox("avc1",stsdOffset+8);
		boxOffset = ReadBox("stsc",stblOffset+8);
		ReadFile chunkCount = new ReadFile(boxOffset+8 + 4, 4,filename);
		chunkCount.readBytes();
		// TOTAL NUM OF CHUNKS (NEED TO RUN A LOOP HERE)
		// System.out.println(chunkCount.ToDECIMAL());
		chunkNum = new int[chunkCount.ToDECIMAL()];
		samplePerChunk = new int[chunkCount.ToDECIMAL()];
		sDINum = new int[chunkCount.ToDECIMAL()];
		for(int i = 0; i < chunkCount.ToDECIMAL(); i++) {
			ReadFile firstChunk = new ReadFile(boxOffset+8 + 4 + 4, 4,filename);
			firstChunk.readBytes(); 
			chunkNum[i] = firstChunk.ToDECIMAL();

			// System.out.println(firstChunk.ToDECIMAL());
			ReadFile sampleInAChunkT = new ReadFile(boxOffset+8 + 4 + 4 + 4, 4,filename);
			sampleInAChunkT.readBytes();
			samplePerChunk[i] = sampleInAChunkT.ToDECIMAL(); 
			// System.out.println(samplePerChunk.ToDECIMAL());
			ReadFile sDI = new ReadFile(boxOffset+8 + 4 + 4 + 4 + 4, 4,filename);
			sDI.readBytes(); // sDI KEA HAI ..?
			sDINum[i] = sDI.ToDECIMAL();
			boxOffset = boxOffset + 12; 
		}
		boxOffset = ReadBox("stco" ,stblOffset + 8);
		ReadFile numOfChunkEnteries = new ReadFile(boxOffset+8 + 4, 4,filename);
		numOfChunkEnteries.readBytes();
		int n = 4;
		chunkOffsets = new int[numOfChunkEnteries.ToDECIMAL()];
		for(int i = 0; i < numOfChunkEnteries.ToDECIMAL(); i++) {

			ReadFile tempChunkOffset = new ReadFile(boxOffset+8 + 4 +n, 4,filename);
			tempChunkOffset.readBytes();
			chunkOffsets[i] = tempChunkOffset.ToDECIMAL();
			// System.out.println("chunkOffset == "+chunkOffset.ToDECIMAL());
			n=n+4;
		}
		// System.out.println(chunkOffsets[0]);
		boxOffset = ReadBox("stsz" ,stblOffset + 8);
		ReadFile sampleSizetemp = new ReadFile(boxOffset+8 + 4, 4,filename);
		sampleSizetemp.readBytes();
		// System.out.println(sampleSize.ToDECIMAL());
		ReadFile sampleCount = new ReadFile(boxOffset+8 + 4+4, 4,filename);
		sampleCount.readBytes();
		sampleSizes = new int[sampleCount.ToDECIMAL()];
		int x = 4;
		// System.out.println(sampleCount.ToDECIMAL());
		for(int i =0; i< sampleCount.ToDECIMAL(); i ++) {
			ReadFile firstSample = new ReadFile(boxOffset+8 + 4+4+x, 4,filename);
			firstSample.readBytes();
			sampleSizes[i] = firstSample.ToDECIMAL();
			x = x + 4;
		}
		// System.out.println(sampleSizes[0]);
		// System.out.println(sampleSizes[1674]);
		ReadFile firstSample = new ReadFile(boxOffset+8 + 4+4+4, 4,filename);
		firstSample.readBytes();
		// System.out.println("firstSample Size == "+firstSample.ToDECIMAL());
		/// stss key frame 
		boxOffset = ReadBox("stss",stblOffset+8);
		ReadFile keyFrame = new ReadFile(boxOffset+8 + 4, 4,filename);
		keyFrame.readBytes();
		keyFrameSampleNumber = new int[keyFrame.ToDECIMAL()];
		x = 4;
		for(int i = 0; i < keyFrame.ToDECIMAL(); i++) {
			ReadFile keyFrameOffset = new ReadFile(boxOffset+8 + 4+x, 4,filename);
			keyFrameOffset.readBytes();
			keyFrameSampleNumber[i] = keyFrameOffset.ToDECIMAL(); 
			x = x + 4;
		}
		sampleOffsets = new int[sampleSizes.length];
		// System.out.println(sampleSizes.length);
		int counter = 0;
		int chunkcount = 0;
		// for(int i = 0; i < chunkOffsets.length; i++) {
		// }
		for(int i = 0; i < chunkCount.ToDECIMAL(); i++) { //
			if(i < (chunkCount.ToDECIMAL() - 1) ) { 
				for(int k = chunkNum[i]; k < chunkNum[i + 1]; k++ ) {
					int tempsize = 0;
					for(int j = 0; j < samplePerChunk[i]; j++) {
						sampleOffsets[counter] = chunkOffsets[chunkcount] + (tempsize);
						tempsize = tempsize + sampleSizes[counter];
						counter++;
						// System.out.print(sampleOffsets[i + j]+" ");
					}
					chunkcount = chunkcount + 1;
				}
			} else {
				 for(int k = chunkNum[i]; k <= chunkOffsets.length; k++ ) {
					int tempsize = 0;
					for(int j = 0; j < samplePerChunk[i]; j++) {
						sampleOffsets[counter] = chunkOffsets[chunkcount] + (tempsize);
						tempsize = tempsize + sampleSizes[counter];
						counter++;
						// System.out.print(sampleOffsets[i + j]+" ");
					}
					chunkcount = chunkcount + 1;
				}
			}
		}
		// System.out.println(chunkCount.ToDECIMAL());
		// System.out.println(chunkNum.length);
		System.out.println(sampleOffsets.length + " lame  ");
		System.out.println(chunkOffsets.length);

		// for (int i = 0;i < sampleOffsets.length; i++ ) {
		System.out.print(sampleOffsets[167] + " ");
		// }
		// }
		// System.out.println("Number of keyFrame = "+keyFrame.ToDECIMAL());
		// System.out.println("offSet of keyFrame = "+keyFrameOffset.ToDECIMAL());
		// ReadFile image = new ReadFile(chunkOffset.ToDECIMAL(),4,filename); /// need to make changes here, chunkoffset here is temp chunk offset
		// image.readBytes(); // need to make change here
		// System.out.println("Relative Time == "+image.ToDECIMAL());		
	}
	mp4(){
		parseMp4();
	}
	// public static void main(String args[]){
	// 	mp4 test = new mp4();
	// 	test.parseMp4();
	// }
}