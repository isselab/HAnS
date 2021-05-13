package se.ch.HAnS.referencing;

import com.intellij.lang.refactoring.NamesValidator;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

import java.util.regex.Pattern;

public class FeatureNameValidator implements NamesValidator {
    @Override
    public boolean isKeyword(@NotNull String name, Project project) {
        return Pattern.matches("[[A-Z]+|[a-z]+|[0-9]+|'_'+|'\''+]+", name);
    }

    @Override
    public boolean isIdentifier(@NotNull String name, Project project) {
        return Pattern.matches("[[A-Z]+|[a-z]+|[0-9]+|'_'+|'\''+]+", name);
    }
}
