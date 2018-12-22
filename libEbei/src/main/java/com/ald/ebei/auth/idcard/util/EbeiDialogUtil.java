package com.ald.ebei.auth.idcard.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;

public class EbeiDialogUtil {

	private Activity activity;
	private AlertDialog alertDialog;

	public EbeiDialogUtil(Activity activity) {
		this.activity = activity;
	}

	public void showDialog(String message) {
		alertDialog = new AlertDialog.Builder(activity)
				.setTitle(message)
				.setNegativeButton("确认", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						activity.finish();
					}
				}).setCancelable(false).create();
		alertDialog.show();
	}

	public void onDestory() {
		if(alertDialog!=null) {
		    alertDialog.dismiss();
		}
		activity = null;
	}
}