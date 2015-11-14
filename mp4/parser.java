public class parser{
		int pointer;
		byte[] bytestream;
	parser(byte[] array){
		bytestream=array;
		pointer=0;
	}
	public boolean byte_aligned(){
		System.out.println("bohat hi lame zindagi hai" + pointer % 8);
		return (pointer % 8 == 0 ? true : false);
	}
	public boolean more_rbsp_data(){
		if(byte_aligned() && pointer / 8 == bytestream.length) {
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
		while(true){
			int bit = readBits(1);
			// System.out.println(bit);
			if(bit==0){
				count_leading_zeros=count_leading_zeros+1;
			}else{
				break;
			}
		}
		double c=Math.pow(2,count_leading_zeros)-1;
		int codeNum = (int)c;
		System.out.println("zeros "+count_leading_zeros);
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
			return (mode == 0 ? tableInter[ExpGolombDecode()] : tableIntra[ExpGolombDecode()]);	
	}
	//ce(v)
	// Inputs to this process are bits from slice data, a maximum number of non-zero transform coefficient 
	// levels maxNumCoeff, the luma block index luma4x4BlkIdx or the chroma block index chroma4x4BlkIdx, 
	// cb4x4BlkIdx or cr4x4BlkIdx of the current block of transform coefficient levels.
	// Output of this process is the list coeffLevel containing transform coefficient levels of the luma block 
	// with block index luma4x4BlkIdx or the chroma block with block index chroma4x4BlkIdx, cb4x4BlkIdx or cr4x4BlkIdx.
	public int cavlc(){

	}
	// public int tev(){
		
	// }


}

