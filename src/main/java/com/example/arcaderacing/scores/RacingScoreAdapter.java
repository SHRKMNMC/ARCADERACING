package com.example.arcaderacing.scores;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.arcaderacing.R;

import java.util.List;

public class RacingScoreAdapter extends BaseAdapter {

    private Context context;
    private List<RacingScoreItem> scores;

    public RacingScoreAdapter(Context context, List<RacingScoreItem> scores) {
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

    private String formatTime(int totalSeconds) {
        int minutes = totalSeconds / 60;
        int seconds = totalSeconds % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = LayoutInflater.from(context)
                    .inflate(R.layout.item_score, parent, false);
        }

        RacingScoreItem item = scores.get(position);

        TextView txtRank = convertView.findViewById(R.id.txtRank);
        TextView txtUser = convertView.findViewById(R.id.txtUser);
        TextView txtDate = convertView.findViewById(R.id.txtDate);
        TextView txtScore = convertView.findViewById(R.id.txtScore);

        txtRank.setText(String.valueOf(position + 1));
        txtUser.setText(item.username);
        txtDate.setText(item.date);
        txtScore.setText(formatTime(item.timeSeconds)); // 🔥 tiempo formateado

        return convertView;
    }
}
