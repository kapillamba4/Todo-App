package com.example.kapillamba4.todoapp;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import java.util.ArrayList;

/**
 * Created by kapil on 27/9/17.
 */

public class TodoCustomAdapter extends ArrayAdapter<ListItem> {
    private Context mContext;
    private ArrayList<ListItem> mListItems;
    private DeleteButtonClickListener mDeleteButtonClickListener;

    public TodoCustomAdapter(@NonNull Context context, @NonNull ArrayList<ListItem> listItems, DeleteButtonClickListener deleteButtonClickListener) {
        super(context, 0, listItems);
        mContext = context;
        mListItems = listItems;
        mDeleteButtonClickListener = deleteButtonClickListener;
    }

    @Override
    public int getCount() {
        return mListItems.size();
    }

    @NonNull
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.row, null);
            viewHolder = new ViewHolder();
            TextView title = convertView.findViewById(R.id.title);
            TextView content = convertView.findViewById(R.id.content);
            TextView date = convertView.findViewById(R.id.date);
            TextView time = convertView.findViewById(R.id.time);
            Button button = convertView.findViewById(R.id.delete_button);
            viewHolder.button = button;
            viewHolder.content = content;
            viewHolder.date = date;
            viewHolder.time = time;
            viewHolder.title = title;

            convertView.setTag(viewHolder);
        }

        viewHolder = (ViewHolder) convertView.getTag();
        viewHolder.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDeleteButtonClickListener.onDeleteClicked(position, view);
            }
        });

        ListItem item = mListItems.get(position);
        viewHolder.title.setText(item.getTitle());
        viewHolder.content.setText(item.getContent());
        viewHolder.date.setText(item.getDate());
        viewHolder.time.setText(item.getTime());
        return convertView;
    }

    interface DeleteButtonClickListener {
        void onDeleteClicked(int position, View v);
    }

    static class ViewHolder {

        TextView title;
        TextView content;
        TextView date;
        TextView time;
        Button button;
    }
}
