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

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

import com.kdgregory.pathfinder.core.WarMachine;


/**
 *  This test looks for simple XML-based configuration, where there's a
 *  single config file attached to the servlet. All tests run using the
 *  same WAR, which is accessed via a static variable.
 */
public class TestSpring2Basic
{
    private static WarMachine machine;

    @BeforeClass
    public static void loadWar()
    throws Exception
    {
    }


//----------------------------------------------------------------------------
//  Testcases
//----------------------------------------------------------------------------

    @Test
    public void testNothing() throws Exception
    {
        // this is here to keep the skeleton build running
    }

}
