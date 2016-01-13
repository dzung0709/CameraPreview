package hci.com.weirdcam;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import java.io.File;

public class PhotoViewActivity extends AppCompatActivity{

    private ImageView photoView;
    private Bitmap bitmap;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photoview);

        photoView = (ImageView) findViewById(R.id.photoview_selected_photo);
        init();
    }

    private void init() {
        if (getIntent()!= null) {
            String imageLocation = getIntent().getExtras().getString("imagePath");
            File storePath = Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_PICTURES);
            String filePath = storePath.getPath() + "/WeirdCamera/" + imageLocation;
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            bitmap = BitmapFactory.decodeFile(filePath, options);
            photoView.setImageBitmap(bitmap);
        }
    }

    @Override
    protected void onDestroy() {
        bitmap.recycle();
        bitmap = null;
        super.onDestroy();
    }
}
