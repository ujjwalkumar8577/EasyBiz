package com.ujjwalkumar.easybiz.util;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

public class PDFprinter {
    public static void stringtopdf(String data) {
        String extstoragedir = Environment.getExternalStorageDirectory().toString();
        File fol = new File(extstoragedir, "pdf");
        File folder = new File(fol, "pdf");
        if (!folder.exists()) {
            boolean bool = folder.mkdirs();
        }
        try {
            File file = new File(folder, "sample.pdf");
            file.createNewFile();
            FileOutputStream fOut = new FileOutputStream(file);

            PdfDocument document = new PdfDocument();
            PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(100, 100, 1).create();
            PdfDocument.Page page = document.startPage(pageInfo);
            Canvas canvas = page.getCanvas();
            Paint paint = new Paint();
            canvas.drawText(data, 10, 10, paint);

            document.finishPage(page);
            document.writeTo(fOut);
            document.close();

        }
        catch (IOException e) {
            e.printStackTrace();
            Log.i("error", e.getMessage());
        }
    }

    public static void savePDF(String data, String filename) {
        try {

            PdfDocument document = new PdfDocument();
            PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(420, 595, 1).create();
            PdfDocument.Page page = document.startPage(pageInfo);
            Canvas canvas = page.getCanvas();
            Paint paint = new Paint();
            canvas.drawText(data, 10, 10, paint);
            document.finishPage(page);

            String path = FileUtil.getExternalStorageDir().concat("/EasyBiz/").concat(filename);
            FileUtil.writeFile(path, "");

            File myFile = new File(path);
            FileOutputStream fOut = new FileOutputStream(myFile);
            OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut);
            document.writeTo(fOut);
            document.close();
            myOutWriter.close();
            fOut.close();
            Log.d("success","File saved successfully");
        }
        catch (Exception e) {
            Log.d("error", e.getMessage());
        }
    }
}
