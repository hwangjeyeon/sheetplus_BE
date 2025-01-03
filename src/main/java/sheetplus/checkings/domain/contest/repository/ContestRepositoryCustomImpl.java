package sheetplus.checkings.domain.contest.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import sheetplus.checkings.domain.event.dto.response.EventResponseDto;
import sheetplus.checkings.domain.contest.entity.Contest;
import sheetplus.checkings.domain.event.entity.Event;
import sheetplus.checkings.domain.enums.EventCategory;
import sheetplus.checkings.exception.exceptionMethod.ApiException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static sheetplus.checkings.domain.contest.entity.QContest.contest;
import static sheetplus.checkings.domain.event.entity.QEvent.event;
import static sheetplus.checkings.exception.error.ApiError.CONTEST_NOT_FOUND;

@Slf4j
@RequiredArgsConstructor
public class ContestRepositoryCustomImpl implements ContestRepositoryCustom{

    private final JPAQueryFactory queryFactory;

    @Override
    public List<EventResponseDto> findTodayEvents(Long contestId) {
        Contest findContest = queryFactory.selectFrom(contest)
                .where(contest.id.eq(contestId))
                .fetchFirst();

        if(findContest == null){
            throw new ApiException(CONTEST_NOT_FOUND);
        }

        List<Event> events = queryFactory.selectFrom(event)
                .where(event.eventContest.id.eq(findContest.getId())
                        .and(event.startTime.dayOfMonth().eq(LocalDateTime.now().getDayOfMonth())
                                .or(event.endTime.dayOfMonth().eq(LocalDateTime.now().getDayOfMonth())))
                )
                .fetch();

        return getEventResponseDtos(events);
    }

    private List<EventResponseDto> getEventResponseDtos(List<Event> events) {
        return events.stream()
                .map(p -> EventResponseDto.builder()
                        .secureId(p.getId().toString())
                        .name(p.getName())
                        .major(p.getMajor())
                        .eventTypeMessage(p.getEventType().getMessage())
                        .conditionMessage(p.getEventCondition().getMessage())
                        .location(p.getLocation())
                        .building(p.getBuilding())
                        .categoryMessage(p.getEventCategory().getMessage())
                        .speakerName(p.getSpeakerName())
                        .startTime(p.getStartTime())
                        .endTime(p.getEndTime())
                        .build())
                .toList();
    }

    @Override
    public List<EventResponseDto> findNowAfterEvents(Long contestId) {
        Contest findContest = queryFactory.selectFrom(contest)
                .where(contest.id.eq(contestId))
                .fetchFirst();

        if(findContest == null){
            throw new ApiException(CONTEST_NOT_FOUND);
        }

        List<Event> events = queryFactory.selectFrom(event)
                .where(event.eventContest.id.eq(findContest.getId())
                        .and(event.startTime.after(LocalDateTime.now())
                                .or(event.endTime.after(LocalDateTime.now())))
                        )
                .fetch();

        return getEventResponseDtos(events);
    }

    @Override
    public List<EventResponseDto> findParticipateEvents(Long contestId, List<EventCategory> eventCategories) {
        Contest findContest = queryFactory.selectFrom(contest)
                .where(contest.id.eq(contestId))
                .fetchFirst();
        if(findContest == null){
            throw new ApiException(CONTEST_NOT_FOUND);
        }


        List<Event> events = new ArrayList<>();

        for (EventCategory category : eventCategories) {
            events.add(queryFactory.selectFrom(event)
                    .where(event.eventContest.id.eq(findContest.getId())
                            .and(event.eventCategory.eq(category)))
                    .fetchFirst());
        }

        return getEventResponseDtos(events);
    }
}
