package com.firsttry.mumbaiparking.helpers;

import com.makeshift.wheretopark.R;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;

public class EasterEgg extends Dialog {

	public EasterEgg(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.easter_egg);
		
	}
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		dismiss();
	}
}
