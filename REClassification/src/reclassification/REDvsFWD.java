/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package reclassification;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import javax.swing.JFileChooser;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import reclassification.util.XMIMetricsExtractor;
import reclassification.util.XMIVersionExtractor;

/**
 *
 * @author truongh
 */
public class REDvsFWD extends javax.swing.JFrame {

    /**
     * Creates new form REDvsFWD
     */
    public REDvsFWD() {
        initComponents();
        
        txtFolderREV.getDocument().addDocumentListener(new DocumentListener() {

            @Override
            public void insertUpdate(DocumentEvent e) {
                preExecution();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                preExecution();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                preExecution();
            }
        });
        
        txtFolderFWD.getDocument().addDocumentListener(new DocumentListener() {

            @Override
            public void insertUpdate(DocumentEvent e) {
                preExecution();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                preExecution();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                preExecution();
            }
        });
        
    }

    private void preExecution(){
        if (!txtFolderREV.getText().equals("") && !txtFolderFWD.getText().equals(""))
            btnExecute.setEnabled(true);
        else
            btnExecute.setEnabled(false);
    }
    
    private void log(String loggingText){
        System.out.println(loggingText);
        txtOutput.append(loggingText + "\n");
    }
    
    private void saveFeaturestoCSV(File fileToSave, String content) throws IOException {
        FileWriter fileWriter = new FileWriter(fileToSave,false);
        //Write the CSV file header
        fileWriter.write(content);
        fileWriter.flush();
        fileWriter.close();
    }
    
    private String proceed(String fileName, int isFWD){
        // Forward designs
        File folder = new File(fileName);
        File[] ls = folder.listFiles();
        
        String result = "";
        for (int i = 0; i < ls.length; i++) {
            try {
                File xmiFile = ls[i];
                if (xmiFile.isFile() && (xmiFile.getName().endsWith(".xml") || xmiFile.getName().endsWith(".xmi"))) {
                    log("Processing file " + xmiFile.getName());

                    String metamodel = "";
                    String modelTrans = "";

                    // UML and XMI transformation selection
                    XMIVersionExtractor ver = new XMIVersionExtractor(xmiFile);
                    //log("xmiVersion = " + ver.getVersion());   

                    if (ver.getVersion().startsWith("1.")) {
                        metamodel = "metamodel.xml";
                        // xmi ver = 1.0
                        if (ver.getVersion().equals("1.0")) {
                            modelTrans = "xmiTrans1_0.xml";
                        } else // xmi ver = 1.x
                        {
                            modelTrans = "xmiTrans1_1.xml";
                        }
                    } else {
                        metamodel = "metamodel2.xml";
                        modelTrans = "xmiTrans2_0.xml";
                    }
                    //log("Using files " + metamodel + " and " + modelTrans);

                    XMIMetricsExtractor xmiExtractor = new XMIMetricsExtractor(xmiFile, metamodel, modelTrans);
                    String fmText = "%s, %d , %d , %d , %d , %b , %d , %d, %b , %d , %.3f, %.3f, %.3f, %.3f, %.3f, %d, %d, %d";
                    String content = String.format(fmText, xmiFile.getName() // name of the xmi file
                            , xmiExtractor.getNoCls() // F01 = Number of class
                            , xmiExtractor.getNoOper() // F02 = Number of operation
                            , xmiExtractor.getNoAttr() // F03 = Number of attribute
                            , xmiExtractor.getNoPara() // F04 = Number of parameter
                            , xmiExtractor.isExtOperPara() // F05 = Existence of parameter
                            , xmiExtractor.getNoAssociation() // F06 = Number of association
                            , xmiExtractor.getNoAssocType() // F07 = Existence of different association type
                            , xmiExtractor.isExtOrpCls() // F08 = Existence of orphan classes
                            , xmiExtractor.getNoOrpCls() // F09 = Number of orphan classes
                            , xmiExtractor.getAvgAttrCls() // F10 = Average number of attribute per class
                            , xmiExtractor.getAvgOperCls() // F11 = Average number of operations per class
                            , xmiExtractor.getAvgAssocCls() // F12 = Average number of associations per class
                            , xmiExtractor.getAvgParaOper() // F13 = Average number of parameters per operations
                            , xmiExtractor.getAvgOrpCls()// F14 = Average number of orphan class over all classes
                            , xmiExtractor.getMaxAttrCls() // F15 = Maximum number of attributes per class
                            , xmiExtractor.getMaxOperCls() // F16 = Maximum number of operations per class
                            , isFWD
                    );
                    result+=content+"\n";
                    //log(content);

//                    log("F01 = Number of class = " + xmiExtractor.getNoCls());
//                    log("F02 = Number of operation = " + xmiExtractor.getNoOper());
//                    log("F03 = Number of attribute = " + xmiExtractor.getNoAttr());
//                    log("F04 = Number of parameter = " + xmiExtractor.getNoPara());
//                    log("F05 = Existence of parameter = " + xmiExtractor.isExtOperPara());
//                    log("F06 = Number of association = " + xmiExtractor.getNoAssociation());
//                    log("F07 = Existence of different association type = " + xmiExtractor.getNoAssocType());
//                    log("F08 = Average number of attribute per class = " + xmiExtractor.getAvgAttrCls());
//                    log("F09 = Average number of operations per class = " + xmiExtractor.getAvgOperCls());
//                    log("F10 = Average number of associations per class = " + xmiExtractor.getAvgAssocCls());
//                    log("F11 = Average number of parameters per operations = " + xmiExtractor.getAvgParaOper());
//                    log("F12 = Maximum number of attributes per class = " + xmiExtractor.getMaxAttrCls());
//                    log("F13 = Maximum number of operations per class = " + xmiExtractor.getMaxOperCls());
                }
            } catch (Exception e) {

            }
        }
        return result;
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        btnChooseREV = new javax.swing.JButton();
        btnChooseFWD = new javax.swing.JButton();
        txtFolderFWD = new javax.swing.JTextField();
        txtFolderREV = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        txtOutput = new javax.swing.JTextArea();
        btnExecute = new javax.swing.JButton();
        btnClean = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        btnChooseREV.setText("Select REV folder");
        btnChooseREV.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnChooseREVActionPerformed(evt);
            }
        });

        btnChooseFWD.setText("Select FWD folder");
        btnChooseFWD.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnChooseFWDActionPerformed(evt);
            }
        });

        txtOutput.setColumns(20);
        txtOutput.setRows(5);
        jScrollPane1.setViewportView(txtOutput);

        btnExecute.setText("Extract features");
        btnExecute.setEnabled(false);
        btnExecute.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnExecuteActionPerformed(evt);
            }
        });

        btnClean.setText("Clean Text");
        btnClean.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCleanActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(17, 17, 17)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addGap(0, 684, Short.MAX_VALUE)
                                .addComponent(btnClean)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnExecute))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(txtFolderFWD)
                                    .addComponent(txtFolderREV))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(btnChooseREV, javax.swing.GroupLayout.PREFERRED_SIZE, 154, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(btnChooseFWD, javax.swing.GroupLayout.PREFERRED_SIZE, 156, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jScrollPane1)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnChooseFWD)
                    .addComponent(txtFolderFWD, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnChooseREV, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(txtFolderREV))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 184, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnExecute)
                    .addComponent(btnClean))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnChooseREVActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnChooseREVActionPerformed
        //int result;    
        JFileChooser chooser = new JFileChooser(); 
        chooser.setCurrentDirectory(new java.io.File("."));
        chooser.setDialogTitle("Choose a folder");
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        chooser.setAcceptAllFileFilterUsed(false);

        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) { 
            txtFolderREV.setText(chooser.getSelectedFile().getAbsolutePath());
            log("The folder "+ chooser.getSelectedFile().getName() +" selected");
        }
        else {
            log("No Selection!");
        }
    }//GEN-LAST:event_btnChooseREVActionPerformed

    private void btnChooseFWDActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnChooseFWDActionPerformed
        JFileChooser chooser = new JFileChooser(); 
        chooser.setCurrentDirectory(new java.io.File("."));
        chooser.setDialogTitle("Choose a folder");
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        chooser.setAcceptAllFileFilterUsed(false);

        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) { 
            txtFolderFWD.setText(chooser.getSelectedFile().getAbsolutePath());
            log("The folder "+ chooser.getSelectedFile().getName() +" selected");
        }
        else {
            log("No folder selected!");
        }
    }//GEN-LAST:event_btnChooseFWDActionPerformed

    private void btnExecuteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnExecuteActionPerformed
        
        try {
            log("Extracting features of FWD designs ... ");
            String fwd = proceed(txtFolderFWD.getText(), 1);
            log("Extracting features of REV designs ... ");
            String rev = proceed(txtFolderREV.getText(), 0);
            
            log("Saving metrics to file");
            String header =  "Name, noCls, noOper, noAttr, noPara, extOperPara, noAssociation, noAssocType, extOrpCls, noOrpCls, avgAttrCls, avgOperCls, avgAssocCls, avgParaOper, avgOrpCls, maxAttrCls, maxOperCls,isFWD";
            
            String currentDir = Paths.get(".").toAbsolutePath().normalize().toString();
            Path fileName = Paths.get(currentDir, "metrics.csv");
            String content = header + "\n" + fwd + rev + "\n";
            saveFeaturestoCSV(fileName.toFile(), content);
            log("Metrics have been successfully saved to file " + fileName.toString());
            //log (header + "\n" + fwd + rev + "\n");
        } catch (Exception ex){
            log("Errors occured when saving the metrics to .csv file! Let try again!");
        }
    }//GEN-LAST:event_btnExecuteActionPerformed

    private void btnCleanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCleanActionPerformed
        // TODO add your handling code here:
        txtOutput.setText("");
    }//GEN-LAST:event_btnCleanActionPerformed

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
            java.util.logging.Logger.getLogger(REDvsFWD.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(REDvsFWD.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(REDvsFWD.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(REDvsFWD.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new REDvsFWD().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnChooseFWD;
    private javax.swing.JButton btnChooseREV;
    private javax.swing.JButton btnClean;
    private javax.swing.JButton btnExecute;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextField txtFolderFWD;
    private javax.swing.JTextField txtFolderREV;
    private javax.swing.JTextArea txtOutput;
    // End of variables declaration//GEN-END:variables
}
