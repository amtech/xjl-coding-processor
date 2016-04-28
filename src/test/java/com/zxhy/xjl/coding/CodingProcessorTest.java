package com.zxhy.xjl.coding;

import org.junit.Test;

import com.zxhy.xjl.coding.domain.FF;

/**
 * Unit test for simple App.
 */
public class CodingProcessorTest {
	@Test
	public void generateMapper(){
		TableCodingLoader loader = new TableCodingLoader();
		loader.buildTable(FF.class);
		loader.generateMapper(FF.class);
	}
}
