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
import java.util.List;

public class CustomAdapterHL extends RecyclerView.Adapter<CustomAdapterHL.HLViewHolder> {

    private Context context;
    Activity activity;
    List<highLights_Item> itemList = new ArrayList<>();


    public  CustomAdapterHL(Activity activity, Context context, List<highLights_Item> itemList){
        this.activity = activity;
        this.context = context;
        this.itemList = itemList;
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
        holder.hl_chapter.setText(String.valueOf(itemList.get(position).getHLChapter()));
        holder.hl_content.setText(String.valueOf(itemList.get(position).getHLContent()));
        holder.hl_page.setText(String.valueOf(itemList.get(position).getHLPage()));
        String actualTitle = String.valueOf(itemList.get(position).getHLTitle());

        // check if content is from the same title
        // if it is, don't show the title of the next Holder with the same title as another
        boolean isDuplicate = false;
        if(position >= 1){
            for (int x = 0; x < position; x++){
                if(String.valueOf(itemList.get(x).getHLTitle()).equals(actualTitle)){
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
        return itemList.size();
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
