public class sps{
	int profile_idc; //u8
	boolean constraint_set0_flag;//u1
	boolean constraint_set1_flag;//u1
	boolean constraint_set2_flag;//u1
	boolean constraint_set3_flag;//u1
	boolean constraint_set4_flag;//u1
	boolean constraint_set5_flag;//u1
	int reserved_zero_2bits; //u2
	int level_idc; //u8
	int seq_parameter_set_id; //uev
	int chroma_format_idc;//uev
	boolean separate_colour_plane_flag;//u1
	int bit_depth_luma_minus8;//uev
	int bit_depth_chroma_minus8;//uev
	boolean qpprime_y_zero_transform_bypass_flag;//u1
	boolean seq_scaling_matrix_present_flag;//u1
	boolean seq_scaling_list_present_flag[];

	int log2_max_frame_num_minus4; //uev
	int pic_order_cnt_type; //uev
	int log2_max_pic_order_cnt_lsb_minus4; //uev
	boolean	delta_pic_order_always_zero_flag;//u1
	int offset_for_non_ref_pic; //sev
	int offset_for_top_to_bottom_field; //sev
	int num_ref_frames_in_pic_order_cnt_cycle;//uev
	int offset_for_ref_frame[];
	int max_num_ref_frames;//uev

	// int num_ref_frames; //uev
	boolean gaps_in_frame_num_value_allowed_flag;//u1
	int pic_width_in_mbs_minus_1; //uev
	int pic_height_in_map_units_minus_1; //uev
	boolean frame_mbs_only_flag; //u1
	boolean mb_adaptive_frame_field_flag;//u1

	boolean direct_8x8_inference_flag;//u1
	boolean frame_cropping_flag;//u1
	int frame_crop_left_offset;//uev
	int frame_crop_right_offset;//uev
	int frame_crop_top_offset;//uev
	int frame_crop_bottom_offset;//uev

	boolean vui_parameters_present_flag; //u1
	byte seq_parameter_set[];

	// kea purpose hai khali constructor ka .......sumran 

	sps(){}
	
	sps(byte spsSet[]){
		separate_colour_plane_flag=false;
		chroma_format_idc=1;
		seq_parameter_set=spsSet;//  
		parser p1= new parser(seq_parameter_set);
		// System.out.println(seq_parameter_set.length);
		profile_idc=p1.readBits(8); //u8
		// System.out.println("profile_idc "+ profile_idc);
		constraint_set0_flag=p1.getBit();//u1
		constraint_set1_flag=p1.getBit();//u1
		constraint_set2_flag=p1.getBit();//u1
		constraint_set3_flag=p1.getBit();//u1
		constraint_set4_flag=p1.getBit();//u1
		constraint_set5_flag=p1.getBit();//u1
		// System.out.println("flags "+constraint_set0_flag+" "+constraint_set1_flag+" "+constraint_set2_flag+" "+constraint_set3_flag+" "
			// +constraint_set4_flag+" "+constraint_set5_flag+" ");
		reserved_zero_2bits=p1.readBits(2); //u2
		// System.out.println("reserved_zero_2bits "+ reserved_zero_2bits);
		level_idc=p1.readBits(8); //u8
		// System.out.println("level_idc "+level_idc);
		seq_parameter_set_id=p1.uev(); //uev
		// System.out.println("seq_parameter_set_id "+ seq_parameter_set_id);
		if(profile_idc==100||profile_idc==110||profile_idc==122
			||profile_idc==244||profile_idc==44||
			profile_idc==83||profile_idc==86||profile_idc==118||
			profile_idc==128||profile_idc==138||profile_idc==139||
			profile_idc==134){
			chroma_format_idc=p1.uev(); //uev
			// System.out.println("chroma_format_idc "+chroma_format_idc);
			if(chroma_format_idc==3){
				separate_colour_plane_flag=p1.getBit();

			}
			bit_depth_luma_minus8=p1.uev();
			// System.out.println("bit_depth_luma_minus8 "+ bit_depth_luma_minus8);
			bit_depth_chroma_minus8=p1.uev();
			// System.out.println("bit_depth_chroma_minus8 "+bit_depth_chroma_minus8);
			qpprime_y_zero_transform_bypass_flag=p1.getBit();
			// System.out.println("qpprime_y_zero_transform_bypass_flag "+ qpprime_y_zero_transform_bypass_flag);
			seq_scaling_matrix_present_flag=p1.getBit();
			// System.out.println("seq_scaling_matrix_present_flag "+seq_scaling_matrix_present_flag);
			if(seq_scaling_matrix_present_flag){
				int size = 0;
				if(chroma_format_idc!=3){
					size=8;
				}else{
					size=12;
				}
				seq_scaling_list_present_flag=new boolean[size];
				for(int flag0=0;flag0<size;flag0++){
					seq_scaling_list_present_flag[flag0]=p1.getBit();
					if(seq_scaling_list_present_flag[flag0]){
						if(flag0<6){
							//scaling_list4
						}else{
							// ScalingList8x8
						}
					}
				}
			}
		}
		log2_max_frame_num_minus4=p1.uev(); //uev
		// System.out.println("log2_max_frame_num_minus4 "+log2_max_frame_num_minus4);
		pic_order_cnt_type=p1.uev(); //uev
		// System.out.println("pic_order_cnt_type "+pic_order_cnt_type);
		if(pic_order_cnt_type==0){
			log2_max_pic_order_cnt_lsb_minus4=p1.uev();

		}else if(pic_order_cnt_type==1){
			delta_pic_order_always_zero_flag=p1.getBit();
			offset_for_non_ref_pic=p1.sev();
			offset_for_top_to_bottom_field=p1.sev();
			num_ref_frames_in_pic_order_cnt_cycle=p1.uev();
			offset_for_ref_frame = new int[num_ref_frames_in_pic_order_cnt_cycle];
			for(int offset0=0;offset0<num_ref_frames_in_pic_order_cnt_cycle;offset0++){
				offset_for_ref_frame[offset0]=p1.sev();

			}

		}
		// log2_max_pic_order_cnt_lsb_minus4=p1.ExpGolombDecode(); //uev
		max_num_ref_frames=p1.uev(); //uev
		// System.out.println("max_num_ref_frames "+max_num_ref_frames);
		gaps_in_frame_num_value_allowed_flag=p1.getBit();//u1
		// System.out.println("gaps_in_frame_num_value_allowed_flag "+gaps_in_frame_num_value_allowed_flag);
		pic_width_in_mbs_minus_1=p1.uev(); //uev
		// System.out.println("pic_width_in_mbs_minus_1 "+pic_width_in_mbs_minus_1);
		pic_height_in_map_units_minus_1=p1.uev(); //uev
		// System.out.println("pic_height_in_map_units_minus_1 "+pic_height_in_map_units_minus_1);
		frame_mbs_only_flag=p1.getBit(); //u1
		// System.out.println("frame_mbs_only_flag "+frame_mbs_only_flag);
		if(!frame_mbs_only_flag){
			mb_adaptive_frame_field_flag=p1.getBit();	
		}

		direct_8x8_inference_flag=p1.getBit();//u1
		// System.out.println("direct_8x8_inference_flag "+direct_8x8_inference_flag);
		frame_cropping_flag=p1.getBit();//u1
		// System.out.println("frame_cropping_flag "+frame_cropping_flag);
		if (frame_cropping_flag) {
			frame_crop_left_offset=p1.uev();
			frame_crop_right_offset=p1.uev();
			frame_crop_top_offset=p1.uev();
			frame_crop_bottom_offset=p1.uev();
				
		}

		vui_parameters_present_flag=p1.getBit(); //u1
		// System.out.println("vui_parameters_present_flag "+vui_parameters_present_flag);
		if(vui_parameters_present_flag){
			//vui_parameters();
		}
	}
}


