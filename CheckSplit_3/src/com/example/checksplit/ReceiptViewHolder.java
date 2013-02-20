package com.example.checksplit;
import android.view.View;
import android.widget.TextView;

import com.example.checksplit.R;

public class ReceiptViewHolder {
	TextView mItemName= null;
	TextView mItemPrice=null;
	ReceiptViewHolder(View row) { 
		this.mItemName = (TextView)row.findViewById(R.id.item_name);
		this.mItemPrice=(TextView)row.findViewById(R.id.item_price);
		
	}
}