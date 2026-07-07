package yuix.mm;

public final class RootSecurityScanner {
    private static final String[] DANGEROUS_PATTERNS = {
            "setenforce 0",
            "chmod 777",
            "mount -o rw,remount /system",
            "rm -rf /",
            "iptables -F",
            "LD_PRELOAD",
            "frida-server",
            "magiskhide",
            "zygisk"
    };

    private final RootCommandExecutor executor;

    public RootSecurityScanner(RootCommandExecutor executor) {
        this.executor = executor;
    }

    public RootScanReport scan() {
        StringBuilder detail = new StringBuilder();
        int risks = 0;
        RootCommandResult rootCheck = run("id", 5000L);
        if (!rootCheck.success || !rootCheck.output.contains("uid=0")) {
            return new RootScanReport(false, 0, "未获得 Root 权限，无法启用 Root 管理器功能。\n" + safeOutput(rootCheck));
        }

        detail.append("Root 已授权：").append(rootCheck.output).append('\n');
        RootCommandResult processScan = run("ps -A | grep -Ei 'frida|xposed|substrate|zygisk|magisk'", 7000L);
        if (processScan.success && !processScan.output.isEmpty()) {
            risks++;
            detail.append("检测到可疑进程：\n").append(processScan.output).append('\n');
        }

        RootCommandResult fileScan = run("find /data/local/tmp /sdcard/Download -maxdepth 2 -type f 2>/dev/null | grep -Ei 'frida|xposed|substrate|exploit|payload|inject'", 9000L);
        if (fileScan.success && !fileScan.output.isEmpty()) {
            risks++;
            detail.append("检测到可疑文件：\n").append(fileScan.output).append('\n');
        }

        for (String pattern : DANGEROUS_PATTERNS) {
            RootCommandResult historyScan = run("grep -R -i \"" + escape(pattern) + "\" /data/local/tmp /sdcard/Download 2>/dev/null | head -20", 9000L);
            if (historyScan.success && !historyScan.output.isEmpty()) {
                risks++;
                detail.append("检测到危险特征 ").append(pattern).append("：\n").append(historyScan.output).append('\n');
            }
        }

        if (risks == 0) {
            detail.append("未发现已知危险进程、文件或命令特征。已保持 Root 监测入口启用。");
        } else {
            detail.append("已截止后续自动操作，请先处理以上风险项。");
        }
        return new RootScanReport(true, risks, detail.toString().trim());
    }

    private RootCommandResult run(String command, long timeoutMillis) {
        try {
            return executor.execute(command, timeoutMillis);
        } catch (Exception exception) {
            return new RootCommandResult(false, -1, exception.getMessage());
        }
    }

    private String safeOutput(RootCommandResult result) {
        if (result.output.isEmpty()) {
            return "Root 请求被拒绝或设备未安装 su。";
        }
        return result.output;
    }

    private String escape(String value) {
        return value.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
