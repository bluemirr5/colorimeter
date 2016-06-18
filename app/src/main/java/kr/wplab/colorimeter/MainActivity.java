package kr.wplab.colorimeter;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.soundcloud.android.crop.Crop;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends BaseActivity {
    final int CAMERA_CAPTURE = 1;
    final int PIC_CROP_WHITE = 2;
    final int PIC_CROP_COFFEE = 3;
    final Pixel BASE_PIXCEL_A80 = new Pixel(0xb6, 0x94, 0x7a);
    final Pixel BASE_PIXCEL_A60_70 = new Pixel(0x88, 0x6f, 0x58);
    final Pixel BASE_PIXCEL_A50_60 = new Pixel(0x7b, 0x64, 0x4f);
    final Pixel BASE_PIXCEL_A45_50 = new Pixel(0x69, 0x59, 0x4a);
    final Pixel BASE_PIXCEL_A40_45 = new Pixel(0x62, 0x55, 0x48);
    final Pixel BASE_PIXCEL_A35_40 = new Pixel(0x68, 0x5b, 0x4d);
    final Pixel BASE_PIXCEL_A30_35 = new Pixel(0x3f, 0x41, 0x38);
    final Pixel BASE_PIXCEL_A25_30 = new Pixel(0x3b, 0x3e, 0x36);
    final Pixel BASE_PIXCEL_A15_25 = new Pixel(0x16, 0x20, 0x1d);
    private Map<Pixel, String> basePixcel = new HashMap<>();

    private Uri picUri;
    private Uri whiteUri;
    private Uri coffeeUri;

    @Bind(R.id.common_toolbar)
    Toolbar toolbar;
    @Bind(R.id.main_img_result_white)
    ImageView resultWhiteIv;
    @Bind(R.id.main_img_result_coffee)
    ImageView resultCoffeeIv;
    @Bind(R.id.main_result_text)
    TextView resultTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        basePixcel.put(BASE_PIXCEL_A80, "BASE_PIXCEL_A80");
        basePixcel.put(BASE_PIXCEL_A60_70, "BASE_PIXCEL_A60_70");
        basePixcel.put(BASE_PIXCEL_A50_60, "BASE_PIXCEL_A50_60");
        basePixcel.put(BASE_PIXCEL_A45_50, "BASE_PIXCEL_A45_50");
        basePixcel.put(BASE_PIXCEL_A40_45, "BASE_PIXCEL_A40_45");
        basePixcel.put(BASE_PIXCEL_A35_40, "BASE_PIXCEL_A35_40");
        basePixcel.put(BASE_PIXCEL_A30_35, "BASE_PIXCEL_A30_35");
        basePixcel.put(BASE_PIXCEL_A25_30, "BASE_PIXCEL_A25_30");
        basePixcel.put(BASE_PIXCEL_A15_25, "BASE_PIXCEL_A15_25");
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
        switch (requestCode) {
            case CAMERA_CAPTURE:
                if(resultCode == RESULT_OK ) {
                    picUri = data.getData();
                    whiteUri = Uri.fromFile(new File(getCacheDir(), "whiteCroped"));
                    Crop.of(picUri, whiteUri).start(this, PIC_CROP_WHITE);
                } else {
                    toast(getString(R.string.error_take_pic));
                }
                break;
            case PIC_CROP_WHITE:
                if(resultCode != Crop.RESULT_ERROR) {
                    coffeeUri = Uri.fromFile(new File(getCacheDir(), "coffeeCroped"));
                    Crop.of(picUri, coffeeUri).asSquare().start(this, PIC_CROP_COFFEE);
                } else {
                    Log.e("Main", "CROP WHITE AREA");
                    if(data != null) {
                        toast(Crop.getError(data).getMessage());
                    }
                }

                break;
            case PIC_CROP_COFFEE:
                if(resultCode != Crop.RESULT_ERROR) {
                    process(whiteUri, coffeeUri);
                } else {
                    Log.e("Main", "CROP COFFEE AREA");
                    if(data != null) {
                        toast(Crop.getError(data).getMessage());
                        Log.e("Main", "CROP COFFEE AREA", Crop.getError(data));
                    }
                }
                break;
        }
    }

    private void process(Uri pWhitUri, Uri pCoffeeUri) {
        Bitmap whiteBitmap = null;
        Bitmap coffeeBitmap = null;
        try {
            whiteBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), pWhitUri);
            coffeeBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), pCoffeeUri);
            Pixel whitePixcel = bitmapProcess(whiteBitmap);
            Pixel diff = whitePixcel.diff(Pixel.WHITE_PIXEL);
            Pixel coffeePixcel = bitmapProcess(coffeeBitmap);
            Pixel fixedCoffeePixel = coffeePixcel.diff(diff);
            Log.d("Main - whiteArea", whitePixcel.toString());
            Log.d("Main - CoffeeArea", coffeePixcel.toString());
            Log.d("Main - fixedCoffeePixel", fixedCoffeePixel.toString());
            String baseStr = matchBase(fixedCoffeePixel);
            resultTv.setText(baseStr);
            resultWhiteIv.setImageBitmap(whiteBitmap);
            resultCoffeeIv.setImageBitmap(coffeeBitmap);
        } catch (Exception e) {
            Log.e("bitmap err", "bitmap error", e);
        } finally {
            if(whiteBitmap != null && !whiteBitmap.isRecycled())whiteBitmap.recycle();
            if(coffeeBitmap != null && !coffeeBitmap.isRecycled())coffeeBitmap.recycle();
        }
    }

    private Pixel bitmapProcess(Bitmap targetBitmap) {
        List<Pixel> pixels = makeFlatPixels(targetBitmap);

        long totalRed = 0;
        long totalGreen = 0;
        long totalBlue = 0;
        for(Pixel pixel : pixels) {
            totalRed += pixel.getRed();
            totalGreen += pixel.getGreen();
            totalBlue += pixel.getBlue();
        }

        int availPixelCount = pixels.size();
        int avrRed = (int) (totalRed / availPixelCount);
        int avrGreen = (int) (totalGreen / availPixelCount);
        int avrBlue = (int) (totalBlue / availPixelCount);
        return new Pixel(avrRed, avrGreen, avrBlue);
    }

    private List<Pixel> makeFlatPixels(Bitmap targetBitmap) {
        int widthPixel = targetBitmap.getWidth();
        int heightPixel = targetBitmap.getHeight();
        List<Pixel> pixels = new ArrayList<>();
        for(int h = 0; h < heightPixel; h++) {
            for(int w = 0; w < widthPixel; w++) {
                int pixelValue = targetBitmap.getPixel(w, h);
                Pixel pixel = new Pixel(pixelValue);
                if(isCoffeArea(pixel)) {
                    pixels.add(pixel);
                }
            }
        }

        return pixels;
    }

    private boolean isCoffeArea(Pixel pixel) {
        return 0x16 < pixel.getRed() && pixel.getRed() < 0xb6 &&
        0x20 < pixel.getGreen() && pixel.getGreen() < 0x94 &&
        0x1d < pixel.getBlue() && pixel.getBlue() < 0x7a;
    }

    private String matchBase(Pixel pixel) {
        int minDiffValue = Integer.MAX_VALUE;
        Pixel target = null;
        Iterator<Pixel> key = basePixcel.keySet().iterator();
        while(key.hasNext()) {
            Pixel base = key.next();
            Pixel diff = pixel.diff(base);
            if(minDiffValue > diff.toRGBValue()) {
                minDiffValue = diff.toRGBValue();
                target = base;
            }
        }
        return basePixcel.get(target);
    }

    @OnClick(R.id.main_btn_take)
    void clickMainBtnTake(View view) {
        try {
            Intent captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(captureIntent, CAMERA_CAPTURE);
//            Crop.pickImage(this);
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

    private void toast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
}
