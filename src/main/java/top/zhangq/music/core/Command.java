package top.zhangq.music.core;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Command {

    MPG123("mpg123 -R", "启动mgp123 进入远程控制模式")
    ,L("L %s%n", "加载并播放")
    ,LP("LP %s%n", "加载不播放")
    ,P("P", "暂停/播放")
    ,S("S", "停止播放 关闭文件")
    ,J("J %ss%n", "跳转到 mpeg 帧 <frame> 或通过偏移改变位置，如果数字后跟“s”，则以秒为单位")
    ,V("V %s%n", "以 % (0..100...) 为单位设置音量；浮点值")
    ;

    private String command;
    private String desc;

    @Override
    public String toString() {
        return command;
    }

}
