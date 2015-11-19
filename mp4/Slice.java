// 
// 7.3.3 Slice header syntax

public class Slice{
	sps sps0;
	pps pps0;
	nal nal0;
	boolean no_output_of_prior_pics_flag; //u1
	boolean long_term_reference_flag;//u1
	boolean adaptive_ref_pic_marking_mode_flag;//u1
	int memory_management_control_operation;//uev
	int difference_of_pic_nums_minus1;//uev
	int long_term_pic_num;//uev
	int long_term_frame_idx;//uev
	int max_long_term_frame_idx_plus1;//uev

	boolean IdrPicFlag;
	//***Header of slice***//
	parser p;
	int first_mb_in_slice; //uev
	int slice_type; //uev
	int pic_parameter_set_id;//uev
	int colour_plane_id; //u2
	int frame_num; //uv
	boolean field_pic_flag; //u1
	boolean bottom_field_flag; //u1
	int idr_pic_id; //uev
	int pic_order_cnt_lsb; //uv
	int delta_pic_order_cnt_bottom;//sev
	int delta_pic_order_cnt[];
	// int delta_pic_order_cnt
	int redundant_pic_cnt; //uev
	boolean direct_spatial_mv_pred_flag;
	boolean num_ref_idx_active_override_flag;
	int num_ref_idx_l0_active_minus1;//uev
	int num_ref_idx_l1_active_minus1; //uev
	int cabac_init_idc; //uev
	int slice_qp_delta;//sev
	boolean sp_for_switch_flag;//u1

	int slice_qs_delta; //sev
	int disable_deblocking_filter_idc; //sev
	int slice_alpha_c0_offset_div2; //sev
	int slice_beta_offset_div2;//sev
	int slice_group_change_cycle; //uv

	// slice data variables 
	int cabac_alignment_one_bit;
	boolean MbaffFrameFlag; // used for CurrMbAddress address calculation . sumran
							// saw the formulae for the C file found later, 
							// MbaffFrameFlag = sps0.mb_adaptive_frame_field_flag
							// && (fieldpicFlag == 0 )
	int CurrMbAddr;      // needed extensively for parsing slice data
	boolean moreDataFlag; //  
	boolean prevMbSkipped; //
	int mb_skip_run;
	boolean mb_field_decoding_flag;



	Slice(byte[] rbsp,sps sps_,pps pps_,nal nal_0){
		sps0=sps_;
		pps0=pps_;
		nal0=nal_0;
		p=new parser(rbsp);
		slice_layer_without_partitioning_rbsp();
	}
	public void slice_layer_without_partitioning_rbsp(){
		slice_header();	
		// slice_data();
		  // all categories of slice data syntax
		// rbsp_slice_trailing_bits(); // 
	}

/*	delta_pic_order_cnt[0]
	first_mb_in_slice specifies the address of the first macroblock in the slice.
	The first macroblock address of the slice is derived as follows:
	– If MbaffFrameFlag is equal to 0, first_mb_in_slice is the macroblock address of 
	the first macroblock in the slice, and first_mb_in_slice shall be in the range of 0 to PicSizeInMbs − 1, inclusive.
	– Otherwise (MbaffFrameFlag is equal to 1), first_mb_in_slice * 2 is the macroblock address of 
	the first macroblock in the slice, which is the top macroblock of the first macroblock pair in the slice, 
	and first_mb_in_slice shall be in the range of 0 to PicSizeInMbs / 2 − 1, inclusive.
	slice_type Name of slice_type
	0	P (P slice)
	1	B (B slice)
	2	I (I slice)
	3	SP (SP slice)
	4	SI (SI slice)
	5	P (P slice)
	6	B (B slice)
	7	I (I slice)
	8	SP (SP slice)
	9	SI (SI slice)

 	When nal_unit_type is equal to 5 (IDR picture), slice_type shall be equal to 2, 4, 7, or 9.
	pic_parameter_set_id specifies the picture parameter set in use. 
	The value of pic_parameter_set_id shall be in the range of 0 to 255, inclusive.
	colour_plane_id specifies the colour plane associated with the current slice RBSP 
	when separate_colour_plane_flag is equal to 1. The value of colour_plane_id 
	shall be in the range of 0 to 2, inclusive. colour_plane_id equal to 0, 1, and 2 correspond to the Y, Cb, and Cr planes, respectively.
	NOTE 2 – There is no dependency between the decoding processes of pictures having 
	different values of colour_plane_id.
	frame_num is used as an identifier for pictures and shall be represented by 
	log2_max_frame_num_minus4 + 4 bits in the bitstream. frame_num is constrained as follows:

	slice_layer_without_partitioning_rbsp( ) {
	C
	Descriptor
	slice_header( )
	2
	slice_data( )  all categories of slice_data( ) syntax 
	2 | 3 | 4
	rbsp_slice_trailing_bits( )
	2
	}
*/
	public void dec_ref_pic_marking(){
		if(IdrPicFlag){
			no_output_of_prior_pics_flag=p.getBit();
			long_term_reference_flag=p.getBit();
		}else{
			adaptive_ref_pic_marking_mode_flag=p.getBit();
			if(adaptive_ref_pic_marking_mode_flag){

				memory_management_control_operation=p.uev();
				while(memory_management_control_operation!=0){
					if(memory_management_control_operation==1||memory_management_control_operation==3){
						difference_of_pic_nums_minus1=p.uev();
					}
					if(memory_management_control_operation==2){
						long_term_pic_num=p.uev();
					}
					if(memory_management_control_operation==3
						||memory_management_control_operation==6){
						long_term_frame_idx=p.uev();
					}
					if(memory_management_control_operation==4){
						max_long_term_frame_idx_plus1=p.uev();
					}
					memory_management_control_operation=p.uev();
				}

			}
		}
	
	}





	public void slice_header(){
		// System.out.println("lame things" + pps0.entropy_coding_mode_flag);
		first_mb_in_slice = p.uev();
		// System.out.println("first_mb_in_slice " + first_mb_in_slice);
		slice_type = p.uev();
		// System.out.println("slice_type "+slice_type);
		pic_parameter_set_id=p.uev();
		// System.out.println("pic_parameter_set_id " +pic_parameter_set_id);
		if(sps0.separate_colour_plane_flag){
			colour_plane_id=p.readBits(2);
			// System.out.println("colour_plane_id "+colour_plane_id);

		}
		int v = sps0.log2_max_frame_num_minus4;
		frame_num=p.readBits(v+4);
		// System.out.println("frame_num "+frame_num);
		// setting the idr pic flag;
		if(nal0.nal_unit_type==5) {
			IdrPicFlag=true;	
		}else {
			IdrPicFlag=false;
		}
		// setting IDR

		if(!(sps0.frame_mbs_only_flag)){ // false , either a frame or a field , true , definetly frame 
			field_pic_flag=p.getBit(); // true field , false frame
			// System.out.println("field_pic_flag "+field_pic_flag); 
			if(field_pic_flag){
				bottom_field_flag=p.getBit();
			}
		}
		if(IdrPicFlag){
			// idr_pic_id=0;
			idr_pic_id=p.uev();
			// System.out.println("idr_pic_id "+idr_pic_id);
		}
		if(sps0.pic_order_cnt_type==0){
			// System.out.println("here");
			// pic_order_cnt_lsb specifies the picture 
			// order count modulo MaxPicOrderCntLsb for 
			// the top field of a coded frame or for a coded 
			// field. The length of the pic_order_cnt_lsb syntax 
			// element is log2_max_pic_order_cnt_lsb_minus4 + 4 
			// bits. The value of the pic_order_cnt_lsb shall be 
			// in the range of 0 to MaxPicOrderCntLsb − 1, 
			// inclusive.
			int n=sps0.log2_max_pic_order_cnt_lsb_minus4+4; //
			pic_order_cnt_lsb=p.readBits(n);
			// System.out.println("pic_order_cnt_lsb "+pic_order_cnt_lsb);
			if (pps0.bottom_field_pic_order_in_frame_present_flag&&!field_pic_flag) {
				delta_pic_order_cnt_bottom=p.sev();
				// System.out.println("delta_pic_order_cnt "+ delta_pic_order_cnt);

			}
		}
		delta_pic_order_cnt=new int[3]; // 
		if(sps0.pic_order_cnt_type==1&&sps0.delta_pic_order_always_zero_flag){
			delta_pic_order_cnt[0]=p.sev();
			// System.out.println("delta_pic_order_cnt "+delta_pic_order_cnt[0]);
		}
			// System.out.println("lame things " + pps0.bottom_field_pic_order_in_frame_present_flag);
			// System.out.println("lame things " + field_pic_flag);
		if(pps0.bottom_field_pic_order_in_frame_present_flag&&!field_pic_flag){
			delta_pic_order_cnt[1]=p.sev();
			// System.out.println("delta_pic_order_cnt 1 "+delta_pic_order_cnt[1]);

		}
			// System.out.println("lame things " + pps0.redundant_pic_cnt_present_flag);
		if(pps0.redundant_pic_cnt_present_flag){
			redundant_pic_cnt=p.uev();
			// System.out.println("redundant_pic_cnt "+redundant_pic_cnt);

		}
			//slice typee
		if(slice_type==1||slice_type==6){
			direct_spatial_mv_pred_flag=p.getBit();
		}
			//p sp b
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
		if(slice_type==0||slice_type==5||slice_type==3||slice_type==8
			||slice_type==6||slice_type==1){
			num_ref_idx_active_override_flag=p.getBit();
			if(num_ref_idx_active_override_flag){
				num_ref_idx_l0_active_minus1=p.uev();
				if(slice_type==1||slice_type==6){
					num_ref_idx_l1_active_minus1=p.uev();

				}
			}
		}
		if(nal0.nal_unit_type==20||nal0.nal_unit_type==21){
			//To be implemeted

			// ref_pic_list_mvc_modification( ) /* specified in Annex H */2
		}else{
			// ref_pic_list_modification()
		}

		if(pps0.weighted_pred_flag&&(slice_type==0||slice_type==5
			||slice_type==3||slice_type==8)||(pps0.weighted_bipred_idc==1&&(slice_type==1 ||slice_type==6)
			)){
			// pred_weight_table()
		}

		if(nal0.nal_ref_idc!=0){
			// System.out.println("dec_ref_pic_marking   ");
			// dec_ref_pic_marking();
			dec_ref_pic_marking();
		}
		if(pps0.entropy_coding_mode_flag&&slice_type!=7&&slice_type!=2&&slice_type!=4&&slice_type!=9){
			cabac_init_idc=p.uev();	
		}
		// System.out.println();
		// System.out.println("qp    "+p.sev());
		slice_qp_delta=p.sev();
		// System.out.println();

		if(slice_type==3||slice_type==8||slice_type==4||slice_type==9){
			if (slice_type==3||slice_type==8) {
				sp_for_switch_flag=p.getBit();
			}
			slice_qs_delta=p.sev();
		}
		if (pps0.deblocking_filter_control_present_flag) {
			disable_deblocking_filter_idc=p.uev();
			// System.out.println("disable_deblocking_filter_idc "+disable_deblocking_filter_idc);
			if (disable_deblocking_filter_idc!=1) {
				slice_alpha_c0_offset_div2=p.sev();
				slice_beta_offset_div2=p.sev();
				// System.out.println("slice_alpha_c0_offset_div2 "+slice_alpha_c0_offset_div2);
				// System.out.println("slice_beta_offset_div2 "+slice_beta_offset_div2);
			}
		}
		if (pps0.num_slice_groups_minus1>0&&pps0.slice_group_map_type>=3&&pps0.slice_group_map_type<=5) {
			// slice_group_change_cycle;

		}



			// System.out.println("false");
		// System.out.println("slice_qp_delta "+slice_qp_delta);
		// System.out.println("******** end of slice header ********");
	}
	

	public int NextMbAddress(int n) { // used im slice data , taken from
			

										// section 8.2.2

		if(pps0.num_slice_groups_minus1==1&&(pps0.slice_group_map_type==3||
			pps0.slice_group_map_type==4||pps0.slice_group_map_type==5)){
				// slice groups 0 and 1
				//clause 8.2.2.4-6
		}


		int MapUnitsInSliceGroup0 = Math.min((pps0->slice_group_change_rate_minus1 + 1) * slice_group_change_cycle, PicSizeInMapUnits);
		
		// int MapUnitsInSliceGroup0;
		// MapUnitsInSliceGroup0=PicSizeInMapUnits- MapUnitsInSliceGroup0;



		//
		// to be used when we cross the I slice thing 
		// i = n + 1;  
		// while(i < PicSizeInMbs && MbToSliceGroupMap[i] != MbToSliceGroupMap[n]) {
		// 	i++;
		// }
		return 1;
	}

	public void slice_data() {
		if(pps0.entropy_coding_mode_flag) {
			while(! p.byte_aligned()) {
				cabac_alignment_one_bit = p.readBits(1);
			}
		}


		MbaffFrameFlag = sps0.mb_adaptive_frame_field_flag && (field_pic_flag == false);
		
		CurrMbAddr = first_mb_in_slice * ( (MbaffFrameFlag ? 1 : 0) + 1 ); // bool to 
																		// int conversion 
		moreDataFlag = true;
		prevMbSkipped = false;
		do {

			if((slice_type != 2 || slice_type != 7) && 
					(slice_type != 4 || slice_type != 9)) {  
										
 				if(! pps0.entropy_coding_mode_flag) {
					mb_skip_run = p.uev();
					prevMbSkipped = (mb_skip_run > 0);
					for(int i = 0; i < mb_skip_run; i++) {
						CurrMbAddr = NextMbAddress(CurrMbAddr);
					}
					if(mb_skip_run > 0) {
						moreDataFlag = p.more_rbsp_data();
					}
				} else {
					//CABAC
					// mb_skip_flag=p.aev(); // aev read cant do right now ..surman 
					// more_rbsp_data = !mb_skip_flag;
				}
				// incomplete becuase we are still dealing with I type slice 
			}
			if(moreDataFlag) {
				if(MbaffFrameFlag && (CurrMbAddr % 2 == 0 ||
					 (CurrMbAddr % 2 == 1 && prevMbSkipped))) {
					mb_field_decoding_flag = p.getBit(); //
				}
				macroblock_layer();  
			}
			if(! pps0.entropy_coding_mode_flag) {
				moreDataFlag = p.more_rbsp_data();
			} else {
				 
				if((slice_type != 2 || slice_type != 7) && 
						(slice_type != 4 || slice_type != 9)) {

					// prevMbSkipped = mb_skip_flag; // CABAC
				}
				if(MbaffFrameFlag && CurrMbAddr % 2 == 0){
					moreDataFlag = true;

				} else {
					// 	end_of_slice_flag = entrpy coding .. (ae(v)); entropy coding // 
					// moreDataFlag = !end_of_slice_flag;
				}	
			}
			CurrMbAddr = NextMbAddress(CurrMbAddr);

		} while(moreDataFlag);
	}
		
	public void macroblock_layer() {
		// unimplemented



	}




}












