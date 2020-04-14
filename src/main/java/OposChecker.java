import java.io.IOException;

public class OposChecker {

    static String finalPath = "C:\\Users\\LMAR01\\Desktop\\OposPatri\\";
    static String general_1_Path = "General_1.pdf";
    static String general_2_Path = "General_2.pdf";
    static String minus_1_Path = "Minus_1.pdf";
    static String minus_2_Path = "Minus_2.pdf";

    public static void main(String[] args) throws IOException {

        PdfChecker pdfChecker = new PdfChecker(finalPath, general_1_Path, general_2_Path, minus_1_Path, minus_2_Path);

        pdfChecker.startPDF();
    }

}
