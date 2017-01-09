package com.example.administrator.gamedemo.widget.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;


import com.example.administrator.gamedemo.model.NoteInfo;
import com.example.administrator.gamedemo.model.Students;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Agent Henry on 2015/7/28.
 */
public class BookDatabaseUtil {
    private static final String TAG = "BookDatabaseUtil";
    protected static final String TRIAL_USER = "trial_user";
    public static final String DATABASE_NAME = "readingroutine_book.db";
    public static final int DATABASE_VERSION = 1;

    private static BookDatabaseUtil instance;
    private BookDBHelper bookDBHelper;
    private Context mContext;

    //单例模型
    private BookDatabaseUtil(Context context) {
        bookDBHelper = new BookDBHelper(context, DATABASE_NAME, null, DATABASE_VERSION);
        mContext = context;
    }

/*    public synchronized static BookDatabaseUtil getInstance(Context context) {
        if (instance == null) {
            instance = new BookDatabaseUtil(context);
        }
        return instance;
    }*/

    public static BookDatabaseUtil getInstance(Context context) {
        if (instance == null) {
            synchronized (BookDatabaseUtil.class) {
                if (instance == null) {
                    instance = new BookDatabaseUtil(context);
                }
            }
        }
        return instance;
    }

    public static void destory() {
        if (instance != null) {
            instance.onDestory();
        }
    }

    public void onDestory() {
        instance = null;
        if (bookDBHelper != null) {
            bookDBHelper.close();
            bookDBHelper = null;
        }
    }

    public void deleteBookInfo(NoteInfo bookInfo) {
        Cursor cursor = null;
        String userId = (String) Students.getObjectByKey( "objectId");
        String where = BookDBHelper.BookinfoTable.USER_ID + " = '" + userId
                + "' AND " + BookDBHelper.BookinfoTable.OBJECT_ID + " = '" + bookInfo.getObjectId() + "'";
        cursor = bookDBHelper.query(BookDBHelper.TABLE_NAME, null, where, null, null, null, null);
        if (cursor != null && cursor.getCount() > 0) {
            bookDBHelper.delete(BookDBHelper.TABLE_NAME, where, null);
            //Log.i(TAG, "delete success");
        }
        if (cursor == null) {
            where = BookDBHelper.BookinfoTable.USER_ID + " = '" + userId
                    + "' AND " + BookDBHelper.BookinfoTable.BOOK_NAME + " = '" + bookInfo.getTitle()
                    + "' AND " + BookDBHelper.BookinfoTable.BOOK_CONTENT + " = '" + bookInfo.getContent()
                    + "' AND " + BookDBHelper.BookinfoTable.BOOK_COLOR + " = '" + bookInfo.getNoteColor()
                    + "' AND " + BookDBHelper.BookinfoTable.BOOK_ALARM_TIME + " = '" + bookInfo.getNoteCreateTime() + "'";
            cursor = bookDBHelper.query(BookDBHelper.TABLE_NAME, null, where, null, null, null, null);
            if (cursor != null && cursor.getCount() > 0) {
                bookDBHelper.delete(BookDBHelper.TABLE_NAME, where, null);
                //Log.i(TAG, "delete success");
            }
        }

        if (cursor != null) {
            cursor.close();
            bookDBHelper.close();
        }
    }

    public void deleteBookInfo(NoteInfo bookInfo, boolean isOffline) {
        Cursor cursor = null;
        String userId = (String) Students.getObjectByKey( "objectId");
        if (userId == null) userId = TRIAL_USER;
        String where = BookDBHelper.BookinfoTable.USER_ID + " = '" + userId
                + "' AND " + BookDBHelper.BookinfoTable.BOOK_NAME + " = '" + bookInfo.getTitle()
                + "' AND " + BookDBHelper.BookinfoTable.BOOK_CONTENT + " = '" + bookInfo.getContent()
                + "' AND " + BookDBHelper.BookinfoTable.BOOK_COLOR + " = '" + bookInfo.getNoteColor()
                + "' AND " + BookDBHelper.BookinfoTable.BOOK_ALARM_TIME + " = '" + bookInfo.getNoteCreateTime() + "'";
        cursor = bookDBHelper.query(BookDBHelper.TABLE_NAME, null, where, null, null, null, null);
        if (cursor != null && cursor.getCount() > 0) {
            bookDBHelper.delete(BookDBHelper.TABLE_NAME, where, null);
            //Log.i(TAG, "delete success");
        }

        if (cursor != null) {
            cursor.close();
            bookDBHelper.close();
        }
    }

    public long insertBookInfo(NoteInfo bookInfo) {
        long uri = 0;
        Cursor cursor = null;
        String userId = (String) Students.getObjectByKey( "objectId");
        String where = BookDBHelper.BookinfoTable.USER_ID + " = '" + userId
                + "' AND " + BookDBHelper.BookinfoTable.OBJECT_ID + " = '"
                + bookInfo.getObjectId() + "'";
        cursor = bookDBHelper.query(BookDBHelper.TABLE_NAME, null, where, null, null, null, null);
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            ContentValues contentValues = new ContentValues();
            contentValues.put(BookDBHelper.BookinfoTable.USER_ID, userId);
            contentValues.put(BookDBHelper.BookinfoTable.OBJECT_ID, bookInfo.getObjectId());
            contentValues.put(BookDBHelper.BookinfoTable.BOOK_NAME, bookInfo.getTitle());
            contentValues.put(BookDBHelper.BookinfoTable.BOOK_CONTENT, bookInfo.getContent());
            contentValues.put(BookDBHelper.BookinfoTable.BOOK_COLOR, bookInfo.getNoteColor());
            contentValues.put(BookDBHelper.BookinfoTable.BOOK_ALARM_TIME, bookInfo.getNoteCreateTime());
            bookDBHelper.update(BookDBHelper.TABLE_NAME, contentValues, where, null);
            //Log.i(TAG, "update");
        } else {
            ContentValues contentValues = new ContentValues();
            contentValues.put(BookDBHelper.BookinfoTable.USER_ID, userId);
            contentValues.put(BookDBHelper.BookinfoTable.OBJECT_ID, bookInfo.getObjectId());
            contentValues.put(BookDBHelper.BookinfoTable.BOOK_NAME, bookInfo.getTitle());
            contentValues.put(BookDBHelper.BookinfoTable.BOOK_CONTENT, bookInfo.getContent());
            contentValues.put(BookDBHelper.BookinfoTable.BOOK_COLOR, bookInfo.getNoteColor());
            contentValues.put(BookDBHelper.BookinfoTable.BOOK_ALARM_TIME, bookInfo.getNoteCreateTime());
            uri = bookDBHelper.insert(BookDBHelper.TABLE_NAME, null, contentValues);
            //Log.i(TAG, "insert");
        }
        if (cursor != null) {
            cursor.close();
            bookDBHelper.close();
        }
        return uri;
    }

    public long insertBookInfo(NoteInfo bookInfo, NoteInfo bookInfoOld, boolean isOffline) {
        long uri = 0;
        Cursor cursor = null;
        String userId = (String) Students.getObjectByKey( "objectId");
        if (userId == null) userId = TRIAL_USER;
        String where = BookDBHelper.BookinfoTable.USER_ID + " = '" + userId
                + "' AND " + BookDBHelper.BookinfoTable.BOOK_NAME + " = '" + bookInfoOld.getTitle()
                + "' AND " + BookDBHelper.BookinfoTable.BOOK_CONTENT + " = '" + bookInfoOld.getContent()
                + "' AND " + BookDBHelper.BookinfoTable.BOOK_COLOR + " = '" + bookInfoOld.getNoteColor()
                + "' AND " + BookDBHelper.BookinfoTable.BOOK_ALARM_TIME + " = '" + bookInfoOld.getNoteCreateTime() + "'";
        cursor = bookDBHelper.query(BookDBHelper.TABLE_NAME, null, where, null, null, null, null);
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            ContentValues contentValues = new ContentValues();
            contentValues.put(BookDBHelper.BookinfoTable.USER_ID, userId);
            contentValues.put(BookDBHelper.BookinfoTable.OBJECT_ID, bookInfo.getObjectId());
            contentValues.put(BookDBHelper.BookinfoTable.BOOK_NAME, bookInfo.getTitle());
            contentValues.put(BookDBHelper.BookinfoTable.BOOK_CONTENT, bookInfo.getContent());
            contentValues.put(BookDBHelper.BookinfoTable.BOOK_COLOR, bookInfo.getNoteColor());
            contentValues.put(BookDBHelper.BookinfoTable.BOOK_ALARM_TIME, bookInfo.getNoteCreateTime());
            bookDBHelper.update(BookDBHelper.TABLE_NAME, contentValues, where, null);
            //Log.i(TAG, "update");
        } else {
            ContentValues contentValues = new ContentValues();
            contentValues.put(BookDBHelper.BookinfoTable.USER_ID, userId);
            contentValues.put(BookDBHelper.BookinfoTable.OBJECT_ID, bookInfo.getObjectId());
            contentValues.put(BookDBHelper.BookinfoTable.BOOK_NAME, bookInfo.getTitle());
            contentValues.put(BookDBHelper.BookinfoTable.BOOK_CONTENT, bookInfo.getContent());
            contentValues.put(BookDBHelper.BookinfoTable.BOOK_COLOR, bookInfo.getNoteColor());
            contentValues.put(BookDBHelper.BookinfoTable.BOOK_ALARM_TIME, bookInfo.getNoteCreateTime());
            uri = bookDBHelper.insert(BookDBHelper.TABLE_NAME, null, contentValues);
            //Log.i(TAG, "insert");
        }

/*        //version1 for trial user and sign in user with online
        String userId = (String) Students.getObjectByKey( "objectId");
        if (userId == null) userId = TRIAL_USER;
        String where = BookDBHelper.BookinfoTable.USER_ID + " = '" + userId
                + "' AND " + BookDBHelper.BookinfoTable.BOOK_NAME + " = '" + bookInfoOld.getTitle()
                + "' AND " + BookDBHelper.BookinfoTable.BOOK_COLOR + " = '" + bookInfoOld.getNoteColor()
                + "' AND " + BookDBHelper.BookinfoTable.BOOK_ALARM_TIME + " = '" + bookInfoOld.getNoteCreateTime() + "'";
        cursor = bookDBHelper.query(BookDBHelper.TABLE_NAME, null, where, null, null, null, null);
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            ContentValues contentValues = new ContentValues();
            contentValues.put(BookDBHelper.BookinfoTable.USER_ID, userId);
            contentValues.put(BookDBHelper.BookinfoTable.OBJECT_ID, bookInfo.getObjectId());
            contentValues.put(BookDBHelper.BookinfoTable.BOOK_NAME, bookInfo.getTitle());
            contentValues.put(BookDBHelper.BookinfoTable.BOOK_COLOR, bookInfo.getNoteColor());
            contentValues.put(BookDBHelper.BookinfoTable.BOOK_ALARM_TIME, bookInfo.getNoteCreateTime());
            bookDBHelper.update(BookDBHelper.TABLE_NAME, contentValues, where, null);
            //Log.i(TAG, "update");
        } else {
            ContentValues contentValues = new ContentValues();
            contentValues.put(BookDBHelper.BookinfoTable.USER_ID, userId);
            contentValues.put(BookDBHelper.BookinfoTable.OBJECT_ID, bookInfo.getObjectId());
            contentValues.put(BookDBHelper.BookinfoTable.BOOK_NAME, bookInfo.getTitle());
            contentValues.put(BookDBHelper.BookinfoTable.BOOK_COLOR, bookInfo.getNoteColor());
            contentValues.put(BookDBHelper.BookinfoTable.BOOK_ALARM_TIME, bookInfo.getNoteCreateTime());
            uri = bookDBHelper.insert(BookDBHelper.TABLE_NAME, null, contentValues);
            //Log.i(TAG, "insert");
        }*/

        if (cursor != null) {
            cursor.close();
            bookDBHelper.close();
        }
        return uri;
    }

    public boolean queryHasBookInfo(String bookName) {
        boolean hasTheBook = false;
        Cursor cursor = null;
        String where = BookDBHelper.BookinfoTable.USER_ID + " = '" + Students.getObjectByKey( "objectId")
                + "' AND " + BookDBHelper.BookinfoTable.BOOK_NAME + " = '" + bookName + "'";
        cursor = bookDBHelper.query(BookDBHelper.TABLE_NAME, null, where, null, null, null, null);

        if (cursor != null && cursor.getCount() > 0) {
            hasTheBook = true;
            cursor.close();
        } else {
            hasTheBook = false;
        }
        return hasTheBook;
    }

    public ArrayList<NoteInfo> queryBookInfos() {
        ArrayList<NoteInfo> bookInfos = null;
        Cursor cursor = bookDBHelper.query(BookDBHelper.TABLE_NAME, null, null, null, null, null, null);
        //Log.i(TAG, cursor.getCount() + "");

        if (cursor == null) {
            return null;
        }
        bookInfos = new ArrayList<>();
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            NoteInfo bookInfo = new NoteInfo();
            bookInfo.setObjectId(cursor.getString(cursor.getColumnIndex(BookDBHelper.BookinfoTable.OBJECT_ID)));
            bookInfo.setTitle(cursor.getString(3));
            bookInfo.setContent(cursor.getString(4));
            bookInfo.setNoteColor(cursor.getInt(5));
            bookInfo.setNoteCreateTime(cursor.getString(6));
            bookInfos.add(0, bookInfo);
        }
        if (cursor != null) {
            cursor.close();
        }
        return bookInfos;
    }

    public List<NoteInfo> setBookInfos(List<NoteInfo> lists) {
        Cursor cursor = null;
        if (lists != null && lists.size() > 0) {
            for (Iterator iterator = lists.iterator(); iterator.hasNext(); ) {
                NoteInfo bookInfo = (NoteInfo) iterator.next();
                insertBookInfo(bookInfo);
            }
        }
        if (cursor != null) {
            cursor.close();
            bookDBHelper.close();
        }
        return lists;
    }

    public ArrayList<NoteInfo> queryInsertBatchBookInfos() {
        ArrayList<NoteInfo> bookInfos = null;
        String where = BookDBHelper.BookinfoTable.USER_ID + " = '" + Students.getObjectByKey( "objectId")
                + "' AND " + BookDBHelper.BookinfoTable.OBJECT_ID + " is null";
        Cursor cursor = bookDBHelper.query(BookDBHelper.TABLE_NAME, null, where, null, null, null, null);
        //Log.i(TAG, cursor.getCount() + "");

        if (cursor == null) {
            return null;
        }
        bookInfos = new ArrayList<>();
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            NoteInfo bookInfo = new NoteInfo();
            bookInfo.setTitle(cursor.getString(3));
            bookInfo.setContent(cursor.getString(4));
            bookInfo.setNoteColor(cursor.getInt(5));
            bookInfo.setNoteCreateTime(cursor.getString(6));
            bookInfos.add(0, bookInfo);
        }
        if (cursor != null) {
            cursor.close();
        }
        return bookInfos;
    }
}
