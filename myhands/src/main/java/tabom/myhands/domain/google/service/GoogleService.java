package tabom.myhands.domain.google.service;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.ClearValuesRequest;
import com.google.api.services.sheets.v4.model.UpdateValuesResponse;
import com.google.api.services.sheets.v4.model.ValueRange;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class GoogleService {
    private static final String SPREADSHEET_ID = "1adSZxf7wF9P6gfMVmu5qGIyl3Yj67Vm_33IA302LhEg"; // 테스트 시트 id
    private static final String APPLICATION_NAME = "myhands"; // Google Sheets 애플리케이션의 이름
    private static final JacksonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance(); // JSON 데이터를 처리하기 위한 JsonFactory 인스턴스
    private static final String CREDENTIALS_FILE_PATH = "googleSheetKey.json"; // 인증에 사용되는 JSON 파일 경로 지정(resources 아래 기준)
    private Sheets sheetsService;

    // Sheets 인스턴스 생성 메소드
    private Sheets getSheetsService() throws IOException, GeneralSecurityException {
        if (sheetsService == null) {
            // 권한 지정
            GoogleCredentials credentials = GoogleCredentials.fromStream(new ClassPathResource(CREDENTIALS_FILE_PATH).getInputStream())
                    .createScoped(Collections.singletonList("https://www.googleapis.com/auth/spreadsheets"));
            sheetsService = new Sheets.Builder(GoogleNetHttpTransport.newTrustedTransport(), JSON_FACTORY, new HttpCredentialsAdapter(credentials))
                    .setApplicationName(APPLICATION_NAME)
                    .build();
        }
        return sheetsService;
    }

    public void writeToSheet(String range, List<List<Object>> values) {
        try {
            sheetsService = getSheetsService();
            ClearValuesRequest clearRequest = new ClearValuesRequest();
            sheetsService.spreadsheets().values()
                    .clear(SPREADSHEET_ID, range, clearRequest)
                    .execute();

            ValueRange body = new ValueRange().setValues(values).setMajorDimension("ROWS");
            UpdateValuesResponse result = sheetsService.spreadsheets()
                    .values()
                    .update(SPREADSHEET_ID, range, body)
                    .setValueInputOption("USER_ENTERED")
                    .setIncludeValuesInResponse(true)
                    .execute();
        } catch (Exception e) {
            log.error("Failed to write data to the spreadsheet", e);
            throw new RuntimeException("Failed to write data to the spreadsheet: " + e.getMessage(), e);
        }
    }
}
