/*
 * Copyright (c) 2014. Olmo Jiménez Alaminos, Víctor Cabezas Lucena.
 *
 * This file is part of DDSBox.
 *
 * DDSBox is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * DDSBox is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with DDSBox.  If not, see <http://www.gnu.org/licenses/>.
 */

package es.ugr.ddsbox.ui;

import es.ugr.ddsbox.models.File;
import es.ugr.ddsbox.InternalDBController;
import es.ugr.ddsbox.MainController;
import es.ugr.ddsbox.models.SharedFolder;
import es.ugr.ddsbox.ui.graphic.FilesTableModel;
import es.ugr.ddsbox.ui.graphic.FolderTableModel;
import es.ugr.ddsbox.ui.graphic.JWizard;
import es.ugr.ddsbox.ui.graphic.Wizard;
import net.miginfocom.swing.MigLayout;
import es.ugr.ddsbox.models.User;
import es.ugr.ddsbox.ui.graphic.*;
import org.pushingpixels.substance.api.skin.SubstanceCremeLookAndFeel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.logging.Logger;

public class GraphicUI extends JPanel implements UI, ActionListener{
    private Logger logger = Logger.getLogger("LoggerUI");
    private InternalDBController sqliteDB;
    private MainController mc;
    public JFrame frame;

    private JPanel tabFolders;
    private JTable foldersTable;
    private FolderTableModel folderTableModel;
    private FilesTableModel filesTableModel;
    private UsersTableModel usersTableModel;

    private JMenuItem mi1,mi2,mi3;

    public GraphicUI(){
        (mc = new MainController(this)).execute();
        sqliteDB = mc.getDBController();
        initialize();
    }

    private void initialize() {
        frame = new JFrame();

        int WIDTH = 800;
        int HEIGHT = 500;
        frame.setSize(WIDTH, HEIGHT);
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        frame.setBounds((screenSize.width / 2) - (WIDTH / 2), (screenSize.height / 2) - (HEIGHT / 2), WIDTH, HEIGHT);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JMenuBar mb = new JMenuBar();
        frame.setJMenuBar(mb);
        JMenu menu1=new JMenu("Options");
        mb.add(menu1);
        mi1=new JMenuItem("Wizard");
        mi1.addActionListener(this);
        menu1.add(mi1);

        JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
        frame.getContentPane().add(tabbedPane, BorderLayout.CENTER);


        // Tab Folders
        tabFolders = new JPanel();
        tabFolders.setLayout(new BorderLayout());
        viewFolders();


        // Tab Files
        JPanel tabFiles = new JPanel();
        tabFiles.setLayout(new BorderLayout());

        filesTableModel = new FilesTableModel();
        JTable filesTable = new JTable(filesTableModel);
        filesTable.getColumn("Completed").setCellRenderer(new FilesTableModel.ProgressCellRender());
        JScrollPane scrollerFilesTable = new JScrollPane(filesTable);
        tabFiles.add(scrollerFilesTable);

        tabbedPane.addTab("Shared Folders", null, tabFolders, null);
        tabbedPane.addTab("Files", null, tabFiles, null);
    }

    private void viewFolders(){
        tabFolders.removeAll();
        tabFolders.repaint();

        folderTableModel = new FolderTableModel();
        foldersTable = new JTable(folderTableModel);
        JScrollPane scrollerFoldersTable = new JScrollPane(foldersTable);
        tabFolders.add(scrollerFoldersTable);

        foldersTable.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent me) {
                JTable table =(JTable) me.getSource();
                Point p = me.getPoint();
                int row = table.rowAtPoint(p);
                if (me.getClickCount() == 2) {
                    openFolder(row);
                }
            }
        });

        JButton btnNewFolder = new JButton("New Shared Folder");
        btnNewFolder.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String[] types = { "Public", "Private" };
                String[] persistenceTypes = {"Persistent", "Transient", "Volatile"};
                JTextField folderName = new JTextField(15);
                JComboBox typesList = new JComboBox(types);
                JComboBox persTypesList = new JComboBox(persistenceTypes);
                typesList.setSelectedIndex(0);

                JPanel myPanel = new JPanel(new MigLayout());
                JLabel labelGeneral = new JLabel("General");
                JSeparator separatorGeneral = new JSeparator();
                JLabel labelAdvanced = new JLabel("Advanced");
                JSeparator separatorAdvanced = new JSeparator();
                myPanel.add(labelGeneral, "split 2, span");
                myPanel.add(separatorGeneral, "growx, wrap");

                myPanel.add(new JLabel("Enter a folder's name: "), "gap para");
                myPanel.add(folderName, "span, growx, wrap");
                myPanel.add(new JLabel("Select the type: "), "gap para");
                myPanel.add(typesList, "span, growx, wrap");

                myPanel.add(labelAdvanced, "split 2, span");
                myPanel.add(separatorAdvanced, "growx, wrap");

                myPanel.add(new JLabel("Select the persistence level: "), "gap para");
                myPanel.add(persTypesList, "span, growx, wrap");

                int result = JOptionPane.showConfirmDialog(null, myPanel, "New Shared Folder", JOptionPane.OK_CANCEL_OPTION);
                if (result == JOptionPane.OK_OPTION) {
                    String fname = folderName.getText();
                    int type = typesList.getSelectedIndex();
                    int persistenceType = persTypesList.getSelectedIndex();
                    if ((fname != null) && (fname.length() > 0)) {
                        SharedFolder folder = mc.createSharedFolder(fname, type,persistenceType);
                        addSharedFolderToList(folder);
                        return;
                    }
                }
            }
        });

        JButton btnSubscribe = new JButton("Subscribe");
        btnSubscribe.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int id = foldersTable.getSelectedRow();
                if(id!=-1) {
                    SharedFolder folder = folderTableModel.getFolder(id);

                   /* String s = (String) JOptionPane.showInputDialog(
                            frame,
                            "You're going to subscribe to the folder " + folder.getName() + ". Enter a name: ",
                            "Subscribe to " + folder.getName(),
                            JOptionPane.PLAIN_MESSAGE,
                            null,
                            null,
                            "");
*/
                    String s = folder.getName();
                    if ((s != null) && (s.length() > 0)) {
                        folder.setName(s);
                        mc.subscribeSharedFolder(folder);

                        return;
                    }
                }
            }
        });

        JToolBar toolBar = new JToolBar("Options");
        toolBar.setFloatable(false);
        toolBar.add(btnSubscribe);
        toolBar.add(btnNewFolder);
        tabFolders.add(toolBar, BorderLayout.PAGE_START);

        populateFoldersTable();
    }

    private void populateFoldersTable(){
        ArrayList<SharedFolder> folders = sqliteDB.getSharedFolders();

        for(SharedFolder folder : folders){
            folderTableModel.add(folder);
        }
    }

    public void addSharedFolderToList(SharedFolder folder){
        folderTableModel.add(folder);
    }

    public void addFilePercentaje(File file){
        filesTableModel.addFile(file);
    }

    public void updateFilePercentaje(File file){
        filesTableModel.updateStatus(file, file.percentCompleted());
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource()==mi1) {
            Wizard wizard = new Wizard();
            int result = wizard.showModalDialog();
            if(result == JWizard.FINISH)
                wizard.saveConfig();
        }
    }

    private void openFolder(int row){
        final SharedFolder folder = sqliteDB.getSharedFolder(folderTableModel.getFolder(row).getUuid());
        tabFolders.removeAll();
        tabFolders.repaint();

        usersTableModel = new UsersTableModel(sqliteDB.getUsersFromFolder(folder), folder.getUsersPermissionMap());
        JTable usersTable = new JTable(usersTableModel);
        JScrollPane scrollerFoldersTable = new JScrollPane(usersTable);
        tabFolders.add(scrollerFoldersTable);

        JButton btnBack = new JButton("Back");
        btnBack.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                viewFolders();
            }
        });

        JButton btnAddUser = new JButton("Add User");
        btnAddUser.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                ArrayList<User> users = sqliteDB.getUsers();
                ArrayList<String> userNames = new ArrayList<String>();
                for(User user: users)
                    userNames.add(user.getUsername());

                JComboBox usersList = new JComboBox(userNames.toArray());
                JTextField folderName = new JTextField(15);

                String[] types = { "Reader", "Contributor", "Editor" };
                JComboBox typesList = new JComboBox(types);
                typesList.setSelectedIndex(0);

                JPanel myPanel = new JPanel();
                myPanel.add(new JLabel("User: "));
                myPanel.add(usersList);
                myPanel.add(Box.createHorizontalStrut(15)); // a spacer
                myPanel.add(new JLabel("Permission: "));
                myPanel.add(typesList);

                int result = JOptionPane.showConfirmDialog(null, myPanel, "Add User", JOptionPane.OK_CANCEL_OPTION);
                if (result == JOptionPane.OK_OPTION) {
                    String fname = users.get(usersList.getSelectedIndex()).getUuid();
                    int type = typesList.getSelectedIndex();

                    if ((fname != null) && (fname.length() > 0)) {

                        User user = mc.addUserToSharedFolder(fname, type, folder);
                        addUserToList(user, type);
                        return;
                    }
                }
            }
        });

        JToolBar toolBar = new JToolBar("Options");
        toolBar.setFloatable(false);
        toolBar.add(btnBack);
        toolBar.add(btnAddUser);
        tabFolders.add(toolBar, BorderLayout.PAGE_START);
    }

    public void addUserToList(User user, int permission){
        usersTableModel.add(user, permission);
    }


    public static void main (java.lang.String args[])
    {
        /*SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                try {
                    UIManager.setLookAndFeel(new SubstanceCremeLookAndFeel());
                } catch (Exception e) {
                    e.printStackTrace();
                }

                InternalDBController sqliteDB = InternalDBController.getInstance();
                if(sqliteDB.getConfigParam("username").equals("")){
                    Wizard wizard = new Wizard();
                    int result = wizard.showModalDialog();
                    if(result == JWizard.FINISH){
                        wizard.saveConfig();

                        try {
                            GraphicUI window = new GraphicUI();
                            window.frame.setVisible(true);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
                else{
                    try {
                        GraphicUI window = new GraphicUI();
                        window.frame.setVisible(true);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });*/
        GraphicUI.run();
    }
    public void wizard(){
        Wizard wizard = new Wizard();
        int result = wizard.showModalDialog();
        if(result == JWizard.FINISH){
            wizard.saveConfig();

            try {
                GraphicUI window = new GraphicUI();
                window.frame.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void run(){
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                try {
                    UIManager.setLookAndFeel(new SubstanceCremeLookAndFeel());
                } catch (Exception e) {
                    e.printStackTrace();
                }

                InternalDBController sqliteDB = InternalDBController.getInstance();
                if(sqliteDB.getConfigParam("username").equals("")){
                    Wizard wizard = new Wizard();
                    int result = wizard.showModalDialog();
                    if(result == JWizard.FINISH){
                        wizard.saveConfig();

                        try {
                            GraphicUI window = new GraphicUI();
                            window.frame.setVisible(true);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
                else{
                    try {
                        GraphicUI window = new GraphicUI();
                        window.frame.setVisible(true);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }
}
