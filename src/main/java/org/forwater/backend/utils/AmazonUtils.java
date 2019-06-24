package org.forwater.backend.utils;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.PutObjectResult;
import org.forwater.backend.config.AppContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;

@Service
@Component
public class AmazonUtils {

    @Autowired
    AppContext appContext;

    public static File convertMultiPartToFile(MultipartFile file) throws IOException {
        File convFile = new File(file.getOriginalFilename());
        FileOutputStream fos = new FileOutputStream(convFile);
        fos.write(file.getBytes());
        fos.close();
        return convFile;
    }


    public static String generateFileName(MultipartFile multiPart) {
        return new Date().getTime() + "-" + multiPart.getOriginalFilename().replace(" ", "_");
    }

    public static void uploadFileTos3bucket(String fileName, File file, AmazonS3 amazonS3) {
        PutObjectResult myresult = amazonS3.putObject(new PutObjectRequest("africa-cdc", fileName, file)
                .withCannedAcl(CannedAccessControlList.PublicReadWrite));
        System.out.println(myresult.getMetadata().getUserMetadata().get(0));
        System.out.println("finished uplaoding");
    }
}
