package com.acorn.movielink.community.service;

import com.acorn.movielink.community.dto.TagDTO;
import com.acorn.movielink.community.repository.TagMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TagService {

    @Autowired
    private TagMapper tagMapper;

    // 인기 태그 조회
    @Transactional(readOnly = true)
    public List<TagDTO> selectTopTags() {
        return tagMapper.selectTopTags();
    }

    // 게시글 ID로 태그 조회
    @Transactional(readOnly = true)
    public List<TagDTO> selectTagsByPostId(int postId) {
        return tagMapper.selectTagsByPostId(postId);
    }

    // 태그 존재 여부 확인
    @Transactional(readOnly = true)
    public Integer checkTagExists(String tagName) {
        return tagMapper.checkTagExists(tagName);
    }

    // 태그 삽입
    @Transactional(propagation = Propagation.REQUIRED)
    public int insertTag(TagDTO tagDTO) {
        tagMapper.insertTag(tagDTO);
        return tagDTO.getTagId();
    }


    // 게시글-태그 관계 삭제
    @Transactional(propagation = Propagation.REQUIRED)
    public void deletePostTags(int postId) {
        tagMapper.deletePostTags(postId);
    }



    // 게시글-태그 관계 추가
    @Transactional(propagation = Propagation.REQUIRED)
    public void insertPostTag(int postId, int tagId) {
        if (tagId == 0) { // ID 검증
            throw new RuntimeException("유효하지 않은 태그 ID! tagId=" + tagId);
        }
        System.out.println("관계 추가 시도: postId=" + postId + ", tagId=" + tagId); // 로그 추가
        tagMapper.insertPostTag(postId, tagId); // 관계 삽입
        System.out.println("관계 추가 완료: postId=" + postId + ", tagId=" + tagId); // 삽입 후 로그
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void processTagsForPost(int postId, List<TagDTO> tagNames) {
        System.out.println("태그 처리 시작: postId=" + postId + ", tagNames=" + tagNames);

        tagMapper.deletePostTags(postId); // void 반환 타입 처리
        System.out.println("기존 관계 삭제 완료: postId=" + postId);

        for (TagDTO tagName : tagNames) {
            System.out.println("태그 처리 중: " + tagName);

            // 태그 ID 확인 또는 삽입
            int tagId = insertOrGetTagId(tagName.getTagName());
            System.out.println("태그 ID 확인 완료: tagName=" + tagName + " → tagId=" + tagId);
            // 관계 추가
            if (tagId == 0) { // ID가 잘못된 경우 예외 처리
                throw new RuntimeException("유효하지 않은 태그 ID! tagName=" + tagName);
            }
            // 관계 추가
            tagMapper.insertPostTag(postId, tagId);
            System.out.println("관계 추가 완료: postId=" + postId + ", tagId=" + tagId);
        }

        System.out.println("태그 처리 완료: postId=" + postId);
    }


    // 태그 삽입 또는 조회
    @Transactional(propagation = Propagation.REQUIRED)
    public int insertOrGetTagId(String tagName) {
        Integer tagId = tagMapper.checkTagExists(tagName);
        System.out.println("태그 존재 여부: " + tagName + " → " + tagId); // 로그 추가

        if (tagId == null) {
            // 태그 새로 삽입
            TagDTO tagDTO = new TagDTO();
            tagDTO.setTagName(tagName);
            tagMapper.insertTag(tagDTO);
            if (tagDTO.getTagId() == 0) {
                throw new RuntimeException("태그 삽입 실패! tagName: " + tagName);
            }
            tagId = tagDTO.getTagId();
            System.out.println("태그 삽입 완료: " + tagName + " → " + tagId); // 삽입 후 로그
        }
        if (tagId == null || tagId == 0) { // 삽입 후에도 키가 없으면 예외 처리
            throw new RuntimeException("태그 삽입 실패! tagName: " + tagName);
        }
        return tagId;
    }


    @Transactional(propagation = Propagation.REQUIRED)
    public List<TagDTO> parseTags(String tagsInput) {
        List<TagDTO> tagList = new ArrayList<>();
        if (tagsInput == null || tagsInput.trim().isEmpty()) {
            return tagList; // 태그 입력이 없는 경우 빈 리스트 반환
        }

        tagList = Arrays.stream(tagsInput.split(",")) // 쉼표로 구분
                .map(String::trim)  // 공백 제거
                .filter(tag -> tag.startsWith("#")) // '#'으로 시작하는 태그만 처리
                .map(tag -> {
                    if (tag.length() <= 1) { // 최소 길이 검증
                        throw new IllegalArgumentException("태그는 최소 2글자 이상이어야 합니다.");
                    }
                    // TagDTO 객체로 변환
                    return new TagDTO(0, tag.substring(1)); // '#' 제거 후 태그 저장
                })
                .collect(Collectors.toList()); // 최종적으로 List<TagDTO> 반환
        return tagList;
    }

}