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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Map;
import java.util.UUID;

import es.ugr.ddsbox.MainController;
import es.ugr.ddsbox.models.SharedFolder;
import es.ugr.ddsbox.models.User;
import es.ugr.ddsbox.utils.SecurityUtils;

public class ConsoleUI implements UI{
    private InternalDBController sqliteDB;
    private MainController mainController;

    public void run(){
        sqliteDB = InternalDBController.getInstance();
        mainController = new MainController(this);

        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));

        if(sqliteDB.getConfigParam("username").compareTo("")==0){
            wizard();
        }
        mainController.execute();
        new Thread(mainController).start();
        String command="";
        boolean quit = false;

        /*SwingWorker<Void,Void> task = new SwingWorker<Void,Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                MainController mainController = new MainController();
                mainController.run();
                return null;
            }
        };*/

        while(!quit){
            System.out.print("> ");
            try {
                command = in.readLine();
            } catch (IOException e) {
                e.printStackTrace();
            }

            if(command.compareTo("q")==0 || command.compareTo("quit")==0){
                quit = true;
            } else if(command.compareTo("h")==0 || command.compareTo("help")==0){
                printHelp();
            } else if(command.compareTo("w")==0 || command.compareTo("wizard")==0){
                wizard();
            } else if(command.compareTo("n")==0 || command.compareTo("new")==0){
                newSharedFolder();
            } else if(command.compareTo("s")==0 || command.compareTo("subscribe")==0){
                subscribeFolder();
            } else if(command.compareTo("l")==0 || command.compareTo("list")==0){
                listSharedFolders();
            } else if(command.compareTo("vsf")==0 || command.compareTo("viewSharedFolders")==0){
                viewSharedFolders();
            }

        }
        //mainController.stop();
        mainController.cancel(true);
        System.out.println("Bye.");
    }

    private void printHelp(){
        System.out.println("Lista de comandos disponibles:");
        System.out.println("\tq o quit: salir.");
        System.out.println("\th o help: imprimir ayuda.");
        System.out.println("\tw o wizard: iniciar asistente.");
        System.out.println("\tn o new: crea una nueva carpeta compartida.");
        System.out.println("\ts o subscribe: suscribirse a una carpeta compartida.");
        System.out.println("\tl o list: lista las carpetas compartidas a las que se esta subscrito.");
        System.out.println("\td o delete: borra una carpeta compartida.");
    }

    private void listSharedFolders(){
        ArrayList<SharedFolder> folders = sqliteDB.getSharedFolders();
        for(SharedFolder folder : folders){
            System.out.println(folder.getPath()+" "+folder.getUuid());
        }
    }

    private void viewSharedFolders(){
        ArrayList<SharedFolder> foldersArray = new ArrayList<SharedFolder>();
        Map<String, SharedFolder> folders = mainController.getSharedFolders();
        for(SharedFolder folder : folders.values()){
            foldersArray.add(folder);
        }

        for(int i=0; i<foldersArray.size(); i++){
            System.out.println(i+" "+foldersArray.get(i).getName()+" "+foldersArray.get(i).getUuid());
        }
    }

    private void newSharedFolder(){
        String name = "";
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));

        try{
            System.out.print("Introduzca el nombre de la carpeta compartida: ");
            name = in.readLine();
        }catch(IOException e){
            e.printStackTrace();
        }

        if(name.trim().compareTo("") != 0){
            SharedFolder folder = mainController.createSharedFolder(name, 0, SharedFolder.TRANSIENT);

            System.out.println("La carpeta compartida "+folder.getName()+ " ha sido creada. " +
                    "Utilizando UUID: "+folder.getUuid());
        }
    }


    private void subscribeFolder(){
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        String id = "";
        String name = "";

        ArrayList<SharedFolder> foldersArray = new ArrayList<SharedFolder>();
        Map<String, SharedFolder> folders = mainController.getSharedFolders();
        for(SharedFolder folder : folders.values()){
            foldersArray.add(folder);
        }

        for(int i=0; i<foldersArray.size(); i++){
            System.out.println(i+" "+foldersArray.get(i).getName()+" "+foldersArray.get(i).getUuid());
        }

        System.out.println("\n\n");

        try {
            System.out.print("Introduzca el id de la carpeta de la lista anterior: ");
            id = in.readLine();
            System.out.print("Introduzca el nombre de la carpeta a la que se quiere suscribir: ");
            name = in.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if(!id.trim().isEmpty() && !name.trim().isEmpty()){
            int i = Integer.parseInt(id);

            SharedFolder folder = foldersArray.get(i);
            folder.setName(name);
            folder.setPersistence(SharedFolder.TRANSIENT);
            String checkUuid = mainController.subscribeSharedFolder(folder);
            if(folder.getUuid().compareTo(checkUuid) == 0){
                System.out.print("Se ha suscrito a la carpeta "+name+" con éxito.");
            }

        }

    }

    public void wizard(){
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));

        System.out.println("Bienvenido al asistente de configuración de DDSBox.");
        System.out.println("Introduzca los datos que se le vayan solicitando a continuación.");

        String username = "";
        String name = "";
        String email = "";

        try {
            System.out.print("Nombre de usuario: ");
            username = in.readLine();
            System.out.print("Nombre y apellidos: ");
            name = in.readLine();
            System.out.print("Email: ");
            email = in.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("Pulse intro para generar sus claves criptográficas.");
        // CREAR CLAVES
        System.out.println("Claves generadas con éxito. Pulse intro para finalizar el asistente.");
        String uuid = UUID.randomUUID().toString();
        User user = new User();
        user.setUuid(uuid);
        user.setUsername(username);
        user.setRealname(name);
        user.setEmail(email);
        SecurityUtils.generateKeysRSA(user);
        //user.setRsakey(userReceived.publicRSA);
        //user.setOnline(userReceived.online);

        sqliteDB.saveUser(user);
        user = sqliteDB.getUser(user.getUuid());
        sqliteDB.saveConfigParam("id_user", "" + user.getId());
        sqliteDB.saveConfigParam("uuid", uuid);
        sqliteDB.saveConfigParam("username", username);
        sqliteDB.saveConfigParam("name", name);
        sqliteDB.saveConfigParam("email", email);
        String rootDir = System.getProperty("user.home")+"/ddsbox/";
        rootDir = rootDir.replace('\\', '/');
        sqliteDB.saveConfigParam("RootDir", rootDir);

        SharedFolder folder = new SharedFolder();
        folder.setName("root");
        folder.setPath("");
        folder.setOwner(username);
        folder.setUuid(UUID.randomUUID().toString());
        folder.setType(1);
        folder.setPermission(SharedFolder.OWNER);
        sqliteDB.saveSharedFolder(folder);

    }

    public void addSharedFolderToList(SharedFolder folder){

    }

    public void addFilePercentaje(File file){}

    public void updateFilePercentaje(File file){

    }

    public static void main(String[] args) {
        ConsoleUI cui = new ConsoleUI();
        cui.run();
    }
}
