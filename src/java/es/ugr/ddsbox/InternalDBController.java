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
import es.ugr.ddsbox.models.File;
import es.ugr.ddsbox.models.SharedFolder;
import es.ugr.ddsbox.models.User;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.*;
import java.util.ArrayList;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class InternalDBController {
	private Connection connection;
	private Statement stmt;
	private String dbPath;
    private String dirDisha;
	//private ResultSet lastResult;
    private Logger logger = Logger.getLogger("LoggerInternalDB");
    private String rootDir = "";
    private static InternalDBController instance;

	private InternalDBController(){
		String homeDir;
		String dirDb;
		homeDir = System.getProperty("user.home");
		dirDb = homeDir+"/.ddsbox";
		dirDisha = homeDir+"/ddsbox/";
        dirDisha = dirDisha.replace('\\','/');
		dbPath = dirDb+"/ddsbox.db";

		try{
			if(new java.io.File(dirDb).mkdir()) {
				System.out.println("Directory: "+dirDb+" Created");
			} else {
				System.out.println("Directory: "+dirDb+" is not created");
			}
		} catch(Exception e){
			e.printStackTrace();
		}

		try{
			if(new java.io.File(dirDisha).mkdir()) {
				System.out.println("Directory: "+dirDisha+" Created");
			} else {
				System.out.println("Directory: "+dirDisha+" is not created");
			}
		} catch(Exception e){
			e.printStackTrace();
		}

        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:"+dbPath);
            connection.setAutoCommit(true);
        }catch (Exception e){
            System.err.println("connect "+ e.getClass().getName() + ": " + e.getMessage() );
        }

        configureDatabase();

        rootDir = getConfigParam("RootDir");
	}

    public static InternalDBController getInstance(){
        if(instance==null)
            instance = new InternalDBController();
        return instance;
    }

    private boolean connect(){
		try {
			Class.forName("org.sqlite.JDBC");
			connection = DriverManager.getConnection("jdbc:sqlite:"+dbPath);
            connection.setAutoCommit(true);
		}catch (Exception e){
			System.err.println("connect "+ e.getClass().getName() + ": " + e.getMessage() );
			return false;
		}
        return true;
    }

    private boolean close(){
		try {
			connection.close();
		}catch (Exception e){
			System.err.println("close "+ e.getClass().getName() + ": " + e.getMessage() );
			return false;
		}
        return true;
    }

	private String ReadCreationDatasesFile(){

		BufferedReader br;
		String everything = "";

		try {
			br = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("/localDBCreation.sql")));
			StringBuilder sb = new StringBuilder();
			String line = br.readLine();

			while (line != null) {
				sb.append(line);
				sb.append('\n');
				line = br.readLine();
			}
			everything = sb.toString();
			br.close();
		}catch (Exception e){
			System.err.println( e.getClass().getName() + ": " + e.getMessage() );
		}
		return everything;
	}

	private ResultSet executeQuery(String sql) throws Exception, SQLException {
        //logger.info("executeQuery: "+sql);
        ResultSet result = null;

        try{
            stmt = connection.createStatement();
            if (sql.toLowerCase().contains("select")){
                result = stmt.executeQuery(sql);
            }else{
                stmt.executeUpdate(sql);
            }
        }
        catch (Exception e){
           logger.log(Level.SEVERE, "executeQuery: " + sql);
            e.printStackTrace();
        }

        return result;
	}

	private void configureDatabase(){
		String createQuery = ReadCreationDatasesFile();

        try {
            System.out.println("creando bd");
            this.executeQuery(createQuery);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if(getConfigParam("RootDir").compareTo("")==0){
            saveConfigParam("RootDir", dirDisha);
        }
	}

	public String getConfigParam(String name){
		String sql = "SELECT content FROM configuration WHERE config_name = '"+name+"';";
		String result = "";
        ResultSet resultSet;

        try {
            resultSet = this.executeQuery(sql);

            while(resultSet.next()){
                result = resultSet.getString("content");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

		return result;
	}

    public void saveConfigParam(String parameter, String content){
        String sql = "INSERT OR IGNORE INTO configuration (config_name, content) VALUES ('"+parameter+"', '"+content+"');";
        sql +="UPDATE configuration set content = '"+content+"' WHERE config_name='"+parameter+"';";

        try {
            this.executeQuery(sql);
        } catch (Exception e){
            e.printStackTrace();
        }

        if(parameter.compareTo("RootDir")==0){
            rootDir = content;
        }
    }

    public void deleteConfigParam(String name){
        String sql = "DELETE FROM configuration WHERE config_name='"+name+"';";

        try {
            this.executeQuery(sql);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

	public File getFile(String name){
		String sql = "SELECT file_path, owner, completed, version, size, segments, hash, last_update, id_folder, isDir FROM files WHERE file_path='"+name+"';";
		File f = null;
        ResultSet resultSet;

        try {
            resultSet = this.executeQuery(sql);

            while(resultSet.next()){
                f = new File();
                f.setName(resultSet.getString("file_path"));
                f.setOwner(resultSet.getString("owner"));
                f.setCompleted(resultSet.getInt("completed") == 1);
                f.setVersion(resultSet.getInt("version"));
                f.setSize(resultSet.getInt("size"));
                f.setSegments(resultSet.getString("segments"));
                f.setHash(resultSet.getString("hash"));
                f.setTimeLastVersion(new Timestamp(resultSet.getLong("last_update")));
                f.setFolderId(resultSet.getString("id_folder"));
                f.setDir(resultSet.getInt("isDir") == 1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

		return f;
	}

	public ArrayList<File> getFileList(){
		String sql="SELECT file_path, owner, completed, version, size, segments, hash, last_update, id_folder, isDir FROM files;";
		ArrayList<File> files = new ArrayList<File>();
        ResultSet resultSet;

        try {
            resultSet = this.executeQuery(sql);

            try{
                while(resultSet.next()){
                    File f = new File();
                    f.setName(resultSet.getString("file_path"));
                    f.setOwner(resultSet.getString("owner"));
                    f.setCompleted(resultSet.getInt("completed") == 1);
                    f.setVersion(resultSet.getInt("version"));
                    f.setSize(resultSet.getInt("size"));
                    f.setSegments(resultSet.getString("segments"));
                    f.setHash(resultSet.getString("hash"));
                    f.setTimeLastVersion(new Timestamp(resultSet.getLong("last_update")));
                    f.setFolderId(resultSet.getString("id_folder"));
                    f.setDir(resultSet.getInt("isDir") == 1);
                    files.add(f);
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

		return files;
	}

	public void saveFile(File f){
		if (this.getFile(f.getName()) != null){	//Si el fichero ya esta en base de datos
            String sqlUpdate = "UPDATE files SET owner='"+f.getOwner()+"', completed='"+f.isCompletedInt()
                    +"', hash='"+f.getHash()+"', version='"+f.getVersion()+"', segments='"+f.getSegments()+"', size='"+f.getSize()
                    +"', "+"last_update='"+f.getTimeLastVersion().getTime()+"', id_folder='"+f.getFolderId()+"', isDir='"+f.isDir()
                    +"' WHERE file_path='"+f.getName()+"';";

            try {
                this.executeQuery(sqlUpdate);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }else{
            String sqlInsert = "INSERT INTO files(file_path, owner, completed, hash, version, segments, size, last_update, id_folder, isDir) VALUES "
                    +"('"+f.getName()+"', '"+f.getOwner()+"', "+f.isCompletedInt()+", '"+f.getHash()+"', "+f.getVersion()+", '"
                    +f.getSegments()+"', "+f.getSize()+", "+f.getTimeLastVersion().getTime()+", '"+f.getFolderId()+"', "+f.isDirInt()+");";

            try {
                this.executeQuery(sqlInsert);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
	}

	public void deleteFile(String name){
		String sql = "DELETE FROM files WHERE file_path='"+name+"';";

        try {
            this.executeQuery(sql);
        } catch (Exception e) {
            e.printStackTrace();
        }
	}

	public void getNumFilesRows(){
		String sql = "SELECT count(id) as total FROM files;";
		int total = 0;
        ResultSet resultSet;

        try {
            resultSet = this.executeQuery(sql);

            try{
                while(resultSet.next()){
                    total = resultSet.getInt("total");
                }
            }catch(Exception e){
                System.err.println("getNumFilesRows " + e.getClass().getName() + ": " + e.getMessage() );
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
	}

    public SharedFolder getSharedFolder(String uuid){
        String sql = "SELECT id, path, name, type, permission, owner, uuid, suscribed, persistence, key FROM shared_folder WHERE uuid = '"+uuid+"'";
        SharedFolder folder = null;
        ResultSet resultSet;

        try {
            resultSet = this.executeQuery(sql);

            folder = new SharedFolder(resultSet.getInt("id"), resultSet.getString("path"), resultSet.getString("name"), resultSet.getInt("type"),
                    resultSet.getInt("permission"), resultSet.getString("owner"), resultSet.getString("uuid"), resultSet.getBoolean("suscribed"),
                    resultSet.getInt("persistence"), resultSet.getString("key"));

            sql = "SELECT id_folder, id_user, permission FROM folders_users WHERE id_folder='"+folder.getUuid()+"';";
            resultSet = this.executeQuery(sql);
            while(resultSet.next()){
                folder.setUsersPermission(resultSet.getString("id_user"), resultSet.getInt("permission"));
            }

        } catch (Exception e) {
            folder = null;
        }

        return folder;
    }

	public ArrayList<SharedFolder> getSharedFolders(){
		String sql = "SELECT id, path, name, type, permission, owner, uuid, suscribed, persistence, key FROM shared_folder;";
        ArrayList<SharedFolder> folders = new ArrayList<SharedFolder>();
        ResultSet resultSet;

        try {
            resultSet = this.executeQuery(sql);

            try{
                while(resultSet.next()){
                    folders.add(new SharedFolder(resultSet.getInt("id"), resultSet.getString("path"), resultSet.getString("name"), resultSet.getInt("type"),
                            resultSet.getInt("permission"), resultSet.getString("owner"), resultSet.getString("uuid"), resultSet.getBoolean("suscribed"),
                            resultSet.getInt("persistence"), resultSet.getString("key")));

                    /*sql = "SELECT id_folder, id_user, uuid, permission FROM folders_users, users WHERE id_folder="+folder.getId()+" AND users.id=id_user;";
                    Statement st2 = connection.createStatement(
                            ResultSet.TYPE_SCROLL_INSENSITIVE,
                            ResultSet.CONCUR_READ_ONLY);
                    ResultSet resultSet2 = st2.executeQuery(sql);
                    while(resultSet2.next()){
                        folders.get(resultSet.getString("uuid")).getUsersPermission().put(resultSet.getString("id_user"), resultSet.getInt("permission"));
                    }
                    st2.close();*/
                }
            }catch(Exception e){
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

		return folders;
	}

    public void saveSharedFolder(SharedFolder folder){
        int suscribed = folder.isSuscribed()?1:0;
        String sql = "";
        if (this.getSharedFolder(folder.getUuid()) == null){
            sql = "INSERT INTO shared_folder (path, name, owner, type, uuid, permission, suscribed, persistence, key) VALUES ('"
                    +folder.getPath()+"', '"+folder.getName()+"', '"+folder.getOwner()+"', '"+folder.getType()+"', '"+folder.getUuid()
                    +"', "+folder.getPermission()+", "+suscribed+", "+folder.getPersistence()+", '"+folder.getKey()+"');";
        } else{
            sql = "UPDATE shared_folder SET name='"+folder.getName()+"', path='"+folder.getPath()+"', permission="+folder.getPermission()+
                    ", persistence="+folder.getPersistence()+", suscribed="+suscribed+", type="+folder.getType()+", owner='"+folder.getOwner()+"', key='"+folder.getKey()+"'"
                    +" WHERE uuid='"+folder.getUuid()+"';";
        }

        try {
            this.executeQuery(sql);

            for (Map.Entry<String, Integer> entry : folder.getUsersPermissionMap().entrySet()) {
                this.insertUserInSharedFolder(entry.getKey(), folder.getUuid(), entry.getValue());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void insertUserInSharedFolder(String userUuid, String folderUuid, int permission){
        String sql1 = "SELECT id_folder, id_user, permission FROM folders_users WHERE id_folder='"+folderUuid+"' AND id_user='"+userUuid+"';";
        boolean exists = false;
        ResultSet resultSet;

        try {
            resultSet = this.executeQuery(sql1);

            while( resultSet.next() ) {
                exists = true;
            }

            if(exists){
                String sql = "UPDATE folders_users SET permission="+permission+" WHERE id_folder='"+folderUuid+"' AND id_user='"+userUuid+"';";
                this.executeQuery(sql);
            }
            else{
                String sql = "INSERT INTO folders_users (id_folder, id_user, permission) VALUES ('"+folderUuid+"', '"+userUuid+"', '"+permission+"');";
                this.executeQuery(sql);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void saveUser(User user){
        String sql = "";

        /*ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutput out = null;
        byte[] publicKey;
        byte[] privateKey;

        try {
            out = new ObjectOutputStream(bos);
            out.writeObject(user.getPublicKey());
            publicKey = bos.toByteArray();
            privateKey = bos.toByteArray();

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
                bos.close();
            } catch (IOException ex) {

            }
        }*/

        if (this.getUser(user.getUuid()) == null){
            sql = "INSERT INTO users (uuid, username, realname, email, publickey, privatekey, online) VALUES ('"
                    +user.getUuid()+"', '"+user.getUsername()+"', '"+user.getRealname()+"', '"+user.getEmail()+"', '"
                    +user.getPublicKey()+"', '"+user.getPrivateKey()+"', '"+user.getOnline()+"');";
        }
        else{
            sql = "UPDATE users SET username='"+user.getUsername()+"', realname='"+user.getRealname()+"', email='"
                    +user.getEmail()+"', publickey='"+user.getPublicKey()+"', privatekey='"+user.getPrivateKey()+"', online="+user.getOnline()+";";
        }

        try {
            this.executeQuery(sql);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public User getUser(String uuid){
        String sql = "SELECT id, uuid, username, realname, email, publickey, privatekey, online FROM users WHERE uuid = '"+uuid+"'";
        User user = null;
        ResultSet resultSet;

        try {
            resultSet = this.executeQuery(sql);

            user = new User(resultSet.getInt("id"), resultSet.getString("uuid"), resultSet.getString("username"),
                    resultSet.getString("realname"), resultSet.getString("email"), resultSet.getString("publickey"), resultSet.getString("privatekey"), resultSet.getInt("online"));

        } catch (Exception e) {
            user = null;
        }

        return user;
    }

    public ArrayList<User> getUsers(){
        String sql = "SELECT id, uuid, username, realname, email, publickey, privatekey, online FROM users;";
        ArrayList<User> users = new ArrayList<User>();
        ResultSet resultSet;

        try {
            resultSet = this.executeQuery(sql);

            try{
                while(resultSet.next()){
                    users.add(new User(resultSet.getInt("id"), resultSet.getString("uuid"), resultSet.getString("username"), resultSet.getString("realname"), resultSet.getString("email"), resultSet.getString("publickey"), resultSet.getString("privatekey"), resultSet.getInt("online")));
                }
            }catch(Exception e){
                System.err.println("getUsers " + e.getClass().getName() + ": " + e.getMessage() );
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return users;
    }

    public ArrayList<User> getUsersFromFolder(SharedFolder folder){
        String sql = "SELECT id, uuid, username, realname, email, publickey, privatekey, online FROM users, folders_users WHERE users.uuid=id_user AND id_folder='"+folder.getUuid()+"';";
        ArrayList<User> users = new ArrayList<User>();
        ResultSet resultSet;

        try {
            resultSet = this.executeQuery(sql);

            try{
                while(resultSet.next()){
                    users.add(new User(resultSet.getInt("id"), resultSet.getString("uuid"), resultSet.getString("username"), resultSet.getString("realname"), resultSet.getString("email"), resultSet.getString("publickey"), resultSet.getString("privatekey"), resultSet.getInt("online")));
                }
            }catch(Exception e){
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return users;
    }

    public boolean exists(){
        Path path = Paths.get(dbPath);
        return Files.exists(path);
    }

    public String getDirDisha() {
        return dirDisha;
    }

	public static void main (String [ ] args)
	{
		InternalDBController dbController = new InternalDBController();
		dbController.configureDatabase();
		System.out.println("RootDir: " + dbController.getConfigParam("RootDir"));
		ArrayList<File> fileList = dbController.getFileList();
		for (int i=0; i<fileList.size(); i++){
			System.out.println(fileList.get(i).getName());
		}

	}
}
