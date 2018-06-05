package at.ac.univie.entertain.elterntrainingapp.service.PdfService;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import at.ac.univie.entertain.elterntrainingapp.Config.Const;
import at.ac.univie.entertain.elterntrainingapp.model.Consequence;
import at.ac.univie.entertain.elterntrainingapp.model.Gedanke;
import at.ac.univie.entertain.elterntrainingapp.model.Loben;
import at.ac.univie.entertain.elterntrainingapp.model.User;
import at.ac.univie.entertain.elterntrainingapp.network.APIInterface;
import at.ac.univie.entertain.elterntrainingapp.network.RetrofitClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

import static android.support.constraint.Constraints.TAG;

public class PdfHandler extends Activity{

    private File pdfFile, pdfFileGedanke, pdfFileCons, pdfFileLoben;
    private SharedPreferences sharedPreferences;
    private User user;
    private String name, email;
    private String token, username;

    public PdfHandler(String token, String username) {
        this.token = token;
        this.username = username;
        getUserData();
    }

    public void createGedanke(List<Gedanke> gedanken, String title) throws DocumentException, IOException {

        PdfPTable table = new PdfPTable(new float[] { 1, 1, 1 ,1, 1 });
        table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
        table.setSpacingBefore(50);
        table.addCell("Situation");
        table.addCell("Bewertung/Interpretation");
        table.addCell("Gefühl");
        table.addCell("Alternative Bewertung");
        table.addCell("Alternative Gefühl");
        table.setHeaderRows(1);
        PdfPCell[] cells = table.getRow(0).getCells();
        for (int j=0;j<cells.length;j++){
            cells[j].setBackgroundColor(BaseColor.GRAY);
        }
        for (int i = 0; i < gedanken.size(); i++) {

            table.addCell(gedanken.get(i).getSituation());
            table.addCell(gedanken.get(i).getBewertung());
            table.addCell(gedanken.get(i).getFeel());
            table.addCell(gedanken.get(i).getAltBewertung());
            table.addCell(gedanken.get(i).getAltReaktion());

        }
        File docsFolder = new File(Environment.getExternalStorageDirectory() + "/ElternTrainingApp");
        if (!docsFolder.exists()) {
            docsFolder.mkdir();
        }
        pdfFileGedanke = new File(docsFolder.getAbsolutePath(),"Gedanken_" + createDate() + ".pdf");
        if (pdfFileGedanke.exists()) {
            pdfFileGedanke.delete();
        }
        pdfFileGedanke.createNewFile();
        OutputStream output = new FileOutputStream(pdfFileGedanke);
        Document document = new Document(PageSize.A4, 20, 20, 50, 25);
        HeaderFooter event = new HeaderFooter(name, email, "", "");
        PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(pdfFileGedanke));
        writer.setPageEvent(event);
        //document.addTitle("Title");

        document.open();
        Paragraph paragraph = new Paragraph(title, new Font(Font.FontFamily.TIMES_ROMAN, 20, Font.BOLD));
        paragraph.setAlignment(Element.ALIGN_CENTER);
        paragraph.setSpacingBefore(50);
        //paragraph.setFont(new Font(Font.FontFamily.TIMES_ROMAN, 20, Font.BOLD));
        document.add(paragraph);
        document.add(table);
        document.close();
        output.flush();
        output.close();
        System.out.println("Done");

    }

    public void createSf(List<String> strengths, List<String> weaknesses, String title) throws DocumentException, IOException {


        PdfPTable table = new PdfPTable(new float[] { 1, 1 });
        table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
        table.setSpacingBefore(50);
        table.addCell("Stärke");
        table.addCell("Schwäche");
        table.setHeaderRows(1);
        PdfPCell[] cells = table.getRow(0).getCells();
        for (int j=0;j<cells.length;j++){
            cells[j].setBackgroundColor(BaseColor.GRAY);
        }
        for (int i = 0 ;i < strengths.size() + weaknesses.size() ; i++){
            System.out.println("strength.size() = " + strengths.size() +  " ------ i = " + i);

            if (strengths.size()-1 < i) {
                table.addCell("");
            } else {
                table.addCell(strengths.get(i));
            }
            System.out.println("weakness.size() = " + weaknesses.size() +  " ------ i = " + i);
            if (weaknesses.size()-1 < i) {
                table.addCell("");
            } else {
                table.addCell(weaknesses.get(i));
            }
        }

        File docsFolder = new File(Environment.getExternalStorageDirectory() + "/ElternTrainingApp");
        if (!docsFolder.exists()) {
            docsFolder.mkdir();
            Log.i(TAG, "Created a new directory for PDF");
        }
        pdfFile = new File(docsFolder.getAbsolutePath(),"Selbstbild_" + createDate() + ".pdf");
        if (pdfFile.exists()) {
            pdfFile.delete();
        }
        pdfFile.createNewFile();
        OutputStream output = new FileOutputStream(pdfFile);
        Document document = new Document(PageSize.A4, 20, 20, 50, 25);
        HeaderFooter event = new HeaderFooter(name, email, "", "");
        PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(pdfFile));
        writer.setPageEvent(event);
        //document.addTitle("Title");

        document.open();
        Paragraph paragraph = new Paragraph(title, new Font(Font.FontFamily.TIMES_ROMAN, 20, Font.BOLD));
        paragraph.setAlignment(Element.ALIGN_CENTER);
        paragraph.setSpacingBefore(50);
        //paragraph.setFont(new Font(Font.FontFamily.TIMES_ROMAN, 20, Font.BOLD));
        document.add(paragraph);
        document.add(table);
        document.close();
        output.flush();
        output.close();
        System.out.println("Done");
    }

    public void createCons(List<Consequence> consList, String title) throws IOException, DocumentException {

        PdfPTable table = new PdfPTable(new float[] { 1, 1, 1 });
        table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
        table.setSpacingBefore(50);
        table.addCell("Situation");
        table.addCell("Reaktion");
        table.addCell("Konsequenz");
        table.setHeaderRows(1);
        PdfPCell[] cells = table.getRow(0).getCells();
        for (int j=0;j<cells.length;j++){
            cells[j].setBackgroundColor(BaseColor.GRAY);
        }
        for (int i = 0; i < consList.size(); i++) {

            table.addCell(consList.get(i).getSituation());
            table.addCell(consList.get(i).getReaktion());
            table.addCell(consList.get(i).getKonsequenz());

        }
        File docsFolder = new File(Environment.getExternalStorageDirectory() + "/ElternTrainingApp");
        if (!docsFolder.exists()) {
            docsFolder.mkdir();
        }
        pdfFileCons = new File(docsFolder.getAbsolutePath(),"Konsequenzen_" + createDate() + ".pdf");
        if (pdfFileCons.exists()) {
            pdfFileCons.delete();
        }
        pdfFileCons.createNewFile();
        OutputStream output = new FileOutputStream(pdfFileCons);
        Document document = new Document(PageSize.A4, 20, 20, 50, 25);
        HeaderFooter event = new HeaderFooter(name, email, "", "");
        PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(pdfFileCons));
        writer.setPageEvent(event);
        //document.addTitle("Title");

        document.open();
        Paragraph paragraph = new Paragraph(title, new Font(Font.FontFamily.TIMES_ROMAN, 20, Font.BOLD));
        paragraph.setAlignment(Element.ALIGN_CENTER);
        paragraph.setSpacingBefore(50);
        //paragraph.setFont(new Font(Font.FontFamily.TIMES_ROMAN, 20, Font.BOLD));
        document.add(paragraph);
        document.add(table);
        document.close();
        output.flush();
        output.close();
        System.out.println("Done");

    }

    public void createLoben(List<Loben> lobenList, String title) throws DocumentException, IOException {

        PdfPTable table = new PdfPTable(new float[] { 1, 1, 1 });
        table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
        table.setSpacingBefore(50);
        table.addCell("In welcher Situation habe ich mein Kind gelobt?");
        table.addCell("Auf welcher Art habe ich mein Kind gelobt?");
        table.addCell("Wie hat mein Kind reagiert?");
        table.setHeaderRows(1);
        PdfPCell[] cells = table.getRow(0).getCells();
        for (int j=0;j<cells.length;j++){
            cells[j].setBackgroundColor(BaseColor.GRAY);
        }
        for (int i = 0; i < lobenList.size(); i++) {

            table.addCell(lobenList.get(i).getSituation());
            table.addCell(lobenList.get(i).getArt());
            table.addCell(lobenList.get(i).getReaktion());

        }
        File docsFolder = new File(Environment.getExternalStorageDirectory() + "/ElternTrainingApp");
        if (!docsFolder.exists()) {
            docsFolder.mkdir();
        }
        pdfFileLoben = new File(docsFolder.getAbsolutePath(),"Kind-Loben_" + createDate() + ".pdf");
        if (pdfFileLoben.exists()) {
            pdfFileLoben.delete();
        }
        pdfFileLoben.createNewFile();
        OutputStream output = new FileOutputStream(pdfFileLoben);
        Document document = new Document(PageSize.A4, 20, 20, 50, 25);
        HeaderFooter event = new HeaderFooter(name, email, "", "");
        PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(pdfFileLoben));
        writer.setPageEvent(event);
        //document.addTitle("Title");

        document.open();
        Paragraph paragraph = new Paragraph(title, new Font(Font.FontFamily.TIMES_ROMAN, 20, Font.BOLD));
        paragraph.setAlignment(Element.ALIGN_CENTER);
        paragraph.setSpacingBefore(50);
        //paragraph.setFont(new Font(Font.FontFamily.TIMES_ROMAN, 20, Font.BOLD));
        document.add(paragraph);
        document.add(table);
        document.close();
        output.flush();
        output.close();
        System.out.println("Done");

    }

    public String getUsername() {
        sharedPreferences = this.getSharedPreferences(Const.SAVE_FILE,MODE_PRIVATE);
        return sharedPreferences.getString(Const.USERNAME_KEY,"");
    }

    public String getToken() {
        sharedPreferences = this.getSharedPreferences(Const.SAVE_FILE,MODE_PRIVATE);
        return sharedPreferences.getString(Const.TOKEN_KEY,"");
    }

    public void getUserData() {

        Retrofit retrofit = RetrofitClient.getRetrofitClient();
        APIInterface api = retrofit.create(APIInterface.class);

        Call<User> call = api.getUser(token, username);
        
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful()) {
                    user = response.body();
                    if (user != null) {
                        name = (user.getFirstName() + " " + user.getLastName());
                        email = user.getEmail();
                        System.out.println("----- " + email);
                    }
                } else {
                    Toast.makeText(PdfHandler.this, response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Toast.makeText(PdfHandler.this, "Fehler auf dem Server", Toast.LENGTH_SHORT).show();
            }
        });

    }

    public String createDate() {
        SimpleDateFormat firstSdf = new SimpleDateFormat("EEE MMM dd yyyy", Locale.GERMANY);
        Date date = new Date();

        String nowDate = firstSdf.format(date);

        return nowDate;
    }

}
