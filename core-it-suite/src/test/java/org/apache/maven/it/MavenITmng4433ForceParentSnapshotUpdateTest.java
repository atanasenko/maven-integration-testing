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
import java.util.Properties;

/**
 * This is a test set for <a href="http://jira.codehaus.org/browse/MNG-4433">MNG-4433</a>.
 * 
 * @author Benjamin Bentmann
 */
public class MavenITmng4433ForceParentSnapshotUpdateTest
    extends AbstractMavenIntegrationTestCase
{

    public MavenITmng4433ForceParentSnapshotUpdateTest()
    {
        super( ALL_MAVEN_VERSIONS );
    }

    /**
     * Verify that snapshot updates of parent POMs can be forced from the command line via "-U".
     */
    public void testit()
        throws Exception
    {
        File testDir = ResourceExtractor.simpleExtractResources( getClass(), "/mng-4433" );

        Verifier verifier = new Verifier( testDir.getAbsolutePath() );
        verifier.setAutoclean( false );
        verifier.deleteArtifacts( "org.apache.maven.its.mng4433" );
        verifier.getCliOptions().add( "-s" );
        verifier.getCliOptions().add( "settings.xml" );

        Properties filterProps = verifier.newDefaultFilterProperties();

        filterProps.setProperty( "@repo@", "repo-1" );
        verifier.filterFile( "settings-template.xml", "settings.xml", "UTF-8", filterProps );
        verifier.setLogFileName( "log-force-1.txt" );
        verifier.deleteDirectory( "target" );
        verifier.executeGoal( "validate" );
        verifier.verifyErrorFreeLog();

        verifier.assertFilePresent( "target/old.txt" );
        verifier.assertFileNotPresent( "target/new.txt" );

        filterProps.setProperty( "@repo@", "repo-2" );
        verifier.filterFile( "settings-template.xml", "settings.xml", "UTF-8", filterProps );
        verifier.setLogFileName( "log-force-2.txt" );
        verifier.deleteDirectory( "target" );
        verifier.getCliOptions().add( "-U" );
        verifier.executeGoal( "validate" );
        verifier.verifyErrorFreeLog();

        verifier.resetStreams();

        verifier.assertFileNotPresent( "target/old.txt" );
        verifier.assertFilePresent( "target/new.txt" );
    }

}