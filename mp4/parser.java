import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.lang.String;
public class parser{
		int pointer;
		byte[] bytestream;
	parser(byte[] array){
		bytestream=array;
		pointer=0;
	}
	public boolean byte_aligned(){
		// System.out.println("bohat hi lame zindagi hai" + pointer % 8);
		return (pointer % 8 == 0 ? true : false);
	}
	public boolean more_rbsp_data(){
		System.out.println(pointer/8+" "+bytestream.length);
		if((pointer) < bytestream.length*8) {
			return true;
		} else {
			return false;
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
		int value=(int)Math.pow(-1,k+1) * (int)Math.ceil(k/2);
		// System.out.println( value + " ***********k********** "+k);

		return value;
	}
	public int mev(int mode){
		int[] tableIntra={47,31,15,0,23,27,29,30,7,11,13,14,39,43,
			45,46,16,3,5,10,12,19,21,26,28,35,37,42,44,1,2,4,8,17,18,
			20,24,6,9,22,25,32,33,34,36,40,38,41};
		int[] tableInter={0,16,1,2,4,8,32,3,5,10,12,15,47,7,
			11,13,14,6,9,31,35,37,42,44,33,
			34,36,40,39,43,45,46,17,18,20,24,19,21,26,28,23,27,
			29,30,22,25,38,41};	
			int k=ExpGolombDecode();
			// System.out.println(k+"  kkkkkkkkkkkkkk");
			return (mode == 0 ? tableInter[k] : tableIntra[k]);	
	}
	//ce(v)
	// Inputs to this process are bits from slice data, a maximum number of non-zero transform coefficient 
	// levels maxNumCoeff, the luma block index luma4x4BlkIdx or the chroma block index chroma4x4BlkIdx, 
	// cb4x4BlkIdx or cr4x4BlkIdx of the current block of transform coefficient levels.
	// Output of this process is the list coeffLevel containing transform coefficient levels of the luma block 
	// with block index luma4x4BlkIdx or the chroma block with block index chroma4x4BlkIdx, cb4x4BlkIdx or cr4x4BlkIdx.
	// public int cavlc(){

	// }
	// public int tev(){
		
	// }
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
		int row=0;
		int col=0;
		if(filename.equals("table9.7.txt")){
			row=16;
			col=8;
		}else if(filename.equals("table9.8.txt")){
			row=9;
			col=9;
		}else if(filename.equals("table9.9.a.txt")){
			row=4;
			col=3;
		}else if(filename.equals("table9.9.b.txt")){
			row=8;
			col=8;
		}else if(filename.equals("table9.10.txt")){
			row=15;
			col=8;
		}
		// System.out.println(row+" "+col);
		String lookupTable[][]=loadTable(filename,row,col);
		String match="";
		int[] numofcof_t1s= new int[3];
		int matchedat=0;
		while (true){
			match=match+readBits(1);
			for(int k=0;k<row;k++){
				if(lookupTable[k][lookupcolom].equals(match)){
					// System.out.println(match);
					matchedat=k;
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
		}
		String lookupTable[][]=loadTable(tablename,row,col);
		return lookupTable[lookUpRow+1][lookUpCol];
	}
	public int[] cavlcTableLookUp(String filename,int row,int col){
		String lookupTable[][]=loadTable(filename,row,col);
		
		int nC=0;
		// if(ChromaArrayType==1){
			// nC=-1
		// } else if(ChromaArrayType==2){
			// nC=-2;
		// }


		// step 3 apply following
		// if #non-zero = 0 return coefflevel
		// else parse trailing ones sign level suffix and prefix as in 9.2.3
		// level and run information are combined into the list coefflevel as in 9.2.4


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
		while (true){
			match=match+readBits(1);
			for(int k=0;k<62;k++){
				if(lookupTable[k][lookupcolom].equals(match)){
					// System.out.println(match);
					matchedat=k;
					numofcof_t1s[0]=Integer.parseInt(lookupTable[k][0]);
					numofcof_t1s[1]=Integer.parseInt(lookupTable[k][1]);
					// System.out.println(matchedat+" matchedat");
					return numofcof_t1s;

					
				}
			}
		}
		
	}
	public int[] cavlc_decoder(){
		// load table 9.5
		

		int maxNumCoeff=16;
		int[] levelVal = new int[16];
		// step 1
		for (int i=0;i<16 ;i++ ) {
			levelVal[i]=0;
		}
		// step 2 Total number of non-zero and tailing ones by clause 9.2.1
		// 9.2.1
		// inputs => bits from slice data
			// maxNumCoeff
			//luma4x4BlkIdx
			//chroma4x4BlkIdx




		// outputs =>
			// TotalCoeff
			// TrailingOnes
			// nC
		int[] ret=cavlcTableLookUp("table9.5.txt",62,8);
		System.out.println(ret[0]+" "+ret[1]);
		int TotalCoeff=ret[1];
		int TrailingOnes=ret[0];
		int nC=ret[2];
		// 9.2.2
		// inputs =>
		//slice data bits
		//total coeff+t1s
		int index=0;
		for(int i=0;i<TrailingOnes;i++){
			int read_Bits= readBits(1);
			if(read_Bits==1){
				levelVal[index]=-1;
			}else{
				levelVal[index]=1;

			}
			index=index+1;
		}
		int suffixLength=0;
		if(TotalCoeff<=10&&TrailingOnes==3){
			suffixLength=0;
		}
		if(TotalCoeff>10&&TrailingOnes<3){
			suffixLength=1;
		}
		int remainingCoeff=TotalCoeff - TrailingOnes;
		int level_prefix=0;
		int levelSuffixSize=0;
		int level_suffix=0;
		int levelCode=0;
		if(remainingCoeff !=0){
			for(int i=0;i<remainingCoeff;i++){
				
				// step 1
				// Inputs to this process are bits from slice data.
				// Output of this process is level_prefix.		
				int leadingZeroBits=0;
				while(readBits(1)==0){
					leadingZeroBits+=1; 
				}
				level_prefix = leadingZeroBits;
				
				// step 2
				// If level_prefix is equal to 14 and suffixLength is equal to 0, levelSuffixSize is set equal to 4.
				if(level_prefix==14&& suffixLength==0){
					levelSuffixSize=4;
				}
				// – Otherwise, if level_prefix is greater than or equal to 15, levelSuffixSize is set equal to level_prefix − 3.
				else if(level_prefix>=15){
					levelSuffixSize=level_prefix-3;
				// – Otherwise, levelSuffixSize is set equal to suffixLength.

				}else{
					levelSuffixSize=suffixLength;					
				}
				// step 3
// – If levelSuffixSize is greater than 0, the syntax element level_suffix is decoded as unsigned integer representation u(v) with levelSuffixSize bits.
				if(levelSuffixSize>0){
					level_suffix=readBits(levelSuffixSize);
					// System.out.println("came here read unsigned int "+level_suffix);
				}else if(levelSuffixSize==0){
					level_suffix=0;
				}
				// – Otherwise (levelSuffixSize is equal to 0), the syntax element level_suffix is inferred to be equal to 0.

				// step 4
				// The variable levelCode is set equal to ( Min( 15, level_prefix ) << suffixLength ) + level_suffix.
				// if()
				levelCode=((Math.min(15,level_prefix)) << suffixLength)+level_suffix;

				// When level_prefix is greater than or equal to 15 and suffixLength is equal to 0, levelCode is incremented by 15.

				if(level_prefix>=15&&suffixLength==0){
					levelCode=levelCode+15;
				}
				// When level_prefix is greater than or equal to 16, levelCode is incremented by (1<<( level_prefix − 3 )) − 4096.
				if(level_prefix>16){
					levelCode=levelCode+((1<<(level_prefix-3))-4096);
				}
				// When the index i is equal to TrailingOnes( coeff_token ) and TrailingOnes( coeff_token ) 
				// is less than 3, levelCode is incremented by 2.
				if(index == TrailingOnes && TrailingOnes<3){
					levelCode=levelCode+2;
				}
				// The variable levelVal[ i ] is derived as follows:
				// If levelCode is an even number, levelVal[ i ] is set equal to ( levelCode + 2 ) >> 1.
				// – Otherwise (levelCode is an odd number), levelVal[ i ] is set equal to ( −levelCode − 1) >> 1.
				// System.out.println(levelCode+" levelCode");
				if(levelCode%2==0){
					levelVal[index]=(levelCode+2)>>1;

				}else{
					levelVal[index]=((-1*levelCode)-1)>>1;
				}
				// When suffixLength is equal to 0, suffixLength is set equal to 1.
				if(suffixLength==0){
					suffixLength=1;
				}
				// When the absolute value of levelVal[ i ] is greater than ( 3 << ( suffixLength − 1 ) ) 
				// and suffixLength is less than 6, suffixLength is incremented by 1.
				if((Math.abs(levelVal[index]))>(3<<(suffixLength-1))&&suffixLength<6){
					suffixLength=suffixLength+1;
				}
				// The index i is incremented by 1.
				index=index+1;
			}

		}
//9.2.3
		// Inputs to this process are bits from slice data, the number of non-zero transform coefficient levels 
		// TotalCoeff( coeff_token ), and the maximum number of non-zero transform coefficient levels maxNumCoeff.
		// Output of this process is a list of runs of zero transform coefficient levels preceding non-zero transform 
		// coefficient levels called runVal.
		
		// The variable tzVlcIndex is set equal to TotalCoeff( coeff_token ).
		
		// 
		int tzVlcIndex=TotalCoeff;
		index =0;
		// int tablenum=0;
		int total_zeros=0;
		
		int[] runVal=new int [TotalCoeff];
		// – If maxNumCoeff is equal to 4, one of the VLCs specified in Table 9-9 (a) is used.
		if(maxNumCoeff==4){
			// tablenum=1;
			total_zeros=vlcTableLookUp("table9.9a.txt",tzVlcIndex);

		// – Otherwise, if maxNumCoeff is equal to 8, one of the VLCs specified in Table 9-9 (b) is used.
		}else if(maxNumCoeff==8){
			total_zeros=vlcTableLookUp("table9.9b.txt",tzVlcIndex);
		}else{
			if(tzVlcIndex<8){
				total_zeros=vlcTableLookUp("table9.7.txt",tzVlcIndex);
			}else if(tzVlcIndex>=8){
				total_zeros=vlcTableLookUp("table9.8.txt",tzVlcIndex);
			}
		}
		int zerosLeft =total_zeros; 


		// – Otherwise (maxNumCoeff is not equal to 4 and not equal to 8), VLCs from Tables 9-7 and 9-8 are used.

		System.out.println("zerosLeft "+zerosLeft);
		// The following ordered steps are then performed TotalCoeff( coeff_token ) − 1 times:
		for(int i=0;i<TotalCoeff-1;i++){
			// 1. The variable runVal[ i ] is derived as follows:

		// – If zerosLeft is greater than zero, a value run_before is decoded based on 
		// Table 9-10 and zerosLeft. runVal[ i ] is set equal to run_before.
			if(zerosLeft>0){
				runVal[index]=vlcTableLookUp("table9.10.txt",zerosLeft);
				// runVal[index]=run_before;
			}else if(zerosLeft==0){
				runVal[index]=0;
			}

		// – Otherwise (zerosLeft is equal to 0), runVal[ i ] is set equal to 0.
		// 2. The value of runVal[ i ] is subtracted from zerosLeft and the result is 
		// assigned to zerosLeft. It is a requirement of bitstream conformance that the 
		// result of the subtraction shall be greater than or equal to 0.
		
		zerosLeft=zerosLeft - runVal[index];

		// 3. The index i is incremented by 1.
		index=index+1;
		// Finally the value of zerosLeft is assigned to runVal[ i ].
		runVal[index]=zerosLeft;
		}
		// for(int i=0;i<TotalCoeff;i++){
		// 	// System.out.println("runVal "+runVal[i]);
		// }

		int[] coefflevel={0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0};
		// Input to this process are a list of transform coefficient levels called levelVal, 
		// a list of runs called runVal, and the number of non-zero transform coefficient levels TotalCoeff( coeff_token ).
// Output of this process is an list coeffLevel of transform coefficient levels.
// A variable coeffNum is set equal to −1 and an index i is set equal to TotalCoeff( coeff_token ) − 1. 
		int coeffNum=-1;

		index=TotalCoeff-1;
// The following ordered steps are then applied TotalCoeff( coeff_token ) times:
		for (int i=0;i<TotalCoeff;i++ ) {
			// 1. coeffNum is incremented by runVal[ i ] + 1.	 
			coeffNum=coeffNum+1+runVal[index];
			// 2. coeffLevel[ coeffNum ] is set equal to levelVal[ i ].
			coefflevel[coeffNum]=levelVal[index];
			// 3. The index i is decremented by 1.
			index=index-1;
			
		}



		return coefflevel;
	}

	// public static void main(String args[]){
	// 	// 0,3,0,1,−1,−1,0,1,0
	// 	int[] testarray={0,3,-1,0,0,-1,1,0,1,0,0,0,0,0,0,0};
	// 	byte[] bytes = {(byte)0x8,(byte)0xe5,(byte)0xed};
	// 	parser p=new parser(bytes);
	// 	int[] coeffLevel = p.cavlc_decoder();
	// 	int i=0;
	// 	for(int j=0;j<4;j++){
	// 		for (int k=0;k<4 ;k++ ) {
	// 			System.out.print(coeffLevel[i]+"  ");
	// 			i=i+1;				
	// 		}
	// 		System.out.println();
	// 	}
	// }


}

