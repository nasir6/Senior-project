public class Slice{
	sps sps0;
	pps pps0;
	//***Header of slice***//
	parser p;
	int first_mb_in_slice; //uev
	int slice_type; //uev
	int pic_parameter_set_id;//uev
	int colour_plane_id; //u2
	int frame_num; //uv
	int field_pic_flag; //u1
	int bottem_field_flag; //u1
	int idr_pic_id; //uev
	int pic_order_cnt_lsb; //uv
	int delta_pic_order_cnt_bottom;//sev
	// delta_pic_order_cnt[0]

	// first_mb_in_slice specifies the address of the first macroblock in the slice.
// 	The first macroblock address of the slice is derived as follows:
// – If MbaffFrameFlag is equal to 0, first_mb_in_slice is the macroblock address of 
// the first macroblock in the slice, and first_mb_in_slice shall be in the range of 0 to PicSizeInMbs − 1, inclusive.
// – Otherwise (MbaffFrameFlag is equal to 1), first_mb_in_slice * 2 is the macroblock address of 
// the first macroblock in the slice, which is the top macroblock of the first macroblock pair in the slice, 
// and first_mb_in_slice shall be in the range of 0 to PicSizeInMbs / 2 − 1, inclusive.
// slice_type Name of slice_type
// 0	P (P slice)
// 1	B (B slice)
// 2	I (I slice)
// 3	SP (SP slice)
// 4	SI (SI slice)
// 5	P (P slice)
// 6	B (B slice)
// 7	I (I slice)
// 8	SP (SP slice)
// 9	SI (SI slice)

 // When nal_unit_type is equal to 5 (IDR picture), slice_type shall be equal to 2, 4, 7, or 9.
	// pic_parameter_set_id specifies the picture parameter set in use. 
	// The value of pic_parameter_set_id shall be in the range of 0 to 255, inclusive.
// 	colour_plane_id specifies the colour plane associated with the current slice RBSP 
// 	when separate_colour_plane_flag is equal to 1. The value of colour_plane_id 
// 	shall be in the range of 0 to 2, inclusive. colour_plane_id equal to 0, 1, and 2 correspond to the Y, Cb, and Cr planes, respectively.
// NOTE 2 – There is no dependency between the decoding processes of pictures having 
// different values of colour_plane_id.
// frame_num is used as an identifier for pictures and shall be represented by 
// log2_max_frame_num_minus4 + 4 bits in the bitstream. frame_num is constrained as follows:

// slice_layer_without_partitioning_rbsp( ) {
// C
// Descriptor
// slice_header( )
// 2
// slice_data( ) /* all categories of slice_data( ) syntax */
// 2 | 3 | 4
// rbsp_slice_trailing_bits( )
// 2
// }
	public void slice_header(){
		first_mb_in_slice = p.ExpGolombDecode();
		System.out.println("first_mb_in_slice " + first_mb_in_slice);
		slice_type = p.ExpGolombDecode();
		System.out.println("slice_type "+slice_type);


	}
	public void slice_layer_without_partitioning_rbsp(){
		slice_header();	
	}
	Slice(byte[] rbsp,sps sps_,pps pps_){
		sps0=sps_;
		pps0=pps_;
		p=new parser(rbsp);
		slice_layer_without_partitioning_rbsp();
	}

}