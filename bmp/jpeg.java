import java.util.*;
//Integer.toString(100,2)	
public class jpeg{
	String[] htcodes00,htSymbols00; // ht number = 0 ht type = DC 0
	String[] htcodes01,htSymbols01; // ht number = 0 ht type = AC 1
	String[] htcodes10,htSymbols10; // ht number = 1 ht type = DC 0
	String[] htcodes11,htSymbols11; // ht number = 1 ht type = AC 1
	String[] htToUseType= new String[6];
	int[] dcACvalues = new int[64];
	int[] dqtValues = new int[64];
	int[] dequantizedValues=new int[64];
	int[] deZigZagged = new int[64];
	String streamData="";
	int offSet = 0;
	String fileName = "jpeg.jpg";
	int matchedAt = 0; // while parsing the data stream it is useful

	public int bitsToDecimal(String s){
		// s = "0111111111";
		int u=0;
		String max ="";
		for(int max_i=0;max_i<s.length();max_i++){
			max=max+1;
		}
		int max_num = Integer.parseInt(max, 2);
		if(s.charAt(0)=='0'){
			// System.out.println("yup");
			u= Integer.parseInt(s, 2);
			// u=-1*(u+1);
			u=u-max_num;
		} else{
			u= Integer.parseInt(s, 2);
		}
		// 
		// System.out.println(u);
		return u;
	}
	public int match(String[] htTable, String input)
	{
		for (int i=0;i<htTable.length ;i++ ) {
			// System.out.println(htTable[i]);
			if(htTable[i].equals(input)){

				return i;
			}
			if(htTable[i].length()>input.length()){
				return -1;
			}
		}
		return -1;
	}
	public class SOF0{
		String marker; // 2 bytes of ffc0
		int length; // 2 bytes 
		int bitsPerSample; // 1 byte for bits per sample
		int imageHeight, width; // 2 bytes each
		int numOfComponents; // 1 byte 
		int eachComponent; // Read each component data of 3 bytes. 
		                                                        // It contains,(component Id(1byte)(1 = Y, 2 = Cb, 3 = Cr, 4 = I, 5 = Q),   

	                                                         //      sampling factors (1byte) (bit 0-3 vertical., 4-7 horizontal.),

	                                                         //     quantization table number (1 byte)). 
	}
	public class DHT{
		String marker; // 2 bytes 
		int length; // 2 bytes 
		boolean type;
		int numberOfHT;	  // int htInformation; // bit 0..3 : number of HT 0..3, otherwise error)
	                      //  bit 4     : type of HT, 0 = DC table, 1 = AC table
	                      // bit 5..7 : not used, must be 0 
		boolean[] htInformation = new boolean[8];
		int[] numOfSymbols= new int[16]; //  16 bytes, Number of symbols with codes of length 1..16,
	                      // the sum.n. of these bytes is the total number of codes,
	                      // which must be <= 256 
		String[] symbols; //  n bytes Table containing the symbols in order of increasing
	                 	  // code length ( n = total number of codes ).   
		String[] codes;
		public void ReaderDHT(){
			// System.out.println("DHT OffSet -> "+offSet);
			int set = offSet;//
			ReadFile dhtLength = new ReadFile(set,2,fileName,"lame");
			dhtLength.readBytes();
			length = dhtLength.ToDECIMAL();
			// System.out.println(length);
			set=set+2;
			ReadFile dhtinfo = new ReadFile(set,1,fileName,"lame");
			dhtinfo.readBytes();
			htInformation=dhtinfo.ToBits();
			set=set+1;
			int totalSymbols=0;
			for(int j=0;j<16;j++){
				ReadFile dhtnos = new ReadFile(set,1,fileName,"lame");
				dhtnos.readBytes();
				numOfSymbols[j]=dhtnos.ToDECIMAL();
				// System.out.print(numOfSymbols[j]+"  ");
				set=set+1;
				totalSymbols=totalSymbols+numOfSymbols[j];
			// htInformation=dhtnos.ToHex();
			}
			// System.out.println("total symbols = "+totalSymbols);
			symbols=new String[totalSymbols];
			for (int k=0;k<totalSymbols ;k++ ) {
				ReadFile dhtsymbol = new ReadFile(set,1,fileName,"lame");
				dhtsymbol.readBytes();
				// numOfSymbols[j]=
				set=set+1;
				// System.out.println(dhtsymbol.ToHex());
				symbols[k]=dhtsymbol.ToHex();
				// System.out.print(symbols[k]+"  ");
			}
			codes=new String[totalSymbols];
			int code=0;
			int index=0;
			for(int l=0;l<16;l++){
				for(int m=0;m<numOfSymbols[l];m++){
					String c=Integer.toString(code,2);
					for(int s=c.length();s<=l;s++){
						c="0"+c;
					}
					codes[index]=c;// 
					index=index+1;
					code=code+1;
				}
				code=code*2;
			}
			// System.out.println(length);
			type = htInformation[4];
			if(htInformation[0]==false){
				if(htInformation[1]==false){
					numberOfHT=0;
				}
			}
			if(htInformation[0]==true){
				if(htInformation[1]==false){
					numberOfHT=1;
				}
			}
			if(htInformation[0]==false){
				if(htInformation[1]==true){
					numberOfHT=2;
				}
			}
			if(htInformation[0]==true){
				if(htInformation[1]==true){
					numberOfHT=3;
				}
			}
		// System.out.println("codes");
		// System.out.println();
		// for (int a=0;a<totalSymbols ;a++ ) {
		// 	System.out.print("  "+codes[a]);
			
		// }
		// System.out.println();
		// System.out.println();
		// System.out.println("ht number -> "+numberOfHT);
		// System.out.println("ht type -> "+type);
		}


	}
	public class DRI{
		String marker; // 2 bytes
		int length; // 2 bytes 
		int restartInterval; // 2 bytes This is in units of MCU blocks, means that every n MCU

	                        // blocks a RSTn marker can be found. The first marker will

	                        // be RST0, then RST1 etc, after RST7 repeating from RST0.

	}
	public class DQT {
		String marker; //  2 bytes ffdb
		int length; // 2 bytes 
		boolean[] qtInformation = new boolean[8]; //  bit 0..3: number of QT [0..3, otherwise error]

	                       // bit 4..7: precision of QT, 0 = 8 bit, otherwise 16 biter

		int numberOfQt;
		int precisionOfQt;
		int[] bytes = new int[64]; // This gives QT values, n = 64*(precision+1)
		public void ReaderDQT() {

			int length = 0;
			int set = offSet;			
			ReadFile readLength = new ReadFile(set,2, fileName, "lame");
			readLength.readBytes();
			set=set+2;
			length = readLength.ToDECIMAL();
			ReadFile readQTinfo = new ReadFile(set,1, fileName, "lame");
			readQTinfo.readBytes();
			qtInformation = readQTinfo.ToBits();
			String num = "";
			String precision = "";
			for(int i = 0; i < 4; i++) {
				if(qtInformation[i] == false) {
					num = num + '0';
				} else {
					num = num + '1';
				}
			}
			for(int i =4; i < 8; i++) {
				if(qtInformation[i] == false) {
					precision = precision + '0';
				} else {
					precision = precision + '1';
				}
			}
			numberOfQt = Integer.parseInt(num, 2);
			precisionOfQt = Integer.parseInt(precision, 2);
			System.out.println("numberOfQt====>"+numberOfQt);
			System.out.println("precisionOfQt===> "+precisionOfQt);
			set =  set + 1;
			for (int byte_num=0;byte_num<64;byte_num++ ) {
				ReadFile readByte = new ReadFile(set,1, fileName, "lame");
				readByte.readBytes();
				bytes[byte_num] = readByte.ToDECIMAL();
				set=set+1;
				// System.out.print(" "+bytes[byte_num]);
			}


		}


	}

	public class SOS{

		String marker; //  2 bytes ffda
		int length; // 2 bytes 
		int numOfComponentsinScan; // 1 byte This must be >= 1 and <=4 (otherwise error), usually 1 or 3
		String componentDetails;  // For each component, read 2 bytes. It contains,
	                            // 1 byte   Component Id (1=Y, 2=Cb, 3=Cr, 4=I, 5=Q),
	                            // 1 byte   Huffman table to use :  bit 0..3 : AC table (0..3) bit 4..7 : DC table (0..3)
		// 3 ignorable bytes 
		public void Reader(){
			int set = offSet;			
			ReadFile readLength = new ReadFile(set,2, fileName, "lame");
			readLength.readBytes();
			set=set+2;
			length = readLength.ToDECIMAL();
			// System.out.println(length);
			ReadFile readnoc = new ReadFile(set,1, fileName, "lame");
			readnoc.readBytes();
			set =  set + 1;
			numOfComponentsinScan = readnoc.ToDECIMAL();
			System.out.println("numOfComponentsinScan ==  " + numOfComponentsinScan);
			int next=0;
			for (int i = 0;i<numOfComponentsinScan ;i++ ) {
				ReadFile id = new ReadFile(set,1, fileName, "lame");
				id.readBytes();
				htToUseType[next]=id.ToHex();
				// System.out.println("id == "+id.ToHex());
				// System.out.println(id.ToDECIMAL());
				set=set+1;	
				next=next+1;
				ReadFile ht = new ReadFile(set,1, fileName, "lame");
				ht.readBytes();
				// System.out.println("ht == "+ht.ToHex());
				set=set+1;
				htToUseType[next]=ht.ToHex();
				next=next+1;
			}
			// ignoring 3 bytes
			set=set+3;
			while(true){
				ReadFile sOsData = new ReadFile(set,1, fileName, "lame");
				sOsData.readBytes();
				String d= sOsData.ToHex();
				// System.out.print(" "+d);
				set=set+1;
				if(d.equals("ff")){
					sOsData = new ReadFile(set,1, fileName, "lame");
					sOsData.readBytes();
					d= sOsData.ToHex();

					if(d.equals("d9")){
						// System.out.println(d);
						break;
					}
					if(d.equals("00")){
						// System.out.println("oo aya hai");
						set=set+1;
					}

				}
				String str = Integer.toString(sOsData.ToDECIMAL(),2);
				for(int x=str.length();x<8;x++){
					str='0'+str;
				}
				// System.out.println(str);
				streamData=streamData+str;
				
			}
		}
	}
	public class APP0{
		String marker; // 2 bytes ffe0
		int length; //  2 bytes 
		String fileIdentifierMark; // 5 bytes // This identifies JFIF.

	                                          // 'JFIF'#0 (0x4a, 0x46, 0x49, 0x46, 0x00) 
		int majorRevNum; // 1 byte
		int minorRevNum; // 1 byte
		int xbyYUnit; //  1 byte 
		int xDensity; // 2 bytes 
		int yDensity; //  2 bytes
	}
	public class SOI{
		String marker; // ffd8 2 bytes
		public boolean fileCheck() {
			ReadFile readSoi = new ReadFile(offSet,2, fileName, "lame");
			readSoi.readBytes();
			offSet =  offSet + 2; 
			marker = readSoi.ToHex();
			if(marker.equals("ffd8")) {
				return true;

			} else {
				return false;
			}
		}

	}
	public int nextMarker(String _marker, int offset) {
		boolean flag = true;
		String marker;
		while(true) {
			ReadFile readSoi = new ReadFile(offset,1, fileName, "lame");
			readSoi.readBytes();
			offset =  offset + 1; 
			marker = readSoi.ToHex();
			if(marker.equals("ff")) {
				readSoi = new ReadFile(offset,1, fileName, "lame");
				readSoi.readBytes();
				offset =  offset + 1; 
				marker = readSoi.ToHex();
				if(marker.equals(_marker)) {
					return offset;
				} else if(marker.equals("d9")) {
					return -9;
				}
			}
		}
	}
	public void extractDQT(String _filename) {
		fileName = _filename;
		offSet=nextMarker("db",2);
		// System.out.println("offSet =====>"+offSet);
		DQT _DQT = new DQT();
		_DQT.ReaderDQT();
		dqtValues=Arrays.copyOf(_DQT.bytes,_DQT.bytes.length);
	}
	public void extractHtSOS(String _fileName) {
		fileName =  _fileName;
		SOI _soi = new SOI();
		// System.out.println(_soi.fileCheck());
		offSet=nextMarker("c4",2);
		DHT _dht = new DHT();
		_dht.ReaderDHT();
		htcodes00=Arrays.copyOf(_dht.codes,_dht.codes.length);
		htSymbols00=Arrays.copyOf(_dht.symbols,_dht.symbols.length);
		offSet=nextMarker("c4",offSet);
		DHT _2dht = new DHT();
		_2dht.ReaderDHT();
		htcodes01=Arrays.copyOf(_2dht.codes,_2dht.codes.length);
		htSymbols01=Arrays.copyOf(_2dht.symbols,_2dht.symbols.length);
		offSet=nextMarker("c4",offSet);
		DHT _3dht = new DHT();
		_3dht.ReaderDHT();
		htcodes10=Arrays.copyOf(_3dht.codes,_3dht.codes.length);
		htSymbols10=Arrays.copyOf(_3dht.symbols,_3dht.symbols.length);
		offSet=nextMarker("c4",offSet);
		DHT _4dht = new DHT();
		_4dht.ReaderDHT();
		htcodes11=Arrays.copyOf(_4dht.codes,_4dht.codes.length);
		htSymbols11=Arrays.copyOf(_4dht.symbols,_4dht.symbols.length);
		offSet = nextMarker("da", 2);
		SOS _sos = new SOS();
		_sos.Reader();		
	}
	public void decodeDC(){
		// DC decoding ..................................................

		String dcLChk = "";
		String[] lookUp = new String[0];
		String[] lookUpSymbol = new String[0];
		int matchResult=0;
		// for (int l=0; l<htSymbols00.length;l++ ) {
		// 	System.out.println(htcodes00[l]);
			
		// }
		if(htToUseType[1].charAt(1)=='0') {
			// System.out.println("00");
			lookUp = Arrays.copyOf(htcodes00, htcodes00.length);
			lookUpSymbol = Arrays.copyOf(htSymbols00, htSymbols00.length);
		}else if(htToUseType[1].charAt(1)=='1') {
			// System.out.println("01");

			lookUp = Arrays.copyOf(htcodes10, htcodes01.length);
			lookUpSymbol = Arrays.copyOf(htSymbols10, htSymbols10.length);
		}else{
			System.out.println("problem in decodeDataStream");
		}
		for(int i = 0; i <streamData.length(); i++ ) {
			dcLChk = dcLChk + streamData.charAt(i);
			matchResult = match(lookUp, dcLChk);
			if(matchResult!=-1){
				matchedAt = i+1;
				break;
			}
		}
		String lengthSymbol = lookUpSymbol[matchResult];
		System.out.println("length -->"+lengthSymbol);
		int dcLength = Integer.parseInt(lengthSymbol, 16);
		String dcValue = "";
		for(int k=0;k<dcLength;k++){
			dcValue=dcValue+streamData.charAt(matchedAt);
			matchedAt=matchedAt+1;
		}
		// System.out.println(dcValue+" == value in hex ");
		System.out.println("dcValue ----:> "+bitsToDecimal(dcValue));
		dcACvalues[0] = bitsToDecimal(dcValue);
		// System.out.println(matchedAt);

		// System.out.println(matchResult + "  ====:> " +dcLength

	}
	public void decodeAC() {
		// System.out.println("  match "+matchedAt);
		String[] lookUp = new String[0];
		String[] lookUpSymbol = new String[0];
		String acLChk = "";
		int matchResult = 0;
		if(htToUseType[1].charAt(0)=='0') {
			// System.out.println("Table selected "+"01");

			lookUp = Arrays.copyOf(htcodes01, htcodes01.length);
			lookUpSymbol = Arrays.copyOf(htSymbols01, htSymbols01.length);
		}else if(htToUseType[1].charAt(0)=='1') {
			// System.out.println("11");
			lookUp = Arrays.copyOf(htcodes11, htcodes11.length);
			lookUpSymbol = Arrays.copyOf(htSymbols11, htSymbols11.length);
		}else {
			System.out.println("problem");
		}
		// System.out.println();System.out.println();
		// System.out.println(streamData);
		// System.out.println();System.out.println();
		int index=1;
		while(true){
			acLChk="";
			// System.out.println("matchedAt shoro== "+matchedAt);	
			for(int i = matchedAt; i <streamData.length(); i++ ) {
				acLChk = acLChk + streamData.charAt(i);
				matchResult = match(lookUp, acLChk);
				// System.out.print(" "+acLChk+"  ");
				if(matchResult!=-1){
					matchedAt = i+1;
					break;
				}
			}
			String lengthSymbol = lookUpSymbol[matchResult];
			// end of block
			if(lengthSymbol.equals("00")){
				for(int remaining = index;remaining<64;remaining++){
					dcACvalues[remaining]=0;
				}
				break;
			}
			String skip = ""+lengthSymbol.charAt(0);
			String extra = ""+lengthSymbol.charAt(1);
			int skipbits = Integer.parseInt(skip,16);
			int extrabits = Integer.parseInt(extra,16);
			// System.out.println("skip "+skipbits+"  extra "+extrabits);
			for(int skipbit=0;skipbit<skipbits;skipbit++){
				dcACvalues[index]=0;
				index=index+1;
			}
			String extravalue = "";
			for(int extrabit=0;extrabit<extrabits;extrabit++){
				extravalue=extravalue+streamData.charAt(matchedAt);
				matchedAt=matchedAt+1;
			}
			int extravalue_int = bitsToDecimal(extravalue);
			dcACvalues[index]=extravalue_int;
			index=index+1;
			// System.out.println("extravalue_int  "+ extravalue_int);
			if(index==63){
				break;
			}
			// System.out.println("matchedAt last== "+matchedAt);	
			// break;
		}

		for(int blokvalue=0;blokvalue<64;blokvalue++){
			System.out.print(" "+dcACvalues[blokvalue]+" ");
		}

	}
	public void dequantization(){
		// System.out.println("dqt ");
		System.out.println();
		for (int i=0;i<64 ; i++) {
			dequantizedValues[i]=dcACvalues[i]*dqtValues[i];
			// System.out.print("  " +dequantizedValues[i]);			
		}
		// System.out.println();
		// System.out.println();
		// System.out.println();
	}
	public void deZigZag() {
		int mappingArray[]={0, 1, 5, 6, 14, 15, 27, 28,
			2, 4, 7, 13, 16, 26, 29, 42,
			3, 8, 12, 17, 25, 30, 41, 43,
			9, 11, 18, 24, 31, 40, 44, 53,
			10, 19, 23, 32, 39, 45, 52, 54,
			20, 22, 33, 38, 46, 51, 55, 60,
			21, 34,37, 47, 50, 56, 59, 61,35,
			36, 48, 49, 57, 58, 62, 63};
		// int array[]={1, 2, 3},
		for(int i = 0; i < 64; i++ ) {
			deZigZagged[i] = dequantizedValues[mappingArray[i]];
		}
		for(int j = 0; j < 64; j++ ) {
			System.out.print(" "+deZigZagged[j]);
		}


	}
	public static void main(String argd[]){
		jpeg lame= new jpeg();
		lame.extractHtSOS("jpeg.jpg");
		lame.decodeDC();
		lame.decodeAC();
		lame.extractDQT("jpeg.jpg");
		lame.dequantization();
		lame.deZigZag();

	}


}