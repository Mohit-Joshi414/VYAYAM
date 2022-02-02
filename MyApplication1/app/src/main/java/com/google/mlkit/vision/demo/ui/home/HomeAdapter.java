package com.google.mlkit.vision.demo.ui.home;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.mlkit.vision.demo.PoseDetailActivity;
import com.google.mlkit.vision.demo.R;
import com.google.mlkit.vision.demo.java.LivePreviewActivity;

import java.util.List;

public class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.ViewHolder> {

    List<HomeGetterSetter> poseList;
    Context context;

    public HomeAdapter(List<HomeGetterSetter> poseList){
        this.poseList = poseList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View view = inflater.inflate(R.layout.item_pose_list, parent, false);

        // Return a new holder instance
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.poseName.setText(poseList.get(position).getPoseName());
        Glide.with(context).load(poseList.get(position).getPoseImage()).into(holder.poseImage);
    }

    @Override
    public int getItemCount() {
        return poseList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ImageView poseImage;
        private TextView poseName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            poseImage = itemView.findViewById(R.id.poseImage);
            poseName = itemView.findViewById(R.id.poseName);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            Intent intent = new Intent(context.getApplicationContext(), PoseDetailActivity.class);

            intent.putExtra("name", poseList.get(getAdapterPosition()).getPoseName());
            intent.putExtra("rha", poseList.get(getAdapterPosition()).getRha());
            intent.putExtra("lha", poseList.get(getAdapterPosition()).getLha());
            intent.putExtra("rka", poseList.get(getAdapterPosition()).getRka());
            intent.putExtra("lka", poseList.get(getAdapterPosition()).getLka());
            intent.putExtra("rsa", poseList.get(getAdapterPosition()).getRsa());
            intent.putExtra("lsa", poseList.get(getAdapterPosition()).getLsa());
            intent.putExtra("rea", poseList.get(getAdapterPosition()).getRea());
            intent.putExtra("lea", poseList.get(getAdapterPosition()).getLea());

            context.startActivity(intent);
        }
    }
}
