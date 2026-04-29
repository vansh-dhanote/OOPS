package ui;

import dao.UserDAO;
import model.Faculty;
import model.Student;
import model.User;
import util.PortalException;
import util.UIStyle;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginFrame extends JFrame implements ActionListener {
    private final JTextField userIdField;
    private final JPasswordField passwordField;
    private final JButton loginButton;
    private final UserDAO userDAO;

    public LoginFrame() {
        userDAO = new UserDAO();

        setTitle("Faculty & Student Interaction Portal");
        setSize(560, 360);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        getContentPane().setBackground(UIStyle.PAGE_BACKGROUND);

        JPanel wrapperPanel = new JPanel(new BorderLayout());
        wrapperPanel.setBackground(UIStyle.PAGE_BACKGROUND);
        wrapperPanel.setBorder(BorderFactory.createEmptyBorder(28, 40, 28, 40));

        JPanel loginCard = UIStyle.createCardPanel();
        loginCard.setLayout(new BoxLayout(loginCard, BoxLayout.Y_AXIS));

        JLabel heading = new JLabel("Faculty & Student Interaction Portal");
        UIStyle.styleLabel(heading, true);
        heading.setAlignmentX(CENTER_ALIGNMENT);

        JLabel subtitle = new JLabel("Login to continue to your dashboard");
        UIStyle.styleLabel(subtitle, false);
        subtitle.setAlignmentX(CENTER_ALIGNMENT);

        JPanel formPanel = new JPanel();
        formPanel.setBackground(UIStyle.CARD_BACKGROUND);
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 8, 6, 8));

        JLabel userLabel = new JLabel("User ID");
        UIStyle.styleLabel(userLabel, false);
        userIdField = new JTextField();
        userIdField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        UIStyle.styleField(userIdField);

        JLabel passwordLabel = new JLabel("Password");
        UIStyle.styleLabel(passwordLabel, false);
        passwordField = new JPasswordField();
        passwordField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        UIStyle.styleField(passwordField);

        loginButton = new JButton("Login");
        UIStyle.stylePrimaryButton(loginButton);
        loginButton.addActionListener(this);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        buttonPanel.setBackground(UIStyle.CARD_BACKGROUND);
        buttonPanel.add(loginButton);

        formPanel.add(userLabel);
        formPanel.add(Box.createVerticalStrut(6));
        formPanel.add(userIdField);
        formPanel.add(Box.createVerticalStrut(14));
        formPanel.add(passwordLabel);
        formPanel.add(Box.createVerticalStrut(6));
        formPanel.add(passwordField);

        loginCard.add(heading);
        loginCard.add(Box.createVerticalStrut(8));
        loginCard.add(subtitle);
        loginCard.add(Box.createVerticalStrut(14));
        loginCard.add(formPanel);
        loginCard.add(Box.createVerticalStrut(12));
        loginCard.add(buttonPanel);

        wrapperPanel.add(loginCard, BorderLayout.CENTER);
        add(wrapperPanel, BorderLayout.CENTER);
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        if (event.getSource() == loginButton) {
            handleLogin();
        }
    }

    private void handleLogin() {
        try {
            User user = userDAO.authenticateUser(userIdField.getText(), new String(passwordField.getPassword()));

            if (user instanceof Student) {
                new StudentDashboard((Student) user).setVisible(true);
            } else if (user instanceof Faculty) {
                new FacultyDashboard((Faculty) user).setVisible(true);
            }

            dispose();
        } catch (PortalException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Login Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new LoginFrame().setVisible(true);
            }
        });
    }
}
