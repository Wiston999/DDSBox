
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


public class FileInfo implements Copyable, Serializable
{

    public String userUuid = ""; /* maximum length = ((es.ugr.ddsbox.idl.MAX_NAME.VALUE)) */
    public String owner = ""; /* maximum length = ((es.ugr.ddsbox.idl.MAX_NAME.VALUE)) */
    public String fileName = ""; /* maximum length = ((es.ugr.ddsbox.idl.MAX_NAME.VALUE)) */
    public int timestamp;
    public int size;
    public String hash = ""; /* maximum length = ((es.ugr.ddsbox.idl.MAX_NAME.VALUE)) */
    public boolean isDir;
    public short change;
    public ByteSeq content = new ByteSeq(((es.ugr.ddsbox.idl.MAX_PAYLOAD_SIZE.VALUE)));


    public FileInfo() {

    }


    public FileInfo(FileInfo other) {

        this();
        copy_from(other);
    }



    public static Object create() {
        return new FileInfo();
    }

    public boolean equals(Object o) {
                
        if (o == null) {
            return false;
        }        
        
        

        if(getClass() != o.getClass()) {
            return false;
        }

        FileInfo otherObj = (FileInfo)o;



        if(!userUuid.equals(otherObj.userUuid)) {
            return false;
        }
            
        if(!owner.equals(otherObj.owner)) {
            return false;
        }
            
        if(!fileName.equals(otherObj.fileName)) {
            return false;
        }
            
        if(timestamp != otherObj.timestamp) {
            return false;
        }
            
        if(size != otherObj.size) {
            return false;
        }
            
        if(!hash.equals(otherObj.hash)) {
            return false;
        }
            
        if(isDir != otherObj.isDir) {
            return false;
        }
            
        if(change != otherObj.change) {
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
                
        __result += owner.hashCode();
                
        __result += fileName.hashCode();
                
        __result += (int)timestamp;
                
        __result += (int)size;
                
        __result += hash.hashCode();
                
        __result += (isDir == true)?1:0;
                
        __result += (int)change;
                
        __result += content.hashCode();
                
        return __result;
    }
    

    /**
     * This is the implementation of the <code>Copyable</code> interface.
     * This method will perform a deep copy of <code>src</code>
     * This method could be placed into <code>FileInfoTypeSupport</code>
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
        

        FileInfo typedSrc = (FileInfo) src;
        FileInfo typedDst = this;

        typedDst.userUuid = typedSrc.userUuid;
            
        typedDst.owner = typedSrc.owner;
            
        typedDst.fileName = typedSrc.fileName;
            
        typedDst.timestamp = typedSrc.timestamp;
            
        typedDst.size = typedSrc.size;
            
        typedDst.hash = typedSrc.hash;
            
        typedDst.isDir = typedSrc.isDir;
            
        typedDst.change = typedSrc.change;
            
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
        strBuffer.append("owner: ").append(owner).append("\n");
            
        CdrHelper.printIndent(strBuffer, indent+1);            
        strBuffer.append("fileName: ").append(fileName).append("\n");
            
        CdrHelper.printIndent(strBuffer, indent+1);            
        strBuffer.append("timestamp: ").append(timestamp).append("\n");
            
        CdrHelper.printIndent(strBuffer, indent+1);            
        strBuffer.append("size: ").append(size).append("\n");
            
        CdrHelper.printIndent(strBuffer, indent+1);            
        strBuffer.append("hash: ").append(hash).append("\n");
            
        CdrHelper.printIndent(strBuffer, indent+1);            
        strBuffer.append("isDir: ").append(isDir).append("\n");
            
        CdrHelper.printIndent(strBuffer, indent+1);            
        strBuffer.append("change: ").append(change).append("\n");
            
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

