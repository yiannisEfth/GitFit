package com2027.killaz.kalorie.gitfit;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class RVA_Tab2 extends RecyclerView.Adapter<RVA_Tab2.MyViewHolder> {

    Context mContext;
    List<Tab2> mData;

    public RVA_Tab2(Context mContext, List<Tab2> mData) {
        this.mContext = mContext;
        this.mData = mData;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v;
        v = LayoutInflater.from(mContext).inflate(R.layout.tab2_list_content,viewGroup,false);
        MyViewHolder vHolder = new MyViewHolder(v);

        return vHolder;

    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int position) {

        myViewHolder.tv_name.setText(mData.get(position).getName());
        myViewHolder.tv_desc.setText(mData.get(position).getDesc());


    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        private TextView tv_name;
        private TextView tv_desc;


    public MyViewHolder (View itemView){
        super(itemView);

        tv_name = (TextView) itemView.findViewById(R.id.text_name);
        tv_desc = (TextView) itemView.findViewById(R.id.text_desc);

    }
}
}
