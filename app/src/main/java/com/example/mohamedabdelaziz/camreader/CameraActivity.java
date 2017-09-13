package com.example.mohamedabdelaziz.camreader;

import android.Manifest;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.SparseArray;
import android.view.Display;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;
import com.irozon.sneaker.Sneaker;

public class CameraActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView textView;
    private SurfaceView surfaceView;
    private CameraSource cameraSource;
    private TextRecognizer textRecognizer;
    private Button flash, replace, clear, copy, pdf;
    private StringBuilder stringBuilder = new StringBuilder();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        surfaceView = (SurfaceView) findViewById(R.id.surface);
        textView = (TextView) findViewById(R.id.textview);
        flash = (Button) findViewById(R.id.flash);
        replace = (Button) findViewById(R.id.replace);
        clear = (Button) findViewById(R.id.clear);
        copy = (Button) findViewById(R.id.copy);
        pdf = (Button) findViewById(R.id.pdf);
        flash.setOnClickListener(this);
        replace.setOnClickListener(this);
        clear.setOnClickListener(this);
        copy.setOnClickListener(this);
        pdf.setOnClickListener(this);
        textRecognizer = new TextRecognizer.Builder(getApplicationContext()).build();
        if (!textRecognizer.isOperational()) {
            Toast.makeText(this, "text not availble", Toast.LENGTH_SHORT).show();
        } else {
            setCameraSource(CameraSource.CAMERA_FACING_BACK);
        }

        findViewById(R.id.snap).setOnClickListener(this);
    }

    private void setCameraSource(int facing) {
        Display display = getWindowManager().getDefaultDisplay();
        cameraSource = new CameraSource.Builder(getApplicationContext(), textRecognizer)
                .setRequestedPreviewSize(display.getWidth(), display.getWidth())
                .setFacing(facing)
                .setAutoFocusEnabled(true)
                .setRequestedFps(1000.0f)
                .build();
        surfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                try {
                    if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                    cameraSource.start(surfaceView.getHolder());

                } catch (Exception e) {
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                cameraSource.stop();
            }
        });

        textRecognizer.setProcessor(new Detector.Processor<TextBlock>() {
            @Override
            public void release() {
            }

            @Override
            public void receiveDetections(Detector.Detections<TextBlock> detections) {

                final SparseArray<TextBlock> sparseArray = detections.getDetectedItems();
                if (sparseArray.size() != 0) {
                    textView.post(new Runnable() {
                        @Override
                        public void run() {
                            stringBuilder = new StringBuilder();
                            for (int i = 0; i < sparseArray.size(); i++) {
                                TextBlock block = sparseArray.valueAt(i);
                                stringBuilder.append(block.getValue() + "\n");
                            }
                            textView.setText(stringBuilder.toString());
                        }
                    });
                }
            }
        });
    }

//    public void createandDisplayPdf(String text) {
//
//        Document doc = new Document();
//
//        try {
//            String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Dir";
//
//            File dir = new File(path);
//            if(!dir.exists())
//                dir.mkdirs();
//
//            File file = new File(dir, "newFile.pdf");
//            FileOutputStream fOut = new FileOutputStream(file);
//
//            PdfWriter.getInstance(doc, fOut);
//
//            //open the document
//            doc.open();
//
//            Paragraph p1 = new Paragraph(text);
//            Font paraFont= new Font(Font.COURIER);
//            p1.setAlignment(Paragraph.ALIGN_CENTER);
//            p1.setFont(paraFont);
//
//            //add paragraph to document
//            doc.add(p1);
//
//        } catch (DocumentException de) {
//            Log.e("PDFCreator", "DocumentException:" + de);
//        } catch (IOException e) {
//            Log.e("PDFCreator", "ioException:" + e);
//        }
//        finally {
//            doc.close();
//        }
//
//        viewPdf("newFile.pdf", "Dir");
//    }
//
//    // Method for opening a pdf file
//    private void viewPdf(String file, String directory) {
//
//        File pdfFile = new File(Environment.getExternalStorageDirectory() + "/" + directory + "/" + file);
//        Uri path = Uri.fromFile(pdfFile);
//
//        // Setting the intent for pdf reader
//        Intent pdfIntent = new Intent(Intent.ACTION_VIEW);
//        pdfIntent.setDataAndType(path, "application/pdf");
//        pdfIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//
//        try {
//            startActivity(pdfIntent);
//        } catch (ActivityNotFoundException e) {
//            Toast.makeText(TableActivity.this, "Can't read pdf file", Toast.LENGTH_SHORT).show();
//        }
//    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.snap) {
            cameraSource.takePicture(null, new CameraSource.PictureCallback() {
                @Override
                public void onPictureTaken(byte[] bytes) {
                    new ImageAsyncTask(getApplicationContext(), bytes).execute();
                }
            });
        } else if (v.getId() == R.id.copy) {
            ClipboardManager clipboard = (ClipboardManager) getSystemService(getApplicationContext().CLIPBOARD_SERVICE);
            if (stringBuilder.length() > 0) {
                ClipData clip = ClipData.newPlainText("text", stringBuilder.toString());
                clipboard.setPrimaryClip(clip);
                String txt;
                if(stringBuilder.length()>25)
                     txt =stringBuilder.toString().substring(0,25)+" ...." ;
                else
                    txt =stringBuilder.toString() ;
                Sneaker.with(this)
                        .setHeight(130)
                        .setTitle("text copied")
                        .setMessage(txt)
                        .autoHide(false)
                        .sneakSuccess();
            }
            else  Sneaker.with(this)
                    .setHeight(130)
            .autoHide(false)
                    .setTitle("text")
                    .setMessage("not detected")
                    .sneakError();
        } else if (v.getId() == R.id.clear) {
            stringBuilder = new StringBuilder();
            textView.setText(stringBuilder.toString());
        } else if (v.getId() == R.id.flash) {
         //   NoobCameraManager.getInstance().init(this);
        } else if (v.getId() == R.id.pdf) {
            new PDFAsyncTask(getApplicationContext(),stringBuilder.toString()).execute();
        }

    }

}
