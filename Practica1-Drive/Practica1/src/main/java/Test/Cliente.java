/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Test;

import java.io.File;

/**
 *
 * @author Reina
 */
public class Cliente {
    
     String localParentDir;
     String remoteParentDir;
    
    File localDir = new File(localParentDir);
    File[] subFiles = localDir.listFiles();
    if (subFiles != null && subFiles.length > 0) {
        for (File item : subFiles) {
            String remoteFilePath = remoteDirPath + "/" + remoteParentDir + "/" + item.getName();
            if (remoteParentDir.equals("")) {
                remoteFilePath = remoteDirPath + "/" + item.getName();
            }
        }
    }
}
