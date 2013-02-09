package com.example.checksplit;

import java.text.NumberFormat;
import java.util.Locale;

import android.app.ListActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class ReceiptActivity extends ListActivity  implements OnItemSelectedListener {
	public static final String TAG = ReceiptActivity.class.getSimpleName();

	Receipt mReceipt;

	TextView mTotalText;
	TextView mSubtotalText;
	EditText mItemNameField;
	EditText mItemPriceField;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mReceipt = Receipt.getFakeReceipt();

		setContentView(R.layout.activity_receipt);
		mTotalText = (TextView) findViewById(R.id.total);
		mSubtotalText = (TextView) findViewById(R.id.subtotal);
		mItemNameField = (EditText) findViewById(R.id.item_name_field);
		mItemPriceField = (EditText) findViewById(R.id.item_price_field);
		mTotalText.setText(getString(R.string.total)
				+ mReceipt.getTotalPrice().toString());
		initSpinner();
		ReceiptListAdapter adapter = new ReceiptListAdapter(mReceipt);
		setListAdapter(adapter);
	}

	private void initSpinner() {
		Spinner tipSpinner = (Spinner) findViewById(R.id.tip_spinner);

		ArrayAdapter<CharSequence> adapter =
				ArrayAdapter.createFromResource(this, R.array.tips_array,
						android.R.layout.simple_spinner_dropdown_item);

		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		tipSpinner.setAdapter(adapter);
		tipSpinner.setSelection(1);

		tipSpinner.setOnItemSelectedListener(this);
	}

	@Override
	protected void onResume() {
		super.onResume();
		refreshTotals();
	}

	private void refreshTotals() {
		// Formatter to properly format our dollar amount.
		NumberFormat usdCostFormat = NumberFormat.getCurrencyInstance(Locale.US);
		usdCostFormat.setMinimumFractionDigits( 2 );
		usdCostFormat.setMaximumFractionDigits( 2 );

		mTotalText.setText(getString(R.string.total) +
				usdCostFormat.format(mReceipt.getTotalPrice().doubleValue()));
		mSubtotalText.setText(getString(R.string.subtotal) +
				usdCostFormat.format(mReceipt.getSubtotalPrice().doubleValue()));
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_receipt, menu);
		return true;
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		Log.d(TAG, "ON PAUSE CALLED");
	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
		String tipAmount = (String)parent.getItemAtPosition(pos);
		tipAmount = "0."+tipAmount.substring(0,tipAmount.length()-1);
		mReceipt.setTip(tipAmount);
		refreshTotals();
	}


	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
		// TODO Auto-generated method stub

	}

	public void finishButtonClicked(View view) {
		Toast.makeText(this, "Button pressed", Toast.LENGTH_LONG).show();
	}

	public class ReceiptListAdapter extends BaseAdapter {
		private Receipt mReceipt;

		public ReceiptListAdapter(Receipt receipt) {
			mReceipt = receipt;
		}

		@Override
		public int getCount() {
			return mReceipt.getNumberOfLineItems();
		}

		@Override
		public Object getItem(int arg0) {
			return mReceipt.getLineItemAtIndex(arg0);
		}

		@Override
		public long getItemId(int index) {
			return index;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View row=convertView;

			if (row == null) {
				row= getLayoutInflater().inflate(R.layout.line_item_row, parent, false);
			}

			ReceiptViewHolder holder = (ReceiptViewHolder)row.getTag();

			if (holder == null) {
				holder=new ReceiptViewHolder(row);
				row.setTag(holder);
			}

			LineItem item = (LineItem)getItem(position);
			String itemName = item.getName();

			holder.mItemName.setText(itemName);
			NumberFormat usdCostFormat = NumberFormat.getCurrencyInstance(Locale.US);
			usdCostFormat.setMinimumFractionDigits(2);
			usdCostFormat.setMaximumFractionDigits(2);
			holder.mItemPrice.setText(
					usdCostFormat.format(item.getTotalDisplayPrice().doubleValue()));

			return(row);
		}

	}

}
