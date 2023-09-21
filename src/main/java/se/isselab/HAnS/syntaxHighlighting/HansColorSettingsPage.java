/**
 Copyright 2023 Johan Martinson & Herman Jansson

 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

 http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
 **/

package se.isselab.HAnS.syntaxHighlighting;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.fileTypes.SyntaxHighlighter;
import com.intellij.openapi.options.colors.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import se.isselab.HAnS.AnnotationIcons;
import se.isselab.HAnS.syntaxHighlighting.codeAnnotations.CodeAnnotationsSyntaxHighlighter;
import se.isselab.HAnS.syntaxHighlighting.featureModel.FeatureModelSyntaxHighlighter;
import se.isselab.HAnS.syntaxHighlighting.fileAnnotations.FileAnnotationSyntaxHighlighter;

import javax.swing.*;
import java.util.HashMap;
import java.util.Map;

public class HansColorSettingsPage implements ColorSettingsPage {
    private static final AttributesDescriptor[] DESCRIPTORS = new AttributesDescriptor[]{
            new AttributesDescriptor("Feature name", FeatureModelSyntaxHighlighter.FEATURE),
            new AttributesDescriptor("Bad character", FeatureModelSyntaxHighlighter.BAD_CHARACTER),
            new AttributesDescriptor("Embedded annotation keyword", CodeAnnotationsSyntaxHighlighter.KEYWORD),
            new AttributesDescriptor("Bad value", CodeAnnotationsSyntaxHighlighter.BAD_CHARACTER),
            new AttributesDescriptor("Comment marker", CodeAnnotationsSyntaxHighlighter.COMMENTMARKER),
            new AttributesDescriptor("File name", FileAnnotationSyntaxHighlighter.FILENAME),
            new AttributesDescriptor("CS", FileAnnotationSyntaxHighlighter.FEATURE_FILE_FOLDER_CS),
            new AttributesDescriptor("Separator", FileAnnotationSyntaxHighlighter.FEATURE_FILE_SEPARATOR),

    };
    @Nullable
    @Override
    public Icon getIcon() {
        return AnnotationIcons.PluginIcon;
    }
    @NotNull
    @Override
    public SyntaxHighlighter getHighlighter() {
        return new CodeAnnotationsSyntaxHighlighter();
    }
    @NotNull
    @Override
    public String getDemoText() {
        return """
                <comment marker>//</comment marker> <EA_key>&begin</EA_key>[<feature>FeatureName</feature>]
                Codeblock
                <comment marker>//</comment marker> <EA_key>&end</EA_key>[<feature>FeatureName</feature>]
                Line of Code <comment marker>//</comment marker> <EA_key>&line</EA_key>[<feature>FeatureName</feature>::<feature>SubFeature</feature>]
                <filename>Filename.any</filename>
                <bad character>Bad-character</bad character>
                <bad value>Bad-value</bad value>
                """;
    }
    @Nullable
    @Override
    public Map<String, TextAttributesKey> getAdditionalHighlightingTagToDescriptorMap() {
        var map = new HashMap<String, TextAttributesKey>();
        map.put("feature", FeatureModelSyntaxHighlighter.FEATURE);
        map.put("filename", FileAnnotationSyntaxHighlighter.FILENAME);
        map.put("bad character", FeatureModelSyntaxHighlighter.BAD_CHARACTER);
        map.put("bad value", CodeAnnotationsSyntaxHighlighter.BAD_CHARACTER);
        map.put("comment marker", CodeAnnotationsSyntaxHighlighter.COMMENTMARKER);
        map.put("EA_key", CodeAnnotationsSyntaxHighlighter.KEYWORD);
        return map;
    }
    @Override
    public AttributesDescriptor @NotNull [] getAttributeDescriptors() {
        return DESCRIPTORS;
    }
    @Override
    public ColorDescriptor @NotNull [] getColorDescriptors() {
        return ColorDescriptor.EMPTY_ARRAY;
    }
    @NotNull
    @Override
    public String getDisplayName() {
        return "HAnS";
    }
}