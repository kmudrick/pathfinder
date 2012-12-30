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

package com.kdgregory.pathfinder.util;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.apache.bcel.classfile.JavaClass;

import net.sf.kdgcommons.lang.StringUtil;

import com.kdgregory.bcelx.classfile.Annotation;
import com.kdgregory.bcelx.parser.AnnotationParser;
import com.kdgregory.pathfinder.core.WarMachine;


/**
 *  This class contains the logic to scan a WAR's classpath, applying zero or
 *  more filters to the classes found there. An unconfigured instance (one
 *  without filters) returns all classes on the classpath.
 */
public class ClasspathScanner
{
    private Map<String,Boolean> basePackages;   // packageName -> recurse
    private Set<String> includedAnnotations;

//----------------------------------------------------------------------------
//  ClasspathScanner
//----------------------------------------------------------------------------

    public ClasspathScanner addBasePackage(String packageName, boolean includeSubPackages)
    {
        if (basePackages == null)
            basePackages = new HashMap<String,Boolean>();

        basePackages.put(packageName, Boolean.valueOf(includeSubPackages));
        return this;
    }


    public ClasspathScanner addBasePackage(String packageName)
    {
        return addBasePackage(packageName, true);
    }


    public ClasspathScanner addBasePackages(Collection<String> packageNames, boolean includeSubPackages)
    {
        for (String packageName : packageNames)
        {
            addBasePackage(packageName, includeSubPackages);
        }
        return this;
    }


    public ClasspathScanner setIncludedAnnotations(String... annotationClasses)
    {
        includedAnnotations = new HashSet<String>();
        includedAnnotations.addAll(Arrays.asList(annotationClasses));
        return this;
    }


    public Map<String,JavaClass> scan(WarMachine war)
    {
        // FIXME - should return AnnotationParser rather than JavaClass

        // a TreeMap is easier for debugging: all scanned classes are in order
        Map<String,JavaClass> result = new TreeMap<String,JavaClass>();

        for (String fileName :  war.getFilesOnClasspath())
        {
            if (! fileName.endsWith(".class"))
                continue;

            String className = StringUtil.extractLeftOfLast(fileName, ".class").replace("/", ".");
            if (! applyBasePackageFilter(className))
                continue;

            JavaClass klass = war.loadClass(className);
            if (! applyIncludedAnnotationFilter(klass))
                continue;

            result.put(className, klass);
        }
        return result;
    }


//----------------------------------------------------------------------------
//  Public Accessor methods -- used for testing and debugging
//----------------------------------------------------------------------------

    public Map<String,Boolean> getBasePackages()
    {
        return (basePackages == null)
             ? Collections.<String,Boolean>emptyMap()
             : Collections.unmodifiableMap(basePackages);
    }


    public Set<String> getIncludedAnnotations()
    {
        return (includedAnnotations == null)
             ? Collections.<String>emptySet()
             : Collections.unmodifiableSet(includedAnnotations);
    }


//----------------------------------------------------------------------------
//  Filters
//----------------------------------------------------------------------------

    private boolean applyBasePackageFilter(String filename)
    {
        if (basePackages == null)
            return true;

        for (Map.Entry<String,Boolean> entry : basePackages.entrySet())
        {
            String basePackage = entry.getKey();
            boolean includeSubPackages = entry.getValue().booleanValue();

            if (!filename.startsWith(basePackage))
                continue;
            if (!includeSubPackages && (filename.lastIndexOf(".") > basePackage.length()))
                continue;

            return true;
        }
        return false;
    }


    private boolean applyIncludedAnnotationFilter(JavaClass klass)
    {
        if (includedAnnotations == null)
            return true;

        AnnotationParser ap = new AnnotationParser(klass);
        for (Annotation anno : ap.getClassVisibleAnnotations())
        {
            String annoClass = anno.getClassName();
            if (includedAnnotations.contains(annoClass))
                return true;
        }

        return false;
    }
}
