public class Rtp{
	int rtpOffSet = 18713;
	String fileName = "mp4.mp4";
	public void ReadRTP(){
		for(int i=0;i<1000;i++){
			ReadFile relativeTime = new ReadFile(rtpOffSet+i, 1,fileName, "information[i]");
			relativeTime.readBytes();
			System.out.println("avcc = "+relativeTime.ToHex());
			
			}
		// ReadFile bits = new ReadFile(rtpOffSet+4, 8,fileName, "information[i]");
		// bits.readBytes();
		// boolean[] bitsArray =  bits.ToBits();
		// System.out.println("bits = "+relativeTime.ToDECIMAL());
	}
	public static void main(String args[]){
		Rtp test = new Rtp();
		test.ReadRTP();


	}

}