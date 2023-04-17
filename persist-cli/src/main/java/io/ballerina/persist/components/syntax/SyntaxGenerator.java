package io.ballerina.persist.components.syntax;

import io.ballerina.compiler.syntax.tree.SyntaxTree;
import io.ballerina.persist.BalException;
import io.ballerina.persist.components.Client;
import io.ballerina.persist.components.ClientResource;
import io.ballerina.persist.components.Function;
import io.ballerina.persist.models.Entity;
import io.ballerina.persist.models.Module;

/**
 * This interface is used to generate the syntax tree for the client.
 *
 * @since 0.3.1
 */
public interface SyntaxGenerator {

    SyntaxTree getClientSyntax() throws BalException;

    Client generateClient(Module entityModule) throws BalException;

    Function generateInitFunction(Module entityModule);

    ClientResource generateClientResource(Entity entity);

    Function generateCloseFunction();

    Function generatePostFunction(Entity entity);

    Function generatePutFunction(Entity entity);

    Function generateDeleteFunction(Entity entity);
}
