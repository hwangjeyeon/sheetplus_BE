package sheetplus.checkings.domain.contest.controller;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import sheetplus.checkings.domain.contest.dto.ContestDto.ContestRequestDto;
import sheetplus.checkings.domain.contest.dto.ContestDto.ContestResponseDto;
import sheetplus.checkings.domain.contest.service.ContestCRUDService;

import java.net.URI;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("private/admin")
public class ContestController implements ContestControllerSpec{

    private final ContestCRUDService contestCRUDService;

    @PostMapping("/contests/v1")
    public ResponseEntity<ContestResponseDto> createContest(
            @RequestBody @Validated ContestRequestDto contestRequestDto) {

        return ResponseEntity.created(URI.create(""))
                .body(contestCRUDService.createContest(contestRequestDto));
    }

    @PutMapping("/contests/{contest}/v1")
    public ResponseEntity<ContestResponseDto> updateContest(
            @PathVariable("contest") Long contestId,
            @RequestBody @Validated ContestRequestDto contestRequestDto
    ){
        return ResponseEntity.ok(contestCRUDService
                                .updateContest(contestId, contestRequestDto));
    }

    @DeleteMapping("/contests/{contest}/v1")
    public ResponseEntity<Void> deleteContest(
            @PathVariable("contest") Long contestId
    ){
        contestCRUDService.deleteContest(contestId);

        return ResponseEntity.noContent().build();
    }


}
