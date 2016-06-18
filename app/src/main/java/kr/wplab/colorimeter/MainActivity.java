package kr.wplab.colorimeter;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends BaseActivity {
    final int CAMERA_CAPTURE = 1;
    final int PIC_CROP_WHITE = 2;
    final int PIC_CROP_COFFEE = 3;
    private Uri picUri;
    private Bitmap whiteBitmap;
    private Bitmap coffeeBitmap;

    @Bind(R.id.common_toolbar)
    Toolbar toolbar;
    @Bind(R.id.main_img_result_white)
    ImageView resultWhiteIv;
    @Bind(R.id.main_img_result_coffee)
    ImageView resultCoffeeIv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK ) {
            Intent cropIntent = new Intent("com.android.camera.action.CROP");
            cropIntent.setDataAndType(picUri, "image/*");
            cropIntent.putExtra("crop", "true");
            cropIntent.putExtra("aspectX", 1);
            cropIntent.putExtra("aspectY", 1);
            cropIntent.putExtra("outputX", 256);
            cropIntent.putExtra("outputY", 256);
            cropIntent.putExtra("return-data", true);
            Bundle extras = data.getExtras();
            Bitmap thePic = extras.getParcelable("data");
            switch (requestCode) {
                case CAMERA_CAPTURE:
                    picUri = data.getData();
                    startActivityForResult(cropIntent, PIC_CROP_WHITE);
                    break;
                case PIC_CROP_WHITE:
                    resultWhiteIv.setImageBitmap(thePic);
                    whiteBitmap = thePic;
                    startActivityForResult(cropIntent, PIC_CROP_COFFEE);
                    break;
                case PIC_CROP_COFFEE:
                    coffeeBitmap = thePic;
                    resultCoffeeIv.setImageBitmap(thePic);
                    break;
            }
        } else {
            if(whiteBitmap != null) whiteBitmap.recycle();
            if(coffeeBitmap != null) coffeeBitmap.recycle();
        }
    }

    @OnClick(R.id.main_btn_take)
    void clickMainBtnTake(View view) {
        try {
            //use standard intent to capture an image
            Intent captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            //we will handle the returned data in onActivityResult
            startActivityForResult(captureIntent, CAMERA_CAPTURE);
        } catch(ActivityNotFoundException anfe){
            Snackbar.make(view, "Whoops - your device doesn't support capturing images!", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        }
    }

    @OnClick(R.id.main_fab)
    void clickMainFab(View view) {
        Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
    }
}
