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

import static java.nio.file.LinkOption.NOFOLLOW_LINKS;
import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_DELETE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public class FileSystemDaemon implements Runnable{

    private final WatchService watcher;
    private final Map<WatchKey, Path> keys;
    private final boolean recursive;
    private boolean trace = false;
    private static String workDirs;
    private static String filters = "";
    private Logger logger = Logger.getLogger("LoggerFileSystemDaemon");
    private MainController mainController;
    private volatile Boolean stop = false;
    @SuppressWarnings("unchecked")
    static <T> WatchEvent<T> cast(WatchEvent<?> event) {
        return (WatchEvent<T>) event;
    }

    /**
     * Register the given directory with the WatchService
     */
    private void register(Path dir) throws IOException {
        WatchKey key = dir.register(watcher, ENTRY_CREATE, ENTRY_DELETE,
                ENTRY_MODIFY);
        if (trace) {
            Path prev = keys.get(key);
            if (prev == null) {
                logger.finest("register: "+dir+"\n");
            } else {
                if (!dir.equals(prev)) {
                    logger.finest(String.format("update: %s -> %s\n", prev, dir));
                }
            }
        }
        keys.put(key, dir);
    }

    public void stop(){
        stop = true;
    }
    /**
     * Register the given directory, and all its sub-directories, with the
     * WatchService.
     */
    private void registerAll(final Path start) throws IOException {
        // register directory and sub-directories
        Files.walkFileTree(start, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult preVisitDirectory(Path dir,
                                                     BasicFileAttributes attrs) throws IOException {
                register(dir);
                return FileVisitResult.CONTINUE;
            }
        });
    }

    /**
     * Creates a WatchService and registers the given directory
     */
    FileSystemDaemon(String dirs, boolean recursive, MainController mC) throws IOException {
        this.watcher = FileSystems.getDefault().newWatchService();
        this.keys = new HashMap<WatchKey, Path>();
        this.recursive = recursive;
        System.out.println(dirs);

        Path path = Paths.get(dirs);
        if (recursive) {
            logger.finest(String.format("Scanning %s ...\n", dirs));
            registerAll(path);
            logger.finest(String.format("Done."));
        } else {
            register(path);
        }

        // enable trace after initial registration
        this.trace = true;
        this.mainController = mC;
    }

    /**
     * Process all events for keys queued to the watcher
     */
    public void run() {
        while (!stop) {
            // wait for key to be signalled
            WatchKey key;
            try {
                key = watcher.take();
            } catch (InterruptedException x) {
                System.out.println("File System Deamon interrupted");
                return;
            }
            Path dir = keys.get(key);
            if (dir == null) {
                logger.warning(String.format("WatchKey not recognized!!"));
                continue;
            }
            for (WatchEvent<?> event : key.pollEvents()) {
                WatchEvent.Kind kind = event.kind();

                // TBD - provide example of how OVERFLOW event is handled
               /* if (kind == OVERFLOW) {
                    continue;
                }*/
                // Context for directory entry event is the file name of entry
                WatchEvent<Path> ev = cast(event);
                Path name = ev.context();
                Path child = dir.resolve(name);

                if (child
                        .toString()
                        .substring(child.toString().lastIndexOf(File.separator))
                        .startsWith("Nuev ")) {
                    logger.info(String.format("Skipping default new name:"
                            + child.toString()));
                    continue;
                }
                Path childAbsolute = child.toAbsolutePath();
                String pathToFile = childAbsolute.toString().substring(mainController.getRootDir().length()).replace('\\', '/');
                //Wait until file is not changing, this is useful when copying big files
                if(kind == ENTRY_MODIFY || kind == ENTRY_CREATE) {
                    try {
                        long previousSize = Files.size(childAbsolute);
                        Thread.sleep(1000);
                        while (previousSize != Files.size(childAbsolute)) {
                            previousSize = Files.size(childAbsolute);
                            Thread.sleep(1000);
                        }
                    } catch (Exception e) {

                    }
                }
                // print out event
                for (String ext : filters.split(",")) {
                    if (child.toString().endsWith(ext)
                            || child.toFile().isDirectory()) {
                        logger.finest(String.format("Processing %s: %s\n", event.kind()
                                .name(), child));

                        // if directory is created, and watching recursively,
                        // then
                        // register it and its sub-directories
                        if (recursive && (kind == ENTRY_CREATE)) {
                            try {
                                if (Files.isDirectory(child, NOFOLLOW_LINKS)) {
                                    registerAll(child);
                                }
                            } catch (IOException x) {
                                // ignore to keep sample readable
                            }
                        }
                        if(kind == ENTRY_CREATE){
                            mainController.fileSystemChangesSlot(new changeInfo(pathToFile, FileSystemController.changes.CREATE, Files.isDirectory(childAbsolute, NOFOLLOW_LINKS)));
                        }else if(!(new java.io.File(child.toString()).isDirectory()) && (new java.io.File(child.toString()).exists()) && kind == ENTRY_MODIFY){
                            mainController.fileSystemChangesSlot(new changeInfo(pathToFile, FileSystemController.changes.MODIFY, Files.isDirectory(childAbsolute, NOFOLLOW_LINKS)));
                        }else if(kind == ENTRY_DELETE){
                            mainController.fileSystemChangesSlot(new changeInfo(pathToFile, FileSystemController.changes.DELETE, Files.isDirectory(childAbsolute, NOFOLLOW_LINKS)));
                        }
                        break;
                    }
                }

            }

            // reset key and remove from set if directory no longer accessible
            boolean valid = key.reset();
            if (!valid) {
                keys.remove(key);

                // all directories are inaccessible
                if (keys.isEmpty()) {
                    break;
                }
            }
        }
    }

    static void usage() {
        System.err
                .println("usage: java FileSystemDaemon dir");
        System.exit(-1);
    }

    public static void main(String[] args) throws IOException {
        if (args.length == 0 || args.length > 7)
            usage();
        boolean recursive = true;

        workDirs = args[0];
        // register directory and process its events
        new FileSystemDaemon(workDirs, recursive, null).run();
    }
}
