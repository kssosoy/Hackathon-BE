package com.example.demo.service.AIService;

import com.example.demo.domain.Diary;

public interface AICommentService {
    Diary generateAIComment(Long diaryId);
}
