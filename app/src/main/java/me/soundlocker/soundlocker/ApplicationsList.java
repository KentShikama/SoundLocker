package me.soundlocker.soundlocker;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

public class ApplicationsList extends ListActivity {
    // LIST OF ARRAY STRINGS WHICH WILL SERVE AS LIST ITEMS
    static ArrayList<String> listItems=new ArrayList<>();
    // DEFINING A STRING ADAPTER WHICH WILL HANDLE THE DATA OF THE LISTVIEW
    static ArrayAdapter<String> adapter;

    //RECORDING HOW MANY TIMES THE BUTTON HAS BEEN CLICKED
    int clickCounter=0;

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.activity_applications_list);
        adapter=new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, listItems);
        setListAdapter(adapter);
    }

    // METHOD WHICH WILL HANDLE DYNAMIC INSERTION
    public void addItems(View v ) {
        Intent intent = new Intent(this, ApplicationAdder.class);
        startActivity(intent);

    }

    public static void updateList(String str) {

        listItems.add(str);
        adapter.notifyDataSetChanged();
    }

    protected void onListItemClick(ListView list, View view, int position, long id) {
        Log.i("HelloListView", "You clicked Item: " + id + " at position:" + position);
        Intent intent = new Intent(this, PasswordScreen.class);
        String selectedItemName = listItems.get(position);
        intent.putExtra("app_name", selectedItemName);
        startActivity(intent);

    }
}
