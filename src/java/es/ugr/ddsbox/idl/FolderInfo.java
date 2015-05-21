
/*
  WARNING: THIS FILE IS AUTO-GENERATED. DO NOT MODIFY.

  This file was generated from .idl using "rtiddsgen".
  The rtiddsgen tool is part of the RTI Connext distribution.
  For more information, type 'rtiddsgen -help' at a command shell
  or consult the RTI Connext manual.
*/
    
package es.ugr.ddsbox.idl;
        

import com.rti.dds.infrastructure.*;
import com.rti.dds.infrastructure.Copyable;

import java.io.Serializable;
import com.rti.dds.cdr.CdrHelper;


public class FolderInfo implements Copyable, Serializable
{

    public String userUuid = ""; /* maximum length = ((es.ugr.ddsbox.idl.MAX_NAME.VALUE)) */
    public String destUser = ""; /* maximum length = ((es.ugr.ddsbox.idl.MAX_NAME.VALUE)) */
    public String owner = ""; /* maximum length = ((es.ugr.ddsbox.idl.MAX_NAME.VALUE)) */
    public String uuid = ""; /* maximum length = ((es.ugr.ddsbox.idl.MAX_NAME.VALUE)) */
    public String folderName = ""; /* maximum length = ((es.ugr.ddsbox.idl.MAX_NAME.VALUE)) */
    public short type;
    public short permission;
    public ByteSeq encryptedKey = new ByteSeq(((es.ugr.ddsbox.idl.MAX_PAYLOAD_SIZE.VALUE)));
    public ByteSeq content = new ByteSeq(((es.ugr.ddsbox.idl.MAX_PAYLOAD_SIZE.VALUE)));


    public FolderInfo() {

    }


    public FolderInfo(FolderInfo other) {

        this();
        copy_from(other);
    }



    public static Object create() {
        return new FolderInfo();
    }

    public boolean equals(Object o) {
                
        if (o == null) {
            return false;
        }        
        
        

        if(getClass() != o.getClass()) {
            return false;
        }

        FolderInfo otherObj = (FolderInfo)o;



        if(!userUuid.equals(otherObj.userUuid)) {
            return false;
        }
            
        if(!destUser.equals(otherObj.destUser)) {
            return false;
        }
            
        if(!owner.equals(otherObj.owner)) {
            return false;
        }
            
        if(!uuid.equals(otherObj.uuid)) {
            return false;
        }
            
        if(!folderName.equals(otherObj.folderName)) {
            return false;
        }
            
        if(type != otherObj.type) {
            return false;
        }
            
        if(permission != otherObj.permission) {
            return false;
        }
            
        if(!encryptedKey.equals(otherObj.encryptedKey)) {
            return false;
        }
            
        if(!content.equals(otherObj.content)) {
            return false;
        }
            
        return true;
    }

    public int hashCode() {
        int __result = 0;

        __result += userUuid.hashCode();
                
        __result += destUser.hashCode();
                
        __result += owner.hashCode();
                
        __result += uuid.hashCode();
                
        __result += folderName.hashCode();
                
        __result += (int)type;
                
        __result += (int)permission;
                
        __result += encryptedKey.hashCode();
                
        __result += content.hashCode();
                
        return __result;
    }
    

    /**
     * This is the implementation of the <code>Copyable</code> interface.
     * This method will perform a deep copy of <code>src</code>
     * This method could be placed into <code>FolderInfoTypeSupport</code>
     * rather than here by using the <code>-noCopyable</code> option
     * to rtiddsgen.
     * 
     * @param src The Object which contains the data to be copied.
     * @return Returns <code>this</code>.
     * @exception NullPointerException If <code>src</code> is null.
     * @exception ClassCastException If <code>src</code> is not the 
     * same type as <code>this</code>.
     * @see com.rti.dds.infrastructure.Copyable#copy_from(java.lang.Object)
     */
    public Object copy_from(Object src) {
        

        FolderInfo typedSrc = (FolderInfo) src;
        FolderInfo typedDst = this;

        typedDst.userUuid = typedSrc.userUuid;
            
        typedDst.destUser = typedSrc.destUser;
            
        typedDst.owner = typedSrc.owner;
            
        typedDst.uuid = typedSrc.uuid;
            
        typedDst.folderName = typedSrc.folderName;
            
        typedDst.type = typedSrc.type;
            
        typedDst.permission = typedSrc.permission;
            
        typedDst.encryptedKey.copy_from(typedSrc.encryptedKey);
            
        typedDst.content.copy_from(typedSrc.content);
            
        return this;
    }


    
    public String toString(){
        return toString("", 0);
    }
        
    
    public String toString(String desc, int indent) {
        StringBuffer strBuffer = new StringBuffer();        
                        
        
        if (desc != null) {
            CdrHelper.printIndent(strBuffer, indent);
            strBuffer.append(desc).append(":\n");
        }
        
        
        CdrHelper.printIndent(strBuffer, indent+1);            
        strBuffer.append("userUuid: ").append(userUuid).append("\n");
            
        CdrHelper.printIndent(strBuffer, indent+1);            
        strBuffer.append("destUser: ").append(destUser).append("\n");
            
        CdrHelper.printIndent(strBuffer, indent+1);            
        strBuffer.append("owner: ").append(owner).append("\n");
            
        CdrHelper.printIndent(strBuffer, indent+1);            
        strBuffer.append("uuid: ").append(uuid).append("\n");
            
        CdrHelper.printIndent(strBuffer, indent+1);            
        strBuffer.append("folderName: ").append(folderName).append("\n");
            
        CdrHelper.printIndent(strBuffer, indent+1);            
        strBuffer.append("type: ").append(type).append("\n");
            
        CdrHelper.printIndent(strBuffer, indent+1);            
        strBuffer.append("permission: ").append(permission).append("\n");
            
        CdrHelper.printIndent(strBuffer, indent+1);
        strBuffer.append("encryptedKey: ");
        for(int i__ = 0; i__ < encryptedKey.size(); ++i__) {
            if (i__!=0) strBuffer.append(", ");        
            strBuffer.append(encryptedKey.get(i__));
        }
        strBuffer.append("\n");
            
        CdrHelper.printIndent(strBuffer, indent+1);
        strBuffer.append("content: ");
        for(int i__ = 0; i__ < content.size(); ++i__) {
            if (i__!=0) strBuffer.append(", ");        
            strBuffer.append(content.get(i__));
        }
        strBuffer.append("\n");
            
        return strBuffer.toString();
    }
    
}

