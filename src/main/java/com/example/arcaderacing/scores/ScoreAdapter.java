package com.example.arcaderacing.scores;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.arcaderacing.R;

import java.util.List;

public class ScoreAdapter extends BaseAdapter {

    private Context context;
    private List<ScoreItem> scores;

    public ScoreAdapter(Context context, List<ScoreItem> scores) {
        this.context = context;
        this.scores = scores;
    }

    @Override
    public int getCount() {
        return scores.size();
    }

    @Override
    public Object getItem(int position) {
        return scores.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = LayoutInflater.from(context)
                    .inflate(R.layout.item_score, parent, false);
        }

        ScoreItem item = scores.get(position);

        TextView txtRank = convertView.findViewById(R.id.txtRank);
        TextView txtUser = convertView.findViewById(R.id.txtUser);
        TextView txtDate = convertView.findViewById(R.id.txtDate);
        TextView txtScore = convertView.findViewById(R.id.txtScore);

        txtRank.setText(String.valueOf(position + 1));
        txtUser.setText(item.username);
        txtDate.setText(item.date);
        txtScore.setText(String.valueOf(item.score));

        return convertView;
    }
}
