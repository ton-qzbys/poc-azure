package com.poc.azure.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("upload-profile-picture")
    public ResponseEntity<MainResponse<UserResponse>> writeBlobFile(@RequestParam(value = "image", required = false) MultipartFile file, HttpServletRequest httpServletRequest) throws IOException {
        String userId = JwtUtil.getObjectIdFromRequest(httpServletRequest);
        return ResponseUtil.data(userService.uploadProfilePicture(file, userId));
    }
}