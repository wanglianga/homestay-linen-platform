package com.homestay.linen.controller;

import com.homestay.linen.common.Result;
import com.homestay.linen.entity.LinenTrack;
import com.homestay.linen.service.LinenTrackService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/linenTrack")
@RequiredArgsConstructor
public class LinenTrackController {

    private final LinenTrackService linenTrackService;

    @PostMapping
    public Result<LinenTrack> create(@RequestBody LinenTrack linenTrack) {
        return linenTrackService.create(linenTrack);
    }

    @GetMapping("/bagId/{bagId}")
    public Result<List<LinenTrack>> getByBagId(@PathVariable Long bagId) {
        return linenTrackService.getByBagId(bagId);
    }

    @GetMapping("/bagCode/{bagCode}")
    public Result<List<LinenTrack>> getByBagCode(@PathVariable String bagCode) {
        return linenTrackService.getByBagCode(bagCode);
    }

    @GetMapping("/roomNo/{roomNo}")
    public Result<List<LinenTrack>> getByRoomNo(@PathVariable String roomNo) {
        return linenTrackService.getByRoomNo(roomNo);
    }

    @GetMapping("/list")
    public Result<List<LinenTrack>> list() {
        return linenTrackService.list();
    }
}
