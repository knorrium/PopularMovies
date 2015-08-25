package knorrium.info.popularmovies.util;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.logging.Logger;

import knorrium.info.popularmovies.R;

public class ImageHelper {
    public static int IMAGE_SIZE_NORMAL = 0;
    public static int IMAGE_SIZE_THUMBNAIL = 1;

    public static void loadAsyncImage(
            Context context,
            ImageView imageView,
            int imageSize,
            String posterPath) {

        Picasso.with(context)
                .load(getImageUrl(context, imageSize, posterPath))
                .tag(context)
                .into(imageView);
    }

    private static String getImageUrl(Context context, int imageSize, String posterPath) {
        String baseUrl = context.getString(R.string.api_poster_entrypoint);
        String imageSizePath;
        if (imageSize == IMAGE_SIZE_THUMBNAIL) {
            imageSizePath = context.getString(R.string.thumbnail_image_path);
        } else {
            imageSizePath = context.getString(R.string.regular_image_path);
        }

        Uri imageUri =
                Uri.parse(baseUrl).buildUpon()
                        .appendPath(imageSizePath)
                        .appendPath(posterPath.replace("/",""))
                        .build();

        return imageUri.toString();
    }
}
