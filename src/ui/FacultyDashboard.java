package ui;

import dao.AnnouncementDAO;
import dao.QueryDAO;
import model.Announcement;
import model.Faculty;
import model.Query;
import util.PortalException;
import util.UIStyle;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.SwingWorker;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FacultyDashboard extends JFrame implements java.awt.event.ActionListener {
    private final Faculty faculty;
    private final QueryDAO queryDAO;
    private final AnnouncementDAO announcementDAO;

    private final CardLayout cardLayout;
    private final JPanel contentPanel;

    private final JTable queryTable;
    private final DefaultTableModel queryTableModel;
    private final JComboBox<String> filterSubjectComboBox;
    private final JComboBox<String> filterStatusComboBox;
    private final JTextArea selectedQueryArea;
    private final JTextArea replyArea;
    private final JCheckBox answeredCheckBox;
    private final JButton refreshQueriesButton;
    private final JButton replyButton;
    private final JButton resolveButton;
    private final JButton openFileButton;

    private final JTextArea announcementArea;
    private final JTextArea announcementHistoryArea;
    private final JButton postAnnouncementButton;
    private final JButton refreshAnnouncementsButton;

    private final ArrayList<Query> loadedQueries;

    public FacultyDashboard(Faculty faculty) {
        this.faculty = faculty;
        this.queryDAO = new QueryDAO();
        this.announcementDAO = new AnnouncementDAO();
        this.loadedQueries = new ArrayList<Query>();

        setTitle(faculty.displayDashboard() + " - " + faculty.getName());
        setSize(1180, 760);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(14, 14));
        getContentPane().setBackground(UIStyle.PAGE_BACKGROUND);

        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setBackground(UIStyle.PAGE_BACKGROUND);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(22, 24, 0, 24));

        JLabel heading = new JLabel("Welcome, " + faculty.getName());
        UIStyle.styleLabel(heading, true);
        JLabel subtitle = new JLabel("Subject: " + faculty.getSubject() + " | Schedule: " + faculty.getSchedule());
        UIStyle.styleLabel(subtitle, false);

        headerPanel.add(heading);
        headerPanel.add(Box.createVerticalStrut(6));
        headerPanel.add(subtitle);
        add(headerPanel, BorderLayout.NORTH);

        JPanel navigationPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 0));
        navigationPanel.setBackground(UIStyle.PAGE_BACKGROUND);
        navigationPanel.setBorder(BorderFactory.createEmptyBorder(0, 24, 18, 24));
        navigationPanel.add(buildNavButton("Queries"));
        navigationPanel.add(buildNavButton("Announcements"));
        add(navigationPanel, BorderLayout.SOUTH);

        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.setBackground(UIStyle.PAGE_BACKGROUND);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(0, 24, 0, 24));

        JPanel queryPanel = new JPanel(new BorderLayout(14, 14));
        queryPanel.setBackground(UIStyle.PAGE_BACKGROUND);

        JPanel filterPanel = UIStyle.createCardPanel();
        filterPanel.setLayout(new GridBagLayout());
        GridBagConstraints filterGbc = new GridBagConstraints();
        filterGbc.insets = new Insets(8, 8, 8, 8);
        filterGbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel queryTitle = new JLabel("Assigned Queries");
        UIStyle.styleLabel(queryTitle, false);
        queryTitle.setFont(queryTitle.getFont().deriveFont(java.awt.Font.BOLD, 18f));
        filterGbc.gridx = 0;
        filterGbc.gridy = 0;
        filterGbc.gridwidth = 5;
        filterPanel.add(queryTitle, filterGbc);

        filterGbc.gridwidth = 1;
        filterGbc.gridy++;
        filterGbc.gridx = 0;
        filterPanel.add(createFieldLabel("Subject"), filterGbc);
        filterSubjectComboBox = new JComboBox<String>(new String[]{"All", faculty.getSubject()});
        UIStyle.styleField(filterSubjectComboBox);
        filterGbc.gridx = 1;
        filterPanel.add(filterSubjectComboBox, filterGbc);

        filterGbc.gridx = 2;
        filterPanel.add(createFieldLabel("Status"), filterGbc);
        filterStatusComboBox = new JComboBox<String>(new String[]{"All", "Pending", "Answered", "Resolved"});
        UIStyle.styleField(filterStatusComboBox);
        filterGbc.gridx = 3;
        filterPanel.add(filterStatusComboBox, filterGbc);

        refreshQueriesButton = new JButton("Apply Filters");
        UIStyle.styleSecondaryButton(refreshQueriesButton);
        refreshQueriesButton.addActionListener(this);
        filterGbc.gridx = 4;
        filterPanel.add(refreshQueriesButton, filterGbc);
        queryPanel.add(filterPanel, BorderLayout.NORTH);

        queryTableModel = new DefaultTableModel(
                new String[]{"ID", "Student", "Subject", "Priority", "Status", "Submitted", "Attachment"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        queryTable = new JTable(queryTableModel);
        JScrollPane queryScrollPane = new JScrollPane(queryTable);
        UIStyle.styleTable(queryTable, queryScrollPane);
        queryTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent event) {
                if (!event.getValueIsAdjusting()) {
                    showSelectedQueryDetails();
                }
            }
        });
        queryPanel.add(queryScrollPane, BorderLayout.CENTER);

        JPanel responsePanel = new JPanel(new GridLayout(1, 2, 14, 14));
        responsePanel.setBackground(UIStyle.PAGE_BACKGROUND);
        responsePanel.setPreferredSize(new Dimension(1000, 260));

        JPanel queryDetailsPanel = UIStyle.createCardPanel();
        queryDetailsPanel.setLayout(new BorderLayout(10, 10));
        JLabel detailTitle = new JLabel("Selected Query Details");
        UIStyle.styleLabel(detailTitle, false);
        detailTitle.setFont(detailTitle.getFont().deriveFont(java.awt.Font.BOLD, 17f));
        queryDetailsPanel.add(detailTitle, BorderLayout.NORTH);
        selectedQueryArea = new JTextArea();
        selectedQueryArea.setEditable(false);
        UIStyle.styleField(selectedQueryArea);
        JScrollPane detailScrollPane = new JScrollPane(selectedQueryArea);
        detailScrollPane.setBorder(BorderFactory.createLineBorder(UIStyle.BORDER));
        queryDetailsPanel.add(detailScrollPane, BorderLayout.CENTER);
        openFileButton = new JButton("Open Attachment");
        UIStyle.styleSecondaryButton(openFileButton);
        openFileButton.addActionListener(this);
        JPanel openButtonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        openButtonPanel.setBackground(UIStyle.CARD_BACKGROUND);
        openButtonPanel.add(openFileButton);
        queryDetailsPanel.add(openButtonPanel, BorderLayout.SOUTH);

        JPanel replyPanel = UIStyle.createCardPanel();
        replyPanel.setLayout(new BorderLayout(10, 10));
        JLabel replyTitle = new JLabel("Reply to Student");
        UIStyle.styleLabel(replyTitle, false);
        replyTitle.setFont(replyTitle.getFont().deriveFont(java.awt.Font.BOLD, 17f));
        replyPanel.add(replyTitle, BorderLayout.NORTH);
        replyArea = new JTextArea();
        UIStyle.styleField(replyArea);
        JScrollPane replyScrollPane = new JScrollPane(replyArea);
        replyScrollPane.setBorder(BorderFactory.createLineBorder(UIStyle.BORDER));
        replyPanel.add(replyScrollPane, BorderLayout.CENTER);
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        actionPanel.setBackground(UIStyle.CARD_BACKGROUND);
        answeredCheckBox = new JCheckBox("Mark as Answered");
        answeredCheckBox.setBackground(UIStyle.CARD_BACKGROUND);
        replyButton = new JButton("Save Reply");
        resolveButton = new JButton("Mark Resolved");
        UIStyle.stylePrimaryButton(replyButton);
        UIStyle.styleSecondaryButton(resolveButton);
        replyButton.addActionListener(this);
        resolveButton.addActionListener(this);
        actionPanel.add(answeredCheckBox);
        actionPanel.add(resolveButton);
        actionPanel.add(replyButton);
        replyPanel.add(actionPanel, BorderLayout.SOUTH);

        responsePanel.add(queryDetailsPanel);
        responsePanel.add(replyPanel);
        queryPanel.add(responsePanel, BorderLayout.SOUTH);

        JPanel announcementPanel = new JPanel(new BorderLayout(14, 14));
        announcementPanel.setBackground(UIStyle.PAGE_BACKGROUND);

        JPanel announcementInputPanel = UIStyle.createCardPanel();
        announcementInputPanel.setLayout(new BorderLayout(10, 10));
        JLabel announcementTitle = new JLabel("Post Announcement");
        UIStyle.styleLabel(announcementTitle, false);
        announcementTitle.setFont(announcementTitle.getFont().deriveFont(java.awt.Font.BOLD, 18f));
        announcementInputPanel.add(announcementTitle, BorderLayout.NORTH);
        announcementArea = new JTextArea(5, 20);
        UIStyle.styleField(announcementArea);
        JScrollPane announcementInputScrollPane = new JScrollPane(announcementArea);
        announcementInputScrollPane.setBorder(BorderFactory.createLineBorder(UIStyle.BORDER));
        announcementInputPanel.add(announcementInputScrollPane, BorderLayout.CENTER);

        JPanel announcementButtonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        announcementButtonsPanel.setBackground(UIStyle.CARD_BACKGROUND);
        postAnnouncementButton = new JButton("Post Announcement");
        refreshAnnouncementsButton = new JButton("Refresh Notices");
        UIStyle.stylePrimaryButton(postAnnouncementButton);
        UIStyle.styleSecondaryButton(refreshAnnouncementsButton);
        postAnnouncementButton.addActionListener(this);
        refreshAnnouncementsButton.addActionListener(this);
        announcementButtonsPanel.add(refreshAnnouncementsButton);
        announcementButtonsPanel.add(postAnnouncementButton);
        announcementInputPanel.add(announcementButtonsPanel, BorderLayout.SOUTH);

        JPanel historyPanel = UIStyle.createCardPanel();
        historyPanel.setLayout(new BorderLayout(10, 10));
        JLabel historyTitle = new JLabel("Announcement History");
        UIStyle.styleLabel(historyTitle, false);
        historyTitle.setFont(historyTitle.getFont().deriveFont(java.awt.Font.BOLD, 18f));
        historyPanel.add(historyTitle, BorderLayout.NORTH);
        announcementHistoryArea = new JTextArea();
        announcementHistoryArea.setEditable(false);
        UIStyle.styleField(announcementHistoryArea);
        JScrollPane historyScrollPane = new JScrollPane(announcementHistoryArea);
        historyScrollPane.setBorder(BorderFactory.createLineBorder(UIStyle.BORDER));
        historyPanel.add(historyScrollPane, BorderLayout.CENTER);

        announcementPanel.add(announcementInputPanel, BorderLayout.NORTH);
        announcementPanel.add(historyPanel, BorderLayout.CENTER);

        contentPanel.add(queryPanel, "Queries");
        contentPanel.add(announcementPanel, "Announcements");
        add(contentPanel, BorderLayout.CENTER);

        loadQueriesInBackground();
        loadAnnouncementsInBackground();
    }

    private JLabel createFieldLabel(String text) {
        JLabel label = new JLabel(text);
        UIStyle.styleLabel(label, false);
        return label;
    }

    private JButton buildNavButton(String title) {
        JButton button = new JButton(title);
        button.setActionCommand("NAV_" + title);
        UIStyle.styleSecondaryButton(button);
        button.setPreferredSize(new Dimension(170, 42));
        button.addActionListener(this);
        return button;
    }

    @Override
    public void actionPerformed(java.awt.event.ActionEvent event) {
        Object source = event.getSource();

        if ("NAV_Queries".equals(event.getActionCommand())) {
            cardLayout.show(contentPanel, "Queries");
            applyFiltersAndRefresh();
        } else if ("NAV_Announcements".equals(event.getActionCommand())) {
            cardLayout.show(contentPanel, "Announcements");
        } else if (source == refreshQueriesButton) {
            loadQueriesInBackground();
        } else if (source == replyButton) {
            replyToSelectedQuery();
        } else if (source == resolveButton) {
            resolveSelectedQuery();
        } else if (source == openFileButton) {
            openSelectedAttachment();
        } else if (source == postAnnouncementButton) {
            postAnnouncement();
        } else if (source == refreshAnnouncementsButton) {
            loadAnnouncementsInBackground();
        }
    }

    private void loadQueriesInBackground() {
        SwingWorker<ArrayList<Query>, Void> worker = new SwingWorker<ArrayList<Query>, Void>() {
            @Override
            protected ArrayList<Query> doInBackground() throws Exception {
                return queryDAO.getQueriesByFaculty(faculty.getId());
            }

            @Override
            protected void done() {
                try {
                    ArrayList<Query> queries = get();
                    loadedQueries.clear();
                    loadedQueries.addAll(queries);
                    applyFiltersAndRefresh();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(FacultyDashboard.this, "Unable to load queries.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        worker.execute();
    }

    private void applyFiltersAndRefresh() {
        ArrayList<Query> filteredQueries = new ArrayList<Query>();
        String selectedSubject = filterSubjectComboBox.getSelectedItem() == null ? "All" : filterSubjectComboBox.getSelectedItem().toString();
        String selectedStatus = filterStatusComboBox.getSelectedItem() == null ? "All" : filterStatusComboBox.getSelectedItem().toString();

        for (Query query : loadedQueries) {
            boolean subjectMatches = "All".equals(selectedSubject) || query.getSubject().equals(selectedSubject);
            boolean statusMatches = "All".equals(selectedStatus) || query.getStatus().equalsIgnoreCase(selectedStatus);
            if (subjectMatches && statusMatches) {
                filteredQueries.add(query);
            }
        }

        queryTableModel.setRowCount(0);
        for (Query query : filteredQueries) {
            queryTableModel.addRow(new Object[]{
                    query.getId(),
                    query.getStudentName(),
                    query.getSubject(),
                    query.getPriority(),
                    query.getStatus(),
                    query.getSubmittedAt(),
                    query.getFilePath()
            });
        }
        selectedQueryArea.setText("");
    }

    private Query getSelectedQuery() {
        int selectedRow = queryTable.getSelectedRow();
        if (selectedRow < 0) {
            return null;
        }

        int selectedId = Integer.parseInt(queryTableModel.getValueAt(selectedRow, 0).toString());
        for (Query query : loadedQueries) {
            if (query.getId() == selectedId) {
                return query;
            }
        }
        return null;
    }

    private void showSelectedQueryDetails() {
        Query selectedQuery = getSelectedQuery();
        if (selectedQuery == null) {
            return;
        }

        selectedQueryArea.setText(
                "Student: " + selectedQuery.getStudentName()
                        + "\nSubject: " + selectedQuery.getSubject()
                        + "\nPriority: " + selectedQuery.getPriority()
                        + "\nStatus: " + selectedQuery.getStatus()
                        + "\nSubmitted: " + selectedQuery.getSubmittedAt()
                        + "\nAttachment Path: " + selectedQuery.getFilePath()
                        + "\n\nQuestion:\n" + selectedQuery.getQuestion()
                        + "\n\nCurrent Reply:\n" + selectedQuery.getAnswer()
        );
    }

    private void replyToSelectedQuery() {
        Query selectedQuery = getSelectedQuery();
        if (selectedQuery == null) {
            JOptionPane.showMessageDialog(this, "Please select a query first.");
            return;
        }

        try {
            queryDAO.replyToQuery(selectedQuery.getId(), replyArea.getText(), answeredCheckBox.isSelected());
            JOptionPane.showMessageDialog(this, "Reply saved successfully.");
            replyArea.setText("");
            answeredCheckBox.setSelected(false);
            loadQueriesInBackground();
        } catch (PortalException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void resolveSelectedQuery() {
        Query selectedQuery = getSelectedQuery();
        if (selectedQuery == null) {
            JOptionPane.showMessageDialog(this, "Please select a query first.");
            return;
        }

        try {
            queryDAO.markResolved(selectedQuery.getId());
            JOptionPane.showMessageDialog(this, "Query marked as resolved.");
            loadQueriesInBackground();
        } catch (PortalException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void openSelectedAttachment() {
        Query selectedQuery = getSelectedQuery();
        if (selectedQuery == null) {
            JOptionPane.showMessageDialog(this, "Please select a query first.");
            return;
        }

        try {
            String filePath = selectedQuery.getFilePath();
            if (filePath == null || filePath.trim().isEmpty()) {
                throw new PortalException("No attachment found for this query.");
            }

            File file = new File(filePath);
            if (!file.exists()) {
                throw new PortalException("Attached file path is no longer available.");
            }

            Desktop.getDesktop().open(file);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "File Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void postAnnouncement() {
        try {
            Announcement announcement = new Announcement(announcementArea.getText(), faculty.getName());
            announcementDAO.addAnnouncement(announcement);
            JOptionPane.showMessageDialog(this, "Announcement posted successfully.");
            announcementArea.setText("");
            loadAnnouncementsInBackground();
        } catch (PortalException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadAnnouncementsInBackground() {
        SwingWorker<ArrayList<Announcement>, Void> worker = new SwingWorker<ArrayList<Announcement>, Void>() {
            @Override
            protected ArrayList<Announcement> doInBackground() throws Exception {
                return announcementDAO.getAllAnnouncements();
            }

            @Override
            protected void done() {
                try {
                    List<Announcement> announcements = get();
                    StringBuilder builder = new StringBuilder();
                    for (Announcement announcement : announcements) {
                        builder.append("- ").append(announcement.toString()).append("\n\n");
                    }
                    announcementHistoryArea.setText(builder.toString());
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(FacultyDashboard.this, "Unable to load announcements.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        worker.execute();
    }
}
