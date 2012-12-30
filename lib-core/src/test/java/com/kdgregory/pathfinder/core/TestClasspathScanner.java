// Copyright (c) Keith D Gregory
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.kdgregory.pathfinder.core;

import java.util.Arrays;
import java.util.Map;

import org.junit.Test;
import static org.junit.Assert.*;

import org.apache.bcel.classfile.JavaClass;

import com.kdgregory.pathfinder.core.impl.ClasspathScannerImpl;
import com.kdgregory.pathfinder.test.WarNames;
import com.kdgregory.pathfinder.util.TestHelpers;


public class TestClasspathScanner
{
    @Test
    public void testUnfiltered() throws Exception
    {
        // this test is identical to TestWarMachine.testGetFilesOnClasspath()
        WarMachine machine = TestHelpers.createWarMachine(WarNames.SERVLET);

        ClasspathScannerImpl scanner = new ClasspathScannerImpl();

        Map<String,JavaClass> result = scanner.scan(machine);
        assertTrue("searching for file under WEB-INF", result.containsKey("com.example.servlet.SomeServlet"));
        assertTrue("searching for file in JAR",        result.containsKey("net.sf.practicalxml.DomUtil"));
    }


    @Test
    public void testBasePackageRecursive() throws Exception
    {
        WarMachine machine = TestHelpers.createWarMachine(WarNames.SERVLET);

        ClasspathScannerImpl scanner = new ClasspathScannerImpl()
                                       .addBasePackage("com.example", true);

        Map<String,JavaClass> result = scanner.scan(machine);
        assertEquals("number of files found", 1, result.size());
        assertTrue("searching for file under WEB-INF", result.containsKey("com.example.servlet.SomeServlet"));
    }


    @Test
    public void testBasePackageNonRecursive() throws Exception
    {
        WarMachine machine = TestHelpers.createWarMachine(WarNames.SERVLET);

        ClasspathScannerImpl scanner = new ClasspathScannerImpl()
                                       .addBasePackage("com.example", false);

        Map<String,JavaClass> result = scanner.scan(machine);
        assertEquals("number of files found", 0, result.size());
    }


    @Test
    public void testMultipleBasePackagesOneAtATime() throws Exception
    {
        WarMachine machine = TestHelpers.createWarMachine(WarNames.SPRING_ANNO);

        ClasspathScannerImpl scanner = new ClasspathScannerImpl()
                                       .addBasePackage("com.kdgregory.pathfinder.test.spring3.pkg1", false)
                                       .addBasePackage("com.kdgregory.pathfinder.test.spring3.pkg2", false);

        Map<String,Boolean> packages = scanner.getBasePackages();
        assertEquals("packages in scan", 2, packages.size());
        assertEquals("expected pkg1", Boolean.FALSE, packages.get("com.kdgregory.pathfinder.test.spring3.pkg1"));
        assertEquals("expected pkg2", Boolean.FALSE, packages.get("com.kdgregory.pathfinder.test.spring3.pkg2"));

        Map<String,JavaClass> result = scanner.scan(machine);
        assertEquals("number of files found", 6, result.size());
        assertTrue("expected ControllerA", result.containsKey("com.kdgregory.pathfinder.test.spring3.pkg1.ControllerA"));
        assertTrue("expected ControllerB", result.containsKey("com.kdgregory.pathfinder.test.spring3.pkg2.ControllerB"));
        assertTrue("expected ControllerC", result.containsKey("com.kdgregory.pathfinder.test.spring3.pkg2.ControllerC"));
        assertTrue("expected ControllerD", result.containsKey("com.kdgregory.pathfinder.test.spring3.pkg2.ControllerD"));
        assertTrue("expected ControllerE", result.containsKey("com.kdgregory.pathfinder.test.spring3.pkg2.ControllerE"));
        assertTrue("expected Dummy",       result.containsKey("com.kdgregory.pathfinder.test.spring3.pkg1.Dummy"));
    }


    @Test
    public void testMultipleBasePackagesAllAtOnce() throws Exception
    {
        WarMachine machine = TestHelpers.createWarMachine(WarNames.SPRING_ANNO);

        ClasspathScannerImpl scanner = new ClasspathScannerImpl()
                                           .addBasePackages(Arrays.asList(
                                               "com.kdgregory.pathfinder.test.spring3.pkg1",
                                               "com.kdgregory.pathfinder.test.spring3.pkg2"),
                                               false);

        Map<String,JavaClass> files = scanner.scan(machine);
        assertEquals("number of files found", 6, files.size());
        assertTrue("expected ControllerA", files.containsKey("com.kdgregory.pathfinder.test.spring3.pkg1.ControllerA"));
        assertTrue("expected ControllerB", files.containsKey("com.kdgregory.pathfinder.test.spring3.pkg2.ControllerB"));
        assertTrue("expected ControllerC", files.containsKey("com.kdgregory.pathfinder.test.spring3.pkg2.ControllerC"));
        assertTrue("expected ControllerD", files.containsKey("com.kdgregory.pathfinder.test.spring3.pkg2.ControllerD"));
        assertTrue("expected ControllerE", files.containsKey("com.kdgregory.pathfinder.test.spring3.pkg2.ControllerE"));
        assertTrue("expected Dummy",       files.containsKey("com.kdgregory.pathfinder.test.spring3.pkg1.Dummy"));
    }


    @Test
    public void testAnnotationFilter() throws Exception
    {
        WarMachine machine = TestHelpers.createWarMachine(WarNames.SPRING_ANNO);

        ClasspathScannerImpl scanner = new ClasspathScannerImpl()
                                       .addBasePackage("com.kdgregory.pathfinder.test.spring3")
                                       .setIncludedAnnotations("org.springframework.stereotype.Controller");

        Map<String,JavaClass> result = scanner.scan(machine);
        assertEquals("number of files found", 5, result.size());
        assertTrue("expected ControllerA", result.containsKey("com.kdgregory.pathfinder.test.spring3.pkg1.ControllerA"));
        assertTrue("expected ControllerB", result.containsKey("com.kdgregory.pathfinder.test.spring3.pkg2.ControllerB"));
        assertTrue("expected ControllerC", result.containsKey("com.kdgregory.pathfinder.test.spring3.pkg2.ControllerC"));
        assertTrue("expected ControllerD", result.containsKey("com.kdgregory.pathfinder.test.spring3.pkg2.ControllerD"));
        assertTrue("expected ControllerE", result.containsKey("com.kdgregory.pathfinder.test.spring3.pkg2.ControllerE"));
        // no Dummy
    }
}
