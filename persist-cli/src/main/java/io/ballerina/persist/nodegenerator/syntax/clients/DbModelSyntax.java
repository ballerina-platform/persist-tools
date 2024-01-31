package io.ballerina.persist.nodegenerator.syntax.clients;

import io.ballerina.compiler.syntax.tree.FunctionDefinitionNode;
import io.ballerina.compiler.syntax.tree.ImportDeclarationNode;
import io.ballerina.compiler.syntax.tree.ModuleMemberDeclarationNode;
import io.ballerina.compiler.syntax.tree.NodeList;
import io.ballerina.persist.BalException;
import io.ballerina.persist.components.Client;
import io.ballerina.persist.models.Entity;
import io.ballerina.persist.models.Module;

public class DbModelSyntax implements ClientSyntax {

    public DbModelSyntax(Module entityModule) {
    }


    @Override
    public NodeList<ImportDeclarationNode> getImports() throws BalException {
        return null;
    }

    @Override
    public NodeList<ModuleMemberDeclarationNode> getConstantVariables() {
        return null;
    }

    @Override
    public Client getClientObject(Module entityModule) {
        return null;
    }

    @Override
    public FunctionDefinitionNode getInitFunction(Module entityModule) {
        return null;
    }

    @Override
    public FunctionDefinitionNode getGetFunction(Entity entity) {
        return null;
    }

    @Override
    public FunctionDefinitionNode getGetByKeyFunction(Entity entity) {
        return null;
    }

    @Override
    public FunctionDefinitionNode getCloseFunction() {
        return null;
    }

    @Override
    public FunctionDefinitionNode getPostFunction(Entity entity) {
        return null;
    }

    @Override
    public FunctionDefinitionNode getPutFunction(Entity entity) {
        return null;
    }

    @Override
    public FunctionDefinitionNode getDeleteFunction(Entity entity) {
        return null;
    }
}
