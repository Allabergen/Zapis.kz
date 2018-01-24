package su.allabergen.zapiskz2;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class SalonAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Activity context;
    private ArrayList<Salon> data;

    SalonAdapter(Activity context, ArrayList<Salon> data) {
        this.context = context;
        this.data = data;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView;
        itemView = LayoutInflater.from(context).inflate(R.layout.recommend_list_item, parent, false);
        return new VH(itemView);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        VH vh = (VH) holder;
        Salon current = data.get(position);
        vh.name.setText(current.name);
        vh.type.setText(current.type);
        if (current.pictureUrl != null && !current.pictureUrl.equals("")) {
            Glide.with(context).load("http://zp.jgroup.kz" + current.pictureUrl)
                    .error(android.R.drawable.stat_notify_error)
                    .into(vh.picture);
        }

    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    private class VH extends RecyclerView.ViewHolder {

        private ImageView picture;
        private TextView name;
        private TextView type;

        VH(View itemView) {
            super(itemView);
            picture = (ImageView) itemView.findViewById(R.id.picture);
            name = (TextView) itemView.findViewById(R.id.name);
            type = (TextView) itemView.findViewById(R.id.type);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, SalonDetail.class);
                    intent.putExtra("id", data.get(getAdapterPosition()).id);
                    context.startActivity(intent);
                }
            });
        }
    }
}
