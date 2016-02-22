// slice_layer_without_partitioning_rbsp( ) {
// // C
// // Descriptor
// // slice_header( )
// 2
// slice_data( ) /* all categories of slice_data( ) syntax */
// 2 | 3 | 4
// rbsp_slice_trailing_bits( )
// 2
// }



// 7.3.3 Slice header syntax
// slice_header( ) {
// C
// Descriptor
// first_mb_in_slice
// 2
// ue(v)
// slice_type
// 2
// ue(v)
// pic_parameter_set_id
// 2
// ue(v)
// if( separate_colour_plane_flag = = 1 )
// colour_plane_id
// 2
// u(2)
// frame_num
// 2
// u(v)
// if( !frame_mbs_only_flag ) {
// field_pic_flag
// 2
// u(1)
// if( field_pic_flag )
// bottom_field_flag
// 2
// u(1)
// }
// if( IdrPicFlag )
// idr_pic_id
// 2
// ue(v)
// if( pic_order_cnt_type = = 0 ) {
// pic_order_cnt_lsb
// 2
// u(v)
// if( bottom_field_pic_order_in_frame_present_flag && !field_pic_flag )
// delta_pic_order_cnt_bottom
// 2
// se(v)
// }
// if( pic_order_cnt_type = = 1 && !delta_pic_order_always_zero_flag ) {
// delta_pic_order_cnt[ 0 ]
// 2
// se(v)
// 50 Rec. ITU-T H.264 (02/2014)
// if( bottom_field_pic_order_in_frame_present_flag && !field_pic_flag )
// delta_pic_order_cnt[ 1 ]
// 2
// se(v)
// }
// if( redundant_pic_cnt_present_flag )
// redundant_pic_cnt
// 2
// ue(v)
// if( slice_type = = B )
// direct_spatial_mv_pred_flag
// 2
// u(1)
// if( slice_type = = P | | slice_type = = SP | | slice_type = = B ) {
// num_ref_idx_active_override_flag
// 2
// u(1)
// if( num_ref_idx_active_override_flag ) {
// num_ref_idx_l0_active_minus1
// 2
// ue(v)
// if( slice_type = = B )
// num_ref_idx_l1_active_minus1
// 2
// ue(v)
// }
// }
// if( nal_unit_type = = 20 | | nal_unit_type = = 21 )
// ref_pic_list_mvc_modification( ) /* specified in Annex H */
// 2
// else
// ref_pic_list_modification( )
// 2
// if( ( weighted_pred_flag && ( slice_type = = P | | slice_type = = SP ) ) | | ( weighted_bipred_idc = = 1 && slice_type = = B ) )
// pred_weight_table( )
// 2
// if( nal_ref_idc != 0 )
// dec_ref_pic_marking( )
// 2
// if( entropy_coding_mode_flag && slice_type != I && slice_type != SI )
// cabac_init_idc
// 2
// ue(v)
// slice_qp_delta
// 2
// se(v)
// if( slice_type = = SP | | slice_type = = SI ) {
// if( slice_type = = SP )
// sp_for_switch_flag
// 2
// u(1)
// slice_qs_delta
// 2
// se(v)
// }
// if( deblocking_filter_control_present_flag ) {
// disable_deblocking_filter_idc
// 2
// ue(v)
// if( disable_deblocking_filter_idc != 1 ) {
// slice_alpha_c0_offset_div2
// 2
// se(v)
// slice_beta_offset_div2
// 2
// se(v)
// }
// }
// if( num_slice_groups_minus1 > 0 && slice_group_map_type >= 3 && slice_group_map_type <= 5)
// slice_group_change_cycle
// 2
// u(v)
// }