package plus.jdk.intellij.renamify.settings;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.options.Configurable;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;

public class OpenAiSettingsConfigurable implements Configurable {
    @Override
    public JComponent createComponent() {
        return getComponent();
    }

    private JPanel panel;
    private JTextField baseUrlField;
    private JTextField modelNameField;
    private JTextField apiKeyField;
    private JTextField temperatureField;
    private JTextField maxTokensField;
    private JTextArea promptField;

    @Nls(capitalization = Nls.Capitalization.Title)
    @Override
    public String getDisplayName() {
        return "Renamify Settings";
    }

    public JComponent getComponent() {
        if (panel == null) {
            panel = new JPanel(new GridBagLayout());
            panel.setBorder(new EmptyBorder(24, 24, 24, 24));
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(8, 8, 8, 8);
            gbc.fill = GridBagConstraints.HORIZONTAL;

            Font labelFont = new Font("Segoe UI", Font.BOLD, 15);
            Font fieldFont = new Font("Segoe UI", Font.PLAIN, 15);

            baseUrlField = createStyledField(fieldFont);
            modelNameField = createStyledField(fieldFont);
            apiKeyField = createStyledField(fieldFont);
            temperatureField = createStyledField(fieldFont);
            maxTokensField = createStyledField(fieldFont);
            promptField = createStyledArea(fieldFont);

            int row = 0;
            gbc.gridx = 0; gbc.gridy = row;
            gbc.weightx = 0.3;
            panel.add(createLabel("Base URL:", labelFont), gbc);
            gbc.gridx = 1;
            gbc.weightx = 0.7;
            panel.add(baseUrlField, gbc);

            row++;
            gbc.gridx = 0; gbc.gridy = row;
            gbc.weightx = 0.3;
            panel.add(createLabel("模型名称:", labelFont), gbc);
            gbc.gridx = 1;
            gbc.weightx = 0.7;
            panel.add(modelNameField, gbc);

            row++;
            gbc.gridx = 0; gbc.gridy = row;
            gbc.weightx = 0.3;
            panel.add(createLabel("API Key:", labelFont), gbc);
            gbc.gridx = 1;
            gbc.weightx = 0.7;
//            panel.add(apiKeyField, gbc);

            row++;
            gbc.gridx = 0; gbc.gridy = row;
            gbc.weightx = 0.3;
            panel.add(createLabel("Temperature:", labelFont), gbc);
            gbc.gridx = 1;
            gbc.weightx = 0.7;
//            panel.add(temperatureField, gbc);

            row++;
            gbc.gridx = 0; gbc.gridy = row;
            gbc.weightx = 0.3;
            panel.add(createLabel("最大 token 数量:", labelFont), gbc);
            gbc.gridx = 1;
            gbc.weightx = 0.7;
//            panel.add(maxTokensField, gbc);

            row++;
            gbc.gridx = 0; gbc.gridy = row;
            gbc.weightx = 0.3;
            gbc.anchor = GridBagConstraints.NORTHEAST;
            panel.add(createLabel("提示词:", labelFont), gbc);
            gbc.gridx = 1;
            gbc.weightx = 0.7;
            gbc.anchor = GridBagConstraints.CENTER;
            // 改为无滚动条、紧凑边框的多行输入框
            promptField.setPreferredSize(new Dimension(0, 36));
            panel.add(promptField, gbc);

            loadSettings();
        }
        return panel;
    }

    private JLabel createLabel(String text, Font font) {
        JLabel label = new JLabel(text);
        label.setFont(font);
        label.setForeground(new Color(220,220,220));
        label.setHorizontalAlignment(SwingConstants.RIGHT);
        return label;
    }

    private JTextField createStyledField(Font font) {
        JTextField field = new JTextField();
        field.setFont(font);
        field.setBackground(new Color(40, 41, 44));
        field.setForeground(new Color(230, 230, 230));
        field.setCaretColor(new Color(255,255,255));
        field.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(100, 100, 100), 1, true),
                new EmptyBorder(8, 12, 8, 12)
        ));
        field.setPreferredSize(new Dimension(0, 36));
        return field;
    }

    private JTextArea createStyledArea(Font font) {
        JTextArea area = new JTextArea();
        area.setFont(font);
        area.setBackground(new Color(40, 41, 44));
        area.setForeground(new Color(230, 230, 230));
        area.setCaretColor(new Color(255,255,255));
        area.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(100, 100, 100), 1, true),
                new EmptyBorder(8, 12, 8, 12)
        ));
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setRows(2);
        area.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        return area;
    }

    private void loadSettings() {
        OpenAiSettings settings = OpenAiSettings.getInstance();
        if (settings == null || settings.getState() == null) {
            // 容错处理，避免空指针异常
            baseUrlField.setText("");
            modelNameField.setText("");
            apiKeyField.setText("");
            temperatureField.setText("0.7");
            maxTokensField.setText("2048");
            promptField.setText("");
            return;
        }
        OpenAiSettings.State state = settings.getState();
        baseUrlField.setText(state.getBaseUrl());
        modelNameField.setText(state.getModelName());
        apiKeyField.setText(state.getApiKey());
        temperatureField.setText(String.valueOf(state.getTemperature()));
        maxTokensField.setText(String.valueOf(state.getMaxTokens()));
        promptField.setText(state.getPrompt());
    }

    @Override
    public boolean isModified() {
        return true;
    }

    @Override
    public void apply() {
        OpenAiSettings settings = OpenAiSettings.getInstance();
        if (settings == null || settings.getState() == null) {
            return;
        }
        OpenAiSettings.State state = settings.getState();
        state.baseUrl = baseUrlField.getText();
        state.modelName = modelNameField.getText();
        state.apiKey = apiKeyField.getText();
        try {
            state.temperature = Double.parseDouble(temperatureField.getText());
        } catch (NumberFormatException e) {
            state.temperature = 0.7;
        }
        try {
            state.maxTokens = Integer.parseInt(maxTokensField.getText());
        } catch (NumberFormatException e) {
            state.maxTokens = 200;
        }
        state.setPrompt(promptField.getText());
        // 强制保存设置，确保输入配置落盘
        ApplicationManager.getApplication().saveSettings();
    }

    @Override
    public void reset() {
        loadSettings();
    }

    @Nullable
    @Override
    public String getHelpTopic() {
        return null;
    }
}