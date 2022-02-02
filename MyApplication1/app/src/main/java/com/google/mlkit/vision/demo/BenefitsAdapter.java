package com.google.mlkit.vision.demo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class BenefitsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    List<String> data_list, step_image_list;
    Context context;

    public BenefitsAdapter(List<String> data_list){
        this.data_list = data_list;
        step_image_list = null;
    }

    public BenefitsAdapter(List<String> data_list, List<String> step_image_list){
        this.data_list = data_list;
        this.step_image_list = step_image_list;
    }

    @Override
    public int getItemViewType(int position) {
        if(step_image_list == null){
            return 0;
        } else {
            return 1;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View view;
        if(viewType == 0) {
            view = inflater.inflate(R.layout.item_benefits, parent, false);
            return new ViewHolderBenefits(view);
        } else {
            view = inflater.inflate(R.layout.item_procedure, parent, false);
            return new ViewHolderSteps(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if(holder.getItemViewType() == 0){
            ViewHolderBenefits viewHolderBenefits = (ViewHolderBenefits) holder;
            viewHolderBenefits.benefits_tv.setText((position+1) + ". " +data_list.get(position));
        } else {
            ViewHolderSteps viewHolderSteps = (ViewHolderSteps) holder;
            viewHolderSteps.step_no_tv.setText("Step " + (position+1) + ": ");
            viewHolderSteps.steps_tv.setText(data_list.get(position));
            Glide.with(context).load(step_image_list.get(position)).into(viewHolderSteps.step_iv);
        }
    }

    @Override
    public int getItemCount() {
        return data_list.size();
    }




    public class ViewHolderBenefits extends RecyclerView.ViewHolder {
        TextView benefits_tv;


               public ViewHolderBenefits(@NonNull View itemView) {
           super(itemView);

            benefits_tv = itemView.findViewById(R.id.benefit_tv);
        }
    }

    public class ViewHolderSteps extends RecyclerView.ViewHolder {
        TextView steps_tv, step_no_tv;
        ImageView step_iv;

        public ViewHolderSteps(@NonNull View itemView) {
            super(itemView);

            step_no_tv = itemView.findViewById(R.id.step_no);
            steps_tv = itemView.findViewById(R.id.step_tv);
            step_iv = itemView.findViewById(R.id.step_iv);
        }
    }
}
