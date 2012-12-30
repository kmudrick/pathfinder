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

import java.util.Collection;
import java.util.Map;

import org.apache.bcel.classfile.JavaClass;


/**
 *  This class contains the logic to scan a WAR's classpath, applying zero or
 *  more filters to the files. An unconfigured instance (one without filters)
 *  returns all files on the classpath. After configuration, instances are
 *  thread-safe.
 */
public interface ClasspathScanner
{

//----------------------------------------------------------------------------
//  Configuration Methods
//----------------------------------------------------------------------------

    /**
     *  Adds a single base package for the scan, optionally including sub-packages.
     */
    public ClasspathScanner addBasePackage(String packageName, boolean includeSubPackages);


    /**
     *  Adds a single base package to the scan, including sub-packages.
     */
    public ClasspathScanner addBasePackage(String packageName);


    /**
     *  Sets multiple base packages for the scan, optionally including sub-packages.
     */
    public ClasspathScanner addBasePackages(Collection<String> packageNames, boolean includeSubPackages);


    /**
     *  Filters selected classes by the specified class-level, runtime-visible
     *  marker annotations.
     */
    public ClasspathScanner setIncludedAnnotations(String... annotationClasses);


//----------------------------------------------------------------------------
//  Operational Methods
//----------------------------------------------------------------------------

    /**
     *  Performs the scan, returning all classes that match the filters. The
     *  map is keyed by classname.
     */
    public Map<String,JavaClass> scan(WarMachine war);
}
