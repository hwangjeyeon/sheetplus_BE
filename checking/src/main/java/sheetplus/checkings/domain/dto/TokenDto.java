package sheetplus.checkings.domain.dto;

import lombok.*;

@Getter @Setter
@Builder @AllArgsConstructor
@NoArgsConstructor
public class TokenDto {

    private String accessToken;
    private String refreshToken;
    private MemberInfoDto memberInfo;

}
