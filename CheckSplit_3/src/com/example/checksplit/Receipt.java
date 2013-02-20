package com.example.checksplit;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * This object represents a single receipt.
 */

public class Receipt implements Parcelable {
	public static final String RECEIPT_EXTRA_NAME = "RECEIPT_EXTRA_NAME";


	// A value between 0-1 which represents the % tip applied to the receipt.
	private BigDecimal mTip;

	// A list of items on the receipt
	private List<LineItem> mLineItems;


	public Receipt() {
		mLineItems = new ArrayList<LineItem>();
		mTip = new BigDecimal(".10");
	}

	// Mutator methods
	public void addLineItem(LineItem item) {
		mLineItems.add(item);
	}

	public void removeLineItem(LineItem item) {
		mLineItems.remove(item);
	}
	public void setTip(String tip) {
		mTip = new BigDecimal(tip);
	}


	// Accessor methods

	public BigDecimal getSubtotalPrice() {
		BigDecimal sum = new BigDecimal(0);
		for (LineItem item : mLineItems) {
			sum = sum.add(item.getTotalPrice());
		}
		return sum.setScale(2, RoundingMode.HALF_EVEN);
	}

	public BigDecimal getTotalPrice() {
		BigDecimal sum = new BigDecimal(0);
		for (LineItem item : mLineItems) {
			sum = sum.add(item.getTotalPrice());
		}
		return sum.multiply(mTip.add(BigDecimal.ONE)).setScale(2, RoundingMode.HALF_EVEN);
	}

	public BigDecimal getTipPrice() {
		BigDecimal sum = new BigDecimal(0);
		for (LineItem item : mLineItems) {
			sum = sum.add(item.getTotalPrice());
		}
		return sum.multiply(mTip).setScale(2, RoundingMode.HALF_EVEN);
	}

	public BigDecimal getTip() {
		return mTip;
	}
	public int getNumberOfLineItems() {
		return mLineItems.size();
	}
	public LineItem getLineItemAtIndex(int index) {
		return mLineItems.get(index);
	}

	// Generates a receipt with some canned data
	public static Receipt getFakeReceipt() {
		Receipt r = new Receipt();
		r.addLineItem(new LineItem("Bagel", "2.99"));
		r.addLineItem(new LineItem("Coffee", "1.99"));
		r.addLineItem(new LineItem("Water", "1.99"));
		return r;
	}


	/* The following methods implement the Parcelable interface
	 * which is used to serialize and deserialize the Receipt object
	 */

	// Required by Parcelable interface, not used here since it is relevant
	// when dealing with subclasses
	@Override
	public int describeContents() {
		return 0;
	}

	// Write out our member variables to the parcel.
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeSerializable(mTip);
		LineItem[] array = new LineItem[mLineItems.size()];
		mLineItems.toArray(array);
		dest.writeParcelableArray(array, 0);
	}

	// Construct a receipt based on a parcel
	public Receipt(Parcel in) {
		mTip = (BigDecimal)in.readSerializable();
		Parcelable []lineItems = in.readParcelableArray(LineItem.class.getClassLoader());
		mLineItems = new ArrayList<LineItem>();
		if (lineItems != null) {
			for (int i = 0; i < lineItems.length; ++i) {
				mLineItems.add((LineItem) lineItems[i]);
			}
		}
	}

	// CREATOR is used by Android to automatically unparcel our object in some instances.
	public static final Parcelable.Creator<Receipt> CREATOR = new Parcelable.Creator<Receipt>() {
		@Override
		public Receipt createFromParcel(Parcel in) {
			return new Receipt(in);
		}
		@Override
		public Receipt[] newArray(int size) {
			return new Receipt[size];
		}
	};


	public SummaryTable summarize() {
		SummaryTable table = new SummaryTable();
		for(LineItem i : mLineItems) {
			table.mergeLineItem(i, mTip);
		}
		return table;
	}
}