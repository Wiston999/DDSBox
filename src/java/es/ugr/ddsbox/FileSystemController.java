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
import es.ugr.ddsbox.utils.FileUtils;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public class FileSystemController{
    public enum changes {CREATE, MODIFY, DELETE, RENAME};
    public static final short CREATE = 0;
    public static final short MODIFY = 1;
    public static final short DELETE = 2;
    public static final short RENAME = 3;
    private Logger logger = Logger.getLogger("LoggerFileSystemController");
	static int open;
	private String rootPath;
	int directoriesMonitored;
	Thread workingThread;
	MainController mainController;

	public FileSystemController(String p, MainController mC){
		this.rootPath = p;
		this.directoriesMonitored = 0;
		this.mainController = mC;
	}

	public String getRootPath(){
		return this.rootPath;
	}

    public ArrayList<changeInfo> scanDirectory(HashMap<String, File> databaseFiles){
        return this.scanDirectory(mainController.getRootDir(), databaseFiles, 0);
    }

	public ArrayList<changeInfo> scanDirectory(String dir, HashMap<String, File> databaseFiles, int recursionLevel){
		ArrayList<changeInfo> changesList = new ArrayList<changeInfo>();
        String dirNormalized = dir.replace('\\', '/');
		System.out.println("scanDirectory: "+dirNormalized);
		java.io.File directory;
		directory = new java.io.File(dirNormalized);
        String dirRelative;
        if(dir.compareTo(rootPath) != 0){
            dirRelative = dirNormalized.substring(rootPath.length());
        }else{
            dirRelative = "/";
        }
		if (directory.exists()){    // does p actually exist?
			java.io.File fileList[] = directory.listFiles();
            //Metemos el directorio para que aparezca en la base de datos, no pasa nada si ya estaba, y si ha sido borrado se especifica mas abajo
            //Esto es solo para que no pete (porque peta aunque no me acuerdo donde exactamente)
            changesList.add(new changeInfo(dirRelative, FileSystemController.changes.CREATE, true));
            databaseFiles.remove(dirRelative);
			for (int i=0; i< fileList.length; i++){
				System.out.println(fileList[i].toString()+": "+fileList[i].isFile()+" "+fileList[i].isDirectory());
				if(fileList[i].isFile()){

					String path = fileList[i].getPath().replace('\\', '/');
                    String filename = path.substring(rootPath.length());
					String newHash = "";

					java.util.Date date= new java.util.Date();
					Timestamp timestamp = new Timestamp(date.getTime());

					if (!fileList[i].isDirectory()){
						newHash = FileUtils.getHashFromFile(filename);
					}
					//System.out.println(newHash+" "+databaseFiles.get(filename).getHash());
					if (databaseFiles.get(filename) == null){	//File exists but is not in SqliteDB
						//Report changes to mainController
						changesList.add(new changeInfo(filename, FileSystemController.changes.CREATE, fileList[i].isDirectory()));
					}else if(databaseFiles.get(filename).getHash().compareTo(newHash) != 0){
						changesList.add(new changeInfo(filename, FileSystemController.changes.MODIFY, fileList[i].isDirectory()));
					}

                /*
                Delete in temporary data structure this file
                At the end, if databaseFiles is not empty it means that there was files
                deleted while the application was not running
                */
                    databaseFiles.remove(filename);
				}else if(fileList[i].isDirectory()){
					changesList.addAll(this.scanDirectory(fileList[i].getAbsolutePath(), databaseFiles, recursionLevel+1));

				}
			}
		}else{
			//Root directory does not exists, so it was deleted
            logger.warning(String.format("Directorio %s no encontrado", dirNormalized));
			changesList.add(new changeInfo(dirRelative, FileSystemController.changes.DELETE, true));
		}

		if( !databaseFiles.isEmpty() && recursionLevel == 0){
			for(Map.Entry entry: databaseFiles.entrySet()){

				changesList.add(new changeInfo((String) entry.getKey(),
                        FileSystemController.changes.DELETE,
                        new java.io.File((String) entry.getKey()).isDirectory())
                );
			}
		}
		return changesList;
	}

    /*private String getFolder(String filename){
        String folder = "";
        InternalDBController sqliteDb = InternalDBController.getInstance();

        String folder_prov = filename.

        ArrayList<SharedFolder> folders = sqliteDb.getSharedFolders();
        for(int i=0; i<folders.size(); i++){

        }
    }*/
}
