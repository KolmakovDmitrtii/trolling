import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class MainFrame extends JFrame {
    private NetworkSystem network;
    private DefaultListModel<LogItem> feedModel;
    private JList<LogItem> feedList;
    private DefaultListModel<String> chatListModel;
    private JList<String> chatList;

    private JButton startButton;
    private JButton statsButton;
    private JButton resetButton;
    private JLabel chatTitleLabel;
    private JLabel chatStatusLabel;

    private Timer simulationTimer;
    private int ticksPassed = 0;
    private ArrayList<LogItem> allEventsLog = new ArrayList<>();

    private static final Color COLOR_MAIN_BG = new Color(18, 22, 31);
    private static final Color COLOR_PANEL_BG = new Color(26, 34, 45);
    private static final Color COLOR_CARD_BG = new Color(34, 43, 54);
    private static final Color COLOR_WHITE = new Color(255, 255, 255);
    private static final Color COLOR_TEXT_MAIN = new Color(230, 235, 245);
    private static final Color COLOR_TEXT_MUTED = new Color(115, 135, 155);
    private static final Color COLOR_ACCENT_BLUE = new Color(48, 144, 240);
    private static final Color COLOR_BORDER = new Color(42, 54, 70);
    private static final Color COLOR_SELECTION = new Color(38, 60, 85);
    private static final Color COLOR_ALERT_RED = new Color(235, 84, 84);
    private static final Color COLOR_ALERT_BG = new Color(46, 28, 32);

    public MainFrame() {
        this.network = new NetworkSystem();
        this.network.init();

        Font fontNormal = new Font("Segoe UI", Font.PLAIN, 14);
        Font fontBold = new Font("Segoe UI", Font.BOLD, 14);
        Font fontHeader = new Font("Segoe UI", Font.BOLD, 16);

        setTitle("Социальная Сеть - Имитационное Моделирование");
        setSize(1150, 720);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBackground(COLOR_MAIN_BG);
        mainPanel.setBorder(new EmptyBorder(15, 15, 15, 15));

        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setBackground(COLOR_PANEL_BG);
        leftPanel.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(COLOR_BORDER, 1),
                new EmptyBorder(10, 10, 10, 10)
        ));
        leftPanel.setPreferredSize(new Dimension(340, 0));

        JLabel leftTitle = new JLabel("Пользователи сети");
        leftTitle.setFont(fontBold);
        leftTitle.setForeground(COLOR_WHITE);
        leftTitle.setBorder(new EmptyBorder(0, 5, 10, 0));
        leftPanel.add(leftTitle, BorderLayout.NORTH);

        chatListModel = new DefaultListModel<>();
        updateChatList();

        chatList = new JList<>(chatListModel);
        chatList.setFont(fontNormal);
        chatList.setBackground(COLOR_PANEL_BG);
        chatList.setForeground(COLOR_TEXT_MAIN);
        chatList.setSelectionBackground(COLOR_SELECTION);
        chatList.setSelectionForeground(COLOR_WHITE);
        chatList.setFixedCellHeight(40);
        chatList.setSelectedIndex(0);

        chatList.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    refreshChatWindow();
                }
            }
        });

        JScrollPane listScroll = new JScrollPane(chatList);
        listScroll.setBorder(null);
        listScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        leftPanel.add(listScroll, BorderLayout.CENTER);
        mainPanel.add(leftPanel, BorderLayout.WEST);

        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBackground(COLOR_PANEL_BG);
        centerPanel.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(COLOR_BORDER, 1),
                new EmptyBorder(15, 15, 15, 15)
        ));

        JPanel chatHeader = new JPanel(new GridLayout(2, 1, 0, 4));
        chatHeader.setBackground(COLOR_PANEL_BG);
        chatHeader.setBorder(new EmptyBorder(0, 0, 15, 0));

        chatTitleLabel = new JLabel("Глобальная Лента Сети [Общий чат]");
        chatTitleLabel.setFont(fontHeader);
        chatTitleLabel.setForeground(COLOR_WHITE);

        chatStatusLabel = new JLabel("Ожидание запуска сессии");
        chatStatusLabel.setFont(fontNormal);
        chatStatusLabel.setForeground(COLOR_TEXT_MUTED);

        chatHeader.add(chatTitleLabel);
        chatHeader.add(chatStatusLabel);
        centerPanel.add(chatHeader, BorderLayout.NORTH);

        feedModel = new DefaultListModel<>();
        feedList = new JList<>(feedModel);
        feedList.setBackground(COLOR_MAIN_BG);

        feedList.setCellRenderer(new FeedCellRenderer());

        feedList.setSelectionModel(new DefaultListSelectionModel() {
            @Override
            public void setSelectionInterval(int index0, int index1) {
            }
        });

        JScrollPane chatScroll = new JScrollPane(feedList);
        chatScroll.setBorder(new LineBorder(COLOR_BORDER, 1));

        chatScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        chatScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        centerPanel.add(chatScroll, BorderLayout.CENTER);
        mainPanel.add(centerPanel, BorderLayout.CENTER);

        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
        rightPanel.setBackground(COLOR_PANEL_BG);
        rightPanel.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(COLOR_BORDER, 1),
                new EmptyBorder(15, 15, 15, 15)
        ));
        rightPanel.setPreferredSize(new Dimension(240, 0));

        JLabel rightTitle = new JLabel("Действия");
        rightTitle.setFont(fontBold);
        rightTitle.setForeground(COLOR_TEXT_MUTED);
        rightTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        rightPanel.add(rightTitle);
        rightPanel.add(Box.createVerticalStrut(20));

        startButton = createActionButton("Запустить", fontBold, COLOR_ACCENT_BLUE, COLOR_WHITE);
        statsButton = createActionButton("Аналитика", fontBold, COLOR_CARD_BG, COLOR_TEXT_MAIN);
        resetButton = createActionButton("Сбросить", fontBold, COLOR_CARD_BG, COLOR_TEXT_MAIN);

        statsButton.setEnabled(false);

        rightPanel.add(startButton);
        rightPanel.add(Box.createVerticalStrut(12));
        rightPanel.add(statsButton);
        rightPanel.add(Box.createVerticalStrut(12));
        rightPanel.add(resetButton);
        mainPanel.add(rightPanel, BorderLayout.EAST);

        add(mainPanel);

        simulationTimer = new Timer(400, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (ticksPassed >= 50) {
                    simulationTimer.stop();
                    chatStatusLabel.setText("Моделирование завершено");
                    statsButton.setEnabled(true);
                    updateChatList();
                    return;
                }

                String rawEvent = network.update();
                LogItem item = parseRawEvent(rawEvent);

                if (item != null) {
                    allEventsLog.add(item);
                    refreshChatWindow();
                }

                ticksPassed++;
            }
        });

        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                allEventsLog.clear();
                feedModel.clear();
                allEventsLog.add(new LogItem("СИСТЕМА", "Инициализация сети успешно выполнена.", "SYSTEM"));
                ticksPassed = 0;
                chatStatusLabel.setText("Идет симуляция сетевой активности...");
                startButton.setEnabled(false);
                simulationTimer.start();
            }
        });

        statsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showStatisticsDialog();
            }
        });

        resetButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                simulationTimer.stop();
                network.reset();
                allEventsLog.clear();
                feedModel.clear();
                chatStatusLabel.setText("Система сброшена");
                startButton.setEnabled(true);
                statsButton.setEnabled(false);
                ticksPassed = 0;
                chatList.setSelectedIndex(0);
                updateChatList();
            }
        });
    }

    private JButton createActionButton(String text, Font font, Color bg, Color fg) {
        JButton button = new JButton(text);
        button.setFont(font);
        button.setBackground(bg);
        button.setForeground(fg);
        button.setMaximumSize(new Dimension(200, 40));
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setFocusPainted(false);
        button.setBorder(new LineBorder(COLOR_BORDER, 1));
        return button;
    }

    private LogItem parseRawEvent(String rawEvent) {
        if (rawEvent.contains("проигнорировал") || rawEvent.contains("тихо прочитал") || rawEvent.contains("затишье")) {
            return null;
        }

        rawEvent = rawEvent.replaceAll("[^\\p{L}\\p{N}\\p{P}\\s=><|-]", "");

        if (rawEvent.contains("ПОСТ:")) {
            String cleanContent = rawEvent.replace("ПОСТ:", "").trim();
            return new LogItem("Новая публикация на стене", cleanContent, "POST");
        }
        if (rawEvent.contains("оставил ехидный комментарий")) {
            return new LogItem("Тролль", rawEvent.trim(), "TROLL");
        }
        if (rawEvent.contains("поддакнул")) {
            return new LogItem("Подпевала", rawEvent.trim(), "SYCOPHANT");
        }
        if (rawEvent.contains("заступился")) {
            return new LogItem("Защитник", rawEvent.trim(), "DEFENDER");
        }
        if (rawEvent.contains("расстроился") || rawEvent.contains("увидел токсичность")) {
            return new LogItem("Системный статус", rawEvent.trim(), "STATUS");
        }

        return new LogItem("Уведомление", rawEvent.trim(), "INFO");
    }

    private void refreshChatWindow() {
        int selectedIndex = chatList.getSelectedIndex();
        if (selectedIndex < 0) return;

        feedModel.clear();

        if (selectedIndex == 0) {
            chatTitleLabel.setText("Глобальная Лента Сети [Общий чат]");
            for (LogItem item : allEventsLog) {
                feedModel.addElement(item);
            }
        } else {
            String selectedItemText = chatList.getSelectedValue();
            String userName = selectedItemText.trim().split(" ")[0];

            chatTitleLabel.setText("Лента активности объекта: " + userName);

            for (LogItem item : allEventsLog) {
                if (item.content.contains(userName) || item.title.contains(userName)) {
                    feedModel.addElement(item);
                }
            }
        }

        if (feedModel.size() > 0) {
            feedList.ensureIndexIsVisible(feedModel.size() - 1);
        }
    }

    private void updateChatList() {
        int currentSelection = chatList != null ? chatList.getSelectedIndex() : 0;
        if (currentSelection < 0) currentSelection = 0;

        chatListModel.clear();
        chatListModel.addElement("  Глобальная Лента [Все посты]");

        ArrayList<User> users = network.getUsers();
        for (User u : users) {
            String status = u.isOnline() ? "[В СЕТИ]" : "[ВНЕ СЕТИ]";
            chatListModel.addElement("  " + u.getName() + " (" + u.getType() + ") " + status);
        }

        if (chatList != null && chatListModel.size() > currentSelection) {
            chatList.setSelectedIndex(currentSelection);
        }
    }

    private void showStatisticsDialog() {
        JDialog dialog = new JDialog(this, "Аналитический отчет", true);
        dialog.setSize(850, 550);
        dialog.setLocationRelativeTo(this);
        dialog.getContentPane().setBackground(COLOR_MAIN_BG);

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        ArrayList<User> users = network.getUsers();
        ArrayList<Post> posts = network.getPosts();

        JPanel structurePanel = new JPanel(new BorderLayout(10, 10));
        structurePanel.setBackground(COLOR_PANEL_BG);
        structurePanel.setBorder(new EmptyBorder(15, 15, 15, 15));

        String[] structColumns = {"Пользователь", "Роль в сети", "Статус активности", "Текущее настроение", "Связи (Друзья)"};
        Object[][] structData = new Object[users.size()][5];
        for (int i = 0; i < users.size(); i++) {
            User u = users.get(i);
            structData[i][0] = "  " + u.getName();
            structData[i][1] = u.getType();
            structData[i][2] = u.isOnline() ? "Активен в сети" : "Покинул сеть";
            structData[i][3] = u.getMood() + "%";

            if (u.getFriends().isEmpty()) {
                structData[i][4] = "Нет прямых связей";
            } else {
                StringBuilder sb = new StringBuilder();
                for (User f : u.getFriends()) {
                    sb.append(f.getName()).append(" ");
                }
                structData[i][4] = sb.toString().trim();
            }
        }

        JTable structTable = new JTable(structData, structColumns) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        structTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        structTable.setRowHeight(30);
        structTable.setBackground(COLOR_CARD_BG);
        structTable.setForeground(COLOR_TEXT_MAIN);
        structTable.setGridColor(COLOR_BORDER);
        structTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        structTable.getTableHeader().setBackground(COLOR_MAIN_BG);
        structTable.getTableHeader().setForeground(COLOR_WHITE);
        structTable.getTableHeader().setReorderingAllowed(false);

        JScrollPane scrollStruct = new JScrollPane(structTable);
        scrollStruct.setBorder(new LineBorder(COLOR_BORDER, 1));
        scrollStruct.getViewport().setBackground(COLOR_PANEL_BG);
        structurePanel.add(scrollStruct, BorderLayout.CENTER);
        tabbedPane.addTab("Структура сети", structurePanel);

        JPanel contentPanel = new JPanel(new GridLayout(2, 1, 15, 15));
        contentPanel.setBackground(COLOR_PANEL_BG);
        contentPanel.setBorder(new EmptyBorder(15, 15, 15, 15));

        JPanel postsStatsPanel = new JPanel(new GridLayout(4, 1, 5, 5));
        postsStatsPanel.setBackground(COLOR_PANEL_BG);
        postsStatsPanel.setBorder(BorderFactory.createTitledBorder(
                new LineBorder(COLOR_BORDER, 1), "Распределение публикаций по тональности",
                0, 0, null, COLOR_TEXT_MAIN));

        int fun = 0, sad = 0, neu = 0, totalComments = 0;
        for (Post p : posts) {
            if (p.getType().equals("Веселый")) fun++;
            else if (p.getType().equals("Грустный")) sad++;
            else if (p.getType().equals("Нейтральный")) neu++;
            totalComments += p.getComments().size();
        }
        int totalPosts = posts.size() == 0 ? 1 : posts.size();

        postsStatsPanel.add(createProgressRow("Позитивные посты (" + fun + " шт.)", fun, totalPosts, COLOR_ACCENT_BLUE));
        postsStatsPanel.add(createProgressRow("Меланхоличные посты (" + sad + " шт.)", sad, totalPosts, COLOR_TEXT_MUTED));
        postsStatsPanel.add(createProgressRow("Нейтральные посты (" + neu + " шт.)", neu, totalPosts, COLOR_BORDER));
        contentPanel.add(postsStatsPanel);

        JPanel generalContentPanel = new JPanel(new GridLayout(2, 1, 10, 10));
        generalContentPanel.setBackground(COLOR_PANEL_BG);

        JLabel lblTotalPosts = new JLabel("Всего записей сгенерировано на стенах: " + posts.size() + " ед.");
        lblTotalPosts.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblTotalPosts.setForeground(COLOR_TEXT_MAIN);
        JLabel lblTotalComments = new JLabel("Всего комментариев оставлено пользователями: " + totalComments + " ед.");
        lblTotalComments.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblTotalComments.setForeground(COLOR_TEXT_MAIN);

        generalContentPanel.add(lblTotalPosts);
        generalContentPanel.add(lblTotalComments);
        contentPanel.add(generalContentPanel);
        tabbedPane.addTab("Анализ контента", contentPanel);

        JPanel stabilityPanel = new JPanel(new BorderLayout(10, 10));
        stabilityPanel.setBackground(COLOR_PANEL_BG);
        stabilityPanel.setBorder(new EmptyBorder(15, 15, 15, 15));

        JPanel listStabilityPanel = new JPanel();
        listStabilityPanel.setLayout(new BoxLayout(listStabilityPanel, BoxLayout.Y_AXIS));
        listStabilityPanel.setBackground(COLOR_PANEL_BG);

        int totalBroken = 0;
        for (User u : users) {
            JPanel userRow = new JPanel(new BorderLayout(10, 5));
            userRow.setBackground(COLOR_PANEL_BG);
            userRow.setMaximumSize(new Dimension(800, 35));
            userRow.setBorder(new EmptyBorder(4, 0, 4, 0));

            JLabel nameLabel = new JLabel("  " + u.getName() + " (" + u.getType() + ")");
            nameLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            nameLabel.setForeground(COLOR_TEXT_MAIN);
            nameLabel.setPreferredSize(new Dimension(250, 25));

            JProgressBar moodBar = new JProgressBar(0, 100);
            moodBar.setValue(u.getMood());
            moodBar.setStringPainted(true);
            moodBar.setFont(new Font("Segoe UI", Font.BOLD, 11));
            moodBar.setPreferredSize(new Dimension(300, 20));
            moodBar.setBackground(COLOR_MAIN_BG);

            JLabel statusLabel = new JLabel();
            statusLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
            statusLabel.setPreferredSize(new Dimension(150, 25));

            if (!u.isOnline()) {
                moodBar.setForeground(COLOR_ALERT_RED);
                statusLabel.setText("Вышел из сети");
                statusLabel.setForeground(COLOR_ALERT_RED);
                totalBroken++;
            } else {
                moodBar.setForeground(COLOR_ACCENT_BLUE);
                statusLabel.setText("Стабилен");
                statusLabel.setForeground(COLOR_WHITE);
            }

            userRow.add(nameLabel, BorderLayout.WEST);
            userRow.add(moodBar, BorderLayout.CENTER);
            userRow.add(statusLabel, BorderLayout.EAST);
            listStabilityPanel.add(userRow);
        }

        JScrollPane scrollStability = new JScrollPane(listStabilityPanel);
        scrollStability.setBorder(new LineBorder(COLOR_BORDER, 1));
        scrollStability.getViewport().setBackground(COLOR_PANEL_BG);
        stabilityPanel.add(scrollStability, BorderLayout.CENTER);

        JPanel summaryStabilityPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        summaryStabilityPanel.setBackground(COLOR_PANEL_BG);
        JLabel summaryLabel = new JLabel("Итог сессии: из-за троллинга платформу покинуло человек: " + totalBroken);
        summaryLabel.setFont(new Font("Segoe UI", Font.BOLD, 15));
        summaryLabel.setForeground(totalBroken > 0 ? COLOR_ALERT_RED : COLOR_WHITE);
        summaryStabilityPanel.add(summaryLabel);
        stabilityPanel.add(summaryStabilityPanel, BorderLayout.SOUTH);

        tabbedPane.addTab("Итоги симуляции", stabilityPanel);

        dialog.add(tabbedPane);
        dialog.setVisible(true);
    }

    private JPanel createProgressRow(String labelText, int value, int max, Color barColor) {
        JPanel row = new JPanel(new BorderLayout(10, 5));
        row.setBackground(COLOR_PANEL_BG);

        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        label.setForeground(COLOR_TEXT_MAIN);
        label.setPreferredSize(new Dimension(220, 20));

        JProgressBar bar = new JProgressBar(0, max);
        bar.setValue(value);
        bar.setForeground(barColor);
        bar.setBackground(COLOR_MAIN_BG);
        bar.setBorderPainted(false);

        row.add(label, BorderLayout.WEST);
        row.add(bar, BorderLayout.CENTER);
        return row;
    }

    private static class LogItem {
        String title;
        String content;
        String type;

        LogItem(String title, String content, String type) {
            this.title = title;
            this.content = content;
            this.type = type;
        }
    }

    private static class FeedCellRenderer extends JPanel implements ListCellRenderer<LogItem> {
        private JLabel titleLabel = new JLabel();
        private JTextArea contentArea = new JTextArea();

        FeedCellRenderer() {
            setLayout(new BorderLayout(5, 5));
            titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
            contentArea.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            contentArea.setLineWrap(true);
            contentArea.setWrapStyleWord(true);
            contentArea.setOpaque(false);
            contentArea.setEditable(false);
            contentArea.setAutoscrolls(false);

            add(titleLabel, BorderLayout.NORTH);
            add(contentArea, BorderLayout.CENTER);
        }

        @Override
        public Component getListCellRendererComponent(JList<? extends LogItem> list, LogItem value, int index, boolean isSelected, boolean cellHasFocus) {
            titleLabel.setText(value.title.toUpperCase());
            contentArea.setText(value.content);

            int listWidth = list.getWidth();
            if (listWidth > 0) {
                int padding = 30;
                if (value.type.equals("SYCOPHANT")) padding = 80;
                else if (value.type.equals("TROLL") || value.type.equals("DEFENDER")) padding = 60;

                contentArea.setSize(new Dimension(listWidth - padding, 1));
            }

            if (value.type.equals("POST")) {
                setBackground(COLOR_CARD_BG);
                titleLabel.setForeground(COLOR_ACCENT_BLUE);
                contentArea.setForeground(COLOR_TEXT_MAIN);
                setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createMatteBorder(1, 1, 1, 1, new Color(48, 60, 78)),
                        BorderFactory.createEmptyBorder(12, 15, 12, 15)
                ));
            } else if (value.type.equals("STATUS")) {
                setBackground(COLOR_ALERT_BG);
                titleLabel.setForeground(COLOR_ALERT_RED);
                contentArea.setForeground(new Color(240, 210, 210));
                setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createMatteBorder(0, 4, 0, 0, COLOR_ALERT_RED),
                        BorderFactory.createEmptyBorder(8, 15, 8, 15)
                ));
            } else if (value.type.equals("SYSTEM")) {
                setBackground(new Color(22, 32, 45));
                titleLabel.setForeground(COLOR_WHITE);
                contentArea.setForeground(COLOR_TEXT_MUTED);
                setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createMatteBorder(1, 1, 1, 1, new Color(38, 50, 68)),
                        BorderFactory.createEmptyBorder(10, 15, 10, 15)
                ));
            } else {
                setBackground(new Color(22, 29, 38));
                titleLabel.setForeground(COLOR_TEXT_MUTED);
                contentArea.setForeground(new Color(190, 200, 215));

                int leftPadding = 45;
                if (value.type.equals("SYCOPHANT")) {
                    leftPadding = 65;
                }

                setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createMatteBorder(0, 2, 0, 0, new Color(42, 54, 70)),
                        BorderFactory.createEmptyBorder(8, leftPadding, 8, 15)
                ));
            }

            return this;
        }
    }
}