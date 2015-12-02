






import java.io.*;
import java.util.*;

public class nal
{
	int NAL_Unit_length_size=4;
	int length;
	int nalOffSet = 20083;//+489; /// 
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

		// ReadFile nalunit = new ReadFile(nalOffSet, NAL_Unit_length_size,fileName, "information[i]");
		// nalunit.readBytes();
		// byte[] s = nalunit.Getbytes();
		// parser p = new parser(s);
		// int ret = p.readBits(32);
		// System.out.println("parser ret ===-=  "+ret);
		ReadFile nallenght = new ReadFile(nalOffSet, NAL_Unit_length_size,fileName);
		nallenght.readBytes();
		length = nallenght.ToDECIMAL();
		System.out.println("length = "+length);
		nalOffSet=nalOffSet+NAL_Unit_length_size;

		ReadFile nalHeader = new ReadFile(nalOffSet, nalUnitHeaderBytes,fileName);
		nalHeader.readBytes();
		String headerBits = nalHeader.TobitString();
		if(headerBits.charAt(0)=='0'){
			System.out.println("valid syntax");
		} else {
			System.out.println("in valid syntax for nal unit");
		}
		if(headerBits.charAt(1)=='0'&&headerBits.charAt(2)=='0'){
			System.out.println("nal unit can be discarded");
			// return;

		} else{
			nal_ref_idc=Integer.parseInt(headerBits.charAt(1)+""+headerBits.charAt(2),2);
			System.out.println();
			System.out.println("nal_ref_idc == "+nal_ref_idc);
			System.out.println();
		}
		nal_unit_type = Integer.parseInt(""+headerBits.charAt(3)+headerBits.charAt(4)+headerBits.charAt(5)+headerBits.charAt(6)+headerBits.charAt(7),2);
		System.out.println("nal_unit_type == "+nal_unit_type);
		if(nal_unit_type==14|| nal_unit_type==20||nal_unit_type==21){
			if(nal_unit_type!=21){
				// read
				// svc_extension_flag=true;
			}else{
				// read
				// avc_3d_extension_flag=true;
			}
			if(svc_extension_flag){
				// nal_unit_header_svc_extension(); /* specified in Annex G */
				nalUnitHeaderBytes = nalUnitHeaderBytes+3;
			} else if(avc_3d_extension_flag){
				// nal_unit_header_3davc_extension(); /* specified in Annex J */
				nalUnitHeaderBytes=nalUnitHeaderBytes+2;
			} else {
				// nal_unit_header_mvc_extension();   /* specified in Annex H */
				nalUnitHeaderBytes=nalUnitHeaderBytes+3;
			}
		}
		nalOffSet = nalOffSet+nalUnitHeaderBytes;
		rbsp_byte = new byte[length-nalUnitHeaderBytes];
		// System.out.println(rbsp_byte.length);
		for(int i=nalUnitHeaderBytes;i<length;i++){
				ReadFile next_bits = new ReadFile(nalOffSet, 3,fileName);
				next_bits.readBytes();
				if(next_bits.ToHex().equals("000003")){
					System.out.println("emulation_prevention_three_byte ");
				}
			// if(i+2<NumBytesInNALunit && next_bits( 24 ) == 0x000003){
				// rbsp_byte[NumBytesInRBSP++];
				// rbsp_byte[NumBytesInRBSP++];
				// i=i+2;
				// emulation_prevention_three_byte equal to 0x000003
			// }else{
				ReadFile readByte = new ReadFile(nalOffSet, 1,fileName);
				readByte.readBytes();
				// length = nallenght.ToDECIMAL();
				rbsp_byte[NumBytesInRBSP++]=readByte.Getbyte();
				// System.out.print(""+readByte.ToHex());
				nalOffSet=nalOffSet+1;

			// }
		}
		NumBytesInRBSP=0;
	}

	// public void parseSlice(Slice slice_1){

	// }
	public static void main(String args[]){
		mp4 mp4_0=new mp4();
		// nal test = new nal();
		nal test = new nal();
		
		test.parseNalUnit();
		test.parseNalUnit();
		// if(test.nal_unit_type==5){

		// }
		sps sps0 = new sps(mp4_0.spsData);
		pps pps0=new pps(mp4_0.ppsData);
		// System.out.println("flag is    "+pps0.transform_8x8_mode_flag);
		// System.out.println("color "+sps0.separate_colour_plane_flag);
		// test.nalOffSet=mp4_0.
		Slice s1=new Slice(test.rbsp_byte,sps0,pps0,test);
	}

}


