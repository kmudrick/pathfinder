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

package com.kdgregory.pathfinder.core.impl;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import net.sf.kdgcommons.lang.StringUtil;

import com.kdgregory.bcelx.classfile.Annotation;
import com.kdgregory.bcelx.parser.AnnotationParser;
import com.kdgregory.pathfinder.core.ClasspathScanner;
import com.kdgregory.pathfinder.core.WarMachine;


/**
 *  This class contains the logic to scan a WAR's classpath, applying zero or
 *  more filters to the classes found there. An unconfigured instance (one
 *  without filters) returns all classes on the classpath.
 */
public class ClasspathScannerImpl
implements ClasspathScanner
{
    private Map<String,Boolean> basePackages;   // packageName -> recurse
    private Set<String> includedAnnotations;


//----------------------------------------------------------------------------
//  ClasspathScanner
//----------------------------------------------------------------------------

    @Override
    public ClasspathScannerImpl addBasePackage(String packageName, boolean includeSubPackages)
    {
        if (basePackages == null)
            basePackages = new HashMap<String,Boolean>();

        basePackages.put(packageName, Boolean.valueOf(includeSubPackages));
        return this;
    }


    @Override
    public ClasspathScannerImpl addBasePackage(String packageName)
    {
        return addBasePackage(packageName, true);
    }


    @Override
    public ClasspathScannerImpl addBasePackages(Collection<String> packageNames, boolean includeSubPackages)
    {
        for (String packageName : packageNames)
        {
            addBasePackage(packageName, includeSubPackages);
        }
        return this;
    }


    @Override
    public ClasspathScannerImpl setIncludedAnnotations(String... annotationClasses)
    {
        includedAnnotations = new HashSet<String>();
        includedAnnotations.addAll(Arrays.asList(annotationClasses));
        return this;
    }


    @Override
    public Set<String> scan(WarMachine war)
    {
        return scan(war, new HashMap<String,AnnotationParser>());
    }


    @Override
    public Set<String> scan(WarMachine war, Map<String,AnnotationParser> parseResults)
    {
        // returns a TreeSet to simplify debugging; it's not part of the contract
        Set<String> result = new TreeSet<String>();
        for (String fileName :  war.getFilesOnClasspath())
        {
            if (! fileName.endsWith(".class"))
                continue;

            String className = StringUtil.extractLeftOfLast(fileName, ".class").replace("/", ".");
            boolean include = applyBasePackageFilter(className)
                           && applyIncludedAnnotationFilter(war, className, parseResults);

            if (include)
                result.add(className);
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


    private boolean applyIncludedAnnotationFilter(WarMachine war, String className, Map<String,AnnotationParser> parseResults)
    {
        if (includedAnnotations == null)
            return true;

        try
        {
            AnnotationParser ap = new AnnotationParser(war.loadClass(className));
            for (Annotation anno : ap.getClassVisibleAnnotations())
            {
                if (includedAnnotations.contains(anno.getClassName()))
                {
                    parseResults.put(className, ap);
                    return true;
                }
            }
            return false;
        }
        catch (Exception ex)
        {
            throw new RuntimeException("unable to parse class file: " + className, ex);
        }
    }

//----------------------------------------------------------------------------
//  Other Intenals
//----------------------------------------------------------------------------

}
