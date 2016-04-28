package com.zxhy.xjl.coding;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zxhy.xjl.coding.annotation.Column;
import com.zxhy.xjl.coding.annotation.Table;

import freemarker.template.utility.StringUtil;

/**
 * 根据domain对象生成
 * 1、sql脚本
 * 2、mapper
 * 3、service
 * 4、controller
 * @author leasonlive
 *
 */
public class CodingProcessor {
	private static Logger log = LoggerFactory.getLogger(CodingProcessor.class);
	public CodingProcessor(Class domain) {
		
	}
	
}
