

public class Dct{
	public int[][] inverse_dct(int f[][]){
		double a_u,a_v;
		double sum=0;
		int[][] inverse_dct_array = new int[8][8];
		for (int x=0;x<8 ;x++ ) {
			for (int y=0;y<8 ;y++ ) {
				for (int u=0;u<8 ;u++ ) {
					for (int v=0;v<8 ;v++ ) {
						if(u==0){
							a_u=1.0/Math.sqrt(2);
						}else{
							a_u=1.0;
						}
						if(v==0){
							a_v=1.0/Math.sqrt(2);
						}else{
							a_v=1.0;
						}
						sum = sum+(a_u*a_v*f[u][v]
							*(Math.cos(((2.0*x+1.0)*u*Math.PI)/16.0))
							*(Math.cos(((2.0*y+1.0)*v*Math.PI)/16.0)));
					}
				}
				sum = 1/4.0*sum;
				inverse_dct_array[x][y]=(int)Math.round(sum)+128;
				sum=0;
			}
		}
		return inverse_dct_array;
	}
	public static void main(String args[]){
		int[][] input = {{-416,-33,-60,32,48,-40,0,0},
		{0,-24,-56,19,26,0,0,0},
		{-42,13,80,-24,-40,0,0,0},
		{-42,17,44,-29,0,0,0,0},
		{18,0,0,0,0,0,0,0},
		{0,0,0,0,0,0,0,0},
		{0,0,0,0,0,0,0,0},
		{0,0,0,0,0,0,0,0}};

		System.out.println();
		int [][] ret =new Dct().inverse_dct(input);
		for (int x=0;x<8 ;x++ ) {
			for (int y=0;y<8 ;y++ ) {
				System.out.print(ret[x][y]+" ");
			}
			System.out.println();
		}
	}
}