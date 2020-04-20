package com.example.android.musicplayer;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

/**
 * Created by sky on 19/07/2017.
 */

class MyRecyclerViewAdapter extends RecyclerView.Adapter<MyRecyclerViewAdapter.MyViewHolder> {


    private List<Music> mMusicList;
    private AdapterView.OnItemClickListener mListener;
    private int highlighted = 01;

    public MyRecyclerViewAdapter(List<Music> musicList) {
        mMusicList = musicList;
    }

    @Override
    public MyRecyclerViewAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.listitem, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyRecyclerViewAdapter.MyViewHolder holder, final int position) {
        holder.setTitle(mMusicList.get(position).getTitle());
        holder.setArtists(mMusicList.get(position).getArtists());


        long durationInMs = mMusicList.get(position).getDurationInMs();
        long totalSeconds = TimeUnit.MILLISECONDS.toSeconds(durationInMs);
        long mins = totalSeconds / 60;
        long seconds = totalSeconds % 60;
        String duration = String.format(Locale.UK, "%d:%02d", mins, seconds);

        holder.setDuration(duration);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mListener != null) {
                    mListener.onItemClick(null, null, position, 0);
                }
            }
        });
    }

    public void setOnItemClickListener(AdapterView.OnItemClickListener listener) {
        mListener = listener;
    }

    @Override
    public int getItemCount() {
        return mMusicList.size();
    }

    public void highlist(int position) {
        highlighted = position;

    }


    class MyViewHolder extends RecyclerView.ViewHolder {

        public MyViewHolder(View itemView) {
            super(itemView);
        }

        public void setTitle(String text) {
            ((TextView) itemView.findViewById(R.id.title)).setText(text);
        }

        public void setDuration(String text) {
            ((TextView) itemView.findViewById(R.id.duration)).setText(text);

        }

        public void setArtists(String text) {
            ((TextView) itemView.findViewById(R.id.artist)).setText(text);
        }
    }
}
