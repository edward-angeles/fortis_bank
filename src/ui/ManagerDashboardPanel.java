package ui;

import bus.*;
import dto.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ManagerDashboardPanel extends JPanel {

    private final BankApp        app;
    private final BankManagerDTO manager;

    private final CardLayout pageLayout = new CardLayout();
    private final JPanel     pageArea   = new JPanel(pageLayout);

    private JButton navCustomers, navAccounts, navAlerts, navReports, navCreate;
    private JButton activeNav;

    public ManagerDashboardPanel(BankApp app, BankManagerDTO manager) {
        this.app     = app;
        this.manager = manager;
        setLayout(new BorderLayout());
        setBackground(BankApp.C_BG);
        add(buildNav(),  BorderLayout.WEST);
        add(buildMain(), BorderLayout.CENTER);
        showPage("CUSTOMERS");
    }

    // ── Sidebar ────────────────────────────────────────────
    private JPanel buildNav() {
        JPanel nav = new JPanel();
        nav.setLayout(new BoxLayout(nav, BoxLayout.Y_AXIS));
        nav.setBackground(BankApp.C_NAV);
        nav.setPreferredSize(new Dimension(220, 0));
        nav.setBorder(BorderFactory.createEmptyBorder(32, 0, 24, 0));

        JLabel brand = new JLabel("  \uD83C\uDFE6 FORTIS", SwingConstants.LEFT);
        brand.setFont(BankApp.F_TITLE);
        brand.setForeground(Color.WHITE);
        brand.setAlignmentX(LEFT_ALIGNMENT);
        brand.setBorder(BorderFactory.createEmptyBorder(0, 24, 8, 0));

        JLabel role = new JLabel("  Manager Portal", SwingConstants.LEFT);
        role.setFont(BankApp.F_SMALL);
        role.setForeground(new Color(255, 153, 0));
        role.setAlignmentX(LEFT_ALIGNMENT);
        role.setBorder(BorderFactory.createEmptyBorder(0, 24, 4, 0));

        JLabel greet = new JLabel("  " + manager.getFirstName() + " " + manager.getLastName());
        greet.setFont(BankApp.F_SMALL);
        greet.setForeground(new Color(160, 185, 220));
        greet.setAlignmentX(LEFT_ALIGNMENT);
        greet.setBorder(BorderFactory.createEmptyBorder(0, 24, 28, 0));

        navCustomers = navBtn("  \uD83D\uDC65  Customers",        "CUSTOMERS");
        navAccounts  = navBtn("  \uD83D\uDCB3  Accounts",         "ACCOUNTS");
        navAlerts    = navBtn("  \uD83D\uDD14  Alerts",           "ALERTS");
        navReports   = navBtn("  \uD83D\uDCC4  Reports",          "REPORTS");
        navCreate    = navBtn("  \u2795  Create Customer",        "CREATE");

        nav.add(brand); nav.add(role); nav.add(greet);
        for (JButton b : new JButton[]{navCustomers, navAccounts, navAlerts, navReports, navCreate})
            nav.add(b);

        nav.add(Box.createVerticalGlue());

        JButton logout = new JButton("  \u2190  Log Out");
        logout.setFont(BankApp.F_BODY);
        logout.setForeground(new Color(200, 80, 80));
        logout.setBackground(BankApp.C_NAV);
        logout.setBorderPainted(false); logout.setFocusPainted(false);
        logout.setOpaque(true); logout.setAlignmentX(LEFT_ALIGNMENT);
        logout.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        logout.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        logout.addActionListener(e -> app.logout());
        nav.add(logout);
        return nav;
    }

    private JButton navBtn(String label, String page) {
        JButton b = new JButton(label);
        b.setFont(BankApp.F_BODY);
        b.setForeground(new Color(180, 200, 230));
        b.setBackground(BankApp.C_NAV);
        b.setBorderPainted(false); b.setFocusPainted(false);
        b.setOpaque(true); b.setHorizontalAlignment(SwingConstants.LEFT);
        b.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        b.setAlignmentX(LEFT_ALIGNMENT);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.addActionListener(e -> { setActiveNav(b); showPage(page); });
        return b;
    }

    private void setActiveNav(JButton b) {
        if (activeNav != null) {
            activeNav.setBackground(BankApp.C_NAV);
            activeNav.setForeground(new Color(180, 200, 230));
        }
        b.setBackground(new Color(14, 120, 255, 60));
        b.setForeground(Color.WHITE);
        activeNav = b;
    }

    // ── Pages ──────────────────────────────────────────────
    private JPanel buildMain() {
        pageArea.setBackground(BankApp.C_BG);
        pageArea.add(buildCustomersPage(), "CUSTOMERS");
        pageArea.add(buildAccountsPage(),  "ACCOUNTS");
        pageArea.add(buildAlertsPage(),    "ALERTS");
        pageArea.add(buildReportsPage(),   "REPORTS");
        pageArea.add(buildCreatePage(),    "CREATE");
        return pageArea;
    }

    private void showPage(String name) {
        pageLayout.show(pageArea, name);
        switch (name) {
            case "CUSTOMERS" -> setActiveNav(navCustomers);
            case "ACCOUNTS"  -> setActiveNav(navAccounts);
            case "ALERTS"    -> setActiveNav(navAlerts);
            case "REPORTS"   -> setActiveNav(navReports);
            case "CREATE"    -> setActiveNav(navCreate);
        }
    }

    // ── PAGE: Customers ─────────────────────────────────────
    private JPanel buildCustomersPage() {
        JPanel p = page("Customer Management");

        String[] cols = {"ID", "First Name", "Last Name", "Email", "Phone"};
        DefaultTableModel mdl = new DefaultTableModel(cols, 0);
        JTable tbl = BankApp.styledTable(mdl);

        // Action buttons
        JButton loadAll  = BankApp.accentBtn("Load All Customers");
        JButton loadMine = BankApp.ghostBtn("My Customers");
        JButton blockBtn = BankApp.dangerBtn("Block Selected Account");
        JButton closeBtn = BankApp.dangerBtn("Close Selected Account");
        JButton assignBtn = BankApp.ghostBtn("Assign to Me");

        loadAll.addActionListener(e -> {
            mdl.setRowCount(0);
            try {
                for (CustomerDTO c : app.custSvc.getAll())
                    mdl.addRow(new Object[]{c.getId(), c.getFirst_name(), c.getLast_name(), c.getEmail(), c.getPhone()});
            } catch (Exception ex) { BankApp.err(this, ex.getMessage()); }
        });

        loadMine.addActionListener(e -> {
            mdl.setRowCount(0);
            try {
                List<CustomerDTO> list = app.mgrSvc.getCustomersByManagerId(Integer.parseInt(manager.getManagerId()));
                for (CustomerDTO c : list)
                    mdl.addRow(new Object[]{c.getId(), c.getFirst_name(), c.getLast_name(), c.getEmail(), c.getPhone()});
            } catch (Exception ex) { BankApp.err(this, ex.getMessage()); }
        });

        assignBtn.addActionListener(e -> {
            int row = tbl.getSelectedRow();
            if (row < 0) { BankApp.err(this, "Select a customer first."); return; }
            String custId = mdl.getValueAt(row, 0).toString();
            try {
                app.mgrSvc.assignCustomer(manager.getManagerId(), custId);
                BankApp.ok(this, "Customer assigned successfully!");
            } catch (Exception ex) { BankApp.err(this, ex.getMessage()); }
        });

        blockBtn.addActionListener(e -> {
            int row = tbl.getSelectedRow();
            if (row < 0) { BankApp.err(this, "Select a customer first."); return; }
            String custId = mdl.getValueAt(row, 0).toString();
            String accId  = JOptionPane.showInputDialog(this, "Enter Account ID to block:");
            if (accId == null || accId.isBlank()) return;
            try {
                app.accSvc.blockAccount(Integer.parseInt(accId));
                BankApp.ok(this, "Account blocked.");
            } catch (Exception ex) { BankApp.err(this, ex.getMessage()); }
        });

        closeBtn.addActionListener(e -> {
            int row = tbl.getSelectedRow();
            if (row < 0) { BankApp.err(this, "Select a customer first."); return; }
            String accId = JOptionPane.showInputDialog(this, "Enter Account ID to close:");
            if (accId == null || accId.isBlank()) return;
            try {
                app.accSvc.closeAccount(Integer.parseInt(accId));
                BankApp.ok(this, "Account closed.");
            } catch (Exception ex) { BankApp.err(this, ex.getMessage()); }
        });

        JPanel btnRow = hRow(loadAll, loadMine, assignBtn);
        JPanel actRow = hRow(blockBtn, closeBtn);

        p.add(btnRow);
        p.add(Box.createVerticalStrut(12));
        p.add(BankApp.scrolled(tbl));
        p.add(Box.createVerticalStrut(12));
        p.add(infoLabel("Select a row, then use the action buttons below:"));
        p.add(Box.createVerticalStrut(6));
        p.add(actRow);
        return p;
    }

    // ── PAGE: Accounts ─────────────────────────────────────
    private JPanel buildAccountsPage() {
        JPanel p = page("Account Management");

        JTextField custIdFld = BankApp.field(10);
        JButton    search    = BankApp.accentBtn("Search");

        String[] cols = {"ID", "Type", "Status", "Balance", "Opened"};
        DefaultTableModel mdl = new DefaultTableModel(cols, 0);
        JTable tbl = BankApp.styledTable(mdl);

        // Open account for customer
        JButton openChk = BankApp.accentBtn("Open Checking");
        JButton openSav = BankApp.ghostBtn("Open Savings");
        JButton openCre = BankApp.ghostBtn("Open Credit");
        JButton openLoc = BankApp.ghostBtn("Open Line of Credit");
        JButton openCur = BankApp.ghostBtn("Open Currency");

        search.addActionListener(e -> {
            mdl.setRowCount(0);
            try {
                List<AccountDTO> list = app.accSvc.getAccountsByCustomerId(Integer.parseInt(custIdFld.getText().trim()));
                for (AccountDTO a : list)
                    mdl.addRow(new Object[]{
                        a.getId(), a.getType().getName(), a.getStatus().getName(),
                        String.format("$%.2f", Double.parseDouble(a.getBalance())),
                        a.getOpening_date()
                    });
            } catch (Exception ex) { BankApp.err(this, ex.getMessage()); }
        });

        openChk.addActionListener(e -> openAccount(custIdFld.getText().trim(), 1));
        openSav.addActionListener(e -> openAccount(custIdFld.getText().trim(), 2));
        openCre.addActionListener(e -> openAccount(custIdFld.getText().trim(), 3));
        openLoc.addActionListener(e -> openAccount(custIdFld.getText().trim(), 4));
        openCur.addActionListener(e -> openAccount(custIdFld.getText().trim(), 5));

        JPanel searchRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        searchRow.setOpaque(false);
        searchRow.add(new JLabel("Customer ID:"));
        searchRow.add(custIdFld);
        searchRow.add(search);

        JPanel openRow = hRow(openChk, openSav, openCre, openLoc, openCur);

        p.add(searchRow);
        p.add(Box.createVerticalStrut(16));
        p.add(BankApp.scrolled(tbl));
        p.add(Box.createVerticalStrut(16));
        p.add(infoLabel("Open a new account for the customer ID above:"));
        p.add(Box.createVerticalStrut(8));
        p.add(openRow);
        return p;
    }

    private void openAccount(String custIdStr, int typeId) {
        if (custIdStr.isBlank()) { BankApp.err(this, "Enter a Customer ID first."); return; }
        try {
            CustomerDTO cust = app.custSvc.getById(custIdStr);
            AccountStatus open = new AccountStatus(1, "open", null);
            AccountType   type = new AccountType(typeId, "", null);

            if (typeId == 1) {
                CheckingAccountDTO dto = new CheckingAccountDTO(
                    "0", custIdStr, open, type, "0.00", "2.50", LocalDate.now());
                app.chkSvc.create(dto);
            } else if (typeId == 2) {
                String rate = JOptionPane.showInputDialog(this, "Annual interest rate (%):");
                if (rate == null) return;
                SavingsAccountDTO dto = new SavingsAccountDTO("0", custIdStr, open, type, "0.00", null, rate);
                app.savSvc.create(dto);
            } else if (typeId == 3) {
                String limit = JOptionPane.showInputDialog(this, "Credit limit ($):");
                String rate  = JOptionPane.showInputDialog(this, "Annual interest rate (%):");
                if (limit == null || rate == null) return;
                CreditAccountDTO dto = new CreditAccountDTO("0", custIdStr, open, type, "0.00",
                    limit, rate, "50.00", LocalDate.now().plusMonths(1));
                app.creSvc.create(dto);
            } else if (typeId == 4) {
                String limit  = JOptionPane.showInputDialog(this, "Credit limit ($):");
                String rate   = JOptionPane.showInputDialog(this, "Annual interest rate (%):");
                String install = JOptionPane.showInputDialog(this, "Number of installments:");
                if (limit == null || rate == null || install == null) return;
                LineOfCreditDTO dto = new LineOfCreditDTO("0", custIdStr, open, type, "0.00", limit, rate, install);
                app.locSvc.create(dto);
            } else if (typeId == 5) {
                String currency = JOptionPane.showInputDialog(this, "Currency code (e.g. EUR, GBP):");
                String rate     = JOptionPane.showInputDialog(this, "Exchange rate:");
                if (currency == null || rate == null) return;
                CurrencyAccountDTO dto = new CurrencyAccountDTO("0", custIdStr, open, type, "0.00", null, currency, rate);
                app.curSvc.create(dto);
            }
            BankApp.ok(this, "Account opened successfully!");
        } catch (Exception ex) { BankApp.err(this, ex.getMessage()); }
    }

    // ── PAGE: Alerts ────────────────────────────────────────
    private JPanel buildAlertsPage() {
        JPanel p = page("Alerts");

        String[] cols = {"ID", "Message", "Type", "BMC ID"};
        DefaultTableModel mdl = new DefaultTableModel(cols, 0);
        JTable tbl = BankApp.styledTable(mdl);

        JButton load = BankApp.accentBtn("Load All Alerts");
        JButton send = BankApp.ghostBtn("Send System Alert");

        load.addActionListener(e -> {
            mdl.setRowCount(0);
            ArrayList<AlertDTO> list = app.alrtSvc.getAll();
            for (AlertDTO a : list)
                mdl.addRow(new Object[]{
                    a.getId(), a.getMessage(),
                    a.getAlertType() != null ? a.getAlertType().getName() : "—",
                    a.getBmc_id() != null ? a.getBmc_id() : "—"
                });
        });

        send.addActionListener(e -> {
            String msg = JOptionPane.showInputDialog(this, "Alert message:");
            if (msg == null || msg.isBlank()) return;
            String[] types = {"success", "low balance", "unusual activity"};
            int choice = JOptionPane.showOptionDialog(this, "Select alert type:", "Type",
                0, JOptionPane.QUESTION_MESSAGE, null, types, types[0]);
            if (choice < 0) return;
            try {
                app.alrtSvc.sendSystemAlert(msg, choice + 1);
                BankApp.ok(this, "Alert sent.");
            } catch (Exception ex) { BankApp.err(this, ex.getMessage()); }
        });

        p.add(hRow(load, send));
        p.add(Box.createVerticalStrut(16));
        p.add(BankApp.scrolled(tbl));
        return p;
    }

    // ── PAGE: Reports ───────────────────────────────────────
    private JPanel buildReportsPage() {
        JPanel p = page("Financial Reports");

        String[] cols = {"ID", "Type", "Generated At", "Author", "Recipient", "Content"};
        DefaultTableModel mdl = new DefaultTableModel(cols, 0);
        JTable tbl = BankApp.styledTable(mdl);

        JButton load   = BankApp.accentBtn("Load All Reports");
        JButton create = BankApp.ghostBtn("Create Report");
        JButton delete = BankApp.dangerBtn("Delete Selected");

        load.addActionListener(e -> {
            mdl.setRowCount(0);
            try {
                for (ReportDTO r : app.rptSvc.getAll())
                    mdl.addRow(new Object[]{
                        r.getReportId(),
                        r.getType() != null ? r.getType().getName() : "—",
                        r.getGeneratedAt(),
                        r.getManagerId(),
                        r.getCustomerId(),
                        r.getReportContent().length() > 60
                            ? r.getReportContent().substring(0, 60) + "..." : r.getReportContent()
                    });
            } catch (Exception ex) { BankApp.err(this, ex.getMessage()); }
        });

        create.addActionListener(e -> {
            String custId  = JOptionPane.showInputDialog(this, "Recipient Customer ID:");
            if (custId == null || custId.isBlank()) return;
            String content = JOptionPane.showInputDialog(this, "Report content:");
            if (content == null || content.isBlank()) return;
            String[] typeNames = {"daily", "monthly", "yearly"};
            int typeIdx = JOptionPane.showOptionDialog(this, "Report type:", "Type",
                0, JOptionPane.QUESTION_MESSAGE, null, typeNames, typeNames[1]);
            if (typeIdx < 0) return;
            try {
                ReportType rt = new ReportType(typeIdx + 1, typeNames[typeIdx], "");
                ReportDTO r = new ReportDTO(null, null, rt, content, manager.getManagerId(), custId);
                app.rptSvc.create(r);
                BankApp.ok(this, "Report created.");
            } catch (Exception ex) { BankApp.err(this, ex.getMessage()); }
        });

        delete.addActionListener(e -> {
            int row = tbl.getSelectedRow();
            if (row < 0) { BankApp.err(this, "Select a report first."); return; }
            String id = mdl.getValueAt(row, 0).toString();
            try {
                app.rptSvc.delete(id);
                mdl.removeRow(row);
                BankApp.ok(this, "Report deleted.");
            } catch (Exception ex) { BankApp.err(this, ex.getMessage()); }
        });

        p.add(hRow(load, create, delete));
        p.add(Box.createVerticalStrut(16));
        p.add(BankApp.scrolled(tbl));
        return p;
    }

    // ── PAGE: Create Customer ───────────────────────────────
    private JPanel buildCreatePage() {
        JPanel p = page("Create New Customer");

        JTextField fnFld    = BankApp.field(18);
        JTextField lnFld    = BankApp.field(18);
        JTextField emailFld = BankApp.field(24);
        JTextField phoneFld = BankApp.field(14);
        JTextField pinFld   = BankApp.field(6);

        JPanel form = new JPanel();
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setOpaque(false);
        form.setAlignmentX(LEFT_ALIGNMENT);

        for (Object[] row : new Object[][]{
            {"First Name:", fnFld}, {"Last Name:", lnFld},
            {"Email:", emailFld}, {"Phone (xxx-xxx-xxxx):", phoneFld},
            {"PIN (4 digits):", pinFld}
        }) {
            JPanel r = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 4));
            r.setOpaque(false);
            JLabel lbl = new JLabel((String) row[0]);
            lbl.setFont(BankApp.F_BODY);
            lbl.setPreferredSize(new Dimension(200, 24));
            r.add(lbl);
            r.add((JComponent) row[1]);
            r.setAlignmentX(LEFT_ALIGNMENT);
            r.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
            form.add(r);
        }

        JButton submit = BankApp.accentBtn("Create Customer + Checking Account");
        submit.addActionListener(e -> {
            try {
                CustomerDTO dto = new CustomerDTO(
                    "0",
                    fnFld.getText().trim(),
                    lnFld.getText().trim(),
                    pinFld.getText().trim(),
                    emailFld.getText().trim(),
                    phoneFld.getText().trim()
                );
                int result = app.custSvc.create(dto);
                if (result > 0) {
                    // Auto-assign to this manager and open mandatory checking account
                    app.mgrSvc.assignCustomer(manager.getManagerId(), dto.getId());
                    AccountStatus open = new AccountStatus(1, "open", null);
                    AccountType   chkType = new AccountType(1, "checking", null);
                    CheckingAccountDTO chk = new CheckingAccountDTO(
                        "0", dto.getId(), open, chkType, "0.00", "2.50", LocalDate.now());
                    app.chkSvc.create(chk);
                    BankApp.ok(this, "Customer created and checking account opened!\nCustomer ID: " + dto.getId());
                    for (JTextField f : new JTextField[]{fnFld, lnFld, emailFld, phoneFld, pinFld})
                        f.setText("");
                }
            } catch (Exception ex) { BankApp.err(this, ex.getMessage()); }
        });

        JPanel note = infoBox(
            "\u2139 Creating a customer automatically opens a checking account\n" +
            "and assigns them to you as their bank manager.");

        p.add(note);
        p.add(Box.createVerticalStrut(20));
        p.add(form);
        p.add(Box.createVerticalStrut(20));
        p.add(submit);
        return p;
    }

    // ── Helpers ─────────────────────────────────────────────

    private JPanel page(String title) {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBackground(BankApp.C_BG);
        p.setBorder(BorderFactory.createEmptyBorder(36, 36, 36, 36));

        JLabel lbl = new JLabel(title);
        lbl.setFont(BankApp.F_DISPLAY);
        lbl.setForeground(BankApp.C_NAV);
        lbl.setAlignmentX(LEFT_ALIGNMENT);
        lbl.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        p.add(lbl);
        return p;
    }

    private JPanel hRow(JButton... btns) {
        JPanel r = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        r.setOpaque(false);
        r.setAlignmentX(LEFT_ALIGNMENT);
        for (JButton b : btns) r.add(b);
        return r;
    }

    private JLabel infoLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(BankApp.F_SMALL);
        l.setForeground(BankApp.C_MUTED);
        l.setAlignmentX(LEFT_ALIGNMENT);
        return l;
    }

    private JPanel infoBox(String text) {
        JPanel b = new JPanel(new BorderLayout());
        b.setBackground(new Color(255, 245, 210));
        b.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 4, 0, 0, BankApp.C_WARN),
            BorderFactory.createEmptyBorder(14, 14, 14, 14)));
        b.setMaximumSize(new Dimension(Integer.MAX_VALUE, 70));
        JLabel lbl = new JLabel("<html>" + text.replace("\n", "<br>") + "</html>");
        lbl.setFont(BankApp.F_SMALL);
        lbl.setForeground(new Color(100, 60, 0));
        b.add(lbl);
        return b;
    }
}