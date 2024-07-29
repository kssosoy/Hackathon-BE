package com.example.demo.service;

import com.example.demo.domain.AIComment;
import com.example.demo.domain.Diary;
import com.example.demo.domain.Member;
import com.example.demo.domain.Mood;
import com.example.demo.web.dto.DiaryRequestDTO;
import com.example.demo.web.dto.DiaryResponseDTO;
import com.example.demo.repository.DiaryRepository;
import com.example.demo.repository.MemberRepository;
import com.example.demo.repository.MoodRepository;
import com.example.demo.service.AIService.AICommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DiaryService {

    private final DiaryRepository diaryRepository;
    private final MemberRepository memberRepository;
    private final MoodRepository moodRepository;
    private final AICommentService aiCommentService; // AICommentService 주입

    public DiaryResponseDTO createDiary(DiaryRequestDTO diaryRequestDTO) {
        Optional<Member> memberOptional = memberRepository.findById(diaryRequestDTO.getMemberId());
        if (!memberOptional.isPresent()) {
            throw new RuntimeException("Member not found");
        }

        Optional<Mood> moodOptional = moodRepository.findById(diaryRequestDTO.getMoodId());
        if (!moodOptional.isPresent()) {
            throw new RuntimeException("Mood not found");
        }

        Member member = memberOptional.get();
        Mood mood = moodOptional.get();

        Diary diary = Diary.builder()
                .title(diaryRequestDTO.getTitle())
                .content(diaryRequestDTO.getContent())
                .isPublic(diaryRequestDTO.isPublic())
                .member(member)
                .mood(mood)
                .likeCount(0)
                .build();

        Diary savedDiary = diaryRepository.save(diary);

        // AI 댓글 생성
        aiCommentService.generateAIComment(savedDiary.getId());

        return DiaryResponseDTO.builder()
                .id(savedDiary.getId())
                .title(savedDiary.getTitle())
                .content(savedDiary.getContent())
                .isPublic(savedDiary.isPublic())
                .memberUsername(member.getUsername())
                .likeCount(savedDiary.getLikeCount())
                .moodName(mood.getName())
                .moodImage(mood.getMoodImage())
                .createdAt(savedDiary.getCreatedAt())
                .aiComments(savedDiary.getAiComment() != null ? List.of(savedDiary.getAiComment().getContent()) : null)  // AI 댓글 포함
                .build();
    }

    public DiaryResponseDTO getDiary(Long diaryId) {
        Optional<Diary> diaryOptional = diaryRepository.findById(diaryId);
        if (!diaryOptional.isPresent()) {
            throw new RuntimeException("Diary not found");
        }

        Diary diary = diaryOptional.get();
        Mood mood = diary.getMood();
        AIComment aiComment = diary.getAiComment();

        return DiaryResponseDTO.builder()
                .id(diary.getId())
                .title(diary.getTitle())
                .content(diary.getContent())
                .isPublic(diary.isPublic())
                .memberUsername(diary.getMember().getUsername())
                .likeCount(diary.getLikeCount())
                .moodName(mood != null ? mood.getName() : null)
                .moodImage(mood != null ? mood.getMoodImage() : null)
                .createdAt(diary.getCreatedAt())
                .aiComments(aiComment != null ? List.of(aiComment.getContent()) : null)  // AI 댓글 포함
                .build();
    }

    public List<DiaryResponseDTO> getDiariesByMember(Long memberId) {
        Optional<Member> memberOptional = memberRepository.findById(memberId);
        if (!memberOptional.isPresent()) {
            throw new RuntimeException("Member not found");
        }

        List<Diary> diaries = diaryRepository.findByMemberId(memberId);
        return diaries.stream()
                .map(diary -> {
                    Mood mood = diary.getMood();
                    AIComment aiComment = diary.getAiComment();

                    return DiaryResponseDTO.builder()
                            .id(diary.getId())
                            .title(diary.getTitle())
                            .content(diary.getContent())
                            .isPublic(diary.isPublic())
                            .memberUsername(diary.getMember().getUsername())
                            .likeCount(diary.getLikeCount())
                            .moodName(mood != null ? mood.getName() : null)
                            .moodImage(mood != null ? mood.getMoodImage() : null)
                            .createdAt(diary.getCreatedAt())
                            .aiComments(aiComment != null ? List.of(aiComment.getContent()) : null)  // AI 댓글 포함
                            .build();
                })
                .collect(Collectors.toList());
    }

    public DiaryResponseDTO getMostLikedDiary() {
        Optional<Diary> diaryOptional = diaryRepository.findTopByOrderByLikeCountDesc();
        if (!diaryOptional.isPresent()) {
            throw new RuntimeException("No diaries found");
        }

        Diary diary = diaryOptional.get();
        Mood mood = diary.getMood();
        AIComment aiComment = diary.getAiComment();

        return DiaryResponseDTO.builder()
                .id(diary.getId())
                .title(diary.getTitle())
                .content(diary.getContent())
                .isPublic(diary.isPublic())
                .memberUsername(diary.getMember().getUsername())
                .likeCount(diary.getLikeCount())
                .moodName(mood != null ? mood.getName() : null)
                .moodImage(mood != null ? mood.getMoodImage() : null)
                .createdAt(diary.getCreatedAt())
                .aiComments(aiComment != null ? List.of(aiComment.getContent()) : null)  // AI 댓글 포함
                .build();
    }
}
