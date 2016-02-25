import java.io.*;
import java.util.*;

public class nal
{
	int NAL_Unit_length_size=4;
	int length;
	int nalOffSet;//+489; /// 
	String fileName = "mp4.mp4";
	int forbidden_zero_bit; //f1
	int nal_ref_idc; //u2
	int nal_unit_type; //u5
	int NumBytesInRBSP =0;
	int nalUnitHeaderBytes =1;
	boolean svc_extension_flag; //u1
	boolean avc_3d_extension_flag; //u1
	byte rbsp_byte[];
	int NumBytesInNALunit;
	public void parseNalUnit(){


		ReadFile nalHeader = new ReadFile(nalOffSet, nalUnitHeaderBytes,fileName);
		nalHeader.readBytes();
		String headerBits = nalHeader.TobitString();
		if(headerBits.charAt(0)=='0'){
		} else {
		}
		if(headerBits.charAt(1)=='0'&&headerBits.charAt(2)=='0'){

		} else{
			nal_ref_idc=Integer.parseInt(headerBits.charAt(1)+""+headerBits.charAt(2),2);
		}
		nal_unit_type = Integer.parseInt(""+headerBits.charAt(3)+headerBits.charAt(4)+headerBits.charAt(5)+headerBits.charAt(6)+headerBits.charAt(7),2);
		if(nal_unit_type==14|| nal_unit_type==20||nal_unit_type==21){
			if(nal_unit_type!=21){
			}else{
			}
			if(svc_extension_flag){
				nalUnitHeaderBytes = nalUnitHeaderBytes+3;
			} else if(avc_3d_extension_flag){
				nalUnitHeaderBytes=nalUnitHeaderBytes+2;
			} else {
				nalUnitHeaderBytes=nalUnitHeaderBytes+3;
			}
		}
		nalOffSet = nalOffSet+nalUnitHeaderBytes;
		rbsp_byte = new byte[length-nalUnitHeaderBytes];
		for(int i=nalUnitHeaderBytes;i<length;i++){
				ReadFile next_bits = new ReadFile(nalOffSet, 3,fileName);
				next_bits.readBytes();
				if(next_bits.ToHex().equals("000003")){
				}
				ReadFile readByte = new ReadFile(nalOffSet, 1,fileName);
				readByte.readBytes();
				rbsp_byte[NumBytesInRBSP++]=readByte.Getbyte();
				nalOffSet=nalOffSet+1;

		}
		NumBytesInRBSP=0;
	}


	public static void main(String args[]){
		mp4 mp4_0=new mp4();

		int nalOffsetTemp;
		sps sps0 = new sps(mp4_0.spsData);
		pps pps0=new pps(mp4_0.ppsData);
		for(int i = 0; i < (mp4_0.sampleOffsets.length); i++) {
			nalOffsetTemp = mp4_0.sampleOffsets[i];
			while(nalOffsetTemp < (mp4_0.sampleOffsets[i] + mp4_0.sampleSizes[i])) {
					nal test = new nal();
					ReadFile nallenght = new ReadFile(nalOffsetTemp, test.NAL_Unit_length_size,test.fileName);
					nallenght.readBytes();
					test.length = nallenght.ToDECIMAL(); /// NAL  UNIT TLENTH YAHAN SA PATA LAG RAHI HAI
					test.nalOffSet = nalOffsetTemp + test.NAL_Unit_length_size;
					test.parseNalUnit();
					// if(test.nal_unit_type==5){
						System.out.println("Nal Unit Type "+test.nal_unit_type);
						Scanner scan= new Scanner(System.in);
						int x=scan.nextInt();
						if(x==1){
							Slice s1=new Slice(test.rbsp_byte,sps0,pps0,test);
						}

					// }
					nalOffsetTemp=nalOffsetTemp+test.NAL_Unit_length_size + test.length;
				}
			}
		}

	}


