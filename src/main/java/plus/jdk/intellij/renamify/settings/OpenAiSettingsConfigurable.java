package plus.jdk.intellij.renamify.settings;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.options.Configurable;
import com.intellij.ui.components.JBScrollPane;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class OpenAiSettingsConfigurable implements Configurable {
    @Override
    public JComponent createComponent() {
        return getComponent();
    }

    private JPanel mainPanel;
    private JTextField baseUrlField;
    private JTextField modelNameField;
    private JTextField apiKeyField;
    private JTextField temperatureField;
    private JTextField maxTokensField;
    private JTextArea promptField;
    private JTable fileSuffixBlackListTable;
    private DefaultTableModel tableModel;

    @Nls(capitalization = Nls.Capitalization.Title)
    @Override
    public String getDisplayName() {
        return "Renamify Settings";
    }

    public JComponent getComponent() {
        if (mainPanel == null) {
            // 创建主面板
            mainPanel = new JPanel(new BorderLayout());
            mainPanel.setBorder(new EmptyBorder(24, 24, 24, 24));
            
            // 创建内容面板
            JPanel contentPanel = new JPanel(new GridBagLayout());
            contentPanel.setBorder(new EmptyBorder(0, 0, 0, 0));
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
            gbc.gridx = 0;
            gbc.gridy = row;
            gbc.weightx = 0.3;
            contentPanel.add(createLabel("Base URL:", labelFont), gbc);
            gbc.gridx = 1;
            gbc.weightx = 0.7;
            contentPanel.add(baseUrlField, gbc);

            row++;
            gbc.gridx = 0;
            gbc.gridy = row;
            gbc.weightx = 0.3;
            contentPanel.add(createLabel("模型名称:", labelFont), gbc);
            gbc.gridx = 1;
            gbc.weightx = 0.7;
            contentPanel.add(modelNameField, gbc);

            row++;
            gbc.gridx = 0;
            gbc.gridy = row;
            gbc.weightx = 0.3;
            contentPanel.add(createLabel("API Key:", labelFont), gbc);
            gbc.gridx = 1;
            gbc.weightx = 0.7;
            contentPanel.add(apiKeyField, gbc);

            row++;
            gbc.gridx = 0;
            gbc.gridy = row;
            gbc.weightx = 0.3;
            contentPanel.add(createLabel("Temperature:", labelFont), gbc);
            gbc.gridx = 1;
            gbc.weightx = 0.7;
            contentPanel.add(temperatureField, gbc);

            row++;
            gbc.gridx = 0;
            gbc.gridy = row;
            gbc.weightx = 0.3;
            contentPanel.add(createLabel("最大 token 数量:", labelFont), gbc);
            gbc.gridx = 1;
            gbc.weightx = 0.7;
            contentPanel.add(maxTokensField, gbc);
            
            row++;
            gbc.gridx = 0;
            gbc.gridy = row;
            gbc.weightx = 0.3;
            gbc.weighty = 0;
            gbc.anchor = GridBagConstraints.NORTHEAST;
            contentPanel.add(createLabel("提示词:", labelFont), gbc);

            gbc.gridx = 1;
            gbc.weightx = 0.7;
            gbc.weighty = 1.0;
            gbc.fill = GridBagConstraints.BOTH;
            gbc.anchor = GridBagConstraints.CENTER;

            JScrollPane promptScrollPane = new JBScrollPane(promptField,
                    JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                    JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
            promptScrollPane.setPreferredSize(new Dimension(0, 120));
            promptScrollPane.setBorder(promptField.getBorder());
            promptField.setBorder(null);
            contentPanel.add(promptScrollPane, gbc);

            // 添加文件后缀黑名单部分
            row++;
            gbc.gridy = row;
            gbc.gridx = 0;
            gbc.weightx = 0.3;
            gbc.weighty = 0;
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.anchor = GridBagConstraints.NORTHEAST;
            contentPanel.add(createLabel("文件后缀黑名单:", labelFont), gbc);

            gbc.gridx = 1;
            gbc.weightx = 0.7;
            gbc.weighty = 0.5;
            gbc.fill = GridBagConstraints.BOTH;
            gbc.anchor = GridBagConstraints.CENTER;
            
            // 创建表格
            tableModel = new DefaultTableModel(new Object[]{"文件后缀"}, 0);
            fileSuffixBlackListTable = new JTable(tableModel);
            fileSuffixBlackListTable.setFont(fieldFont);
            fileSuffixBlackListTable.getTableHeader().setFont(labelFont);
            fileSuffixBlackListTable.setRowHeight(20); // 减小行高
            fileSuffixBlackListTable.setBackground(new Color(40, 41, 44));
            fileSuffixBlackListTable.setForeground(new Color(230, 230, 230));
            fileSuffixBlackListTable.setSelectionBackground(new Color(60, 61, 64));
            fileSuffixBlackListTable.setSelectionForeground(new Color(255, 255, 255));
            fileSuffixBlackListTable.setGridColor(new Color(100, 100, 100));
            fileSuffixBlackListTable.setIntercellSpacing(new Dimension(0, 0)); // 移除单元格间距
            
            JScrollPane tableScrollPane = new JScrollPane(fileSuffixBlackListTable);
            tableScrollPane.setPreferredSize(new Dimension(0, 100)); // 减小高度
            tableScrollPane.setBorder(BorderFactory.createCompoundBorder(
                    new LineBorder(new Color(100, 100, 100), 1, true),
                    new EmptyBorder(8, 12, 8, 12)
            ));
            
            JPanel tablePanel = new JPanel(new BorderLayout());
            tablePanel.add(tableScrollPane, BorderLayout.CENTER);
            
            // 添加按钮面板
            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            JButton addButton = new JButton("添加");
            JButton removeButton = new JButton("删除");
            addButton.setFont(fieldFont);
            removeButton.setFont(fieldFont);
            buttonPanel.add(addButton);
            buttonPanel.add(removeButton);
            tablePanel.add(buttonPanel, BorderLayout.SOUTH);
            
            contentPanel.add(tablePanel, gbc);
            
            // 添加事件监听器
            addButton.addActionListener(e -> {
                String suffix = JOptionPane.showInputDialog(mainPanel, "请输入要添加的文件后缀 (例如: .log)", "");
                if (suffix != null && !suffix.trim().isEmpty()) {
                    tableModel.addRow(new Object[]{suffix.trim()});
                }
            });
            
            removeButton.addActionListener(e -> {
                int selectedRow = fileSuffixBlackListTable.getSelectedRow();
                if (selectedRow >= 0) {
                    tableModel.removeRow(selectedRow);
                } else {
                    JOptionPane.showMessageDialog(mainPanel, "请选择要删除的行", "提示", JOptionPane.WARNING_MESSAGE);
                }
            });

            // 双击编辑功能
            fileSuffixBlackListTable.addMouseListener(new java.awt.event.MouseAdapter() {
                @Override
                public void mouseClicked(java.awt.event.MouseEvent evt) {
                    if (evt.getClickCount() == 2) {
                        int row = fileSuffixBlackListTable.rowAtPoint(evt.getPoint());
                        if (row >= 0) {
                            String currentValue = (String) tableModel.getValueAt(row, 0);
                            String newValue = JOptionPane.showInputDialog(mainPanel, "编辑文件后缀:", currentValue);
                            if (newValue != null && !newValue.trim().isEmpty()) {
                                tableModel.setValueAt(newValue.trim(), row, 0);
                            }
                        }
                    }
                }
            });

            // 使用 JScrollPane 包装内容面板以支持滚动
            JScrollPane scrollPane = new JScrollPane(contentPanel);
            scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
            scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
            scrollPane.setBorder(null);
            mainPanel.add(scrollPane, BorderLayout.CENTER);

            loadSettings();
        }
        return mainPanel;
    }

    private JLabel createLabel(String text, Font font) {
        JLabel label = new JLabel(text);
        label.setFont(font);
        label.setForeground(new Color(220, 220, 220));
        label.setHorizontalAlignment(SwingConstants.RIGHT);
        return label;
    }

    private JTextField createStyledField(Font font) {
        JTextField field = new JTextField();
        field.setFont(font);
        field.setBackground(new Color(40, 41, 44));
        field.setForeground(new Color(230, 230, 230));
        field.setCaretColor(new Color(255, 255, 255));
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
        area.setCaretColor(new Color(255, 255, 255));
        area.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(100, 100, 100), 1, true),
                new EmptyBorder(8, 12, 8, 12)
        ));
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setRows(10);
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
        
        // 加载文件后缀黑名单
        tableModel.setRowCount(0); // 清空现有数据
        List<String> blackList = state.getFileSuffixBlackList();
        if (blackList != null) {
            for (String suffix : blackList) {
                tableModel.addRow(new Object[]{suffix});
            }
        }
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
        
        // 保存文件后缀黑名单
        state.getFileSuffixBlackList().clear();
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            String suffix = (String) tableModel.getValueAt(i, 0);
            if (suffix != null && !suffix.trim().isEmpty()) {
                state.getFileSuffixBlackList().add(suffix.trim());
            }
        }
        
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