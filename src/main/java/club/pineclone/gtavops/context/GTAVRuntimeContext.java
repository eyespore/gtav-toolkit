package club.pineclone.gtavops.context;

import lombok.Getter;
import lombok.Setter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class GTAVRuntimeContext {

    @Getter
    private static final GTAVRuntimeContext INSTANCE = new GTAVRuntimeContext();

    private static final long CHECK_INTERVAL = 1000L; // 每秒检查一次
    private final ScheduledExecutorService scheduler;

    @Getter
    @Setter
    private int gameVersion;

    public GTAVRuntimeContext() {
        this.scheduler = Executors.newSingleThreadScheduledExecutor();
        this.gameVersion = 0;
    }

//    public void startMonitoring() {
//        scheduler.scheduleAtFixedRate(this::checkGameProcess, 0, CHECK_INTERVAL, TimeUnit.MILLISECONDS);
//    }

//    private void checkGameProcess() {
//        String newGameVersion = fetchCurrentGameVersion();
//        if (!newGameVersion.equals(gameVersion)) {
//            gameVersion = newGameVersion;
//            // 触发更新逻辑，例如通知其他组件
//            System.out.println(gameVersion);
//        }
//    }

    private String fetchCurrentGameVersion() {
        try {
            // 先检查是否存在 gta5.exe（传承版）
            Process legacy = Runtime.getRuntime().exec("tasklist /FI \"IMAGENAME eq gta5.exe\"");
            if (isProcessRunning(legacy)) {
                return "legacy";
            }

            // 再检查是否存在 gtav.exe（增强版）
            Process enhanced = Runtime.getRuntime().exec("tasklist /FI \"IMAGENAME eq GTA5_Enhanced.exe\"");
            if (isProcessRunning(enhanced)) {
                return "enhanced";
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return "Unknown";
    }

    private boolean isProcessRunning(Process process) throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                // “No tasks are running” 是 tasklist 找不到的提示
                if (line.toLowerCase().contains("image name")) {
                    return true;
                }
            }
        }
        return false;
    }

    public void stopMonitoring() {
        scheduler.shutdownNow();
    }

}
