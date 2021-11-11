package com.quar.turnoffsilentmode.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.paging.PagedListAdapter;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.quar.turnoffsilentmode.R;
import com.quar.turnoffsilentmode.room_database.NumbersTable;

public class NumbersAdapter extends PagedListAdapter<NumbersTable, NumbersAdapter.viewHolder> {

    private final Context context;
    private ItemClickListener mClickListener;


    public NumbersAdapter(Context context) {
        super(DIFF_CALLBACK);
        this.context = context;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.number_rv_item, parent, false);
        return new viewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, final int position) {
        final NumbersTable numbersTable = getItem(position);

        if (numbersTable != null) {
            holder.bindTo(numbersTable);
            holder.id.setText(String.valueOf(position + 1));
        } else {
            holder.clear();
        }

    }

    public class viewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        NumbersTable numbersTable;
        TextView id, name, number;
        Switch voice_mode;


        viewHolder(@NonNull View itemView) {
            super(itemView);
            id = itemView.findViewById(R.id.id);
            name = itemView.findViewById(R.id.name);
            number = itemView.findViewById(R.id.number);

            voice_mode = itemView.findViewById(R.id.switch1);


        }

        void bindTo(NumbersTable numbersTable) {
            this.numbersTable = numbersTable;

            name.setText(numbersTable.getName());
            number.setText(numbersTable.getPhone_number());
            voice_mode.setChecked(numbersTable.getVoice_mode());
            voice_mode.setOnClickListener(this);
        }

        void clear() {
            itemView.invalidate();
        }


        @Override
        public void onClick(View view) {
            if (mClickListener != null) {
                NumbersTable numbersT = numbersTable;
                numbersT.setVoice_mode(voice_mode.isChecked());
                mClickListener.onItemClick(view, numbersT);
            }
        }
    }

    public void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    public interface ItemClickListener {
        void onItemClick(View view, NumbersTable numbersTable);
    }


    public static DiffUtil.ItemCallback<NumbersTable> DIFF_CALLBACK = new DiffUtil.ItemCallback<NumbersTable>() {
        @Override
        public boolean areItemsTheSame(@NonNull NumbersTable oldItem, @NonNull NumbersTable newItem) {
            return oldItem.getId() == newItem.getId();
        }

        @Override
        public boolean areContentsTheSame(@NonNull NumbersTable oldItem, @NonNull NumbersTable newItem) {
            return oldItem.getId() == newItem.getId();
        }


    };
}
