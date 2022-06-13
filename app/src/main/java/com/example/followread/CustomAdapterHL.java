package com.example.followread;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class CustomAdapterHL extends RecyclerView.Adapter<CustomAdapterHL.HLViewHolder> {

    private Context context;
    Activity activity;
    private ArrayList HL_id, HL_content, Hl_page, HL_chapter, HL_title;


    public CustomAdapterHL(Activity activity, Context context,
                  ArrayList HL_id,
                  ArrayList HL_content,
                  ArrayList HL_page,
                  ArrayList HL_chapter,
                  ArrayList HL_title){
        this.activity = activity;
        this.context = context;
        this.HL_id = HL_id;
        this.HL_content = HL_content;
        this.Hl_page = HL_page;
        this.HL_chapter = HL_chapter;
        this.HL_title = HL_title;
    }

    @NonNull
    @Override
    public HLViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.highlight_row, parent, false);

        return new HLViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HLViewHolder holder, int position) {
        holder.hl_chapter.setText(String.valueOf(HL_chapter.get(position)));
        holder.hl_content.setText(String.valueOf(HL_content.get(position)));
        holder.hl_page.setText(String.valueOf(Hl_page.get(position)));
        String actualTitle = String.valueOf(HL_title.get(position));

        // check if content is from the same title
        // if it is, don't show the title of the next Holder with the same title as another
        boolean isDuplicate = false;
        if(position >= 1){
            for (int x = 0; x < position; x++){
                if(String.valueOf(HL_title.get(x)).equals(actualTitle)){
                    holder.hl_title.setText("");
                    isDuplicate = true;
                }
            }
        }
        if (!isDuplicate) {
            holder.hl_title.setText(actualTitle);
        }
    }

    @Override
    public int getItemCount() {
        return HL_id.size();
    }

    public class HLViewHolder extends RecyclerView.ViewHolder {
        TextView hl_chapter, hl_page, hl_content, hl_title;
        public HLViewHolder(View itemView){
            super(itemView);
            hl_chapter = itemView.findViewById(R.id.highlight_chapter);
            hl_page = itemView.findViewById(R.id.highlight_page);
            hl_content = itemView.findViewById(R.id.highlight_content);
            hl_title = itemView.findViewById(R.id.highlight_title);
        }
    }
}
