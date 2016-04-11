package me.kartikarora.aadharcardreader;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/**
 * Developer: chipset
 * Package : me.kartikarora.aadharcardreader
 * Project : Aadhar Card Reader
 * Date : 9/4/16
 */
public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.AadharCardViewHolder> {

    private Context mContext;
    private List<PrintLetterBarcodeData> mDataList;
    private OnItemClickListener listener;

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public RecyclerViewAdapter(Context context, List<PrintLetterBarcodeData> dataList) {
        this.mContext = context;
        this.mDataList = dataList;
    }

    @Override
    public AadharCardViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.aadhar_card_recycler_view_item, parent, false);
        return new AadharCardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(AadharCardViewHolder holder, int position) {
        holder.setPosition(position);
        holder.nameTextView.setText(mDataList.get(position).getName());
        holder.uidTextView.setText(mDataList.get(position).getUid());

    }

    @Override
    public int getItemCount() {
        return mDataList.size();
    }

    protected class AadharCardViewHolder extends RecyclerView.ViewHolder {

        TextView nameTextView, uidTextView;
        int position;

        public AadharCardViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(mDataList.get(position));
                }
            });
            nameTextView = (TextView) itemView.findViewById(R.id.name_text_view);
            uidTextView = (TextView) itemView.findViewById(R.id.uid_text_view);
        }

        public void setPosition(int position) {
            this.position = position;
        }
    }

    public interface OnItemClickListener {
        void onItemClick(PrintLetterBarcodeData item);
    }
}
