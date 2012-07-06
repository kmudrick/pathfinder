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

package com.kdgregory.pathfinder.spring;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

import com.kdgregory.pathfinder.core.PathRepo;
import com.kdgregory.pathfinder.core.PathRepo.HttpMethod;
import com.kdgregory.pathfinder.core.WarMachine;
import com.kdgregory.pathfinder.servlet.ServletInspector;
import com.kdgregory.pathfinder.spring.SpringInspector.SpringDestination;
import com.kdgregory.pathfinder.spring.test.WarNames;
import com.kdgregory.pathfinder.util.TestHelpers;


/**
 *  Tests annotation-driven configuration. There are multiple testcases, each
 *  exploring a different facet of the WAR (and reflecting the incremental
 *  implementation process).
 */
public class TestSpring3
{
    private static WarMachine machine;
    private PathRepo pathRepo;


    @BeforeClass
    public static void loadWar()
    throws Exception
    {
        machine = TestHelpers.createWarMachine(WarNames.SPRING3);
    }


    @Before
    public void setUp()
    throws Exception
    {
        // we run the inspector chain here, assert its actions in the test methods
        pathRepo = new PathRepo();
        new ServletInspector().inspect(machine, pathRepo);
        new SpringInspector().inspect(machine, pathRepo);
    }


//----------------------------------------------------------------------------
//  Testcases
//----------------------------------------------------------------------------

    @Test
    public void testBasicAnnotations() throws Exception
    {
        // the happy path

        SpringDestination dest1 = (SpringDestination)pathRepo.get("/servlet/foo.html", HttpMethod.GET);
        assertEquals("bean: foo.html", "controllerA", dest1.getBeanDefinition().getBeanName());

        SpringDestination dest2 = (SpringDestination)pathRepo.get("/servlet/bar.html", HttpMethod.GET);
        assertEquals("bean: bar.html", "controllerB", dest2.getBeanDefinition().getBeanName());

        SpringDestination dest3 = (SpringDestination)pathRepo.get("/servlet/baz.html", HttpMethod.POST);
        assertEquals("bean: baz.html", "controllerB", dest3.getBeanDefinition().getBeanName());

        // verify that we add all  variants when method isn't specified

        // and that we don't add methods that aren't specified
    }
}