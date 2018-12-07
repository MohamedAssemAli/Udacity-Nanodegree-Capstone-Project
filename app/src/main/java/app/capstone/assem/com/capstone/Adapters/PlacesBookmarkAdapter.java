package app.capstone.assem.com.capstone.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Objects;

import app.capstone.assem.com.capstone.Activities.DetailsActivity;
import app.capstone.assem.com.capstone.App.AppConfig;
import app.capstone.assem.com.capstone.Models.PlaceBookmarkModel;
import app.capstone.assem.com.capstone.R;
import butterknife.BindView;
import butterknife.ButterKnife;

public class PlacesBookmarkAdapter extends RecyclerView.Adapter<PlacesBookmarkAdapter.PlaceHolder> {

    private Context context;
    private ArrayList<PlaceBookmarkModel> placeBookmarkModelArrayList;
    private String uid;

    public PlacesBookmarkAdapter(Context context, ArrayList<PlaceBookmarkModel> placeBookmarkModelArrayList, String uid) {
        this.context = context;
        this.placeBookmarkModelArrayList = placeBookmarkModelArrayList;
        this.uid = uid;
    }

    @NonNull
    @Override
    public PlaceHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View item = LayoutInflater.from(context).inflate(R.layout.item_place_bookmark, parent, false);
        return new PlaceHolder(item);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull PlaceHolder holder, int position) {
        final PlaceBookmarkModel placeBookmarkModel = placeBookmarkModelArrayList.get(position);
        holder.placeTitle.setText(placeBookmarkModel.getTitle());
        holder.placeLatLan.setText("Lat : " + placeBookmarkModel.getLat() + "\nLon : " + placeBookmarkModel.getLon());

        holder.placeTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToDetailsActivity(placeBookmarkModel);
            }
        });

        holder.placeLatLan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToDetailsActivity(placeBookmarkModel);
            }
        });

        holder.placeImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToDetailsActivity(placeBookmarkModel);
            }
        });

        holder.placeDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setupRemoveBookmarkDialog(placeBookmarkModel);
            }
        });
    }

    @Override
    public int getItemCount() {
        return placeBookmarkModelArrayList.size();
    }

    public class PlaceHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.item_place_bookmark_img)
        ImageView placeImg;
        @BindView(R.id.item_place_bookmark_delete)
        ImageView placeDelete;
        @BindView(R.id.item_place_bookmark_title)
        TextView placeTitle;
        @BindView(R.id.item_place_bookmark_lat_lan)
        TextView placeLatLan;

        PlaceHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    private void goToDetailsActivity(PlaceBookmarkModel placeBookmarkModel) {
        Intent intent = new Intent(context, DetailsActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable(AppConfig.PLACE_INTENT_KEY, placeBookmarkModel);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }

    private void setupRemoveBookmarkDialog(final PlaceBookmarkModel placeBookmarkModel) {
        AlertDialog.Builder builder = new AlertDialog.Builder(Objects.requireNonNull(context));
        final View mView = LayoutInflater.from(context).inflate(R.layout.dialog_remove_bookmark, null);

        Button removeBtn = mView.findViewById(R.id.dialog_remove_bookmark_remove_btn);
        Button cancelBtn = mView.findViewById(R.id.dialog_remove_bookmark_cancel_btn);

        builder.setView(mView);
        final AlertDialog alertDialog = builder.create();
        alertDialog.show();

        removeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removePlaceToBookmark(placeBookmarkModel, uid);
                alertDialog.dismiss();
            }
        });

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
    }

    private void removePlaceToBookmark(PlaceBookmarkModel placeBookmarkModel, String uid) {
        FirebaseDatabase.getInstance().getReference().child(AppConfig.BOOKMARKS).child(uid).child(placeBookmarkModel.getUid()).removeValue();
        this.notifyDataSetChanged();
    }
}
