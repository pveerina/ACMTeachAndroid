package com.example.checksplit;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import android.net.Uri;

public class SummaryTable {
	List<SummaryRow> rows;

	public SummaryTable() {
		rows = new ArrayList<SummaryRow>();
	}

	public void mergeLineItem(LineItem item, BigDecimal mTip) {
		Uri lookupUri = item.getContactUri();
		SummaryRow row = getRow(lookupUri);
		if (row == null) {
			row = new SummaryRow(lookupUri, mTip);
			rows.add(row);
		}
		row.addLineItem(item);
	}

	public SummaryRow getRow(Uri uri) {
		for (SummaryRow row : rows) {
			if ((row.contactUri == null && uri == null)
					|| (row.contactUri != null && row.contactUri.equals(uri))) {
				return row;
			}
		}
		return null;
	}

}
