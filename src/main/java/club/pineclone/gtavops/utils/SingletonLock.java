package club.pineclone.gtavops.utils;

import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.channels.OverlappingFileLockException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

/* 单运行实例锁 */
public class SingletonLock {

    private static final Path lockFilePath = PathUtils.getLockFilePath();;
    private static FileChannel channel;
    private static FileLock lock;

    public static boolean lockInstance() throws IOException {
        channel = FileChannel.open(lockFilePath, StandardOpenOption.CREATE, StandardOpenOption.WRITE);
        try {
            lock = channel.tryLock();
            if (lock != null) {
                Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                    try {
                        release();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }));
                return true;
            } else {
                return false;
            }
        } catch (OverlappingFileLockException e) {
            return false;
        }
    }

    public static void release() throws IOException {
        if (lock != null) {
            lock.release();
            lock = null;
        }
        if (channel != null) {
            channel.close();
            channel = null;
        }
    }

}
