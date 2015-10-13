package com.sirtrack.construct;

//from construct import Struct, MetaField, StaticField, FormatField
//from construct import Container, Byte
//from construct import FieldError, SizeofError

import static com.sirtrack.construct.Core.ByteArray;
import static com.sirtrack.construct.Core.Container;
import static com.sirtrack.construct.Core.Pass;
import static com.sirtrack.construct.Core.Struct;
import static com.sirtrack.construct.Macros.Array;
import static com.sirtrack.construct.Macros.BitStruct;
import static com.sirtrack.construct.Macros.Bits;
import static com.sirtrack.construct.Macros.Bitwise;
import static com.sirtrack.construct.Macros.Enum;
import static com.sirtrack.construct.Macros.Field;
import static com.sirtrack.construct.Macros.IfThenElse;
import static com.sirtrack.construct.Macros.PrefixedArray;
import static com.sirtrack.construct.Macros.UBInt16;
import static com.sirtrack.construct.Macros.UBInt8;
import static com.sirtrack.construct.lib.Containers.ListContainer;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.sirtrack.construct.Adapters.MappingError;
import com.sirtrack.construct.Core.ArrayError;
import com.sirtrack.construct.Core.Construct;
import com.sirtrack.construct.Core.CountFunc;
import com.sirtrack.construct.Core.KeyFunc;
import com.sirtrack.construct.Core.LengthFunc;
import com.sirtrack.construct.Core.Switch;
import com.sirtrack.construct.lib.Containers.Container;

public class MacrosTest  
{
  @Rule
  public ExpectedException exception = ExpectedException.none();

  @Test
  public void BitIntegerAdapterTest() {
  	Construct bw;

    bw = Bitwise( Field("bitwise",8) );
    assertArrayEquals( ByteArray(1,1,1,1,1,1,1,1), (byte[]) bw.parse( ByteArray( 0xFF )));
    assertEquals( (byte)0xFF, bw.build(ByteArray(1,1,1,1,1,1,1,1))[0]);
    
    bw = Bitwise( Field("bitwise", new LengthFunc(){
      public int length(Container context) {
	      return 8;
      }} ));
    assertArrayEquals( ByteArray(1,1,1,1,1,1,1,1), (byte[]) bw.parse( ByteArray( 0xFF )));

    /*  
     * TODO implement BitStream.build()  
    [Bitwise(Field("bitwise", lambda ctx: 8)).build, "\x01" * 8, "\xff", None],
    */
    
    // Test BitStream re-streamer with Arrays
    
    bw = Bitwise( Array( new CountFunc(){
  		public int count(Container ctx) {
      return 8;
  	}}, Bits("bitwise",1)) );
    assertEquals( ListContainer(1,1,1,1,1,1,1,1), bw.parse( ByteArray( 0xFF )));

    bw = Bitwise( Array( new CountFunc(){
  		public int count(Container ctx) {
      return 4;
  	}}, Bits("bitwise",2)) );
    assertEquals( ListContainer(3,3,3,3), bw.parse( ByteArray( 0xFF )));
    
    bw = Bitwise( Array( new CountFunc(){
  		public int count(Container ctx) {
      return 4;
  	}}, Bits("bitwise",2)) );
    assertEquals( ListContainer(0,1,2,3), bw.parse( ByteArray( 0x1B )));

    bw = Struct( "mixed",
  			UBInt8("Length"),
    		Bitwise( Array( new CountFunc(){
      		public int count(Container ctx) {
          return 4;
      	}}, Bits("bitwise",2)) ));
    assertEquals( Container("Length", 2, "bitwise", ListContainer(0,1,2,3)), bw.parse( ByteArray( 2, 0x1B )));
    
    bw = BitStruct( "mixed",
  			Bits("Length", 8),
    		Array( new CountFunc(){
      		public int count(Container ctx) {
          return 4;
      	}}, Bits("bitwise",2)));
     assertEquals( Container("Length", 2, "bitwise", ListContainer(0,1,2,3)), bw.parse( ByteArray( 2, 0x1B )));

     bw = BitStruct( "mixed",
    			Bits("Length", 4),
      		Array( new CountFunc(){
        		public int count(Container ctx) {
            return 1;
        	}}, Bits("bitwise",12)));
     assertEquals( Container("Length", 1, "bitwise", ListContainer(0x1B)), bw.parse( ByteArray( 0x10, 0x1B )));
  }
  
  @Test 
  public void EnumTest(){
  	Adapter a;
  	
  	a = Enum( UBInt8("enum"), 'q',3,'r',4,'t',5);
  	assertEquals( 'r', a.parse(ByteArray(4)));

  	a = Enum( UBInt8("enum"), 'q',3,'r',4,'t',5, "_default_", "spam");
  	assertEquals( "spam", a.parse(ByteArray(7)));

//  	a = Enum( UBInt8("enum"), 'q',3,'r',4,'t',5, "_default_", Pass );
//  	assertEquals( 7, a.parse(ByteArray(7)));

  	a = Enum( UBInt8("enum"), 'q',3,'r',4,'t',5);
  	assertArrayEquals( ByteArray(4), a.build('r'));

//  	a = Enum( UBInt8("enum"), 'q',3,'r',4,'t',5, "_default_", 9);
//  	assertArrayEquals( ByteArray(9), a.build("spam"));

//  	a = Enum( UBInt8("enum"), 'q',3,'r',4,'t',5, "_default_", Pass);
//  	assertArrayEquals( ByteArray(9), a.build(9));
  	
  	a = Enum( UBInt8("enum"), 'q',3,'r',4,'t',5);
    exception.expect( MappingError.class );
  	a.build("spam");

  	a = Enum( UBInt8("enum"), 'q',3,'r',4,'t',5);
    exception.expect( MappingError.class );
    a.parse(ByteArray(7));
  }

  @Test
  public void PrefixedArrayTest(){
  	assertEquals( ListContainer(1,1,1), PrefixedArray(UBInt8("array"), UBInt8("count")).parse(ByteArray(3,1,1,1)));

  	assertArrayEquals( ByteArray(3,1,1,1), PrefixedArray(UBInt8("array"), UBInt8("count")).build(ListContainer(1,1,1)));

    exception.expect( ArrayError.class );
  	assertEquals( ListContainer(1,1,1), PrefixedArray(UBInt8("array"), UBInt8("count")).parse(ByteArray(3,1,1)));
  }
  
  @Test
  public void ifThenElseTest(){
  	Switch ifThenElse;
  	
  	ifThenElse = IfThenElse("ifthenelse", new KeyFunc(){ public Object get(Container context){return true;}}, UBInt8("then"), UBInt16("else") );
  	assertEquals(1, ifThenElse.parse(ByteArray(1)));

  	ifThenElse = IfThenElse("ifthenelse", new KeyFunc(){ public Object get(Container context){return false;}}, UBInt8("then"), UBInt16("else") );
  	assertEquals(1, ifThenElse.parse(ByteArray(0,1)));

  	ifThenElse = IfThenElse("ifthenelse", new KeyFunc(){ public Object get(Container context){return true;}}, UBInt8("then"), UBInt16("else") );
  	assertArrayEquals(ByteArray(1), (byte[])ifThenElse.build(1));

  	ifThenElse = IfThenElse("ifthenelse", new KeyFunc(){ public Object get(Container context){return false;}}, UBInt8("then"), UBInt16("else") );
  	assertArrayEquals(ByteArray(0,1), (byte[])ifThenElse.build(1));
  }
}

