package com.zxhy.xjl.coding.domain;

import com.zxhy.xjl.coding.annotation.Column;
import com.zxhy.xjl.coding.annotation.Table;

@Table(name="xjl_ff")
public class FF {
	@Column(name="col_abc", type="varchar2(200)")
	private String abc;
	@Column(name="id_key", type="varchar2(36)",pk=true)
	private String idKey;
	@Column(name="fk_key", type="varchar2(36)", fkDomain=FF.class)
	private String fkey;
	@Column(name="fk2_key",type="varchar2(36)", fk="xjl_ff(id_key)")
	private String f2key;
}
