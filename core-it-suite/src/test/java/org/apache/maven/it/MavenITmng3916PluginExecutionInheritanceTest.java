package org.apache.maven.it;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import org.apache.maven.it.Verifier;
import org.apache.maven.it.util.ResourceExtractor;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * This is a test set for <a href="http://jira.codehaus.org/browse/MNG-3916">MNG-3916</a>.
 * 
 * @author Benjamin Bentmann
 * @version $Id$
 */
public class MavenITmng3916PluginExecutionInheritanceTest
    extends AbstractMavenIntegrationTestCase
{

    public MavenITmng3916PluginExecutionInheritanceTest()
    {
        super( "(2.0.4,)" );
    }

    /**
     * Test that plugin executions are properly merged during inheritance, even if the plugin sections have different
     * versions.
     */
    public void testitMNG3916()
        throws Exception
    {
        File testDir = ResourceExtractor.simpleExtractResources( getClass(), "/mng-3916" );

        Verifier verifier = new Verifier( new File( testDir, "sub" ).getAbsolutePath() );
        verifier.setAutoclean( false );
        verifier.deleteDirectory( "target" );
        verifier.executeGoal( "validate" );
        verifier.verifyErrorFreeLog();
        verifier.resetStreams();

        List executions = verifier.loadLines( "target/exec.log", "UTF-8" );
        // NOTE: Ordering of executions is another issue (MNG-3887), so ignore/normalize order
        Collections.sort( executions );
        List expected = Arrays.asList( new String[] { "child-1", "child-2", "child-default", "parent-1", "parent-2" } );
        assertEquals( expected, executions );
    }

}
