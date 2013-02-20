package com.example.checksplit;

import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.ContentUris;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.ContactsContract;

public class SummaryRow {
	public static String TAG = SummaryRow.class.getSimpleName();
	Uri contactUri;
	BigDecimal total;
	List<LineItem> items;
	String name;
	String number;
	BigDecimal tip;

	public SummaryRow(Uri contactUri, BigDecimal tip) {
		super();
		this.contactUri = contactUri;
		items = new ArrayList<LineItem>();
		this.tip = tip;
		name = "some name";

	}
	public void addLineItem(LineItem item) {
		items.add(item);
	}
	public BigDecimal getTotal() {
		total = BigDecimal.ZERO;
		for(LineItem i : items) {
			total = total.add(i.getTotalPrice());
		}
		return total.multiply(tip.add(BigDecimal.ONE)).setScale(2, RoundingMode.HALF_EVEN);
	}
	public String getName(Activity cxt) {
		String contact = "Me";
		//		Uri phonesUri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
		//		String[] projection = new String[] {
		//				Phone._ID, Phone.DISPLAY_NAME,
		//				Phone.TYPE, Phone.NUMBER, Phone.LABEL };
		//		String 		selection 		= ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?";
		//		String[] 	selectionArgs 	= new String[] { Long.toString(contactUri) };
		//
		//		Cursor mCursor = cxt.getContentResolver().query(phonesUri,
		//				projection, selection, selectionArgs, null);
		//		if (mCursor.moveToFirst()){
		//			contact = mCursor.getString(mCursor.getColumnIndex(Phone.DISPLAY_NAME)));
		//		}
		if (contactUri != null) {
			Cursor c =  cxt.managedQuery(contactUri, null, null, null, null);
			if (c.moveToFirst()) {
				contact = c.getString(c.getColumnIndexOrThrow(ContactsContract.Contacts.DISPLAY_NAME));
			}
		}
		return contact;
	}
	public Bitmap loadPhoto(Activity cxt) {
		long id;
		if(contactUri !=null){
			id = Long.parseLong(contactUri.getLastPathSegment());

			Uri photoUri =
					ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, id);
			InputStream photoInput = ContactsContract.Contacts
					.openContactPhotoInputStream(cxt.getContentResolver(), photoUri);
			if (photoInput != null)
			{
				return BitmapFactory.decodeStream(photoInput);
			}
		}
		return null;
	}
}
