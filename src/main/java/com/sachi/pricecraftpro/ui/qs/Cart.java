package com.sachi.pricecraftpro.ui.qs;

import com.sachi.pricecraftpro.helper.DBConnection;
import com.sachi.pricecraftpro.helper.EmailSender;
import com.sachi.pricecraftpro.ui.Loading;
import com.sachi.pricecraftpro.ui.LogIn;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

public class Cart extends javax.swing.JFrame {

    /**
     * Creates new form Cart
     */
    public Cart() {
        initComponents();
        startup();
    }

    public static String id = null;
    Connection conn;
    JMenuItem[] item = null;
    int i = 0;

    private void startup() {
        Loading l = new Loading();
        l.setVisible(true);

        new Thread(() -> {
            panelOperations(false);

            try {
                openConn();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT name "
                        + "FROM category");
                while (rs.next()) {
                    jComboBox1.addItem(rs.getString(1));
                }
            } catch (SQLException ex) {
                Logger.getLogger(Cart.class.getName())
                        .log(Level.SEVERE, null, ex);
            } finally {
                closeConn();
            }

            firstTableFill();

            addMenuItems();

            l.dispose();
        }).start();
    }

    private void firstTableFill() {
        DefaultTableModel model = (DefaultTableModel) jTable2.getModel();
        model.setRowCount(0);

        try {
            openConn();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * "
                    + "FROM material");
            while (rs.next()) {
                Statement stmt0 = conn.createStatement();
                ResultSet rs0 = stmt0.executeQuery("SELECT name "
                        + "FROM category "
                        + "WHERE id = '" + rs.getString(4) + "'");
                while (rs0.next()) {
                    Object[] row = {rs.getString(1), rs.getString(2),
                        rs.getString(3), rs0.getString(1)};
                    model.addRow(row);
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(Cart.class.getName())
                    .log(Level.SEVERE, null, ex);
        } finally {
            closeConn();
        }
    }

    private void addMenuItems() {
        try {
            openConn();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT DISTINCT COUNT(plan) "
                    + "FROM cart "
                    + "WHERE customer = '" + id + "'");
            while (rs.next()) {
                item = new JMenuItem[rs.getInt(1)];
                Statement stmt0 = conn.createStatement();
                ResultSet rs0 = stmt0.executeQuery("SELECT DISTINCT plan "
                        + "FROM cart "
                        + "WHERE customer = '" + id + "'");
                i = 0;
                while (rs0.next()) {
                    item[i].setIcon(new ImageIcon(getClass()
                            .getResource("/icons/description_FILL0_wght200_GRAD0_opsz20.png")));
                    item[i].setText(rs0.getString(1));
                    item[i].addActionListener((ActionEvent evt) -> {
                        menuItemOperations();
                    });
                    jMenu3.add(item[i]);
                    i++;
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(Cart.class.getName())
                    .log(Level.SEVERE, null, ex);
        } finally {
            closeConn();
        }
    }

    private void panelOperations(boolean option) {
        for (Component com : jPanel1.getComponents()) {
            com.setEnabled(option);
        }
        jButton3.setEnabled(option);
        jButton1.setEnabled(option);

        jButton4.setEnabled(false);
        jButton5.setEnabled(false);
    }

    private void menuItemOperations() {
        for (int j = 0; j <= i; j++) {
            if (item[j].isSelected()) {
                String itemName = item[j].getText();
                new Thread(() -> {
                    Loading l = new Loading();
                    l.setVisible(true);

                    jLabel4.setText(itemName);
                    DefaultTableModel model = (DefaultTableModel) jTable3.getModel();
                    model.setRowCount(0);
                    try {
                        openConn();
                        Statement stmt = conn.createStatement();
                        ResultSet rs = stmt.executeQuery("SELECT material, units "
                                + "FROM cart "
                                + "WHERE customer = '" + id + "' "
                                + "AND plan = '" + itemName + "'");
                        while (rs.next()) {
                            Statement stmt0 = conn.createStatement();
                            ResultSet rs0 = stmt0.executeQuery("SELECT id, name, price, category "
                                    + "FROM material "
                                    + "WHERE id = '" + rs.getString(1) + "'");
                            while (rs0.next()) {
                                Statement stmt1 = conn.createStatement();
                                ResultSet rs1 = stmt1.executeQuery("SELECT name "
                                        + "FROM category "
                                        + "WHERE id = '" + rs0.getString(4) + "'");
                                while (rs1.next()) {
                                    Object[] row = {rs0.getString(1), rs0.getString(2),
                                        rs.getString(2), rs.getInt(2) * rs0.getInt(3),
                                        rs1.getString(1)};
                                    model.addRow(row);
                                }
                            }
                        }
                    } catch (SQLException ex) {
                        Logger.getLogger(Cart.class.getName())
                                .log(Level.SEVERE, null, ex);
                    } finally {
                        closeConn();
                    }

                    panelOperations(true);

                    l.dispose();
                }).start();
                break;
            }
        }
    }

    private void openConn() {
        conn = new DBConnection().CONN();
    }

    private void closeConn() {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException ex) {
                Logger.getLogger(Cart.class.getName())
                        .log(Level.SEVERE, null, ex);
            }
        }
    }

    private synchronized void detailsWritterX() {
        jProgressBar1.setIndeterminate(true);

        String details;
        // basic details
        details = "QS Name : \t" + LogIn.name + "\n";
        details += "Estimate ID : \t" + jLabel4.getText() + "\n\n";

        DefaultTableModel model = (DefaultTableModel) jTable3.getModel();
        int subTotal = 0;
        for (int k = 1; k <= jComboBox1.getItemCount(); k++) {
            details += jComboBox1.getItemAt(k) + " : \n";
            int categoryTotal = 0;
            for (int j = 0; j < model.getRowCount(); j++) {
                if (model.getValueAt(j, 4).equals(jComboBox1.getItemAt(k))) {
                    details += "|- " + model.getValueAt(j, 1) + "\t["
                            + model.getValueAt(j, 2)
                            + "]\t" + model.getValueAt(j, 3) + "\n";
                    categoryTotal += Integer.parseInt((String) model.getValueAt(j, 3));
                }
            }
            details += "[Total : " + categoryTotal + "]\n\n";
            subTotal += categoryTotal;
        }
        details += "[Sub Total : " + subTotal + "]";

        jTextArea1.setText(details);

        jProgressBar1.setIndeterminate(false);
    }

    private void detailsWritter() {
        new Thread(() -> {
            detailsWritterX();
        }).start();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTable2 = new javax.swing.JTable();
        jLabel1 = new javax.swing.JLabel();
        jComboBox1 = new javax.swing.JComboBox<>();
        jButton4 = new javax.swing.JButton();
        jButton5 = new javax.swing.JButton();
        jScrollPane3 = new javax.swing.JScrollPane();
        jTable3 = new javax.swing.JTable();
        jLabel5 = new javax.swing.JLabel();
        jTextField1 = new javax.swing.JTextField();
        jButton2 = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();
        jProgressBar1 = new javax.swing.JProgressBar();
        jPanel3 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        jMenu3 = new javax.swing.JMenu();
        jMenuItem1 = new javax.swing.JMenuItem();
        jMenu2 = new javax.swing.JMenu();
        jMenuItem2 = new javax.swing.JMenuItem();
        jMenu4 = new javax.swing.JMenu();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Cart");

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Estimate Editor : "));

        jTable2.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "ID", "Name", "Price", "Type"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTable2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTable2MouseClicked(evt);
            }
        });
        jScrollPane2.setViewportView(jTable2);
        if (jTable2.getColumnModel().getColumnCount() > 0) {
            jTable2.getColumnModel().getColumn(0).setMinWidth(40);
            jTable2.getColumnModel().getColumn(0).setPreferredWidth(40);
            jTable2.getColumnModel().getColumn(0).setMaxWidth(40);
        }

        jLabel1.setText("Material Type : ");

        jComboBox1.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "All" }));
        jComboBox1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBox1ActionPerformed(evt);
            }
        });

        jButton4.setText("+");
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });

        jButton5.setText("-");
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });

        jTable3.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID", "Name", "Units", "Price", "Type"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTable3.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTable3MouseClicked(evt);
            }
        });
        jScrollPane3.setViewportView(jTable3);
        if (jTable3.getColumnModel().getColumnCount() > 0) {
            jTable3.getColumnModel().getColumn(0).setMinWidth(40);
            jTable3.getColumnModel().getColumn(0).setPreferredWidth(40);
            jTable3.getColumnModel().getColumn(0).setMaxWidth(40);
        }

        jLabel5.setText("Material Name : ");

        jTextField1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField1ActionPerformed(evt);
            }
        });

        jButton2.setText("Edit units");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTextField1))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, 308, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 400, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jButton5, javax.swing.GroupLayout.DEFAULT_SIZE, 84, Short.MAX_VALUE)
                            .addComponent(jButton4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane3))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jButton2)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton2))
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(159, 159, 159)
                        .addComponent(jButton4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton5)
                        .addContainerGap(182, Short.MAX_VALUE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel5)
                                    .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                            .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                        .addContainerGap())))
        );

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder("Detailed View : "));

        jTextArea1.setEditable(false);
        jTextArea1.setColumns(20);
        jTextArea1.setLineWrap(true);
        jTextArea1.setRows(5);
        jTextArea1.setWrapStyleWord(true);
        jScrollPane1.setViewportView(jTextArea1);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 267, Short.MAX_VALUE)
                    .addComponent(jProgressBar1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jProgressBar1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jPanel3.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel3.setText("Estimate Name : ");

        jLabel4.setText("---");

        jButton1.setText("Edit");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(jLabel4)
                    .addComponent(jButton1))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jButton3.setText("Save");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        jMenu1.setText("File");

        jMenu3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/folder_open_FILL0_wght100_GRAD200_opsz48.png"))); // NOI18N
        jMenu3.setText("Open Estimate");
        jMenu1.add(jMenu3);

        jMenuItem1.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_N, java.awt.event.InputEvent.CTRL_DOWN_MASK));
        jMenuItem1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/add_FILL0_wght200_GRAD0_opsz20.png"))); // NOI18N
        jMenuItem1.setText("New Estimate");
        jMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem1ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem1);

        jMenuBar1.add(jMenu1);

        jMenu2.setText("Options");

        jMenuItem2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/mail_FILL0_wght200_GRAD0_opsz20.png"))); // NOI18N
        jMenuItem2.setText("Email to customer");
        jMenuItem2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem2ActionPerformed(evt);
            }
        });
        jMenu2.add(jMenuItem2);

        jMenuBar1.add(jMenu2);

        jMenu4.setText("Exit");
        jMenu4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenu4ActionPerformed(evt);
            }
        });
        jMenuBar1.add(jMenu4);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButton3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton3))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void jMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem1ActionPerformed
        String name = JOptionPane.showInputDialog(this, "Enter name : ", "Estimate");
        try {
            openConn();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT COUNT(id) "
                    + "FROM cart "
                    + "WHERE plan = '" + name + "' "
                    + "AND customer = '" + id + "'");
            while (rs.next()) {
                if (rs.getInt(1) != 0) {
                    JOptionPane.showMessageDialog(this,
                            "Name already in use. Please use another.");
                } else {
                    panelOperations(true);
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(Cart.class.getName())
                    .log(Level.SEVERE, null, ex);
        } finally {
            closeConn();
        }
        detailsWritter();
    }//GEN-LAST:event_jMenuItem1ActionPerformed

    private void jMenu4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenu4ActionPerformed
        new Customer().setVisible(true);
        this.dispose();
    }//GEN-LAST:event_jMenu4ActionPerformed

    private void jMenuItem2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem2ActionPerformed
        Loading l = new Loading();
        l.setVisible(true);
        panelOperations(false);

        new Thread(() -> {
            EmailSender email = new EmailSender();

            try {
                openConn();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT email "
                        + "FROM customer "
                        + "WHERE id = '" + id + "'");
                while (rs.next()) {
                    email.setTo(rs.getString(1));
                }
            } catch (SQLException ex) {
                Logger.getLogger(Cart.class.getName())
                        .log(Level.SEVERE, null, ex);
            } finally {
                closeConn();
            }

            try {
                openConn();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT email, key "
                        + "FROM login "
                        + "WHERE id = '" + LogIn.id + "'");
                while (rs.next()) {
                    email.setFrom(rs.getString(1));
                    email.setKey(rs.getString(2));
                }
            } catch (SQLException ex) {
                Logger.getLogger(Cart.class.getName())
                        .log(Level.SEVERE, null, ex);
            } finally {
                closeConn();
            }

            email.setSubject("Building estimate - " + jLabel4.getText());
            email.setBody(jTextArea1.getText());
            boolean status = email.sendMail();

            l.dispose();

            JOptionPane.showMessageDialog(this, status ? "Success!" : "Error occurred!");

            panelOperations(true);
        }).start();
    }//GEN-LAST:event_jMenuItem2ActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        String name = JOptionPane.showInputDialog(this, "Enter new name : ", "Estimate");
        try {
            openConn();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT COUNT(id) "
                    + "FROM cart "
                    + "WHERE plan = '" + name + "' "
                    + "AND customer = '" + id + "'");
            while (rs.next()) {
                if (rs.getInt(1) == 0) {
                    Statement stmt0 = conn.createStatement();
                    stmt0.executeUpdate("UPDATE cart "
                            + "SET plan = '" + name + "' "
                            + "WHERE plan = '" + jLabel4.getText() + "' "
                            + "AND customer = '" + id + "'");

                    for (int j = 0; j <= i; j++) {
                        if (item[j].getText().equals(jLabel4.getText())) {
                            item[j].setText(name);
                            break;
                        }
                    }

                    jLabel4.setText(name);

                    JOptionPane.showMessageDialog(this,
                            "Success!");
                } else {
                    JOptionPane.showMessageDialog(this,
                            "Name already in use. Please use another.");
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(Cart.class.getName())
                    .log(Level.SEVERE, null, ex);
        } finally {
            closeConn();
        }
        detailsWritter();
    }//GEN-LAST:event_jButton1ActionPerformed

    private void filter(int filter) {
        DefaultTableModel model = (DefaultTableModel) jTable2.getModel();
        model.setRowCount(0);
        if (filter == 0) {
            if (jComboBox1.getSelectedIndex() == 0) {
                firstTableFill();
            } else {
                try {
                    openConn();
                    Statement stmt = conn.createStatement();
                    ResultSet rs = stmt.executeQuery("SELECT id "
                            + "FROM category "
                            + "WHERE name = '"
                            + jComboBox1.getSelectedItem().toString() + "'");
                    while (rs.next()) {
                        Statement stmt0 = conn.createStatement();
                        ResultSet rs0 = stmt0.executeQuery("SELECT * "
                                + "FROM materials "
                                + "WHERE category = '" + rs.getString(1) + "'");
                        while (rs0.next()) {
                            Object[] row = {rs0.getString(1),
                                rs0.getString(2), rs0.getString(3),
                                jComboBox1.getSelectedItem().toString()};
                            model.addRow(row);
                        }
                    }
                } catch (SQLException ex) {
                    Logger.getLogger(Cart.class.getName())
                            .log(Level.SEVERE, null, ex);
                } finally {
                    closeConn();
                }
            }
        } else {
            if (jTextField1.getText().equals("")) {
                firstTableFill();
            } else {
                try {
                    openConn();
                    Statement stmt = conn.createStatement();
                    ResultSet rs = stmt.executeQuery("SELECT * "
                            + "FROM material "
                            + "WHERE name LIKE '" + jTextField1.getText() + "%'");
                    while (rs.next()) {
                        Statement stmt0 = conn.createStatement();
                        ResultSet rs0 = stmt0.executeQuery("SELECT name "
                                + "FROM category "
                                + "WHERE id = '" + rs.getString(4) + "'");
                        while (rs0.next()) {
                            Object[] row = {rs.getString(1), rs.getString(2),
                                rs.getString(3), rs0.getString(1)};
                            model.addRow(row);
                        }
                    }
                } catch (SQLException ex) {
                    Logger.getLogger(Cart.class.getName())
                            .log(Level.SEVERE, null, ex);
                } finally {
                    closeConn();
                }
            }
        }
    }

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        DefaultTableModel model0 = (DefaultTableModel) jTable2.getModel();
        DefaultTableModel model1 = (DefaultTableModel) jTable3.getModel();

        for (int j = 0; j < model1.getRowCount(); j++) {
            if (model0.getValueAt(jTable2.getSelectedRow(), 0)
                    .equals(model1.getValueAt(j, 0))) {
                JOptionPane.showMessageDialog(this, "Item already added!");
                return;
            }
        }

        String units = JOptionPane.showInputDialog(this, "Enter units : ", "Units");

        try {
            if (Integer.parseInt(units) > 0) {
                Object[] row = {model0.getValueAt(jTable2.getSelectedRow(), 0),
                    model0.getValueAt(jTable2.getSelectedRow(), 1),
                    units,
                    Integer.parseInt(model0.getValueAt(jTable2.getSelectedRow(), 2)
                    .toString()) * Integer.parseInt(units),
                    model0.getValueAt(jTable2.getSelectedRow(), 3)};

                model1.addRow(row);

                jTable2.clearSelection();
                jButton4.setEnabled(false);

                detailsWritter();
            } else {
                JOptionPane.showMessageDialog(this, "Invalid value!");
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid value!");
        }
    }//GEN-LAST:event_jButton4ActionPerformed

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
        DefaultTableModel model = (DefaultTableModel) jTable3.getModel();
        model.removeRow(jTable3.getSelectedRow());

        jTable3.clearSelection();
        jButton5.setEnabled(false);

        detailsWritter();
    }//GEN-LAST:event_jButton5ActionPerformed

    private void jTextField1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField1ActionPerformed
        filter(1);
        jComboBox1.setSelectedIndex(0);
    }//GEN-LAST:event_jTextField1ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        String units = JOptionPane.showInputDialog(this, "Enter new value : ", "Units");
        try {
            if (Integer.parseInt(units) > 0) {
                DefaultTableModel model = (DefaultTableModel) jTable3.getModel();
                model.setValueAt(units, jTable3.getSelectedRow(), 2);
                detailsWritter();
            } else {
                JOptionPane.showMessageDialog(this, "Invalid value!");
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid value!");
        }
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        Loading l = new Loading();
        l.setVisible(true);

        new Thread(() -> {
            panelOperations(false);

            DefaultTableModel model = (DefaultTableModel) jTable3.getModel();
            for (int j = 0; j < model.getRowCount(); j++) {
                try {
                    openConn();
                    Statement stmt = conn.createStatement();
                    ResultSet rs = stmt.executeQuery("SELECT COUNT(id) "
                            + "FROM cart "
                            + "WHERE plan = '" + jLabel4.getText() + "'");
                    while (rs.next()) {
                        Statement stmt0 = conn.createStatement();
                        ResultSet rs0 = stmt0.executeQuery("SELECT id "
                                + "FROM material "
                                + "WHERE name = '" + model.getValueAt(j, 4) + "'");
                        while (rs0.next()) {
                            if (rs.getInt(1) != 0) {
                                Statement stmt1 = conn.createStatement();
                                stmt1.executeUpdate("DELETE FROM cart "
                                        + "WHERE plan = '" + jLabel4.getText() + "'");
                            }
                            Statement stmt2 = conn.createStatement();
                            stmt2.executeUpdate("INSERT INTO cart "
                                    + "(customer, material, units, plan) "
                                    + "VALUES "
                                    + "('" + id + "', '" + rs0.getString(1) + "', "
                                    + "'" + model.getValueAt(j, 2) + "', "
                                    + "'" + jLabel4.getText() + "')");
                            JOptionPane.showMessageDialog(this, "Success!");
                        }
                    }
                } catch (SQLException ex) {
                    Logger.getLogger(Cart.class.getName())
                            .log(Level.SEVERE, null, ex);
                } finally {
                    closeConn();
                }

                l.dispose();
                panelOperations(true);
            }
        }).start();
    }//GEN-LAST:event_jButton3ActionPerformed

    private void jComboBox1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox1ActionPerformed
        if (jComboBox1.getSelectedItem() != null) {
            filter(0);
            jTextField1.setText("");
        }
    }//GEN-LAST:event_jComboBox1ActionPerformed

    private void jTable2MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable2MouseClicked
        jButton4.setEnabled(true);
        jButton5.setEnabled(false);
        jTable3.clearSelection();
    }//GEN-LAST:event_jTable2MouseClicked

    private void jTable3MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable3MouseClicked
        jButton4.setEnabled(false);
        jButton5.setEnabled(true);
        jTable2.clearSelection();
    }//GEN-LAST:event_jTable3MouseClicked

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(Cart.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Cart.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Cart.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Cart.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Cart().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JComboBox<String> jComboBox1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenu jMenu3;
    private javax.swing.JMenu jMenu4;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JMenuItem jMenuItem2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JProgressBar jProgressBar1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JTable jTable2;
    private javax.swing.JTable jTable3;
    private javax.swing.JTextArea jTextArea1;
    private javax.swing.JTextField jTextField1;
    // End of variables declaration//GEN-END:variables
}
