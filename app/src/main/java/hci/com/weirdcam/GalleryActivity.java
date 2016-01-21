package hci.com.weirdcam;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import hci.com.weirdcam.adapter.GalleryAdapter;

public class GalleryActivity extends AppCompatActivity{

    private GridView galleryGridView;
    private GalleryAdapter adapter;
    private List<String>  items = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);

        galleryGridView = (GridView) findViewById(R.id.gallery_gridview);
        init();
    }

    private void init() {
        File path = new File(Environment.getExternalStorageDirectory(),"Pictures/WeirdCamera");
        File[] listFile;
        if(path.exists())
        {
            listFile = path.listFiles();
            for (int i = 0; i < listFile.length; i++) {
                items.add(listFile[i].getAbsolutePath());
            }

            adapter = new GalleryAdapter(this, items);
            galleryGridView.setAdapter(adapter);
            galleryGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    String fileName = items.get(position);
                    Intent intent = new Intent(GalleryActivity.this, PhotoViewActivity.class);
                    intent.putExtra("imagePath", fileName);
                    startActivity(intent);
                }
            });
        }
    }
}
