package sheetplus.checkings.domain.entry.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.hateoas.Link;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sheetplus.checkings.business.page.admin.controller.AdminPageController;
import sheetplus.checkings.domain.entry.controller.EntryController;
import sheetplus.checkings.domain.entry.dto.EntryDto.EntryRequestDto;
import sheetplus.checkings.domain.entry.dto.EntryDto.EntryResponseDto;
import sheetplus.checkings.domain.contest.entity.Contest;
import sheetplus.checkings.domain.entry.entity.Entry;
import sheetplus.checkings.domain.contest.repository.ContestRepository;
import sheetplus.checkings.domain.entry.repository.EntryRepository;
import sheetplus.checkings.exception.exceptionMethod.ApiException;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;
import static sheetplus.checkings.exception.error.ApiError.CONTEST_NOT_FOUND;
import static sheetplus.checkings.exception.error.ApiError.ENTRY_NOT_FOUND;

@Service
@Slf4j
@RequiredArgsConstructor
public class EntryCRUDService {

    private final EntryRepository entryRepository;
    private final ContestRepository contestRepository;


    @Transactional
    public EntryResponseDto createEntry(Long contestId, EntryRequestDto entryRequestDto){
        Entry entry = Entry.builder()
                .name(entryRequestDto.getName())
                .location(entryRequestDto.getLocation())
                .building(entryRequestDto.getBuilding())
                .teamNumber(entryRequestDto.getTeamNumber())
                .professorName(entryRequestDto.getProfessorName())
                .major(entryRequestDto.getMajor())
                .leaderName(entryRequestDto.getLeaderName())
                .entryType(entryRequestDto.getEntryType())
                .build();
        Contest contest = contestRepository.findById(contestId)
                .orElseThrow(() -> new ApiException(CONTEST_NOT_FOUND));
        entry.setContestEntry(contest);
        Long entryId = entryRepository.save(entry).getId();



        return EntryResponseDto.builder()
                .id(entryId)
                .name(entry.getName())
                .location(entry.getLocation())
                .building(entry.getBuilding())
                .teamNumber(entry.getTeamNumber())
                .professorName(entry.getProfessorName())
                .leaderName(entry.getLeaderName())
                .major(entry.getMajor())
                .entryType(entry.getEntryType().getMessage())
                .link(hateoasLinks(contestId))
                .build();
    }

    @Transactional
    public EntryResponseDto updateEntry(Long id, EntryRequestDto entryRequestDto){
        Entry entry = entryRepository.findById(id)
                .orElseThrow(() -> new ApiException(ENTRY_NOT_FOUND));;
        entry.updateEntry(entryRequestDto);
        Long contestId = entry.getEntryContest().getId();

        return EntryResponseDto.builder()
                .id(id)
                .name(entry.getName())
                .location(entry.getLocation())
                .building(entry.getBuilding())
                .teamNumber(entry.getTeamNumber())
                .major(entry.getMajor())
                .professorName(entry.getProfessorName())
                .leaderName(entry.getLeaderName())
                .entryType(entry.getEntryType().getMessage())
                .link(hateoasLinks(contestId))
                .build();
    }

    private List<Link> hateoasLinks(Long contestId) {
        List<Link> lists = new ArrayList<>();
        lists.add(linkTo(methodOn(AdminPageController.class)
                .readAdminHomeStampStats(contestId)).withRel("관리자 Home 페이지 - 스탬프 통계"));
        lists.add(linkTo(methodOn(AdminPageController.class)
                .readAdminHomeContestStats(contestId)).withRel("관리자 Home 페이지 - Contest 통계"));
        lists.add(linkTo(methodOn(AdminPageController.class)
                .readAdminHomeEventStats(contestId,1,1)).withRel("관리자 Home 페이지 - Event 통계"));
        lists.add(linkTo(methodOn(AdminPageController.class)
                .readAdminHomeEntryStats(contestId, 1,1)).withRel("관리자 Home 페이지 - Entry 통계"));
        lists.add(linkTo(methodOn(EntryController.class)
                .createEntry(contestId, EntryRequestDto.builder().build()))
                .withRel("작품 CREATE"));
        lists.add(linkTo(methodOn(EntryController.class)
                .deleteEntry(contestId))
                .withRel("작품 DELETE"));
        return lists;
    }

    @Transactional
    public void deleteEntry(Long id){
        if(entryRepository.existsById(id)){
            entryRepository.deleteById(id);
        }else{
            throw new ApiException(ENTRY_NOT_FOUND);
        }
    }


}
