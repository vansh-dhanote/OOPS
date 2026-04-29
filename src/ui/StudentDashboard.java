package ui;

import dao.AnnouncementDAO;
import dao.QueryDAO;
import dao.UserDAO;
import model.Announcement;
import model.Faculty;
import model.Query;
import model.Student;
import util.PortalException;
import util.UIStyle;
import util.ValidationUtil;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingWorker;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class StudentDashboard extends JFrame implements ActionListener {
    private final Student student;
    private final QueryDAO queryDAO;
    private final AnnouncementDAO announcementDAO;
    private final UserDAO userDAO;

    private final CardLayout cardLayout;
    private final JPanel contentPanel;

    private final JComboBox<String> subjectComboBox;
    private final JComboBox<Faculty> facultyComboBox;
    private final JComboBox<String> priorityComboBox;
    private final JTextArea questionArea;
    private final JLabel scheduleLabel;
    private final JTextField filePathField;
    private final JButton chooseFileButton;
    private final JButton checkAvailabilityButton;
    private final JButton submitButton;

    private final JTable queryTable;
    private final DefaultTableModel queryTableModel;
    private final JComboBox<String> filterSubjectComboBox;
    private final JComboBox<String> filterStatusComboBox;
    private final JButton refreshQueriesButton;

    private final JTextArea announcementArea;
    private final JButton refreshAnnouncementsButton;

    private final ArrayList<Query> loadedQueries;
    private final HashMap<Integer, String> lastSeenReplies;
    private boolean firstQueryLoad;

    public StudentDashboard(Student student) {
        this.student = student;
        this.queryDAO = new QueryDAO();
        this.announcementDAO = new AnnouncementDAO();
        this.userDAO = new UserDAO();
        this.loadedQueries = new ArrayList<Query>();
        this.lastSeenReplies = new HashMap<Integer, String>();
        this.firstQueryLoad = true;

        setTitle(student.displayDashboard() + " - " + student.getName());
        setSize(1120, 720);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(14, 14));
        getContentPane().setBackground(UIStyle.PAGE_BACKGROUND);

        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setBackground(UIStyle.PAGE_BACKGROUND);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(22, 24, 0, 24));

        JLabel heading = new JLabel("Welcome, " + student.getName());
        UIStyle.styleLabel(heading, true);
        JLabel subtitle = new JLabel("Raise queries, track replies, and stay updated with faculty announcements.");
        UIStyle.styleLabel(subtitle, false);

        headerPanel.add(heading);
        headerPanel.add(Box.createVerticalStrut(6));
        headerPanel.add(subtitle);
        add(headerPanel, BorderLayout.NORTH);

        JPanel navigationPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 0));
        navigationPanel.setBackground(UIStyle.PAGE_BACKGROUND);
        navigationPanel.setBorder(BorderFactory.createEmptyBorder(0, 24, 18, 24));
        navigationPanel.add(buildNavButton("Dashboard"));
        navigationPanel.add(buildNavButton("Queries"));
        navigationPanel.add(buildNavButton("Announcements"));
        add(navigationPanel, BorderLayout.SOUTH);

        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.setBackground(UIStyle.PAGE_BACKGROUND);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(0, 24, 0, 24));

        JPanel dashboardPanel = new JPanel(new BorderLayout(14, 14));
        dashboardPanel.setBackground(UIStyle.PAGE_BACKGROUND);

        JPanel formPanel = UIStyle.createCardPanel();
        formPanel.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel formTitle = new JLabel("Raise New Query");
        UIStyle.styleLabel(formTitle, false);
        formTitle.setFont(formTitle.getFont().deriveFont(java.awt.Font.BOLD, 18f));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        formPanel.add(formTitle, gbc);

        gbc.gridwidth = 1;
        gbc.gridy++;
        formPanel.add(createFieldLabel("Subject"), gbc);
        subjectComboBox = new JComboBox<String>();
        UIStyle.styleField(subjectComboBox);
        subjectComboBox.addActionListener(this);
        gbc.gridx = 1;
        formPanel.add(subjectComboBox, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        formPanel.add(createFieldLabel("Faculty"), gbc);
        facultyComboBox = new JComboBox<Faculty>();
        UIStyle.styleField(facultyComboBox);
        facultyComboBox.addActionListener(this);
        gbc.gridx = 1;
        formPanel.add(facultyComboBox, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        formPanel.add(createFieldLabel("Priority"), gbc);
        priorityComboBox = new JComboBox<String>(new String[]{"Low", "Medium", "High"});
        UIStyle.styleField(priorityComboBox);
        gbc.gridx = 1;
        formPanel.add(priorityComboBox, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        formPanel.add(createFieldLabel("Attachment"), gbc);
        JPanel filePanel = new JPanel(new BorderLayout(8, 0));
        filePanel.setBackground(UIStyle.CARD_BACKGROUND);
        filePathField = new JTextField();
        filePathField.setEditable(false);
        UIStyle.styleField(filePathField);
        chooseFileButton = new JButton("Choose File");
        UIStyle.styleSecondaryButton(chooseFileButton);
        chooseFileButton.addActionListener(this);
        filePanel.add(filePathField, BorderLayout.CENTER);
        filePanel.add(chooseFileButton, BorderLayout.EAST);
        gbc.gridx = 1;
        formPanel.add(filePanel, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        formPanel.add(createFieldLabel("Faculty Schedule"), gbc);
        JPanel schedulePanel = new JPanel(new BorderLayout(8, 0));
        schedulePanel.setBackground(UIStyle.CARD_BACKGROUND);
        scheduleLabel = new JLabel("Select faculty to view schedule");
        UIStyle.styleLabel(scheduleLabel, false);
        scheduleLabel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(UIStyle.BORDER), BorderFactory.createEmptyBorder(10, 12, 10, 12)));
        checkAvailabilityButton = new JButton("Check Availability");
        UIStyle.styleSecondaryButton(checkAvailabilityButton);
        checkAvailabilityButton.addActionListener(this);
        schedulePanel.add(scheduleLabel, BorderLayout.CENTER);
        schedulePanel.add(checkAvailabilityButton, BorderLayout.EAST);
        gbc.gridx = 1;
        formPanel.add(schedulePanel, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        formPanel.add(createFieldLabel("Description"), gbc);
        questionArea = new JTextArea(6, 20);
        UIStyle.styleField(questionArea);
        JScrollPane questionScrollPane = new JScrollPane(questionArea);
        questionScrollPane.setBorder(BorderFactory.createLineBorder(UIStyle.BORDER));
        gbc.gridx = 1;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        formPanel.add(questionScrollPane, gbc);

        gbc.gridx = 1;
        gbc.gridy++;
        gbc.weighty = 0;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.EAST;
        submitButton = new JButton("Submit Query");
        UIStyle.stylePrimaryButton(submitButton);
        submitButton.addActionListener(this);
        formPanel.add(submitButton, gbc);

        dashboardPanel.add(formPanel, BorderLayout.CENTER);

        JPanel queriesPanel = new JPanel(new BorderLayout(14, 14));
        queriesPanel.setBackground(UIStyle.PAGE_BACKGROUND);

        JPanel filterPanel = UIStyle.createCardPanel();
        filterPanel.setLayout(new GridBagLayout());
        GridBagConstraints filterGbc = new GridBagConstraints();
        filterGbc.insets = new Insets(8, 8, 8, 8);
        filterGbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel queryTitle = new JLabel("My Queries");
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
        filterSubjectComboBox = new JComboBox<String>(new String[]{"All"});
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
        queriesPanel.add(filterPanel, BorderLayout.NORTH);

        queryTableModel = new DefaultTableModel(
                new String[]{"ID", "Subject", "Faculty", "Priority", "Status", "Submitted", "Attachment", "Reply"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        queryTable = new JTable(queryTableModel);
        JScrollPane queryScrollPane = new JScrollPane(queryTable);
        UIStyle.styleTable(queryTable, queryScrollPane);
        queriesPanel.add(queryScrollPane, BorderLayout.CENTER);

        JPanel announcementPanel = new JPanel(new BorderLayout(14, 14));
        announcementPanel.setBackground(UIStyle.PAGE_BACKGROUND);
        JPanel announcementCard = UIStyle.createCardPanel();
        announcementCard.setLayout(new BorderLayout(12, 12));
        JLabel announcementTitle = new JLabel("Announcements");
        UIStyle.styleLabel(announcementTitle, false);
        announcementTitle.setFont(announcementTitle.getFont().deriveFont(java.awt.Font.BOLD, 18f));
        announcementCard.add(announcementTitle, BorderLayout.NORTH);

        announcementArea = new JTextArea();
        announcementArea.setEditable(false);
        UIStyle.styleField(announcementArea);
        JScrollPane announcementScrollPane = new JScrollPane(announcementArea);
        announcementScrollPane.setBorder(BorderFactory.createLineBorder(UIStyle.BORDER));
        announcementCard.add(announcementScrollPane, BorderLayout.CENTER);

        refreshAnnouncementsButton = new JButton("Refresh Announcements");
        UIStyle.styleSecondaryButton(refreshAnnouncementsButton);
        refreshAnnouncementsButton.addActionListener(this);
        JPanel announcementButtonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        announcementButtonPanel.setBackground(UIStyle.CARD_BACKGROUND);
        announcementButtonPanel.add(refreshAnnouncementsButton);
        announcementCard.add(announcementButtonPanel, BorderLayout.SOUTH);
        announcementPanel.add(announcementCard, BorderLayout.CENTER);

        contentPanel.add(dashboardPanel, "Dashboard");
        contentPanel.add(queriesPanel, "Queries");
        contentPanel.add(announcementPanel, "Announcements");
        add(contentPanel, BorderLayout.CENTER);

        loadSubjects();
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
    public void actionPerformed(ActionEvent event) {
        Object source = event.getSource();

        if (source == subjectComboBox) {
            loadFacultyForSelectedSubject();
        } else if (source == facultyComboBox) {
            updateScheduleLabel();
        } else if (source == chooseFileButton) {
            chooseAttachment();
        } else if (source == checkAvailabilityButton) {
            showAvailability();
        } else if (source == submitButton) {
            submitQuery();
        } else if (source == refreshQueriesButton) {
            applyFiltersAndRefresh();
        } else if (source == refreshAnnouncementsButton) {
            loadAnnouncementsInBackground();
        } else if ("NAV_Dashboard".equals(event.getActionCommand())) {
            cardLayout.show(contentPanel, "Dashboard");
        } else if ("NAV_Queries".equals(event.getActionCommand())) {
            cardLayout.show(contentPanel, "Queries");
            applyFiltersAndRefresh();
        } else if ("NAV_Announcements".equals(event.getActionCommand())) {
            cardLayout.show(contentPanel, "Announcements");
        }
    }

    private void loadSubjects() {
        try {
            List<String> subjects = userDAO.getAvailableSubjects();
            subjectComboBox.removeAllItems();
            for (String subject : subjects) {
                subjectComboBox.addItem(subject);
            }
            if (!subjects.isEmpty()) {
                subjectComboBox.setSelectedIndex(0);
                loadFacultyForSelectedSubject();
            }
        } catch (PortalException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadFacultyForSelectedSubject() {
        Object selectedSubject = subjectComboBox.getSelectedItem();
        facultyComboBox.removeAllItems();
        scheduleLabel.setText("Select faculty to view schedule");

        if (selectedSubject == null) {
            return;
        }

        try {
            List<Faculty> faculties = userDAO.getFacultyBySubject(selectedSubject.toString());
            for (Faculty faculty : faculties) {
                facultyComboBox.addItem(faculty);
            }
            updateScheduleLabel();
        } catch (PortalException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateScheduleLabel() {
        Faculty selectedFaculty = (Faculty) facultyComboBox.getSelectedItem();
        if (selectedFaculty == null) {
            scheduleLabel.setText("No faculty available for this subject");
            return;
        }
        scheduleLabel.setText(selectedFaculty.getSchedule());
    }

    private void chooseAttachment() {
        javax.swing.JFileChooser fileChooser = new javax.swing.JFileChooser();
        int result = fileChooser.showOpenDialog(this);
        if (result == javax.swing.JFileChooser.APPROVE_OPTION) {
            try {
                File selectedFile = fileChooser.getSelectedFile();
                filePathField.setText(selectedFile.getCanonicalPath());
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Unable to select file.", "File Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void showAvailability() {
        Faculty selectedFaculty = (Faculty) facultyComboBox.getSelectedItem();
        if (selectedFaculty == null) {
            JOptionPane.showMessageDialog(this, "Please select a faculty member first.");
            return;
        }

        JOptionPane.showMessageDialog(
                this,
                selectedFaculty.getName() + " is available at: " + selectedFaculty.getSchedule(),
                "Faculty Availability",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    private void submitQuery() {
        try {
            Faculty selectedFaculty = (Faculty) facultyComboBox.getSelectedItem();
            if (selectedFaculty == null) {
                throw new PortalException("Please select a faculty member.");
            }

            ValidationUtil.requireText(questionArea.getText(), "Description");

            Query query = new Query(
                    student.getId(),
                    selectedFaculty.getId(),
                    subjectComboBox.getSelectedItem().toString(),
                    questionArea.getText(),
                    filePathField.getText(),
                    priorityComboBox.getSelectedItem().toString()
            );
            query.setAnswer("No reply yet");
            queryDAO.addQuery(query);

            JOptionPane.showMessageDialog(this, "Query submitted successfully.");
            questionArea.setText("");
            filePathField.setText("");
            loadQueriesInBackground();
        } catch (PortalException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Validation Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadQueriesInBackground() {
        SwingWorker<ArrayList<Query>, Void> worker = new SwingWorker<ArrayList<Query>, Void>() {
            @Override
            protected ArrayList<Query> doInBackground() throws Exception {
                return queryDAO.getQueriesByStudent(student.getId());
            }

            @Override
            protected void done() {
                try {
                    ArrayList<Query> queries = get();
                    loadedQueries.clear();
                    loadedQueries.addAll(queries);
                    refreshSubjectFilterOptions(queries);
                    fillQueryTable(applyQueryFilters(queries));
                    showReplyNotifications(queries);
                    firstQueryLoad = false;
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(StudentDashboard.this, "Unable to load queries.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        worker.execute();
    }

    private void refreshSubjectFilterOptions(List<Query> queries) {
        String currentSelection = filterSubjectComboBox.getSelectedItem() == null ? "All" : filterSubjectComboBox.getSelectedItem().toString();

        filterSubjectComboBox.removeAllItems();
        filterSubjectComboBox.addItem("All");
        ArrayList<String> subjects = new ArrayList<String>();
        for (Query query : queries) {
            if (!subjects.contains(query.getSubject())) {
                subjects.add(query.getSubject());
                filterSubjectComboBox.addItem(query.getSubject());
            }
        }
        filterSubjectComboBox.setSelectedItem(currentSelection);
    }

    private ArrayList<Query> applyQueryFilters(List<Query> sourceQueries) {
        ArrayList<Query> filteredQueries = new ArrayList<Query>();
        String selectedSubject = filterSubjectComboBox.getSelectedItem() == null ? "All" : filterSubjectComboBox.getSelectedItem().toString();
        String selectedStatus = filterStatusComboBox.getSelectedItem() == null ? "All" : filterStatusComboBox.getSelectedItem().toString();

        for (Query query : sourceQueries) {
            boolean subjectMatches = "All".equals(selectedSubject) || query.getSubject().equals(selectedSubject);
            boolean statusMatches = "All".equals(selectedStatus) || query.getStatus().equalsIgnoreCase(selectedStatus);

            if (subjectMatches && statusMatches) {
                filteredQueries.add(query);
            }
        }
        return filteredQueries;
    }

    private void fillQueryTable(List<Query> queries) {
        queryTableModel.setRowCount(0);
        for (Query query : queries) {
            queryTableModel.addRow(new Object[]{
                    query.getId(),
                    query.getSubject(),
                    query.getFacultyName(),
                    query.getPriority(),
                    query.getStatus(),
                    query.getSubmittedAt(),
                    query.getFilePath(),
                    query.getAnswer()
            });
        }
    }

    private void showReplyNotifications(List<Query> queries) {
        for (Query query : queries) {
            String answer = query.getAnswer() == null ? "" : query.getAnswer().trim();
            String oldAnswer = lastSeenReplies.get(query.getId());
            if (!firstQueryLoad && oldAnswer != null && !oldAnswer.equals(answer) && answer.length() > 0 && !"No reply yet".equalsIgnoreCase(answer)) {
                JOptionPane.showMessageDialog(this,
                        "New reply received for query ID " + query.getId(),
                        "Notification",
                        JOptionPane.INFORMATION_MESSAGE);
            }
            lastSeenReplies.put(query.getId(), answer);
        }
    }

    private void applyFiltersAndRefresh() {
        fillQueryTable(applyQueryFilters(loadedQueries));
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
                    announcementArea.setText(builder.toString());
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(StudentDashboard.this, "Unable to load announcements.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        worker.execute();
    }
}
