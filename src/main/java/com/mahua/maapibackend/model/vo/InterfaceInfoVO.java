package com.mahua.maapibackend.model.vo;

import com.mahua.maapicommon.model.entity.InterfaceInfo;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class InterfaceInfoVO extends InterfaceInfo {
	/**
	 * 总调用次数
	 */
	private Integer totalNum;

	private static final long serialVersionUID = 1L;

}
