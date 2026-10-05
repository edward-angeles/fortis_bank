package ui;

import bus.*;
import dto.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;

public class CustomerDashboardPanel extends JPanel {

    private final BankApp     app;
    private final CustomerDTO customer;

    // Customer's accounts — loaded once, refreshed on demand
    private ArrayList<AccountDTO> myAccounts = new ArrayList<>();

    public CustomerDashboardPanel(BankApp app, CustomerDTO customer) {
        this.app      = app;
        this.customer = customer;
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        loadMyAccounts();

        // Top header bar
        add(buildHeader(), BorderLayout.NORTH);

        // Tab pane — one tab per feature
        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(new Font("SansSerif", Font.PLAIN, 13));
        tabs.addTab("My Accounts",    buildAccountsTab());
        tabs.addTab("New Transaction",buildTransactionTab());
        tabs.addTab("History",        buildHistoryTab());
        tabs.addTab("Request Account",buildRequestTab());
        tabs.addTab("My Profile",     buildProfileTab());
        add(tabs, BorderLayout.CENTER);
    }

    // ── Load only this customer's accounts ─────────────────
    private void loadMyAccounts() {
        try {
            ArrayList<AccountDTO> allAccounts = app.custSvc.getAccountListByCustomer(customer.getId());
            // Safety filter: ensure accounts belong to this customer
            myAccounts = new ArrayList<>();
            for (AccountDTO acc : allAccounts) {
                if (acc.getCustomer_id() != null && acc.getCustomer_id().equals(customer.getId())) {
                    myAccounts.add(acc);
                }
            }
        } catch (Exception ex) { 
        	err(ex.getMessage()); 
        }
    }

    // ── Header ─────────────────────────────────────────────
    private JPanel buildHeader() {
        JPanel h = new JPanel(new BorderLayout());
        h.setBackground(new Color(10, 30, 70));
        h.setBorder(BorderFactory.createEmptyBorder(14, 20, 14, 20));

        JLabel title = new JLabel("FORTIS BANK  |  Welcome, " + customer.getFirst_name() + " " + customer.getLast_name());
        title.setFont(new Font("SansSerif", Font.BOLD, 16));
        title.setForeground(Color.WHITE);

        JButton logout = new JButton("Log Out");
        logout.setBackground(new Color(180, 40, 40));
        logout.setForeground(Color.WHITE);
        logout.setFocusPainted(false);
        logout.setBorderPainted(false);
        logout.setOpaque(true);
        logout.setFont(new Font("SansSerif", Font.BOLD, 13));
        logout.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        logout.addActionListener(e -> app.logout());

        h.add(title,  BorderLayout.WEST);
        h.add(logout, BorderLayout.EAST);
        return h;
    }

    // ═══════════════════════════════════════════════════════
    // TAB 1 — My Accounts
    // ═══════════════════════════════════════════════════════
    private JPanel buildAccountsTab() {
        JPanel p = tab();

        sectionLabel(p, "Your Accounts");

        // Table
        String[] cols = {"Account ID", "Type", "Status", "Balance", "Opened"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable table = styledTable(model);
        populateAccountTable(model);

        JScrollPane scroll = new JScrollPane(table);
        scroll.setPreferredSize(new Dimension(0, 300));
        p.add(scroll);

        // Refresh button
        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.LEFT));
        btnRow.setOpaque(false);
        JButton refresh = blueBtn("↻  Refresh Accounts");
        refresh.addActionListener(e -> {
            loadMyAccounts();
            populateAccountTable(model);
        });
        btnRow.add(refresh);
        p.add(btnRow);

        // Balance check
        sectionLabel(p, "Check Account Balance");
        JPanel checkRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        checkRow.setOpaque(false);
        JComboBox<String> accBox = accountCombo();
        JLabel resultLabel = new JLabel("—");
        resultLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        resultLabel.setForeground(new Color(10, 30, 70));
        JButton checkBtn = blueBtn("Check Balance");
        checkBtn.addActionListener(e -> {
            if (myAccounts.isEmpty()) { err("No accounts found."); return; }
            AccountDTO selected = myAccounts.get(accBox.getSelectedIndex());
            try {
                double bal = app.custSvc.checkAccountBalance(selected.getId(), customer.getId());
                resultLabel.setText(String.format("$%.2f", bal));
            } catch (Exception ex) { err(ex.getMessage()); }
        });
        checkRow.add(new JLabel("Account:"));
        checkRow.add(accBox);
        checkRow.add(checkBtn);
        checkRow.add(resultLabel);
        p.add(checkRow);

        return p;
    }

    private void populateAccountTable(DefaultTableModel model) {
        model.setRowCount(0);
        for (AccountDTO a : myAccounts) {
            model.addRow(new Object[]{
                a.getId(),
                a.getType().getName(),
                a.getStatus().getName(),
                String.format("$%.2f", Double.parseDouble(a.getBalance())),
                a.getOpening_date()
            });
        }
    }

    // ═══════════════════════════════════════════════════════
    // TAB 2 — New Transaction
    // ═══════════════════════════════════════════════════════
    private JPanel buildTransactionTab() {
        JPanel p = tab();
        sectionLabel(p, "Make a Transaction");

        // Type selector
        JPanel row1 = formRow("Transaction Type:");
        JComboBox<String> typeBox = new JComboBox<>(new String[]{"Deposit", "Withdrawal", "Transfer"});
        typeBox.setFont(new Font("SansSerif", Font.PLAIN, 13));
        typeBox.setPreferredSize(new Dimension(220, 32));
        row1.add(typeBox);
        p.add(row1);

        // Source account
        JPanel row2 = formRow("Your Account:");
        JComboBox<String> srcBox = accountCombo();
        row2.add(srcBox);
        p.add(row2);

        // Target account id (for transfers)
        JPanel row3 = formRow("Target Account ID (Transfer only):");
        JTextField targetFld = plainField(120);
        row3.add(targetFld);
        p.add(row3);

        // Amount
        JPanel row4 = formRow("Amount ($):");
        JTextField amtFld = plainField(220);
        amtFld.setFont(new Font("SansSerif", Font.PLAIN, 15));
        row4.add(amtFld);
        p.add(row4);

        // Enable/disable target field based on type
        typeBox.addActionListener(e -> {
            boolean isTransfer = typeBox.getSelectedIndex() == 2;
            targetFld.setEnabled(isTransfer);
            targetFld.setBackground(isTransfer ? Color.WHITE : new Color(240, 240, 240));
        });
        targetFld.setEnabled(false);
        targetFld.setBackground(new Color(240, 240, 240));

        // Submit
        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.LEFT));
        btnRow.setOpaque(false);
        JButton submit = blueBtn("Submit Transaction");
        JLabel  result = new JLabel("");
        result.setFont(new Font("SansSerif", Font.BOLD, 13));

        submit.addActionListener(e -> {
            result.setText("");
            if (myAccounts.isEmpty()) { err("You have no accounts."); return; }
            String amtStr = amtFld.getText().trim();
            if (amtStr.isEmpty()) { err("Please enter an amount."); return; }

            double amount;
            try { amount = Double.parseDouble(amtStr); }
            catch (NumberFormatException ex) { err("Amount must be a number."); return; }

            int    typeIdx = typeBox.getSelectedIndex();
            String srcId   = myAccounts.get(srcBox.getSelectedIndex()).getId();
            String tgtId   = targetFld.getText().trim();

            if (typeIdx == 2 && tgtId.isEmpty()) { err("Enter a target account ID for transfers."); return; }

            try {
                // Build alert
                String msg = switch (typeIdx) {
                    case 0 -> String.format("Deposit of $%.2f completed successfully.", amount);
                    case 1 -> String.format("Withdrawal of $%.2f completed successfully.", amount);
                    default -> String.format("Transfer to account "+ tgtId+" of $%.2f completed successfully.", amount);
                };
                
                AlertType type = app.alrtTypeSvc.getById(1);
                
                AlertDTO tempAlert = new AlertDTO(null, msg, type, null);
                
                AlertDTO alert = app.alrtSvc.create(tempAlert);

                // Get transaction type by id
                TransactionType ttype = app.transTypeSvc.getById(typeIdx + 1);
                
                String source = (typeIdx == 0) ? null : srcId;
                String target = (typeIdx == 1) ? null : (typeIdx == 0 ? srcId : tgtId);

                TransactionDTO tx = new TransactionDTO(
                    null, ttype, amount, LocalDate.now(),
                    source, target, 0.0, alert);
                app.tranSvc.create(tx);

                // If checking — apply fee
                if (myAccounts.get(srcBox.getSelectedIndex()).getType().getId() == 1) {
                    CheckingAccountDTO chk = app.chkSvc.getById(Integer.parseInt(srcId));
                    double fee = app.chkSvc.applyTransactionFee(chk); //needs to modify balance in account
                    if (fee > 0)
                        result.setText("Transaction done. Fee applied: $" + String.format("%.2f", fee));
                    else
                        result.setText("Transaction done. Free transaction used.");
                } else {
                    result.setText("Transaction completed successfully!");
                }
                result.setForeground(new Color(20, 140, 60));
                amtFld.setText("");
                targetFld.setText("");
                loadMyAccounts();

            } catch (Exception ex) {
                result.setText("Error: " + ex.getMessage());
                result.setForeground(Color.RED);
            }
        });

        btnRow.add(submit);
        btnRow.add(Box.createHorizontalStrut(16));
        btnRow.add(result);
        p.add(Box.createVerticalStrut(8));
        p.add(btnRow);
        return p;
    }

    // ═══════════════════════════════════════════════════════
    // TAB 3 — Transaction History
    // ═══════════════════════════════════════════════════════
    private JPanel buildHistoryTab() {
        JPanel p = tab();
        sectionLabel(p, "Transaction History");

        JPanel selRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        selRow.setOpaque(false);
        // Only this customer's accounts
        JComboBox<String> accBox = accountCombo();
        JButton load = blueBtn("Load History");
        selRow.add(new JLabel("Select Account:"));
        selRow.add(accBox);
        selRow.add(load);
        p.add(selRow);
        p.add(Box.createVerticalStrut(10));

        String[] cols = {"ID", "Type", "Amount", "Date", "Source Acc", "Target Acc", "Fee"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable table = styledTable(model);
        JScrollPane scroll = new JScrollPane(table);
        scroll.setPreferredSize(new Dimension(0, 340));
        p.add(scroll);

        load.addActionListener(e -> {
            model.setRowCount(0);
            if (myAccounts.isEmpty()) { err("No accounts found."); return; }
            String accId = myAccounts.get(accBox.getSelectedIndex()).getId();
            try {
                ArrayList<TransactionDTO> list = app.tranSvc.getByAccount(accId);
                for (TransactionDTO t : list) {
                    model.addRow(new Object[]{
                        t.getId(),
                        t.getType().getName(),
                        String.format("$%.2f", t.getAmount()),
                        t.getTransaction_date(),
                        t.getSource_account_id() == null ? "—" : t.getSource_account_id(),
                        t.getTarget_account_id() == null ? "—" : t.getTarget_account_id(),
                        String.format("$%.2f", t.getFee())
                    });
                }
                if (model.getRowCount() == 0)
                    JOptionPane.showMessageDialog(this, "No transactions found for this account.");
            } catch (Exception ex) { err(ex.getMessage()); }
        });

        return p;
    }

    // ═══════════════════════════════════════════════════════
    // TAB 4 — Request Account
    // ═══════════════════════════════════════════════════════
    private JPanel buildRequestTab() {
        JPanel p = tab();
        sectionLabel(p, "Request a New Account");

        JPanel infoBox = new JPanel(new BorderLayout());
        infoBox.setBackground(new Color(230, 242, 255));
        infoBox.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 4, 0, 0, new Color(14, 120, 255)),
            BorderFactory.createEmptyBorder(12, 14, 12, 14)));
        infoBox.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));
        JLabel infoLbl = new JLabel("<html>Your bank manager will be notified and must approve the request.<br>" +
            "A checking account is required before requesting others.</html>");
        infoLbl.setFont(new Font("SansSerif", Font.PLAIN, 12));
        infoBox.add(infoLbl);
        p.add(infoBox);
        p.add(Box.createVerticalStrut(20));

        JPanel row = formRow("Account Type:");
        JComboBox<String> typeBox = new JComboBox<>(
            new String[]{"Savings", "Credit", "Currency", "Line of Credit"});
        typeBox.setFont(new Font("SansSerif", Font.PLAIN, 13));
        typeBox.setPreferredSize(new Dimension(220, 32));
        row.add(typeBox);
        p.add(row);
        p.add(Box.createVerticalStrut(12));

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.LEFT));
        btnRow.setOpaque(false);
        JButton submit = blueBtn("Submit Request");
        JLabel  status = new JLabel("");
        status.setFont(new Font("SansSerif", Font.BOLD, 13));

        submit.addActionListener(e -> {
            
            String[] typeNames = {"Savings", "Credit", "Line Of Credit", "Currency"};
            int idx = typeBox.getSelectedIndex();
            AccountType aType = new AccountType(idx, typeNames[idx], "");
            try {
            	
                String bmcId = app.custSvc.getBMCId(customer.getId());
                app.custSvc.requestAccountOpening(customer, aType, bmcId);
                status.setText("Request submitted! Your manager will review it.");
                status.setForeground(new Color(20, 140, 60));
            } catch (Exception ex) {
                status.setText("Error: " + ex.getMessage());
                status.setForeground(Color.RED);
            }
        });

        btnRow.add(submit);
        btnRow.add(Box.createHorizontalStrut(16));
        btnRow.add(status);
        p.add(btnRow);
        return p;
    }

    // ═══════════════════════════════════════════════════════
    // TAB 5 — My Profile
    // ═══════════════════════════════════════════════════════
    private JPanel buildProfileTab() {
        JPanel p = tab();
        sectionLabel(p, "My Profile");

        JTextField fnFld    = plainField(200); fnFld.setText(customer.getFirst_name());
        JTextField lnFld    = plainField(200); lnFld.setText(customer.getLast_name());
        JTextField emailFld = plainField(260); emailFld.setText(customer.getEmail());
        JTextField phoneFld = plainField(200); phoneFld.setText(customer.getPhone());

        // Read-only ID
        JPanel idRow = formRow("Customer ID:");
        JLabel idLbl = new JLabel("#" + customer.getId());
        idLbl.setFont(new Font("SansSerif", Font.BOLD, 13));
        idLbl.setForeground(new Color(10, 30, 70));
        idRow.add(idLbl);
        p.add(idRow);

        for (Object[] r : new Object[][]{
            {"First Name:", fnFld}, {"Last Name:", lnFld},
            {"Email:", emailFld},   {"Phone (xxx-xxx-xxxx):", phoneFld}
        }) {
            JPanel row = formRow((String) r[0]);
            row.add((JComponent) r[1]);
            p.add(row);
        }

        p.add(Box.createVerticalStrut(12));

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.LEFT));
        btnRow.setOpaque(false);
        JButton save = blueBtn("Save Changes");
        JLabel  msg  = new JLabel("");
        msg.setFont(new Font("SansSerif", Font.BOLD, 13));

        save.addActionListener(e -> {
            try {
                app.custSvc.updatePersonalInfo(
                    customer.getId(),
                    fnFld.getText().trim(),
                    lnFld.getText().trim(),
                    emailFld.getText().trim(),
                    phoneFld.getText().trim());
                msg.setText("Profile updated successfully!");
                msg.setForeground(new Color(20, 140, 60));
            } catch (Exception ex) {
                msg.setText("Error: " + ex.getMessage());
                msg.setForeground(Color.RED);
            }
        });

        btnRow.add(save);
        btnRow.add(Box.createHorizontalStrut(16));
        btnRow.add(msg);
        p.add(btnRow);
        return p;
    }

    // ═══════════════════════════════════════════════════════
    // UI Helpers
    // ═══════════════════════════════════════════════════════

    /** Base panel for every tab — vertical, padded, white */
    private JPanel tab() {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBackground(Color.WHITE);
        p.setBorder(BorderFactory.createEmptyBorder(24, 28, 24, 28));
        return p;
    }

    /** Bold section heading */
    private void sectionLabel(JPanel parent, String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("SansSerif", Font.BOLD, 15));
        l.setForeground(new Color(10, 30, 70));
        l.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        l.setAlignmentX(LEFT_ALIGNMENT);
        parent.add(l);
    }

    /** Single form row with a right-aligned label */
    private JPanel formRow(String label) {
        JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 4));
        row.setOpaque(false);
        row.setAlignmentX(LEFT_ALIGNMENT);
        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("SansSerif", Font.PLAIN, 13));
        lbl.setPreferredSize(new Dimension(230, 26));
        row.add(lbl);
        return row;
    }

    /** A text field with a visible border */
    private JTextField plainField(int width) {
        JTextField f = new JTextField();
        f.setFont(new Font("SansSerif", Font.PLAIN, 13));
        f.setPreferredSize(new Dimension(width, 30));
        f.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(180, 190, 210)),
            BorderFactory.createEmptyBorder(4, 8, 4, 8)));
        return f;
    }

    /**
     * Combo box showing ONLY this customer's accounts.
     * Format: "ID – Type (Balance)"
     */
    private JComboBox<String> accountCombo() {
        DefaultComboBoxModel<String> mdl = new DefaultComboBoxModel<>();
        for (AccountDTO a : myAccounts)
            mdl.addElement(a.getId() + " – " + a.getType().getName());
        JComboBox<String> box = new JComboBox<>(mdl);
        box.setFont(new Font("SansSerif", Font.PLAIN, 13));
        box.setPreferredSize(new Dimension(280, 30));
        box.setBackground(Color.WHITE);
        return box;
    }

    /** Blue-on-white styled button that always shows its color */
    private JButton blueBtn(String text) {
        JButton b = new JButton(text);
        b.setFont(new Font("SansSerif", Font.BOLD, 13));
        b.setBackground(new Color(14, 80, 200));
        b.setForeground(Color.WHITE);
        b.setOpaque(true);
        b.setBorderPainted(false);
        b.setFocusPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setPreferredSize(new Dimension(b.getPreferredSize().width + 20, 34));
        return b;
    }

    /** Table with alternating row colors and no editable cells */
    private JTable styledTable(DefaultTableModel model) {
        JTable t = new JTable(model) {
            public boolean isCellEditable(int r, int c) { return false; }
            public Component prepareRenderer(TableCellRenderer r, int row, int col) {
                Component c = super.prepareRenderer(r, row, col);
                c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(245, 247, 252));
                if (isRowSelected(row)) c.setBackground(new Color(210, 228, 255));
                return c;
            }
        };
        t.setFont(new Font("SansSerif", Font.PLAIN, 13));
        t.setRowHeight(30);
        t.setGridColor(new Color(230, 233, 240));
        t.setShowGrid(true);
        
        //new stuff
//        t.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 13));
//        t.getTableHeader().setBackground(new Color(10, 30, 70));
//        t.getTableHeader().setForeground(Color.WHITE);
//        t.getTableHeader().setPreferredSize(new Dimension(0, 36));
//        t.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
//        return t;
//    }
        
        t.getTableHeader().setDefaultRenderer(new ModernHeaderRenderer());
        t.getTableHeader().setPreferredSize(new Dimension(0, 36));
        
        t.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        return t;
    }

    private void err(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }
    
    
 // Custom renderer to force the header to show black text on a light background
    class ModernHeaderRenderer extends DefaultTableCellRenderer {
        public ModernHeaderRenderer() {
            setOpaque(true);
            // Using a light grey-blue so the black text pops
            setBackground(new Color(230, 235, 245)); 
            setForeground(Color.BLACK); 
            setFont(new Font("SansSerif", Font.BOLD, 13));
            setHorizontalAlignment(JLabel.CENTER);
            setBorder(BorderFactory.createMatteBorder(0, 0, 2, 1, new Color(200, 210, 230)));
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, 
                boolean isSelected, boolean hasFocus, int row, int column) {
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            return this;
        }
    }
}