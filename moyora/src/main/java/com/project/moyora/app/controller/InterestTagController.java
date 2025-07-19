package com.project.moyora.app.controller;

import com.project.moyora.app.dto.InterestTagGroupDto;
import com.project.moyora.app.service.InterestTagService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class InterestTagController {

    private final InterestTagService interestTagService;

    public InterestTagController(InterestTagService interestTagService) {
        this.interestTagService = interestTagService;
    }

    @GetMapping("/interest-tags/select")
    public ResponseEntity<List<InterestTagGroupDto>> getInterestTags() {
        return ResponseEntity.ok(interestTagService.getGroupedInterestTags());
    }
}
