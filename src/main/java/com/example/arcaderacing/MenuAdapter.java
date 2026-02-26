package com.example.arcaderacing;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class MenuAdapter extends ArrayAdapter<String> {

    private final Context context;
    private final String[] titles;
    private final int[] icons;

    public MenuAdapter(Context context, String[] titles, int[] icons) {
        super(context, R.layout.menu_item, titles);
        this.context = context;
        this.titles = titles;
        this.icons = icons;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        LayoutInflater inflater = LayoutInflater.from(context);
        View row = inflater.inflate(R.layout.menu_item, parent, false);

        ImageView icon = row.findViewById(R.id.icon);
        TextView title = row.findViewById(R.id.title);

        icon.setImageResource(icons[position]);
        title.setText(titles[position]);

        return row;
    }
}
