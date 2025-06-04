package register;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hanoicraftcorner.R;

import java.util.List;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ImageViewHolder> {
    private final List<Uri> imageUris;
    private final Context context;
    private final OnImageActionListener listener;

    public interface OnImageActionListener {
        void onImageClick(Uri uri);
        void onDeleteClick(int position);
    }

    public ImageAdapter(Context context, List<Uri> imageUris, OnImageActionListener listener) {
        this.context = context;
        this.imageUris = imageUris;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.register_artisan_item_image, parent, false);
        return new ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
        Uri uri = imageUris.get(position);
        if (uri != null) {
            holder.imageView.setImageURI(uri);
        } else {
            holder.imageView.setImageDrawable(null);
        }
        holder.imageView.setOnClickListener(v -> listener.onImageClick(uri));
        holder.deleteButton.setOnClickListener(v -> listener.onDeleteClick(position));
    }

    @Override
    public int getItemCount() {
        return imageUris.size();
    }

    public static class ImageViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        ImageButton deleteButton;
        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.image_item_view);
            deleteButton = itemView.findViewById(R.id.delete_image_btn);
        }
    }
}

