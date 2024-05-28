/*
 * Copyright (C) 2024 DANS - Data Archiving and Networked Services (info@dans.knaw.nl)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package nl.knaw.dans.avconvert.core;

import nl.knaw.dans.avconvert.AbstractTestWithTestDir;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.util.Set;

import static ch.qos.logback.core.util.FileUtil.createMissingParentDirectories;
import static java.nio.file.Files.createDirectory;
import static java.nio.file.Files.createFile;
import static java.nio.file.Files.writeString;
import static org.assertj.core.api.Assertions.assertThat;

public class BagVersion2Test extends AbstractTestWithTestDir {

    private final Path bagDir = testDir.resolve("bag");

    @Test
    public void testRemoveNoneNone() throws Exception {
        var filesXmlPath = bagDir.resolve("metadata").resolve("files.xml");
        createMissingParentDirectories(filesXmlPath.toFile());
        createDirectory(bagDir.resolve("data"));
        createFile(bagDir.resolve("data/file1.mp4"));
        createFile(bagDir.resolve("data/file2.mp4"));
        createFile(bagDir.resolve("data/file3.mp4"));
        writeString(filesXmlPath, """
            <files
                    xsi:schemaLocation="http://easy.dans.knaw.nl/schemas/bag/metadata/files/ http://easy.dans.knaw.nl/schemas/bag/metadata/files/files.xsd"
                    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                    xmlns="http://easy.dans.knaw.nl/schemas/bag/metadata/files/"
                    xmlns:dct="http://purl.org/dc/terms/">
              <file filepath="data/file1.mp4">
                <dct:identifier>file1</dct:identifier>
                <dct:source>just has to be present</dct:source>
              </file>
              <file filepath="data/file2.mp4">
                <accessibleToRights>NONE</accessibleToRights>
                <visibleToRights>NONE</visibleToRights>
              </file>
              <file filepath="data/file3.mp4">
                <dct:identifier>file3</dct:identifier>
                <accessibleToRights>NONE</accessibleToRights>
                <visibleToRights>ANONYMOUS</visibleToRights>
              </file>
            </files>
            """
        );
        var filesXml = Converter.readFilesXml(filesXmlPath);
        assertThat(new BagVersion2(bagDir).removeNoneNone(filesXml))
            .containsExactlyInAnyOrderElementsOf(Set.of(
                Path.of("data/file1.mp4"),
                Path.of("data/file2.mp4")
            ));
    }
}
