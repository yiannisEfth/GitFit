package com2027.killaz.kalorie.gitfit;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

public class RVA_Tab3 extends RecyclerView.Adapter<RVA_Tab3.MyViewHolder> {

    Context mContext;
    List<Tab3> mData;
    Dialog myDialog;

    public RVA_Tab3(Context mContext, List<Tab3> mData) {
        this.mContext = mContext;
        this.mData = mData;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v;
        v = LayoutInflater.from(mContext).inflate(R.layout.tab3_list_content,viewGroup,false);
        final MyViewHolder vHolder = new MyViewHolder(v);

        myDialog = new Dialog(mContext);
        myDialog.setContentView(R.layout.tab3_popup);


        vHolder.item_popout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                TextView dialog_name_tv = (TextView) myDialog.findViewById(R.id.dialog_Name);
                dialog_name_tv.setText(mData.get(vHolder.getAdapterPosition()).getName());


                //Toast.makeText(mContext, "Test Click" + String.valueOf(vHolder.getAdapterPosition()),Toast.LENGTH_SHORT).show();
                myDialog.show();

            }
        });

        return vHolder;

    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int position) {

        myViewHolder.tv_name.setText(mData.get(position).getName());



    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        private LinearLayout item_popout;
        private TextView tv_name;



        public MyViewHolder (View itemView){
            super(itemView);
            item_popout = (LinearLayout) itemView.findViewById(R.id.tab3_layout);

            tv_name = (TextView) itemView.findViewById(R.id.text_name);


        }
    }
}
