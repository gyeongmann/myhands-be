package tabom.myhands.domain.google.service;

import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.firebase.messaging.FirebaseMessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tabom.myhands.domain.alarm.service.AlarmService;
import tabom.myhands.domain.board.dto.BoardRequest;
import tabom.myhands.domain.board.entity.Board;
import tabom.myhands.domain.board.repository.BoardRepository;
import tabom.myhands.error.errorcode.BoardErrorCode;
import tabom.myhands.error.exception.BoardApiException;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.services.sheets.v4.model.AppendValuesResponse;
import com.google.api.services.sheets.v4.model.ValueRange;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.*;


@Service
@RequiredArgsConstructor
@Slf4j
public class GoogleBoardServiceImpl implements GoogleBoardService {

    private static final String SPREADSHEET_ID = "1adSZxf7wF9P6gfMVmu5qGIyl3Yj67Vm_33IA302LhEg"; // 테스트 시트 id
    private final BoardRepository boardRepository;
    private final AlarmService alarmService;
    private static final String APPLICATION_NAME = "myhands"; // Google Sheets 애플리케이션의 이름
    private static final JacksonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance(); // JSON 데이터를 처리하기 위한 JsonFactory 인스턴스
    private static final String CREDENTIALS_FILE_PATH = "googleSheetKey.json"; // 인증에 사용되는 JSON 파일 경로 지정(resources 아래 기준)
    private Sheets sheetsService;

    @Override
    @Transactional
    public void create(Long userId, BoardRequest.Edit requestDto) throws FirebaseMessagingException {
        if (requestDto.getTitle() == null || requestDto.getTitle().isEmpty() ||
                requestDto.getContent() == null || requestDto.getContent().isEmpty()) {
            throw new BoardApiException(BoardErrorCode.TITLE_OR_CONTENT_MISSING);
        }

        Optional<Board> opBoard = boardRepository.findByGoogleId(requestDto.getBoardId());
        if(opBoard.isPresent()) {
            Board board = opBoard.get();
            board.edit(requestDto);
            boardRepository.save(board);
        } else {
            Board board = Board.googleBoardBuild(requestDto, userId);
            boardRepository.save(board);
            alarmService.createBoardAlarm(board);
        }
    }

    public void createBoardToSheet(Board board) {
        Long googleId = boardRepository.findMaxGoogleId() + 1;
        board.updateGoogleId(googleId);
        boardRepository.save(board);

        List<List<Object>> values = new ArrayList<>();
        String range = "참고. 게시판!B" + (googleId + 6) + ":D" + (googleId + 6);  // 데이터를 삽입할 범위
        List<Object> row = Arrays.asList(googleId, board.getTitle(), board.getContent());  // 각 셀에 들어갈 값
        values.add(row);
        writeToSheet(SPREADSHEET_ID, range, values);
    }

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

    public void writeToSheet(String spreadsheetId, String range, List<List<Object>> values) {
        try {
            Sheets service = getSheetsService();
            ValueRange body = new ValueRange().setValues(values);
            AppendValuesResponse result = service.spreadsheets()
                    .values()
                    .append(spreadsheetId, range, body)
                    .setValueInputOption("USER_ENTERED")
                    .setIncludeValuesInResponse(true)
                    .execute();
        } catch (Exception e) {
            log.error("Failed to write data to the spreadsheet", e);
            throw new RuntimeException("Failed to write data to the spreadsheet: " + e.getMessage(), e);
        }
    }
}
