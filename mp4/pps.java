// 7.3.2.2 Picture parameter set RBSP syntax
public class pps{
	int pic_parameter_set_id;//uev
	int seq_parameter_set_id;//uev
	boolean entropy_coding_mode_flag;//u1
	boolean bottom_field_pic_order_in_frame_present_flag;//u1
	int num_slice_groups_minus1;//uev
	int slice_group_map_type; //uev
	int run_length_minus1[];
	int top_left[];//uev
	int bottom_right[];//uev
	boolean slice_group_change_direction_flag;//u1
	int slice_group_change_rate_minus1;//u1
	int pic_size_in_map_units_minus1;//uev
	int slice_group_id[];
	int num_ref_idx_l0_default_active_minus1;//uev
	int num_ref_idx_l1_default_active_minus1;//uev
	boolean weighted_pred_flag;//u1
	int weighted_bipred_idc;//u2
	int pic_init_qp_minus26;//sev
	int pic_init_qs_minus26;//uev
	int chroma_qp_index_offset;//uev
	boolean deblocking_filter_control_present_flag;//u1
	boolean constrained_intra_pred_flag;//u1
	boolean redundant_pic_cnt_present_flag;//u1
	boolean transform_8x8_mode_flag;//u1
	boolean pic_scaling_matrix_present_flag;//u1
	boolean pic_scaling_list_present_flag[];
	int second_chroma_qp_index_offset;//sev
	byte pps_set[];
	pps(){} // 


	pps(byte pps_set_0[]){
		pps_set=pps_set_0;
		parser p1 = new parser(pps_set);

		pic_parameter_set_id = p1.uev();
		seq_parameter_set_id = p1.uev();
		entropy_coding_mode_flag=p1.getBit();
		bottom_field_pic_order_in_frame_present_flag=p1.getBit();
		num_slice_groups_minus1=p1.uev();
		if (num_slice_groups_minus1>0) {
			slice_group_map_type=p1.uev();
			if (slice_group_map_type==0) {
				run_length_minus1=new int [num_slice_groups_minus1+1];
				for (int igroup=0;igroup<=num_slice_groups_minus1 ;igroup++ ) {
					run_length_minus1[igroup]=p1.uev();

					
				}

			}else if(slice_group_map_type==2){
				top_left=new int[num_slice_groups_minus1];
				bottom_right=new int[num_slice_groups_minus1];
				for (int igroup2=0;igroup2<num_slice_groups_minus1 ;igroup2++ ) {
					top_left[igroup2]=p1.uev();
					bottom_right[igroup2]=p1.uev();

				}
			}else if(slice_group_map_type==3||slice_group_map_type==4||
				slice_group_map_type==5){
				slice_group_change_direction_flag=p1.getBit();
				slice_group_change_rate_minus1=p1.uev();

			}else if(slice_group_map_type==6){
				pic_size_in_map_units_minus1=p1.uev();
				slice_group_id=new int[pic_size_in_map_units_minus1];
				for (int id0=0; id0<pic_size_in_map_units_minus1;id0++ ) {
					slice_group_id[id0]=p1.uev();
				}
			}
		}
		num_ref_idx_l0_default_active_minus1=p1.uev();
		num_ref_idx_l1_default_active_minus1=p1.uev();
		weighted_pred_flag=p1.getBit();
		weighted_bipred_idc=p1.readBits(2);
		pic_init_qp_minus26=p1.sev();
		pic_init_qs_minus26=p1.sev();

		chroma_qp_index_offset=p1.sev();
		deblocking_filter_control_present_flag=p1.getBit();
		constrained_intra_pred_flag=p1.getBit();
		redundant_pic_cnt_present_flag=p1.getBit();
		// if rbsp data is 
		//
		// System.out.println("pic_parameter_set_id "+pic_parameter_set_id);
		// System.out.println("seq_parameter_set_id "+seq_parameter_set_id);
		// System.out.println("entropy_coding_mode_flag "+entropy_coding_mode_flag);
		// System.out.println("bottom_field_pic_order_in_frame_present_flag "+bottom_field_pic_order_in_frame_present_flag);
		// System.out.println("num_slice_groups_minus1 "+num_slice_groups_minus1);
		// System.out.println("num_ref_idx_l0_default_active_minus1 "+num_ref_idx_l0_default_active_minus1);
		// System.out.println("num_ref_idx_l1_default_active_minus1 "+num_ref_idx_l1_default_active_minus1);
		// System.out.println("weighted_pred_flag "+weighted_pred_flag);
		// System.out.println("weighted_bipred_idc "+weighted_bipred_idc);
		// System.out.println("pic_init_qp_minus26 "+pic_init_qp_minus26);
		// System.out.println("pic_init_qs_minus26 "+pic_init_qs_minus26);
		// System.out.println("chroma_qp_index_offset "+chroma_qp_index_offset);
		// System.out.println("redundant_pic_cnt_present_flag "+redundant_pic_cnt_present_flag);
		
		boolean more_data=p1.more_rbsp_data();// check in rbsp for remainig bits
		transform_8x8_mode_flag=false;
		// System.out.println();
		// System.out.println("more data ");
		// System.out.println();
		if(more_data){

			transform_8x8_mode_flag=p1.getBit();
			pic_scaling_matrix_present_flag=p1.getBit();
			if (pic_scaling_matrix_present_flag) {
				System.out.println();
				System.out.println("Picture pic_scaling_matrix_present_flag flag is present do it ");
				System.out.println();
			}
		}

// extra bits to be impelmented 

	}



}