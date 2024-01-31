package io.ballerina.persist.nodegenerator.syntax.sources;

import io.ballerina.persist.BalException;
import io.ballerina.persist.models.Module;

public interface IntrospectSyntaxTree {
    io.ballerina.compiler.syntax.tree.SyntaxTree getDataModels(Module entityModule) throws BalException;
}
