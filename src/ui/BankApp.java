package ui;

import bus.*;
import dto.*;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.SQLException;

//── Main Application Frame ────────────────────────────────
public class BankApp extends JFrame {

// ── THEME ──────────────────────────────────────────────
 static final Color C_NAV     = new Color(10, 30, 70);
 static final Color C_ACCENT  = new Color(14, 120, 255);
 static final Color C_BG      = new Color(245, 247, 252);
 static final Color C_CARD    = Color.WHITE;
 static final Color C_SUCCESS = new Color(34, 197, 94);
 static final Color C_DANGER  = new Color(220, 53, 69);
 static final Color C_WARN    = new Color(255, 153, 0);
 static final Color C_MUTED   = new Color(108, 117, 125);
 static final Color C_BORDER  = new Color(222, 226, 230);
 static final Color C_TEXT    = new Color(33, 37, 41);

 static final Font F_DISPLAY = new Font("Segoe UI", Font.BOLD, 26);
 static final Font F_TITLE   = new Font("Segoe UI", Font.BOLD, 18);
 static final Font F_HEADER  = new Font("Segoe UI", Font.BOLD, 14);
 static final Font F_BODY    = new Font("Segoe UI", Font.PLAIN, 13);
 static final Font F_SMALL   = new Font("Segoe UI", Font.PLAIN, 11);
 static final Font F_MONO    = new Font("Segoe UI", Font.BOLD, 20);

 // ── SERVICES (shared across all panels) ─────────────────
 final CustomerService        custSvc  = new CustomerService();
 final BankManagerService     mgrSvc   = new BankManagerService();
 final AccountService         accSvc   = new AccountService();
 final CheckingAccountService chkSvc   = new CheckingAccountService();
 final SavingsAccountService  savSvc   = new SavingsAccountService();
 final CreditAccountService   creSvc   = new CreditAccountService();
 final LineOfCreditAccountService locSvc = new LineOfCreditAccountService();
 final CurrencyAccountService curSvc   = new CurrencyAccountService();
 final TransactionService     tranSvc  = new TransactionService();
 final TransactionTypeService transTypeSvc = new TransactionTypeService();
 final AlertService           alrtSvc  = new AlertService();
 final AlertTypeService		  alrtTypeSvc = new AlertTypeService();
 final ReportService          rptSvc   = new ReportService();
 final ReportTypeService      rptTypeSvc = new ReportTypeService();
 final AccountTypeService     accTypeSvc = new AccountTypeService();

 // ── ROUTING ─────────────────────────────────────────────
 private final CardLayout cardLayout = new CardLayout();
 private final JPanel     root       = new JPanel(cardLayout);

 // ── ENTRY ───────────────────────────────────────────────
 public static void main(String[] args) {
     SwingUtilities.invokeLater(() -> {
         try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); }
         catch (Exception ignored) {}
         new BankApp().setVisible(true);
     });
 }

 public BankApp() {
     super("Fortis Bank");
     setDefaultCloseOperation(EXIT_ON_CLOSE);
     setSize(1280, 800);
     setMinimumSize(new Dimension(1024, 680));
     setLocationRelativeTo(null);

     LoginPanel login = new LoginPanel(this);
     login.setName("LOGIN");
     root.add(login, "LOGIN");
     add(root);
     
     root.revalidate();
     cardLayout.show(root, "LOGIN");
 }

 void showCustomer(CustomerDTO c) {
     root.add(new CustomerDashboardPanel(this, c), "CUSTOMER");
     cardLayout.show(root, "CUSTOMER");
 }

 void showManager(BankManagerDTO m) {
     root.add(new ManagerDashboardPanel(this, m), "MANAGER");
     cardLayout.show(root, "MANAGER");
 }

 void logout() {
     for (Component c : root.getComponents())
         if (!"LOGIN".equals(c.getName())) root.remove(c);
     cardLayout.show(root, "LOGIN");
     root.revalidate(); root.repaint();
 }

 // ── SHARED UI BUILDERS ──────────────────────────────────

 static JButton accentBtn(String t) {
     JButton b = new JButton(t);
     b.setBackground(C_ACCENT); b.setForeground(Color.BLACK);
     b.setFont(F_HEADER); b.setFocusPainted(false); b.setOpaque(true);
//     b.setBorder(BorderFactory.createEmptyBorder(10, 22, 10, 22));
     b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
     Color hover = new Color(0, 90, 210);
     b.addMouseListener(new MouseAdapter() {
         public void mouseEntered(MouseEvent e) { b.setBackground(hover); }
         public void mouseExited(MouseEvent e)  { b.setBackground(C_ACCENT); }
     });
     return b;
 }

 static JButton dangerBtn(String t) {
     JButton b = accentBtn(t); b.setBackground(C_DANGER);
     Color hover = new Color(185, 30, 30);
     b.addMouseListener(new MouseAdapter() {
         public void mouseEntered(MouseEvent e) { b.setBackground(hover); }
         public void mouseExited(MouseEvent e)  { b.setBackground(C_DANGER); }
     });
     return b;
 }

 static JButton ghostBtn(String t) {
     JButton b = new JButton(t);
     b.setBackground(C_CARD); b.setForeground(C_NAV);
     b.setFont(F_BODY); b.setFocusPainted(false);
     b.setBorder(BorderFactory.createLineBorder(C_BORDER));
     b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
     return b;
 }

 static JTextField field(int cols) {
     JTextField f = new JTextField(cols); f.setFont(F_BODY);
     f.setBorder(BorderFactory.createCompoundBorder(
         BorderFactory.createLineBorder(C_BORDER),
         BorderFactory.createEmptyBorder(9, 12, 9, 12)));
     return f;
 }

 static JPasswordField passField() {
     JPasswordField f = new JPasswordField(20); f.setFont(F_BODY);
     f.setBorder(BorderFactory.createCompoundBorder(
         BorderFactory.createLineBorder(C_BORDER),
         BorderFactory.createEmptyBorder(9, 12, 9, 12)));
     return f;
 }

 static JLabel lbl(String text, Font f, Color c) {
     JLabel l = new JLabel(text); l.setFont(f); l.setForeground(c); return l;
 }

 static JTable styledTable(DefaultTableModel model) {
     JTable t = new JTable(model) {
         /**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public boolean isCellEditable(int r, int c) { return false; }
     };
     t.setFont(F_BODY); t.setRowHeight(38); t.setShowGrid(false);
     t.setIntercellSpacing(new Dimension(0, 0));
     t.getTableHeader().setFont(F_HEADER);
     t.getTableHeader().setBackground(new Color(248, 249, 252));
     t.getTableHeader().setForeground(C_MUTED);
     t.getTableHeader().setPreferredSize(new Dimension(0, 40));
     t.setSelectionBackground(new Color(14, 120, 255, 20));
     t.setSelectionForeground(C_TEXT);
     t.setBackground(C_CARD); t.setForeground(C_TEXT);
     return t;
 }

 static JScrollPane scrolled(JTable tbl) {
     JScrollPane sp = new JScrollPane(tbl);
     sp.setBorder(BorderFactory.createLineBorder(C_BORDER));
     sp.getViewport().setBackground(C_CARD);
     return sp;
 }

 static void err(Component p, String m) {
     JOptionPane.showMessageDialog(p, m, "Error", JOptionPane.ERROR_MESSAGE);
 }

 static void ok(Component p, String m) {
     JOptionPane.showMessageDialog(p, m, "Success", JOptionPane.INFORMATION_MESSAGE);
 }
}