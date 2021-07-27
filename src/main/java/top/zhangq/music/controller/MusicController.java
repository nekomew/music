package top.zhangq.music.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import top.zhangq.music.core.Player;

import java.util.HashMap;

/**
 * @author zhangqian
 * @date 2021-07-27 10:07
 */
@Slf4j
@RequestMapping("music")
@RestController
public class MusicController {

    @Autowired
    private Player player;

    @GetMapping("playInfo")
    public Object playInfo() {
        HashMap<String, Object> map = new HashMap<>();

        map.put("list", player.getPlayList());
        map.put("current", player.getCurrent());
        map.put("currentIndex", player.getCurrentIndex());
        map.put("playType", player.getPlayType());

        return map;
    }


}
