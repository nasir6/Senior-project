import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.lang.String;
import java.util.*;
import java.math.BigInteger;

public class parser{
		int pointer;
		boolean trailing_ones_sign_flag;
		int nC;
		int maxNumCoeff;
		int[] coeffLevel;
		byte[] bytestream;
		int TotalCoeff;
		int trailingBitOffSet;

		int startIdx, endIdx,zerosLeft,run_before;
	parser(byte[] array){
		bytestream=array;
		pointer=0;
		rbsp_trailing_bits();
	}
	public void rbsp_trailing_bits(){
		for(int i = (bytestream.length-1)*8; i > 0; i--) {
			int byte_offset = i/8; 
			int bit_offset = i%8;
			byte valByte = bytestream[byte_offset];
			int valInt = valByte>>(8-(bit_offset+1)) & 0x0001;
			if(valInt==1){
				trailingBitOffSet=i;
				break;
			}
		}
	}
	public boolean byte_aligned(){
		return (pointer % 8 == 0 ? true : false);
	}
	public boolean more_rbsp_data(){
		// System.out.println("more_rbsp_data "+trailingBitOffSet+" pointer "+pointer);
		if(pointer==(bytestream.length-1)*8){
			return false;
		}else if(pointer>=trailingBitOffSet){
			return false;
		}else{
			return true;
		}
	}	
	public boolean getBit(){
		int byte_offset = pointer/8; 
		int bit_offset = pointer%8;
		byte valByte = bytestream[byte_offset];
		int valInt = valByte>>(8-(bit_offset+1)) & 0x0001;
		pointer+=1;
		if (valInt==1) {
			return true;
		} else{
			return false;
		}
	}


	public int ReadBit(int n) {

		int byte_offset = n/8; 
		int bit_offset = n%8;
		byte valByte = bytestream[byte_offset];
		int valInt = valByte>>(8-(bit_offset+1)) & 0x0001;
		return valInt;
	}
	public int nextBits(int n){
		StringBuilder sb = new StringBuilder(n);
		int ret_value = 0;
		for (int i = 0; i < n; i++)
		{
			sb.append(ReadBit(pointer+i));
		}
		// System.out.println(" nextBits in stream "+n+"  "+sb.toString());
		ret_value= Integer.parseInt(sb.toString(), 2);
		return ret_value;
	}
	public int readBits(int n){
		int ret = nextBits(n);
		pointer = pointer+n;
		return ret;
	}
	public int ExpGolombDecode(){

		int count_leading_zeros=0;
		while(readBits(1)==0){
			count_leading_zeros+=1; 
		}
		double c=(Math.pow(2,count_leading_zeros))-1;
		int codeNum = (int)c;
		// System.out.println("zeros "+count_leading_zeros);
		if(count_leading_zeros==0){
			codeNum=0;
		}else{
			codeNum=codeNum+readBits(count_leading_zeros);
		}
		return codeNum;
	}
	public int uev(){
		return ExpGolombDecode();
	}
	public int sev(){
		int k=ExpGolombDecode();
		// System.out.println("k== "+ k);
		int value=(int)Math.pow(-1,k+1) * (int)Math.ceil(k/2.0);
		// System.out.println( value + " ***********k********** "+k);

		return value;
	}
	public int mev(int mode,int ChromaArrayType_){
		int[] tableIntra12={47,31,15,0,23,27,29,30,7,11,13,14,39,43,
			45,46,16,3,5,10,12,19,21,26,28,35,37,42,44,1,2,4,8,17,18,
			20,24,6,9,22,25,32,33,34,36,40,38,41};
		int[] tableInter12={0,16,1,2,4,8,32,3,5,10,12,15,47,7,
			11,13,14,6,9,31,35,37,42,44,33,
			34,36,40,39,43,45,46,17,18,20,24,19,21,26,28,23,27,
			29,30,22,25,38,41};	
		int[] tableInter03={0,1,2,4,8,3,5,10,12,15,7,11,13,14,6,9};
		int[] tableIntra03={15,0,7,11,13,14,3,5,10,12,1,2,4,8,6,9};
		int k=ExpGolombDecode();
		// System.out.println(k+"  kkkkkkkkkkkkkk");
		if(ChromaArrayType_==1||ChromaArrayType_==2){
			return (mode == 0 ? tableInter12[k] : tableIntra12[k]);	
		}
		if(ChromaArrayType_==0||ChromaArrayType_==3){
			return (mode == 0 ? tableInter03[k] : tableIntra03[k]);	
		}
		return 0;
	}
	public int tev(){
		int codeNum;
		int x=ExpGolombDecode();
		codeNum=x;
		if(x>1){
			return x;
		}else if(x==1){
			codeNum=(readBits(1)==0) ? 1 : 0;
		}
		return codeNum;
		
	}
	public int countlines(String filename){
		BufferedReader br = null;
		int count=0;
		try {
			
			String line;
			br = new BufferedReader(new FileReader(filename));
			while(br.readLine()!= null){
				count++;
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (br != null)br.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
		// System.out.println(count+" total entries");
		return count;
	}
	public String[][] loadTable(String filename,int row,int col){
		String lookupTable[][]=new String[row][col];
		BufferedReader br = null;
		int count=0;
		try {
			
			String line;
			br = new BufferedReader(new FileReader(filename));
			for (int i=0;i<row ;i++ ) {
				for(int j=0;j<col;j++){
					count++;
					line = br.readLine();
					line = line.replaceAll("\\s+","");
					lookupTable[i][j]=line;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (br != null)br.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
		// System.out.println(count+" total entries");
		return lookupTable;
	}
	public int vlcTableLookUp(String filename,int lookupcolom){
		//table 9.7 16x8
		//table 9.8 9x9
		//table 9.9a 4x3
		//table 9.9b 8x8
		//table 9.10 15x8
		// System.out.println("table "+ filename);
		int row=0;
		int col=0;
		if(filename.equals("table9.7.txt")){
			row=16;
			col=8;
		}else if(filename.equals("table9.8.txt")){
			row=9;
			col=9;
		}else if(filename.equals("table9.9a.txt")){
			row=4;
			col=4;
		}else if(filename.equals("table9.9b.txt")){
			row=8;
			col=8;
		}else if(filename.equals("table9.10.txt")){
			row=15;
			col=8;
		}
		// System.out.println();
		// System.out.println(row+" "+col);
		String lookupTable[][]=loadTable(filename,row,col);
		String match="";
		int[] numofcof_t1s= new int[3];
		int matchedat=0;
		// int maxCodeLength=0;
		while (true){
			// System.out.println("string matching "+ match);
			
			match=match+readBits(1);
			for(int k=0;k<row;k++){
				// System.out.println(lookupTable[k][lookupcolom]+" matching to ");
				if(lookupTable[k][lookupcolom].equals(match)){
					matchedat=k;
					// System.out.println("matchedat "+k+"  "+match);
					return Integer.parseInt(lookupTable[k][0]);
				}
			}
		}
	}
	public String Mb_Type(String tablename, int lookUpRow,int lookUpCol){
		int row=0,col=0;
		if(tablename.equals("table7.11.txt")){
			row=27;
			col=7;
			String lookupTable[][]=loadTable(tablename,row,col);
			return lookupTable[lookUpRow+1][lookUpCol];
		}
		if(tablename.equals("table7.13.txt")){
			row=6;
			col=7;
		}
		String lookupTable[][]=loadTable(tablename,row,col);
		// if(tablename.equals)
		return lookupTable[lookUpRow][lookUpCol];

	}

	public int[] cavlcTableLookUp(String filename,int row,int col){
		String lookupTable[][]=loadTable(filename,row,col);
		// System.out.println("nC in VLC "+nC);
		int lookupcolom=0;
		if(nC>=0&&nC<2){
			lookupcolom=2;
		}
		else if(nC>=2&&nC<4){
			lookupcolom=3;
		}
		else if(nC>=4&&nC<8){
			lookupcolom=4;
		}
		else if(nC>=8){
			lookupcolom=5;
		}
		else if(nC==-1){
			lookupcolom=6;
		}
		else if(nC==-2){
			lookupcolom=7;
		}
		String match="";
		int[] numofcof_t1s= new int[3];
		numofcof_t1s[2]=nC;
		int matchedat=0;
		// System.out.println("start");
		while (true){
			match=match+readBits(1);
			for(int k=0;k<62;k++){
				if(lookupTable[k][lookupcolom].equals(match)){

					matchedat=k;
					// System.out.print("match raw stream " + match);
					// pointer=pointer-1;

					numofcof_t1s[0]=Integer.parseInt(lookupTable[k][0]);
					numofcof_t1s[1]=Integer.parseInt(lookupTable[k][1]);
					// System.out.println("matchedat "+match);
					// System.out.println("end");
					
					return numofcof_t1s;


					
				}
			}
		}
		// return numofcof_t1s;
		
	}
	public void residual_block_cavlc(int[] coeffLevel_,int startIdx_,int endIdx_,int maxNumCoeff_){
		// System.out.println("Pointer at ************** "+ pointer);
		maxNumCoeff=maxNumCoeff_;
		coeffLevel=coeffLevel_;
		startIdx=startIdx_;
		endIdx=endIdx_;
		int[] levelVal = new int[maxNumCoeff];
		// int TrailingOnes;
		int index=0;
		int suffixLength=0;
		// int remainingCoeff=TotalCoeff - TrailingOnes;
		int level_prefix=0;
		int levelSuffixSize=0;
		int level_suffix=0;
		int levelCode=0;

		
		for(int i =0;i<maxNumCoeff;i++){
			coeffLevel[i]=0;
			levelVal[i]=0;
		}
		// nextBits(30);
		int[] ret=cavlcTableLookUp("table9.5.txt",62,8);
		// System.out.println("pointer at start "+pointer);
		TotalCoeff=ret[1];
		int TrailingOnes=ret[0];
		// TotalCoeff=5;
		// int TrailingOnes=3;
		int[] runVal=new int [TotalCoeff];
		nC=ret[2];
		// System.out.println("nc in residual_block_cavlc "+nC);
		if(TotalCoeff>0){
			if(TotalCoeff>10&&TrailingOnes<3){
				suffixLength=1;
			}else if(TotalCoeff<=10||TrailingOnes==3){
				suffixLength=0;	
			}
			// System.out.println("suffixLength "+suffixLength);
			for(int i =0;i<TotalCoeff;i++){
				if(i<TrailingOnes){
					// System.out.println("TrailingOnes sign");
					trailing_ones_sign_flag=getBit();
					// System.out.println(trailing_ones_sign_flag);
					levelVal[i]=1-2*((trailing_ones_sign_flag) ? 1:0);
					// System.out.println(levelVal[i]);
				}else{
					
					// System.out.println("came here in coefflevel wala part ");
					// Scanner scan= new Scanner(System.in);
					// int x=scan.nextInt();
					// int 
					int leadingZeroBits=-1;
					for(int b = 0; b!=1; leadingZeroBits++){
						b = readBits(1);
						level_prefix = leadingZeroBits;
					}
					// int leadingZeroBits=0;
					// while(true){
					// 	System.out.println("false ");
					// 	if(getBit()){
					// 		break;
					// 		// System.out.println("");
					// 	}
					// 	leadingZeroBits=leadingZeroBits+1;
					// }
					// // pointer=pointer-1;
					level_prefix = leadingZeroBits;
					// System.out.println("level_prefix "+level_prefix);
					levelCode=((Math.min(15,level_prefix)) << suffixLength);
					// if(level_prefix==14&& suffixLength==0){
					// 	levelSuffixSize=4;
					// 	// suffixLength=0;
					// }
					// else if(level_prefix>=15){
					// 	levelSuffixSize=level_prefix-3;
					// }else{
					// 	levelSuffixSize=suffixLength;				
					// }
					// if(levelSuffixSize>0){
					// 	// nsigned integer
					// 	level_suffix=readBits(levelSuffixSize);
					// }else if(levelSuffixSize==0){
					// 	level_suffix=0;
					// }
					// levelCode=levelCode+level_suffix;

					if(suffixLength>0||level_prefix>=14){
						// level_suffix
						if(level_prefix==14&& suffixLength==0){
							levelSuffixSize=4;
							// suffixLength=0;
						}
						else if(level_prefix>=15){
							levelSuffixSize=level_prefix-3;
						}else{
							levelSuffixSize=suffixLength;				
						}
						if(levelSuffixSize>0){
							// nsigned integer
							level_suffix=readBits(levelSuffixSize);
						}else if(levelSuffixSize==0){
							level_suffix=0;
						}
					levelCode=levelCode+level_suffix;
					}
					if(level_prefix>=15&& suffixLength==0){
						levelCode+=15;
					}
					if(level_prefix>=16){
						levelCode+=(1<<(level_prefix-3))-4096;
					}
					if(i==TrailingOnes&&TrailingOnes<3){
						levelCode+=2;
					}

					if(levelCode%2==0){
						levelVal[i]=(levelCode+2)>>1;
					} else{
						levelVal[i]=(-1*levelCode-1)>>1;
					}
					if(suffixLength==0){
						suffixLength=1;
					}
					if(Math.abs(levelVal[i])>(3<<(suffixLength-1))&&suffixLength<6){
						suffixLength=suffixLength+1;
					}
				}
			}
			// if(TotalCoeff<((endIdx- startIdx) +1)){
			int tzVlcIndex=TotalCoeff;
			// index =0;
			int total_zeros=0;
			if(TotalCoeff==maxNumCoeff){
				zerosLeft=0;
			}else if(TotalCoeff<maxNumCoeff){
				// System.out.println("zeros start");
				// nextBits(30);

				if(maxNumCoeff==4){
					total_zeros=vlcTableLookUp("table9.9a.txt",tzVlcIndex);
				}else if(maxNumCoeff==8){
					total_zeros=vlcTableLookUp("table9.9b.txt",tzVlcIndex);
				}else{
					if(tzVlcIndex<8){
						total_zeros=vlcTableLookUp("table9.7.txt",tzVlcIndex);
					}else if(tzVlcIndex>=8){
						total_zeros=vlcTableLookUp("table9.8.txt",tzVlcIndex-7);
					}
				}
				// System.out.println("zeros end "+total_zeros);

				zerosLeft=total_zeros;
			}
			// nextBits(30);

			// System.out.println();
			for(int i=0;i<TotalCoeff-1;i++){
				// System.out.println("TotalCoeff-1 "+TotalCoeff);
				if(zerosLeft>0){
					// System.out.println("greater than zero ");
					if(zerosLeft>6){
						run_before=vlcTableLookUp("table9.10.txt",7);

					}else{
						// System.out.println("r b start");
						run_before=vlcTableLookUp("table9.10.txt",zerosLeft);
						// System.out.println("r b end");

					}
					runVal[i]=run_before;
				}else{
					runVal[i]=0;
				}
				zerosLeft=zerosLeft-runVal[i];
			}
			runVal[TotalCoeff-1]=zerosLeft;
			int coeffNum=-1;
			// nextBits(30);

			for(int i=TotalCoeff-1;i>=0;i--){
				coeffNum=coeffNum+runVal[i]+1;
				coeffLevel[0+coeffNum]=levelVal[i];
			}
		}

		System.out.println();
		for (int i=0;i<maxNumCoeff ;i++ ) {
			System.out.print(coeffLevel[i]+" ");
		}
	}




	/*
	public static void main(String[] args) {
		String b="000010001110010111101101";
		// 0,3,0,1,-1,-1,0,1,0
		// String b = "0110100001101001";
		byte[] bytes = new BigInteger(b, 2).toByteArray();
		// int[] testarray={0,3,0,1,-1,-1,0,1,0,0,0,0,0,0,0,0};
		// byte[] bytes = {(byte)0x08,(byte)0xe5,(byte)0xed};

		parser p=new parser(bytes);
		int[] coeffLevel=new int[16];// = p.cavlc_decoder();

		p.residual_block_cavlc(coeffLevel,0,15,16);
		int i=0;
		for(int j=0;j<4;j++){
			for (int k=0;k<4 ;k++ ) {
				System.out.print(coeffLevel[i]+"  ");
				i=i+1;				
			}
			// System.out.println();
		}
		// byte[] a={(byte)1, (byte)1, (byte)(-2), (byte)(-4), (byte)0, (byte)0, (byte)0, (byte)1, (byte)1, (byte)1, (byte)0, (byte)1, (byte)0, (byte)0, (byte)0, (byte)0}
		// parser p=new  parser(a);


	}
	*/
	
}

