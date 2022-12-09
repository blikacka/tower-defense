package com.project;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.os.Bundle;
import android.util.Log;
import java.util.ArrayList;
import java.util.List;

public class Database extends Activity {

	private Context context;
	private SQLiteDatabase db;

	private SQLiteStatement insertStmt;
	private static final String INSERT = "insert into " + DatabaseHelper.TABLE_NAME + "(score) values (?)";

	public Database(Context context) {
		this.context = context;
		DatabaseHelper openHelper = new DatabaseHelper(this.context);
		this.db = openHelper.getWritableDatabase();
		this.insertStmt = this.db.compileStatement(INSERT);
	}

	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
	}

	public long insert(int score) {
		this.insertStmt.bindDouble(1, score);
		return this.insertStmt.executeInsert();
	}

	public List<Integer> selectAll() {
		List<Integer> list = new ArrayList<Integer>();
		Cursor cursor = this.db.query(DatabaseHelper.TABLE_NAME, new String[]{"score"},
				null, null, null, null, "score desc limit 5");
		if (cursor.moveToFirst()) {
			do {
				list.add(Integer.parseInt(cursor.getString(0)));
			} while (cursor.moveToNext());
		}
		if (cursor != null && !cursor.isClosed()) {
			cursor.close();
		}
		return list;
	}

}
