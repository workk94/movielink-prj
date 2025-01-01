package com.acorn.movielink.comunity.service;

import com.acorn.movielink.comunity.dto.PostDTO;
import com.acorn.movielink.comunity.dto.PostImageDTO;
import com.acorn.movielink.comunity.dto.TagDTO;
import com.acorn.movielink.comunity.repository.CommunityPostMapper;
import com.acorn.movielink.comunity.repository.PostImageMapper;
import com.acorn.movielink.comunity.repository.TagMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.util.List;


@Service
@Transactional
public class CommunityPostService {

    private final PostImageService postImageService;
    private CommunityPostMapper postMapper;
    private TagMapper tagMapper;
    private PostImageMapper postImageMapper;

    @Autowired
    CommunityPostService (CommunityPostMapper postMapper,
                          TagMapper tagMapper,
                          PostImageMapper postImageMapper, PostImageService postImageService) {
        this.postMapper = postMapper;
        this.tagMapper = tagMapper;
        this.postImageMapper = postImageMapper;
        this.postImageService = postImageService;
    }

    // 게시글 전체 조회
    public List<PostDTO> selectAllList() {
        return postMapper.selectAllList();
    }

    // 좋아요 많은 게시글 Top10
    public List<PostDTO> selectTopTenPosts() {
        return postMapper.selectTopTenPosts();
    }


    // 게시글 ID로 게시글 상세 조회
    public PostDTO selectPostById(int postId) {
        postMapper.updatePostViews(postId);
        PostDTO postOne = postMapper.selectPostById(postId);
        System.out.println("좋아요 수: " + postOne.getPostLikeCnt());

        // 태그를 문자열 리스트로 조회 및 설정
        List<TagDTO> tags = tagMapper.selectTagsByPostId(postId);
        postOne.setTags(tags);

        return postOne;
    }

    @Transactional(readOnly = true)
    public boolean existsById(int postId) {
        return postMapper.countById(postId) > 0;
    }


    public boolean existsPostById(int postId) {
        PostDTO post = postMapper.selectPostById(postId); // 게시글 조회
        return post != null; // 게시글 존재 여부 반환
    }



    // 게시글 수정
    public void updatePost(PostDTO postDTO) {
        postMapper.updatePost(postDTO);
    }

    // 게시글 삭제
    public void deletePost(int postId) {
        // 게시글 삭제전 게시글에 연결된 이미지 가져오기
        List<PostImageDTO> postImages = postImageService.getPostImagesByPostId(postId);
        postMapper.deletePost(postId);

        // 경로에 위치한 파일 제거
        for (PostImageDTO image : postImages) {
            String fullPath = image.getFilePath() + image.getStoredFileNm();
            File file = new File(fullPath);
            if (file.exists()) {
                file.delete();
            }
        }
    }

    // 좋아요 많은 유저 Top7 조회
    public List<PostDTO> selectTop7Member() {
        return postMapper.selectTop7Member();
    }

    // 특정 유저의 게시글 목록 조회
    public List<PostDTO> selectOneMemberPostList(int memId) {
        return postMapper.selectOneMemberPostList(memId);
    }

    // 게시글 작성 및 태그 처리

    public int insertPost(PostDTO postDTO) {
        // 1. 게시글 삽입
        postMapper.insertPost(postDTO);

        // 2. 게시글 ID 검증
        int postId = postDTO.getPostId(); // Auto-generated key 확인
        if (postId == 0) {
            throw new RuntimeException("게시글 ID 생성 실패!");
        }

        return postId;
    }
}