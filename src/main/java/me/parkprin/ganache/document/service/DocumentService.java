package me.parkprin.ganache.document.service;

import me.parkprin.ganache.aws.service.S3UploadComponent;
import me.parkprin.ganache.document.model.Document;
import me.parkprin.ganache.document.model.MenuType;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class DocumentService {

    @Autowired
    S3UploadComponent s3UploadComponent;

    public List<Document> createDummyData(){
        List<Document> documents = new ArrayList<>();
        documents.add(new Document(1L, "박종훈", "서울", "010-1111-2222"));
        documents.add(new Document(2L, "최지웅", "한국", "010-3333-4444"));
        documents.add(new Document(3L, "설연수", "대한민국", "010-5555-6666"));
        documents.add(new Document(4L, "오준택", "지구", "010-7777-8888"));
        return documents;
    }

    private List<Field> getAllFields(Class clazz) {
        List<Field> fields = new ArrayList<Field>();

        fields.addAll(Arrays.asList( clazz.getDeclaredFields() ));

        Class superClazz = clazz.getSuperclass();
        if(superClazz != null){
            fields.addAll( getAllFields(superClazz) );
        }

        return fields;
    }
    private HSSFWorkbook cretaeExcelInstance(List<Document> dummyDatas){
        List<Field> columnList = getAllFields(Document.class);
        HSSFWorkbook workbook = new HSSFWorkbook();
        HSSFSheet sheet = workbook.createSheet("document");
        HSSFRow row = sheet.createRow(0);
        HSSFCell cell;


        for (int i = 0; i <columnList.size(); i++) {
            cell = row.createCell(i);
            cell.setCellValue(columnList.get(i).getName());
        }

        for (int i = 0; i < dummyDatas.size(); i++) {
            row = sheet.createRow(i+1);
            cell = row.createCell(0);
            cell.setCellValue(dummyDatas.get(i).getId());

            cell = row.createCell(1);
            cell.setCellValue(dummyDatas.get(i).getName());

            cell = row.createCell(2);
            cell.setCellValue(dummyDatas.get(i).getAddress());

            cell = row.createCell(3);
            cell.setCellValue(dummyDatas.get(i).getPhone());
        }

        return workbook;
    }

    public Map<String, String> createExcelFileAfterS3Upload(List<Document> dummyDatas){
        Map<String, String> resultMap = new HashMap<>();
        HSSFWorkbook workbook = cretaeExcelInstance(dummyDatas);
        String fileName = getSHA256(createFileNameByDate("Excel")) +".xls";
        File file  = new File("./"+ fileName);
        FileOutputStream fos  = null;

        try {
            fos = new FileOutputStream(file);
            workbook.write(fos);
            resultMap.put("state", "success");
            resultMap.put("fileName", fileName);
            resultMap.put("s3FileURL", s3UploadComponent.getAwsCredentials(fileName, file, MenuType.Item));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            resultMap.put("error", e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
            resultMap.put("error", e.getMessage());
        } finally {
            try {
                if (workbook != null) workbook.close();
                if(fos != null) fos.close();
            } catch (IOException e) {
                e.printStackTrace();
                resultMap.put("error", e.getMessage());
            }
        }
        return resultMap;
    }

    private String createFileNameByDate(String defaultName){
        SimpleDateFormat format = new SimpleDateFormat ( "yyyy-MM-dd HH:mm:ss");
        return format.format(Calendar.getInstance(Locale.KOREAN).getTime()).toString() + "_" + defaultName;
    }

    private String getSHA256(String input){

        String toReturn = null;
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            digest.reset();
            digest.update(input.getBytes("utf8"));
            toReturn = String.format("%064x", new BigInteger(1, digest.digest()));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return toReturn;
    }
}
