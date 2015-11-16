import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.lang.String;
// readBits
public class cavlc{
	String bits;
	parser parser_slice;
	public int[] cavlcTableLookUp(){
		String lookupTable[][]=new String[62][8];
		BufferedReader br = null;
		try {
			String line;

			br = new BufferedReader(new FileReader("table9.5.txt"));
			for (int i=0;i<62 ;i++ ) {
				for(int j=0;j<8;j++){
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
		for (int i=0;i<bits.length();i++){
			match=match+bits.charAt(i);
			for(int k=0;k<62;k++){
				if(lookupTable[k][lookupcolom].equals(match)){
					System.out.println(match);
					matchedat=k;
					numofcof_t1s[0]=Integer.parseInt(lookupTable[k][0]);
					numofcof_t1s[1]=Integer.parseInt(lookupTable[k][1]);
					bits=bits.replace(match,"");
					break;
				}
			}
		}
		System.out.println(matchedat+" matchedat");
		
		return numofcof_t1s;
	}
	public int[] cavlc_decoder(){
		// load table 9.5
		

		
		int[] coeffLevel = new int[16];
		// step 1
		for (int i=0;i<16 ;i++ ) {
			coeffLevel[i]=0;
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
		int[] ret=cavlcTableLookUp();
		System.out.println(ret[0]+" "+ret[1]);
		int TotalCoeff=ret[1];
		int TrailingOnes=ret[0];
		int nC=ret[2];
		// 9.2.2
		// inputs =>
		//slice data bits
		//total coeff+t1s
		int index=0;
		for(int i=0;i<TrailingOnes-1;i++){

			coeffLevel[index]=1;
		}
		

		return coeffLevel;
	}
	public static void main(String args[]){

		int[] testarray={0,3,-1,0,0,-1,1,0,1,0,0,0,0,0,0,0};
		cavlc c=new cavlc();
		c.bits="000010001110010111101101";
		// String b="";
		// 8
		byte[] bytes = {(byte)0x8,(byte)0xe5,(byte)0xed};
		c.parser_slice=new parser(bytes);
		for(int i=0;i<24;i++){
			System.out.print(c.parser_slice.readBits(1));
		}
		System.out.println();
		int[] coeffLevel = c.cavlc_decoder();
		int i=0;
		for(int j=0;j<4;j++){
			for (int k=0;k<4 ;k++ ) {
				System.out.print(coeffLevel[i]+"  ");
				i=i+1;				
			}
			System.out.println();
		}
	}
}