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

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.apache.bcel.classfile.ClassParser;
import org.apache.bcel.classfile.JavaClass;

import net.sf.kdgcommons.io.IOUtil;

import com.kdgregory.bcelx.classfile.Annotation;
import com.kdgregory.bcelx.parser.AnnotationParser;


/**
 *  This class contains the logic to scan a WAR's classpath, applying zero or
 *  more filters to the files. An unconfigured instance (one without filters)
 *  returns all files on the classpath. After configuration, instances are
 *  thread-safe.
 */
public class ClasspathScanner
{
    private List<String> basePackages;
    private boolean includeSubPackages;
    private Set<String> includedAnnotations;


//----------------------------------------------------------------------------
//  Configuration Methods
//----------------------------------------------------------------------------

    /**
     *  Sets a single base package for the scan, including sub-packages.
     */
    public ClasspathScanner setBasePackage(String packageName)
    {
        return setBasePackage(packageName, true);
    }


    /**
     *  Sets a single base package for the scan, optionally including sub-packages.
     */
    public ClasspathScanner setBasePackage(String packageName, boolean includeSubPackages)
    {
        return setBasePackages(Arrays.asList(packageName), includeSubPackages);
    }


    /**
     *  Sets multiple base packages for the scan, optionally including sub-packages.
     */
    public ClasspathScanner setBasePackages(List<String> packageNames, boolean includeSubPackages)
    {
        this.basePackages = new ArrayList<String>(packageNames.size());
        for (String packageName : packageNames)
        {
            basePackages.add(packageName.replace('.', '/'));
        }
        this.includeSubPackages = includeSubPackages;
        return this;
    }


    /**
     *  Filters selected classes by the specified class-level, runtime-visible
     *  marker annotations.
     */
    public ClasspathScanner setIncludedAnnotations(String... annotationClasses)
    {
        includedAnnotations = new HashSet<String>();
        includedAnnotations.addAll(Arrays.asList(annotationClasses));
        return this;
    }


//----------------------------------------------------------------------------
//  Operational Methods
//----------------------------------------------------------------------------
    /**
     *  Perform the scan.
     */
    public Set<String> scan(WarMachine war)
    {
        // returns a TreeSet to simplify debugging; it's not part of the contract
        Set<String> result = new TreeSet<String>();
        for (String file :  war.getFilesOnClasspath())
        {
            boolean include = applyBasePackageFilter(file)
                           && applyIncludedAnnotationFilter(war, file);
            if (include)
                result.add(file);
        }
        return result;
    }


//----------------------------------------------------------------------------
//  Filterns
//----------------------------------------------------------------------------

    private boolean applyBasePackageFilter(String filename)
    {
        if (basePackages == null)
            return true;
        if (!filename.endsWith(".class"))
            return false;
        for (String basePackage : basePackages)
        {
            if (!filename.startsWith(basePackage))
                continue;
            if (!includeSubPackages && (filename.lastIndexOf("/") > basePackage.length()))
                continue;
            return true;
        }
        return false;
    }


    private boolean applyIncludedAnnotationFilter(WarMachine war, String filename)
    {
        if (includedAnnotations == null)
            return true;

        if (!filename.endsWith(".class"))
            return false;

        AnnotationParser ap = parseAnnotations(war, filename);
        for (Annotation anno : ap.getClassVisibleAnnotations())
        {
            if (includedAnnotations.contains(anno.getClassName()))
                return true;
        }
        return false;
    }

//----------------------------------------------------------------------------
//  Other Intenals
//----------------------------------------------------------------------------

    private AnnotationParser parseAnnotations(WarMachine war, String classFileName)
    {
        InputStream in = null;
        try
        {
            in = war.openClasspathFile(classFileName);
            String className = classFileName.replace('/', '.');
            JavaClass parsedClass = new ClassParser(in, className).parse();
            return new AnnotationParser(parsedClass);
        }
        catch (Exception ex)
        {
            throw new RuntimeException("unable to parse file: " + classFileName, ex);
        }
        finally
        {
            IOUtil.closeQuietly(in);
        }
    }
}
