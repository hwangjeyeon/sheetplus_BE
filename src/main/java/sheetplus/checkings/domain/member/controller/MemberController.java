package sheetplus.checkings.domain.member.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import sheetplus.checkings.business.auth.dto.LoginDto;
import sheetplus.checkings.domain.member.dto.MemberDto.MemberRequestDto;
import sheetplus.checkings.domain.member.dto.MemberDto.MemberUpdateRequestDto;
import sheetplus.checkings.domain.token.dto.TokenDto;
import sheetplus.checkings.domain.member.entity.Member;
import sheetplus.checkings.business.auth.service.AuthService;
import sheetplus.checkings.business.email.service.EmailService;
import sheetplus.checkings.domain.member.service.MemberCRUDService;
import sheetplus.checkings.business.page.superadmin.service.SuperAdminService;
import sheetplus.checkings.util.JwtUtil;

import java.net.URI;

@RestController
@RequiredArgsConstructor
@Slf4j
public class MemberController implements MemberControllerSpec{

    private final MemberCRUDService memberCRUDService;
    private final AuthService authService;
    private final SuperAdminService superAdminService;
    private final EmailService emailService;
    private final JwtUtil jwtUtil;

    @PostMapping("public/member/v1")
    public ResponseEntity<TokenDto> createMember(
            @RequestBody @Validated MemberRequestDto memberRequestDto){

        emailService.verifyEmail(memberRequestDto.getUniversityEmail(),
                memberRequestDto.getCode());

        // 멤버 타입 체크 로직
        if(!authService.memberTypeCheck(memberRequestDto.getMemberType())){
            return ResponseEntity.created(URI.create(""))
                    .body(superAdminService.createAdmin(memberRequestDto));
        }


        Member member = memberCRUDService.createMember(memberRequestDto);
        TokenDto tokenWithData = authService.memberLogin(LoginDto.builder()
                        .id(member.getId())
                        .email(member.getUniversityEmail())
                        .memberType(member.getMemberType())
                .build());

        return ResponseEntity.created(URI.create(""))
                .body(tokenWithData);
    }



    @PutMapping("private/member/v1")
    public ResponseEntity<MemberUpdateRequestDto> updateMember(
            @RequestHeader(value = "Authorization", required = false) String token,
            @RequestBody @Validated MemberUpdateRequestDto memberUpdateRequestDto
    ){

        MemberUpdateRequestDto updatedMember
                = memberCRUDService.updateMember(memberUpdateRequestDto,
                jwtUtil.getMemberId(token.replace("Bearer ", "")));

        return ResponseEntity.ok(updatedMember);
    }


    @DeleteMapping("private/member/v1")
    public ResponseEntity<Void> deleteMember(
            @RequestHeader(value = "Authorization", required = false) String token){
        memberCRUDService.deleteMember(
                jwtUtil.getMemberId(token.replace("Bearer ", "")));
        return ResponseEntity.noContent().build();
    }

}
