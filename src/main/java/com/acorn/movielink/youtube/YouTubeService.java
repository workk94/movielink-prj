package com.acorn.movielink.youtube;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

@Service
public class YouTubeService {
    private static final String API_KEY = "AIzaSyA99KE6ZVpVIOdCI43JCbtUfDYBaBGUZCw";
    private static final String SEARCH_URL = "https://www.googleapis.com/youtube/v3/search";
    private static final String VIDEOS_URL = "https://www.googleapis.com/youtube/v3/videos";

    private final RestTemplate restTemplate;

    public YouTubeService() {
//        this.API_KEY = youtubeAPIKey;
        this.restTemplate = new RestTemplate();
    }

    // Step 1: 영화 관련 동영상 검색
    public List<String> searchMovieVideos(String query) {
        String url = UriComponentsBuilder.fromUriString(SEARCH_URL)
                .queryParam("key", API_KEY)
                .queryParam("q", query)
                .queryParam("part", "snippet")
                .queryParam("type", "video")
                .queryParam("regionCode", "KR")
                .queryParam("relevanceLanguage", "ko")
                .queryParam("maxResults", 50)
                .queryParam("safeSearch", "moderate")
                .queryParam("videoEmbeddable", "true")
                .queryParam("order", "viewCount")
                .queryParam("pageToken", "")
                .toUriString();

        YouTubeSearchResponse response = restTemplate.getForObject(url, YouTubeSearchResponse.class);

        List<String> videoIds = new ArrayList<>();
        if (response != null && response.getItems() != null) {
            for (YouTubeSearchResponse.Item item : response.getItems()) {
                videoIds.add(item.getId().getVideoId());
            }
        }

//        List<String> videoIds = new ArrayList<>();
//        if (response != null && response.getItems() != null) {
//            for (YouTubeSearchResponse.Item item : response.getItems()) {
//                // 제목 및 설명에서 한국어 필터링
//                if (isKoreanText(item.getSnippet().getTitle()) || isKoreanText(item.getSnippet().getDescription())) {
//                    videoIds.add(item.getId().getVideoId());
//                }
//            }
//        }

        return videoIds;
    }

    // Step 2: 동영상 길이 필터링
    public List<String> filterShorts(List<String> videoIds) {
        String url = UriComponentsBuilder.fromHttpUrl(VIDEOS_URL)
                .queryParam("key", API_KEY)
                .queryParam("id", String.join(",", videoIds))
                .queryParam("part", "contentDetails")
                .toUriString();

        YouTubeVideoDetailsResponse response = restTemplate.getForObject(url, YouTubeVideoDetailsResponse.class);

        List<String> shortVideos = new ArrayList<>();
        if (response != null && response.getItems() != null) {
            for (YouTubeVideoDetailsResponse.Item item : response.getItems()) {
                String duration = item.getContentDetails().getDuration();
                if (duration != null && duration.startsWith("PT") && !duration.contains("M")) {
                    shortVideos.add("https://www.youtube.com/embed/" + item.getId());
                }
            }
        }
        return shortVideos;
    }

    public List<String> getMovieShorts(String query) {
        List<String> videoIds = searchMovieVideos(query);
        return filterShorts(videoIds);
    }

    public List<String> searchMovieMusicVideos(String query) {
        String url = UriComponentsBuilder.fromUriString(SEARCH_URL)
                .queryParam("key", API_KEY)
                .queryParam("q", query) // 영화 관련 음악 검색
                .queryParam("part", "snippet")
                .queryParam("type", "video")
                .queryParam("videoCategoryId", "10") // 음악 카테고리
                .queryParam("regionCode", "KR") // 한국 지역
                .queryParam("relevanceLanguage", "ko") // 한국어 우선
                .queryParam("maxResults", 50) // 결과 제한
                .queryParam("safeSearch", "moderate")
                .queryParam("videoEmbeddable", "true")
                .queryParam("order", "viewCount")
                .toUriString();

        YouTubeSearchResponse response = restTemplate.getForObject(url, YouTubeSearchResponse.class);

        List<String> videoIds = new ArrayList<>();
        if (response != null && response.getItems() != null) {
            for (YouTubeSearchResponse.Item item : response.getItems()) {
                videoIds.add(item.getId().getVideoId());
            }
        }
        return videoIds;
    }

    // 한국어 텍스트 확인 함수
//    private boolean isKoreanText(String text) {
//        if (text == null) return false;
//        // 한글 정규식: 한글 유니코드 범위 체크
//        Pattern koreanPattern = Pattern.compile("[가-힣]");
//        return koreanPattern.matcher(text).find();
//    }
}
