/**
 * Copyright (C) 2008 Mycila (mathieu.carbou@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.mycila.maven.plugin.license.util;

import com.mycila.maven.plugin.license.Default;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author Mathieu Carbou (mathieu.carbou@gmail.com)
 */
public final class SelectionTest {
    @Rule
    public final TemporaryFolder temp = new TemporaryFolder();

    @Test
    public void test_default_select_all() {
        Selection selection = new Selection(new File("."), new String[0], new String[0], false);
        assertEquals(selection.getExcluded().length, 0);
        assertEquals(selection.getIncluded().length, 1);
        assertTrue(selection.getSelectedFiles().length > 0);
    }

    @Test
    public void test_limit_inclusion() {
        Selection selection = new Selection(new File("."), new String[]{"toto"}, new String[]{"tata"}, false);
        assertEquals(selection.getExcluded().length, 1);
        assertEquals(selection.getIncluded().length, 1);
        assertEquals(selection.getSelectedFiles().length, 0);
    }

    @Test
    public void test_limit_inclusion_and_check_default_excludes() {
        Selection selection = new Selection(new File("."), new String[]{"toto"}, new String[0], true);
        assertEquals(selection.getExcluded().length, Default.EXCLUDES.length); // default exludes from Scanner and Selection + toto
        assertEquals(selection.getIncluded().length, 1);
        assertEquals(selection.getSelectedFiles().length, 0);
        assertTrue(Arrays.asList(selection.getExcluded()).containsAll(Arrays.asList(Default.EXCLUDES)));
    }

    @Test
    public void test_exclusions_respect_with_fastScan() throws IOException {
        createAFakeProject();

        Selection selection = new Selection(temp.getRoot(), new String[]{"**/*.txt"}, new String[] {"**/target/**"}, false);

        assertIncludedFilesInFakeProject(selection);
        assertEquals(0, selection.getFilesExcluded().length);
    }

    private void assertIncludedFilesInFakeProject(Selection selection) {
        List<String> selected = new ArrayList<String>(asList(selection.getSelectedFiles()));
        Collections.sort(selected);
        assertEquals(asList("included.txt", "module/src/main/java/not-ignored.txt", "module/sub/subsub/src/main/java/not-ignored.txt"), selected);
    }

    private void createAFakeProject() throws IOException {
        touch(temp.newFile("included.txt"));
        touch(new File(temp.newFolder("target"), "ignored.txt"));
        touch(new File(temp.getRoot(), "module/src/main/java/not-ignored.txt"));
        touch(new File(temp.getRoot(), "module/target/ignored.txt"));
        touch(new File(temp.getRoot(), "module/sub/subsub/src/main/java/not-ignored.txt"));
        touch(new File(temp.getRoot(), "module/sub/subsub/target/foo/not-ignored.txt"));
    }

    private void touch(final File newFile) throws IOException {
        newFile.getParentFile().mkdirs();
        final FileWriter w = new FileWriter(newFile);
        w.close();
    }
}
