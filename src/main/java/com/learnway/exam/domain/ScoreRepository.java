package com.learnway.exam.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ScoreRepository extends JpaRepository<Score, Long> {

    Page<Score> findByMemIdAndExam_ExamId(Long memId, Long examId, Pageable pageable);
    Page<Score> findByMemId(Long memId, Pageable pageable);
    Optional<Score> findByMemIdAndScoreId(Long memId, Long scoreId);
    void deleteByMemIdAndScoreId(Long memId, Long scoreId);
    Page<Score> findByMemIdAndExam_ExamIdIn(Long memId, List<Long> examIds, Pageable pageable);
    List<Score> findAllByMemId(Long memId);
}
