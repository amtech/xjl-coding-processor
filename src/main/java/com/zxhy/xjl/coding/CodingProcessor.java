package com.zxhy.xjl.coding;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.FileUtils;
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
	private static TableCodingLoader loader = new TableCodingLoader();
	/**
	 * 生成mapper文件
	 * @param domain
	 */
	public static void generateMapper(Class domain) {
		loader.buildTable(domain);
		Table table = loader.getTable(domain);
		StringBuilder sb = new StringBuilder();
		String mapperPackage = getMapperPackage(domain);
		log.debug("mapperPackage:" + mapperPackage);
		sb.append("package " + mapperPackage + ";\r\n");
		sb.append("import java.util.List;\r\n");
		sb.append("import org.apache.ibatis.annotations.Delete;\r\n");
		sb.append("import org.apache.ibatis.annotations.Insert;\r\n");
		sb.append("import org.apache.ibatis.annotations.Param;\r\n");
		sb.append("import org.springframework.stereotype.Repository;\r\n");
		sb.append("import " + domain.getName() + ";\r\n");
		sb.append("@Repository\r\n");
		String mapperClassName = domain.getSimpleName() + "Mapper";
		log.debug("mapper名称:" + mapperClassName);
		sb.append("public interface "+mapperClassName+" {\r\n");
		sb.append("\t@Insert(\"insert into " + table.name());
		log.debug("循环列");
		Map<String, Column> columns = loader.getColumns(domain);
		String fields = null;
		String fieldValues = null;
		Set<String> keys = columns.keySet();
		for (String columnKey : keys) {
			if (fields == null){
				fields = columns.get(columnKey).name();
				fieldValues = "#{" + columnKey + "}";
			} else {
				fields += "," + columns.get(columnKey).name();
				fieldValues += ",#{" + columnKey + "}";
			}
			
		}
		sb.append("(" + fields + ") values (" + fieldValues + ")\")\r\n");
		sb.append("\tpublic int insert(" + domain.getSimpleName() + " " + StringUtils.uncapitalize(domain.getSimpleName()) + ");\r\n");
		sb.append("}");
		loader.generateFile(sb, mapperPackage, mapperClassName);
	}
	
	/**
	 * 得到mapper包名称
	 * @param domain
	 * @return
	 */
	private static String getMapperPackage(Class domain){
		return loader.getBasePackage(domain) + ".mapper";
	}
}
