package com.example.javaapp.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.javaapp.R;
import com.example.javaapp.models.TenderNotice;
import java.util.List;

public class TenderNoticeAdapter extends RecyclerView.Adapter<TenderNoticeAdapter.TenderViewHolder> {
    private final List<TenderNotice> tenderList;

    public TenderNoticeAdapter(List<TenderNotice> tenderList) {
        this.tenderList = tenderList;
    }

    @NonNull
    @Override
    public TenderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_tender_notice, parent, false);
        return new TenderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TenderViewHolder holder, int position) {
        TenderNotice notice = tenderList.get(position);
        holder.title.setText(notice.getTitle());
        holder.description.setText(notice.getDescription());
        holder.date.setText(notice.getDate());
    }

    @Override
    public int getItemCount() {
        return tenderList.size();
    }

    static class TenderViewHolder extends RecyclerView.ViewHolder {
        TextView title, description, date;
        TenderViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.tender_title);
            description = itemView.findViewById(R.id.tender_description);
            date = itemView.findViewById(R.id.tender_date);
        }
    }
}

