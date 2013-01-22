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
/**
 * This activity drives the main receipt screen of the CheckSplit app.
 * @author Prasanth Veerina (pveerina@gmail.com)
 */

public class ReceiptActivity extends ListActivity implements  OnItemSelectedListener {
	private static final String TAG = ReceiptActivity.class.getSimpleName();

	// Data objects
	Receipt mReceipt;
	ReceiptListAdapter mReceiptListAdapter;

	// References to widgets
	TextView mTotalText;
	TextView mSubtotalText;
	EditText mItemNameField;
	EditText mItemPriceField;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// Set up our data objects
		mReceipt = Receipt.getFakeReceipt();
		mReceiptListAdapter = new ReceiptListAdapter(mReceipt);


		// Set up our UI elements
		setContentView(R.layout.activity_receipt);
		mTotalText = (TextView)findViewById(R.id.total);
		mSubtotalText = (TextView)findViewById(R.id.subtotal);
		mItemNameField = (EditText)findViewById(R.id.item_name_field);
		mItemPriceField = (EditText)findViewById(R.id.item_price_field);
		initialzeTipSpinner();
		setListAdapter(mReceiptListAdapter);
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

	// On click event for the add line item button
	public void addItemClicked(View view) {
		// Grab the text out of our fields
		String itemName = mItemNameField.getText().toString();
		String itemPrice = mItemPriceField.getText().toString();

		if(!itemName.isEmpty() && !itemPrice.isEmpty()) {
			Log.v(TAG, "Item added to receipt: (" + itemName + ", " +itemPrice + ")" );
			// Clear text fields
			mItemNameField.setText("");
			mItemPriceField.setText("");
			// Add new line item to our receipt object
			mReceipt.addLineItem(new LineItem(itemName, itemPrice));
			// Refresh list view and totals
			mReceiptListAdapter.notifyDataSetChanged();
			getListView().smoothScrollToPosition(mReceiptListAdapter.getCount());
			refreshTotals();
		}
	}

	private void initialzeTipSpinner() {
		// We get a reference to the tips spinner
		Spinner tipSpinner = (Spinner) findViewById(R.id.tip_spinner);
		// Create an ArrayAdapter using the string array and a default spinner layout
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
				this,R.array.tips_array, android.R.layout.simple_spinner_item);
		// Specify the layout to use when the list of choices appears
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		// Apply the adapter to the spinner
		tipSpinner.setAdapter(adapter);
		tipSpinner.setSelection(1);
		// Set the class which handles spinner selection events which in this case is "this"
		tipSpinner.setOnItemSelectedListener(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_receipt, menu);
		return true;
	}

	// Implements an event handler for the tip spinner
	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
		// An item was selected, Grab the text value, transform the string into a format
		// we can use and set it as the tip amount
		String tipAmount = (String) parent.getItemAtPosition(pos);
		tipAmount = "0."+ tipAmount.substring(0, tipAmount.length()-1);
		mReceipt.setTip(tipAmount);
		refreshTotals();
	}

	// Implements an event handler for the tip spinner
	@Override
	public void onNothingSelected(AdapterView<?> parent) {
		// Reset the tip amount to 0
		mReceipt.setTip("0");
		refreshTotals();
	}


	/* This Adapter is used to render a receipt as a list view. It takes
	 * each line item out of the receipt object and puts the relevant info into a line_item_row
	 */
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
		public Object getItem(int index) {
			return mReceipt.getLineItemAtIndex(index);
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

			ReceiptViewHolder holder=(ReceiptViewHolder)row.getTag();
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
