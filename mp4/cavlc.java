import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.lang.String;

public class cavlc{

	public int[] cavlc_decoder(){
		// load table 9.5
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
					// System.out.print(lookupTable[i][j]+" ");
				}
				// System.out.println();
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




		return coeffLevel;
	}
	public static void main(String args[]){

		int[] testarray={0,3,-1,0,0,-1,1,0,1,0,0,0,0,0,0,0};
		String bitStream="000010001110010111101101";
		int[] coeffLevel = new cavlc().cavlc_decoder();
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