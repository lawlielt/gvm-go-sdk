package com.lawlielt.gvm;

import com.goide.sdk.GoSdk;
import com.intellij.openapi.projectRoots.*;
import com.intellij.openapi.projectRoots.impl.SdkConfigurationUtil;
import com.intellij.openapi.util.SystemInfo;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class GoGvmSdkProvider {
    public static List<GoSdk> getGvmGoRoots() {
        File gvmPath = getGvmPath();
        if (gvmPath == null) {
            return new ArrayList<>();
        }

        File gosDir = new File(gvmPath, "gos");
        File[] goVersions = gosDir.listFiles();
        if (goVersions == null) {
            return new ArrayList<>();
        }

        List<GoSdk> sdks = new ArrayList<>();
        for (File goDir : goVersions) {
            if (goDir.isDirectory()) {
                String sdkName = "Go " + goDir.getName();
                String homePath = goDir.getAbsolutePath();
                sdks.add(GoSdk.fromHomePath(homePath));
            }
        }
        return sdks;
    }

    private static File getGvmPath() {
        String home = System.getProperty("user.home");
        File gvmPath;
        if (SystemInfo.isWindows) {
            gvmPath = new File(home, "gvm");
        } else {
            gvmPath = new File(home, ".gvm");
        }
        return gvmPath.exists() ? gvmPath : null;
    }

    public static GoSdk getCurrentGvmGoRoot() {
        File gvmPath = getGvmPath();
        if (gvmPath == null) {
            return null;
        }

        File defaultEnvFile = new File(gvmPath, "environments/default");
        if (!defaultEnvFile.exists()) {
            return null;
        }

        String goVersion = getCurrentVersionFromEnvFile(defaultEnvFile);
        if (goVersion.isEmpty()) {
            return null;
        }

        File gosDir = new File(gvmPath, "gos");
        File currentVersionDir = new File(gosDir, goVersion);
        if (!currentVersionDir.exists()) {
            return null;
        }

        return GoSdk.fromHomePath(currentVersionDir.getAbsolutePath());
    }

    private static String getCurrentVersionFromEnvFile(File envFile) {
        try {
            List<String> lines = java.nio.file.Files.readAllLines(envFile.toPath());
            for (String line : lines) {
                if (line.contains("gvm_go_name")) {
                    // 提取 go1.23.0 这样的版本号
                    String[] parts = line.split("\"");
                    if (parts.length >= 2) {
                        return parts[1];  // 返回引号中的版本号
                    }
                }
            }
        } catch (Exception e) {
            return "";
        }
        return "";
    }
}
