package org.dromara.redisfront.ui.component;

import cn.hutool.core.io.resource.ResourceUtil;

import javax.swing.*;
import java.awt.*;

/**
 * LoadingPanel
 *
 * @author Jin
 */
public class LoadingPanel extends JPanel {
    private static final LoadingPanel instance = new LoadingPanel();

    public static LoadingPanel getSingleton() {
        return instance;
    }

    private LoadingPanel() {
        setLayout(new BorderLayout());
        var iconImage = new ImageIcon(ResourceUtil.getResource("gif/21.gif"));
        var iconLabel = new JLabel(iconImage);
        add(iconLabel, BorderLayout.CENTER);
    }
}
