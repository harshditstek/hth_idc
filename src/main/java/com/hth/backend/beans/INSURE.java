package com.hth.backend.beans;

import com.hth.backend.iSeries;
import com.hth.util.IDCard;

import java.util.List;

public class INSURE {
	public static void updateCardCount(IDCard[] cards) {
		
	}

	public static List<String[]> getInsureList() {
		String alias = "QTEMP.insure";
		String file = "testdata.insure(TRT)";
		String sql = "select IFNAM,ILNAM,ISSN,IGRPNO from qtemp.insure limit 40";
		List<String[]> resultList = iSeries.executeSQLByAlias(sql, alias, file);

		return resultList;
	}

	public static String[] getInsureData(String id) {
		String alias = "QTEMP.insure";
		String file = "testdata.insure(TRT)";
		String sql = "select IFNAM,ILNAM,IGRPNO from qtemp.insure where issn='"+id+"' or iempid='"+id+"' or ipolcy='"+id+"'";
		String[] resultList = iSeries.executeSQLByAliasArray(sql, alias, file);

		return resultList;
	}


}
