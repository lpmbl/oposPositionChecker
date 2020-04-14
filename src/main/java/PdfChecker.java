import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PdfChecker {

    String dniPatri = "";
    static String general_1_Path, general_2_Path, minus_1_Path, minus_2_Path;

    Hashtable<String, Float> general_1_List, general_2_List, minus_1_List, minus_2_List, totalGeneralList, totalMinusList;

    static String finalPath;

    static String general_1_Resuls = "general_1.txt";
    static String general_2_Resuls = "general_2.txt";
    static String minus_1_Resuls = "minus_1.txt";
    static String minus_2_Resuls = "minus_2.txt";
    static String finalResult = "Result.txt";

    public PdfChecker(String finalPath, String general_1, String general_2, String mimus_1, String minus_2) {

        PdfChecker.finalPath = finalPath;
        general_1_Path = general_1;
        general_2_Path = general_2;
        minus_1_Path = mimus_1;
        minus_2_Path = minus_2;

    }

    public void startPDF() throws IOException {
        char type1 = '1';
        char type2 = '2';
        general_1_List = readPDF(finalPath + general_1_Path, finalPath + general_1_Resuls, type1);
        general_2_List = readPDF(finalPath + general_2_Path, finalPath + general_2_Resuls, type2);
        minus_1_List = readPDF(finalPath + minus_1_Path, finalPath + minus_1_Resuls, type1);
        minus_2_List = readPDF(finalPath + minus_2_Path, finalPath + minus_2_Resuls, type2);

        totalGeneralList = combineSameType(general_1_List, general_2_List);
        totalMinusList = combineSameType(minus_1_List, minus_2_List);

        writeToFile(combineResult(totalGeneralList, totalMinusList), finalPath + finalResult);
    }

    public Hashtable<String, Float> readPDF(String entryPath, String finalPath, char type) throws IOException {

        Hashtable<String, Float> alumnList = new Hashtable<>();
        PDDocument document = PDDocument.load(new File(entryPath));

        if (!document.isEncrypted()) {
            PDFTextStripper stripper = new PDFTextStripper();
            String text = stripper.getText(document);

            alumnList = userFilter(text, type);

            //System.out.println(text);
            //writeToFile(finalPath, printAlumns(alumnList));
        }
        document.close();

        return alumnList;
    }

    public Hashtable<String, Float> userFilter(String text, char type) {
        Hashtable<String, Float> alumnList = new Hashtable<>();
        Pattern pattern = Pattern.compile("\\d{5,}");
        String[] lines = text.split(System.getProperty("line.separator"));

        for (String line : lines) {
            Matcher m = pattern.matcher(line);
            if (m.find()) {

                if (type == '1') {
                    String[] alumno = createAlumn_1(line);
                    alumnList.put(alumno[0], Float.parseFloat(alumno[1]));
                }
                if (type == '2') {
                    String[] alumno = createAlumn_2(line);
                    alumnList.put(alumno[0], Float.parseFloat(alumno[1]));
                }
            }
        }

        return alumnList;
    }

    private Hashtable<String, Float> combineSameType(Hashtable<String, Float> firstExam, Hashtable<String, Float> secondExam) {
        Hashtable<String, Float> total = new Hashtable<>();

        Float firstResult;
        Float secondtResult;

        Set<String> keys = secondExam.keySet();
        for (String key : keys) {
            if (firstExam.containsKey(key)) {
                firstResult = firstExam.get(key);
                secondtResult = secondExam.get(key);
                total.put(key, firstResult + secondtResult);
            }
        }

        return total;
    }

    private Map<String, Float> combineResult(Hashtable<String, Float> totalGeneralList, Hashtable<String, Float> totalMinusList) {

        Hashtable<String, Float> finalExam = new Hashtable<>();

        finalExam.putAll(totalGeneralList);
        finalExam.putAll(totalMinusList);

        List<Map.Entry<String, Float>> list = new ArrayList<>(finalExam.entrySet());

        list.sort(Map.Entry.comparingByValue());
        Collections.reverse(list);

        Map<String, Float> mapSortedByValues = new LinkedHashMap<>();

//put all sorted entries in LinkedHashMap
        for (Map.Entry<String, Float> entry : list) {
            mapSortedByValues.put(entry.getKey(), entry.getValue());
        }

        Set<String> keys = mapSortedByValues.keySet();
        int i = 1;
        for (String key : keys) {
            System.out.println(i++ + " - " + key + "\t\t" + mapSortedByValues.get(key));
        }
        return mapSortedByValues;
    }


    private String[] createAlumn_1(String line) {
        String[] alumno = new String[2];

        Pattern pattern = Pattern.compile("\\d{5,}");
        Matcher m = pattern.matcher(line);
        String[] fields = pattern.split(line);

        String nota = fields[1].substring(0, fields[1].length() - 1);
        nota = nota.replace(',', '.');

        String dni = "";
        if (m.find()) {
            dni = m.group(0);

            if (dni.startsWith("0")) {
                dni = dni.substring(1);
            }
        }

        alumno[0] = dni;
        alumno[1] = nota;

        return alumno;
    }

    private String[] createAlumn_2(String line) {
        String[] alumno = new String[2];
        Pattern pattern = Pattern.compile("[ ]{1}?");
        String[] fields = pattern.split(line);

        String nota = fields[fields.length - 1];
        nota = nota.replace(',', '.');

        String dni = fields[0].substring(0, fields[0].length() - 1);
        if (dni.startsWith("0")) {
            dni = dni.substring(1);
        }
        alumno[0] = dni;
        alumno[1] = nota;

        return alumno;
    }

    private String printAlumns(Hashtable<String, Float> alumns) {
        StringBuilder txt = new StringBuilder();
        Set<String> keys = alumns.keySet();

        /*
        for (String key : keys) {
            //txt.append(alumn.getDni()).append("\t").append(alumn.getNota()).append("\n");
            System.out.println("Value of " + key + " is: " + alumns.get(key));
        }
        */

        return txt.toString();
    }

    private void writeToFile(Map<String, Float> result, String path) throws IOException {
        BufferedWriter writer;
        StringBuilder res = new StringBuilder();

        Set<String> keys = result.keySet();
        int i = 1;
        for (String key : keys) {
            if (key.equals(dniPatri))
                res.append("\t\t\t\t\t\t\t\t\t\t\t\t");
            res.append(i++).append(" - ").append(key).append("\t\t").append(result.get(key)).append("\n");
        }

        File logFile = new File(path);
        writer = new BufferedWriter(new FileWriter(logFile));
        writer.write(res.toString());
        writer.close();
    }

}
