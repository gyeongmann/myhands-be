package tabom.myhands.domain.user.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tabom.myhands.common.jwt.TokenUtils;
import tabom.myhands.common.response.DtoResponse;
import tabom.myhands.common.response.MessageResponse;
import tabom.myhands.domain.user.dto.UserRequest;
import tabom.myhands.domain.user.dto.UserResponse;
import tabom.myhands.domain.user.service.UserService;
import tabom.myhands.common.properties.ResponseProperties;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {

    private final UserService userService;
    private final ResponseProperties responseProperties;

    @PostMapping(value ="/join")
    public ResponseEntity<MessageResponse> createUser(HttpServletRequest request, @RequestBody UserRequest.Join requestDto) {
        boolean isAdmin = (boolean) request.getAttribute("isAdmin");
        userService.join(isAdmin, requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(MessageResponse.of(HttpStatus.CREATED, responseProperties.getSuccess()));
    }

    @GetMapping("/duplicate")
    public ResponseEntity<MessageResponse> checkDuplicate(HttpServletRequest request, @RequestParam String id) {
        boolean isAdmin = (boolean) request.getAttribute("isAdmin");
        userService.isDuplicate(isAdmin, id);
        return ResponseEntity.status(HttpStatus.OK).body(MessageResponse.of(HttpStatus.OK, responseProperties.getSuccess()));
    }

    @PostMapping("/login")
    public ResponseEntity<DtoResponse<UserResponse.Login>> loginUser(@RequestBody UserRequest.Login request) {
        UserResponse.Login response = userService.login(request);
        return ResponseEntity.status(HttpStatus.OK).body(DtoResponse.of(HttpStatus.OK, responseProperties.getSuccess(), response));
    }

    @DeleteMapping("/logout")
    public ResponseEntity<MessageResponse> logoutUser(HttpServletRequest request, @RequestHeader("Authorization") String accessTokenHeader) {
        Long userId = (Long) request.getAttribute("userId");
        boolean isAdmin = (boolean) request.getAttribute("isAdmin");
        String accessToken = TokenUtils.extractToken(accessTokenHeader);
        userService.logout(userId, isAdmin, accessToken);
        return ResponseEntity.status(HttpStatus.OK).body(MessageResponse.of(HttpStatus.OK, responseProperties.getSuccess()));
    }

    @PatchMapping("/password")
    public ResponseEntity<MessageResponse> editPassword(HttpServletRequest request, @RequestBody UserRequest.Password requestDto){
        Long userId = (Long) request.getAttribute("userId");
        userService.editPassword(userId, requestDto);
        return ResponseEntity.status(HttpStatus.OK).body(MessageResponse.of(HttpStatus.OK, responseProperties.getSuccess()));
    }

    @PatchMapping("/image")
    public ResponseEntity<MessageResponse> editImage(HttpServletRequest request, @RequestParam Integer avartaId){
        Long userId = (Long) request.getAttribute("userId");
        userService.editImage(userId, avartaId);
        return ResponseEntity.status(HttpStatus.OK).body(MessageResponse.of(HttpStatus.OK, responseProperties.getSuccess()));
    }

    @GetMapping("/info")
    public ResponseEntity<DtoResponse<UserResponse.Info>> getInfo(HttpServletRequest request){
        Long userId = (Long) request.getAttribute("userId");
        UserResponse.Info response = userService.getInfo(userId);
        return ResponseEntity.status(HttpStatus.OK).body(DtoResponse.of(HttpStatus.OK, responseProperties.getSuccess(), response));
    }

    @GetMapping("/list")
    public ResponseEntity<DtoResponse<List<UserResponse.UserList>>> getList(HttpServletRequest request){
        boolean isAdmin = (boolean) request.getAttribute("isAdmin");
        List<UserResponse.UserList> response = userService.getList(isAdmin);
        return ResponseEntity.status(HttpStatus.OK).body(DtoResponse.of(HttpStatus.OK, responseProperties.getSuccess(), response));
    }

    @GetMapping("/detail")
    public ResponseEntity<DtoResponse<UserResponse.Detail>> getDetail(HttpServletRequest request, @RequestParam Long userId){
        boolean isAdmin = (boolean) request.getAttribute("isAdmin");
        UserResponse.Detail response = userService.getDetail(isAdmin, userId);
        return ResponseEntity.status(HttpStatus.OK).body(DtoResponse.of(HttpStatus.OK, responseProperties.getSuccess(), response));
    }
}