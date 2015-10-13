/**
 * @author Nasir Hayat
 *
 */
package com.sirtrack.construct;

//import static org.junit.Assert.*;
//
//import org.junit.Rule;
//import org.junit.Test;
//import org.junit.Ignore;
//import org.junit.rules.ExpectedException;

import com.sirtrack.construct.Core.RangeError;
import com.sirtrack.construct.lib.Containers.Container;

import static com.sirtrack.construct.Core.*;
import static com.sirtrack.construct.Macros.*;
import static com.sirtrack.construct.lib.Containers.*;

import java.io.*;
import java.util.*;

import com.sirtrack.construct.*;
import com.sirtrack.construct.Core.Construct;

public class TestLibrary {

	

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Construct struct = BitStruct("foo",
	            BitField("a", 3),
	            Flag("b"),
	            Padding(3),
	            Nibble("c"),
	            Struct("bar",
	              Nibble("d"),
	              Bit("e")));
		struct.parse( ByteArray( 0xe1, 0x1f ));
		 Container c1 = Container( "a", 7, "b", false, "bar", Container( "d", 15 , "e", 1), "c",8);
		 Container c2 = struct.parse( ByteArray( 0xe1, 0x1f ));
//		 assertEquals( c1, c2 );
		System.out.println(struct.name);
		
	}

}
