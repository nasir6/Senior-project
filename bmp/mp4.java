public class mp4{
	public static void main(String args[]){

	// int[] offSet = {0, 2, 10, 14, 18, 22, 26, 28, 30, 34, 38, 42, 46, 50};
	// int[] len = {2, 4, 4, 4, 4, 4, 2, 2, 4, 4, 4, 4, 4, 4};
	// int[] flag = {0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1};
	String filename = "mp4.mp4";
	// String[] information = {"BM", "File Size", 
	// "file Offset to Raster Data", "InfoHeader size", 
	// "bitmap width", "bitmap height", "Number of planes", 
	// "Bits per pixel", "Compression", "Size of compressed Image", 
	// "X pixels/meter", "Y pixels/meter", "Num of Used Colors", "Num of Important Colors"};
	// bmp(){
		// System.out.println(len.length+" == "+offSet.length+" == "+information.length);
		// for(int i = 0; i < offSet.length ; i++) {
			int count =4;
			int offSet_=0;
			while(count>0){
				
				ReadFile offSet = new ReadFile(offSet_, 4, filename, "information[i]");
				offSet.readBytes();
				ReadFile ascii = new ReadFile(offSet_+4, 4, filename, "information[i]");
				ascii.readBytes();
				offSet_=offSet_+offSet.ToDECIMAL();
				System.out.println(offSet.ToDECIMAL()+" == "+offSet_);
				// if(flag[i] == 0) {
					
					System.out.println(ascii.ToASCII());
					count--;
				}
				// ;
			// } else {
				// nasir.ToDECIMAL();
			// }
			// }
		}	
	}