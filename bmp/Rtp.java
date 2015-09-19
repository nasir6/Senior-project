public class Rtp{
	int rtpOffSet = 3502593;
	String fileName = "mp4.mp4";
	int relativeTime; //32 bits
	int reserved2,p1,x1,reserved4,M_bit1,payLoadType7;
	int rtp_seq; // 16 bit
	int reserved; //13 bits
	int extraFlag; //1 bit
	int b_frameFlag;//1 bit
	int repeatFlag; //1 bit
	int entryCount; //16 bit
	// if extraFlag 
	int extraInformationLength; //Extra data box 32 bits
	public void ReadRTP(){
		ReadFile relativeTime = new ReadFile(rtpOffSet, 4,fileName, "information[i]");
		relativeTime.readBytes();
		System.out.println("relativeTime = "+relativeTime.ToDECIMAL());
		ReadFile bits = new ReadFile(rtpOffSet+4, 8,fileName, "information[i]");
		bits.readBytes();
		boolean[] bitsArray =  bits.ToBits();
		System.out.println("bits = "+relativeTime.ToDECIMAL());
		int count =1;
		for (int i=0;i<bitsArray.length ;i++ ) {
			if(bitsArray[i]==false){
				System.out.println(count+"  ==  "+0);
			} else{
				System.out.println(count+"  ==  "+1);
			}
			count++;
		}
		if(bitsArray[18]==true){
			ReadFile extraData = new ReadFile(rtpOffSet+4+8, 4,fileName, "information[i]");
			extraData.readBytes();
			System.out.println("extraInformationLength = "+extraData.ToUint());
			ReadFile extraInfo = new ReadFile(rtpOffSet+4+8+4, extraData.ToUint(),fileName, "information[i]");
			extraInfo.readBytes();
			System.out.println("extraInformationLength = "+extraInfo.ToASCII());
		}
	}
	public static void main(String args[]){
		Rtp test = new Rtp();
		test.ReadRTP();


	}

}