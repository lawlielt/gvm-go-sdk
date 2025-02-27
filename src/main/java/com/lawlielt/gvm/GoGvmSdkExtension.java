package com.lawlielt.gvm;

import com.goide.sdk.GoSdk;
import com.intellij.openapi.projectRoots.*;
import com.intellij.openapi.projectRoots.impl.SdkConfigurationUtil;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.options.Configurable;
import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.io.File;
import com.intellij.notification.NotificationGroupManager;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.goide.sdk.GoSdkService;
import com.goide.project.GoModuleSettings;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.WriteAction;

public class GoGvmSdkExtension implements Configurable {

    private JPanel panel;
    private JButton button;

    @Override
    public JComponent createComponent() {
        panel = new JPanel(new GridBagLayout());
        button = new JButton("Use Current GVM Go SDK");
        button.addActionListener(e -> addCurrentGvmGoSdk());
        panel.add(button);
        return panel;
    }

    @Override
    public boolean isModified() {
        return false;
    }

    @Override
    public void apply() {
        addCurrentGvmGoSdk();
//        addGvmGoSdk();
    }

    @Override
    public void reset() {
        // Reset logic can be added here if needed
    }

    @Override
    public String getDisplayName() {
        return "GVM Go SDK Extension";
    }

    private void addCurrentGvmGoSdk() {
//        List<GoSdk> gvmSdks = GoGvmSdkProvider.getGvmGoRoots();
        GoSdk currentSdk = GoGvmSdkProvider.getCurrentGvmGoRoot();

        if (currentSdk != null) {
            // 获取所有打开的项目
            Project[] projects = ProjectManager.getInstance().getOpenProjects();
            for (Project project : projects) {
                GoSdkService sdkService = GoSdkService.getInstance(project);
                sdkService.setSdk(currentSdk, true);

                // Trigger project settings refresh
                // Notify about the change
                NotificationGroupManager.getInstance()
                        .getNotificationGroup("GVM.GoSDK")
                        .createNotification("Go SDK Updated", "Set current GVM Go SDK: " + currentSdk.getHomePath(), NotificationType.INFORMATION)
                        .notify(project);

                Messages.showInfoMessage("Set current GVM Go SDK: " + currentSdk.getHomePath(), "Success");
            }

        } else {
            Messages.showErrorDialog("No GVM Go versions found", "Error");
        }
    }

    private void removeOtherGvmGoSdks() {
        // Get all SDKs in the project
        ProjectJdkTable sdkManager = ProjectJdkTable.getInstance();
        List<Sdk> allSdks = List.of(sdkManager.getAllJdks());

        for (Sdk sdk : allSdks) {
            // Only remove Go SDKs that are different from the current one
                sdkManager.removeJdk(sdk);
        }
    }
}
