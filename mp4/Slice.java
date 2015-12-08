// 
// 7.3.3 Slice header syntax

public class Slice{
	sps sps0;
	pps pps0;
	nal nal0;
	int[] scalingList ={6,13,13,20,20,20,28,28,28,28,32,32,32,37,37,42};
	// int[] scalingList=new int[16];
	boolean no_output_of_prior_pics_flag; //u1
	boolean long_term_reference_flag;//u1
	boolean adaptive_ref_pic_marking_mode_flag;//u1
	int memory_management_control_operation;//uev
	int difference_of_pic_nums_minus1;//uev
	int long_term_pic_num;//uev
	int long_term_frame_idx;//uev
	int max_long_term_frame_idx_plus1;//uev
	// int PicSizeInMapUnits;
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
	// MbaffFrameFlag
	boolean MbaffFrameFlag; 
	int CurrMbAddr;      
	boolean moreDataFlag; //  
	boolean prevMbSkipped; //
	int mb_skip_run;
	boolean mb_field_decoding_flag;

	//MB layer

	String mb_type; //uev|aev
	int mbRow; // row in lookuptable for mb type
	int pcm_alignment_zero_bit;//f1
	int [] pcm_sample_luma; //uv [256]
	int [] pcm_sample_chroma; //2 * MbWidthC * MbHeightC // uv

	boolean transform_size_8x8_flag=false;//if not in bit stream //u1|aev
	int coded_block_pattern; //aev mev
	// boolean transform_size_8x8_flag; //u1|aev
	int mb_qp_delta; //sev|aev
	boolean noSubMbPartSizeLessThan8x8Flag;

	// mb_pred
	boolean[] prev_intra4x4_pred_mode_flag;//u1
	int[] rem_intra4x4_pred_mode;//u3
	boolean[] prev_intra8x8_pred_mode_flag;//u1
	int[] rem_intra8x8_pred_mode;//u3
	int intra_chroma_pred_mode; //uev
	int []ref_idx_l0;//tev
	int []ref_idx_l1;//tev
	int [][][]mvd_l0;//sev
	int[][][] mvd_l1; //sev
	int luma4x4BlkIdx;
	int cr4x4BlkIdx;
	int cb4x4BlkIdx;
	int blkA;
	int blkB;
	int CodedBlockPatternLuma,CodedBlockPatternChroma;
	int mbAddrA,mbAddrB,luma4x4BlkIdxB,luma4x4BlkIdxA;
	int[] Intra16x16DCLevel;
	int[][] Intra16x16ACLevel;
	int[][] LumaLevel4x4;
	int[][] LumaLevel8x8;

	int NumC8x8;
	int[][] ChromaDCLevel;
	int [][][] ChromaACLevel;


	int[] CbIntra16x16DCLevel;
	int[][] CbIntra16x16ACLevel;
	int[][] CbLevel4x4;
	int[][] CbLevel8x8;



	int[] CrIntra16x16DCLevel;
	int[][] CrIntra16x16ACLevel;
	int[][] CrLevel4x4;
	int[][] CrLevel8x8;

	int BitDepthY;
	int BitDepthC;
	int ChromaArrayType;
	boolean sMbFlag;
	int BitDepth;
	int qp;
	int QPY=0;
	int QpBdOffsetY;
	boolean TransformBypassModeFlag;
	boolean mbIsInterFlag;
	int PicWidthInSamplesL;
	int PicWidthInMbs;
	int [][] predL;
	Slice(byte[] rbsp,sps sps_,pps pps_,nal nal_0){
		// System.out.println("slice header");
		sps0=sps_;
		pps0=pps_;
		nal0=nal_0;
		p=new parser(rbsp);
		if(sps0.mb_adaptive_frame_field_flag&&!field_pic_flag){
			MbaffFrameFlag=true;
		}else{
			MbaffFrameFlag=false;
		}
		if(sps0.separate_colour_plane_flag==false){
			ChromaArrayType=sps0.chroma_format_idc;
		}else{
			ChromaArrayType=0;
		}
		BitDepthY=8+sps0.bit_depth_luma_minus8;
		PicWidthInMbs=sps0.pic_width_in_mbs_minus_1+1;
		// System.out.println("pic_width_in_mbs_minus_1 "+PicWidthInMbs);
		PicWidthInSamplesL=PicWidthInMbs*16;
		slice_layer_without_partitioning_rbsp();

		
		// If separate_colour_plane_flag is equal to 0, ChromaArrayType is set equal to chroma_format_idc.
		// – Otherwise (separate_colour_plane_flag is equal to 1), ChromaArrayType is set equal to 0.
	}
	public void slice_layer_without_partitioning_rbsp(){
		slice_header();	
		slice_data();
		  // all categories of slice data syntax
		// rbsp_slice_trailing_bits(); // 
	}


	public int LevelScale4x4(int m,int i,int j){
		int[] scalingList ={6,13,13,20,20,20,28,28,28,28,32,32,32,37,37,42};

		int [][] v=
		{{10, 16, 13},
		{11, 18, 14},
		{13,20,16},
		{14,23,18},
		{16,25,20},
		{18,29,23}};
		mbIsInterFlag=false;
		int iYCbCr;
		if(sps0.separate_colour_plane_flag){
			iYCbCr=colour_plane_id;
		}else {
			iYCbCr=0;
		}
		// 16*
		// normadjus
		int[][] weightScale4x4=Inverse_zigzag_process(scalingList);

		if(i%2==0&&j%2==0){
			// System.out.println(weightScale4x4[i][j]*v[m][0]+"LevelScale4x4");
			return weightScale4x4[i][j]*v[m][0];
			// return 16*v[m][0];
		}else if(i%2==1&&j%2==1){
			// return 16*v[m][1];

			return weightScale4x4[i][j]*v[m][1];
		}else{
			// return 16*v[m][2];

			return weightScale4x4[i][j]*v[m][2];
		}
	}
	int InverseRasterScan(int a,int b,int c,int d,int e){
		int ret=0;
		if(e==0){
			// System.out.println("a=="+d+"b "+b);
			ret=(a%(d/b))*b;
		}else if(e==1){
			ret=(a/(d/b))*c;
		}
		return ret;
	}
	// 8.5.12
	int [][] Scaling_and_transformation_process(int [][] c){

		BitDepth=8+sps0.bit_depth_luma_minus8;
		sMbFlag=false;
		if(!sMbFlag){
			QpBdOffsetY=6*sps0.bit_depth_luma_minus8;
			QPY=((QPY+mb_qp_delta+52+2*QpBdOffsetY)%(52+QpBdOffsetY))-QpBdOffsetY;
			qp=QPY+QpBdOffsetY;
		}else{
			// qp=qsy
			int QSY =26+pps0.pic_init_qs_minus26+slice_qs_delta;
			qp=QSY;
		}
		if(sps0.qpprime_y_zero_transform_bypass_flag&&QPY+QpBdOffsetY==0){
			TransformBypassModeFlag=true;
		}else if(!sps0.qpprime_y_zero_transform_bypass_flag||QPY+QpBdOffsetY!=0){
			TransformBypassModeFlag=false;
		}
		int [][] r=new int [4][4];
		if(TransformBypassModeFlag){
			for (int i=0;i<4 ;i++ ) {
				for(int j=0;j<4;j++){
					r[i][j]=c[i][j];
				}
			}
		} else if(!TransformBypassModeFlag){
			// clause 8.5.12.1
			int [][] d=new int [4][4];
			for(int i=0;i<4 ;i++){
				for(int j=0;j<4;j++){
					if(i==0&&j==0){
						d[i][j]=c[i][j];
					}else{
						if(qp>=24){
							d[i][j]=(c[i][j]*LevelScale4x4(qp%6,i,j))<<(qp/6 -4);
							// System.out.print(c[i][j]+" ");

						}else{
							d[i][j]=(int)(c[i][j]*LevelScale4x4(qp%6,i,j)+Math.pow(2,(3-qp/6)))>>(int)(4-qp/6);
							// System.out.print(d[i][j]+" ");

						}
					}
					// r[i][j]=c[i][j];
				}
			}
			// for(int i=0;i<4 ;i++){
			// 	for(int j=0;j<4;j++){
			// 		System.out.print(d[i][j]+" ");
			// 	}
			// 	System.out.println();
			// }
			// clause 8.5.12.2
			int [][] e=new int[4][4];
			int [][] h= new int[4][4];
			for(int i=0;i<4;i++){
				e[i][0]=d[i][0]+d[i][2];
			}
			for(int i=0;i<4;i++){
				e[i][1]=d[i][0]-d[i][2];
			}
			for(int i=0;i<4;i++){
				e[i][2]=(d[i][1]>>1) - d[i][3];
			}
			for(int i=0;i<4;i++){
				e[i][3]=d[i][1]+d[i][3]>>1;
			}
			int[][] f=new int[4][4];
			for(int i=0;i<4;i++){
				f[i][0]=e[i][0]+e[i][3];
			}
			for(int i=0;i<4;i++){
				f[i][1]=e[i][1]+e[i][2];
			}
			for(int i=0;i<4;i++){
				f[i][2]=e[i][1]-e[i][2];
			}
			for(int i=0;i<4;i++){
				f[i][3]=e[i][0]-e[i][3];
			}
			int [][] g= new int[4][4];

			for(int i=0;i<4;i++){
				g[0][i]=f[0][i]+f[2][i];
			}
			for(int i=0;i<4;i++){
				g[1][i]=f[0][i]-f[2][i];
			}
			for(int i=0;i<4;i++){
				g[2][i]=(f[1][i]>>1)-f[3][i];
			}
			for(int i=0;i<4;i++){
				g[3][i]=f[1][i]+(f[3][i]>>1);
			}
			for(int i=0;i<4;i++){
				h[0][i]=g[0][i]+g[3][i];
			}
			
			for(int i=0;i<4;i++){
				h[1][i]=g[1][i]+g[2][i];
			}

			for(int i=0;i<4;i++){
				h[2][i]=g[1][i]-g[2][i];
			}

			for(int i=0;i<4;i++){
				h[3][i]=g[0][i]-g[3][i];
			}

			for(int i=0;i<4 ;i++){
				for(int j=0;j<4;j++){
					// System.out.println(h[i][j]+" ");
					r[i][j]=(int)(h[i][j]+Math.pow(2,5))>>6;
					// System.out.print(" "+r[i][j]);
				}
			}
		}
		return r;
	}
	// clause 8.5.6-zigzag
	public int[][] Inverse_zigzag_process(int [] input){

		int [][]ret=new int [4][4];
		int[] zigzag={0,1,5,6
			,2,4,7,12
			,3,8,1,13
			,9,10,14,15};
		int index=0;
		for(int i=0;i<4;i++){
			for(int j=0;j<4;j++){
				ret[i][j]=input[zigzag[index]];
				index++;
			}
		}
		return ret;
	}
	public int[][] matrixMul(int[][] a ,int[][] b){
		int [][] c=new int[4][4];
		for (int i = 0; i < 4; i++)
        {
            for (int j = 0; j < 4; j++)
            {
                for (int k = 0; k < 4; k++)
                {
                    c[i][j] = c[i][j] + a[i][k] * b[k][j];
                }
            }
        }
        return c;
	}
	public void Transform_coefficient_decoding(){
		// predL[ x, y ] = ( 1 << ( BitDepthY − 1 ) ), with x, y = 0..15
		predL=new int[16][16];
		System.out.println("BitDepth "+BitDepthY);

		for(int i=0;i<16;i++){
			for(int j=0;j<16;j++){
				predL[i][j]=(1<<(BitDepthY-1));
				// System.out.print(predL[i][j] +" ");
			}
		}

		int [][] SL=new int [16][16];
		int nE,x0,y0;
		int xp=0;
		int yp=0;
		int [][] c,r;
		int [][] rMB=new int[16][16];
		// int LumaLevel4x4
		int[] zigzag={0,1,5,6
			,2,4,7,12
			,3,8,1,13
			,9,10,14,15};
		int[] fieldScan={0,2,8,12,
			1,5,9,13,
			3,6,10,14,
			4,7,11,15};
		// 8.5.1
		if(!transform_size_8x8_flag){
			for(luma4x4BlkIdx=0;luma4x4BlkIdx<16;luma4x4BlkIdx++){
					//clause 8.5.6
				// input
				// LumaLevel4x4[luma4x4BlkIdx];
				int index=0;
				c = new int[4][4];
				for(int i=0;i<4;i++){
					for (int j=0;j<4 ;j++ ) {
						c[i][j]=LumaLevel4x4[luma4x4BlkIdx][zigzag[index]];
						index++;
					}
				}
				r=Scaling_and_transformation_process(c);
				if(TransformBypassModeFlag&& MbPartPredMode(mbRow,0).equals("Intra_4x4")){
					System.out.println("implemetation of clause 8.5.15");
				}
				// clause 6.4.3
				int x=InverseRasterScan(luma4x4BlkIdx/4,8,8,16,0)+
				InverseRasterScan(luma4x4BlkIdx%4,4,4,8,0);
				int y=InverseRasterScan(luma4x4BlkIdx/4,8,8,16,1)+
				InverseRasterScan(luma4x4BlkIdx%4,4,4,8,1);
				int[][] u=new int [4][4];
				for(int i=0;i<4 ;i++){
					for(int j=0;j<4;j++){
						// "    "
						// u[i][j]=clip1y(predL[x+j,y+i]+r[i][j]);

						u[i][j]=clip1y(predL[x+j][i+y]+r[i][j]);
						// System.out.print(" "+u[i][j]);
					}
					// System.out.println();
				}

				// clause 6.4.1
				
				if(MbaffFrameFlag==false){
					// System.out.println("flag is false");
					xp=InverseRasterScan(CurrMbAddr,16,16,PicWidthInSamplesL,0);
					yp=InverseRasterScan(CurrMbAddr,16,16,PicWidthInSamplesL,1);
				}else if (MbaffFrameFlag==true){
					x0=InverseRasterScan(CurrMbAddr/2,16,32,PicWidthInSamplesL,0);
					y0=InverseRasterScan(CurrMbAddr/2,16,32,PicWidthInSamplesL,1);
					xp=x0;
					yp=y0+(CurrMbAddr%2)*16;
				}

				nE=4;
				x0=InverseRasterScan(luma4x4BlkIdx/4,8,8,16,0)+
				InverseRasterScan(luma4x4BlkIdx%4,4,4,8,0);

				y0=InverseRasterScan(luma4x4BlkIdx/4,8,8,16,1)+
				InverseRasterScan(luma4x4BlkIdx%4,4,4,8,1);
				for(int i=0;i<nE;i++){
					for(int j=0;j<nE;j++){
						if(!MbaffFrameFlag){
							SL[xp+x0+j][yp+y0+i]=u[i][j];
						}
					}
				}

				// rMB

			}	
		}
		// 8.5.2
		int [][]dcy;
		int m=0;
		int n=0;
		// System.out.println(TransformBypassModeFlag+" TransformBypassModeFlag");
		if(MbPartPredMode(mbRow,0).equals("Intra_16x16")){
			int [] lumaList=new int[16];
			c=Inverse_zigzag_process(Intra16x16DCLevel);
			dcy=transformation_process_for_DC(c);
			// System.out.println("dcy "+dcy[0][0]);
			for(luma4x4BlkIdx=0;luma4x4BlkIdx<16;luma4x4BlkIdx++){
				lumaList[0]=dcy[m][n];
				n++;
				if(n==4){
					n=0;
					m++;
				}
				for(int k=1;k<16;k++){
					lumaList[k]=Intra16x16ACLevel[luma4x4BlkIdx][k-1];

				}
				c=Inverse_zigzag_process(lumaList);
				
				r=Scaling_and_transformation_process(c);
				// for(int i=0;i<4;i++){
				// 	for(int j=0;j<4;j++){
				// 		System.out.print(r[i][j]+" ");
				// 	}	
				// 	System.out.println();
				// }
				int [] xy=Inverse_4x4_luma_block_scanning_process(luma4x4BlkIdx);
				x0=xy[0];
				y0=xy[1];
				for(int i=0;i<4;i++){
					for(int j=0;j<4;j++){
						rMB[x0+j][y0+i]=r[i][j];

					}
				}


			}
			
			int [][] u=new int [16][16];
			for(int i=0;i<16;i++){
				for(int j=0;j<16;j++){
					// uij = Clip1Y( predL[ j, i ] + rMb[ j, i ] )
					u[i][j]=clip1y(predL[j][i]+rMB[j][i]);
					// System.out.print(rMB[i][j]+" ");
				}	
				// System.out.println();
			}
			for(int i=0;i<16;i++){
					for(int j=0;j<16;j++){
						// System.out.print(u[i][j]+" ");
					}	
					// System.out.println();
			}
			x0=0;
			y0=0;
			nE=16;
			for(int i=0;i<nE;i++){
				for(int j=0;j<nE;j++){
					if(MbaffFrameFlag){
						SL[xp+x0+j][yp+2*(y0+i)]=u[i][j];
					}else if(!MbaffFrameFlag){
						SL[xp+x0+j][yp+y0+i]=u[i][j];
						// System.out.println();
					}
					// u[i][j]=clip1y(0+rMB[i][j]);
					// System.out.print(rMB[i][j]+" ");
				}	
				// System.out.println();
			}

			for(int i=0;i<nE;i++){
				for(int j=0;j<nE;j++){
					System.out.print(SL[i][j]+" ");
				}
				System.out.println();
			}
			



		}


		
	}
	// 8.5.10
	public int[][] transformation_process_for_DC(int [][]c){
		// qp'y
		int [][] dcy=new int[4][4];
		int [][] a=
		{{1,1,1,1},
		{1,1,-1,-1},
		{1,-1,-1,1},
		{1,-1,1,-1}};
		
		int [][]f;
		
		if(TransformBypassModeFlag){
			for (int i=0;i<4 ;i++ ) {
				for (int j=0;j<4 ;j++ ) {
					dcy[i][j]=c[i][j];
				}
			}
		}else if (!TransformBypassModeFlag){
			f=matrixMul(a,c);
			f=matrixMul(f,a);
		
		// QPY=((QPY+mb_qp_delta+52+2*QpBdOffsetY)%(52+QpBdOffsetY))-QpBdOffsetY;
			qp=QPY+QpBdOffsetY;
			// qp=QPY;

			// System.out.println("qp "+qp);
			if(qp>=36){
				// System.out.println(f[0][0]+"fij");
				for (int i=0;i<4 ;i++ ) {
					for (int j=0;j<4 ;j++ ) {

						dcy[i][j]=(f[i][j]*LevelScale4x4(qp%6,0,0))<<(qp/6 -6);
					}
				}
			}else if(qp<36){
				for (int i=0;i<4 ;i++ ) {
					for (int j=0;j<4 ;j++ ) {
						dcy[i][j]=(f[i][j]*LevelScale4x4(qp%6,0,0)+(1<<(5-qp/6)))>>(6-qp/6);
					}
				}
			}
		}
		// for (int i=0;i<4 ;i++ ) {
		// 		for (int j=0;j<4 ;j++ ) {
		// 			System.out.print(dcy[i][j]+" ");
		// 			}
		// 			System.out.println();
		// 	}
		return dcy;
	}
	public int[] Inverse_4x4_luma_block_scanning_process(int index){

		int x=InverseRasterScan(index/4,8,8,16,0)+
		InverseRasterScan(index%4,4,4,8,0);
		int y=InverseRasterScan(index/4,8,8,16,1)+
		InverseRasterScan(index%4,4,4,8,1);
		int[] ret = new int[2];
		ret[0]=x;
		ret[1]=y;
		return ret;
	}
	public int clip1y(int x){
		return clip3(0,(1<<BitDepthY)-1,x);
	}
	public int clip3(int x, int y,int z){
		if(z<x){
			return x;
		}else if(z>y){
			return y;
		}else{
			return z;
		}
	}

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
		field_pic_flag=false;
		if(!(sps0.frame_mbs_only_flag)){ // false , either a frame or a field , true , definetly frame 
			field_pic_flag=p.getBit(); // true field , false frame
			System.out.println("field_pic_flag "+field_pic_flag); 
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
		// System.out.println("here ");
		slice_group_change_cycle=0;
		if (pps0.num_slice_groups_minus1>0&&pps0.slice_group_map_type>=3&&pps0.slice_group_map_type<=5) {
			// slice_group_change_cycle;
			int bits = (int)Math.ceil(Math.log((pps0.pic_size_in_map_units_minus1+1)/(pps0.slice_group_change_rate_minus1+1)+1));
			slice_group_change_cycle=p.readBits(bits);
			// System.out.println("bits "+bits);
			// Ceil( Log2( PicSizeInMapUnits / SliceGroupChangeRate + 1 ) )
		}



			// System.out.println("false");
		// System.out.println("slice_group_change_cycle "+slice_group_change_cycle);
		// System.out.println("******** end of slice header ********");
	}
	

	public int NextMbAddress(int n) { // used im slice data , taken from
		// 8.2.2



		//The variables PicHeightInMapUnits and PicSizeInMapUnits are derived as
		// PicHeightInMapUnits = pic_height_in_map_units_minus1 + 1 (7-16)
		// PicSizeInMapUnits = PicWidthInMbs * PicHeightInMapUnits
		int PicSizeInMapUnits= (sps0.pic_width_in_mbs_minus_1+1)*(sps0.pic_height_in_map_units_minus_1+1);
		int FrameHeightInMbs = (2-(sps0.frame_mbs_only_flag ? 1:0))*(sps0.pic_height_in_map_units_minus_1+1);
 		int PicHeightInMbs = FrameHeightInMbs / (1+(field_pic_flag ? 1:0));

 		int PicSizeInMbs = (sps0.pic_width_in_mbs_minus_1+1) * (PicHeightInMbs);
		// (7-34)
		int MapUnitsInSliceGroup0 = Math.min(((pps0.slice_group_change_rate_minus1 + 1) * slice_group_change_cycle), 
			(PicSizeInMapUnits));
		// System.out.println(MapUnitsInSliceGroup0);
		int sizeOfUpperLeftGroup;
		if(pps0.num_slice_groups_minus1==1&&(pps0.slice_group_map_type==4||pps0.slice_group_map_type==5)){

			sizeOfUpperLeftGroup=(pps0.slice_group_change_direction_flag ? (PicSizeInMapUnits - MapUnitsInSliceGroup0)
				: MapUnitsInSliceGroup0);
		}
		int [] mapUnitToSliceGroupMap=new int[PicSizeInMapUnits];
		if(pps0.num_slice_groups_minus1==0){
			// System.out.println("zero  ");
			for(int i=0;i<pps0.pic_size_in_map_units_minus1+1;i++){
				mapUnitToSliceGroupMap[i]=0;
			}			
		}

		else if(pps0.num_slice_groups_minus1!=0){
			if(pps0.slice_group_map_type==0){
				// 8.2.2.1
			}
			else if(pps0.slice_group_map_type==1){
				// 8.2.2.2
			}else if(pps0.slice_group_map_type==2){
				// 8.2.2.3
			}else if(pps0.slice_group_map_type==3){
				// 8.2.2.4
			}else if(pps0.slice_group_map_type==4){
				// 8.2.2.5
			}
			else if(pps0.slice_group_map_type==5){
				// 8.2.2.6
			}
			else if(pps0.slice_group_map_type==6){
				// 8.2.2.7
			}
		}


									/* 8.2.2.8 */
 		int[] MbToSliceGroupMap=new int[PicSizeInMbs];
 		for(int i=0;i<PicSizeInMbs;i++){
 			if(sps0.frame_mbs_only_flag==true||field_pic_flag==true){
 				// System.out.println(PicSizeInMbs+" "+PicSizeInMapUnits);
 				MbToSliceGroupMap[i]=mapUnitToSliceGroupMap[i];
 			} else if(MbaffFrameFlag){
 				MbToSliceGroupMap[i]=mapUnitToSliceGroupMap[(int)i/2];

 			}else if(sps0.frame_mbs_only_flag==false&&sps0.mb_adaptive_frame_field_flag==false&&field_pic_flag==false){
 				MbToSliceGroupMap[i]=mapUnitToSliceGroupMap[(int)(i/(2*sps0.pic_width_in_mbs_minus_1+1))
 				*sps0.pic_width_in_mbs_minus_1+1+(i%sps0.pic_width_in_mbs_minus_1+1)];
 			}

 		}
		// i = n + 1 
		// while( i < PicSizeInMbs && MbToSliceGroupMap[ i ] != MbToSliceGroupMap[ n ] )
		 // i++; 
		// nextMbAddress = i
		int i = n + 1;
		// int nextMbAddress = i;

		while(i<PicSizeInMbs &&( MbToSliceGroupMap[i]!=MbToSliceGroupMap[n])) {
			i++;
			// System.out.println("here   mb address");
			// nextMbAddress = i;
		}
		System.out.println("nextMbAddress "+i);
		return i;
	}

	public void slice_data() {
		if(pps0.entropy_coding_mode_flag) {
			while(! p.byte_aligned()) {
				// System.out.println("entropy mode");
				cabac_alignment_one_bit = p.readBits(1);
			}
		}

		MbaffFrameFlag = sps0.mb_adaptive_frame_field_flag && (field_pic_flag == false);
		
		CurrMbAddr = first_mb_in_slice * ( (MbaffFrameFlag ? 1 : 0) + 1 ); // bool to 
																		// int conversion 
		moreDataFlag = true;
		prevMbSkipped = false;
		while(moreDataFlag) {
			if(!(slice_type == 2 || slice_type == 7) && 
					!(slice_type == 4 || slice_type == 9)) {  
										
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
					// moreDataFlag = !mb_skip_flag;
				}
				// incomplete becuase we are still dealing with I type slice 
			}
			// System.out.println("moreDataFlag"+moreDataFlag);
			if(moreDataFlag) {
				// System.out.println("here moreDataFlag");
				if(MbaffFrameFlag && (CurrMbAddr % 2 == 0 ||
					 (CurrMbAddr % 2 == 1 && prevMbSkipped))) {
					mb_field_decoding_flag = p.getBit(); //
				}
				macroblock_layer();  
				break;
			}
			if(! pps0.entropy_coding_mode_flag) {
				moreDataFlag = p.more_rbsp_data();
			} else {
				 
				if(!(slice_type == 2 || slice_type == 7) && 
						!(slice_type == 4 || slice_type == 9)) {

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

		}
	}
	public int getSubWidthC(){
		if(sps0.chroma_format_idc==1&&!sps0.separate_colour_plane_flag){
			return 2;
		}else if(sps0.chroma_format_idc==2&&!sps0.separate_colour_plane_flag){
			return 2;
		
		}else if(sps0.chroma_format_idc==3&&!sps0.separate_colour_plane_flag){
			return 1;
		}
		return 0;
	}
		
	public int getSubHeightC(){
		if(sps0.chroma_format_idc==1&&!sps0.separate_colour_plane_flag){
			return 2;
		}else if(sps0.chroma_format_idc==2&&!sps0.separate_colour_plane_flag){
			return 1;
		
		}else if(sps0.chroma_format_idc==3&&!sps0.separate_colour_plane_flag){
			return 1;
		}
		return 0;
	} 
	public String MbPartPredMode(int mbType,int n){
		String ret =p.Mb_Type("table7.11.txt",mbType,3);
		return ret;
	}
	public void setnC(){
		mbAddrA=CurrMbAddr;// or to the left;
		luma4x4BlkIdxA=luma4x4BlkIdx; // or to the left of it;


		mbAddrB=CurrMbAddr;// or above the curr;
		luma4x4BlkIdxB=luma4x4BlkIdx; // or above the 4x4
		//for mb ==0
		p.nC=0;
		

		// x=InverseRasterScan(luma4x4BlkIdx/4,8,8,16,0)+InverseRasterScan(luma4x4BlkIdx % 4,4,4,8,0);// (6-17)
		// y=InverseRasterScan(luma4x4BlkIdx/4,8,8,16,1)+InverseRasterScan(luma4x4BlkIdx % 4,4,4,8,1);//

		// int xD=-1;
		// int yD=-1;

		// 3. The luma location ( xN, yN ) is specified by:
		// int xN= x + xD;// (6-25)
		// int yN= y + yD;// (6-26)
		// 4. The derivation process for neighbouring locations as specified in clause 
		// 6.4.12 is invoked for luma locations with ( xN, yN ) as the input and the 
		// output is assigned to mbAddrN and ( xW, yW ).

		// int xW,yW;
		// 5. The variable luma4x4BlkIdxN is derived as follows:
		// – If mbAddrN is not available, luma4x4BlkIdxN is marked as not available.
		// – Otherwise (mbAddrN is available), the derivation process for 4x4 luma block indices as specified in clause 6.4.13.1 is invoked with the luma location ( xW, yW ) as the input and the output is assigned to luma4x4BlkIdxN.




		
		// – Otherwise, if the CAVLC parsing process is invoked for CbIntra16x16DCLevel, CbIntra16x16ACLevel, or CbLevel4x4, the process specified in clause 6.4.11.6 is invoked with cb4x4BlkIdx as the input, and the output is assigned to mbAddrA, mbAddrB, cb4x4BlkIdxA, and cb4x4BlkIdxB. The 4x4 Cb block specified by mbAddrA\cb4x4BlkIdxA is assigned to blkA, and the 4x4 Cb block specified by mbAddrB\cb4x4BlkIdxB is assigned to blkB.
		// – Otherwise, if the CAVLC parsing process is invoked for CrIntra16x16DCLevel, CrIntra16x16ACLevel, or CrLevel4x4, the process specified in clause 6.4.11.6 is invoked with cr4x4BlkIdx as the input, and the output is assigned to mbAddrA, mbAddrB, cr4x4BlkIdxA, and cr4x4BlkIdxB. The 4x4 Cr block specified by mbAddrA\cr4x4BlkIdxA is assigned to blkA, and the 4x4 Cr block specified by mbAddrB\cr4x4BlkIdxB is assigned to blkB.
		// – Otherwise (the CAVLC parsing process is invoked for ChromaACLevel), the process specified in clause 6.4.11.5 is invoked with chroma4x4BlkIdx as input, and the output is assigned to mbAddrA, mbAddrB, chroma4x4BlkIdxA, and chroma4x4BlkIdxB. The 4x4 chroma block specified by mbAddrA\iCbCr\chroma4x4BlkIdxA is assigned to blkA, and the 4x4 chroma block specified by mbAddrB\iCbCr\chroma4x4BlkIdxB is assigned to blkB.
		// 5. The variable availableFlagN with N being replaced by A and B is derived as follows:
		// – If any of the following conditions are true, availableFlagN is set equal to 0:
		// – mbAddrN is not available,
		// – the current macroblock is coded using an Intra macroblock prediction mode, constrained_intra_pred_flag is equal to 1, mbAddrN is coded using an Inter macroblock prediction mode, and slice data partitioning is in use (nal_unit_type is in the range of 2 to 4, inclusive).
		// – Otherwise, availableFlagN is set equal to 1.
		// 6. For N being replaced by A and B, when availableFlagN is equal to 1, the variable nN is derived as follows:
		// – If any of the following conditions are true, nN is set equal to 0:
		// – The macroblock mbAddrN has mb_type equal to P_Skip or B_Skip,
		// – The macroblock mbAddrN has mb_type not equal to I_PCM and all AC residual transform coefficient levels of the neighbouring block blkN are equal to 0 due to the corresponding bit of CodedBlockPatternLuma or CodedBlockPatternChroma being equal to 0.
		// – Otherwise, if mbAddrN is an I_PCM macroblock, nN is set equal to 16.
		// – Otherwise, nN is set equal to the value TotalCoeff( coeff_token ) of the neighbouring block blkN.
		// NOTE 1 – The values nA and nB that are derived using TotalCoeff( coeff_token ) do not include the DC transform coefficient levels in Intra_16x16 macroblocks or DC transform coefficient levels in chroma blocks, because these transform coefficient levels are decoded separately. When the block above or to the left belongs to an Intra_16x16 macroblock, nA or nB is the number of decoded non-zero AC transform coefficient levels for the adjacent 4x4 block in the Intra_16x16 macroblock. When the block above or to the left is a chroma block, nA or nB is the number of decoded non-zero AC transform coefficient levels for the adjacent chroma block.
		// NOTE 2 – When parsing for Intra16x16DCLevel, CbIntra16x16DCLevel, or CrIntra16x16DCLevel, the values nA and nB are based on the number of non-zero transform coefficient levels in adjacent 4x4 blocks and not on the number of non-zero DC transform coefficient levels in adjacent 16x16 blocks.
		// 7. The variable nC is derived as follows:
		// – If availableFlagA is equal to 1 and availableFlagB is equal to 1, the variable nC is set equal to ( nA + nB + 1 ) >> 1.
		// – Otherwise, if availableFlagA is equal to 1 (and availableFlagB is equal to 0), the variable nC is set equal to nA.
		// – Otherwise, if availableFlagB is equal to 1 (and availableFlagA is equal to 0), the variable nC is set equal to nB.
		// – Otherwise (availableFlagA is equal to 0 and availableFlagB is equal to 0), the variable nC is set equal to 0.
		// When maxNumCoeff is equal to 15, it is a requirement of bitstream conformance that the value of TotalCoeff( coeff_token ) resulting from decoding coeff_token shall not be equal to 16.
		// Table 9-5 – coeff_token mapping to TotalCoeff( coeff_token ) and TrailingOnes( coeff_token )

	}
	public void ChromaDCLevelnC(){
		if(ChromaArrayType==1){
			p.nC=-1;
		}else if(ChromaArrayType==2){
			p.nC=-2;
		}			
	}

	public void residual(int startIdx,int endIdx){
		int[] i16x16DClevel=new int[16];
		int[][] i16x16AClevel=new int[16][16];
		int[][] level4x4=new int[16][16];
		int[][] level8x8=new int[16][64];
		NumC8x8=4/(getSubHeightC()*getSubWidthC());
		ChromaDCLevel=new int[2][NumC8x8*4];
		ChromaACLevel=new int[2][NumC8x8*4+4][15];
		
		// int NumC8x8=4/(getSubHeightC()*getSubWidthC());
		// int[][] ChromaDCLevel=new int[2][NumC8x8*4];
		// int [][][] ChromaACLevel=new int[2][NumC8x8][4];
		// int[] CbIntra16x16DCLevel;
		// int[][] CbIntra16x16ACLevel;
		// int[][] CbLevel4x4;
		// int[][] CbLevel8x8;
		// // residual_luma(i16x16DClevel,i16x16AClevel,level4x4,level8x8,startIdx,endIdx);
		// int[] CrIntra16x16DCLevel;
		// int[][] CrIntra16x16ACLevel;
		// int[][] CrLevel4x4;
		// int[][] CrLevel8x8;

		// System.out.println("residual");
		if(!pps0.entropy_coding_mode_flag){
			// int []residual_block=residual_block_cavlc();
			// int []residual_block= new int[16];
			// setnC();

			// p.residual_block_cavlc(residual_block,0,15,16);
			// System.out.println("residual_block ");
			// for (int i=0;i<16 ;i++ ) {
			// 	System.out.print(residual_block[i]+" ");
			// }
			System.out.println();
		}else{
			// int []residual_block=residual_block_cabac();
		}
		luma4x4BlkIdx=0;
		// System.out.println("residual_luma");
		residual_luma(i16x16DClevel,i16x16AClevel,level4x4,level8x8,startIdx,endIdx);
		Intra16x16DCLevel=i16x16DClevel;
		Intra16x16ACLevel=i16x16AClevel;
		LumaLevel4x4=level4x4;
		LumaLevel8x8=level8x8;
		// System.out.println("ChromaArrayType "+ChromaArrayType);
		if(ChromaArrayType==1||ChromaArrayType==2){
			for(int iCbCr=0;iCbCr<2;iCbCr++){
				if(((CodedBlockPatternChroma & 3)!=0)&&startIdx==0){
					int[] cdc=new int[NumC8x8*4];
					ChromaDCLevelnC();
					p.residual_block_cavlc(cdc,0,4*NumC8x8-1,4*NumC8x8);
					System.out.println("cdc");
					ChromaDCLevel[iCbCr]=cdc;
				}else{
					for(int i=0;i<4*NumC8x8;i++){
						ChromaDCLevel[iCbCr][i]=0;
					}
				}
			}
			for(int iCbCr=0;iCbCr<2;iCbCr++){
				for(int i8x8=0;i8x8<NumC8x8;i8x8++){
					for(int i4x4=0;i4x4<4;i4x4++){
						if((CodedBlockPatternChroma&2)!=0){
							setnC();
							p.residual_block_cavlc(ChromaACLevel[iCbCr][i8x8*4+i4x4],Math.max(0,startIdx-1),endIdx-1,15);
							System.out.println("ac");
						}else{
							for(int i=0;i<15;i++){
								ChromaACLevel[iCbCr][i8x8*4+i4x4][i]=0;
							}
						}
					}	
				}
			}
		}else if(ChromaArrayType==3){
			// System.out.println("chroma component present");

			cb4x4BlkIdx=0;
			// System.out.println("ChromaArrayType==3");
			residual_luma(i16x16DClevel,i16x16AClevel,level4x4,level8x8,startIdx,endIdx);
			CbIntra16x16DCLevel=i16x16DClevel;
			CbIntra16x16ACLevel=i16x16AClevel;
			CbLevel4x4=level4x4;
			CbLevel8x8=level8x8;
			cr4x4BlkIdx=0;
			residual_luma(i16x16DClevel,i16x16AClevel,level4x4,level8x8,startIdx,endIdx);
			CrIntra16x16DCLevel=i16x16DClevel;
			CrIntra16x16ACLevel=i16x16AClevel;
			CrLevel4x4=level4x4;
			CrLevel8x8=level8x8;	
		}
	}

	public void residual_luma(int[] i16x16DClevel,int[][] i16x16AClevel
		,int[][] level4x4,int[][] level8x8,int startIdx,int endIdx){
		if(startIdx==0&&MbPartPredMode(mbRow,0).equals("Intra_16x16")){
			// luma4x4BlkIdx=0;
			setnC();
			System.out.println("i16x16DClevel");
			p.residual_block_cavlc(i16x16DClevel,0,15,16);
		}

		System.out.println("transform_size_8x8_flag "+transform_size_8x8_flag);
		for(int i8x8=0;i8x8<4;i8x8++){
			if(!transform_size_8x8_flag||!pps0.entropy_coding_mode_flag){
				for(int i4x4=0;i4x4<4;i4x4++){
					// System.out.println(CodedBlockPatternLuma+" "+"CodedBlockPatternLuma");
					// if(CodedBlockPatternChroma==0){

					// }
					if((CodedBlockPatternLuma & (1<<i8x8))!=0){
						if(MbPartPredMode(mbRow,0).equals("Intra_16x16")){
							// i16x16AClevel=
							int [] ac=new int[16];
							setnC();
							p.residual_block_cavlc(ac,Math.max(0,startIdx-1),endIdx-1,15);
							i16x16AClevel[i8x8*4+i4x4]=ac;
							// System.out.println("line 577 implemetation required");
						}else{
							int [] l4x4=new int[16];
							setnC();

							p.residual_block_cavlc(l4x4,startIdx,endIdx,16);
							level4x4[i8x8*4+i4x4]=l4x4;

							// residual_block(level4x4[i8x8*4])
							// System.out.println("line 580 implemetation required");
						}

					}else if(MbPartPredMode(mbRow,0).equals("Intra_16x16")){
						for(int i=0;i<15;i++){
							// System.out.print("0");

							i16x16AClevel[i8x8*4+i4x4][i]=0;
							// level8x8[i8x8][4*i+i4x4]=level4x4[i8x8*4+i4x4][i];
						}
					}else{
						for (int i=0;i<16 ;i++ ) {
							// System.out.println("line 600 implemetation required");
							// System.out.println("0");	
							level8x8[i8x8][4*i+i4x4]=level4x4[i8x8*4+i4x4][i];
								
						}
					}
					if(!pps0.entropy_coding_mode_flag&&transform_size_8x8_flag){
						for(int i=0;i<16;i++){
							// System.out.println("line 608 implemetation required");

							level8x8[i8x8][4*i+i4x4]=level4x4[i8x8*4+i4x4][i];
						}
					}
				}
			}
			else if((CodedBlockPatternLuma&(1<<i8x8))!=0){
				int[] l8x8=new int [64];
				setnC();

				p.residual_block_cavlc(l8x8,4*startIdx,4*endIdx+3,64);
				level8x8[i8x8]=l8x8;
					// residual_block( level8x8[ i8x8 ], 4 * startIdx, 4 * endIdx + 3, 64 )
				// System.out.println("line 603 implemetation required");
			}else{
				for(int i=0;i<64;i++){
					// System.out.print("line 580 implemetation required");

					level8x8[i8x8][i]=0;
				}
			}


		}
	}
	public int NumSubMbPart(String type){
		System.out.println("implemetation required line 645 NumSubMbPart");
		return 0;
	}
	public String sub_mb_type(int idx){
		System.out.println("implemetation sub_mb_type");
		return"nill";
	}
	public void mb_pred(int mb_type){
		// ref_idx_l0
		// System.out.println("call to mb_pred");
		if(MbPartPredMode(mb_type,0).equals("Intra_4x4")||
			MbPartPredMode(mb_type,0).equals("Intra_8x8")||
			MbPartPredMode(mb_type,0).equals("Intra_16x16")){
			if(MbPartPredMode(mb_type,0).equals("Intra_4x4")){
				prev_intra4x4_pred_mode_flag=new boolean[16];
				rem_intra4x4_pred_mode=new int [16];
				for (luma4x4BlkIdx=0;luma4x4BlkIdx<16 ;luma4x4BlkIdx++ ) {
					prev_intra4x4_pred_mode_flag[luma4x4BlkIdx]=p.getBit();
					if(!prev_intra4x4_pred_mode_flag[luma4x4BlkIdx]){
						rem_intra4x4_pred_mode[luma4x4BlkIdx]=p.readBits(3);
					}else{
						rem_intra4x4_pred_mode[luma4x4BlkIdx]=0;
					}
				}

			}
			if(MbPartPredMode(mb_type,0).equals("Intra_8x8")){
				// System.out.println("seccond if ");
				prev_intra8x8_pred_mode_flag=new boolean[4];
				rem_intra8x8_pred_mode=new int [4];
				for (int luma8x8BlkIdx=0;luma8x8BlkIdx<4 ;luma8x8BlkIdx++ ) {
					prev_intra8x8_pred_mode_flag[luma8x8BlkIdx]=p.getBit();
					if(!prev_intra8x8_pred_mode_flag[luma8x8BlkIdx]){
						rem_intra8x8_pred_mode[luma8x8BlkIdx]=p.readBits(3);
					}else{
						rem_intra8x8_pred_mode[luma8x8BlkIdx]=0;
					}
				}
			}
			if(ChromaArrayType==1||ChromaArrayType==2){
				// System.out.println("chroma tyoe 1 or 2");
				intra_chroma_pred_mode=p.uev();
			}
		}else if(!MbPartPredMode(mb_type,0).equals("Direct")){
			


			// for(int mbPartIdx=0;
			// 	){

			// }

			// ref_idx_l0
			// System.out.println("akhri wali ");
			System.out.println("end of func");
			// for(int mbPartIdx=0;mbPartIdx<NumMbPart(mb_type)){

			}
		}	
	// not for i slice
	public int NumMbPart(int row){
		String ret= p.Mb_Type("table7.13.txt",row,2);
		System.out.println("implemetation NumMbPart required at line 684");
		// return Integer.parseInt(ret);
		return 0;
	}
	public void sub_mb_pred(int mb_type){}// not for i slice

	public void macroblock_layer() {
		// unimplemented
		// table 7.11
		// mb_type
		// Name of mb_type
		// transform_size_8x8_flag
		// MbPartPredMode ( mb_type, 0 )
		// Intra16x16PredMode
		// CodedBlockPatternChroma
		// CodedBlockPatternLuma

		// macroblock_layer( ) { C Descriptor mb_type 2 ue(v) | ae(v) if( mb_type = = I_PCM ) { while( !byte_aligned( ) ) pcm_alignment_zero_bit 3 f(1) for( i = 0; i < 256; i++ ) pcm_sample_luma[ i ] 3 u(v) for( i = 0; i < 2 * MbWidthC * MbHeightC; i++ ) pcm_sample_chroma[ i ] 3 u(v) } else { noSubMbPartSizeLessThan8x8Flag = 1 if( mb_type != I_NxN && MbPartPredMode( mb_type, 0 ) != Intra_16x16 && NumMbPart( mb_type ) = = 4 ) { sub_mb_pred( mb_type ) 2 for( mbPartIdx = 0; mbPartIdx < 4; mbPartIdx++ ) if( sub_mb_type[ mbPartIdx ] != B_Direct_8x8 ) { if( NumSubMbPart( sub_mb_type[ mbPartIdx ] ) > 1 ) noSubMbPartSizeLessThan8x8Flag = 0 } else if( !direct_8x8_inference_flag ) noSubMbPartSizeLessThan8x8Flag = 0 } else { if( transform_8x8_mode_flag && mb_type = = I_NxN ) transform_size_8x8_flag 2 u(1) | ae(v) mb_pred( mb_type ) 2 } if( MbPartPredMode( mb_type, 0 ) != Intra_16x16 ) { coded_block_pattern 2 me(v) | ae(v) if( CodedBlockPatternLuma > 0 && transform_8x8_mode_flag && mb_type != I_NxN && noSubMbPartSizeLessThan8x8Flag && ( mb_type != B_Direct_16x16 | | direct_8x8_inference_flag ) ) transform_size_8x8_flag 2 u(1) | ae(v) } if( CodedBlockPatternLuma > 0 | | CodedBlockPatternChroma > 0 | | MbPartPredMode( mb_type, 0 ) = = Intra_16x16 ) { mb_qp_delta 2 se(v) | ae(v) residual( 0, 15 ) 3 | 4 } } }
		int MbWidthC;
		// System.out.println("macroblock_layer");
		int MbHeightC;
		if(sps0.chroma_format_idc==0||sps0.separate_colour_plane_flag){
			MbWidthC=0;
			MbHeightC=0;

		}else{
			MbWidthC=16/getSubWidthC();
			MbHeightC=16/getSubHeightC();
		}
		mbRow=p.uev();
		System.out.println("mb row  "+mbRow);
		// System.out.println(" came ");
		// pause(0.1);
		mb_type=p.Mb_Type("table7.11.txt",mbRow,1);
		System.out.println("mb_type "+mb_type);
		String patLuma,patChroma;
		patLuma=p.Mb_Type("table7.11.txt",mbRow,6);
		patChroma=p.Mb_Type("table7.11.txt",mbRow,5);
		CodedBlockPatternChroma=0;
		CodedBlockPatternLuma=0;
		// CodedBlockPatternLuma = coded_block_pattern % 16 
		// CodedBlockPatternChroma = coded_block_pattern / 16
		if(patChroma.equals("Equation7-36")){
			// System.out.println("true eq");
		}else{
			CodedBlockPatternChroma=Integer.parseInt(patChroma);
			CodedBlockPatternLuma=Integer.parseInt(patLuma);

		}
		// System.out.println("");
		// System.out.println(" ********************** slice layer unimplemented "+mb_type);
		if(mb_type.equals("I_PCM")){
			while(!p.byte_aligned()){
				pcm_alignment_zero_bit=p.readBits(1);
			}
			pcm_sample_luma=new int[256];
			BitDepthY = 8 + sps0.bit_depth_luma_minus8;

			for(int i=0;i<256;i++){
				pcm_sample_luma[i]=p.readBits(BitDepthY);
			}

			int pcm_sample_chroma_Size=2*MbWidthC*MbHeightC;
			pcm_sample_chroma=new int[pcm_sample_chroma_Size];
			
			BitDepthC = 8 + sps0.bit_depth_chroma_minus8;
			for(int i=0;i<pcm_sample_chroma_Size;i++){
				pcm_sample_chroma[i]=p.readBits(BitDepthC);
			}
		}else {			// &&NumMbPart(mbRow)==4
			noSubMbPartSizeLessThan8x8Flag=true;
			if(!mb_type.equals("I_NxN")&& !MbPartPredMode(mbRow,0).equals("Intra_16x16")
			&&NumMbPart(mbRow)==4){
				System.out.println("sub_mb_pred unimplemented");
				sub_mb_pred(mbRow);
				for(int mbPartIdx=0;mbPartIdx<4;mbPartIdx++){
					if(!sub_mb_type(mbPartIdx).equals("B_Direct_8x8")){
						if(NumSubMbPart(sub_mb_type(mbPartIdx))>1){
							noSubMbPartSizeLessThan8x8Flag=false;
						}
					}else if(!sps0.direct_8x8_inference_flag){
						noSubMbPartSizeLessThan8x8Flag=false;
					}
				}
			}else{

				if(pps0.transform_8x8_mode_flag&& mb_type.equals("I_NxN")){
					transform_size_8x8_flag=p.getBit();
				}
				mb_pred(mbRow);
			}
			if(!MbPartPredMode(mbRow,0).equals("Intra_16x16")){
				coded_block_pattern=p.mev(1);
			
				if(patChroma.equals("Equation7-36")){
					// System.out.println("CodedBlockPatternLuma");
					CodedBlockPatternChroma=(int)coded_block_pattern/16;
					CodedBlockPatternLuma=(int)coded_block_pattern%16;
				}else{
					CodedBlockPatternChroma=Integer.parseInt(patChroma);
					CodedBlockPatternLuma=Integer.parseInt(patLuma);
					// System.out.println("CodedBlockPatternChroma "+coded_block_pattern);
				}
				if(CodedBlockPatternLuma>0&&pps0.transform_8x8_mode_flag
					&&!mb_type.equals("I_NxN")
					&&noSubMbPartSizeLessThan8x8Flag&&(!mb_type.equals("B_Direct_16x16")
						||sps0.direct_8x8_inference_flag)){
					transform_size_8x8_flag=p.getBit();
					// System.out.println("transform_size_8x8_flag  ");
				}
			}

			if(CodedBlockPatternLuma>0||CodedBlockPatternChroma>0||
				MbPartPredMode(mbRow,0).equals("Intra_16x16")){
				mb_qp_delta=p.sev();
				System.out.println("call to "+mb_qp_delta);
				residual(0,15);
				Transform_coefficient_decoding();
			}
		}
	}
}












