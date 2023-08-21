/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.commons.compress.archivers;
import static org.apache.commons.compress.AbstractTestCase.getFile;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import org.apache.commons.compress.archivers.arj.ArjArchiveInputStream;
import org.apache.commons.compress.archivers.cpio.CpioArchiveInputStream;
import org.apache.commons.compress.archivers.dump.DumpArchiveInputStream;
import org.apache.commons.compress.archivers.jar.JarArchiveInputStream;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.archivers.zip.ZipArchiveInputStream;
import org.junit.Test;
public class ArchiveStreamFactoryTest {
    private static boolean eq(String exp, String act) {
        if (exp == null) {
            return act == null;
        }
        return exp.equals(act);
    }
    private static String getField(Object instance, String name) {
        Class<?> cls = instance.getClass();
        Field fld;
        try {
            fld = cls.getDeclaredField(name);
        } catch (NoSuchFieldException nsfe) {
                try {
                    fld = cls.getSuperclass().getDeclaredField(name);
                } catch (NoSuchFieldException e) {
                    System.out.println("Cannot find " + name + " in class " + instance.getClass().getSimpleName());
                    return "??";
                }                
        }
        boolean isAccessible = fld.isAccessible();
        try {
            if (!isAccessible) {
                fld.setAccessible(true);
            }
            final Object object = fld.get(instance);
            if (object instanceof String || object == null) {
                return (String) object;
            } else {
                System.out.println("Wrong type: " + object.getClass().getCanonicalName() + " for " + name + " in class " + instance.getClass().getSimpleName());
                return "??";                
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "??";
        } finally {
            if (!isAccessible) {
                fld.setAccessible(isAccessible);
            }
        }
    }
    private ArchiveInputStream getInputStreamFor(String resource, ArchiveStreamFactory factory)
            throws IOException, ArchiveException {
        return factory.createArchiveInputStream(
                   new BufferedInputStream(new FileInputStream(
                       getFile(resource))));
    }
    private ArchiveInputStream getInputStreamFor(String type, String resource, ArchiveStreamFactory factory)
            throws IOException, ArchiveException {
        return factory.createArchiveInputStream(
                   type,
                   new BufferedInputStream(new FileInputStream(
                       getFile(resource))));
    }
    private ArchiveOutputStream getOutputStreamFor(String type, ArchiveStreamFactory factory)
            throws IOException, ArchiveException {
        return factory.createArchiveOutputStream(type, new ByteArrayOutputStream());
    }
    @Test
    public void testEncodingInputStream() throws Exception {
        int failed = 0;
        for(int i = 1; i <= TESTS.length; i++) {
            TestData test = TESTS[i-1];
            ArchiveInputStream ais = getInputStreamFor(test.type, test.testFile, test.fac);
            final String field = getField(ais,test.fieldName);
            if (!eq(test.expectedEncoding,field)) {
                System.out.println("Failed test " + i + ". expected: " + test.expectedEncoding + " actual: " + field + " type: " + test.type);
                failed++;
            }
        }
        if (failed > 0) { fail("Tests failed: " + failed);
        }
    }
}
