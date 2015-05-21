
/*
  WARNING: THIS FILE IS AUTO-GENERATED. DO NOT MODIFY.

  This file was generated from .idl using "rtiddsgen".
  The rtiddsgen tool is part of the RTI Connext distribution.
  For more information, type 'rtiddsgen -help' at a command shell
  or consult the RTI Connext manual.
*/
    
package es.ugr.ddsbox.idl;
        
import com.rti.dds.typecode.*;


public class FileSegmentTypeCode {
    public static final TypeCode VALUE = getTypeCode();

    private static TypeCode getTypeCode() {
        TypeCode tc = null;
        int i=0;
        StructMember sm[] = new StructMember[5];

        sm[i]=new StructMember("userUuid",false,(short)-1,false,(TypeCode)es.ugr.ddsbox.idl.nameTypeTypeCode.VALUE); i++;
        sm[i]=new StructMember("hash",false,(short)-1,false,(TypeCode)es.ugr.ddsbox.idl.nameTypeTypeCode.VALUE); i++;
        sm[i]=new StructMember("fileName",false,(short)-1,true,(TypeCode)es.ugr.ddsbox.idl.nameTypeTypeCode.VALUE); i++;
        sm[i]=new StructMember("idSegment",false,(short)-1,true,(TypeCode)TypeCode.TC_LONG); i++;
        sm[i]=new StructMember("segmentContent",false,(short)-1,false,(TypeCode)new TypeCode((es.ugr.ddsbox.idl.MAX_PAYLOAD_SIZE.VALUE),TypeCode.TC_OCTET)); i++;

        tc = TypeCodeFactory.TheTypeCodeFactory.create_struct_tc("es::ugr::ddsbox::idl::FileSegment",ExtensibilityKind.EXTENSIBLE_EXTENSIBILITY,sm);
        return tc;
    }
}
