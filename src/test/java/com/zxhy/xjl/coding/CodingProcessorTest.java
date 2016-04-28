package com.zxhy.xjl.coding;

import org.junit.Test;

import com.zxhy.xjl.coding.domain.FF;

/**
 * Unit test for simple App.
 */
public class CodingProcessorTest {
	@Test
	public void generateMapper(){
		CodingProcessor.generateMapper(FF.class);
	}
}
