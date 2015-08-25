package knorrium.info.popularmovies;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

final class CustomPosterImageView extends ImageView {
    public CustomPosterImageView(Context context) {
        super(context);
    }

    public CustomPosterImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(getMeasuredWidth(), (int) Math.round(getMeasuredWidth() * 1.5));
    }
}