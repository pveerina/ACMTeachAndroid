package com.example.checksplit;

import java.math.BigDecimal;
import java.math.RoundingMode;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
/** This object represents a single row on a receipt.
 */
public class LineItem implements Parcelable {
	private Uri contactURI;
	private String mName;
	private BigDecimal mPrice;

	public LineItem(String name, String price) {
		this.mName = name;
		this.mPrice = new BigDecimal(price);
	}

	// Mutator methods
	public void setName(String name) {
		this.mName = name;
	}
	public void setPrice(String price) {
		this.mPrice = new BigDecimal(price);
	}

	// Accessor methods
	public String getName() {
		return mName;
	}
	public BigDecimal getTotalPrice() {
		return mPrice;
	}
	public BigDecimal getTotalDisplayPrice() {
		return mPrice.setScale(2, RoundingMode.HALF_EVEN);
	}

	@Override
	public String toString() {
		return this.getName();
	}

	/* The following methods implement the Parcelable interface
	 * which is used to serialize and deserialize the LineItem object
	 */

	// Required by Parcelable interface but not used here since it more relevant to subclasses
	@Override
	public int describeContents() {
		return 0;
	}
	// Writes our lineitem object into a parcel.
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(mName);
		dest.writeSerializable(mPrice);
		dest.writeParcelable(contactURI, 0);

	}
	// Constructs a lineitem object from a parcel
	public LineItem(Parcel in) {
		mName = in.readString();
		mPrice = (BigDecimal) in.readSerializable();
		contactURI = in.readParcelable(Uri.class.getClassLoader());
	}
	public Uri getContactUri() {
		return contactURI;
	}

	public void setContactUri(Uri contactUri) {
		this.contactURI = contactUri;
	}
	// CREATOR is used by Android to automatically unparcel our object in some instances
	public static final Parcelable.Creator<LineItem> CREATOR = new Parcelable.Creator<LineItem>() {
		@Override
		public LineItem createFromParcel(Parcel in) {
			return new LineItem(in);
		}
		@Override
		public LineItem[] newArray(int size) {
			return new LineItem[size];
		}
	};
}