package ui;


import bus.*;
import dto.*;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.SQLException;

public class LoginPanel extends JPanel {

    private final BankApp app;
    private boolean isCustomer = true;

    private final JTextField     emailFld = new JTextField(20);
    private final JPasswordField pinFld   = new JPasswordField(20);
    private final JTextField     idFld    = new JTextField(20);
    private final JPanel         formBox  = new JPanel();

    private JButton cBtn, mBtn;

    public LoginPanel(BankApp app) {
        this.app = app;
        setName("LOGIN");
        setBackground(BankApp.C_BG);
        setLayout(new GridBagLayout());
        add(buildCard());
    }

    // ── Card container ─────────────────────────────────────
    private JPanel buildCard() {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(BankApp.C_CARD);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BankApp.C_BORDER, 1),
            BorderFactory.createEmptyBorder(44, 52, 44, 52)));
        card.setPreferredSize(new Dimension(440, 480));
        card.setMaximumSize(new Dimension(440, 600));

        // Logo
        JLabel logo = new JLabel("\uD83C\uDFE6  FORTIS BANK", SwingConstants.CENTER);
        logo.setFont(BankApp.F_DISPLAY);
        logo.setForeground(BankApp.C_NAV);
        logo.setAlignmentX(CENTER_ALIGNMENT);

        JLabel sub = new JLabel("Secure Digital Banking", SwingConstants.CENTER);
        sub.setFont(BankApp.F_SMALL);
        sub.setForeground(BankApp.C_MUTED);
        sub.setAlignmentX(CENTER_ALIGNMENT);

        // Form area
        formBox.setLayout(new BoxLayout(formBox, BoxLayout.Y_AXIS));
        formBox.setBackground(BankApp.C_CARD);
        formBox.setAlignmentX(LEFT_ALIGNMENT);
        styleField(emailFld);
        styleField(pinFld);
        styleField(idFld);
        buildForm(true);

        // Sign-in button
        JButton signIn = new JButton("Sign In");
        signIn.setFont(BankApp.F_HEADER);
        signIn.setBackground(BankApp.C_ACCENT);
        signIn.setForeground(Color.WHITE);
        signIn.setOpaque(true);
        signIn.setBorderPainted(false);
        signIn.setFocusPainted(false);
        signIn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        signIn.setAlignmentX(CENTER_ALIGNMENT);
        signIn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        signIn.addActionListener(e -> doLogin());

        card.add(logo);
        card.add(Box.createVerticalStrut(4));
        card.add(sub);
        card.add(Box.createVerticalStrut(28));
        card.add(buildToggle());
        card.add(Box.createVerticalStrut(22));
        card.add(formBox);
        card.add(Box.createVerticalStrut(8));
        card.add(signIn);

        return card;
    }

    // ── Customer / Manager toggle ──────────────────────────
    private JPanel buildToggle() {
        JPanel wrapper = new JPanel(new GridLayout(1, 2, 0, 0));
        wrapper.setBackground(new Color(235, 238, 245));
        wrapper.setBorder(BorderFactory.createLineBorder(BankApp.C_BORDER, 1));
        wrapper.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        wrapper.setAlignmentX(LEFT_ALIGNMENT);

        cBtn = makeToggleBtn("Customer");
        mBtn = makeToggleBtn("Manager");

        applyActive(cBtn);
        applyInactive(mBtn);

        cBtn.addActionListener(e -> {
            isCustomer = true;
            applyActive(cBtn);
            applyInactive(mBtn);
            buildForm(true);
        });
        mBtn.addActionListener(e -> {
            isCustomer = false;
            applyActive(mBtn);
            applyInactive(cBtn);
            buildForm(false);
        });

        wrapper.add(cBtn);
        wrapper.add(mBtn);
        return wrapper;
    }

    private JButton makeToggleBtn(String text) {
        JButton b = new JButton(text);
        b.setFont(BankApp.F_BODY);
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setOpaque(true);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setPreferredSize(new Dimension(0, 40));
        return b;
    }

    private void applyActive(JButton b) {
        b.setBackground(BankApp.C_NAV);
        b.setForeground(Color.WHITE);
    }

    private void applyInactive(JButton b) {
        b.setBackground(new Color(235, 238, 245));
        b.setForeground(BankApp.C_TEXT);
    }

    // ── Dynamic form rows ──────────────────────────────────
    private void buildForm(boolean customer) {
        formBox.removeAll();
        if (customer) {
            addFormRow("Email Address", emailFld);
            addFormRow("PIN (4 digits)", pinFld);
        } else {
            addFormRow("Manager ID", idFld);
            addFormRow("Email Address", emailFld);
        }
        formBox.revalidate();
        formBox.repaint();
    }

    private void addFormRow(String labelText, JComponent field) {
        JLabel lbl = new JLabel(labelText);
        lbl.setFont(BankApp.F_SMALL);
        lbl.setForeground(BankApp.C_MUTED);
        lbl.setAlignmentX(LEFT_ALIGNMENT);

        field.setAlignmentX(LEFT_ALIGNMENT);
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));

        formBox.add(lbl);
        formBox.add(Box.createVerticalStrut(5));
        formBox.add(field);
        formBox.add(Box.createVerticalStrut(14));
    }

    private void styleField(JComponent f) {
        f.setFont(BankApp.F_BODY);
        f.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BankApp.C_BORDER),
            BorderFactory.createEmptyBorder(9, 12, 9, 12)));
    }

    // ── Login logic ────────────────────────────────────────
    private void doLogin() {
        try {
            if (isCustomer) {
                String email = emailFld.getText().trim();
                String pin   = new String(pinFld.getPassword()).trim();
                CustomerDTO c = app.custSvc.login(email, pin);
                app.showCustomer(c);
            } else {
                String id    = idFld.getText().trim();
                String email = emailFld.getText().trim();
                BankManagerDTO m = app.mgrSvc.login(id, email);
                app.showManager(m);
            }
        } catch (NotNullException | NotInRangeException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Login Failed", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}