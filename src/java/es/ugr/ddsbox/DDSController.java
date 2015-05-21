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

import com.rti.dds.domain.DomainParticipant;
import com.rti.dds.domain.DomainParticipantFactory;
import com.rti.dds.infrastructure.*;
import com.rti.dds.publication.Publisher;
import com.rti.dds.subscription.Subscriber;
import com.rti.dds.topic.ContentFilteredTopic;
import com.rti.dds.topic.Topic;
import es.ugr.ddsbox.dds.*;
import es.ugr.ddsbox.idl.*;
import es.ugr.ddsbox.idl.Command;
import es.ugr.ddsbox.idl.User;
import es.ugr.ddsbox.models.*;
import es.ugr.ddsbox.utils.FileUtils;
import es.ugr.ddsbox.utils.SecurityUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;

public class DDSController {


    private MainController mC;

    private HashMap<String, CommandDataReader> commandReaders = new HashMap<String, CommandDataReader>();
    private HashMap<String, FileInfoDataReader> fileInfoReaders = new HashMap<String, FileInfoDataReader>();
    private HashMap<String, FileSegmentDataReader> fileSegmentReaders = new HashMap<String, FileSegmentDataReader>();
    private HashMap<String, CommandDataWriter> commandWriters = new HashMap<String, CommandDataWriter>();
    private HashMap<String, FileInfoDataWriter> fileInfoWriters = new HashMap<String, FileInfoDataWriter>();
    private HashMap<String, FileSegmentDataWriter> fileSegmentWriters = new HashMap<String, FileSegmentDataWriter>();

    private FolderInfoDataReader folderInfoReader = null;
    private FolderInfoDataWriter folderInfoWriter = null;
    private UserDataReader userReader = null;
    private UserDataWriter userWriter = null;

    private DomainParticipant participant;
    private Subscriber subscriber;
    private Publisher publisher;

    private CommandListener commandListener;
    private FileInfoListener fileInfoListener;
    private FileSegmentListener fileSegmentListener;
    private FolderInfoListener folderInfoListener;
    private UserListener userListener;

    public DDSController(int domID, MainController mC){
        participant = null;
        subscriber = null;
        publisher = null;

        this.mC = mC;

        ArrayList<SharedFolder> sharedFolders = mC.sqliteDB.getSharedFolders();

        commandListener = new CommandListener(mC);
        fileInfoListener = new FileInfoListener(mC);
        fileSegmentListener = new FileSegmentListener(mC);
        folderInfoListener = new FolderInfoListener(mC);
        userListener = new UserListener(mC);

        participant = DomainParticipantFactory.get_instance().create_participant(
                domID,
                DomainParticipantFactory.PARTICIPANT_QOS_DEFAULT,
                null,                           // no listener
                StatusKind.STATUS_MASK_NONE);   // no listener callbacks

        if (participant == null) {
            System.err.println("! Unable to create DomainParticipant");
            return;
        }

        publisher = participant.create_publisher(
                DomainParticipant.PUBLISHER_QOS_DEFAULT, null /* listener */,
                StatusKind.STATUS_MASK_NONE);
        if (publisher == null) {
            System.err.println("create_publisher error\n");
            return;
        }

        subscriber = participant.create_subscriber(
                DomainParticipant.SUBSCRIBER_QOS_DEFAULT, null /* listener */,
                StatusKind.STATUS_MASK_NONE);
        if (subscriber == null) {
            System.err.println("create_subscriber error\n");
            return;
        }

        FileInfoTypeSupport.register_type(participant, FileInfoTypeSupport.get_type_name());
        FileSegmentTypeSupport.register_type(participant, FileSegmentTypeSupport.get_type_name());
        CommandTypeSupport.register_type(participant, CommandTypeSupport.get_type_name());
        FolderInfoTypeSupport.register_type(participant, FolderInfoTypeSupport.get_type_name());
        UserTypeSupport.register_type(participant, UserTypeSupport.get_type_name());

        for(SharedFolder folder : sharedFolders){
            if(folder.isSuscribed()){
                this.addSharedFolder(folder);
            }
        }

        StringSeq noFilterParams = new StringSeq();
        Topic topicFolderInfo = participant.create_topic(
                "folderInfo",
                FolderInfoTypeSupport.get_type_name(), DomainParticipant.TOPIC_QOS_DEFAULT,
                null /* listener */, StatusKind.STATUS_MASK_NONE);
        if (topicFolderInfo == null) {
            System.err.println("create_topic error\n");
            return;
        }

        ContentFilteredTopic filterFolderInfo = participant.create_contentfilteredtopic(
                "folderInfoFiltered",
                topicFolderInfo,
                "(destUser='"+mC.userUuid+"' or destUser='')",
                noFilterParams);

        folderInfoWriter = (FolderInfoDataWriter)publisher.create_datawriter(
                topicFolderInfo, Publisher.DATAWRITER_QOS_DEFAULT,
                null /* listener */, StatusKind.STATUS_MASK_NONE);
        if (folderInfoWriter == null) {
            System.err.println("FolderInfo create_datawriter error\n");
            return;
        }


        folderInfoReader = (FolderInfoDataReader)
                subscriber.create_datareader(
                        filterFolderInfo, Subscriber.DATAREADER_QOS_DEFAULT, folderInfoListener,
                        StatusKind.STATUS_MASK_ALL);
        if (folderInfoReader == null) {
            System.err.println("create_datareader error\n");
            return;
        }

        Topic topicUser ;
        topicUser = participant.create_topic(
                "user",
                UserTypeSupport.get_type_name(), DomainParticipant.TOPIC_QOS_DEFAULT,
                null, StatusKind.STATUS_MASK_NONE);
        if (topicUser == null) {
            System.err.println("create_topic error\n");
            return;
        }

        ContentFilteredTopic filterUser = participant.create_contentfilteredtopic(
                "userFiltered",
                topicUser,
                "userUuid<>'"+mC.userUuid+"'",
                noFilterParams);

        userWriter = (UserDataWriter)publisher.create_datawriter(
                topicUser, Publisher.DATAWRITER_QOS_DEFAULT,
                null, StatusKind.STATUS_MASK_NONE);
        if (userWriter == null) {
            System.err.println("User create_datawriter error\n");
            return;
        }


        userReader = (UserDataReader)
                subscriber.create_datareader(
                        topicUser, Subscriber.DATAREADER_QOS_DEFAULT, userListener,
                        StatusKind.STATUS_MASK_ALL);
        if (userReader == null) {
            System.err.println("create_datareader error\n");
            return;
        }

    }

    public void addSharedFolder(SharedFolder folder){
        String uuid = folder.getUuid();
        StringSeq noFilterParams = new StringSeq();
        String qosProfile = "Disha_Profile";
        switch (folder.getPersistence()){
            case SharedFolder.VOLATILE:
                qosProfile = "volatile_profile";
            break;
            case SharedFolder.TRANSIENT:
                qosProfile = "transient_profile";
                break;
            case SharedFolder.PERSISTENT:
                qosProfile = "persistent_profile";
                break;
        }
        System.out.println("Cargada la "+uuid+" carpeta con persistencia: " +qosProfile);
        Topic topicCommand = participant.create_topic(
                "command-"+uuid,
                CommandTypeSupport.get_type_name(), DomainParticipant.TOPIC_QOS_DEFAULT,
                null /* listener */, StatusKind.STATUS_MASK_NONE);
        if (topicCommand == null) {
            System.err.println("create_topic error\n");
            return;
        }



        Topic topicFileInfo = participant.create_topic(
                "fileInfo-" + uuid,
                FileInfoTypeSupport.get_type_name(), DomainParticipant.TOPIC_QOS_DEFAULT,
                null /* listener */, StatusKind.STATUS_MASK_NONE);
        if (topicFileInfo == null) {
            System.err.println("create_topic error\n");
            return;
        }

        Topic topicFileSegment = participant.create_topic(
                "fileSegment-"+uuid,
                FileSegmentTypeSupport.get_type_name(), DomainParticipant.TOPIC_QOS_DEFAULT,
                null /* listener */, StatusKind.STATUS_MASK_NONE);
        if (topicFileSegment == null) {
            System.err.println("create_topic error\n");
            return;
        }

        ContentFilteredTopic filterCommand = participant.create_contentfilteredtopic(
                "commandFiltered-"+uuid,
                topicCommand,
                "userUuid<>'"+mC.userUuid+"'",
                noFilterParams);
        ContentFilteredTopic filterFileInfo = participant.create_contentfilteredtopic(
                "fileInfoFiltered-"+uuid,
                topicFileInfo,
                "userUuid<>'"+mC.userUuid+"'",
                noFilterParams);
        ContentFilteredTopic filterFileSegment = participant.create_contentfilteredtopic(
                "segmentsFiltered-"+uuid,
                topicFileSegment,
                "userUuid<>'"+mC.userUuid+"'",
                noFilterParams);

        FileSegmentDataWriter fileSegmentWriter = (FileSegmentDataWriter)publisher.create_datawriter_with_profile(
                topicFileSegment, "Disha_Default_Library", qosProfile,
                null /* listener */, StatusKind.STATUS_MASK_NONE);
        if (fileSegmentWriter == null) {
            System.err.println("FileSegment create_datawriter error\n");
            return;
        }

        FileInfoDataWriter fileInfoWriter = (FileInfoDataWriter)publisher.create_datawriter_with_profile(
                topicFileInfo, "Disha_Default_Library", qosProfile,
                null /* listener */, StatusKind.STATUS_MASK_NONE);
        if (fileInfoWriter == null) {
            System.err.println("FileInfo create_datawriter error\n");
            return;
        }

        CommandDataWriter commandWriter = (CommandDataWriter)publisher.create_datawriter_with_profile(
                topicCommand, "Disha_Default_Library", qosProfile,
                null /* listener */, StatusKind.STATUS_MASK_NONE);
        if (commandWriter == null) {
            System.err.println("Command create_datawriter error\n");
            return;
        }

        fileSegmentWriters.put(uuid, fileSegmentWriter);
        fileInfoWriters.put(uuid, fileInfoWriter);
        commandWriters.put(uuid, commandWriter);

        CommandDataReader commandReader = (CommandDataReader)
                subscriber.create_datareader_with_profile(
                        filterCommand, "Disha_Default_Library", qosProfile, commandListener,
                        StatusKind.STATUS_MASK_ALL);
        if (commandReader == null) {
            System.err.println("create_datareader error\n");
            return;
        }

        FileInfoDataReader fileInfoReader = (FileInfoDataReader)
                subscriber.create_datareader_with_profile(
                        filterFileInfo, "Disha_Default_Library", qosProfile, fileInfoListener,
                        StatusKind.STATUS_MASK_ALL);
        if (fileInfoReader == null) {
            System.err.println("create_datareader error\n");
            return;
        }

        FileSegmentDataReader fileSegmentReader = (FileSegmentDataReader)
                subscriber.create_datareader_with_profile(
                        filterFileSegment, "Disha_Default_Library", qosProfile, fileSegmentListener,
                        StatusKind.STATUS_MASK_ALL);
        if (fileSegmentReader == null) {
            System.err.println("create_datareader error\n");
            return;
        }

        commandReaders.put(uuid, commandReader);
        fileInfoReaders.put(uuid, fileInfoReader);
        fileSegmentReaders.put(uuid, fileSegmentReader);

    }

    public void publishCommand(String uuid, int idCommand, String parameters){
        Command instance = new Command();
        InstanceHandle_t instance_handle = InstanceHandle_t.HANDLE_NIL;

        instance.idCommand = (short)idCommand;
        instance.userUuid = mC.userUuid;
        instance.parameters = parameters;

        commandWriters.get(uuid).write(instance, instance_handle);
    }

    public void publishFileInfo(SharedFolder folder, File file){
        FileInfo instance = new FileInfo();
        InstanceHandle_t instance_handle = InstanceHandle_t.HANDLE_NIL;

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutput out = null;

        try {
            out = new ObjectOutputStream(bos);
            out.writeObject(file);
            byte[] binaryFileInfo = bos.toByteArray();

            if(folder.getType() == SharedFolder.PRIVATE){
                binaryFileInfo = SecurityUtils.encryptAES(folder.getKey(), binaryFileInfo);
            }

            instance.userUuid = mC.userUuid;
            instance.fileName = file.getName();
            /*instance.size = (int)file.getSize();
            instance.hash = file.getHash();
            instance.timestamp = (int)file.getTimeLastVersion().getTime();
            instance.isDir = file.isDir();
            instance.change = (short)changeCode.ordinal();*/
            instance.content.addAllByte(binaryFileInfo);

            if(fileSegmentWriters.containsKey(folder.getUuid())) {
                fileInfoWriters.get(folder.getUuid()).write(instance, instance_handle);
                System.out.println(String.format("Publicado FileInfo: file=%s", file.getName()));
            }

        } catch(Exception e){
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException ex) {
                // ignore close exception
            }
            try {
                bos.close();
            } catch (IOException ex) {
                // ignore close exception
            }
        }

    }

    public void publishFileSegment(SharedFolder folder, String filename, long idSegment){
        FileSegment instance = new FileSegment();
        InstanceHandle_t instance_handle = InstanceHandle_t.HANDLE_NIL;

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutput out = null;

        try {

            File file = new File();
            file.setName(filename);
            FileUtils.readInfo(file);
            byte[] binaryFileContent = FileUtils.readSegment(file, idSegment);

            if(folder.getType() == SharedFolder.PRIVATE){
                binaryFileContent = SecurityUtils.encryptAES(folder.getKey(), binaryFileContent);
            }

            instance.userUuid = mC.userUuid;
            instance.fileName = filename;
            instance.idSegment = (int)idSegment;
            instance.segmentContent.addAllByte(binaryFileContent);

            if(fileSegmentWriters.containsKey(folder.getUuid())) {
                fileSegmentWriters.get(folder.getUuid()).write(instance, instance_handle);
                System.out.println(String.format("Publicado FileSegment: userName=%s id=%d", mC.userUuid, idSegment));
            }

        } catch(Exception e){
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException ex) {
                // ignore close exception
            }
            try {
                bos.close();
            } catch (IOException ex) {
                // ignore close exception
            }
        }

    }

    public void publishFolderInfo(SharedFolder folder, es.ugr.ddsbox.models.User destUser){
        FolderInfo instance = new FolderInfo();
        InstanceHandle_t instance_handle = InstanceHandle_t.HANDLE_NIL;

        String destUUID = "";

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutput out = null;

        try {
            out = new ObjectOutputStream(bos);
            out.writeObject(folder);
            byte[] binaryFolder = bos.toByteArray();

            if(destUser!=null){
                String key = SecurityUtils.generateKeyAES();
                binaryFolder = SecurityUtils.encryptAES(key, binaryFolder);

                byte [] binkey = SecurityUtils.encryptRSA(destUser.getPublicKey(), key.getBytes());
                instance.encryptedKey.addAllByte(binkey);
                destUUID = destUser.getUuid();
            }

            instance.userUuid = mC.userUuid;
            instance.destUser = destUUID;
            instance.content.addAllByte(binaryFolder);

            folderInfoWriter.write(instance, instance_handle);

        } catch(Exception e){
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException ex) {
                // ignore close exception
            }
            try {
                bos.close();
            } catch (IOException ex) {
                // ignore close exception
            }
        }

    }

    public void publishUser(es.ugr.ddsbox.models.User user){
        User instance = new User();
        InstanceHandle_t instance_handle = InstanceHandle_t.HANDLE_NIL;

        System.out.println(String.format("Publicado Usuario"));
        instance.uuid = user.getUuid();
        instance.userName = user.getUsername();
        instance.realName = user.getRealname();
        instance.email = user.getEmail();
        instance.publicRSA = user.getPublicKey();
        instance.online = (short)user.getOnline();

        userWriter.write(instance, instance_handle);
    }

    public void terminate(){
        if(participant != null){
            participant.delete_contained_entities();
            DomainParticipantFactory.get_instance().delete_participant(participant);
        }
    }
}
