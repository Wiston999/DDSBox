
/*
  WARNING: THIS FILE IS AUTO-GENERATED. DO NOT MODIFY.

  This file was generated from .idl using "rtiddsgen".
  The rtiddsgen tool is part of the RTI Connext distribution.
  For more information, type 'rtiddsgen -help' at a command shell
  or consult the RTI Connext manual.
*/
    
package es.ugr.ddsbox.idl;
        
import com.rti.dds.typecode.*;


public class FileInfoTypeCode {
    public static final TypeCode VALUE = getTypeCode();

    private static TypeCode getTypeCode() {
        TypeCode tc = null;
        int i=0;
        StructMember sm[] = new StructMember[9];

        sm[i]=new StructMember("userUuid",false,(short)-1,false,(TypeCode)es.ugr.ddsbox.idl.nameTypeTypeCode.VALUE); i++;
        sm[i]=new StructMember("owner",false,(short)-1,false,(TypeCode)es.ugr.ddsbox.idl.nameTypeTypeCode.VALUE); i++;
        sm[i]=new StructMember("fileName",false,(short)-1,false,(TypeCode)es.ugr.ddsbox.idl.nameTypeTypeCode.VALUE); i++;
        sm[i]=new StructMember("timestamp",false,(short)-1,false,(TypeCode)TypeCode.TC_LONG); i++;
        sm[i]=new StructMember("size",false,(short)-1,false,(TypeCode)TypeCode.TC_LONG); i++;
        sm[i]=new StructMember("hash",false,(short)-1,false,(TypeCode)es.ugr.ddsbox.idl.nameTypeTypeCode.VALUE); i++;
        sm[i]=new StructMember("isDir",false,(short)-1,false,(TypeCode)TypeCode.TC_BOOLEAN); i++;
        sm[i]=new StructMember("change",false,(short)-1,false,(TypeCode)TypeCode.TC_SHORT); i++;
        sm[i]=new StructMember("content",false,(short)-1,false,(TypeCode)new TypeCode((es.ugr.ddsbox.idl.MAX_PAYLOAD_SIZE.VALUE),TypeCode.TC_OCTET)); i++;

        tc = TypeCodeFactory.TheTypeCodeFactory.create_struct_tc("es::ugr::ddsbox::idl::FileInfo",ExtensibilityKind.EXTENSIBLE_EXTENSIBILITY,sm);
        return tc;
    }
}
