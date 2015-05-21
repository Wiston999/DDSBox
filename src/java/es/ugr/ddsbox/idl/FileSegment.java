
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


public class FileSegment implements Copyable, Serializable
{

    public String userUuid = ""; /* maximum length = ((es.ugr.ddsbox.idl.MAX_NAME.VALUE)) */
    public String hash = ""; /* maximum length = ((es.ugr.ddsbox.idl.MAX_NAME.VALUE)) */
    public String fileName = ""; /* maximum length = ((es.ugr.ddsbox.idl.MAX_NAME.VALUE)) */
    public int idSegment;
    public ByteSeq segmentContent = new ByteSeq(((es.ugr.ddsbox.idl.MAX_PAYLOAD_SIZE.VALUE)));


    public FileSegment() {

    }


    public FileSegment(FileSegment other) {

        this();
        copy_from(other);
    }



    public static Object create() {
        return new FileSegment();
    }

    public boolean equals(Object o) {
                
        if (o == null) {
            return false;
        }        
        
        

        if(getClass() != o.getClass()) {
            return false;
        }

        FileSegment otherObj = (FileSegment)o;



        if(!userUuid.equals(otherObj.userUuid)) {
            return false;
        }
            
        if(!hash.equals(otherObj.hash)) {
            return false;
        }
            
        if(!fileName.equals(otherObj.fileName)) {
            return false;
        }
            
        if(idSegment != otherObj.idSegment) {
            return false;
        }
            
        if(!segmentContent.equals(otherObj.segmentContent)) {
            return false;
        }
            
        return true;
    }

    public int hashCode() {
        int __result = 0;

        __result += userUuid.hashCode();
                
        __result += hash.hashCode();
                
        __result += fileName.hashCode();
                
        __result += (int)idSegment;
                
        __result += segmentContent.hashCode();
                
        return __result;
    }
    

    /**
     * This is the implementation of the <code>Copyable</code> interface.
     * This method will perform a deep copy of <code>src</code>
     * This method could be placed into <code>FileSegmentTypeSupport</code>
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
        

        FileSegment typedSrc = (FileSegment) src;
        FileSegment typedDst = this;

        typedDst.userUuid = typedSrc.userUuid;
            
        typedDst.hash = typedSrc.hash;
            
        typedDst.fileName = typedSrc.fileName;
            
        typedDst.idSegment = typedSrc.idSegment;
            
        typedDst.segmentContent.copy_from(typedSrc.segmentContent);
            
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
        strBuffer.append("hash: ").append(hash).append("\n");
            
        CdrHelper.printIndent(strBuffer, indent+1);            
        strBuffer.append("fileName: ").append(fileName).append("\n");
            
        CdrHelper.printIndent(strBuffer, indent+1);            
        strBuffer.append("idSegment: ").append(idSegment).append("\n");
            
        CdrHelper.printIndent(strBuffer, indent+1);
        strBuffer.append("segmentContent: ");
        for(int i__ = 0; i__ < segmentContent.size(); ++i__) {
            if (i__!=0) strBuffer.append(", ");        
            strBuffer.append(segmentContent.get(i__));
        }
        strBuffer.append("\n");
            
        return strBuffer.toString();
    }
    
}

