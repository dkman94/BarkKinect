package app.android.barkinector;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by deepakkumar on 1/18/15.
 */
public class CustomListAdapter extends BaseAdapter {

    private Activity activity;
    private static LayoutInflater inflater=null;
    private ArrayList<ListObj> objs;

    public CustomListAdapter(Activity a, ArrayList<ListObj> items){
        activity = a;
        objs = items;
        inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public int getCount(){
        return objs.size();
    }

    public Object getItem(int position) {
        return position;
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent){
        View v = convertView;
        if(convertView==null){
            v = inflater.inflate(R.layout.listview_row, null);
        }

        ListObj curr = objs.get(position);
        String bName = curr.getBarName();
        String bAddr = curr.getBarLoc();

        TextView barName = (TextView)v.findViewById(R.id.bar_name);
        barName.setText(bName);
        TextView barAddr = (TextView)v.findViewById(R.id.bar_addr);
        barAddr.setText(bAddr);

        return v;
    }
}
