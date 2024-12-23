package com.acorn.movielink.people_detail.service;

import com.acorn.movielink.people_detail.repository.PeopleLikeMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PeopleLikeService {

    @Autowired
    private PeopleLikeMapper peopleLikeMapper;

    @Transactional
    public void likePerson(Integer peopleId, Integer memId) {
        peopleLikeMapper.insertLike(peopleId, memId);
    }

    @Transactional
    public void unlikePerson(Integer peopleId, Integer memId) {
        peopleLikeMapper.deleteLike(peopleId, memId);
    }

    public int getLikeCount(Integer peopleId) {
        return peopleLikeMapper.countLikes(peopleId);
    }

    public boolean isPersonLikedByUser(Integer peopleId, Integer memId) {
        return peopleLikeMapper.isLiked(peopleId, memId);
    }
}
