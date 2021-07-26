package top.zhangq.music.entity;

import lombok.Data;

/**
 * @author zhangqian
 * @date 2021-07-26 11:51
 */
@Data
public class Music {

    /**
     * 文件名称
     */
    private String name;

    /**
     * 绝对路径
     */
    private String path;

    /**
     * 时间长度 单位秒
     */
    private int len;
    /**
     * 当前位置 单位秒
     */
    private int pos;

    /**
     * 时间格式化
     * @return
     */
    public String getTime() {
        return String.format("%02d:%02d", len / 60, len % 60);
    }
    public String getPosTime() {
        return String.format("%02d:%02d", pos / 60, pos % 60);
    }
}
