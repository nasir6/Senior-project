public class parser{
		int pointer;
		byte[] bytestream;
	parser(byte[] array){
		bytestream=array;
		// System.out.println("constructer initilized");
		pointer=0;
	}
	public boolean byte_aligned(){
		System.out.println("bohat hi lame zindagi hai" + pointer % 8);
		return (pointer % 8 == 0 ? true : false);
		// 
		// *function to be implemented
		//If the current position in the bitstream is on a byte boundary, 
	// i.e., the next bit in the bitstream is the first bit in a byte, 
	// the return value of byte_aligned( ) is equal to TRUE.
	// – Otherwise, the return value of byte_aligned( ) is equal to FALSE.	
	}
	public boolean more_rbsp_data(){
		if(byte_aligned() && pointer / 8 == bytestream.length) {
			return true;
		} else {
			return false;
		}
		//  need to think what to do if not byte alligned , ... sumran 
	}
		/*
	/If there is no more data in the RBSP, the return value of more_rbsp_data( ) is equal to FALSE.
	– Otherwise, the RBSP data is searched for the last (least significant, right-most) bit equal to 1 that is present in the RBSP. Given the position of this bit, which is the first bit (rbsp_stop_one_bit) of the rbsp_trailing_bits( ) syntax structure, the following applies:
	– If there is more data in an RBSP before the rbsp_trailing_bits( ) syntax structure, the return value of more_rbsp_data( ) is equal to TRUE.
	– Otherwise, the return value of more_rbsp_data( ) is equal to FALSE.
		The method for enabling determination of whether there is more data in the RBSP is specified by the application (or in Annex B for applications that use the byte stream format).
	more_rbsp_trailing_data( ) is specified as follows:
	– If there is more data in an RBSP, the return value of more_rbsp_trailing_data( ) is equal to TRUE.
	– Otherwise, the return value of more_rbsp_trailing_data( ) is equal to FALSE.

	// reads nth bit from bytestream;
*/
		
	public boolean getBit(){
		int byte_offset = pointer/8; 
		int bit_offset = pointer%8;
		byte valByte = bytestream[byte_offset];
		int valInt = valByte>>(8-(bit_offset+1)) & 0x0001;
		//   yeah ooper kea yaain hai explain kar ...... sumran... 
		pointer+=1;
		// sirf val int kyn nae return karwa daita ........sumran
		if (valInt==1) {
			return true;
		} else{
			return false;
		}
	}


	public int ReadBit(int n) {
		// ReadBit or getBit main farq kea hai ....... sumran 

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
				// ret_value |= (ReadBit(pointer+i) >> i);
		}
		ret_value= Integer.parseInt(sb.toString(), 2);
		return ret_value;
	}
/*provides the next bits in the bitstream for comparison purposes, without advancing the bitstream pointer. 
	Provides a look at the next n bits in the bitstream with n being its argument. 
	When used within the byte stream as specified in Annex B, next_bits( n ) returns a value of 0 
	if fewer than n bits remain within the byte stream.*/
	public int readBits(int n){
		//  nextBit or Read Bits main kea farq hai //// ...... sumran 
		int ret = nextBits(n);
		pointer = pointer+n;
		return ret;
	}
	public int ExpGolombDecode(){
		int count_leading_zeros=0;
		// System.out.println("came here in exp ExpGolombDecode ");
		while(true){
			int bit = ReadBit(pointer);
			// System.out.println("bit == "+bit);
			if(bit==0){
				pointer=pointer+1;
				count_leading_zeros=count_leading_zeros+1;
			}else{
				break;
			}
		}
		int codeNum = readBits(count_leading_zeros+1)-1;
		return codeNum;
	}
/* 
	reads the next n bits from the bitstream and advances the bitstream pointer by n bit positions. 
	When n is equal to 0, read_bits( n ) is specified to return a value equal to 0 and to not advance the bitstream pointer.
*/

/*
	The following descriptors specify the parsing process of each syntax element. 
	For some syntax elements, two descriptors, separated by a vertical bar, are used. In these cases, the left descriptors apply 
	when entropy_coding_mode_flag is equal to 0 and the right descriptor applies when entropy_coding_mode_flag is equal to 1.
	ae(v): context-adaptive arithmetic entropy-coded syntax element. 
	The parsing process for this descriptor is specified in clause 9.3.
	– b(8): byte having any pattern of bit string (8 bits). 
	The parsing process for this descriptor is specified by the return value of the function read_bits( 8 ).
	– ce(v): context-adaptive variable-length entropy-coded syntax element with the left bit first. 
	The parsing process for this descriptor is specified in clause 9.2.
	– f(n): fixed-pattern bit string using n bits written (from left to right) with the left bit first. 
	The parsing process for this descriptor is specified by the return value of the function read_bits( n ).
	– i(n): signed integer using n bits. When n is "v" in the syntax table, 
	the number of bits varies in a manner dependent on the value of other syntax elements. 
	The parsing process for this descriptor is specified by the return value of the function read_bits( n ) 
	interpreted as a two's complement integer representation with most significant bit written first.
	– me(v): mapped Exp-Golomb-coded syntax element with the left bit first. 
	The parsing process for this descriptor is specified in clause 9.1.
	– se(v): signed integer Exp-Golomb-coded syntax element with the left bit first. 
	The parsing process for this descriptor is specified in clause 9.1.
	– te(v): truncated Exp-Golomb-coded syntax element with left bit first. 
	The parsing process for this descriptor is specified in clause 9.1.
	– u(n): unsigned integer using n bits. When n is "v" in the syntax table, 
	the number of bits varies in a manner dependent on the value of other syntax elements. 
	The parsing process for this descriptor is specified by the return value of the function read_bits( n ) 
	interpreted as a binary representation of an unsigned integer with most significant bit written first.
	– ue(v): unsigned integer Exp-Golomb-coded syntax element with the left bit first. 
	The parsing process for this descriptor is specified in clause 9.1.

}*/


	// public static void main(String args[]){
	// 	// byte b = new byte[] { (byte)0xe0};
	// }
}

