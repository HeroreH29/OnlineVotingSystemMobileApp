package com.example.onlinevotingapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

public class HasNotVotedAdapter extends FirestoreRecyclerAdapter<AdminVoterItem, HasNotVotedAdapter.ViewHolder> {
    private OnItemClickListener listener;

    public HasNotVotedAdapter(@NonNull FirestoreRecyclerOptions<AdminVoterItem> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull AdminVoterItem model) {
        holder.voterFullName.setText(model.getVoterFirstName() + " " + model.getVoterLastName());
        holder.voterUID.setText(model.getVoterUID());
        holder.voterImage.setImageResource(R.drawable.ic_baseline_file_copy_24);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.admin_voter_item,
                parent, false);
        return new ViewHolder(v);
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        public TextView voterFullName;
        public TextView voterUID;
        public ImageView voterImage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            voterFullName = itemView.findViewById(R.id.voterListName);
            voterUID = itemView.findViewById(R.id.voterListEmail);
            voterImage = itemView.findViewById(R.id.voterListImage);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getBindingAdapterPosition();

                    if (position != RecyclerView.NO_POSITION && listener != null) {
                        listener.onItemClick(getSnapshots().getSnapshot(position), position);
                    }
                }
            });
        }
    }

    public interface OnItemClickListener {
        void onItemClick(DocumentSnapshot documentSnapshot, int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }
}
