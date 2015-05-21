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

package es.ugr.ddsbox.ui.graphic;

import es.ugr.ddsbox.InternalDBController;
import es.ugr.ddsbox.models.SharedFolder;
import es.ugr.ddsbox.models.User;
import es.ugr.ddsbox.utils.SecurityUtils;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.util.UUID;

public class Wizard extends JWizard {
    private static final long serialVersionUID = 2103905729508952829L;
    private InternalDBController sqliteDb = InternalDBController.getInstance();

    private String username, name, email;

    public Wizard() {
        super("Configuration wizard");

        this.setSize(300,200);

        username = sqliteDb.getConfigParam("username");
        name = sqliteDb.getConfigParam("name");
        email = sqliteDb.getConfigParam("email");
        System.out.println(username);

        JPage page1 = new Page1("page1");
        page1.setBackPageIdentifier(null);
        page1.setNextPageIdentifier("page2");
        registerWizardPanel(page1);

        JPage page2 = new Page2("page2");
        page2.setBackPageIdentifier("page1");
        page2.setNextPageIdentifier(JPage.FINISH);
        registerWizardPanel(page2);

        setCurrentPanel("page1");

    }

    public void saveConfig() {
        String uuid = UUID.randomUUID().toString();
        sqliteDb.saveConfigParam("uuid", uuid);
        sqliteDb.saveConfigParam("username", username);
        sqliteDb.saveConfigParam("name", name);
        sqliteDb.saveConfigParam("email", email);
        String rootDir = System.getProperty("user.home")+"/ddsbox/";
        rootDir = rootDir.replace('\\', '/');
        sqliteDb.saveConfigParam("RootDir", rootDir);

        User user = new User();
        user.setUuid(uuid);
        user.setUsername(username);
        user.setRealname(name);
        user.setEmail(email);
        SecurityUtils.generateKeysRSA(user);
        //user.setRsakey(userReceived.publicRSA);
        //user.setOnline(userReceived.online);

        sqliteDb.saveUser(user);
        user = sqliteDb.getUser(user.getUuid());
        sqliteDb.saveConfigParam("id_user", "" + user.getId());

        SharedFolder folder = new SharedFolder();
        folder.setName("root");
        folder.setPath("");
        folder.setOwner(username);
        folder.setUuid(UUID.randomUUID().toString());
        folder.setType(SharedFolder.PRIVATE);
        folder.setPermission(SharedFolder.OWNER);
        folder.setSuscribed(true);
        folder.setKey(SecurityUtils.generateKeyAES());
        sqliteDb.saveSharedFolder(folder);
    }

    class Page1 extends JPage {
        public Page1(String id) {
            super( id);
        }
        JTextField field;

        @Override
        public JPanel createPanel() {
            JPanel page = new JPanel( new MigLayout("","","[150px]") );
            page.add(new JLabel("Welcome to the configuration wizard. Press next to continue."), "skip");
            return page;
        }
    }

    class Page2 extends JPage {
        public Page2( String id) {
            super( id );
        }

        JTextField usernameField, nameField, emailField;

        @Override
        public JPanel createPanel() {

            JPanel page = new JPanel( new MigLayout() );
            page.add(new JLabel("User: "), "gap para");
            page.add( usernameField = new JTextField(15), "span, growx");
            page.add(new JLabel("Name: "), "gap para");
            page.add( nameField = new JTextField(15), "span, growx");
            page.add(new JLabel("Email: "), "gap para");
            page.add( emailField = new JTextField(15), "span, growx");

            usernameField.setText( (username.equals("") ) ? "" : username );
            nameField.setText( ( name.equals("")) ? "" : name );
            emailField.setText( ( email.equals("")) ? "" : email );

            return page;
        }

        @Override
        public void preDisplayPanel() {
            usernameField.addKeyListener( getJWizard().getController() );
            nameField.addKeyListener( getJWizard().getController() );
            emailField.addKeyListener( getJWizard().getController() );
        }

        @Override
        public void preClosePanel() {
            usernameField.removeKeyListener( getJWizard().getController() );
            nameField.removeKeyListener( getJWizard().getController() );
            emailField.removeKeyListener( getJWizard().getController() );
        }

        @Override
        public boolean isValid() {
            try {
                username = usernameField.getText();
                name = nameField.getText();
                email = emailField.getText();
                return true;
            }
            catch (NumberFormatException invalid ){
                return false;
            }
        }
    }

    public static void main(String args[]) {
        Wizard wizard = new Wizard();

        System.out.println("Show wizard " + wizard.getTitle());
        int result = wizard.showModalDialog();
        System.out.print("Wizard completed with:");
        switch (result) {
            case JWizard.CANCEL:
                System.out.println("CANCEL");
                break;
            case JWizard.FINISH:
                wizard.saveConfig();
                System.out.println("CONFIG SAVED");
                break;
            case JWizard.ERROR:
                System.out.println("ERROR");
                break;
            default:
                System.out.println("unexpected " + result);
        }
    }
}
