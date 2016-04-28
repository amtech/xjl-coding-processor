package com.zxhy.xjl.coding;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.security.PKCS12Attribute;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zxhy.xjl.coding.annotation.Column;
import com.zxhy.xjl.coding.annotation.Table;

/**
 * 加载器
 * @author leasonlive
 *
 */
public class TableCodingLoader {
	private static Logger log = LoggerFactory.getLogger(TableCodingLoader.class);
	private Map<Class, Table> domainTables = new HashMap<Class, Table>();
	private Map<Class, Column> domainPKS = new HashMap<Class, Column>();
	private Map<Class, Map<String, Column>> domainColumns = new HashMap<Class, Map<String, Column>>();
	
	public void buildTable(Class domain){
		log.debug("开始构建表：" + domain.getName());
		if (!domain.isAnnotationPresent(Table.class)){
			throw new RuntimeException(domain.getName() + " 不是一个domain对象，请使用@Table进行注解以声明为domian对象");
		}
		log.debug(domain.getName() + "是一个Table注解对象");
		Table table = (Table)domain.getAnnotation(Table.class);
		log.debug("表名称：" + table.name());
		if (StringUtils.isEmpty(table.name())){
			throw new RuntimeException("表名称不能为空，请使用注解配置表名称");
		}
		log.debug("添加表:domain:" + domain + " table:" + table);
		this.domainTables.put(domain, table);
		log.debug("开始处理属性");
		Field[] fields = domain.getDeclaredFields();
		log.debug("该class的属性数量:" + fields.length);
		Map<String, Column> columns = new HashMap<String, Column>();
		Column pkColumn = null;
		for (Field field : fields) {
			log.debug("fieldName:" + field.getName());
			if (!field.isAnnotationPresent(Column.class)){
				log.debug("该field不是一个Column的注解，忽略它");
				continue;
			}
			Column column = field.getAnnotation(Column.class);
			columns.put(field.getName(), column);
			log.debug("字段名称:" + column.name() + " 字段类型:" + column.type());
			if (column.pk()){
				log.debug("这是一个主键，放入主键序列中");
				pkColumn = column;
				this.domainPKS.put(domain, pkColumn);
			}
		}
		this.domainColumns.put(domain, columns);
	}
	public void generateMapper(Class domain){
		Table table = this.domainTables.get(domain);
		StringBuilder sb = new StringBuilder();
		String mapperPackage = this.getMapperPackage(domain);
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
		Map<String, Column> columns = this.domainColumns.get(domain);
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
		File path = new File("src/main/java");
		String[] packagePath = StringUtils.split(mapperPackage, ".");
		
		for (String pkg : packagePath) {
			path = new File(path, pkg);
		}
		File mapperFile = new File(path, mapperClassName + ".java");
		log.debug("写入文件:" + mapperFile.getName());
		try {
			FileUtils.write(mapperFile, sb.toString());
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		
	}
	/**
	 * 得到基本包名称
	 * @param domain
	 * @return
	 */
	private String getBasePackage(Class domain){
		String domainPackage = domain.getPackage().getName();
		String basePackage;
		if (domainPackage.endsWith(".domain")){
			basePackage = domainPackage.substring(0, domainPackage.indexOf(".domain"));
		} else {
			basePackage = domainPackage;
		}
		return basePackage;
	}
	/**
	 * 得到mapper包名称
	 * @param domain
	 * @return
	 */
	private String getMapperPackage(Class domain){
		return this.getBasePackage(domain) + ".mapper";
	}
}
