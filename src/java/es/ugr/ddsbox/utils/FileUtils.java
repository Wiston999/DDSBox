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

package es.ugr.ddsbox.utils;

import es.ugr.ddsbox.models.File;

import javax.xml.bind.annotation.adapters.HexBinaryAdapter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.logging.Logger;

public class FileUtils {
    private static Logger logger = Logger.getLogger("LoggerFileUtils");
    private static String rootDir = "";

    public static void setRootDir(String rD){
        rootDir = rD;
    }

    public static void createEmptyFile(File file, long s){
        file.setSize(s);

        try {
            RandomAccessFile f = new RandomAccessFile(rootDir+file.getName(), "rw");
            if(file.getTamLastSegment()>0)
                f.setLength(file.getSegmentSize()*(file.getNumSegments()-1)+file.getTamLastSegment());
            else
                f.setLength(file.getSegmentSize()*file.getNumSegments());
            f.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        file.setSegmentsArray(new ArrayList<Short>((int)file.getNumSegments()));

        for(int i=0; i<file.getNumSegments(); i++)
            file.getSegments().add((short) 0);
    }

    public static void readInfo(File file){
        try{
            Path path = Paths.get(rootDir+file.getName());
            file.setSize(Files.size(path));
        }
        catch (Exception e){
            e.printStackTrace();
        }

        file.setSegmentSize(calcSegSize(file.getSize()));

        file.setSegmentsArray(new ArrayList<Short>((int)file.getNumSegments()));
        for(int i=0; i<file.getNumSegments(); i++)
            file.getSegments().add((short)1);
        System.out.println(file.getSegments());
    }

    public static byte[] readSegment(File file, long seg){
        ByteBuffer buffer;
        FileChannel fc = null;
        RandomAccessFile raf = null;

        if(seg == file.getNumSegments()-1)
            buffer = ByteBuffer.allocate((int)file.getTamLastSegment());
        else
            buffer = ByteBuffer.allocate((int)file.getSegmentSize());

        while(true) {
            try {
                raf = new RandomAccessFile(rootDir + file.getName(), "r");
                fc = raf.getChannel();
                fc.position(seg * file.getSegmentSize());
                fc.read(buffer);
                fc.close();
                raf.close();
                break;
            } catch (FileNotFoundException ex) {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return buffer.array();
    }

    public static synchronized boolean writeSegment(File file, long seg, byte[] cont){
        ByteBuffer buffer = ByteBuffer.wrap(cont);

        try{
            //Abre para lectura y escritura pero hace que se fuerze la escritura ?? leido de la documentacion de RandomAccessFile
            RandomAccessFile raf = new RandomAccessFile(rootDir+file.getName(), "rwd");
            FileChannel fc = raf.getChannel();
            fc.position(seg*file.getSegmentSize());
            fc.write(buffer);
            fc.close();
            raf.close();
        }
        catch(Exception e){
            e.printStackTrace();
        }

        file.getSegments().set((int)seg, (short)1);
        file.checkCompleted();
        return true;
    }

    public static long getSizeFromFile(String fileName){
        long size = 0;

        try{
            Path path = Paths.get(fileName);
            size = Files.size(path);
        }
        catch (Exception e){
            e.printStackTrace();
        }

        return size;
    }

    public static String getHashFromFile(String fileName){
        //fileName = rootDir + fileName;
        String hash = "";
        InputStream is = null;
        java.io.File fileToRead = new java.io.File(fileName);
        if( (new java.io.File(fileName)).exists()){
            try{
                while(!fileToRead.renameTo(fileToRead)){
                    logger.warning(String.format("Esperando para poder leer el archivo %s 1 segundo", fileName));
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                MessageDigest md = MessageDigest.getInstance("MD5");
                byte[] buffer = new byte[512*1024]; //Bloques de 512kB en 512kB
                is = Files.newInputStream(Paths.get(fileName));
                int sizeLastRead = 0;
                while ((sizeLastRead = is.read(buffer)) > 0){
                    md.update(buffer, 0, sizeLastRead);
                }
                byte[] digest = md.digest();
                hash = (new HexBinaryAdapter()).marshal(digest);
            }
            catch (Exception e){
                e.printStackTrace();
            }
            finally {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return hash;
    }

    private static long calcSegSize(long size){
        /*if(size<=100*1024*1024)
            return 1024*1024;
        else
            return 5*1024*1024;*/
        return 1024*1024;
    }

    public static boolean isDirectory(String filename){
        Path path = Paths.get(rootDir+filename);
        if(Files.isDirectory(path))
            return true;

        return false;
    }

    public static boolean exists(String filename){
        Path path = Paths.get(rootDir+filename);
        if(Files.exists(path))
            return true;

        return false;
    }

    public static void createDir(String filename){
        Path path = Paths.get(rootDir+filename);
        try {
            Files.createDirectory(path);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void removeDir(String filename){
        Path path = Paths.get(rootDir+filename);
        try {
            //Files.delete(path);

            Files.walkFileTree(path, new SimpleFileVisitor<Path>()
            {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs)
                        throws IOException
                {
                    Files.delete(file);
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException
                {
                    // try to delete the file anyway, even if its attributes
                    // could not be read, since delete-only access is
                    // theoretically possible
                    Files.delete(file);
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException
                {
                    if (exc == null)
                    {
                        Files.delete(dir);
                        return FileVisitResult.CONTINUE;
                    }
                    else
                    {
                        // directory iteration failed; propagate exception
                        throw exc;
                    }
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
