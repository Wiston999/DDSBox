
/*
  WARNING: THIS FILE IS AUTO-GENERATED. DO NOT MODIFY.

  This file was generated from .idl using "rtiddsgen".
  The rtiddsgen tool is part of the RTI Connext distribution.
  For more information, type 'rtiddsgen -help' at a command shell
  or consult the RTI Connext manual.
*/
    
package es.ugr.ddsbox.idl;
        

import com.rti.dds.cdr.CdrEncapsulation;
import com.rti.dds.cdr.CdrInputStream;
import com.rti.dds.cdr.CdrOutputStream;
import com.rti.dds.cdr.CdrPrimitiveType;
import com.rti.dds.domain.DomainParticipant;
import com.rti.dds.publication.DataWriter;
import com.rti.dds.publication.DataWriterListener;
import com.rti.dds.subscription.DataReader;
import com.rti.dds.subscription.DataReaderListener;
import com.rti.dds.topic.TypeSupportImpl;
import com.rti.dds.topic.TypeSupportType;
import com.rti.dds.infrastructure.RETCODE_ERROR;

    
import com.rti.dds.topic.TypeSupportParticipantInfo;
import com.rti.dds.topic.TypeSupportEndpointInfo;
import com.rti.dds.typecode.TypeCode;


/**
 * A collection of useful methods for dealing with objects of type
 * UserMessage.
 */
public class UserMessageTypeSupport extends TypeSupportImpl {
    // -----------------------------------------------------------------------
    // Private Fields
    // -----------------------------------------------------------------------

    private static final String TYPE_NAME = "UserMessage";

    private static final char[] PLUGIN_VERSION = {2, 0, 0, 0};

    
    public static final int LAST_MEMBER_ID = 0;
        

    private static final UserMessageTypeSupport _singleton
        = new UserMessageTypeSupport();
    
    // -----------------------------------------------------------------------
    // Public Methods
    // -----------------------------------------------------------------------

    // --- External methods: -------------------------------------------------
    /* The methods in this section are for use by users of RTI Connext
     */

    public static String get_type_name() {
        return _singleton.get_type_nameI();
    }

    public static void register_type(DomainParticipant participant,
                                     String type_name) {
        _singleton.register_typeI(participant, type_name);
    }

    public static void unregister_type(DomainParticipant participant,
                                       String type_name) {
        _singleton.unregister_typeI(participant, type_name);
    }

    
     /* The methods in this section are for use by RTI Connext
     * itself and by the code generated by rtiddsgen for other types.
     * They should be used directly or modified only by advanced users and are
     * subject to change in future versions of RTI Connext.
     */
    public static UserMessageTypeSupport get_instance() {
        return _singleton;
    }

    public static UserMessageTypeSupport getInstance() {
        return get_instance();
    }

    public Object create_data() {
        return new UserMessage();
    }

    public void destroy_data(Object data) {
        return;
    }

    public Object create_key() {
        return new UserMessage();
    }

    public void destroy_key(Object key) {
        return;
    }

    public Class get_type() {
        return UserMessage.class;
    }
    
    /**
     * This is a concrete implementation of this method inherited from the base class.
     * This method will perform a deep copy of <code>source</code> into
     * <code>destination</code>.
     * 
     * @param src The Object which contains the data to be copied.
     * @return Returns <code>destination</code>.
     * @exception NullPointerException If <code>destination</code> or 
     * <code>source</code> is null.
     * @exception ClassCastException If either <code>destination</code> or
     * <code>this</code> is not a <code>UserMessage</code>
     * type.
     */
    public Object copy_data(Object destination, Object source) {
        UserMessage typedDst = (UserMessage) destination;
        UserMessage typedSrc = (UserMessage) source;

        return typedDst.copy_from(typedSrc);
    
    }


    
    public long get_serialized_sample_max_size(Object endpoint_data,boolean include_encapsulation,short encapsulation_id,long currentAlignment) {
        long origAlignment = currentAlignment;

        long encapsulation_size = currentAlignment;


        if(include_encapsulation) {
          if (!CdrEncapsulation.isValidEncapsulationKind(encapsulation_id)) {
              throw new RETCODE_ERROR("Unsupported encapsulation");
          }

          encapsulation_size += CdrPrimitiveType.SHORT.getMaxSizeSerialized(encapsulation_size);
          encapsulation_size += CdrPrimitiveType.SHORT.getMaxSizeSerialized(encapsulation_size);
          encapsulation_size -= currentAlignment;
          currentAlignment = 0;
          origAlignment = 0;

        }

 

        currentAlignment +=  CdrPrimitiveType.getStringMaxSizeSerialized(currentAlignment, (((es.ugr.ddsbox.idl.MAX_NAME.VALUE))) + 1);
            
        currentAlignment +=  CdrPrimitiveType.getStringMaxSizeSerialized(currentAlignment, (((es.ugr.ddsbox.idl.MAX_NAME.VALUE))) + 1);
            
        currentAlignment +=  CdrPrimitiveType.getStringMaxSizeSerialized(currentAlignment, (((es.ugr.ddsbox.idl.MAX_NAME.VALUE))) + 1);
            
        currentAlignment +=  CdrPrimitiveType.INT.getMaxSizeSerialized(currentAlignment);
            
        currentAlignment +=  CdrPrimitiveType.getStringMaxSizeSerialized(currentAlignment, (((es.ugr.ddsbox.idl.MAX_NAME.VALUE))) + 1);
            
        currentAlignment +=  CdrPrimitiveType.SHORT.getMaxSizeSerialized(currentAlignment);
            
        currentAlignment +=  CdrPrimitiveType.getStringMaxSizeSerialized(currentAlignment, (((es.ugr.ddsbox.idl.MAX_PARAMETER.VALUE))) + 1);
            
        if (include_encapsulation) {
            currentAlignment += encapsulation_size;
        }
    
        return currentAlignment - origAlignment;
    }

    
    public long get_serialized_sample_min_size(Object endpoint_data,boolean include_encapsulation,short encapsulation_id,long currentAlignment) {
        long origAlignment = currentAlignment;

        long encapsulation_size = currentAlignment;
       
    
        if(include_encapsulation) {
            if (!CdrEncapsulation.isValidEncapsulationKind(encapsulation_id)) {
                throw new RETCODE_ERROR("Unsupported encapsulation");
            }

            encapsulation_size += CdrPrimitiveType.SHORT.getMaxSizeSerialized(encapsulation_size);
            encapsulation_size += CdrPrimitiveType.SHORT.getMaxSizeSerialized(encapsulation_size);
            encapsulation_size -= currentAlignment;
            currentAlignment = 0;
            origAlignment = 0;

        }
        
        
        currentAlignment +=  CdrPrimitiveType.getStringMaxSizeSerialized(currentAlignment, 1);
            
        currentAlignment +=  CdrPrimitiveType.getStringMaxSizeSerialized(currentAlignment, 1);
            
        currentAlignment +=  CdrPrimitiveType.getStringMaxSizeSerialized(currentAlignment, 1);
            
        currentAlignment +=  CdrPrimitiveType.INT.getMaxSizeSerialized(currentAlignment);
            
        currentAlignment +=  CdrPrimitiveType.getStringMaxSizeSerialized(currentAlignment, 1);
            
        currentAlignment +=  CdrPrimitiveType.SHORT.getMaxSizeSerialized(currentAlignment);
            
        currentAlignment +=  CdrPrimitiveType.getStringMaxSizeSerialized(currentAlignment, 1);
            
        if (include_encapsulation) {
            currentAlignment += encapsulation_size;
        }
    
        return currentAlignment - origAlignment;
    }

    
    public long get_serialized_sample_size(
	Object endpoint_data, boolean include_encapsulation, 
        short encapsulation_id, long currentAlignment,
	Object sample) 
    {
        long origAlignment = currentAlignment;

        long encapsulation_size = currentAlignment;
        UserMessage typedSrc = (UserMessage) sample;
    
        if(include_encapsulation) {
            if (!CdrEncapsulation.isValidEncapsulationKind(encapsulation_id)) {
                throw new RETCODE_ERROR("Unsupported encapsulation");
            }


            encapsulation_size += CdrPrimitiveType.SHORT.getMaxSizeSerialized(encapsulation_size);
            encapsulation_size += CdrPrimitiveType.SHORT.getMaxSizeSerialized(encapsulation_size);
            encapsulation_size -= currentAlignment;
            currentAlignment = 0;
            origAlignment = 0;

        }
        

        currentAlignment += CdrPrimitiveType.getStringSerializedSize(currentAlignment, typedSrc.destUser);
            
        currentAlignment += CdrPrimitiveType.getStringSerializedSize(currentAlignment, typedSrc.userName);
            
        currentAlignment += CdrPrimitiveType.getStringSerializedSize(currentAlignment, typedSrc.text);
            
        currentAlignment += CdrPrimitiveType.INT.getMaxSizeSerialized(currentAlignment);
            
        currentAlignment += CdrPrimitiveType.getStringSerializedSize(currentAlignment, typedSrc.reciever);
            
        currentAlignment += CdrPrimitiveType.SHORT.getMaxSizeSerialized(currentAlignment);
            
        currentAlignment += CdrPrimitiveType.getStringSerializedSize(currentAlignment, typedSrc.parameters);
            
        if (include_encapsulation) {
            currentAlignment += encapsulation_size;
        }

        return currentAlignment - origAlignment;
    }

    
    public long get_serialized_key_max_size(
        Object endpoint_data,
        boolean include_encapsulation, 
        short encapsulation_id,
        long currentAlignment) 
    {

        long encapsulation_size = currentAlignment;

        long origAlignment = currentAlignment;
                

        if(include_encapsulation) {
            if (!CdrEncapsulation.isValidEncapsulationKind(encapsulation_id)) {
                throw new RETCODE_ERROR("Unsupported encapsulation");
            }


            encapsulation_size += CdrPrimitiveType.SHORT.getMaxSizeSerialized(encapsulation_size);
            encapsulation_size += CdrPrimitiveType.SHORT.getMaxSizeSerialized(encapsulation_size);
            encapsulation_size -= currentAlignment;
            currentAlignment = 0;
            origAlignment = 0;

        }


    currentAlignment += get_serialized_sample_max_size(
                            endpoint_data,false,encapsulation_id,currentAlignment);
    
        if (include_encapsulation) {
            currentAlignment += encapsulation_size;
        }

        return currentAlignment - origAlignment;
    }

    
    public void serialize(Object endpoint_data,Object src, CdrOutputStream dst,boolean serialize_encapsulation,
                          short encapsulation_id, boolean serialize_sample, Object endpoint_plugin_qos) {
        int position = 0;
        

        if(serialize_encapsulation) {
        
            dst.serializeAndSetCdrEncapsulation(encapsulation_id);;


            position = dst.resetAlignment();

        }


        if(serialize_sample) {
UserMessage typedSrc = (UserMessage) src;    

        dst.writeString(typedSrc.destUser,((es.ugr.ddsbox.idl.MAX_NAME.VALUE)));
            
        dst.writeString(typedSrc.userName,((es.ugr.ddsbox.idl.MAX_NAME.VALUE)));
            
        dst.writeString(typedSrc.text,((es.ugr.ddsbox.idl.MAX_NAME.VALUE)));
            
        dst.writeInt(typedSrc.timestamp);        
            
        dst.writeString(typedSrc.reciever,((es.ugr.ddsbox.idl.MAX_NAME.VALUE)));
            
        dst.writeShort(typedSrc.idCommand);        
            
        dst.writeString(typedSrc.parameters,((es.ugr.ddsbox.idl.MAX_PARAMETER.VALUE)));
            
        }


        if (serialize_encapsulation) {
          dst.restoreAlignment(position);
        }
    
    }
 
    
    public void serialize_key(
        Object endpoint_data,
        Object src,
        CdrOutputStream dst,
        boolean serialize_encapsulation,
        short encapsulation_id,
        boolean serialize_key,
        Object endpoint_plugin_qos) 
    {
        int position = 0;

        if (serialize_encapsulation) {
            
            dst.serializeAndSetCdrEncapsulation(encapsulation_id);


            position = dst.resetAlignment();

        }

        if (serialize_key) {
UserMessage typedSrc = (UserMessage) src;    

            serialize(endpoint_data, src, dst, false, CdrEncapsulation.CDR_ENCAPSULATION_ID_CDR_BE, true, endpoint_plugin_qos);
    
        }


        if (serialize_encapsulation) {
            dst.restoreAlignment(position);
        }

    }

    
    public Object deserialize_sample(
        Object endpoint_data,
        Object dst, 
        CdrInputStream src, boolean deserialize_encapsulation,
        boolean deserialize_sample,
        Object endpoint_plugin_qos) 
    {
        int position = 0;
        
        if(deserialize_encapsulation) {
            src.deserializeAndSetCdrEncapsulation();


            position = src.resetAlignment();

        }

        if(deserialize_sample) {
UserMessage typedDst = (UserMessage) dst;

    try {

            typedDst.destUser = src.readString(((es.ugr.ddsbox.idl.MAX_NAME.VALUE)));
            
            typedDst.userName = src.readString(((es.ugr.ddsbox.idl.MAX_NAME.VALUE)));
            
            typedDst.text = src.readString(((es.ugr.ddsbox.idl.MAX_NAME.VALUE)));
            
            typedDst.timestamp = src.readInt();
            
            typedDst.reciever = src.readString(((es.ugr.ddsbox.idl.MAX_NAME.VALUE)));
            
            typedDst.idCommand = src.readShort();
            
            typedDst.parameters = src.readString(((es.ugr.ddsbox.idl.MAX_PARAMETER.VALUE)));
            
    } catch (Exception e) {
        if (src.available() > 0) {
            throw new RETCODE_ERROR("Error deserializing sample! Remainder: " + src.available() + "\n" +
                                                              "Exception caused by: " + e.getMessage());
        }
    }

        }


        if (deserialize_encapsulation) {
            src.restoreAlignment(position);
        }


        return dst;
    }


    
    public Object deserialize_key_sample(
        Object endpoint_data,
        Object dst,
        CdrInputStream src,
        boolean deserialize_encapsulation,
        boolean deserialize_key,
        Object endpoint_plugin_qos) 
    {
        int position = 0;


        if(deserialize_encapsulation) {
            src.deserializeAndSetCdrEncapsulation();


            position = src.resetAlignment();

        }

        if(deserialize_key) {
UserMessage typedDst = (UserMessage) dst;


        deserialize_sample(endpoint_data, dst, src, false, true, endpoint_plugin_qos);
    
        }


        if (deserialize_encapsulation) {
            src.restoreAlignment(position);
        }


        return dst;
    }

    
    public void skip(Object endpoint_data, 
                     CdrInputStream src,
                     boolean skip_encapsulation, 
                     boolean skip_sample, 
                     Object endpoint_plugin_qos)
    {
        int position = 0;


        if (skip_encapsulation) {
            src.skipEncapsulation();


            position = src.resetAlignment();

        }

        if (skip_sample) {

            src.skipString();
            
            src.skipString();
            
            src.skipString();
            
            src.skipInt();
            
            src.skipString();
            
            src.skipShort();
            
            src.skipString();
            
        }


        if (skip_encapsulation) {
            src.restoreAlignment(position);
        }

    }

    public Object serialized_sample_to_key(
        Object endpoint_data,
        Object sample,
        CdrInputStream src, 
        boolean deserialize_encapsulation,  
        boolean deserialize_key, 
        Object endpoint_plugin_qos) 
    {
        int position = 0;

        if(deserialize_encapsulation) {
            src.deserializeAndSetCdrEncapsulation();

            position = src.resetAlignment();

        }

        if (deserialize_key) {
UserMessage typedDst = (UserMessage) sample;


            deserialize_sample(
                endpoint_data, sample, src, false, 
                true, endpoint_plugin_qos);

        }


        if (deserialize_encapsulation) {
            src.restoreAlignment(position);
        }


        return sample;
    }



    // -----------------------------------------------------------------------
    // Callbacks
    // -----------------------------------------------------------------------

    public Object on_participant_attached(Object registration_data,
                                          TypeSupportParticipantInfo participant_info,
                                          boolean top_level_registration,
                                          Object container_plugin_context,
                                          TypeCode type_code) {
        return super.on_participant_attached(
            registration_data, participant_info, top_level_registration,
            container_plugin_context, type_code);
    }

    public void on_participant_detached(Object participant_data) {
        super.on_participant_detached(participant_data);
    }

    public Object on_endpoint_attached(Object participantData,
                                       TypeSupportEndpointInfo endpoint_info,
                                       boolean top_level_registration,
                                       Object container_plugin_context) {
        return super.on_endpoint_attached(
              participantData,  endpoint_info,  
              top_level_registration, container_plugin_context);        
    }

    public void on_endpoint_detached(Object endpoint_data) {
        super.on_endpoint_detached(endpoint_data);
    }

    // -----------------------------------------------------------------------
    // Protected Methods
    // -----------------------------------------------------------------------

    protected DataWriter create_datawriter(long native_writer,
                                           DataWriterListener listener,
                                           int mask) {
        
        return new UserMessageDataWriter(native_writer, listener, mask, this);                
            
    }

    protected DataReader create_datareader(long native_reader,
                                           DataReaderListener listener,
                                           int mask) {
        
        return new UserMessageDataReader(native_reader, listener, mask, this);                
            
    }

    // -----------------------------------------------------------------------
    // Constructor
    // -----------------------------------------------------------------------

    protected UserMessageTypeSupport() {
        
        /* If the user data type supports keys, then the second argument
        to the constructor below should be true.  Otherwise it should
        be false. */        

        super(TYPE_NAME, false,UserMessageTypeCode.VALUE,UserMessage.class,TypeSupportType.TST_STRUCT, PLUGIN_VERSION);
    
    }

    protected UserMessageTypeSupport(boolean enableKeySupport) {
    
        super(TYPE_NAME, enableKeySupport,UserMessageTypeCode.VALUE,UserMessage.class,TypeSupportType.TST_STRUCT, PLUGIN_VERSION);
    }
}
