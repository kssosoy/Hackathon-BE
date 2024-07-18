package com.example.demo.converter;

import com.example.demo.domain.AIComment;
import com.example.demo.domain.Diary;
import com.example.demo.domain.Member;
import com.example.demo.web.dto.DiaryResponseDTO;
import com.example.demo.web.dto.MemberResponseDTO;
import jakarta.persistence.EntityNotFoundException;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

public class DiaryConverter {
    public static DiaryResponseDTO.AiCommentResultDTO aiCommentResultDTO(Diary diary){
        return DiaryResponseDTO.AiCommentResultDTO.builder()
                .moodId(diary.getMood().getId())
                .moodName(diary.getMood().getName())
                .aiCommentList(diary.getAICommentList().stream()
                        .map(AIComment::getContent)
                        .collect(Collectors.toList()))
                .title(diary.getTitle())
                .content(diary.getContent())
                .build();
    }
}
