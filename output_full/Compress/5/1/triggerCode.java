/*
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */
package org.apache.commons.compress.archivers.zip;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import org.apache.commons.compress.archivers.ArchiveEntry;
import junit.framework.TestCase;
/**
 * JUnit 3 testcase for a multi-volume zip file.
 * 
 * Some tools (like 7-zip) allow users to split a large archives into 'volumes'
 * with a given size to fit them into multiple cds, usb drives, or emails with
 * an attachment size limit. It's basically the same file split into chunks of
 * exactly 65536 bytes length. Concatenating volumes yields exactly the original
 * file. There is no mechanism in the ZIP algorithm to accommodate for this.
 * Before commons-compress used to enter an infinite loop on the last entry for
 * such a file. This test is intended to prove that this error doesn't occur
 * anymore. All entries but the last one are returned correctly, the last entry
 * yields an exception.
 * 
 */
public class Maven221MultiVolumeTest extends TestCase {
    public void testRead7ZipMultiVolumeArchiveForStream() throws IOException,
	    URISyntaxException {
	
	URL zip = getClass().getResource("/apache-maven-2.2.1.zip.001");
	FileInputStream archive = new FileInputStream(
		new File(new URI(zip.toString())));
	ZipArchiveInputStream zi = null;
	try {
	    zi = new ZipArchiveInputStream(archive,null,false);
	    
	    // these are the entries that are supposed to be processed
	    // correctly without any problems
	    for (int i = 0; i < ENTRIES.length; i++) {
		assertEquals(ENTRIES[i], zi.getNextEntry().getName());
	    }
	    
	    // this is the last entry that is truncated
	    ArchiveEntry lastEntry = zi.getNextEntry();
	    assertEquals(LAST_ENTRY_NAME, lastEntry.getName());
	    byte [] buffer = new byte [4096];
	    
	    // before the fix, we'd get 0 bytes on this read and all
	    // subsequent reads thus a client application might enter
	    // an infinite loop after the fix, we should get an
	    // exception
	    try {
                int read = 0;
		while ((read = zi.read(buffer)) > 0) { }
		fail("shouldn't be able to read from truncated entry");
	    } catch (IOException e) {
                assertEquals("Truncated ZIP file", e.getMessage());
	    }
	    
	    // and now we get another entry, which should also yield
	    // an exception
	    try {
		zi.getNextEntry();
		fail("shouldn't be able to read another entry from truncated" + " file");
	    } catch (IOException e) {
		// this is to be expected
	    }
	} finally {
	    if (zi != null) {
		zi.close();
	    }
	}
    }
}
