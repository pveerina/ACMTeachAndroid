package com.example.checksplit;

import java.text.NumberFormat;
import java.util.Locale;

import android.app.Activity;
import android.app.ListActivity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.QuickContactBadge;

public class SplitSummaryActivity extends ListActivity {
	Receipt mReceipt;
	SummaryTable summaryTable;
	SummaryTableAdapter adapter;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_split_summary);
		mReceipt = getIntent().getExtras().getParcelable(Receipt.RECEIPT_EXTRA_NAME);
		summaryTable = mReceipt.summarize();
		adapter = new SummaryTableAdapter(summaryTable,this);
		setListAdapter(adapter);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_split_summary, menu);
		return true;
	}


	public class SummaryTableAdapter extends BaseAdapter {

		private SummaryTable summaryTable;
		Activity cxt;

		public SummaryTableAdapter(SummaryTable table, Activity cxt) {
			summaryTable = table;
			this.cxt = cxt;
		}

		@Override
		public int getCount() {
			return summaryTable.rows.size();
		}

		@Override
		public Object getItem(int index) {
			return summaryTable.rows.get(index);
		}

		@Override
		public long getItemId(int index) {
			return index;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View row = convertView;
			if (row == null) {
				row = getLayoutInflater().inflate(R.layout.line_item_row,
						parent, false);
			}

			ReceiptViewHolder holder = (ReceiptViewHolder) row.getTag();
			if (holder == null) {
				holder = new ReceiptViewHolder(row);
				row.setTag(holder);
			}

			final SummaryRow srow = (SummaryRow) getItem(position);
			String itemName = srow.getName(cxt);

			holder.mItemName.setText(itemName);
			NumberFormat usdCostFormat = NumberFormat
					.getCurrencyInstance(Locale.US);
			usdCostFormat.setMinimumFractionDigits(2);
			usdCostFormat.setMaximumFractionDigits(2);
			holder.mItemPrice.setText(usdCostFormat.format(srow
					.getTotal().doubleValue()));

			Bitmap image=srow.loadPhoto(cxt);

			FrameLayout frame=(FrameLayout) row.findViewById(R.id.frameLayout1);
			QuickContactBadge myBadge=new QuickContactBadge(cxt);
			myBadge.assignContactUri(srow.contactUri);
			myBadge.setMode(ContactsContract.QuickContact.MODE_SMALL);
			frame.removeAllViews();
			frame.addView(myBadge);

			if(image!=null)
			{
				myBadge.setImageBitmap(image);
			}
			else
			{
				myBadge.setImageResource(R.drawable.social_add_person);
			}

			return (row);
		}
	}

}
