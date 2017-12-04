package com.bluesky.exoplayerexample;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bluesky.exoplayerexample.Model.MusicObject;
import com.bluesky.exoplayerexample.PlayerService.PlayerUtil;

import java.util.List;

/**
 * Created by mac on 11/17/17.
 */

public class ListMusicObjectAdapter extends RecyclerView.Adapter{
    private List<MusicObject> objects ;
    private Context context ;

    public ListMusicObjectAdapter(List<MusicObject> objects , Context context){
        this.objects = objects ;
        this.context = context ;

    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_music , parent , false);
        return new MusicViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if(holder instanceof MusicViewHolder){
            ((MusicViewHolder) holder).textView.setText(objects.get(position).getTitle());
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PlayerUtil.openListSong(objects , position , (Activity)context);
            }
        });
    }

    @Override
    public int getItemCount() {
        return objects.size();
    }
    private class MusicViewHolder extends RecyclerView.ViewHolder{
        private TextView textView ;
        MusicViewHolder(View view){
            super(view);
            textView = (TextView)view.findViewById(R.id.textView);
        }
    }

}
