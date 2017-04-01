package com.dwyanecf.maxcontactor;

import android.app.ListActivity;
import android.content.ComponentName;
import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

public class Contacts extends ListActivity {
    private static final String TAG = "Contacts";

    private static final int AddContact_ID = Menu.FIRST;
    private static final int EditContact_ID = Menu.FIRST+1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setDefaultKeyMode(DEFAULT_KEYS_SHORTCUT);

        Intent intent = getIntent();
        if (intent.getData() == null) {
            intent.setData(ContactsProvider.CONTENT_URI);
        }

        getListView().setOnCreateContextMenuListener(this);
        Cursor cursor = managedQuery(getIntent().getData(), ContactColumn.PROJECTION, null, null,null);
        //注册每个列表表示形式 ：姓名 + 手机号码
        SimpleCursorAdapter adapter = new SimpleCursorAdapter(this, R.layout.contact_list_item, cursor,
                new String[] { ContactColumn.NAME,ContactColumn.MOBILE }, new int[] { android.R.id.text1,android.R.id.text2 });
        setListAdapter(adapter);
        Log.e(TAG+"onCreate"," is ok");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        menu.add(0, AddContact_ID, 0, R.string.menu_add)
                .setShortcut('3', 'a')
                .setIcon(android.R.drawable.ic_menu_add);

        Intent intent = new Intent(null, getIntent().getData());
        intent.addCategory(Intent.CATEGORY_ALTERNATIVE);
        menu.addIntentOptions(Menu.CATEGORY_ALTERNATIVE, 0, 0,
                new ComponentName(this, Contacts.class), null, intent, 0, null);

        return true;

    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        final boolean haveItems = getListAdapter().getCount() > 0;

        if (haveItems) {
            Uri uri = ContentUris.withAppendedId(getIntent().getData(), getSelectedItemId());

            Intent[] specifics = new Intent[1];
            specifics[0] = new Intent(Intent.ACTION_EDIT, uri);
            MenuItem[] items = new MenuItem[1];

            Intent intent = new Intent(null, uri);
            intent.addCategory(Intent.CATEGORY_ALTERNATIVE);
            menu.addIntentOptions(Menu.CATEGORY_ALTERNATIVE, 0, 0, null, specifics, intent, 0,items);

            if (items[0] != null) {
                items[0].setShortcut('1', 'e');
            }
        } else {
            menu.removeGroup(Menu.CATEGORY_ALTERNATIVE);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case AddContact_ID:
                //添加 联系人
                startActivity(new Intent(Intent.ACTION_INSERT, getIntent().getData()));
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View view, ContextMenuInfo menuInfo) {
        AdapterView.AdapterContextMenuInfo info;
        try {
            info = (AdapterView.AdapterContextMenuInfo) menuInfo;
        } catch (ClassCastException e) {
            return;
        }

        Cursor cursor = (Cursor) getListAdapter().getItem(info.position);
        if (cursor == null) {
            return;
        }

        menu.setHeaderTitle(cursor.getString(1));

        menu.add(0, EditContact_ID, 0, R.string.menu_delete);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info;
        try {
            info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        } catch (ClassCastException e) {
            return false;
        }

        switch (item.getItemId()) {
            case EditContact_ID: {
                //
                Uri noteUri = ContentUris.withAppendedId(getIntent().getData(), info.id);
                getContentResolver().delete(noteUri, null, null);
                return true;
            }
        }
        return false;
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        Uri uri = ContentUris.withAppendedId(getIntent().getData(), id);

        String action = getIntent().getAction();
        if (Intent.ACTION_PICK.equals(action) || Intent.ACTION_GET_CONTENT.equals(action)) {

            setResult(RESULT_OK, new Intent().setData(uri));
        } else {
            //编辑 联系人
            startActivity(new Intent(Intent.ACTION_EDIT, uri));
        }
    }


}
