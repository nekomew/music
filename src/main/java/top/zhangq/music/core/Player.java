package top.zhangq.music.core;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import top.zhangq.music.entity.Music;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import static top.zhangq.music.core.Command.*;

/**
 * @author zhangqian
 * @date 2021-07-26 10:27
 */
@Component
@Slf4j
public class Player {

    private Process p;
    private BufferedReader reader;
    private BufferedReader errorReader;
    private PrintWriter writer;
    /**
     * 当前播放
     */
    private Music current;

    private int currentIndex;

    private List<Music> playList = new ArrayList<>();

    private Random random = new Random();

    private Thread readerThread;
    /**
     * 播放类型 0 单曲循环 1 循环  2 随机
     */
    private int playType = 1;

    public void stop() {
        init();
        writer.println(S);
    }

    public void pause() {
        init();
        writer.println(P);
    }

    public void volume(int v) {
        if (v < 0) {
            v = 0;
        }
        if (v > 100) {
            v = 100;
        }

        writer.printf(V.getCommand(), v);
    }

    public void jump(int s) {
        if (current == null) {
            return;
        }
        if (s < 0) {
            s = 0;
        }
        if (s > current.getLen()) {
            s = current.getLen();
        }
        writer.printf(J.getCommand(), s);
    }

    public void play() {
        if (playList == null || playList.isEmpty()) {
            return;
        }
        if (current == null || currentIndex >= playList.size() || currentIndex < 0) {
            playNext();
            return;
        }
        init();
        writer.println(S);
        writer.printf(L.getCommand(), current.getPath());
    }

    public void play(int index) {
        if (playList == null || playList.isEmpty() || index >= playList.size()) {
            return;
        }
        current = playList.get(index);
        currentIndex = index;
        play();
    }

    public void nextRandom() {
        int nextInt = random.nextInt(playList.size());

        current = playList.get(nextInt);
        currentIndex = nextInt;
    }

    public void nextList() {
        int nextInt = currentIndex + 1;
        if (current == null || nextInt >= playList.size()) {
            nextInt = 0;
        }
        current = playList.get(nextInt);
        currentIndex = nextInt;
    }

    public void playNext() {
        new Thread(() -> {
            try {
                Thread.sleep(1000);
                if (playType == 1) {
                    nextList();
                    play();
                }
                else if (playType == 2) {
                    nextRandom();
                    play();
                }
                else {
                    play();
                }
            } catch (Exception e) {
                log.error("", e);
            }
        }).start();
    }

    private void run4reader() {
        if (readerThread != null || readerThread.isAlive()) {
            return;
        }
        if (readerThread != null) {
            try {
                readerThread.interrupt();
            } catch (Exception e) {
            }
        }

        readerThread = new Thread(() -> {
            String line = null;
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    while (!Thread.currentThread().isInterrupted() && reader != null && (line = reader.readLine()) != null) {
                        if (current == null) {
                            break;
                        }
                        if (!line.startsWith("@F")) {
                            continue;
                        }
                        String[] split = line.split(" ");
                        if (split.length != 5) {
                            continue;
                        }
                        double pos = Double.parseDouble(split[3]);
                        double remain = Double.parseDouble(split[4]);
                        current.setPos((int) pos);
                        current.setLen((int) (pos + remain));

                        //播放结束
                        if (pos + 1 >= pos + remain) {
                            playNext();
                        }
                    }

                    Thread.sleep(500);
                } catch (Exception e) {
                    log.error("run4reader", e);
                }
            }
        });

        readerThread.setDaemon(true);
        readerThread.start();
    }


    public void add(String file) {
        add(new File(file));
    }

    public void add(File file) {
        if (file == null || !file.exists()) {
            return;
        }

        if (file.isDirectory()) {
            File[] files = file.listFiles();
            Arrays.stream(files).filter(File::isFile).forEach(this::add);
        }

        if (!file.getName().endsWith(".mp3") || !file.getName().endsWith(".wav")) {
            return;
        }

        Music music = new Music();
        music.setName(file.getName());
        music.setPath(file.getAbsolutePath());

        if (playList == null) {
            playList = new ArrayList<>();
        }

        playList.add(music);

        if (current == null) {
            current = music;
            currentIndex = playList.indexOf(music);
        }
    }

    private void init() {
        try {
            if (p != null && p.isAlive()) {
                return;
            }
            destory();

            p = Runtime.getRuntime().exec(MPG123.getCommand());
            reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
            errorReader = new BufferedReader(new InputStreamReader(p.getErrorStream()));
            writer = new PrintWriter(p.getOutputStream());

            run4reader();
        } catch (IOException e) {
            log.error("init", e);
        }
    }

    private void destory() {
        if (reader != null) {
            try {
                reader.close();
            } catch (Exception e) {
                log.error("destory", e);
            }
            reader = null;
        }
        if (errorReader != null) {
            try {
                errorReader.close();
            } catch (Exception e) {
                log.error("destory", e);
            }
            errorReader = null;
        }
        if (writer != null) {
            try {
                writer.close();
            } catch (Exception e) {
                log.error("destory", e);
            }
            writer = null;
        }

        if (p != null) {
            try {
                p.destroy();
            } catch (Exception e) {
                log.error("destory", e);
            }
            p = null;
        }
    }


    public Music getCurrent() {
        return current;
    }

    public void setCurrent(Music current) {
        this.current = current;
    }

    public int getCurrentIndex() {
        return currentIndex;
    }

    public void setCurrentIndex(int currentIndex) {
        this.currentIndex = currentIndex;
    }

    public List<Music> getPlayList() {
        return playList;
    }

    public void setPlayList(List<Music> playList) {
        this.playList = playList;
    }

    public int getPlayType() {
        return playType;
    }

    public void setPlayType(int playType) {
        this.playType = playType;
    }
}
