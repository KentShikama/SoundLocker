package me.soundlocker.soundlocker;

import android.app.ListActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;

import java.util.ArrayList;

public class ListView extends ListActivity {
    //LIST OF ARRAY STRINGS WHICH WILL SERVE AS LIST ITEMS
    ArrayList<String> listItems=new ArrayList<String>();

    //DEFINING A STRING ADAPTER WHICH WILL HANDLE THE DATA OF THE LISTVIEW
    ArrayAdapter<String> adapter;

    //RECORDING HOW MANY TIMES THE BUTTON HAS BEEN CLICKED
    int clickCounter=0;

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        listItems.add(0, "first");
        setContentView(R.layout.activity_list_view);
        adapter=new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1,
                listItems);
        setListAdapter(adapter);
    }

    //METHOD WHICH WILL HANDLE DYNAMIC INSERTION
    public void addItems(View v) {
        listItems.add("Clicked : "+clickCounter++);
        adapter.notifyDataSetChanged();
    }

//    public void onItemClick(AdapterView<?> l, View v, int position, long id) {
//        Log.i("HelloListView", "You clicked Item: " + id + " at position:" + position);
//        // Then you start a new Activity via Intent
//        Intent intent = new Intent();
//        intent.setClass(this, PasswordScreen.class);
//        intent.putExtra("position", position);
//        // Or / And
//        intent.putExtra("id", id);
//        startActivity(intent);
//    }
}
