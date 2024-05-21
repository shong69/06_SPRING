package edu.kh.project.video;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.log;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.UUID;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Value;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import me.desair.tus.server.TusFileUploadService;
import me.desair.tus.server.exception.TusException;
import me.desair.tus.server.upload.UploadInfo;

public class TusService {
    private final TusFileUploadService tusFileUploadService;

    @Value("${tus.save.path}")
    private String savePath;

    public String tusUpload(HttpServletRequest request, HttpServletResponse response) {
        try {
        	// 업로드
            tusFileUploadService.process(request, response);
			
            
            // 현재 업로드 정보
            UploadInfo uploadInfo = tusFileUploadService.getUploadInfo(request.getRequestURI());

			// 완료 된 경우 파일 저장
            if (uploadInfo != null && !uploadInfo.isUploadInProgress()) {
 	           // 파일 저장
               createFile(tusFileUploadService.getUploadedBytes(request.getRequestURI()), uploadInfo.getFileName());

               // 임시 파일 삭제 
               tusFileUploadService.deleteUpload(request.getRequestURI());

                return "success";
            }

            return null;
        } catch (IOException | TusException e) {
            log.error("exception was occurred. message={}", e.getMessage(), e);

            throw new RuntimeException(e);
        }
    }

	// 파일 업로드 (날짜별 디렉토리 하위에 저장)
    private void createFile(InputStream is, String filename) throws IOException {
        LocalDate today = LocalDate.now();

        String uploadedPath = savePath + "/" + today;

        String vodName = getVodName(filename);

        File file = new File(uploadedPath, vodName);

        FileUtils.copyInputStreamToFile(is, file);
    }

	// 파일 이름은 랜덤 UUID 사용
    private String getVodName(String filename) {
        String[] split = filename.split("\\.");
        String uuid = UUID.randomUUID().toString().replaceAll("-", "");
        return uuid + "." + split[split.length - 1];
    }
}
