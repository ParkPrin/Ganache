package me.parkprin.ganache.aws.service;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.SdkClientException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.PutObjectResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.parkprin.ganache.document.model.MenuType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.time.LocalDate;

@Component @Slf4j
@RequiredArgsConstructor
public class S3UploadComponent {
    // S3에 저장되는 경로
    // {아마존 S3에서 사용하는 프로토콜}://{아마존 S3에서 사용하는 Host}{아마존 S3에서 사용하는 Path}
    // 아마존 S3에서 사용하는 Path 규칙: /storage/{stage}/{헤싱한네이밍}.xls
    public static Regions clientRegion = Regions.AP_NORTHEAST_2;
    public static String bucketName = "marketbom2";
    public static String rootPath = "storage";
    public static String stagePath = "dev";

    @Value("${cloud.aws.credentials.accessKey}")
    private String accessKey;

    @Value("${cloud.aws.credentials.secretKey}")
    private String secretKey;

    public String getAwsCredentials(String fileObjKeyName, File file, MenuType menuType) {
        AWSCredentials credentials = new BasicAWSCredentials(accessKey, secretKey);
        AmazonS3 s3Client = AmazonS3ClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .withRegion(clientRegion)
                .build();
        return s3UploadReturnUploadFilePath(getForderPath(menuType)+fileObjKeyName, s3Client, file);
    }

    private String s3UploadReturnUploadFilePath(String fileObjKeyName, AmazonS3 s3Client, File file) {
        String s3UploadFilePath = null;
        try {

            PutObjectRequest request = new PutObjectRequest(bucketName, fileObjKeyName, file);
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentType("application/vnd.ms-excel; charset=UTF-8");
            request.setMetadata(metadata);
            request.withCannedAcl(CannedAccessControlList.PublicRead);
            s3Client.putObject(request);

            s3UploadFilePath = getS3UploadFileUrl(s3Client, fileObjKeyName);
        } catch (AmazonServiceException e) {
            log.error("AmazonServiceException", e);
        } catch (SdkClientException e) {
            log.error("SdkClientException", e);
        }
        return s3UploadFilePath;
    }

    private String getForderPath(MenuType menuType){
        LocalDate localDate = LocalDate.now();
        return rootPath+"/" + stagePath + "/" + menuType.getName() +
                "/" + localDate.getYear() +
                "/" + localDate.getMonth().getValue() +
                "/" + localDate.getDayOfMonth() +
                "/";
    }

    private String getS3UploadFileUrl(AmazonS3 s3Client, String fileObjKeyName){
        String protocal = s3Client.getUrl(bucketName, fileObjKeyName).getProtocol();
        String host = s3Client.getUrl(bucketName, fileObjKeyName).getHost();
        String path = s3Client.getUrl(bucketName, fileObjKeyName).getPath();
        return protocal+ "://"+host+path;
    }
}
