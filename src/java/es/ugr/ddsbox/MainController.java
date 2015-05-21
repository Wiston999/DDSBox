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

package es.ugr.ddsbox;

import es.ugr.ddsbox.idl.Command;
import es.ugr.ddsbox.idl.FileInfo;
import es.ugr.ddsbox.idl.FileSegment;
import es.ugr.ddsbox.idl.FolderInfo;
import es.ugr.ddsbox.models.*;
import es.ugr.ddsbox.models.File;
import es.ugr.ddsbox.ui.InfoContainer;
import es.ugr.ddsbox.ui.UI;
import es.ugr.ddsbox.utils.FileUtils;
import es.ugr.ddsbox.utils.SecurityUtils;

import javax.swing.*;
import java.io.*;
import java.nio.file.Path;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MainController extends SwingWorker<Void, InfoContainer> {
    public static int SHAREPERMISSION = 2;

    private Logger logger = Logger.getLogger("LoggerMain");
    private String rootDir;
    private FileSystemController fileSystemController;
    private DishaDaemon dishaDaemon;
    private FileSystemDaemon fileSystemDaemon;
    private String state;
    DDSController ddsController;
    InternalDBController sqliteDB;
    String hostName;
    String userUuid;
    //ArrayList<FileInfo> changes;
    ArrayList<File> files = new ArrayList<File>();
    HashMap<String, Integer> filesIds = new HashMap<String, Integer>();
    HashMap<String, String> fileInfoTopicNames;
    HashMap<String, String> fileSegmentTopicNames;
    HashMap<String, String> commandTopicNames;

    private ArrayList<FileInfo> changes = new ArrayList<FileInfo>();
    private long currentlyFilesSending = 0;

    private enum CommandTypes{REQUESTFILE, REPORTCHANGES /* No deberia ser usada*/, REQUESTCHANGES};

    private Map<String,SharedFolder> sharedFolders = Collections.synchronizedMap(new HashMap<String,SharedFolder>());
    private HashMap<String,User> users = new HashMap<String,User>();

    private Thread dishaDaemonThread;
    private Thread fileSystemDaemonThread;

    private UI ui;
    
    public MainController(UI ui){
        this.ui = ui;
        state = "Creando controlador de base de datos";
        sqliteDB = InternalDBController.getInstance();
        if(sqliteDB.getConfigParam("username").compareTo("")==0){
            ui.wizard();
        }
        state = "Creado controlador de base de datos";

        ArrayList<File> dbFiles = sqliteDB.getFileList();
        HashMap<String, File> dbFilesMap = new HashMap<String, File>();

        state = "Extrayendo ficheros de la base de datos";

        for(int i=0; i<dbFiles.size(); i++){
            System.out.println(dbFiles.get(i).getName());
            dbFilesMap.put(dbFiles.get(i).getName(), dbFiles.get(i));
        }

        state = "Ficheros de la base de datos extraidos";
        rootDir = sqliteDB.getConfigParam("RootDir");
        state = "Asignando fichero raiz "+rootDir;
        FileUtils.setRootDir(rootDir);

        filesIds = new HashMap<String, Integer>();
		fileInfoTopicNames = new HashMap<String, String>();
		fileSegmentTopicNames = new HashMap<String, String>();
		commandTopicNames = new HashMap<String, String>();

        hostName = sqliteDB.getConfigParam("username");
        userUuid = sqliteDB.getConfigParam("uuid");
        ddsController = new DDSController(0, this);

        ArrayList<User> userArray = sqliteDB.getUsers();
        for(User user : userArray){
            users.put(user.getUuid(),user);
        }

        String lastTimestampStr = sqliteDB.getConfigParam("lastOnline");
        java.sql.Timestamp lastTimestamp = new java.sql.Timestamp(0);
        if(lastTimestampStr.length() > 0){
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS");
            try {
                Date parsedDate = dateFormat.parse(lastTimestampStr);
                lastTimestamp = new java.sql.Timestamp(parsedDate.getTime());
            } catch (ParseException e) {

            }
        }

        fileSystemController = new FileSystemController(rootDir, this);
        try {
            fileSystemDaemon = new FileSystemDaemon(rootDir, true, this);
        } catch (IOException e) {
            e.printStackTrace();
        }
        dishaDaemon = new DishaDaemon();
        state = "Inicializando demonio del sistema de archivos en: "+rootDir;

        state = "Comprobando estado de los ficheros en disco";
        ArrayList<changeInfo> fileChanges = fileSystemController.scanDirectory(dbFilesMap);

        for(int i=0; i<fileChanges.size(); i++){
            logger.log(Level.INFO, fileChanges.get(i).filename+" "+fileChanges.get(i).code+" "+fileChanges.get(i).isDir);
            if (fileChanges.get(i).code == FileSystemController.changes.CREATE){
                File file = new File(fileChanges.get(i).filename, userUuid);
                java.util.Date date= new java.util.Date();
                file.setTimeLastVersion(new Timestamp(date.getTime()));
                sqliteDB.saveFile(file);
            }else if(fileChanges.get(i).code == FileSystemController.changes.DELETE){
                sqliteDB.deleteFile(fileChanges.get(i).filename);
            }else if(fileChanges.get(i).code == FileSystemController.changes.MODIFY){
                File file = new File(fileChanges.get(i).filename, userUuid);
                java.util.Date date= new java.util.Date();
                file.setTimeLastVersion(new Timestamp(date.getTime()));
                sqliteDB.saveFile(file);
            }
            //Enviamos los nuevos cambios
            this.fileSystemChangesSlot(fileChanges.get(i));
        }

        ArrayList<SharedFolder> folders = sqliteDB.getSharedFolders();
        for(SharedFolder folder: folders){
            sharedFolders.put(folder.getUuid(), folder);

            if(folder.getType()==SharedFolder.PUBLIC)
                ddsController.publishFolderInfo(folder, null);
        }

        ddsController.publishUser(sqliteDB.getUser(userUuid));
    }

    //Protegemos todo acceso a la variable currentlyFilesSending con un mutex para evitar condiciones de carrera
    private synchronized long modifySendingFiles(int value){
        currentlyFilesSending += value;
        return currentlyFilesSending;
    }

    @Override
    protected Void doInBackground() {
        fileSystemDaemonThread = new Thread(fileSystemDaemon);
        dishaDaemonThread = new Thread(dishaDaemon);
        fileSystemDaemonThread.start();
        dishaDaemonThread.start();
        while(!isCancelled()){
            try {
                Thread.sleep(1000);
            } catch(InterruptedException ex) {
                Thread.currentThread().interrupt();
            }

            //changesControl();
        }

        return null;
    }

    public String getDishaState(){
        return state;
    }
    
    @Override
    protected void process(List<InfoContainer> pairs) {
        InfoContainer pair = pairs.get(pairs.size() - 1);
        /*headsText.setText(String.format("%d", pair.heads));
        totalText.setText(String.format("%d", pair.total));
        devText.setText(String.format("%.10g",
                ((double) pair.heads)/((double) pair.total) - 0.5));*/
        if(ui!=null){
            if(pair.type.compareTo("folderInfo")==0)
                ui.addSharedFolderToList((SharedFolder)pair.object);
            else if(pair.type.compareTo("file")==0)
                ui.updateFilePercentaje((File)pair.object);
        }
    }

    /*public void run() {
        //fileSystemController.run();
        fileSystemDaemonThread = new Thread(fileSystemDaemon);
        dishaDaemonThread = new Thread(dishaDaemon);
        fileSystemDaemonThread.start();
        dishaDaemonThread.start();
		while(!stop){

			try {
				Thread.sleep(1000);
			} catch(InterruptedException ex) {
				Thread.currentThread().interrupt();
			}

            changesControl();
		}
    }

    public void stop(){
        state = "Saliendo";
        stop = true;
    }*/

    void fileSystemChangesSlot(changeInfo changes){
        java.util.Date date= new java.util.Date();
        Timestamp timestamp = new Timestamp(date.getTime());
        boolean send = false;

        //String filename = changes.path.substring(rootDir.length());
        String completePath = (rootDir+changes.filename).replace('\\', '/');
        //completePath = changes.path;
        String filename = changes.filename;

        File file = sqliteDB.getFile(filename);

        if(file==null || file.isCompleted()){
            if(changes.code == FileSystemController.changes.CREATE){
                file = new File();
                file.setName(filename);
                file.setOwner(userUuid);
                if(!changes.isDir){
                    file.setSize(FileUtils.getSizeFromFile(completePath));
                    file.setHash(FileUtils.getHashFromFile(completePath));
                }
                file.setDir(changes.isDir);
                file.setCompleted(true);
                file.setTimeLastVersion(timestamp);
                file.setVersion(1);
                file.setChange(FileSystemController.CREATE);
                sqliteDB.saveFile(file);
                send = true;
            }
            else if(changes.code == FileSystemController.changes.MODIFY ||
                    changes.code == FileSystemController.changes.RENAME){

                String newHash = "";
                if(!changes.isDir){
                    newHash = FileUtils.getHashFromFile(completePath);
                }
                if(newHash.compareTo(file.getHash())!=0){
                    File newFile = new File(filename, userUuid);

                   //readInfo y setHash se hace dentro del constructor
                   // newFile.readInfo();
                   // newFile.setHash(File.getHashFromFile(completePath));
                    newFile.setCompleted(true);
                    newFile.setHash(newHash);
                    newFile.setTimeLastVersion(timestamp);
                    newFile.setVersion(file.getVersion()+1);
                    newFile.setChange(FileSystemController.MODIFY);

                    file = newFile;
                    sqliteDB.saveFile(file);
                    send = true;
                }
            }
            else if(changes.code == FileSystemController.changes.DELETE ){
                File newFile = new File();
                newFile.setName(filename);
                newFile.setOwner(userUuid);
                newFile.setSize(0);
                newFile.setCompleted(true);
                newFile.setHash("0");
                newFile.setTimeLastVersion(timestamp);
                newFile.setVersion(0);
                newFile.setChange(FileSystemController.DELETE);
                file = newFile;
                sqliteDB.deleteFile(filename);
                send = true;
            }
        }

        if(send){
            ArrayList<SharedFolder> sharedFolders = sqliteDB.getSharedFolders();
            int folderIndex = -1;
            int rootIndex = -1;

            this.modifySendingFiles(+1);

            for(int i=0; i<sharedFolders.size(); i++){
                if(sharedFolders.get(i).getName().compareTo("root")!=0){
                    try{
                        String comp = file.getName().substring(0, sharedFolders.get(i).getPath().length());
                        if(comp.compareTo(sharedFolders.get(i).getPath())==0)
                            folderIndex = i;
                    } catch (Exception e){

                    }
                }
                else{
                    rootIndex = i;
                }
            }

            if(folderIndex==-1)
                folderIndex = rootIndex;

            if(folderIndex!=-1 && sharedFolders.get(folderIndex).getPermission()>0){
                //String uuid = sharedFolders.get(folderIndex).getUuid();
                SharedFolder folder = sharedFolders.get(folderIndex);
                file.setDir(changes.isDir);
                state = String.format("Publicando: %s: %s", file.getName(), changes.code.name());
                ddsController.publishFileInfo(folder, file);
                for(int i=0; i<file.getNumSegments(); i++){
                    ddsController.publishFileSegment(folder, file.getName(),i);
                }
                this.modifySendingFiles(-1);
            }
            else{
                // Imprimir error
            }
        }
    }

    public void commandReceived(Command command, String uuid){
        System.out.println("Commando recibido: "+command.idCommand+" "+command.parameters);
        state = "Commando recibido: "+command.idCommand+" "+command.parameters;

        if(command.idCommand == SHAREPERMISSION){
            SharedFolder folder = sqliteDB.getSharedFolder(uuid);
            if(folder.getUsersPermission(command.userUuid)!= null && folder.getUsersPermission(command.userUuid) >= SharedFolder.EDITOR){
                String[] temp = command.parameters.split("#");
                User user = sqliteDB.getUser(temp[0]);
                sqliteDB.insertUserInSharedFolder(user.getUuid(), folder.getUuid(), Integer.parseInt(temp[1]));
                folder = sqliteDB.getSharedFolder(uuid);
                sharedFolders.put(folder.getUuid(), folder);
            }
        }
    }

    public void fileInfoReceived(FileInfo fileInfo, String uuid){
        state = "FileInfo recibido: "+fileInfo.fileName;
        System.out.println("FileInfo recibido: " + fileInfo.fileName);

        if(sharedFolders.containsKey(uuid)) {
            Integer permission = sharedFolders.get(uuid).getUsersPermission(fileInfo.userUuid);
            int type = sharedFolders.get(uuid).getType();

            if (type == 0 || (permission != null && permission > 0)) {

                ByteArrayInputStream bis = null;

                if(type==SharedFolder.PRIVATE) {
                    bis = new ByteArrayInputStream( SecurityUtils.decryptAES(sharedFolders.get(uuid).getKey(),
                            fileInfo.content.toArrayByte(null)));
                }
                else
                    bis = new ByteArrayInputStream(fileInfo.content.toArrayByte(null));

                ObjectInput in = null;

                try {
                    in = new ObjectInputStream(bis);
                    File file = (File) in.readObject();

                    bis.close();
                    in.close();

                    file.setSegmentsArray(new ArrayList<Short>((int)file.getNumSegments()));
                    for(int i=0; i<file.getNumSegments(); i++)
                        file.getSegments().add((short)0);
                    file.setCompleted(false);

                    //changes.add(fileInfo);

                    File ant = sqliteDB.getFile(file.getName());
                    if(file.isDir()){
                        if (file.getChange() == FileSystemController.CREATE){
                            FileUtils.createDir(file.getName());
                        }else if(file.getChange() == FileSystemController.DELETE){
                            FileUtils.removeDir(file.getName());
                        }
                    }else {
                        if (file.getChange() == FileSystemController.CREATE || file.getChange() == FileSystemController.MODIFY) {
                            if (ant != null && file.getHash().compareTo(ant.getHash()) != 0) {
                                sqliteDB.saveFile(file);
                                FileUtils.createEmptyFile(file, file.getSize());
                                files.add(file);
                                filesIds.put(file.getName(), files.size() - 1);
                            } else if (ant == null) {
                                sqliteDB.saveFile(file);
                                FileUtils.createEmptyFile(file, file.getSize());
                                files.add(file);
                                filesIds.put(file.getName(), files.size() - 1);
                            }
                        } else if (file.getChange() == FileSystemController.DELETE) {
                            if (ant != null && ant.getName().compareTo("") != 0 && Files.exists(Paths.get(rootDir + ant.getName()))) {   //Si el fichero esta en la base de datos,
                                try {
                                    Files.delete(Paths.get(rootDir + ant.getName()));
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                sqliteDB.deleteFile(ant.getName());
                            }
                        }
                    }

                } catch(Exception e){
                    e.printStackTrace();
                }

            }
        }
    }

    public void fileSegmentReceived(FileSegment fileSegment, String uuid){
        System.out.println("FileSegment recibido: "+fileSegment.idSegment+" "+fileSegment.fileName);
        state = "FileSegment recibido: "+fileSegment.idSegment+" "+fileSegment.fileName;

        if(sharedFolders.containsKey(uuid)) {
            Integer permission = sharedFolders.get(uuid).getUsersPermission(fileSegment.userUuid);
            int type = sharedFolders.get(uuid).getType();

            if (type == 0 || (permission != null && permission > 0)) {
                byte [] content = fileSegment.segmentContent.toArrayByte(null);

                if(type==SharedFolder.PRIVATE)
                    content = SecurityUtils.decryptAES(sharedFolders.get(uuid).getKey(), content);

                Integer id = filesIds.get(fileSegment.fileName);
                if (id != null) {
                    FileUtils.writeSegment(files.get(id), fileSegment.idSegment, content);
                    publish(new InfoContainer("file", files.get(id)));
                }
            }
        }
    }

    public void folderInfoReceived(FolderInfo folderInfo){
        logger.log(Level.INFO, "FolderInfo recibido de "+folderInfo.userUuid);
        ByteArrayInputStream bis = null;

        if(folderInfo.destUser!="") {
            User user = InternalDBController.getInstance().getUser(userUuid);

            byte [] key = SecurityUtils.decryptRSA(user.getPrivateKey(), folderInfo.encryptedKey.toArrayByte(null));

            bis = new ByteArrayInputStream( SecurityUtils.decryptAES(new String(key),
                    folderInfo.content.toArrayByte(null)));
        }
        else
            bis = new ByteArrayInputStream(folderInfo.content.toArrayByte(null));

        ObjectInput in = null;

        try {
            in = new ObjectInputStream(bis);
            SharedFolder folder = (SharedFolder) in.readObject();

            state = "FolderInfo recibido."+folder.getName();

            if(!sharedFolders.containsKey(folder.getUuid())){
                folder.setPath(folder.getName()+"/");
                folder.setSuscribed(false);

                sharedFolders.put(folder.getUuid(), folder);

                sqliteDB.saveSharedFolder(folder);

                sharedFolders.put(folder.getUuid(), folder);

                publish(new InfoContainer("folderInfo", folder));
            }

        } catch(Exception e){

        } finally {
            try {
                bis.close();
            } catch (IOException ex) {
                // ignore close exception
            }
            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException ex) {
                // ignore close exception
            }
        }

    }

    public void userReceived(es.ugr.ddsbox.idl.User userReceived){
        state = "User recibido: "+userReceived.userName;

        if(!users.containsKey(userReceived.uuid)){
            User user = new User();
            user.setUuid(userReceived.uuid);
            user.setUsername(userReceived.userName);
            user.setRealname(userReceived.realName);
            user.setEmail(userReceived.email);
            user.setPublicKey(userReceived.publicRSA);
            user.setOnline(userReceived.online);

            sqliteDB.saveUser(user);
            users.put(user.getUuid(), sqliteDB.getUser(user.getUuid()));
            publish(new InfoContainer("user", user));
        }
    }

    @Override
    protected void done() {
        dishaDaemonThread.interrupt();
        fileSystemDaemonThread.interrupt();
        state = "Esperando a que todos los archivos se envien";
        while(this.modifySendingFiles(0) > 0){
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        }
        ddsController.terminate();
        state = "Terminando Controlador Principal";

    }

    public String getRootDir() {
        return rootDir;
    }

    public InternalDBController getDBController(){
        return sqliteDB;
    }

    public SharedFolder createSharedFolder(String folderName, int type, int persistenceType){
        state = "Creando carpeta compartida: "+folderName;
        String uuid = UUID.randomUUID().toString();

        SharedFolder folder = new SharedFolder();
        folder.setPath(folderName+"/");
        folder.setName(folderName);
        folder.setType(type);
        folder.setPermission(SharedFolder.OWNER);
        folder.setOwner(hostName); //El hostname es el nombre de usuario
        folder.setUuid(uuid);
        folder.setSuscribed(true);
        folder.setPersistence(persistenceType);

        if(type == SharedFolder.PRIVATE)
            folder.setKey(SecurityUtils.generateKeyAES());

        folder.setUsersPermission(userUuid, SharedFolder.OWNER);

        sqliteDB.saveSharedFolder(folder);

        sharedFolders.put(folder.getUuid(), folder);
        String basedir = sqliteDB.getConfigParam("RootDir");
        ddsController.addSharedFolder(folder);

        Path path = Paths.get(basedir+folderName);
        if(Files.exists(path)){
            state = "Utilizando el directorio "+folderName+" ya existente";
        }
        else{
            new java.io.File(basedir+folderName).mkdir();
            state = "Directorio "+folderName+" creado";
        }

        if(folder.getType()==SharedFolder.PUBLIC)
            ddsController.publishFolderInfo(folder, null);

        return folder;
    }

    public String subscribeSharedFolder(SharedFolder f){
        SharedFolder folder = f;//sqliteDB.getSharedFolder(f.getUuid());
        folder.setPath(folder.getName() + "/");
        folder.setSuscribed(true);
        folder.setUsersPermission(userUuid, folder.getPermission());

        sqliteDB.saveSharedFolder(folder);

        sharedFolders.put(folder.getUuid(), folder);
        String basedir = sqliteDB.getConfigParam("RootDir");
        ddsController.addSharedFolder(folder);

        Path path = Paths.get(basedir + folder.getName());
        if(Files.exists(path)){
            System.out.println("Utilizando el directorio "+folder.getName()+" ya existente.");
        }
        else{
            new java.io.File(basedir+folder.getName()).mkdir();
            System.out.println("Directorio "+folder.getName()+" creado.");
        }

        return sqliteDB.getSharedFolder(folder.getUuid()).getUuid();
    }

    public User addUserToSharedFolder(String uuid, int type, SharedFolder folder){
        User user = sqliteDB.getUser(uuid);

        folder.setUsersPermission(user.getUuid(), type);

        SharedFolder destFolder = folder;
        destFolder.setPermission(type);

        ddsController.publishFolderInfo(folder, user);
        ddsController.publishCommand(folder.getUuid(), SHAREPERMISSION, uuid+"#"+type);

        sharedFolders.get(folder.getUuid()).setUsersPermission(user.getUuid(), type);
        sqliteDB.insertUserInSharedFolder(user.getUuid(), folder.getUuid(), type);

        return user;
    }

    public Map<String, SharedFolder> getSharedFolders(){
        return sharedFolders;
    }

    public static void main(String [] args){
		MainController mainController = new MainController(null);
		mainController.execute();
	}

}

