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

public class FirstYearAdapter extends FirestoreRecyclerAdapter<VoterCandidateItem, FirstYearAdapter.ViewHolder> {
    private OnItemClickListener listener;

    public FirstYearAdapter(@NonNull FirestoreRecyclerOptions<VoterCandidateItem> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull VoterCandidateItem model) {
        holder.candidateListTitle.setText(model.getCandidateFullName());
        holder.candidateListPosition.setText(model.getCandidatePosition());
        Picasso.get().load(model.getImageURL()).into(holder.candidateImageURL);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.voter_candidate_item,
                parent, false);
        return new ViewHolder(v);
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        public TextView candidateListTitle;
        public TextView candidateListPosition;
        public ImageView candidateImageURL;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            candidateListTitle = itemView.findViewById(R.id.candidateListName);
            candidateListPosition = itemView.findViewById(R.id.candidateListPosition);
            candidateImageURL = itemView.findViewById(R.id.candidateListImage);

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
